package com.natlex.assignment.impex;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.natlex.assignment.model.GeologicalType;
import com.natlex.assignment.model.Section;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {

	/**
	 * 
	 * @param sections List &lt;Section&gt;
	 * @param filePath String absolute or relative path of the file to be created
	 * @throws IOException
	 */
    public static void exportSections(List<Section> sections, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            createSectionsSheet(sections, workbook);      

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }

    private static void createSectionsSheet(List<Section> sections, Workbook workbook) {
        Sheet sectionsSheet = workbook.createSheet("Sections");

        Row headerRow = sectionsSheet.createRow(0);        

        int rowNum = 1;
        int maxGeoClasses=0;
        for (Section section : sections) {
            Row row = sectionsSheet.createRow(rowNum++);
            row.createCell(0).setCellValue(section.getName());
            int colNum=1;
            for(GeologicalType gt: section.getGeologicalClasses()) {
            	//null values are not checked here, assuming that's how client wants it to be            	
            	row.createCell(colNum++).setCellValue(gt.getName());
            	row.createCell(colNum++).setCellValue(gt.getCode());            	
            }
            if(maxGeoClasses<colNum) maxGeoClasses=colNum;
        }
        
        populateHeader(headerRow, maxGeoClasses/2);
        
    }
    
    private static void populateHeader(Row headerRow, int maxGeoClasses) {
    	headerRow.createCell(0).setCellValue("Section Name");
    	int nameCol=1;
    	int valCol=2;
    	for(int i=1;i<=maxGeoClasses;i++) {
    		headerRow.createCell(nameCol).setCellValue("Class "+i+" name");
    		headerRow.createCell(valCol).setCellValue("Class "+i+" value");
        	nameCol+=2;
        	valCol+=2;
    	}    	
    }
}
