package com.github.reposearch.search;

public class FileChangeInfo {

    private String extension;
    private long count;

    public FileChangeInfo(String extension, long count) {
        this.extension = extension;
        this.count = count;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}