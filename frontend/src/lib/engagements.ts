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

export interface AuditLogEntry {
  id: number;
  actorId: number;
  action: string;
  targetType: string;
  targetId: string;
  metadata: {
    fromState?: string;
    toState?: string;
    engagementId?: number;
    comment?: string;
  };
  timestamp: string;
}

export async function fetchAuditLog(engagementId: number): Promise<AuditLogEntry[]> {
  return apiFetch<AuditLogEntry[]>(`/engagements/${engagementId}/audit`);
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

export type ExportFormat = "PDF" | "JSON" | "CSV";

export interface ExportResult {
  blob: Blob;
  filename: string;
}

export async function exportEngagement(
  engagementId: number,
  format: ExportFormat,
  includeExported: boolean
): Promise<ExportResult> {
  const csrfMatch = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };
  if (csrfMatch) {
    headers["X-XSRF-TOKEN"] = decodeURIComponent(csrfMatch[1]);
  }

  const res = await fetch(`/api/engagements/${engagementId}/export`, {
    method: "POST",
    credentials: "include",
    headers,
    body: JSON.stringify({ format, includeExported }),
  });

  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || `Export failed: ${res.status}`);
  }

  const blob = await res.blob();
  const disposition = res.headers.get("Content-Disposition") || "";
  const match = disposition.match(/filename="?([^";]+)"?/i);
  const filename = match ? match[1] : `engagement-${engagementId}-export`;
  return { blob, filename };
}
