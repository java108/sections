package com.natlex.assignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.natlex.assignment.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
	List<Section> findByGeologicalClassesCode(String code);
}
