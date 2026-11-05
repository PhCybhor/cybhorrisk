package com.curso.cybhorrisco.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void devePublicarContratoOpenApiComRotasEssenciais() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi", startsWith("3.")))
                .andExpect(jsonPath("$.info.title").value("Cybhor Risk API"))
                .andExpect(jsonPath("$.info.version").value("1.0.0"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.bearerFormat").value("JWT"))
                .andExpect(jsonPath("$.paths['/api/categorias-risco/{id}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/categorias-risco/{id}'].put").exists())
                .andExpect(jsonPath("$.paths['/api/riscos/{id}'].get.summary")
                        .value("Consultar risco por ID"))
                .andExpect(jsonPath("$.paths['/api/riscos/{id}'].get.responses['200'].content")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/riscos/{id}'].get.responses['404'].content")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/riscos/{id}'].delete").exists())
                .andExpect(jsonPath("$.paths['/api/riscos/{id}/ocorrencias'].post").exists())
                .andExpect(jsonPath("$.paths['/api/riscos/{id}/mitigacoes'].post").exists())
                .andExpect(jsonPath("$.paths['/api/pessoas'].post").exists())
                .andExpect(jsonPath("$.paths['/api/interessados/{id}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/analistas'].post").exists())
                .andExpect(jsonPath("$.paths['/api/contas-acesso'].post").exists())
                .andExpect(jsonPath("$.paths['/api/contas-acesso/me'].get").exists())
                .andExpect(jsonPath("$.paths['/api/contas-acesso/me/senha'].put").exists())
                .andExpect(jsonPath("$.paths['/api/contas-acesso/{id}/perfis'].put").exists())
                .andExpect(jsonPath("$.paths['/api/contas-acesso/{id}/status'].put").exists())
                .andExpect(jsonPath("$.paths['/api/autenticacao/login'].post").exists())
                .andExpect(jsonPath("$.paths['/api/autenticacao/refresh'].post").exists())
                .andExpect(jsonPath("$.paths['/api/autenticacao/logout'].post").exists())
                .andExpect(jsonPath("$.paths['/api/autenticacao/logout-todos'].post").exists())
                .andExpect(jsonPath("$.paths['/api/auditoria-seguranca'].get").exists())
                .andExpect(jsonPath("$.paths['/api/planos-mitigacao/{id}/itens'].post").exists())
                .andExpect(jsonPath("$.paths['/api/planos-mitigacao/{id}/finalizacao'].post").exists())
                .andExpect(jsonPath("$.components.schemas.RiscoResponse").exists())
                .andExpect(jsonPath("$.components.schemas.PlanoMitigacaoResponse").exists())
                .andExpect(jsonPath("$.components.schemas.ApiError").exists());

        mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isOk());
    }
}
