package com.github.reposearch.search;

import com.github.reposearch.search.Commit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitRepository extends JpaRepository<Commit, Long> {
}