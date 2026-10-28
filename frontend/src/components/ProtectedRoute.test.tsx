import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ProtectedRoute from "./ProtectedRoute";
import { useCurrentUser } from "../hooks/useAuth";

vi.mock("../hooks/useAuth", () => ({
  useCurrentUser: vi.fn(),
}));

function renderRoute() {
  return render(
    <MemoryRouter initialEntries={["/private"]}>
      <Routes>
        <Route
          path="/private"
          element={<ProtectedRoute><div>Private content</div></ProtectedRoute>}
        />
        <Route path="/login" element={<div>Login page</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe("ProtectedRoute", () => {
  beforeEach(() => {
    vi.mocked(useCurrentUser).mockReset();
  });

  it("shows a loading state while checking the session", () => {
    vi.mocked(useCurrentUser).mockReturnValue({
      isLoading: true,
      data: undefined,
    } as ReturnType<typeof useCurrentUser>);

    renderRoute();

    expect(screen.getByText("Loading...")).toBeInTheDocument();
    expect(screen.queryByText("Private content")).not.toBeInTheDocument();
  });

  it("redirects unauthenticated users to login", () => {
    vi.mocked(useCurrentUser).mockReturnValue({
      isLoading: false,
      data: null,
    } as ReturnType<typeof useCurrentUser>);

    renderRoute();

    expect(screen.getByText("Login page")).toBeInTheDocument();
    expect(screen.queryByText("Private content")).not.toBeInTheDocument();
  });

  it("renders protected pages for authenticated users", () => {
    vi.mocked(useCurrentUser).mockReturnValue({
      isLoading: false,
      data: { id: 1, email: "user@test.local", displayName: "User" },
    } as ReturnType<typeof useCurrentUser>);

    renderRoute();

    expect(screen.getByText("Private content")).toBeInTheDocument();
    expect(screen.queryByText("Login page")).not.toBeInTheDocument();
  });
});
