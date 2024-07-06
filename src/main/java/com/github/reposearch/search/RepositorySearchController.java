package com.github.reposearch.search;

import com.github.reposearch.search.Project;
import com.github.reposearch.search.RepoSearchService;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class RepositorySearchController {
	
	String repos_folder = "../repositories/";
	
	@Autowired
	private RepoSearchService rs;
	
    @GetMapping
    public List<Project> getAllProjects() {
        return rs.getAllProjects();
    }

	@GetMapping("/{name}")
	public ResponseEntity<Project> getProjectByName(@PathVariable String name) {
        Project project = rs.getProjectByName(name);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(project);
    }


	@PostMapping("/create")
    public ResponseEntity<String> createProject(@RequestParam String url, @RequestParam String name) throws GitAPIException, IOException {

		if (rs.getProjectByName(name) != null) {
            return new ResponseEntity<>("Project with this name already exists in the database", HttpStatus.CONFLICT);
        }
		
		String localPath = repos_folder + name;
		File dir = new File(localPath);
		if (dir.exists()) {
			try {
				GitUtilities.closeRepository(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
			GitUtilities.deleteDirectory(dir);
            if(dir.exists()) {
            	return new ResponseEntity<>("Directory with this name already exists in the filesystem", HttpStatus.CONFLICT);
            }
        }
        Git git = GitUtilities.cloneRepository(localPath, url, null);
        
        List<Commit> commits = GitUtilities.getCommits(git);
        Map<String, Author> authorsMap = new HashMap<>();
       
        for (Commit commit : commits) {
            String authorName = commit.getAuthor().getName();
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
            Author author = authorsMap.get(commit.getAuthor().getName());
            commit.setAuthor(author);
            rs.saveCommit(commit);
        }

        Project project = new Project(url, name);
        for (Commit commit : commits) {
            project.addCommit(commit);
        }
        rs.saveProject(project);
        return new ResponseEntity<>("Project created successfully", HttpStatus.CREATED);
    }

	 @DeleteMapping("/{name}")
	    public ResponseEntity<String> deleteProjectByName(@PathVariable String name) {
	        String resultMessage = rs.deleteProjectByName(name);

	        if (resultMessage.startsWith("Error")) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultMessage);
	        } else {
	            return ResponseEntity.ok(resultMessage);
	        }
	 }
    
    @GetMapping("/{name}/authors")
    public ResponseEntity<List<AuthorInfo>> getAuthorsByProjectName(@PathVariable String name,
            @RequestParam(required = false) Boolean platformEng) {
		List<AuthorInfo> authorInfos = rs.getAuthorsByProjectName(name, platformEng);
		if (authorInfos == null) {
		return ResponseEntity.badRequest().body(List.of(new AuthorInfo("Project does not exist", false, 0)));
		} else if (authorInfos.isEmpty()) {
		return ResponseEntity.ok(List.of(new AuthorInfo("No authors found", false, 0)));
		}
		return ResponseEntity.ok(authorInfos);
	}
}

