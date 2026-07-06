import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  fetchCurrentUser,
  loginUser,
  registerUser,
  logoutUser,
  type User,
} from "../lib/auth";

export function useCurrentUser() {
  return useQuery<User | null>({
    queryKey: ["currentUser"],
    queryFn: fetchCurrentUser,
    retry: false,
  });
}

export function useLogin() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ email, password }: { email: string; password: string }) =>
      loginUser(email, password),
    onSuccess: (user) => {
      queryClient.setQueryData(["currentUser"], user);
    },
  });
}

export function useRegister() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      email,
      password,
      displayName,
    }: {
      email: string;
      password: string;
      displayName: string;
    }) => registerUser(email, password, displayName),
    onSuccess: (user) => {
      queryClient.setQueryData(["currentUser"], user);
    },
  });
}

export function useLogout() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => logoutUser(),
    onSettled: () => {
      queryClient.setQueryData(["currentUser"], null);
      queryClient.clear();
    },
  });
}
