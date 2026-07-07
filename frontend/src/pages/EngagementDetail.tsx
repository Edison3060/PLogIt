import { useParams, useNavigate } from "react-router-dom";
import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  fetchEngagement,
  updateEngagement,
  removeMember,
  transferLeadership,
  generateJoinCode,
} from "../lib/engagements";

type Tab = "overview" | "scope" | "team";

export default function EngagementDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [tab, setTab] = useState<Tab>("overview");
  const [editing, setEditing] = useState(false);
  const [copied, setCopied] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [showTransfer, setShowTransfer] = useState(false);

  const { data: engagement, isLoading } = useQuery({
    queryKey: ["engagement", id],
    queryFn: () => fetchEngagement(Number(id)),
    enabled: !!id,
  });

  const [editName, setEditName] = useState("");
  const [editDesc, setEditDesc] = useState("");
  const [editStartDate, setEditStartDate] = useState("");
  const [editDueDate, setEditDueDate] = useState("");
  const [editScope, setEditScope] = useState("");
  const [editAllowedHours, setEditAllowedHours] = useState("");
  const [editAllowedTech, setEditAllowedTech] = useState("");
  const [editForbiddenTech, setEditForbiddenTech] = useState("");
  const [editEmergency, setEditEmergency] = useState("");
  const [editOutOfScope, setEditOutOfScope] = useState("");

  const updateMut = useMutation({
    mutationFn: (data: Parameters<typeof updateEngagement>[1]) =>
      updateEngagement(Number(id), data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["engagement", id] });
      setEditing(false);
      setSuccess("Engagement updated");
      setTimeout(() => setSuccess(""), 3000);
    },
    onError: (err: Error) => setError(err.message),
  });

  const removeMut = useMutation({
    mutationFn: (userId: number) => removeMember(Number(id), userId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["engagement", id] });
      setSuccess("Member removed");
      setTimeout(() => setSuccess(""), 3000);
    },
    onError: (err: Error) => setError(err.message),
  });

  const transferMut = useMutation({
    mutationFn: (newLeaderId: number) => transferLeadership(Number(id), newLeaderId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["engagement", id] });
      setShowTransfer(false);
      setSuccess("Leadership transferred");
      setTimeout(() => setSuccess(""), 3000);
    },
    onError: (err: Error) => setError(err.message),
  });

  const handleGenerateCode = async () => {
    const result = await generateJoinCode(Number(id));
    navigator.clipboard.writeText(result.code);
    setCopied(true);
    setTimeout(() => setCopied(false), 3000);
    queryClient.invalidateQueries({ queryKey: ["engagement", id] });
  };

  const startEdit = () => {
    if (!engagement) return;
    setEditName(engagement.name);
    setEditDesc(engagement.description);
    setEditStartDate(engagement.startDate || "");
    setEditDueDate(engagement.dueDate || "");
    setEditScope((engagement.inScopeTargets || []).join("\n"));
    setEditAllowedHours(engagement.allowedHours || "");
    setEditAllowedTech(engagement.allowedTechniques || "");
    setEditForbiddenTech(engagement.forbiddenTechniques || "");
    setEditEmergency(engagement.emergencyContacts || "");
    setEditOutOfScope(engagement.outOfScope || "");
    setEditing(true);
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    updateMut.mutate({
      name: editName,
      description: editDesc,
      startDate: editStartDate || null,
      dueDate: editDueDate || null,
      inScopeTargets: editScope.split("\n").map((s) => s.trim()).filter(Boolean),
      allowedHours: editAllowedHours,
      allowedTechniques: editAllowedTech,
      forbiddenTechniques: editForbiddenTech,
      emergencyContacts: editEmergency,
      outOfScope: editOutOfScope,
    });
  };

  if (isLoading) {
    return (
      <div className="max-w-6xl mx-auto p-6 flex items-center gap-2 text-text-muted">
        <i className="fa-solid fa-circle-notch fa-spin"></i>
        <span>Loading...</span>
      </div>
    );
  }

  if (!engagement) {
    return (
      <div className="max-w-6xl mx-auto p-6">
        <p className="text-text-muted">Engagement not found</p>
      </div>
    );
  }

  const isLeader = engagement.role === "LEADER";

  return (
    <div className="max-w-6xl mx-auto p-6">
      <button
        onClick={() => navigate("/")}
        className="text-text-muted text-sm mb-4 hover:text-text-strong flex items-center gap-2"
      >
        <i className="fa-solid fa-arrow-left"></i> Back to dashboard
      </button>

      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold text-text-strong flex items-center gap-3">
            <i className="fa-solid fa-folder text-text-muted"></i>
            {engagement.name}
          </h1>
          <p className="text-text-muted text-sm flex items-center gap-2 mt-1">
            <i className={isLeader ? "fa-solid fa-crown text-amber-500" : "fa-solid fa-user"}></i>
            {isLeader ? "You are the leader" : "You are a member"}
          </p>
        </div>
        <div className="flex items-center gap-2">
          {isLeader && (
            <>
              <button
                onClick={handleGenerateCode}
                className="border border-border-default rounded px-3 py-2 text-sm hover:bg-bg-inset flex items-center gap-2"
              >
                <i className="fa-solid fa-ticket"></i>
                Join code
              </button>
              <button
                onClick={editing ? () => setEditing(false) : startEdit}
                className="bg-primary text-white rounded px-4 py-2 text-sm font-medium hover:bg-primary-hover flex items-center gap-2"
              >
                <i className={editing ? "fa-solid fa-xmark" : "fa-solid fa-pen"}></i>
                {editing ? "Cancel" : "Edit"}
              </button>
            </>
          )}
        </div>
      </div>

      {copied && (
        <div className="flex items-center gap-2 text-green-700 dark:text-green-400 text-sm bg-green-50 dark:bg-green-950/30 rounded p-2 mb-4">
          <i className="fa-solid fa-circle-check"></i>
          <span>Join code copied to clipboard!</span>
        </div>
      )}

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

      <div className="flex gap-1 mb-6 border-b border-border-subtle">
        {(["overview", "scope", "team"] as Tab[]).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`px-4 py-2 text-sm font-medium border-b-2 transition-colors capitalize ${
              tab === t
                ? "border-primary text-primary"
                : "border-transparent text-text-muted hover:text-text-strong"
            }`}
          >
            {t === "scope" ? "Scope & RoE" : t}
          </button>
        ))}
      </div>

      {editing ? (
        <form onSubmit={handleSave} className="flex flex-col gap-4">
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Name</span>
            <input type="text" value={editName} onChange={(e) => setEditName(e.target.value)} required maxLength={255}
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary" />
          </label>
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Description</span>
            <textarea value={editDesc} onChange={(e) => setEditDesc(e.target.value)} required
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary min-h-[80px]" />
          </label>
          <div className="grid grid-cols-2 gap-4">
            <label className="flex flex-col gap-1">
              <span className="text-sm text-text-strong">Start date</span>
              <input type="date" value={editStartDate} onChange={(e) => setEditStartDate(e.target.value)}
                className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary" />
            </label>
            <label className="flex flex-col gap-1">
              <span className="text-sm text-text-strong">Due date</span>
              <input type="date" value={editDueDate} onChange={(e) => setEditDueDate(e.target.value)}
                className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary" />
            </label>
          </div>
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">In-scope targets (one per line)</span>
            <textarea value={editScope} onChange={(e) => setEditScope(e.target.value)} placeholder={"10.0.0.0/24\nexample.com"}
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary min-h-[80px] font-mono text-sm" />
          </label>
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Allowed testing hours</span>
            <input type="text" value={editAllowedHours} onChange={(e) => setEditAllowedHours(e.target.value)} placeholder="09:00-17:00 ICT"
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary" />
          </label>
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Allowed techniques</span>
            <textarea value={editAllowedTech} onChange={(e) => setEditAllowedTech(e.target.value)}
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary min-h-[60px]" />
          </label>
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Forbidden techniques</span>
            <textarea value={editForbiddenTech} onChange={(e) => setEditForbiddenTech(e.target.value)} placeholder="No DoS, no social engineering"
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary min-h-[60px]" />
          </label>
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Emergency contacts</span>
            <textarea value={editEmergency} onChange={(e) => setEditEmergency(e.target.value)}
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary min-h-[60px]" />
          </label>
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Out of scope (explicit)</span>
            <textarea value={editOutOfScope} onChange={(e) => setEditOutOfScope(e.target.value)}
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary min-h-[60px]" />
          </label>
          <button type="submit" disabled={updateMut.isPending}
            className="bg-primary text-white rounded px-4 py-2 text-sm font-medium hover:bg-primary-hover disabled:opacity-50 flex items-center gap-2 self-start">
            {updateMut.isPending ? <><i className="fa-solid fa-circle-notch fa-spin"></i> Saving...</> : <><i className="fa-solid fa-check"></i> Save changes</>}
          </button>
        </form>
      ) : (
        <>
          {tab === "overview" && (
            <div className="flex flex-col gap-4">
              <div className="grid gap-4 md:grid-cols-2">
                {engagement.startDate && (
                  <div className="bg-bg-card rounded-lg border border-border-subtle p-4 flex items-center gap-3">
                    <i className="fa-solid fa-calendar-day text-primary"></i>
                    <div>
                      <p className="text-text-muted text-xs">Start date</p>
                      <p className="text-text-strong font-mono">{engagement.startDate}</p>
                    </div>
                  </div>
                )}
                {engagement.dueDate && (
                  <div className="bg-bg-card rounded-lg border border-border-subtle p-4 flex items-center gap-3">
                    <i className="fa-solid fa-calendar-xmark text-red-500"></i>
                    <div>
                      <p className="text-text-muted text-xs">Due date</p>
                      <p className="text-text-strong font-mono">{engagement.dueDate}</p>
                    </div>
                  </div>
                )}
              </div>
              <div className="bg-bg-card rounded-lg border border-border-subtle p-6">
                <h2 className="font-medium text-text-strong mb-2 flex items-center gap-2">
                  <i className="fa-solid fa-align-left text-text-muted"></i> Description
                </h2>
                <p className="text-text-body whitespace-pre-wrap">{engagement.description}</p>
              </div>
            </div>
          )}

          {tab === "scope" && (
            <div className="flex flex-col gap-4">
              <div className="bg-bg-card rounded-lg border border-border-subtle p-6">
                <h2 className="font-medium text-text-strong mb-3 flex items-center gap-2">
                  <i className="fa-solid fa-bullseye text-primary"></i> In-scope targets
                </h2>
                {engagement.inScopeTargets && engagement.inScopeTargets.length > 0 ? (
                  <ul className="font-mono text-sm text-text-body space-y-1">
                    {engagement.inScopeTargets.map((t, i) => (
                      <li key={i} className="flex items-center gap-2">
                        <i className="fa-solid fa-check text-green-500"></i> {t}
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-text-muted text-sm">No scope targets defined</p>
                )}
              </div>
              <div className="grid gap-4 md:grid-cols-2">
                <div className="bg-bg-card rounded-lg border border-border-subtle p-6">
                  <h3 className="font-medium text-text-strong mb-2 flex items-center gap-2 text-sm">
                    <i className="fa-solid fa-clock text-text-muted"></i> Allowed hours
                  </h3>
                  <p className="text-text-body text-sm">{engagement.allowedHours || "Not specified"}</p>
                </div>
                <div className="bg-bg-card rounded-lg border border-border-subtle p-6">
                  <h3 className="font-medium text-text-strong mb-2 flex items-center gap-2 text-sm">
                    <i className="fa-solid fa-check text-green-500"></i> Allowed techniques
                  </h3>
                  <p className="text-text-body text-sm whitespace-pre-wrap">{engagement.allowedTechniques || "Not specified"}</p>
                </div>
                <div className="bg-bg-card rounded-lg border border-border-subtle p-6">
                  <h3 className="font-medium text-text-strong mb-2 flex items-center gap-2 text-sm">
                    <i className="fa-solid fa-ban text-red-500"></i> Forbidden techniques
                  </h3>
                  <p className="text-text-body text-sm whitespace-pre-wrap">{engagement.forbiddenTechniques || "Not specified"}</p>
                </div>
                <div className="bg-bg-card rounded-lg border border-border-subtle p-6">
                  <h3 className="font-medium text-text-strong mb-2 flex items-center gap-2 text-sm">
                    <i className="fa-solid fa-phone text-text-muted"></i> Emergency contacts
                  </h3>
                  <p className="text-text-body text-sm whitespace-pre-wrap">{engagement.emergencyContacts || "Not specified"}</p>
                </div>
              </div>
              <div className="bg-bg-card rounded-lg border border-border-subtle p-6">
                <h3 className="font-medium text-text-strong mb-2 flex items-center gap-2 text-sm">
                  <i className="fa-solid fa-circle-exclamation text-amber-500"></i> Out of scope
                </h3>
                <p className="text-text-body text-sm whitespace-pre-wrap">{engagement.outOfScope || "Not specified"}</p>
              </div>
            </div>
          )}

          {tab === "team" && (
            <div className="bg-bg-card rounded-lg border border-border-subtle p-6">
              <div className="flex items-center justify-between mb-3">
                <h2 className="font-medium text-text-strong flex items-center gap-2">
                  <i className="fa-solid fa-users text-text-muted"></i> Team ({engagement.members.length})
                </h2>
                {isLeader && engagement.members.length > 1 && (
                  <button
                    onClick={() => setShowTransfer(!showTransfer)}
                    className="text-sm border border-border-default rounded px-3 py-1 hover:bg-bg-inset flex items-center gap-2"
                  >
                    <i className="fa-solid fa-exchange"></i> Transfer leadership
                  </button>
                )}
              </div>
              <ul className="divide-y divide-border-subtle">
                {engagement.members.map((m) => (
                  <li key={m.id} className="py-3 flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 rounded-full bg-primary-soft flex items-center justify-center">
                        <i className="fa-solid fa-user text-primary text-sm"></i>
                      </div>
                      <div>
                        <span className="text-text-strong">{m.displayName}</span>
                        <span className="text-text-muted text-sm ml-2">{m.email}</span>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className={`text-xs px-2 py-1 rounded font-mono flex items-center gap-1 ${
                        m.role === "LEADER" ? "bg-amber-100 dark:bg-amber-950/30 text-amber-700 dark:text-amber-400" : "bg-bg-inset text-text-muted"
                      }`}>
                        <i className={m.role === "LEADER" ? "fa-solid fa-crown" : "fa-solid fa-user"}></i>
                        {m.role}
                      </span>
                      {isLeader && m.role !== "LEADER" && (
                        <>
                          {showTransfer && (
                            <button
                              onClick={() => transferMut.mutate(m.id)}
                              className="text-xs border border-primary text-primary rounded px-2 py-1 hover:bg-primary-soft"
                            >
                              Make leader
                            </button>
                          )}
                          <button
                            onClick={() => removeMut.mutate(m.id)}
                            className="text-xs border border-red-300 text-red-600 rounded px-2 py-1 hover:bg-red-50 dark:hover:bg-red-950/30"
                          >
                            <i className="fa-solid fa-trash"></i>
                          </button>
                        </>
                      )}
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </>
      )}

      <p className="text-text-muted text-sm mt-6 flex items-center gap-2">
        <i className="fa-solid fa-info-circle"></i>
        Logs board coming in Slice 6
      </p>
    </div>
  );
}
