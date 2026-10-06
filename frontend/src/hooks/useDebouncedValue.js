import { useState, useEffect } from 'react';

// Returns `value` only after it has stopped changing for `delay` ms.
// Used so the search box does not fire one API request per keystroke.
export function useDebouncedValue(value, delay = 300) {
  const [debounced, setDebounced] = useState(value);

  useEffect(() => {
    const id = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(id);
  }, [value, delay]);

  return debounced;
}
