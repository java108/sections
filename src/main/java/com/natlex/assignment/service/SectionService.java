package com.natlex.assignment.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.natlex.assignment.ExportStatus;
import com.natlex.assignment.Status;

@Service
public class SectionService {

	@Autowired
	private SectionImportService sectionImportService;

	@Autowired
	private SectionExportService sectionExportService;

	private Map<String, Status> importJobs = new HashMap<>();

	private Map<String, ExportStatus> exportJobs = new HashMap<>();

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	public String importSectionsAsync(MultipartFile file) {
		String jobId = UUID.randomUUID().toString();
		try {
			sectionImportService.processSectionsAsync(file.getInputStream(), jobId, importJobs);
		} catch (IOException e) {
			LOGGER.error("Exception occurred {}", e.getMessage());
			jobId = Status.ERROR.toString();
		}
		return jobId;
	}

	public Status getImportStatus(String jobId) {
		return importJobs.get(jobId);
	}

	public String exportSectionsAsync() {
		String jobId = UUID.randomUUID().toString();
		sectionExportService.exportSectionsAsync(jobId, exportJobs);
		return jobId;
	}

	public ExportStatus getExportStatus(String id) {
		return exportJobs.get(id);
	}
}
