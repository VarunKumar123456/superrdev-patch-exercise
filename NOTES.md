# NOTES

## Summary of changes
1. **Search SQL precedence bug** (`TaskRepository`, `db/queries`, Oracle package): `A AND B OR C AND D` let archived tasks leak in and ignored the status filter. Added parentheses around the OR (Oracle had it in both the count and page queries).
2. **Removed `Thread.sleep`** in the controller (up to 1s delay on short/blank queries).
3. **Pagination in the database** (`Pageable` + count query), stable `created_at DESC, id DESC` order, validated page/pageSize.
4. **Invalid `status`** returns 400 with a message instead of 500.
5. **LIKE wildcards** (`%`, `_`) typed by users are escaped.
6. **`useTasks` hook**: loading never cleared on error, stale responses could overwrite newer ones (now `AbortController`), error never reset.
7. **Frontend UX**: 300ms debounce, page resets on filter change, rows stay visible while refreshing, aria-labels.
8. Schema: `archived NOT NULL` plus an index; `println` replaced with SLF4J.

## What I chose not to change
Result ranking (title before description), `status` as String instead of enum, hard-coded CORS origin, auth and rate limiting. These are features or design changes, not patch-sized bugs.

## Biggest remaining risk
No automated tests, so a search-query regression would go unnoticed. Also `LIKE '%x%'` cannot use an index and will not scale.

## Tools used
Claude helped me read the code, draft the fixes and check the SQL logic against the seed data. I ran the Spring Boot backend and frontend locally and tested task search, archived-task filtering, the Done status filter, pagination, and invalid status handling.
