export const API_BASE = "/api";

function getCsrfToken(): string | null {
  const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
  return match ? decodeURIComponent(match[1]) : null;
}

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...((options.headers as Record<string, string>) || {}),
  };

  const method = (options.method || "GET").toUpperCase();
  if (method !== "GET" && method !== "HEAD") {
    let csrf = getCsrfToken();
    if (!csrf) {
      const bootstrap = await fetch(`${API_BASE}/health`, { credentials: "include" });
      if (!bootstrap.ok) throw new Error("Unable to initialize request protection");
      csrf = getCsrfToken();
    }
    if (!csrf) throw new Error("CSRF token unavailable");
    headers["X-XSRF-TOKEN"] = csrf;
  }

  const res = await fetch(`${API_BASE}${path}`, {
    credentials: "include",
    ...options,
    headers,
  });

  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || `Request failed: ${res.status}`);
  }

  return res.status === 204 ? (undefined as T) : res.json();
}
