package com.natlex.assignment.impex;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.natlex.assignment.model.GeologicalType;
import com.natlex.assignment.model.Section;

class ExcelExporterTest {

    @Test
    void testExportSection() throws IOException {
        // Prepare test data
        
    	Section section = new Section();
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(section);
        
        section.setName("Test Section");
        GeologicalType geoClass1 = new GeologicalType();
        geoClass1.setName("Geo Class 1");
        geoClass1.setCode("GC1");
        GeologicalType geoClass2 = new GeologicalType();
        geoClass2.setName("Geo Class 2");
        geoClass2.setCode("GC2");
        section.setGeologicalClasses(List.of(geoClass1, geoClass2));

        // Test the export functionality
        String filename = "test.xlsx";
        ExcelExporter.exportSections(sectionList,filename);
        
        List<Section> opList = ExcelImporter.importSectionsFromExcel(Files.newInputStream(Paths.get(filename)));
        
        assertThat(opList).isNotNull();
        assertThat(opList).size().isEqualTo(1);
        assertEquals(opList.get(0).getName(), section.getName());
        assertEquals(opList.get(0).getGeologicalClasses().get(0).getCode(), geoClass1.getCode());        
    }
    
    
}
