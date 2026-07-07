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
  const [success, setSuccess] = useState("");

  const [newName, setNewName] = useState("");
  const [newDesc, setNewDesc] = useState("");

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    createEng.mutate(
      { name: newName, description: newDesc },
      {
        onSuccess: () => {
          setShowCreate(false);
          setNewName("");
          setNewDesc("");
          setSuccess("Engagement created");
          setTimeout(() => setSuccess(""), 3000);
        },
        onError: (err: Error) => setError(err.message),
      }
    );
  };

  const handleJoin = (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    joinEng.mutate(joinCode, {
      onSuccess: () => {
        setJoinCode("");
        setSuccess("Joined engagement");
        setTimeout(() => setSuccess(""), 3000);
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
          className="bg-primary text-white rounded px-4 py-2 text-sm font-medium hover:bg-primary-hover flex items-center gap-2"
        >
          <i className={showCreate ? "fa-solid fa-xmark" : "fa-solid fa-plus"}></i>
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
              placeholder="e.g. Acme Corp Q3 Pentest"
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary"
            />
          </label>
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Description</span>
            <textarea
              value={newDesc}
              onChange={(e) => setNewDesc(e.target.value)}
              required
              placeholder="Scope, objectives, target system..."
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary min-h-[80px]"
            />
          </label>
          <button
            type="submit"
            disabled={createEng.isPending}
            className="bg-primary text-white rounded px-4 py-2 text-sm font-medium hover:bg-primary-hover disabled:opacity-50 self-start flex items-center gap-2"
          >
            {createEng.isPending ? (
              <>
                <i className="fa-solid fa-circle-notch fa-spin"></i> Creating...
              </>
            ) : (
              <>
                <i className="fa-solid fa-check"></i> Create
              </>
            )}
          </button>
        </form>
      )}

      <form onSubmit={handleJoin} className="flex gap-2 mb-4">
        <div className="relative flex-1">
          <i className="fa-solid fa-ticket absolute left-3 top-1/2 -translate-y-1/2 text-text-faint text-sm"></i>
          <input
            type="text"
            value={joinCode}
            onChange={(e) => setJoinCode(e.target.value)}
            placeholder="Enter join code (e.g. PL0G-XXXXXX)"
            className="bg-bg-canvas border border-border-default rounded pl-9 pr-3 py-2 w-full text-text-strong focus:outline-none focus:border-primary font-mono text-sm"
          />
        </div>
        <button
          type="submit"
          disabled={joinEng.isPending || !joinCode}
          className="bg-bg-card border border-border-default rounded px-4 py-2 text-sm font-medium text-text-strong hover:bg-bg-inset disabled:opacity-50 flex items-center gap-2"
        >
          {joinEng.isPending ? (
            <i className="fa-solid fa-circle-notch fa-spin"></i>
          ) : (
            <i className="fa-solid fa-arrow-right-to-bracket"></i>
          )}
          Join
        </button>
      </form>

      {success && (
        <div className="flex items-center gap-2 text-green-700 dark:text-green-400 text-sm bg-green-50 dark:bg-green-950/30 rounded p-2 mb-4">
          <i className="fa-solid fa-circle-check"></i>
          <span>{success}</span>
        </div>
      )}

      {error && (
        <div className="flex items-center gap-2 text-red-600 text-sm bg-red-50 dark:bg-red-950/30 rounded p-2 mb-4">
          <i className="fa-solid fa-circle-exclamation"></i>
          <span>{error}</span>
        </div>
      )}

      {isLoading ? (
        <div className="flex items-center gap-2 text-text-muted">
          <i className="fa-solid fa-circle-notch fa-spin"></i>
          <span>Loading engagements...</span>
        </div>
      ) : engagements && engagements.length > 0 ? (
        <div className="grid gap-3">
          {engagements.map((eng) => (
            <button
              key={eng.id}
              onClick={() => navigate(`/engagements/${eng.id}`)}
              className="bg-bg-card rounded-lg border border-border-subtle p-4 text-left hover:border-primary transition-colors flex items-center gap-4"
            >
              <i className="fa-solid fa-folder text-text-muted text-xl"></i>
              <div className="flex-1">
                <h3 className="font-medium text-text-strong">{eng.name}</h3>
                <p className="text-text-muted text-sm flex items-center gap-2">
                  <i
                    className={
                      eng.role === "LEADER"
                        ? "fa-solid fa-crown text-amber-500"
                        : "fa-solid fa-user"
                    }
                  ></i>
                  {eng.role === "LEADER" ? "Leader" : "Member"}
                  {eng.dueDate && (
                    <>
                      <span className="text-text-faint">·</span>
                      <i className="fa-solid fa-calendar"></i>
                      {eng.dueDate}
                    </>
                  )}
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
              <i className="fa-solid fa-chevron-right text-text-faint"></i>
            </button>
          ))}
        </div>
      ) : (
        <div className="bg-bg-card rounded-lg border border-border-subtle p-12 text-center">
          <i className="fa-solid fa-folder-open text-text-faint text-3xl mb-3"></i>
          <p className="text-text-muted">No engagements yet</p>
          <p className="text-text-muted text-sm mt-1">
            Create one above or join with a code
          </p>
        </div>
      )}
    </div>
  );
}
