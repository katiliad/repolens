package com.github.reposearch.search;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

@Service
public class RepoSearchService {
	
	@Autowired
    private ProjectRepository projectRepository;
	
	@Autowired
    private AuthorRepository authorRepository;
	
	 @Autowired
	private CommitRepository commitRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }
    
    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }
    
    public Commit saveCommit(Commit commit) {
        return commitRepository.save(commit);
    }
    
    public Project getProjectByName(String name) {
        return projectRepository.findByName(name);
    }

    public void deleteProjectByName(String name) {
        Project project = projectRepository.findByName(name);
        if (project != null) {
            projectRepository.delete(project);
        }
    }

}