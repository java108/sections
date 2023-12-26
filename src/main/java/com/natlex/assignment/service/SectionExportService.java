package com.natlex.assignment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.natlex.assignment.ExportStatus;
import com.natlex.assignment.Status;
import com.natlex.assignment.impex.ExcelExporter;
import com.natlex.assignment.model.Section;
import com.natlex.assignment.repository.SectionRepository;

import jakarta.transaction.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SectionExportService {
   
	@Autowired
	private SectionRepository sectionRepo;
	private Logger LOGGER=LoggerFactory.getLogger(getClass());
	private String folderPath=""; //"D:\\milind\\assignment\\natlex\\tmp\\export\\";

    @Async
    @Transactional
    public void exportSectionsAsync(String jobId, Map<String, ExportStatus> exportStatus) {
    	LOGGER.info("Started exporting {}", jobId);   	
    	ExportStatus  expStatus = new ExportStatus(Status.IN_PROGRESS,null);
    	exportStatus.put(jobId,expStatus);
    	String filePath=folderPath+jobId+".xlsx";        
        try {
			ExcelExporter.exportSections(sectionRepo.findAll(), filePath);
			expStatus.setStatus(Status.DONE);
			expStatus.setPath(filePath);	
			LOGGER.info("Done exporting {}", jobId);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			expStatus.setStatus(Status.ERROR);			
		}
    }
    
    /**
     * This method can be used to remove files older than 4 hours
     */
    //@Scheduled
    private void removeOldFiles() {
    	//TODO Logic to remove files older than 24 hours from folderPath
    }
}
