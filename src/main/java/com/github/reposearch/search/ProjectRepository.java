package com.github.reposearch.search;

import com.github.reposearch.search.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}