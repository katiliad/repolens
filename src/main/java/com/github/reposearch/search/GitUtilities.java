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

public class GitUtilities {
	
	public static Git cloneRepository(String name, String url, String accessToken) {
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
	
	public static List<CommitObj> getCommitIds(Git git) {
	    List<String> commitIds = new ArrayList<>();
	    try {
	        String treeName = getHeadName(git.getRepository());
	        for (RevCommit commit : git.log().add(git.getRepository().resolve(treeName)).call())
	            commitIds.add(commit.getName());
	    } catch (Exception ignored) {
	    }
	    Collections.reverse(commitIds);

	    List<CommitObj> commitObjList = new ArrayList<>();
	    for (String s: commitIds){
	        commitObjList.add(new CommitObj(s, getDateOfCommit(git,s)));
	    }

	    commitObjList.sort(Comparator.comparing(CommitObj::getDate));

	    return commitObjList;
	}


	private static String getHeadName(Repository repo) {
	    String result = null;
	    try {
	        ObjectId id = repo.resolve(Constants.HEAD);
	        result = id.getName();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
	/**
	 * Checkouts to specified commitId (SHA)
	 *
	 * @param sha             the revision we are checking out to
	 * @param git             a git object
	 */
	public static void checkout(String name, String url, String accessToken, String sha, Git git) throws GitAPIException {
	    try {
	        git.checkout().setCreateBranch(true).setName("version" + sha).setStartPoint(sha).call();
	    } catch (GitAPIException e) {
	        deleteClonedFolder(name);
	        cloneRepository(name, url, accessToken);
	        git.checkout().setCreateBranch(true).setName("version" + sha).setStartPoint(sha).call();
	    }
	}


	private static void deleteClonedFolder(String folder){
	    if (isWindows()) {
	        try {
	            Process proc = Runtime.getRuntime().exec("cmd /c cd " +System.getProperty("user.dir")+ "\\" + folder +
	                    " && del .git\\index.lock " );
	            System.out.println("start analysis");
	            BufferedReader inputReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	            String inputLine;
	            while ((inputLine = inputReader.readLine()) != null) {
	                System.out.println(" "+inputLine);
	            }
	            BufferedReader errorReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
	            String errorLine;
	            while ((errorLine = errorReader.readLine()) != null) {
	                System.out.println(errorLine);
	            }
	            // TO FIX deleteSourceCode(new File(System.getProperty("user.dir")+ "\\" + folder));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    else {
	        try {
	            ProcessBuilder pbuilder = new ProcessBuilder("bash", "-c",
	                    "cd '" + System.getProperty("user.dir") +""+ "' ; rm -rf " + folder);
	            File err = new File("err.txt");
	            pbuilder.redirectError(err);
	            Process p = pbuilder.start();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	            String line;
	            while ((line = reader.readLine()) != null) {
	                System.out.println(" "+line);
	            }
	            BufferedReader reader_2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	            String line_2;
	            while ((line_2 = reader_2.readLine()) != null) {
	                System.out.println(line_2);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
	
	private static String getDeveloperOfCommit(Git git, String sha){
	    RevCommit headCommit;
	    try {
	        headCommit = git.getRepository().parseCommit(ObjectId.fromString(sha));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	    return headCommit.getAuthorIdent().getName();
	}
	
	private static Date getDateOfCommit(Git git, String sha){
	    RevCommit headCommit;
	    try {
	        headCommit = git.getRepository().parseCommit(ObjectId.fromString(sha));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	    return headCommit.getAuthorIdent().getWhen();
	}
	

//RevCommit headCommit;
//try {
//    headCommit = git.getRepository().parseCommit(ObjectId.fromString(commitIds.get(i).getSha()));
//} catch (Exception e) {
//    e.printStackTrace();
//    return null;
//}
//if(headCommit.getParentCount()!=0) {
//
// RevCommit diffWith = Objects.requireNonNull(headCommit).getParent(0);
// FileOutputStream stdout = new FileOutputStream(FileDescriptor.out);
//
// try (DiffFormatter diffFormatter = new DiffFormatter(stdout)) {
//
//    diffFormatter.setRepository(git.getRepository());
//    try {
//        RenameDetector renameDetector = new RenameDetector(git.getRepository());
//        renameDetector.addAll(diffFormatter.scan(diffWith, headCommit));
//
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        DiffFormatter df = new DiffFormatter(out);
//        df.setRepository(git.getRepository());
//        for (org.eclipse.jgit.diff.DiffEntry entry : renameDetector.compute()) {
//
//          //path =  entry.getNewPath().toLowerCase();
}
