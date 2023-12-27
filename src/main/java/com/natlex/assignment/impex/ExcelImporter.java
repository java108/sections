package com.natlex.assignment.impex;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.natlex.assignment.model.GeologicalType;
import com.natlex.assignment.model.Section;

public class ExcelImporter {
	private static Logger LOGGER = LoggerFactory.getLogger(ExcelImporter.class);
	
	/**
	 * Takes InputStream of Sections, converts them in Section objects are return
	 * @param fis InputStream
	 * @return List of Sections
	 * @throws IOException
	 */

	//Read the input stream, skip 1st row assuming it is header, read each row and create Section object out of it
	//Also it is assumed that the header row will have max number of Geological classes
	// in case null GeologicalClass name and code is found then do not add it to section
	
	public static List<Section> importSectionsFromExcel(InputStream fis) throws IOException {
		List<Section> sections = new ArrayList<>();
		LOGGER.info("started");
		try (Workbook workbook = new XSSFWorkbook(fis)) {
			// Assuming Sections will be in first sheet always.
			Sheet sectionsSheet = workbook.getSheetAt(0);

			// Import Sections
			Iterator<Row> sectionIterator = sectionsSheet.iterator();

			// Skip the headers and get total number of columns for GeologicalClasses
			Row row = sectionIterator.next();
			int geoClassesLength = getGeoLogicalClassLength(row);

			while (sectionIterator.hasNext()) {
				row = sectionIterator.next();
				if (row.getCell(0) == null)
					break;

				LOGGER.info("Reading section {}", row.getCell(0).getStringCellValue());
				Section section = new Section();
				section.setName(row.getCell(0).getStringCellValue());

				// Find geological classes for the section
				List<GeologicalType> geologicalClasses = getGeologicalClasses(row, geoClassesLength);
				section.setGeologicalClasses(geologicalClasses);

				sections.add(section);
			}
		}

		return sections;
	}

	private static List<GeologicalType> getGeologicalClasses(Row row, int length) {
		List<GeologicalType> list = new ArrayList<>();
		LOGGER.info("started getGeologicalClasses {}", length);

		for (int i = 1; i < length;) {
			String name = row.getCell(i) != null ? row.getCell(i).getStringCellValue() : null;
			i++;
			String code = row.getCell(i) != null ? row.getCell(i).getStringCellValue() : null;
			i++;

			// Additional validations can be added such that only name or code is provided
			// then throw an exception
			if (name != null && code != null) {
				GeologicalType geologicalClass = new GeologicalType();
				geologicalClass.setName(name);
				geologicalClass.setCode(code);
				list.add(geologicalClass);
			}
		}
		return list;
	}

	private static int getGeoLogicalClassLength(Row row) {
		int length = 1;

		int i = 1; // start of GeologicalClass details columns
		while (row.getCell(i++) != null) {
			length++;
		}
		return length;
	}
}
