package com.github.reposearch.search;

import com.github.reposearch.search.Project;
import com.github.reposearch.search.RepoSearchService;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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

		GitUtilities.deleteDirectory(dir);
        if (dir.exists()) {
            return new ResponseEntity<>("Directory with this name already exists in the filesystem", HttpStatus.CONFLICT);
        }

        Git git = GitUtilities.cloneRepository(localPath, url, null);
        
        List<Commit> commits = GitUtilities.getCommits(git);
        Map<String, Author> authorsMap = new HashMap<>();
       
        for (Commit commit : commits) {
            String authorName = commit.getAuthor().getName();
            authorsMap.putIfAbsent(authorName, new Author(authorName));
            Author author = authorsMap.get(authorName);

            boolean isDevopsEngineer = author.isDevopsEngineer();
            boolean isPlatformEngineer = author.isPlatformEngineer();
            for (String filePath : commit.getChangedFiles()) {
                if (filePath.startsWith(".github/actions") || filePath.startsWith(".github/workflows")) {
                    isDevopsEngineer = true;
                } else {
                    isPlatformEngineer = false;
                }
            }
            
            if(isPlatformEngineer) {
            	isDevopsEngineer = false;
            }
            
            author.setDevopsEngineer(isDevopsEngineer);
            author.setPlatformEngineer(isPlatformEngineer);
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
        GitUtilities.closeAndDeleteRepository(dir, git);
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
	 public ResponseEntity<List<AuthorInfo>> getAuthorsByProjectName(
	         @PathVariable String name,
	         @RequestParam(required = false) Boolean platformEng,
	         @RequestParam(required = false) Boolean devopsEng) {

	     Project project = rs.getProjectByName(name);

	     if (project == null) {
	         return ResponseEntity.badRequest().body(List.of(new AuthorInfo("Project does not exist", false, false, 0)));
	     }

	     List<Commit> commits = rs.getCommitsByProjectName(name);
	     Map<Author, Long> authorCommitCounts = rs.getAuthorCommitCountsByProjectName(name);

	     List<AuthorInfo> authorInfos = commits.stream()
	             .map(Commit::getAuthor)
	             .distinct()
	             .filter(author -> (platformEng == null || author.isPlatformEngineer() == platformEng)
	                     && (devopsEng == null || author.isDevopsEngineer() == devopsEng))
	             .map(author -> {
	                 long commitCount = authorCommitCounts.getOrDefault(author, 0L);
	                 return new AuthorInfo(author.getName(), author.isPlatformEngineer(), author.isDevopsEngineer(), commitCount);
	             })
	             .sorted(Comparator.comparingLong(AuthorInfo::getCommitCount).reversed())
	             .collect(Collectors.toList());

	     if (authorInfos.isEmpty()) {
	         return ResponseEntity.ok(List.of(new AuthorInfo("No authors found", false, false, 0)));
	     } else {
	         return ResponseEntity.ok(authorInfos);
	     }
	 }
    
    @GetMapping("/{project}/changedFiles/{author}")
    public ResponseEntity<?> getFileChangesByProjectAndAuthor(
            @PathVariable String project,
            @PathVariable String author) {
        try {
            String decodedAuthor = URLDecoder.decode(author, StandardCharsets.UTF_8.name());

            Project proj = rs.getProjectByName(project);
            if (proj == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
            }

            Author auth = rs.getAuthorByNameAndProject(decodedAuthor, proj);
            if (auth == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found");
            }

            List<FileChangeInfo> fileChanges = rs.getFileChangesByProjectAndAuthor(project, decodedAuthor);
            if (fileChanges.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No file changes found for this author");
            }
            fileChanges.sort(Comparator.comparingLong(FileChangeInfo::getCount).reversed());
            return ResponseEntity.ok(fileChanges);
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error decoding author name");
        }
    }
}

