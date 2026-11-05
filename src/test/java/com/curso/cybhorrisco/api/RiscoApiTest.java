package com.curso.cybhorrisco.api;

import com.curso.cybhorrisco.domain.AreaResponsavel;
import com.curso.cybhorrisco.domain.CategoriaRisco;
import com.curso.cybhorrisco.domain.Risco;
import com.curso.cybhorrisco.repository.AreaResponsavelRepository;
import com.curso.cybhorrisco.repository.CategoriaRiscoRepository;
import com.curso.cybhorrisco.repository.RiscoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class RiscoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRiscoRepository categoriaRepository;

    @Autowired
    private AreaResponsavelRepository areaRepository;

    @Autowired
    private RiscoRepository riscoRepository;

    @Test
    void deveCadastrarRiscoERetornar201() throws Exception {
        CategoriaRisco categoria = categoriaRepository.save(new CategoriaRisco("Categoria API"));
        AreaResponsavel area = areaRepository.save(new AreaResponsavel(
                "Área API",
                "API"));

        String json = """
                {
                  "codigo": "API-001",
                  "descricao": "Risco criado pela API",
                  "probabilidade": 10.000,
                  "impactoFinanceiro": 49.90,
                  "limiteTolerancia": 2.000,
                  "categoriaId": %d,
                  "areaId": %d
                }
                """.formatted(categoria.getId(), area.getId());

        mockMvc.perform(post("/api/riscos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.codigo").value("API-001"))
                .andExpect(jsonPath("$.categoriaNome").value("Categoria API"))
                .andExpect(jsonPath("$.areaNome").value("Área API"));
    }

    @Test
    void deveRetornar400ComErrosPorCampo() throws Exception {
        mockMvc.perform(post("/api/riscos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Um ou mais campos são inválidos"))
                .andExpect(jsonPath("$.fields.codigo").exists())
                .andExpect(jsonPath("$.fields.categoriaId").exists());
    }

    @Test
    void deveRetornar404ParaRiscoInexistente() throws Exception {
        mockMvc.perform(get("/api/riscos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Risco não encontrado"))
                .andExpect(jsonPath("$.path").value("/api/riscos/" + Long.MAX_VALUE));
    }

    @Test
    void deveAlterarStatusEAjustarProbabilidade() throws Exception {
        CategoriaRisco categoria = categoriaRepository.save(new CategoriaRisco("Categoria operações"));
        Risco risco = salvarRisco(categoria, "API-OPERACOES-001", "Risco original");

        String alteracao = """
                {
                  "descricao": "Risco alterado",
                  "impactoFinanceiro": 59.90,
                  "limiteTolerancia": 4.000,
                  "categoriaId": %d,
                  "areaId": null
                }
                """.formatted(categoria.getId());

        mockMvc.perform(put("/api/riscos/{id}", risco.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alteracao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Risco alterado"));

        mockMvc.perform(put("/api/riscos/{id}/status", risco.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INATIVO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));

        mockMvc.perform(post("/api/riscos/{id}/ocorrencias", risco.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":3.000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.probabilidade").value(13.0));

        mockMvc.perform(post("/api/riscos/{id}/mitigacoes", risco.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":2.000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.probabilidade").value(11.0));
    }

    @Test
    void devePesquisarRiscosComFiltrosEPaginacao() throws Exception {
        CategoriaRisco categoria = categoriaRepository.save(new CategoriaRisco("Categoria pesquisa"));
        salvarRisco(categoria, "API-PESQUISA-001", "Falha de autenticação");
        salvarRisco(categoria, "API-PESQUISA-002", "Interrupção de backup");

        mockMvc.perform(get("/api/riscos")
                        .param("descricao", "autenticação")
                        .param("categoriaId", categoria.getId().toString())
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "descricao,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo.length()").value(1))
                .andExpect(jsonPath("$.conteudo[0].descricao").value("Falha de autenticação"))
                .andExpect(jsonPath("$.totalElementos").value(1));
    }

    @Test
    void deveExcluirRisco() throws Exception {
        CategoriaRisco categoria = categoriaRepository.save(new CategoriaRisco("Categoria exclusão"));
        Risco risco = salvarRisco(categoria, "API-EXCLUSAO-001", "Risco descartável");

        mockMvc.perform(delete("/api/riscos/{id}", risco.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void naoDeveMitigarAcimaDaProbabilidade() throws Exception {
        CategoriaRisco categoria = categoriaRepository.save(new CategoriaRisco("Categoria mitigação"));
        Risco risco = salvarRisco(categoria, "API-MITIGACAO-001", "Risco limitado");

        mockMvc.perform(post("/api/riscos/{id}/mitigacoes", risco.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":10.001}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Probabilidade insuficiente para mitigação"));
    }

    private Risco salvarRisco(CategoriaRisco categoria, String codigo, String descricao) {
        Risco risco = new Risco(
                codigo,
                descricao,
                new BigDecimal("10.000"),
                new BigDecimal("49.90"),
                new BigDecimal("2.000"),
                LocalDate.of(2026, 9, 29));
        categoria.adicionarRisco(risco);
        return riscoRepository.save(risco);
    }
}
