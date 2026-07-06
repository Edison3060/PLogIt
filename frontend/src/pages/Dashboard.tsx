import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCurrentUser } from "../hooks/useAuth";
import {
  useEngagements,
  useCreateEngagement,
  useJoinEngagement,
} from "../hooks/useEngagements";

export default function Dashboard() {
  const { data: user } = useCurrentUser();
  const { data: engagements, isLoading } = useEngagements();
  const createEng = useCreateEngagement();
  const joinEng = useJoinEngagement();
  const navigate = useNavigate();

  const [showCreate, setShowCreate] = useState(false);
  const [joinCode, setJoinCode] = useState("");
  const [error, setError] = useState("");

  const [newName, setNewName] = useState("");
  const [newDesc, setNewDesc] = useState("");

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    createEng.mutate(
      { name: newName, description: newDesc },
      {
        onSuccess: () => {
          setShowCreate(false);
          setNewName("");
          setNewDesc("");
        },
        onError: (err: Error) => setError(err.message),
      }
    );
  };

  const handleJoin = (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    joinEng.mutate(joinCode, {
      onSuccess: () => {
        setJoinCode("");
      },
      onError: (err: Error) => setError(err.message),
    });
  };

  return (
    <div className="max-w-6xl mx-auto p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold text-text-strong">
            Welcome, {user?.displayName ?? "Pentester"}
          </h1>
          <p className="text-text-muted text-sm">Your pentest engagements</p>
        </div>
        <button
          onClick={() => setShowCreate(!showCreate)}
          className="bg-primary text-white rounded px-4 py-2 text-sm font-medium hover:bg-primary-hover"
        >
          {showCreate ? "Cancel" : "New Engagement"}
        </button>
      </div>

      {showCreate && (
        <form
          onSubmit={handleCreate}
          className="bg-bg-card rounded-lg border border-border-subtle p-6 mb-6 flex flex-col gap-4"
        >
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Engagement name</span>
            <input
              type="text"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              required
              maxLength={255}
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary"
            />
          </label>
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Description</span>
            <textarea
              value={newDesc}
              onChange={(e) => setNewDesc(e.target.value)}
              required
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary min-h-[80px]"
            />
          </label>
          <button
            type="submit"
            disabled={createEng.isPending}
            className="bg-primary text-white rounded px-4 py-2 text-sm font-medium hover:bg-primary-hover disabled:opacity-50 self-start"
          >
            {createEng.isPending ? "Creating..." : "Create"}
          </button>
        </form>
      )}

      <form onSubmit={handleJoin} className="flex gap-2 mb-6">
        <input
          type="text"
          value={joinCode}
          onChange={(e) => setJoinCode(e.target.value)}
          placeholder="Enter join code (e.g. PL0G-XXXXXX)"
          className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary flex-1 font-mono text-sm"
        />
        <button
          type="submit"
          disabled={joinEng.isPending || !joinCode}
          className="bg-bg-card border border-border-default rounded px-4 py-2 text-sm font-medium text-text-strong hover:bg-bg-inset disabled:opacity-50"
        >
          {joinEng.isPending ? "Joining..." : "Join"}
        </button>
      </form>

      {error && (
        <p className="text-red-600 text-sm mb-4" role="alert">
          {error}
        </p>
      )}

      {isLoading ? (
        <p className="text-text-muted">Loading...</p>
      ) : engagements && engagements.length > 0 ? (
        <div className="grid gap-3">
          {engagements.map((eng) => (
            <button
              key={eng.id}
              onClick={() => navigate(`/engagements/${eng.id}`)}
              className="bg-bg-card rounded-lg border border-border-subtle p-4 text-left hover:border-primary transition-colors"
            >
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="font-medium text-text-strong">{eng.name}</h3>
                  <p className="text-text-muted text-sm">
                    {eng.role === "LEADER" ? "Leader" : "Member"}
                    {eng.dueDate && ` · Due ${eng.dueDate}`}
                  </p>
                </div>
                <span
                  className={`text-xs px-2 py-1 rounded font-mono ${
                    eng.status === "ACTIVE"
                      ? "bg-primary-soft text-primary"
                      : "bg-bg-inset text-text-muted"
                  }`}
                >
                  {eng.status}
                </span>
              </div>
            </button>
          ))}
        </div>
      ) : (
        <div className="bg-bg-card rounded-lg border border-border-subtle p-8 text-center">
          <p className="text-text-muted">No engagements yet</p>
          <p className="text-text-muted text-sm mt-1">
            Create one above or join with a code
          </p>
        </div>
      )}
    </div>
  );
}
