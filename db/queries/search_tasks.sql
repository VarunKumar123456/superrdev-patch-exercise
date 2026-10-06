-- H2-compatible task search query
-- Mirrors TaskRepository.searchTasks (Spring Data repository layer)
--
-- Parameters:
--   :term   — lower-cased search term wrapped in wildcards, e.g. '%api%'
--             (literal % _ \ in the user's text are escaped with \)
--   :status — status filter or NULL for all statuses
--
-- The OR is parenthesised: AND binds tighter than OR, so without the parentheses
-- the archived and status filters only applied to the title branch.

SELECT *
FROM tasks
WHERE archived = FALSE
  AND (LOWER(title) LIKE :term ESCAPE '\' OR LOWER(description) LIKE :term ESCAPE '\')
  AND (:status IS NULL OR status = :status)
ORDER BY created_at DESC, id DESC;
