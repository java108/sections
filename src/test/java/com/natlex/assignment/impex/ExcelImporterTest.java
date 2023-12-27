package com.natlex.assignment.impex;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.natlex.assignment.model.Section;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ExcelImporterTest {

    @Test
    void testImportSectionsFromExcel() throws IOException {
        // Prepare a test Excel file as an input stream
        InputStream inputStream = getClass().getResourceAsStream("/test_input_sections.xlsx");
        MockMultipartFile file = new MockMultipartFile("file", "test_input_sections.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", inputStream);

        // Test the import functionality
        List<Section> sections = ExcelImporter.importSectionsFromExcel(file.getInputStream());
        
        // Assert that the sections are imported correctly
        assertThat(sections).hasSize(4);
        Section section = sections.get(0);
        assertThat(section.getName()).isEqualTo("Section 1");
        assertThat(section.getGeologicalClasses()).hasSize(3);
        assertThat(section.getGeologicalClasses().get(0).getName()).isEqualTo("Geo Class 11");
        assertThat(section.getGeologicalClasses().get(0).getCode()).isEqualTo("GC 11");
        assertThat(section.getGeologicalClasses().get(1).getName()).isEqualTo("Geo Class 12");
        assertThat(section.getGeologicalClasses().get(1).getCode()).isEqualTo("GC 12");
        assertThat(section.getGeologicalClasses().get(2).getName()).isEqualTo("Geo Class 1M");
        assertThat(section.getGeologicalClasses().get(2).getCode()).isEqualTo("GC 1M");
    }
}



