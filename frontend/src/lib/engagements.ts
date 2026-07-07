import { apiFetch } from "./api";

export interface EngagementSummary {
  id: number;
  name: string;
  status: string;
  dueDate: string | null;
  role: string;
}

export interface EngagementDetail {
  id: number;
  name: string;
  description: string;
  startDate: string | null;
  dueDate: string | null;
  status: string;
  role: string;
  leader: { id: number; email: string; displayName: string };
  members: { id: number; email: string; displayName: string; role: string }[];
  inScopeTargets: string[] | null;
  allowedHours: string | null;
  allowedTechniques: string | null;
  forbiddenTechniques: string | null;
  emergencyContacts: string | null;
  outOfScope: string | null;
  createdAt: string;
}

export async function fetchEngagements(): Promise<EngagementSummary[]> {
  return apiFetch<EngagementSummary[]>("/engagements");
}

export async function fetchEngagement(id: number): Promise<EngagementDetail> {
  return apiFetch<EngagementDetail>(`/engagements/${id}`);
}

export async function createEngagement(data: {
  name: string;
  description: string;
  startDate?: string;
  dueDate?: string;
}): Promise<EngagementSummary> {
  return apiFetch<EngagementSummary>("/engagements", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateEngagement(
  id: number,
  data: Partial<EngagementDetail>
): Promise<EngagementDetail> {
  return apiFetch<EngagementDetail>(`/engagements/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export async function removeMember(
  engagementId: number,
  userId: number
): Promise<void> {
  return apiFetch<void>(`/engagements/${engagementId}/members/${userId}`, {
    method: "DELETE",
  });
}

export async function transferLeadership(
  engagementId: number,
  newLeaderId: number
): Promise<void> {
  return apiFetch<void>(`/engagements/${engagementId}/transfer-leadership`, {
    method: "POST",
    body: JSON.stringify({ newLeaderId }),
  });
}

export async function joinEngagement(code: string): Promise<EngagementSummary> {
  return apiFetch<EngagementSummary>("/join", {
    method: "POST",
    body: JSON.stringify({ code }),
  });
}

export async function generateJoinCode(engagementId: number): Promise<{ code: string; engagementId: number }> {
  return apiFetch<{ code: string; engagementId: number }>(`/engagements/${engagementId}/join-code`, {
    method: "POST",
  });
}
