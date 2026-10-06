import { useState, useEffect } from 'react';
import { fetchTasks } from '../api';

export function useTasks(query, status, page, pageSize) {
  const [tasks, setTasks] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Cancel the previous request when inputs change (or on unmount / StrictMode
    // re-run) so a slow, older response can never overwrite a newer one.
    const controller = new AbortController();

    setLoading(true);
    setError(null);

    fetchTasks({ query, status, page, pageSize }, { signal: controller.signal })
      .then((data) => {
        setTasks(data.items);
        setTotal(data.total);
        setLoading(false);
      })
      .catch((err) => {
        if (err.name === 'AbortError') return; // superseded by a newer request
        setError(err.message);
        setLoading(false); // previously missing: UI stayed on "Loading tasks..." forever
      });

    return () => controller.abort();
  }, [query, status, page, pageSize]);

  return { tasks, total, loading, error };
}
