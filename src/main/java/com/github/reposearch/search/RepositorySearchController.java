package com.github.reposearch.search;

import com.github.reposearch.search.Project;
import com.github.reposearch.search.RepoSearchService;

import java.io.IOException;
import java.util.*;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class RepositorySearchController {
	
	@Autowired
	private RepoSearchService rs;
	
    @GetMapping
    public List<Project> getAllProjects() {
        return rs.getAllProjects();
    }

	@GetMapping("/{name}")
	public Project getProjectByName(@PathVariable String name) {
	    return rs.getProjectByName(name);
	}

	@PostMapping("/create")
    public Project createProject(@RequestParam String url, @RequestParam String name) throws GitAPIException, IOException {

		String localPath = "../myRepo/" + name;
		
        Git git = GitUtilities.cloneRepository(localPath, url, null);

        List<Commit> commits = GitUtilities.getCommits(git);

        Map<String, Author> authorsMap = new HashMap<>();

        
        for (Commit commit : commits) {
            String authorName = commit.getAuthorName();
            authorsMap.putIfAbsent(authorName, new Author(authorName));
            Author author = authorsMap.get(authorName);

            for (String filePath : commit.getChangedFiles()) {
                if (!(filePath.startsWith(".github/actions") || filePath.startsWith(".github/workflows"))) {
                    author.setPlatformEngineer(false);
                    break;
                }
            }
        }

        for (Map.Entry<String, Author> entry : authorsMap.entrySet()) {
            rs.saveAuthor(entry.getValue());
        }

        for (Commit commit : commits) {
            Author author = authorsMap.get(commit.getAuthorName());
            commit.setAuthor(author);
            rs.saveCommit(commit);
        }

        Project project = new Project(url, name);
        for (Commit commit : commits) {
            project.addCommit(commit);
        }
        return rs.saveProject(project);
    }

    @DeleteMapping("/{name}")
    public void deleteProjectByName(@PathVariable String name) {
        rs.deleteProjectByName(name);
    }
}

