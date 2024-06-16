package com.github.reposearch.search;

import com.github.reposearch.search.Project;
import com.github.reposearch.search.RepoSearchService;

import java.util.*;
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

    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable Long id) {
        return rs.getProjectById(id);
    }

    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return rs.saveProject(project);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
    	rs.deleteProject(id);
    }
}

