import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  fetchLogs,
  fetchLog,
  fetchLogHistory,
  createLog,
  updateLog,
  transitionLog,
  type LogFormData,
  type ReviewAction,
} from "../lib/logs";

type LogQueryParams = {
  authorId?: number;
  activityType?: string;
  outcome?: string;
  reviewState?: string;
  search?: string;
  page?: number;
  size?: number;
};

export function useLogs(engagementId: number, params: LogQueryParams = {}) {
  return useQuery({
    queryKey: ["logs", engagementId, params],
    queryFn: () => fetchLogs(engagementId, params),
    enabled: !!engagementId,
  });
}

export function useLog(logId: string | undefined) {
  return useQuery({
    queryKey: ["log", logId],
    queryFn: () => fetchLog(logId!),
    enabled: !!logId,
  });
}

export function useLogHistory(logId: string | undefined) {
  return useQuery({
    queryKey: ["log-history", logId],
    queryFn: () => fetchLogHistory(logId!),
    enabled: !!logId,
  });
}

export function useCreateLog(engagementId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: LogFormData) => createLog(engagementId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["logs", engagementId] });
    },
  });
}

export function useUpdateLog(engagementId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ logId, data }: { logId: string; data: Partial<LogFormData> }) =>
      updateLog(logId, data),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["logs", engagementId] });
      queryClient.invalidateQueries({ queryKey: ["log", variables.logId] });
    },
  });
}

export function useTransitionLog(engagementId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      logId,
      action,
      comment,
    }: {
      logId: string;
      action: ReviewAction;
      comment?: string;
    }) => transitionLog(logId, action, comment),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["logs", engagementId] });
      queryClient.invalidateQueries({ queryKey: ["log", variables.logId] });
    },
  });
}
