package com.github.reposearch.search;

import com.github.reposearch.search.Author;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
	void deleteByCommitsProject(Project project);
	Author findByName(String name);
	Author findByNameAndCommitsProject(String name, Project project);
	List<Author> findByCommitsProjectName(String projectName);
}
