package com.github.reposearch.search;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private boolean isPlatformEngineer;

    public Author() {
    }

    public Author(String name, boolean isPlatformEngineer) {
        this.name = name;
        this.isPlatformEngineer = isPlatformEngineer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlatformEngineer() {
        return isPlatformEngineer;
    }

    public void setPlatformEngineer(boolean platformEngineer) {
        isPlatformEngineer = platformEngineer;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Author author = (Author) obj;
        return isPlatformEngineer == author.isPlatformEngineer &&
                Objects.equals(id, author.id) &&
                Objects.equals(name, author.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isPlatformEngineer);
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isPlatformEngineer=" + isPlatformEngineer +
                '}';
    }
}