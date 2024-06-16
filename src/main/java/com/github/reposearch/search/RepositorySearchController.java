package com.github.reposearch.search;

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
}

