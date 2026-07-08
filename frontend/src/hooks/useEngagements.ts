import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  createEngagement,
  fetchEngagements,
  fetchAuditLog,
  joinEngagement,
  generateJoinCode,
} from "../lib/engagements";

export function useEngagements() {
  return useQuery({
    queryKey: ["engagements"],
    queryFn: fetchEngagements,
  });
}

export function useAuditLog(engagementId: number | undefined) {
  return useQuery({
    queryKey: ["audit", engagementId],
    queryFn: () => fetchAuditLog(engagementId!),
    enabled: !!engagementId,
  });
}

export function useCreateEngagement() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createEngagement,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["engagements"] }),
  });
}

export function useJoinEngagement() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: joinEngagement,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["engagements"] }),
  });
}

export function useGenerateJoinCode() {
  return useMutation({
    mutationFn: generateJoinCode,
  });
}
