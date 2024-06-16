package com.github.reposearch.search;

import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Commit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sha; // Git commit SHA ID

    private String author;

    @ElementCollection
    private List<String> paths;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public Commit() {
    }

    public Commit(String sha, String author, List<String> paths, Project project) {
        this.sha = sha;
        this.author = author;
        this.paths = paths;
        this.project = project;
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

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Commit commit = (Commit) obj;
        return Objects.equals(id, commit.id) &&
                Objects.equals(sha, commit.sha) &&
                Objects.equals(author, commit.author) &&
                Objects.equals(paths, commit.paths) &&
                Objects.equals(project, commit.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sha, author, paths, project);
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id=" + id +
                ", sha='" + sha + '\'' +
                ", author='" + author + '\'' +
                ", paths=" + paths +
                ", project=" + project +
                '}';
    }
}