package com.github.reposearch.search;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class GitUtilities {

    public static Git cloneRepository(String name, String url, String accessToken) throws GitAPIException {

        return Git.cloneRepository()
                .setURI(url)
                .setDirectory(new File(name))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(accessToken, ""))
                .call();
    }
    
    public static void closeAndDeleteRepository(File dir) {
        try {
            closeRepository(dir);
            deleteDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void closeRepository(File localPath) throws IOException {
        try (Git git = Git.open(localPath)) {
            git.getRepository().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    public static List<Commit> getCommits(Git git) throws GitAPIException, IOException {
        List<Commit> commits = new ArrayList<>();
        Iterable<RevCommit> revCommits = git.log().call();
        for (RevCommit revCommit : revCommits) {
            String sha = revCommit.getName();
            String authorName = revCommit.getAuthorIdent().getName();
            Date date = revCommit.getAuthorIdent().getWhen();
            String fullDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
            String slimDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            List<String> changedFiles = getChangedFiles(git, sha);
            Author author = new Author(authorName);
            Commit new_commit = new Commit(sha, date, fullDate, slimDate, changedFiles);
            new_commit.setAuthor(author);
            commits.add(new_commit);
        }
        return commits;
    }

    private static List<String> getChangedFiles(Git git, String sha) throws IOException, GitAPIException {
        List<String> changedFiles = new ArrayList<>();

        try (RevWalk revWalk = new RevWalk(git.getRepository())) {
            ObjectId commitId = ObjectId.fromString(sha);
            RevCommit commit = revWalk.parseCommit(commitId);

            if (commit.getParentCount() > 0) {
                RevCommit parent = revWalk.parseCommit(commit.getParent(0).getId());

                try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
                    diffFormatter.setRepository(git.getRepository());
                    List<DiffEntry> diffs = diffFormatter.scan(parent.getTree(), commit.getTree());

                    for (DiffEntry diff : diffs) {
                        changedFiles.add(diff.getNewPath());
                    }
                }
            } else {
                // Handle the case where the commit has no parent (initial commit)
                try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
                    diffFormatter.setRepository(git.getRepository());
                    List<DiffEntry> diffs = diffFormatter.scan(null, commit.getTree());

                    for (DiffEntry diff : diffs) {
                        changedFiles.add(diff.getNewPath());
                    }
                }
            }
        }
        return changedFiles;
    }

}