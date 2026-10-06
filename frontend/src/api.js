const API_BASE = '/api';

export async function fetchTasks({ query = '', status = '', page = 1, pageSize = 10 }, { signal } = {}) {
  const params = new URLSearchParams();
  if (query) params.set('q', query);
  if (status) params.set('status', status);
  params.set('page', String(page));
  params.set('pageSize', String(pageSize));

  const response = await fetch(`${API_BASE}/tasks?${params.toString()}`, { signal });

  if (!response.ok) {
    // The backend answers 400 with { error: "..." }; surface that message when present.
    let detail = '';
    try {
      detail = (await response.json()).error || '';
    } catch {
      // body was not JSON - fall through to the generic message
    }
    throw new Error(detail || `Request failed: ${response.status}`);
  }

  return response.json();
}
