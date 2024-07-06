package com.github.reposearch.search;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Transactional(readOnly = true)
    public Project getProjectByName(String name) {
        Project project = projectRepository.findByName(name);
        if (project != null) {
            project.getCommits().size();
        }
        return project;
    }

    @Transactional
    public String deleteProjectByName(String name) {
        Project project = projectRepository.findByName(name);
        if (project == null) {
            return "Error: Project with name '" + name + "' not found.";
        }
        try {
            commitRepository.deleteByProject(project);
            authorRepository.deleteByCommitsProject(project);
            projectRepository.delete(project);
            return "Success: Project '" + name + "' deleted successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Failed to delete project '" + name + "'. Please try again later.";
        }
    }
    
    public List<AuthorInfo> getAuthorsByProjectName(String projectName, Boolean platformEng) {
        Project project = projectRepository.findByName(projectName);
        if (project == null) {
            return null;
        }
        List<Commit> commits = project.getCommits();

        Map<Long, Long> authorCommitCounts = commits.stream()
                .collect(Collectors.groupingBy(commit -> commit.getAuthor().getId(), Collectors.counting()));

        List<AuthorInfo> authorInfos = commits.stream()
                .map(Commit::getAuthor)
                .distinct()
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