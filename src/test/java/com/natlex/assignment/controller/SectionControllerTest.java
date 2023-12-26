package com.natlex.assignment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer.OrderAnnotation;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natlex.assignment.model.GeologicalType;
import com.natlex.assignment.model.Section;
import com.natlex.assignment.repository.SectionRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SectionRepository sectionRepository;

    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @Order(10)
    void createSection() throws Exception {
        Section section = createTestSection();
        //when(sectionRepository.save(any(Section.class))).thenReturn(section);

         mockMvc.perform(post("/sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(section)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Section 1"))
                .andExpect(jsonPath("$.geologicalClasses[0].name").value("Geo Class 11"))
                .andExpect(jsonPath("$.geologicalClasses[0].code").value("GC11"));
        
        
    }

    @Test
    @Order(20)
    void getAllSections() throws Exception {
        
        mockMvc.perform(get("/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Section 1"))
                .andExpect(jsonPath("$[0].geologicalClasses[0].name").value("Geo Class 11"))
                .andExpect(jsonPath("$[0].geologicalClasses[0].code").value("GC11"));
    }

    @Test
    @Order(30)
    void getSectionById() throws Exception {
        mockMvc.perform(get("/sections/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Section 1"))
                .andExpect(jsonPath("$.geologicalClasses[0].name").value("Geo Class 11"))
                .andExpect(jsonPath("$.geologicalClasses[0].code").value("GC11"));
    }   

    @Test
    @Order(40)
    void updateSection() throws Exception {
        Section updatedSection = createTestSection();
        updatedSection.setName("Updated Section");

        mockMvc.perform(put("/sections/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSection)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Section"))
                .andExpect(jsonPath("$.geologicalClasses[0].name").value("Geo Class 11"))
                .andExpect(jsonPath("$.geologicalClasses[0].code").value("GC11"));
    }

    @Test
    void deleteSection() throws Exception {
        mockMvc.perform(delete("/sections/1"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/sections"))
        .andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());               
    }

    private Section createTestSection() {
        Section section = new Section();
        section.setId(1L);
        section.setName("Section 1");

        GeologicalType geologicalType = new GeologicalType();
        geologicalType.setId(1L);
        geologicalType.setName("Geo Class 11");
        geologicalType.setCode("GC11");

        section.setGeologicalClasses(Collections.singletonList(geologicalType));

        return section;
    }
}

