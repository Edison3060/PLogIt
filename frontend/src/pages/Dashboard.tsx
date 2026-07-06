import { useCurrentUser } from "../hooks/useAuth";

export default function Dashboard() {
  const { data: user } = useCurrentUser();

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h1 className="text-2xl font-semibold text-text-strong mb-2">
        Welcome, {user?.displayName ?? "Pentester"}
      </h1>
      <p className="text-text-muted mb-6 text-sm">
        Your pentest engagements will appear here (Slice 4)
      </p>
      <div className="bg-bg-card rounded-lg border border-border-subtle p-8 text-center">
        <p className="text-text-muted">No engagements yet</p>
      </div>
    </div>
  );
}
