package com.github.reposearch.search;
import com.github.reposearch.search.Project;
import com.github.reposearch.search.ProjectRepository;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.eclipse.jgit.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

@Service
public class RepoSearchService {
	
	public String getAllCommitsForMyRepo(){
		Git repo = cloneRepository("../myRepo", "https://github.com/katiliad/vaccineappointment", null);
		return getCommitIds(repo);
	}
	
	public Git cloneRepository(String name, String url, String accessToken) {
	    try {
	        if (Objects.isNull(accessToken))
	            return Git.cloneRepository()
	                    .setURI(url)
	                    .setDirectory(new File(name))
	                    .call();
	        else {
	            return Git.cloneRepository()
	                    .setURI(url)
	                    .setDirectory(new File(name))
	                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(accessToken, ""))
	                    .call();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	/**
	 * Gets all commit ids for a specific git repo.
	 *
	 * @param git the git object
	 */
	public String getCommitIds(Git git) {
	    List<String> commitIds = new ArrayList<>();
	    try {
	        String treeName = getHeadName(git.getRepository());
	        for (RevCommit commit : git.log().add(git.getRepository().resolve(treeName)).call())
	            commitIds.add(commit.getFullMessage());
	    } catch (Exception ignored) {
	    }
	    Collections.reverse(commitIds);

	    List<String> commitObjList = new ArrayList<>();
	    for (String s: commitIds){
	        commitObjList.add(s);
	    }

	    return commitObjList.toString();
	}
	
	private String getHeadName(Repository repo) {
	    String result = null;
	    try {
	        ObjectId id = repo.resolve(Constants.HEAD);
	        result = id.getName();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
	@Autowired
    private ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}