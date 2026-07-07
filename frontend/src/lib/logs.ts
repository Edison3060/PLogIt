import { apiFetch } from "./api";

export interface LogSummary {
  id: string;
  createdAt: string;
  authorDisplayName: string;
  title: string;
  activityType: string;
  target: string | null;
  outcome: string;
  reviewState: string;
}

export interface LogDetail {
  id: string;
  engagementId: number;
  leaderId: number;
  authorId: number;
  authorDisplayName: string;
  activityType: string;
  title: string;
  description: string;
  result: string;
  target: string | null;
  toolUsed: string | null;
  outcome: string;
  tags: string[] | null;
  codeBlock: string | null;
  codeLanguage: string | null;
  reviewState: string;
  rejectionComment: string | null;
  createdAt: string;
  lastEditedAt: string;
  lastEditedById: number | null;
}

export interface LogListResponse {
  items: LogSummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface LogFormData {
  activityType: string;
  title: string;
  description: string;
  result: string;
  target: string;
  toolUsed: string;
  outcome: string;
  tags: string[];
  codeBlock: string;
  codeLanguage: string;
}

export const ACTIVITY_TYPES = [
  "OSINT",
  "RECONNAISSANCE",
  "PORT_SCANNING",
  "SERVICE_ENUMERATION",
  "WEB_APPLICATION_TESTING",
  "VULNERABILITY_SCANNING",
  "EXPLOITATION",
  "POST_EXPLOITATION",
  "PRIVILEGE_ESCALATION",
  "LATERAL_MOVEMENT",
  "SOCIAL_ENGINEERING",
  "PHYSICAL_ACCESS",
  "WIRELESS_TESTING",
  "MANUAL_TESTING",
  "OTHER",
];

export const OUTCOMES = ["IN_PROGRESS", "DEAD_END", "CONFIRMED"];

export const REVIEW_STATES = ["DRAFT", "SUBMITTED", "APPROVED", "EXPORTED"];

export function formatActivityType(type: string): string {
  return type
    .split("_")
    .map((w) => w.charAt(0) + w.slice(1).toLowerCase())
    .join(" ");
}

export function formatEnum(value: string): string {
  return value
    .split("_")
    .map((w) => w.charAt(0) + w.slice(1).toLowerCase())
    .join(" ");
}

export function outcomeBadgeClass(outcome: string): string {
  switch (outcome) {
    case "CONFIRMED":
      return "bg-green-100 dark:bg-green-950/30 text-green-700 dark:text-green-400";
    case "IN_PROGRESS":
      return "bg-blue-100 dark:bg-blue-950/30 text-blue-700 dark:text-blue-400";
    case "DEAD_END":
      return "bg-bg-inset text-text-muted";
    default:
      return "bg-bg-inset text-text-muted";
  }
}

export function reviewStateBadgeClass(state: string): string {
  switch (state) {
    case "DRAFT":
      return "bg-bg-inset text-text-muted";
    case "SUBMITTED":
      return "bg-amber-100 dark:bg-amber-950/30 text-amber-700 dark:text-amber-400";
    case "APPROVED":
      return "bg-green-100 dark:bg-green-950/30 text-green-700 dark:text-green-400";
    case "EXPORTED":
      return "bg-purple-100 dark:bg-purple-950/30 text-purple-700 dark:text-purple-400";
    default:
      return "bg-bg-inset text-text-muted";
  }
}

export async function fetchLogs(
  engagementId: number,
  params: {
    authorId?: number;
    activityType?: string;
    outcome?: string;
    reviewState?: string;
    search?: string;
    page?: number;
    size?: number;
  } = {}
): Promise<LogListResponse> {
  const searchParams = new URLSearchParams();
  if (params.authorId != null) searchParams.set("authorId", String(params.authorId));
  if (params.activityType) searchParams.set("activityType", params.activityType);
  if (params.outcome) searchParams.set("outcome", params.outcome);
  if (params.reviewState) searchParams.set("reviewState", params.reviewState);
  if (params.search) searchParams.set("search", params.search);
  searchParams.set("page", String(params.page ?? 0));
  searchParams.set("size", String(params.size ?? 20));

  return apiFetch<LogListResponse>(
    `/engagements/${engagementId}/logs?${searchParams.toString()}`
  );
}

export async function fetchLog(logId: string): Promise<LogDetail> {
  return apiFetch<LogDetail>(`/logs/${logId}`);
}

export async function createLog(
  engagementId: number,
  data: LogFormData
): Promise<LogDetail> {
  return apiFetch<LogDetail>(`/engagements/${engagementId}/logs`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateLog(
  logId: string,
  data: Partial<LogFormData>
): Promise<LogDetail> {
  return apiFetch<LogDetail>(`/logs/${logId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export type ReviewAction = "SUBMIT" | "APPROVE" | "REJECT";

export async function transitionLog(
  logId: string,
  action: ReviewAction,
  comment?: string
): Promise<LogDetail> {
  const body: { action: ReviewAction; comment?: string } = { action };
  if (comment) {
    body.comment = comment;
  }
  return apiFetch<LogDetail>(`/logs/${logId}/transition`, {
    method: "POST",
    body: JSON.stringify(body),
  });
}
