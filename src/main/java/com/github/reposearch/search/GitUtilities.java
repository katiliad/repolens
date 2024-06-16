package com.github.reposearch.search;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.eclipse.jgit.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;

public class GitUtilities {

    public static Git cloneRepository(String name, String url, String accessToken) {
        try {
            deleteClonedFolder(name); // Delete folder if exists

            if (accessToken == null) {
                return Git.cloneRepository()
                        .setURI(url)
                        .setDirectory(new File(name))
                        .call();
            } else {
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

    public static List<Commit> fetchCommitsAndFiles(String repoPath) {
        List<Commit> commitList = new ArrayList<>();
        try (Repository repository = openRepository(repoPath)) {
            Iterable<RevCommit> commits = new Git(repository).log().all().call();
            for (RevCommit revCommit : commits) {
                String sha = revCommit.getName();
                String author = revCommit.getAuthorIdent().getName();
                Date date = revCommit.getAuthorIdent().getWhen();
                String fullDate = formatDate(date); // Format date as needed
                String slimDate = formatSlimDate(date); // Format slim date as needed

                // Checkout commit
                checkoutCommit(repository, sha);

                // Get changed files
                List<String> changedFiles = getChangedFiles(repository, revCommit);

                // Create Commit object
                Commit commit = new Commit(sha, author, date, fullDate, slimDate, changedFiles);
                commitList.add(commit);
            }
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        return commitList;
    }

    private static Repository openRepository(String repoPath) throws IOException {
        return Git.open(new File(repoPath)).getRepository();
    }

    private static void checkoutCommit(Repository repository, String commitId) throws GitAPIException {
        Git git = new Git(repository);
        git.checkout().setName(commitId).call();
    }

    private static List<String> getChangedFiles(Repository repository, RevCommit commit) throws IOException {
        List<String> changedFiles = new ArrayList<>();
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(commit.getTree());
            if (commit.getParentCount() > 0) {
                treeWalk.addTree(commit.getParent(0).getTree());
            }
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
                changedFiles.add(treeWalk.getPathString());
            }
        }
        return changedFiles;
    }

    private static String formatDate(Date date) {
        // Implement date formatting logic if needed
        return date.toString(); // Example: Convert Date to String
    }

    private static String formatSlimDate(Date date) {
        // Implement slim date formatting logic if needed
        return date.toString(); // Example: Convert Date to String
    }

    public static void deleteClonedFolder(String folder) {
        Path folderPath = Path.of(folder);
        try {
            if (Files.exists(folderPath)) {
                if (Files.isDirectory(folderPath)) {
                    Files.walk(folderPath)
                         .map(Path::toFile)
                         .forEach(File::delete);
                } else {
                    Files.delete(folderPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
	