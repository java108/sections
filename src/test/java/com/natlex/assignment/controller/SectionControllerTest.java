package com.natlex.assignment.controller;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natlex.assignment.Status;
import com.natlex.assignment.impex.ExcelImporter;
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
		// when(sectionRepository.save(any(Section.class))).thenReturn(section);

		mockMvc.perform(post("/sections").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(section))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Section 1"))
				.andExpect(jsonPath("$.geologicalClasses[0].name").value("Geo Class 11"))
				.andExpect(jsonPath("$.geologicalClasses[0].code").value("GC11"));

	}

	@Test
	@Order(20)
	void getAllSections() throws Exception {

		mockMvc.perform(get("/sections")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Section 1"))
				.andExpect(jsonPath("$[0].geologicalClasses[0].name").value("Geo Class 11"))
				.andExpect(jsonPath("$[0].geologicalClasses[0].code").value("GC11"));
	}

	@Test
	@Order(30)
	void getSectionById() throws Exception {
		mockMvc.perform(get("/sections/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Section 1"))
				.andExpect(jsonPath("$.geologicalClasses[0].name").value("Geo Class 11"))
				.andExpect(jsonPath("$.geologicalClasses[0].code").value("GC11"));
	}

	@Test
	@Order(40)
	void updateSection() throws Exception {
		Section updatedSection = createTestSection();
		updatedSection.setName("Updated Section");

		mockMvc.perform(put("/sections/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedSection))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated Section"))
				.andExpect(jsonPath("$.geologicalClasses[0].name").value("Geo Class 11"))
				.andExpect(jsonPath("$.geologicalClasses[0].code").value("GC11"));
	}

	@Test
	@Order(50)
	void deleteSection() throws Exception {
		mockMvc.perform(delete("/sections/1")).andExpect(status().isOk());

		mockMvc.perform(get("/sections")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
	}

	@Test
	@Order(60)

	void testImportSections() throws Exception {
		// Prepare a test Excel file as an input stream
		InputStream inputStream = getClass().getResourceAsStream("/test_input_sections.xlsx");
		MockMultipartFile file = new MockMultipartFile("file", "test_input_sections.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", inputStream);

		// Perform the import request
		MvcResult mvcResult = mockMvc.perform(multipart("/sections/import").file(file))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/plain;charset=UTF-8")).andExpect(jsonPath("$").isString())
				.andReturn();

		String jobid = mvcResult.getResponse().getContentAsString();
		List<String> status = Arrays.asList(Status.DONE.toString(), Status.IN_PROGRESS.toString());

		// Check if api working fine
		mvcResult = mockMvc.perform(get("/sections/import/" + jobid)).andExpect(status().isOk()).andReturn();

		// check if the status of job DONE or IN Progress
		assertThat(status.contains(mvcResult.getResponse().getContentAsString()));

		// wait for 5 seconds to complete the import
		Thread.sleep(5000);

		mvcResult = mockMvc.perform(get("/sections/import/" + jobid)).andExpect(status().isOk()).andReturn();

		// check if the status is DONE 
		assertThat(Status.DONE.toString().equals(mvcResult.getResponse().getContentAsString()));

		
		// Test for export
		
		mvcResult = mockMvc.perform(get("/sections/export" )).andExpect(status().isOk()).andReturn();

		// check if the status of job DONE or IN Progress
		//assertThat(status.contains(mvcResult.getResponse().getContentAsString()));
		jobid=mvcResult.getResponse().getContentAsString();
		
		// wait for 5 seconds to complete the export
		Thread.sleep(5000);
		
		mvcResult = mockMvc.perform(get("/sections/export/"+jobid+"/file"))
         .andExpect(status().isOk())
         .andExpect(header().string("Content-Disposition", "attachment; filename="+jobid+".xlsx"))
         .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).andReturn();
		
		//read the file again as earlier stream was closed
		inputStream = getClass().getResourceAsStream("/test_input_sections.xlsx");
		
		List<Section> exportedSections = ExcelImporter.importSectionsFromExcel(new ByteArrayInputStream(mvcResult.getResponse().getContentAsByteArray()));
		List<Section> origSections = ExcelImporter.importSectionsFromExcel(inputStream);
		
		assertEquals(exportedSections.size(), origSections.size());
		assertEquals(exportedSections.get(0).getName(), origSections.get(0).getName());
		assertEquals(exportedSections.get(0).getGeologicalClasses().size(), origSections.get(0).getGeologicalClasses().size());
		
		//More tests can be added here		
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
