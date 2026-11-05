package com.curso.cybhorrisco.application;

import com.curso.cybhorrisco.domain.Analista;
import com.curso.cybhorrisco.domain.CategoriaRisco;
import com.curso.cybhorrisco.domain.FuncaoAnalista;
import com.curso.cybhorrisco.domain.Interessado;
import com.curso.cybhorrisco.domain.Pessoa;
import com.curso.cybhorrisco.domain.PlanoMitigacao;
import com.curso.cybhorrisco.domain.Risco;
import com.curso.cybhorrisco.domain.StatusPlanoMitigacao;
import com.curso.cybhorrisco.repository.AnalistaRepository;
import com.curso.cybhorrisco.repository.CategoriaRiscoRepository;
import com.curso.cybhorrisco.repository.InteressadoRepository;
import com.curso.cybhorrisco.repository.PessoaRepository;
import com.curso.cybhorrisco.repository.RiscoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class PlanoMitigacaoServiceTest {

    @Autowired
    PlanoMitigacaoService planoMitigacaoService;
    @Autowired
    PessoaRepository pessoaRepository;
    @Autowired
    InteressadoRepository interessadoRepository;
    @Autowired
    AnalistaRepository analistaRepository;
    @Autowired
    CategoriaRiscoRepository categoriaRepository;
    @Autowired
    RiscoRepository riscoRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @AfterEach
    void limparDadosDoCenario() {
        jdbcTemplate.update("DELETE FROM item_mitigacao");
        jdbcTemplate.update("DELETE FROM plano_mitigacao");
        jdbcTemplate.update("DELETE FROM interessado");
        jdbcTemplate.update("DELETE FROM analista");
        jdbcTemplate.update("DELETE FROM pessoa");
        jdbcTemplate.update("DELETE FROM risco WHERE codigo LIKE 'PLANO-%'");
        jdbcTemplate.update("DELETE FROM categoria_risco WHERE nome LIKE 'Categoria PLANO-%'");
    }

    @Test
    void deveFinalizarPlanoEAplicarMitigacoes() {
        Participantes participantes = criarParticipantes();
        Risco risco = criarRisco("PLANO-OK-" + System.nanoTime(), "5.000");
        PlanoMitigacao plano = planoMitigacaoService.cadastrar(
                participantes.interessado().getId(),
                participantes.analista().getId());
        planoMitigacaoService.adicionarItem(plano.getId(), risco.getId(), new BigDecimal("2.000"));

        PlanoMitigacao finalizado = planoMitigacaoService.finalizar(plano.getId());

        assertEquals(StatusPlanoMitigacao.FINALIZADA, finalizado.getStatus());
        assertEquals(
                new BigDecimal("3.000"),
                riscoRepository.findById(risco.getId()).orElseThrow().getProbabilidade());
    }

    @Test
    void deveDesfazerTodasAsMitigacoesQuandoUmItemNaoTemProbabilidade() {
        Participantes participantes = criarParticipantes();
        Risco primeiro = criarRisco("PLANO-ROLLBACK-A-" + System.nanoTime(), "5.000");
        Risco segundo = criarRisco("PLANO-ROLLBACK-B-" + System.nanoTime(), "1.000");
        PlanoMitigacao plano = planoMitigacaoService.cadastrar(
                participantes.interessado().getId(),
                participantes.analista().getId());
        planoMitigacaoService.adicionarItem(plano.getId(), primeiro.getId(), new BigDecimal("2.000"));
        planoMitigacaoService.adicionarItem(plano.getId(), segundo.getId(), new BigDecimal("2.000"));

        assertThrows(IllegalArgumentException.class, () -> planoMitigacaoService.finalizar(plano.getId()));

        assertEquals(
                new BigDecimal("5.000"),
                riscoRepository.findById(primeiro.getId()).orElseThrow().getProbabilidade());
        assertEquals(
                StatusPlanoMitigacao.ABERTA,
                planoMitigacaoService.buscarPorId(plano.getId()).getStatus());
    }

    @Test
    void deveImpedirPlanoAcimaDaProbabilidadeEmFinalizacoesConcorrentes() throws Exception {
        Participantes participantes = criarParticipantes();
        Risco risco = criarRisco("PLANO-CONCORRENTE-" + System.nanoTime(), "5.000");
        PlanoMitigacao primeira = criarPlanoComItem(participantes, risco, "4.000");
        PlanoMitigacao segunda = criarPlanoComItem(participantes, risco, "4.000");

        CountDownLatch inicioSimultaneo = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<ResultadoFinalizacao> primeiroResultado = executor.submit(
                    () -> finalizarAposSinal(primeira.getId(), inicioSimultaneo));
            Future<ResultadoFinalizacao> segundoResultado = executor.submit(
                    () -> finalizarAposSinal(segunda.getId(), inicioSimultaneo));

            inicioSimultaneo.countDown();

            List<ResultadoFinalizacao> resultados = List.of(
                    primeiroResultado.get(10, TimeUnit.SECONDS),
                    segundoResultado.get(10, TimeUnit.SECONDS));

            assertEquals(1, resultados.stream().filter(ResultadoFinalizacao::sucesso).count());
            assertEquals(1, resultados.stream().filter(resultado -> !resultado.sucesso()).count());
            assertTrue(resultados.stream()
                    .filter(resultado -> !resultado.sucesso())
                    .allMatch(resultado ->
                            "Probabilidade insuficiente para mitigação".equals(resultado.mensagem())));
        }

        assertEquals(
                new BigDecimal("1.000"),
                riscoRepository.findById(risco.getId()).orElseThrow().getProbabilidade());

        List<StatusPlanoMitigacao> estados = List.of(
                planoMitigacaoService.buscarPorId(primeira.getId()).getStatus(),
                planoMitigacaoService.buscarPorId(segunda.getId()).getStatus());
        assertEquals(1, estados.stream().filter(StatusPlanoMitigacao.FINALIZADA::equals).count());
        assertEquals(1, estados.stream().filter(StatusPlanoMitigacao.ABERTA::equals).count());
    }

    private PlanoMitigacao criarPlanoComItem(
            Participantes participantes,
            Risco risco,
            String reducao) {
        PlanoMitigacao plano = planoMitigacaoService.cadastrar(
                participantes.interessado().getId(),
                participantes.analista().getId());
        planoMitigacaoService.adicionarItem(
                plano.getId(),
                risco.getId(),
                new BigDecimal(reducao));
        return plano;
    }

    private ResultadoFinalizacao finalizarAposSinal(
            Long planoId,
            CountDownLatch inicioSimultaneo) throws InterruptedException {
        inicioSimultaneo.await();
        try {
            planoMitigacaoService.finalizar(planoId);
            return new ResultadoFinalizacao(true, null);
        } catch (IllegalArgumentException exception) {
            return new ResultadoFinalizacao(false, exception.getMessage());
        }
    }

    private Participantes criarParticipantes() {
        long semente = System.nanoTime();
        String sufixo = String.valueOf(semente);
        String cpfInteressado = gerarCpf(semente);
        String cpfAnalista = gerarCpf(semente + 7919);
        Pessoa pi = pessoaRepository.save(new Pessoa(
                "Interessado",
                "interessado" + sufixo + "@example.com",
                cpfInteressado,
                LocalDate.now()));
        Pessoa pa = pessoaRepository.save(new Pessoa(
                "Analista",
                "analista" + sufixo + "@example.com",
                cpfAnalista,
                LocalDate.now()));
        Interessado interessado = interessadoRepository.save(new Interessado(pi, null));
        Analista analista = analistaRepository.save(new Analista(
                pa,
                "M-" + sufixo,
                FuncaoAnalista.ANALISTA,
                LocalDate.now()));
        return new Participantes(interessado, analista);
    }

    private String gerarCpf(long semente) {
        String base = "%09d".formatted(Math.floorMod(semente, 1_000_000_000L));
        if (digitosRepetidos(base)) {
            base = "123456789";
        }
        int primeiro = digitoCpf(base, 10);
        return base + primeiro + digitoCpf(base + primeiro, 11);
    }

    private int digitoCpf(String base, int peso) {
        int soma = 0;
        for (char c : base.toCharArray()) {
            soma += Character.getNumericValue(c) * peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private boolean digitosRepetidos(String valor) {
        for (int i = 1; i < valor.length(); i++) {
            if (valor.charAt(i) != valor.charAt(0)) {
                return false;
            }
        }
        return true;
    }

    private Risco criarRisco(String codigo, String probabilidade) {
        CategoriaRisco categoria = categoriaRepository.save(new CategoriaRisco("Categoria " + codigo));
        Risco risco = new Risco(
                codigo,
                "Risco de teste",
                new BigDecimal(probabilidade),
                new BigDecimal("10.00"),
                LocalDate.now());
        categoria.adicionarRisco(risco);
        return riscoRepository.save(risco);
    }

    private record Participantes(Interessado interessado, Analista analista) {
    }

    private record ResultadoFinalizacao(boolean sucesso, String mensagem) {
    }
}
