package com.github.reposearch.search;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sha;
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;
    
    public Commit() {
    }

    public Commit(String sha, Date date, String fullDate, String slimDate, List<String> changedFiles) {
        this.sha = sha;
        this.date = date;
        this.fullDate = fullDate;
        this.slimDate = slimDate;
        this.changedFiles = changedFiles;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
	public List<String> getChangedFiles() {
		return changedFiles;
	}

	public void setChangedFiles(List<String> changedFiles) {
		this.changedFiles = changedFiles;
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
    
    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id=" + id +
                ", sha='" + sha + '\'' +
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

	public Author getAuthor() {
		return this.author;
	}
}