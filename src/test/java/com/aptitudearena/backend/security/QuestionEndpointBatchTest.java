package com.aptitudearena.backend.security;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aptitudearena.backend.controller.QuestionController;
import com.aptitudearena.backend.repository.QuestionOptionRepository;
import com.aptitudearena.backend.service.AppUserService;
import com.aptitudearena.backend.service.QuestionService;
import com.aptitudearena.backend.service.SubjectService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(QuestionController.class)
@Import(SecurityConfig.class)
class QuestionEndpointBatchTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private SubjectService subjectService;

    @MockitoBean
    private QuestionOptionRepository questionOptionRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserService appUserService;

    @Test
    void anonymousRequestCanLoadAllQuestionsForOneExam() throws Exception {
        UUID examId = UUID.randomUUID();
        when(questionService.findByExam(examId)).thenReturn(List.of());

        mockMvc.perform(get("/api/questions/exam/{examId}", examId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(questionService).findByExam(examId);
    }
}