import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { cleanup, fireEvent, render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { afterEach, describe, expect, it, vi } from "vitest";
import * as api from "../lib/engagements";
import Dashboard from "./Dashboard";
import EngagementsPage from "./EngagementsPage";

vi.mock("../hooks/useAuth", () => ({ useCurrentUser: () => ({ data: null }) }));
vi.mock("../lib/engagements", () => ({
  fetchEngagements: vi.fn(),
  createEngagement: vi.fn(),
  joinEngagement: vi.fn(),
}));

function renderPage(page: React.ReactElement) {
  const client = new QueryClient({ defaultOptions: { queries: { retry: false, gcTime: 0 } } });
  render(
    <QueryClientProvider client={client}>
      <MemoryRouter>{page}</MemoryRouter>
    </QueryClientProvider>,
  );
}

afterEach(() => {
  cleanup();
  vi.resetAllMocks();
});

describe("engagement list and join feedback", () => {
  it.each([Dashboard, EngagementsPage])("announces a failed load on %p", async (Page) => {
    vi.mocked(api.fetchEngagements).mockRejectedValue(new Error("Request failed: 403"));
    renderPage(<Page />);

    expect(screen.getByRole("status")).toHaveTextContent("Loading engagements");
    expect(await screen.findByText("Unable to load engagements")).toHaveAttribute("role", "alert");
    expect(screen.queryByText("No engagements yet")).not.toBeInTheDocument();
  });

  it("announces a rejected join while retaining a legitimate empty list", async () => {
    vi.mocked(api.fetchEngagements).mockResolvedValue([]);
    vi.mocked(api.joinEngagement).mockRejectedValue(new Error("Invalid join code"));
    renderPage(<EngagementsPage />);

    await screen.findByText("No engagements yet");
    fireEvent.change(screen.getByLabelText("Join code"), { target: { value: "PL0G-AAAAAA" } });
    fireEvent.click(screen.getByRole("button", { name: "Join" }));

    expect((await screen.findByText("Invalid join code")).closest('[role="alert"]')).toBeInTheDocument();
    expect(screen.queryByText("Joined engagement")).not.toBeInTheDocument();
    expect(screen.getByText("No engagements yet")).toBeInTheDocument();
  });
});
