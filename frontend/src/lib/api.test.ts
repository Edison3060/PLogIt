import { beforeEach, describe, expect, it, vi } from "vitest";
import { apiFetch } from "./api";
import { fetchCurrentUser, loginUser, logoutUser, registerUser } from "./auth";

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

  it("rejects unsafe operations when bootstrap produces no CSRF cookie", async () => {
    fetchMock.mockResolvedValue({ ok: true, status: 200 });
    await expect(logoutUser()).rejects.toThrow("CSRF token unavailable");
    expect(fetchMock).toHaveBeenCalledWith("/api/health", { credentials: "include" });
    expect(fetchMock).toHaveBeenCalledTimes(1);
  });

  it("preserves API error messages on failed requests", async () => {
    fetchMock.mockResolvedValue({
      ok: false,
      status: 403,
      json: async () => ({ message: "Forbidden" }),
    });

    await expect(apiFetch("/engagements/1")).rejects.toThrow("Forbidden");
  });

  it("bootstraps a real CSRF cookie before registration", async () => {
    fetchMock.mockImplementation(async (url: string) => {
      if (url === "/api/health") {
        document.cookie = "XSRF-TOKEN=preflight-token; Path=/";
        return { ok: true, status: 200 };
      }
      return {
        ok: true,
        status: 202,
        json: async () => ({ message: "Registration request received. Please sign in" }),
      };
    });

    await expect(registerUser("user@test.local", "password123", "User")).resolves.toEqual({
      message: "Registration request received. Please sign in",
    });
    expect(fetchMock).toHaveBeenNthCalledWith(1, "/api/health", { credentials: "include" });
    expect(fetchMock).toHaveBeenNthCalledWith(2, "/api/auth/register", {
      credentials: "include",
      method: "POST",
      body: JSON.stringify({ email: "user@test.local", password: "password123", displayName: "User" }),
      headers: { "Content-Type": "application/json", "X-XSRF-TOKEN": "preflight-token" },
    });
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
