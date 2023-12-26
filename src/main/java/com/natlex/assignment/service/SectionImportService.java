package com.natlex.assignment.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.natlex.assignment.Status;
import com.natlex.assignment.impex.ExcelImporter;
import com.natlex.assignment.model.Section;
import com.natlex.assignment.repository.SectionRepository;

@Service
public class SectionImportService {
	@Autowired
	private SectionRepository sectionRepo;
	
	private Logger LOGGER=LoggerFactory.getLogger(getClass());
	
    @Async
    public void processSectionsAsync(InputStream inpStream, String jobId, Map<String, Status> importJobs) {
        try {
            // Parse the Excel file and save results to the database asynchronously
        	LOGGER.info("Started importing {}", jobId);
        	//Thread.sleep(10000);
        	importJobs.put(jobId, Status.IN_PROGRESS);
            List<Section> sections = ExcelImporter.importSectionsFromExcel(inpStream);
            // Save sections to the database (use SectionRepository)
            sectionRepo.saveAll(sections);

            // Update job status to "DONE"
            importJobs.put(jobId, Status.DONE);
            LOGGER.info("Done importing {}", jobId);
        } catch (Exception e) {
            // Handle exceptions and update job status to "ERROR"
        	LOGGER.error(e.getMessage());        	
        	importJobs.put(jobId, Status.ERROR);
        }
    }    
}

