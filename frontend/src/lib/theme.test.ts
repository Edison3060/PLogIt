import { afterEach, describe, expect, it } from "vitest";
import { getSavedTheme, initializeTheme, setTheme } from "./theme";

afterEach(() => {
  localStorage.clear();
  document.documentElement.classList.remove("dark");
});

describe("theme management", () => {
  it("defaults to light and removes stale dark classes", () => {
    document.documentElement.classList.add("dark");

    initializeTheme();

    expect(getSavedTheme()).toBe("light");
    expect(document.documentElement.classList.contains("dark")).toBe(false);
  });

  it("restores the saved dark theme on startup", () => {
    localStorage.setItem("plogit-theme", "dark");

    initializeTheme();

    expect(getSavedTheme()).toBe("dark");
    expect(document.documentElement.classList.contains("dark")).toBe(true);
  });

  it("toggles DOM class and persistence together", () => {
    setTheme("dark");
    expect(localStorage.getItem("plogit-theme")).toBe("dark");
    expect(document.documentElement.classList.contains("dark")).toBe(true);

    setTheme("light");
    expect(localStorage.getItem("plogit-theme")).toBe("light");
    expect(document.documentElement.classList.contains("dark")).toBe(false);
  });
});
