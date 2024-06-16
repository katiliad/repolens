package com.github.reposearch.search;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sha;
    private String author;
    private Date date;
    private String fullDate;
    private String slimDate;
    
    @ElementCollection
    @CollectionTable(name = "changed_files", joinColumns = @JoinColumn(name = "commit_id"))
    @Column(name = "path")
    private List<String> changedFiles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    public Commit() {
    }

    public Commit(String sha, String author, Date date, String fullDate, String slimDate, List<String> changedFilePaths) {
        this.sha = sha;
        this.author = author;
        this.date = date;
        this.fullDate = fullDate;
        this.slimDate = slimDate;
        this.changedFiles = changedFilePaths;
    }

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFullDate() {
        return fullDate;
    }

    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }

    public String getSlimDate() {
        return slimDate;
    }

    public void setSlimDate(String slimDate) {
        this.slimDate = slimDate;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id=" + id +
                ", sha='" + sha + '\'' +
                ", author='" + author + '\'' +
                ", date=" + date +
                ", fullDate='" + fullDate + '\'' +
                ", slimDate='" + slimDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Commit commit = (Commit) obj;
        return id.equals(commit.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}