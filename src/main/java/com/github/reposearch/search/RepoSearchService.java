package com.github.reposearch.search;

import java.util.*;
import java.util.stream.Collectors;

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
    
    public List<AuthorInfo> getAuthorsByProjectName(String projectName, Boolean platformEng) {
        Project project = projectRepository.findByName(projectName);
        List<Commit> commits = commitRepository.findByProject(project);

        List<Author> authors = authorRepository.findAll();
        
        Map<Long, Long> authorCommitCounts = commits.stream()
                .collect(Collectors.groupingBy(commit -> commit.getAuthor().getId(), Collectors.counting()));

        List<AuthorInfo> authorInfos = authors.stream()
                .filter(author -> platformEng == null || author.isPlatformEngineer() == platformEng)
                .map(author -> {
                    long commitCount = authorCommitCounts.getOrDefault(author.getId(), 0L);
                    return new AuthorInfo(author.getName(), author.isPlatformEngineer(), commitCount);
                })
                .collect(Collectors.toList());

        if (authorInfos.isEmpty()) {
            return List.of(new AuthorInfo("No authors found", false, 0));
        }

        return authorInfos;
    }
}