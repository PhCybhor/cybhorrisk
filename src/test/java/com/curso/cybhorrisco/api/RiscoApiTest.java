package com.curso.cybhorrisco.api;

import com.curso.cybhorrisco.domain.AreaResponsavel;
import com.curso.cybhorrisco.domain.CategoriaRisco;
import com.curso.cybhorrisco.repository.AreaResponsavelRepository;
import com.curso.cybhorrisco.repository.CategoriaRiscoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RiscoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRiscoRepository categoriaRepository;

    @Autowired
    private AreaResponsavelRepository areaRepository;

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
}
