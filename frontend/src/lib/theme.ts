const THEME_STORAGE_KEY = "plogit-theme";

export type Theme = "light" | "dark";

export function getSavedTheme(): Theme {
  return localStorage.getItem(THEME_STORAGE_KEY) === "dark" ? "dark" : "light";
}

export function initializeTheme(): void {
  document.documentElement.classList.toggle("dark", getSavedTheme() === "dark");
}

export function setTheme(theme: Theme): void {
  document.documentElement.classList.toggle("dark", theme === "dark");
  localStorage.setItem(THEME_STORAGE_KEY, theme);
}
