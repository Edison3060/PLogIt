import { beforeEach, describe, expect, it, vi } from "vitest";
import { apiFetch } from "./api";
import { fetchCurrentUser, loginUser, logoutUser } from "./auth";

describe("API authentication and CSRF", () => {
  const fetchMock = vi.fn();

  beforeEach(() => {
    fetchMock.mockReset();
    vi.stubGlobal("fetch", fetchMock);
    document.cookie = "XSRF-TOKEN=; Max-Age=0; Path=/";
  });

  it("sends cookies on GET without a CSRF header", async () => {
    fetchMock.mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => ({ id: 1 }),
    });

    await expect(apiFetch("/auth/me")).resolves.toEqual({ id: 1 });
    expect(fetchMock).toHaveBeenCalledWith("/api/auth/me", {
      credentials: "include",
      headers: { "Content-Type": "application/json" },
    });
  });

  it("decodes the CSRF cookie on state-changing requests", async () => {
    document.cookie = "XSRF-TOKEN=token%2Bwith%2Fsymbols; Path=/";
    fetchMock.mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => ({ id: 1 }),
    });

    await loginUser("user@test.local", "password");

    expect(fetchMock).toHaveBeenCalledWith("/api/auth/login", {
      credentials: "include",
      method: "POST",
      body: JSON.stringify({ email: "user@test.local", password: "password" }),
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": "token+with/symbols",
      },
    });
  });

  it("does not invent a CSRF header when the cookie is missing", async () => {
    fetchMock.mockResolvedValue({
      ok: true,
      status: 204,
    });

    await expect(logoutUser()).resolves.toBeUndefined();
    expect(fetchMock).toHaveBeenCalledWith("/api/auth/logout", {
      credentials: "include",
      method: "POST",
      headers: { "Content-Type": "application/json" },
    });
  });

  it("preserves API error messages on failed requests", async () => {
    fetchMock.mockResolvedValue({
      ok: false,
      status: 403,
      json: async () => ({ message: "Forbidden" }),
    });

    await expect(apiFetch("/engagements/1")).rejects.toThrow("Forbidden");
  });

  it("treats a rejected session lookup as logged out", async () => {
    fetchMock.mockResolvedValue({
      ok: false,
      status: 403,
      json: async () => ({ message: "Forbidden" }),
    });

    await expect(fetchCurrentUser()).resolves.toBeNull();
  });
});
