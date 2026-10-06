# Handwritten notes - what to cover (write in YOUR OWN words, by hand)

The reviewers ask for: **where** the bug is, **how you found it**, **root cause**, **fix + why**.
They will ask you to walk through these on a call, so write only what you can explain without looking.
Use one page per bug. Photograph each page (good light, flat) into `handwritten/` (bug1.jpg, bug2.jpg...).

## Page 1 - Search SQL precedence (highest value)
- Where: TaskRepository.java line 14-16 (also db/queries/search_tasks.sql, Oracle package x2)
- Found: `?q=api&status=DONE` returned archived and non-DONE tasks. Compared SQL to the output.
- Root cause: AND binds tighter than OR, so it read (archived=false AND title LIKE) OR (desc LIKE AND status).
- Fix: parentheses around the two LIKEs. Why: smallest change, keeps one query.
- Evidence I measured: with q=api, old query returned 10 rows incl. 2 archived; fixed returns 8.

## Page 2 - Thread.sleep in controller
- Where: TaskController line 35-42. Found by: blank search felt slow (~1s). Root cause: sleep scaled with 10 - query length. Fix: removed.

## Page 3 - Pagination + validation
- Loaded ALL rows then subList in memory; page=0 gives negative start -> exception. Fix: Pageable + count query, validate page/pageSize, cap pageSize at 100, tie-break ORDER BY id.

## Page 4 - Invalid status => 500
- TaskStatus.valueOf throws IllegalArgumentException. Fix: catch, return 400 with allowed values.

## Page 5 - useTasks hook
- setLoading(false) missing in catch -> stuck "Loading". No abort -> out-of-order responses. Error never cleared.

## Page 6 - Frontend UX
- No debounce (request per keystroke), page not reset when filters change, loading box flicker.

## Page 7 - Oracle package
- Same precedence bug in COUNT and the ROWNUM page query; NULL/invalid page args; wildcard escaping.

Also be ready to say: what you did NOT fix, biggest risk, and how you used AI.
