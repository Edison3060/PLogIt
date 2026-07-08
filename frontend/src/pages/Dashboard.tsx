import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCurrentUser } from "../hooks/useAuth";
import {
  useEngagements,
  useCreateEngagement,
  useJoinEngagement,
} from "../hooks/useEngagements";

const inputClass =
  "bg-bg-canvas border border-border-default rounded-lg px-3 py-2.5 text-text-strong focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-colors";

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
    <div className="max-w-7xl mx-auto p-6 md:p-8">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 mb-8">
        <div>
          <h1 className="text-3xl font-bold text-text-strong">
            Welcome, {user?.displayName ?? "Pentester"}
          </h1>
          <p className="text-text-muted text-sm mt-1">
            Manage your security engagements and active audits.
          </p>
        </div>
        <button
          onClick={() => setShowCreate(!showCreate)}
          className={`px-4 py-2.5 rounded-lg text-sm font-medium transition-colors flex items-center gap-2 ${
            showCreate
              ? "bg-bg-card border border-border-default text-text-body hover:bg-bg-inset"
              : "bg-primary text-white hover:bg-primary-hover"
          }`}
        >
          <i className={showCreate ? "fa-solid fa-xmark" : "fa-solid fa-plus"}></i>
          {showCreate ? "Cancel" : "New Engagement"}
        </button>
      </div>

      {showCreate && (
        <div className="bg-bg-card border border-border-default rounded-xl shadow-sm p-6 mb-6">
          <form onSubmit={handleCreate} className="flex flex-col gap-4">
            <h2 className="text-lg font-semibold text-text-strong flex items-center gap-2">
              <i className="fa-solid fa-folder-plus text-primary"></i>
              Create New Engagement
            </h2>
            <label className="flex flex-col gap-1.5">
              <span className="text-sm font-medium text-text-strong">Engagement name</span>
              <input
                type="text"
                value={newName}
                onChange={(e) => setNewName(e.target.value)}
                required
                maxLength={255}
                placeholder="e.g. Acme Corp Q3 Pentest"
                className={inputClass}
              />
            </label>
            <label className="flex flex-col gap-1.5">
              <span className="text-sm font-medium text-text-strong">Scope & description</span>
              <textarea
                value={newDesc}
                onChange={(e) => setNewDesc(e.target.value)}
                required
                placeholder="Define the scope, objectives, and target systems..."
                className={`${inputClass} min-h-[100px] resize-y`}
              />
            </label>
            <button
              type="submit"
              disabled={createEng.isPending}
              className="bg-primary text-white rounded-lg px-4 py-2.5 text-sm font-medium hover:bg-primary-hover disabled:opacity-50 flex items-center gap-2 self-start"
            >
              {createEng.isPending ? (
                <><i className="fa-solid fa-circle-notch fa-spin"></i> Creating...</>
              ) : (
                <><i className="fa-solid fa-check"></i> Create Engagement</>
              )}
            </button>
          </form>
        </div>
      )}

      <div className="bg-bg-card border border-border-default rounded-xl shadow-sm p-4 mb-6">
        <form onSubmit={handleJoin} className="flex flex-col md:flex-row gap-3 items-stretch md:items-center">
          <div className="flex items-center gap-2 text-text-strong font-medium text-sm shrink-0">
            <i className="fa-solid fa-ticket text-primary"></i>
            <span>Join with code</span>
          </div>
          <div className="relative flex-1">
            <input
              type="text"
              value={joinCode}
              onChange={(e) => setJoinCode(e.target.value)}
              placeholder="Enter join code (e.g. PL0G-XXXXXX)"
              className={`${inputClass} pl-3 font-mono text-sm w-full`}
            />
          </div>
          <button
            type="submit"
            disabled={joinEng.isPending || !joinCode}
            className="bg-bg-inset border border-border-default text-text-strong rounded-lg px-4 py-2.5 text-sm font-medium hover:bg-border-subtle disabled:opacity-50 flex items-center gap-2 justify-center"
          >
            {joinEng.isPending ? (
              <i className="fa-solid fa-circle-notch fa-spin"></i>
            ) : (
              <i className="fa-solid fa-arrow-right-to-bracket"></i>
            )}
            Join
          </button>
        </form>
      </div>

      {success && (
        <div className="bg-success-soft border border-success/30 text-success px-4 py-3 rounded-lg mb-6 flex items-center gap-2 text-sm font-medium">
          <i className="fa-solid fa-circle-check"></i>
          {success}
        </div>
      )}

      {error && (
        <div className="bg-danger-soft border border-danger/30 text-danger px-4 py-3 rounded-lg mb-6 flex items-center gap-2 text-sm font-medium">
          <i className="fa-solid fa-circle-exclamation"></i>
          {error}
        </div>
      )}

      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-text-strong flex items-center gap-2">
          <i className="fa-solid fa-folder text-text-muted"></i>
          Your engagements
          {engagements && engagements.length > 0 && (
            <span className="text-text-muted text-sm font-normal">({engagements.length})</span>
          )}
        </h2>
      </div>

      {isLoading ? (
        <div className="text-center py-16 text-text-muted">
          <i className="fa-solid fa-circle-notch fa-spin text-3xl mb-3"></i>
          <p>Loading engagements...</p>
        </div>
      ) : engagements && engagements.length > 0 ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {engagements.map((eng) => (
            <button
              key={eng.id}
              onClick={() => navigate(`/engagements/${eng.id}`)}
              className="bg-bg-card border border-border-default rounded-xl shadow-sm p-5 text-left hover:border-primary hover:shadow-md transition-all flex flex-col gap-4 group"
            >
              <div className="flex items-start justify-between">
                <div className="w-11 h-11 rounded-lg bg-primary-soft flex items-center justify-center">
                  <i className="fa-solid fa-folder-open text-primary text-lg"></i>
                </div>
                <span
                  className={`text-[10px] px-2 py-1 rounded font-bold uppercase tracking-wide ${
                    eng.status === "ACTIVE"
                      ? "bg-success-soft text-success"
                      : "bg-bg-inset text-text-muted"
                  }`}
                >
                  {eng.status}
                </span>
              </div>

              <div>
                <h3 className="font-bold text-text-strong text-base group-hover:text-primary transition-colors line-clamp-2">
                  {eng.name}
                </h3>
                <div className="flex flex-wrap items-center gap-3 mt-2 text-xs text-text-muted">
                  <span className="flex items-center gap-1">
                    <i className={eng.role === "LEADER" ? "fa-solid fa-crown text-warning" : "fa-solid fa-user"}></i>
                    {eng.role === "LEADER" ? "Leader" : "Member"}
                  </span>
                  {eng.dueDate && (
                    <span className="flex items-center gap-1">
                      <i className="fa-solid fa-calendar-day"></i>
                      {eng.dueDate}
                    </span>
                  )}
                </div>
              </div>

              <div className="flex items-center justify-end text-text-faint group-hover:text-primary transition-colors pt-2 border-t border-border-subtle">
                <span className="text-xs font-medium">Open</span>
                <i className="fa-solid fa-arrow-right ml-2"></i>
              </div>
            </button>
          ))}
        </div>
      ) : (
        <div className="bg-bg-card border border-dashed border-border-default rounded-xl p-12 text-center">
          <div className="w-16 h-16 mx-auto rounded-full bg-bg-inset flex items-center justify-center mb-4">
            <i className="fa-solid fa-folder-open text-text-faint text-2xl"></i>
          </div>
          <h3 className="text-lg font-bold text-text-strong mb-1">No engagements yet</h3>
          <p className="text-text-muted text-sm max-w-sm mx-auto">
            Create one above or join an existing engagement with a code.
          </p>
        </div>
      )}
    </div>
  );
}
