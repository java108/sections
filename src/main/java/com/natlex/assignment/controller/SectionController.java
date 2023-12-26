package com.natlex.assignment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.natlex.assignment.ExportStatus;
import com.natlex.assignment.Status;
import com.natlex.assignment.model.Section;
import com.natlex.assignment.repository.SectionRepository;
import com.natlex.assignment.service.SectionService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/sections")
public class SectionController {

	@Autowired
	private SectionRepository sectionRepository;

	@Autowired
	private SectionService sectionService;

	@GetMapping
	public List<Section> getAllSections() {
		return (List<Section>) sectionRepository.findAll();

	}

	@GetMapping("/{id}")
	public Section getSectionById(@PathVariable Long id) {
		return sectionRepository.findById(id).orElse(null);
	}

	@GetMapping("/by-code")
	public List<Section> getSectionsByGeologicalClassCode(@RequestParam String code) {
		return sectionRepository.findByGeologicalClassesCode(code);
	}

	@PostMapping
	public Section createSection(@RequestBody Section section) {
		return sectionRepository.save(section);
	}

	@PutMapping("/{id}")
	public Section updateSection(@PathVariable Long id, @RequestBody Section updatedSection) {
		Section existingSection = sectionRepository.findById(id).orElse(null);
		if (existingSection != null) {
			existingSection.setName(updatedSection.getName());
			existingSection.setGeologicalClasses(updatedSection.getGeologicalClasses());
			return sectionRepository.save(existingSection);
		}
		return null;
	}

	@DeleteMapping("/{id}")
	public void deleteSection(@PathVariable Long id) {
		sectionRepository.deleteById(id);
	}

	@PostMapping("/import")
	public ResponseEntity<String> importSectionsAsync(@RequestParam("file") MultipartFile file) {
		String jobId = sectionService.importSectionsAsync(file);
		return ResponseEntity.ok(jobId);
	}

	@GetMapping("/import/{id}")
	public ResponseEntity<String> getImportStatus(@PathVariable String id) {
		Status status = sectionService.getImportStatus(id);

		if (status == Status.DONE || status == Status.IN_PROGRESS) {
			return ResponseEntity.ok(status.toString());
		} else if (status == Status.ERROR) {
			return ResponseEntity.status(500).body(status.toString());
		} else {
			return ResponseEntity.status(404).body("No job found with given ID");
		}
	}

	@GetMapping("/export")
	public ResponseEntity<String> exportSectionsAsync() {
		String jobId = sectionService.exportSectionsAsync();
		return ResponseEntity.ok(jobId);
	}

	@GetMapping("/export/{id}")
	public ResponseEntity<String> getExportStatus(@PathVariable String id) {
		ExportStatus status = sectionService.getExportStatus(id);

		if (status.getStatus() == Status.DONE || status.getStatus() == Status.IN_PROGRESS) {
			return ResponseEntity.ok(status.getStatus().toString());
		} else if (status.getStatus() == Status.ERROR) {
			return ResponseEntity.status(500).body(status.toString());
		} else {
			return ResponseEntity.status(404).body("No job found with given ID");
		}
	}

	@GetMapping("/export/{id}/file")
	public ResponseEntity<Resource> downloadFile(@PathVariable String id) {
		HttpHeaders headers = new HttpHeaders();
		Resource resource = null;
		ExportStatus status = sectionService.getExportStatus(id);

		if (status.getStatus() != Status.DONE) {
			headers.add(HttpHeaders.CONTENT_TYPE, "text/html");
			return ResponseEntity.notFound().headers(headers).build();
		}
		// Build the file path
		String filename = status.getPath();
		Path filePath = Paths.get(filename).normalize();

		try {
			// Load file as a resource
			resource = new UrlResource(filePath.toUri());
		} catch (MalformedURLException e) {
			return ResponseEntity.notFound().build();
		}

		// Try to determine file's content type
		String contentType;
		try {
			contentType = Files.probeContentType(filePath);
		} catch (IOException e) {
			contentType = "application/octet-stream";
		}

		// Set content disposition header to enable browser download

		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		headers.add(HttpHeaders.CONTENT_TYPE, contentType);

		return ResponseEntity.ok().headers(headers).body(resource);
	}
}
