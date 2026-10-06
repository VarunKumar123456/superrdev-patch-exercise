package com.internal.tasktracker;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Search non-archived tasks by term (title OR description) AND optional status.
    // - The OR is wrapped in parentheses: AND binds tighter than OR, so without them
    //   the archived/status filters only applied to the title branch.
    // - ESCAPE '\' lets the caller treat % and _ in the user's text as literals.
    // - Pagination happens in the database (LIMIT/OFFSET via Pageable) instead of
    //   loading every matching row into memory. "id DESC" makes the order stable
    //   for rows that share a created_at value, so pages never overlap or skip rows.
    @Query(value = "SELECT * FROM tasks "
                 + "WHERE archived = FALSE "
                 + "AND (LOWER(title) LIKE :term ESCAPE '\\' OR LOWER(description) LIKE :term ESCAPE '\\') "
                 + "AND (:status IS NULL OR status = :status) "
                 + "ORDER BY created_at DESC, id DESC",
           countQuery = "SELECT COUNT(*) FROM tasks "
                      + "WHERE archived = FALSE "
                      + "AND (LOWER(title) LIKE :term ESCAPE '\\' OR LOWER(description) LIKE :term ESCAPE '\\') "
                      + "AND (:status IS NULL OR status = :status)",
           nativeQuery = true)
    Page<Task> searchTasks(@Param("term") String term,
                           @Param("status") String status,
                           Pageable pageable);
}
