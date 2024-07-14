package com.github.reposearch.search;

import com.github.reposearch.search.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommitRepository extends JpaRepository<Commit, Long> {
    List<Commit> findByProject(Project project);
    void deleteByProject(Project project);
    List<Commit> findByProjectAndAuthor(Project project, Author author);
    List<Commit> findByProjectAndAuthorName(Project project, String authorName);
}