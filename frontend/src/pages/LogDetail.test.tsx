import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import LogDetailPage from "./LogDetail";
import { useLog, useLogHistory, useTransitionLog } from "../hooks/useLogs";
import { useCurrentUser } from "../hooks/useAuth";
import { fetchAttachments } from "../lib/logs";

vi.mock("../hooks/useLogs", () => ({
  useLog: vi.fn(),
  useLogHistory: vi.fn(),
  useTransitionLog: vi.fn(),
}));

vi.mock("../hooks/useAuth", () => ({
  useCurrentUser: vi.fn(),
}));

vi.mock("../lib/logs", async (importOriginal) => {
  const actual = await importOriginal<typeof import("../lib/logs")>();
  return {
    ...actual,
    fetchAttachments: vi.fn().mockResolvedValue([]),
  };
});

function renderLog(log: Record<string, unknown>) {
  vi.mocked(useLog).mockReturnValue({
    data: log,
    isLoading: false,
  } as ReturnType<typeof useLog>);
  return render(
    <MemoryRouter initialEntries={["/engagements/1/logs/test-log"]}>
      <Routes>
        <Route
          path="/engagements/:id/logs/:logId"
          element={<LogDetailPage />}
        />
      </Routes>
    </MemoryRouter>
  );
}

describe("LogDetail HTML rendering", () => {
  const log = {
    id: "test-log",
    title: "Test log",
    authorId: 1,
    authorDisplayName: "User",
    leaderId: 2,
    createdAt: "2026-01-01T00:00:00Z",
    lastEditedAt: "2026-01-01T00:00:00Z",
    activityType: "RECONNAISSANCE",
    outcome: "IN_PROGRESS",
    reviewState: "DRAFT",
    description: "<img src=x onerror=alert(1)>",
    result: "<script>alert(2)</script>",
    descriptionHtml: "",
    resultHtml: "",
    target: "",
    toolUsed: "",
    tags: [],
  };

  beforeEach(() => {
    vi.mocked(useCurrentUser).mockReturnValue({
      data: { id: 1 },
    } as ReturnType<typeof useCurrentUser>);
    vi.mocked(useLogHistory).mockReturnValue({
      data: [],
      isLoading: false,
    } as ReturnType<typeof useLogHistory>);
    vi.mocked(useTransitionLog).mockReturnValue({
      mutate: vi.fn(),
      isPending: false,
    } as unknown as ReturnType<typeof useTransitionLog>);
    vi.mocked(fetchAttachments).mockResolvedValue([]);
  });

  it("never uses raw descriptions or results when sanitized HTML is empty", () => {
    const { container } = renderLog(log);

    expect(screen.getByText("Description")).toBeInTheDocument();
    expect(screen.getByText("Result")).toBeInTheDocument();
    expect(container.querySelector(".prose img")).toBeNull();
    expect(container.querySelector(".prose script")).toBeNull();
    expect(container.querySelector(".prose")?.textContent).toBe("");
    expect(container.querySelectorAll(".prose")[1]?.textContent).toBe("");
  });

  it("renders the sanitized HTML returned by the backend", () => {
    renderLog({
      ...log,
      descriptionHtml: "<p>Sanitized description</p>",
      resultHtml: "<strong>Sanitized result</strong>",
    });

    expect(screen.getByText("Sanitized description")).toBeInTheDocument();
    expect(screen.getByText("Sanitized result")).toBeInTheDocument();
  });
});
