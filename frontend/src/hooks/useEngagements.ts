import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  createEngagement,
  fetchEngagements,
  joinEngagement,
  generateJoinCode,
} from "../lib/engagements";

export function useEngagements() {
  return useQuery({
    queryKey: ["engagements"],
    queryFn: fetchEngagements,
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
