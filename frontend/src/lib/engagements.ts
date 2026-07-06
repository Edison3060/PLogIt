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
