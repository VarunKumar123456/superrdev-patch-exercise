package com.internal.tasktracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    static final int MAX_PAGE_SIZE = 100;
    static final int MAX_PAGE = 100_000;      // keeps (page * pageSize) well inside int range
    static final int MAX_QUERY_LENGTH = 100;

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<Map<String, Object>> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        String query = q == null ? "" : q.trim();
        if (query.length() > MAX_QUERY_LENGTH) {
            return badRequest("q must be at most " + MAX_QUERY_LENGTH + " characters");
        }
        if (page < 1 || page > MAX_PAGE) {
            return badRequest("page must be between 1 and " + MAX_PAGE);
        }
        if (pageSize < 1) {
            return badRequest("pageSize must be at least 1");
        }
        int effectivePageSize = Math.min(pageSize, MAX_PAGE_SIZE);

        // An unknown status used to throw IllegalArgumentException -> HTTP 500.
        // It is a client mistake, so answer 400 and say what is allowed.
        String normalizedStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                normalizedStatus = TaskStatus.valueOf(status.trim().toUpperCase(Locale.ROOT)).name();
            } catch (IllegalArgumentException e) {
                return badRequest("Invalid status. Allowed values: " + Arrays.toString(TaskStatus.values()));
            }
        }

        String searchTerm = "%" + escapeLike(query.toLowerCase(Locale.ROOT)) + "%";

        log.debug("searchTasks queryLength={} status={} page={} pageSize={}",
                query.length(), normalizedStatus, page, effectivePageSize);

        Page<Task> result = taskRepository.searchTasks(
                searchTerm, normalizedStatus, PageRequest.of(page - 1, effectivePageSize));

        // Response shape is unchanged (items/total/page/pageSize); totalPages is additive.
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", result.getContent());
        response.put("total", result.getTotalElements());
        response.put("page", page);
        response.put("pageSize", effectivePageSize);
        response.put("totalPages", result.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /** Escape LIKE wildcards so user text such as "50%" or "a_b" is matched literally. */
    static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static ResponseEntity<Map<String, Object>> badRequest(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", message);
        return ResponseEntity.badRequest().body(body);
    }
}
