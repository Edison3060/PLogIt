import { describe, expect, it, vi } from "vitest";
import { fireEvent, render, screen } from "@testing-library/react";
import type { EngagementDetail } from "../../lib/engagements";
import OverviewPanel from "./OverviewPanel";
import ScopePanel from "./ScopePanel";
import TeamPanel from "./TeamPanel";
import AuditPanel from "./AuditPanel";

const engagement: EngagementDetail = {
  id: 12,
  name: "Assessment",
  description: "Authorized assessment",
  startDate: "2026-10-01",
  dueDate: "2026-10-20",
  status: "ACTIVE",
  role: "LEADER",
  leader: { id: 1, email: "lead@example.com", displayName: "Leader" },
  members: [
    { id: 1, email: "lead@example.com", displayName: "Leader", role: "LEADER" },
    { id: 2, email: "member@example.com", displayName: "Member", role: "MEMBER" },
  ],
  inScopeTargets: ["example.com"],
  allowedHours: "9-17",
  allowedTechniques: "Scanning",
  forbiddenTechniques: "Denial of service",
  emergencyContacts: "Security team",
  outOfScope: "Production payments",
  createdAt: "2026-10-01T00:00:00Z",
};

describe("engagement detail panels", () => {
  it("keeps overview navigation behind an explicit callback", () => {
    const onViewLogs = vi.fn();
    render(<OverviewPanel engagement={engagement} onViewLogs={onViewLogs} />);

    expect(screen.getByText("Authorized assessment")).toBeTruthy();
    fireEvent.click(screen.getByRole("button", { name: /Activity Logs/i }));
    expect(onViewLogs).toHaveBeenCalledOnce();
  });

  it("renders the saved rules of engagement", () => {
    render(<ScopePanel engagement={engagement} />);
    expect(screen.getByText("example.com")).toBeTruthy();
    expect(screen.getByText("Denial of service")).toBeTruthy();
    expect(screen.getByText("Production payments")).toBeTruthy();
  });

  it("keeps team transfer and removal actions wired to the page", () => {
    const onTransfer = vi.fn();
    const onRemove = vi.fn();
    render(
      <TeamPanel engagement={engagement} isLeader={true} showTransfer={true}
        onToggleTransfer={vi.fn()} onTransfer={onTransfer} onRemove={onRemove} />
    );

    fireEvent.click(screen.getByRole("button", { name: "Make leader" }));
    expect(onTransfer).toHaveBeenCalledWith(2);
    fireEvent.click(screen.getByRole("button", { name: "Remove Member" }));
    expect(onRemove).toHaveBeenCalledWith(2);
  });

  it("shows audit metadata without changing its presentation contract", () => {
    render(
      <AuditPanel auditLog={[{
        id: 3, actorId: 1, action: "TRANSFER_LEADERSHIP",
        targetType: "ENGAGEMENT", targetId: "12",
        timestamp: "2026-10-01T00:00:00Z",
        metadata: { previousLeaderId: 1, newLeaderId: 2 }
      }]} isLoading={false} />
    );

    expect(screen.getByText("Leader #1 → #2")).toBeTruthy();
  });
});
