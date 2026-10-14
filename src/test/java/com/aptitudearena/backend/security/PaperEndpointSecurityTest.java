package com.aptitudearena.backend.security;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aptitudearena.backend.controller.PreviousYearPaperController;
import com.aptitudearena.backend.service.AppUserService;
import com.aptitudearena.backend.service.PreviousYearPaperService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PreviousYearPaperController.class)
@Import(SecurityConfig.class)
class PaperEndpointSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PreviousYearPaperService paperService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserService appUserService;

    @Test
    void anonymousRequestCanReadPapersFromAllowedFrontendOrigin() throws Exception {
        when(paperService.findPapers("SSC-CGL", 2024)).thenReturn(List.of());

        mockMvc.perform(get("/api/papers")
                        .param("exam", "SSC-CGL")
                        .param("year", "2024")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void anonymousRequestToProtectedEndpointRemainsForbidden() throws Exception {
        mockMvc.perform(get("/api/attempts"))
                .andExpect(status().isForbidden());
    }
}