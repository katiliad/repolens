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

	@GetMapping("/{name}")
	public Project getProjectByName(@PathVariable String name) {
	    return rs.getProjectByName(name);
	}

    @PostMapping("/create")
    public Project createProject(@RequestParam String url, @RequestParam String name) {
    	Project project = new Project(url, name);
    	return rs.saveProject(project);
    }

    @DeleteMapping("/{name}")
    public void deleteProjectByName(@PathVariable String name) {
        rs.deleteProjectByName(name);
    }
}

