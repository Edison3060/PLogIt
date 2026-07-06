import { apiFetch } from "./api";

export interface User {
  id: number;
  email: string;
  displayName: string;
}

export async function fetchCurrentUser(): Promise<User | null> {
  try {
    return await apiFetch<User>("/auth/me");
  } catch {
    return null;
  }
}

export async function loginUser(email: string, password: string): Promise<User> {
  return apiFetch<User>("/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

export async function registerUser(
  email: string,
  password: string,
  displayName: string
): Promise<User> {
  return apiFetch<User>("/auth/register", {
    method: "POST",
    body: JSON.stringify({ email, password, displayName }),
  });
}

export async function logoutUser(): Promise<void> {
  await apiFetch<void>("/auth/logout", { method: "POST" });
}
