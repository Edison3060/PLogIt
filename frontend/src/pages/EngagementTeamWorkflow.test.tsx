import { afterEach, describe, expect, it, vi } from "vitest";
import { fireEvent, render, screen, waitFor, cleanup } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import EngagementDetailPage from "./EngagementDetail";
import type { EngagementDetail } from "../lib/engagements";
import * as api from "../lib/engagements";

vi.mock("../lib/engagements", () => ({
  fetchEngagement: vi.fn(), updateEngagement: vi.fn(), removeMember: vi.fn(),
  transferLeadership: vi.fn(), generateJoinCode: vi.fn(), exportEngagement: vi.fn(),
}));
vi.mock("../hooks/useEngagements", () => ({
  useAuditLog: () => ({ data: [], isLoading: false }),
}));

const initial: EngagementDetail = {
  id: 12, name: "Assessment", description: "Synthetic assessment", status: "ACTIVE", role: "LEADER",
  startDate: null, dueDate: null, createdAt: "2026-11-01T00:00:00Z",
  leader: { id: 1, displayName: "Old leader", email: "old@example.test" },
  members: [
    { id: 1, displayName: "Old leader", email: "old@example.test", role: "LEADER" },
    { id: 2, displayName: "New leader", email: "new@example.test", role: "MEMBER" },
  ],
  inScopeTargets: [], allowedHours: null, allowedTechniques: null, forbiddenTechniques: null,
  emergencyContacts: null, outOfScope: null,
};

afterEach(() => { cleanup(); vi.resetAllMocks(); });

describe("team workflow canonical role refresh", () => {
  it("refetches after transfer and removes old leader controls", async () => {
    const transferred: EngagementDetail = {
      ...initial, role: "MEMBER", leader: initial.members[1],
      members: initial.members.map(m => ({ ...m, role: m.id === 2 ? "LEADER" : "MEMBER" })),
    };
    vi.mocked(api.fetchEngagement).mockResolvedValueOnce(initial).mockResolvedValue(transferred);
    vi.mocked(api.transferLeadership).mockResolvedValue(undefined);
    const client = new QueryClient({ defaultOptions: { queries: { retry: false, gcTime: 0 } } });
    render(
      <QueryClientProvider client={client}>
        <MemoryRouter initialEntries={["/engagements/12"]}>
          <Routes><Route path="/engagements/:id" element={<EngagementDetailPage />} /></Routes>
        </MemoryRouter>
      </QueryClientProvider>
    );
    await screen.findByText("You are the leader");
    fireEvent.click(screen.getByRole("button", { name: /^Team/ }));
    fireEvent.click(screen.getByRole("button", { name: "Transfer leadership" }));
    fireEvent.click(screen.getByRole("button", { name: "Make leader" }));
    await waitFor(() => expect(api.transferLeadership).toHaveBeenCalledWith(12, 2));
    await screen.findByText("You are a member");
    expect(api.fetchEngagement).toHaveBeenCalledTimes(2);
    expect(screen.queryByRole("button", { name: "Transfer leadership" })).toBeNull();
    expect(screen.queryByRole("button", { name: "Make leader" })).toBeNull();
    expect(screen.queryByRole("button", { name: /^Remove/ })).toBeNull();
    client.clear();
  });
});
