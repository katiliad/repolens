package com.github.reposearch.search;

import com.github.reposearch.search.Project;
import com.github.reposearch.search.RepoSearchService;

import java.util.*;

import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class RepositorySearchController {
	
	@Autowired
	private RepoSearchService rs;
	
	@GetMapping
	public String helloworld() {
		return rs.getAllCommitsForMyRepo();
	}
	
//    @GetMapping
//    public List<Project> getAllProjects() {
//        return rs.getAllProjects();
//    }

	@GetMapping("/{name}")
	public Project getProjectByName(@PathVariable String name) {
	    return rs.getProjectByName(name);
	}

    @PostMapping("/create")
    public Project createProject(@RequestParam String url, @RequestParam String name,  @RequestParam(required = false) String accessToken) {
        String localPath = "../myRepo/" + name;
        Git git = GitUtilities.cloneRepository(localPath, url, accessToken);

        List<Commit> commitList = GitUtilities.fetchCommitsAndFiles(localPath);

        Project project = new Project(url, name);
        for (Commit commit : commitList) {
            project.addCommit(commit); 
        }

        return rs.saveProject(project);
    }

    @DeleteMapping("/{name}")
    public void deleteProjectByName(@PathVariable String name) {
        rs.deleteProjectByName(name);
    }
}

