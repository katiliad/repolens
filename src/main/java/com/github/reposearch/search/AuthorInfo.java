package com.github.reposearch.search;

public class AuthorInfo {
    private String name;
    private boolean platformEngineer;
    private long commitCount;

    public AuthorInfo(String name, boolean platformEngineer, long commitCount) {
        this.name = name;
        this.platformEngineer = platformEngineer;
        this.commitCount = commitCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlatformEngineer() {
        return platformEngineer;
    }

    public void setPlatformEngineer(boolean platformEngineer) {
        this.platformEngineer = platformEngineer;
    }

    public long getCommitCount() {
        return commitCount;
    }

    public void setCommitCount(long commitCount) {
        this.commitCount = commitCount;
    }
}
