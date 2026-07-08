import { useParams, useNavigate } from "react-router-dom";
import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  fetchEngagement,
  updateEngagement,
  removeMember,
  transferLeadership,
  generateJoinCode,
  exportEngagement,
  type ExportFormat,
} from "../lib/engagements";
import { useAuditLog } from "../hooks/useEngagements";
import { formatEnum } from "../lib/logs";

type Tab = "overview" | "scope" | "team" | "audit";

const inputClass =
  "bg-bg-canvas border border-border-default rounded-lg px-3 py-2.5 text-text-strong focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-colors";

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
  const [showExport, setShowExport] = useState(false);
  const [exportFormat, setExportFormat] = useState<ExportFormat>("PDF");
  const [includeExported, setIncludeExported] = useState(false);
  const [exporting, setExporting] = useState(false);

  const { data: engagement, isLoading } = useQuery({
    queryKey: ["engagement", id],
    queryFn: () => fetchEngagement(Number(id)),
    enabled: !!id,
  });

  const engagementId = engagement?.id;
  const isLeaderDerived = engagement?.role === "LEADER";
  const { data: auditLog, isLoading: auditLoading } = useAuditLog(
    isLeaderDerived ? engagementId : undefined
  );

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

  const handleExport = async () => {
    setError("");
    setExporting(true);
    try {
      const { blob, filename } = await exportEngagement(
        Number(id),
        exportFormat,
        includeExported
      );
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
      setShowExport(false);
      setSuccess(`Exported ${exportFormat} (${filename})`);
      setTimeout(() => setSuccess(""), 4000);
      queryClient.invalidateQueries({ queryKey: ["engagement", id] });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Export failed");
    } finally {
      setExporting(false);
    }
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
      <div className="max-w-6xl mx-auto p-8 text-center text-text-muted">
        <i className="fa-solid fa-circle-notch fa-spin text-3xl mb-3"></i>
        <p>Loading...</p>
      </div>
    );
  }

  if (!engagement) {
    return (
      <div className="max-w-6xl mx-auto p-8">
        <div className="bg-bg-card border border-dashed border-border-default rounded-xl p-12 text-center">
          <i className="fa-solid fa-folder-open text-text-faint text-3xl mb-3"></i>
          <p className="text-text-muted">Engagement not found</p>
        </div>
      </div>
    );
  }

  const isLeader = engagement.role === "LEADER";
  const tabs: { id: Tab; label: string; leaderOnly?: boolean }[] = [
    { id: "overview", label: "Overview" },
    { id: "scope", label: "Scope & RoE" },
    { id: "team", label: "Team" },
    { id: "audit", label: "Audit", leaderOnly: true },
  ];
  const visibleTabs = tabs.filter((t) => !t.leaderOnly || isLeader);

  return (
    <div className="max-w-6xl mx-auto p-6 md:p-8">
      <div className="flex flex-col md:flex-row md:items-start justify-between gap-4 mb-6">
        <div className="min-w-0">
          <h1 className="text-2xl font-bold text-text-strong flex items-center gap-3">
            <div className="w-10 h-10 rounded-lg bg-primary-soft flex items-center justify-center shrink-0">
              <i className="fa-solid fa-folder text-primary"></i>
            </div>
            <span className="truncate">{engagement.name}</span>
          </h1>
          <p className="text-text-muted text-sm flex items-center gap-2 mt-2 ml-13">
            <i className={isLeader ? "fa-solid fa-crown text-warning" : "fa-solid fa-user"}></i>
            {isLeader ? "You are the leader" : "You are a member"}
          </p>
        </div>
        <div className="flex items-center gap-2 shrink-0">
          {isLeader && (
            <>
              <button
                onClick={handleGenerateCode}
                className="bg-bg-card border border-border-default rounded-lg px-3 py-2 text-sm hover:bg-bg-inset flex items-center gap-2 transition-colors"
              >
                <i className="fa-solid fa-ticket text-primary"></i>
                Join code
              </button>
              <button
                onClick={() => setShowExport(true)}
                className="bg-bg-card border border-border-default rounded-lg px-3 py-2 text-sm hover:bg-bg-inset flex items-center gap-2 transition-colors"
              >
                <i className="fa-solid fa-file-export text-success"></i>
                Export
              </button>
              <button
                onClick={editing ? () => setEditing(false) : startEdit}
                className="bg-primary text-white rounded-lg px-4 py-2 text-sm font-medium hover:bg-primary-hover flex items-center gap-2 transition-colors"
              >
                <i className={editing ? "fa-solid fa-xmark" : "fa-solid fa-pen"}></i>
                {editing ? "Cancel" : "Edit"}
              </button>
            </>
          )}
        </div>
      </div>

      {copied && (
        <div className="flex items-center gap-2 text-success bg-success-soft border border-success/30 rounded-lg p-3 mb-4 text-sm font-medium">
          <i className="fa-solid fa-circle-check"></i>
          <span>Join code copied to clipboard!</span>
        </div>
      )}

      {success && (
        <div className="flex items-center gap-2 text-success bg-success-soft border border-success/30 rounded-lg p-3 mb-4 text-sm font-medium">
          <i className="fa-solid fa-circle-check"></i>
          <span>{success}</span>
        </div>
      )}

      {error && (
        <div className="flex items-center gap-2 text-danger bg-danger-soft border border-danger/30 rounded-lg p-3 mb-4 text-sm font-medium">
          <i className="fa-solid fa-circle-exclamation"></i>
          <span>{error}</span>
        </div>
      )}

      <div className="flex gap-1 mb-6 border-b border-border-default overflow-x-auto">
        {visibleTabs.map((t) => (
          <button
            key={t.id}
            onClick={() => setTab(t.id)}
            className={`px-4 py-2.5 text-sm font-medium border-b-2 transition-colors whitespace-nowrap ${
              tab === t.id
                ? "border-primary text-primary"
                : "border-transparent text-text-muted hover:text-text-strong"
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {editing ? (
        <form onSubmit={handleSave} className="bg-bg-card border border-border-default rounded-xl shadow-sm p-6 flex flex-col gap-4">
          <label className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">Name</span>
            <input type="text" value={editName} onChange={(e) => setEditName(e.target.value)} required maxLength={255} className={inputClass} />
          </label>
          <label className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">Description</span>
            <textarea value={editDesc} onChange={(e) => setEditDesc(e.target.value)} required className={`${inputClass} min-h-[80px] resize-y`} />
          </label>
          <div className="grid grid-cols-2 gap-4">
            <label className="flex flex-col gap-1.5">
              <span className="text-sm font-medium text-text-strong">Start date</span>
              <input type="date" value={editStartDate} onChange={(e) => setEditStartDate(e.target.value)} className={inputClass} />
            </label>
            <label className="flex flex-col gap-1.5">
              <span className="text-sm font-medium text-text-strong">Due date</span>
              <input type="date" value={editDueDate} onChange={(e) => setEditDueDate(e.target.value)} className={inputClass} />
            </label>
          </div>
          <label className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">In-scope targets (one per line)</span>
            <textarea value={editScope} onChange={(e) => setEditScope(e.target.value)} placeholder={"10.0.0.0/24\nexample.com"} className={`${inputClass} min-h-[80px] font-mono text-sm resize-y`} />
          </label>
          <label className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">Allowed testing hours</span>
            <input type="text" value={editAllowedHours} onChange={(e) => setEditAllowedHours(e.target.value)} placeholder="09:00-17:00 ICT" className={inputClass} />
          </label>
          <label className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">Allowed techniques</span>
            <textarea value={editAllowedTech} onChange={(e) => setEditAllowedTech(e.target.value)} className={`${inputClass} min-h-[60px] resize-y`} />
          </label>
          <label className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">Forbidden techniques</span>
            <textarea value={editForbiddenTech} onChange={(e) => setEditForbiddenTech(e.target.value)} placeholder="No DoS, no social engineering" className={`${inputClass} min-h-[60px] resize-y`} />
          </label>
          <label className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">Emergency contacts</span>
            <textarea value={editEmergency} onChange={(e) => setEditEmergency(e.target.value)} className={`${inputClass} min-h-[60px] resize-y`} />
          </label>
          <label className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">Out of scope (explicit)</span>
            <textarea value={editOutOfScope} onChange={(e) => setEditOutOfScope(e.target.value)} className={`${inputClass} min-h-[60px] resize-y`} />
          </label>
          <button type="submit" disabled={updateMut.isPending}
            className="bg-primary text-white rounded-lg px-4 py-2.5 text-sm font-medium hover:bg-primary-hover disabled:opacity-50 flex items-center gap-2 self-start transition-colors">
            {updateMut.isPending ? <><i className="fa-solid fa-circle-notch fa-spin"></i> Saving...</> : <><i className="fa-solid fa-check"></i> Save changes</>}
          </button>
        </form>
      ) : (
        <>
          {tab === "overview" && (
            <div className="flex flex-col gap-4">
              <div className="grid gap-4 md:grid-cols-2">
                {engagement.startDate && (
                  <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-4 flex items-center gap-3">
                    <div className="w-10 h-10 rounded-lg bg-primary-soft flex items-center justify-center">
                      <i className="fa-solid fa-calendar-day text-primary"></i>
                    </div>
                    <div>
                      <p className="text-text-muted text-xs">Start date</p>
                      <p className="text-text-strong font-mono">{engagement.startDate}</p>
                    </div>
                  </div>
                )}
                {engagement.dueDate && (
                  <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-4 flex items-center gap-3">
                    <div className="w-10 h-10 rounded-lg bg-danger-soft flex items-center justify-center">
                      <i className="fa-solid fa-calendar-xmark text-danger"></i>
                    </div>
                    <div>
                      <p className="text-text-muted text-xs">Due date</p>
                      <p className="text-text-strong font-mono">{engagement.dueDate}</p>
                    </div>
                  </div>
                )}
              </div>
              <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6">
                <h2 className="font-semibold text-text-strong mb-2 flex items-center gap-2">
                  <i className="fa-solid fa-align-left text-primary"></i> Description
                </h2>
                <p className="text-text-body whitespace-pre-wrap">{engagement.description}</p>
              </div>
              <button
                onClick={() => navigate(`/engagements/${engagement.id}/logs`)}
                className="bg-bg-card rounded-xl border border-border-default shadow-sm p-5 text-left hover:border-primary hover:shadow-md transition-all flex items-center gap-4 group"
              >
                <div className="w-12 h-12 rounded-lg bg-primary-soft flex items-center justify-center">
                  <i className="fa-solid fa-list-check text-primary text-xl"></i>
                </div>
                <div className="flex-1">
                  <h3 className="font-semibold text-text-strong group-hover:text-primary transition-colors">Activity Logs</h3>
                  <p className="text-text-muted text-sm">View and manage engagement logs</p>
                </div>
                <i className="fa-solid fa-chevron-right text-text-faint group-hover:text-primary transition-colors"></i>
              </button>
            </div>
          )}

          {tab === "scope" && (
            <div className="flex flex-col gap-4">
              <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6">
                <h2 className="font-semibold text-text-strong mb-3 flex items-center gap-2">
                  <i className="fa-solid fa-bullseye text-primary"></i> In-scope targets
                </h2>
                {engagement.inScopeTargets && engagement.inScopeTargets.length > 0 ? (
                  <ul className="font-mono text-sm text-text-body space-y-2">
                    {engagement.inScopeTargets.map((t, i) => (
                      <li key={i} className="flex items-center gap-2 bg-bg-inset rounded-lg px-3 py-1.5">
                        <i className="fa-solid fa-check text-success"></i> {t}
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-text-muted text-sm">No scope targets defined</p>
                )}
              </div>
              <div className="grid gap-4 md:grid-cols-2">
                <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6">
                  <h3 className="font-semibold text-text-strong mb-2 flex items-center gap-2 text-sm">
                    <i className="fa-solid fa-clock text-text-muted"></i> Allowed hours
                  </h3>
                  <p className="text-text-body text-sm">{engagement.allowedHours || "Not specified"}</p>
                </div>
                <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6">
                  <h3 className="font-semibold text-text-strong mb-2 flex items-center gap-2 text-sm">
                    <i className="fa-solid fa-check text-success"></i> Allowed techniques
                  </h3>
                  <p className="text-text-body text-sm whitespace-pre-wrap">{engagement.allowedTechniques || "Not specified"}</p>
                </div>
                <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6">
                  <h3 className="font-semibold text-text-strong mb-2 flex items-center gap-2 text-sm">
                    <i className="fa-solid fa-ban text-danger"></i> Forbidden techniques
                  </h3>
                  <p className="text-text-body text-sm whitespace-pre-wrap">{engagement.forbiddenTechniques || "Not specified"}</p>
                </div>
                <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6">
                  <h3 className="font-semibold text-text-strong mb-2 flex items-center gap-2 text-sm">
                    <i className="fa-solid fa-phone text-text-muted"></i> Emergency contacts
                  </h3>
                  <p className="text-text-body text-sm whitespace-pre-wrap">{engagement.emergencyContacts || "Not specified"}</p>
                </div>
              </div>
              <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6">
                <h3 className="font-semibold text-text-strong mb-2 flex items-center gap-2 text-sm">
                  <i className="fa-solid fa-circle-exclamation text-warning"></i> Out of scope
                </h3>
                <p className="text-text-body text-sm whitespace-pre-wrap">{engagement.outOfScope || "Not specified"}</p>
              </div>
            </div>
          )}

          {tab === "team" && (
            <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6">
              <div className="flex items-center justify-between mb-4">
                <h2 className="font-semibold text-text-strong flex items-center gap-2">
                  <i className="fa-solid fa-users text-text-muted"></i> Team ({engagement.members.length})
                </h2>
                {isLeader && engagement.members.length > 1 && (
                  <button
                    onClick={() => setShowTransfer(!showTransfer)}
                    className="text-sm bg-bg-inset border border-border-default rounded-lg px-3 py-1.5 hover:bg-border-subtle flex items-center gap-2 transition-colors"
                  >
                    <i className="fa-solid fa-exchange"></i> Transfer leadership
                  </button>
                )}
              </div>
              <ul className="divide-y divide-border-subtle">
                {engagement.members.map((m) => (
                  <li key={m.id} className="py-3 flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="w-9 h-9 rounded-full bg-primary-soft flex items-center justify-center">
                        <i className="fa-solid fa-user text-primary text-sm"></i>
                      </div>
                      <div>
                        <span className="text-text-strong font-medium">{m.displayName}</span>
                        <span className="text-text-muted text-sm ml-2">{m.email}</span>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className={`text-[10px] px-2 py-1 rounded font-bold uppercase tracking-wide flex items-center gap-1 ${
                        m.role === "LEADER" ? "bg-warning-soft text-warning" : "bg-bg-inset text-text-muted"
                      }`}>
                        <i className={m.role === "LEADER" ? "fa-solid fa-crown" : "fa-solid fa-user"}></i>
                        {m.role}
                      </span>
                      {isLeader && m.role !== "LEADER" && (
                        <>
                          {showTransfer && (
                            <button
                              onClick={() => transferMut.mutate(m.id)}
                              className="text-xs border border-primary text-primary rounded-lg px-2 py-1 hover:bg-primary-soft transition-colors"
                            >
                              Make leader
                            </button>
                          )}
                          <button
                            onClick={() => removeMut.mutate(m.id)}
                            className="text-xs border border-danger/40 text-danger rounded-lg px-2 py-1 hover:bg-danger-soft transition-colors"
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

          {tab === "audit" && isLeader && (
            <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6">
              <h2 className="font-semibold text-text-strong mb-4 flex items-center gap-2">
                <i className="fa-solid fa-shield-halved text-text-muted"></i> Audit log
              </h2>
              {auditLoading ? (
                <div className="flex items-center gap-2 text-text-muted text-sm">
                  <i className="fa-solid fa-circle-notch fa-spin"></i> Loading...
                </div>
              ) : auditLog && auditLog.length > 0 ? (
                <ul className="divide-y divide-border-subtle">
                  {auditLog.map((entry) => (
                    <li key={entry.id} className="py-3 flex items-center gap-3">
                      <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                        entry.action === "APPROVE" ? "bg-success-soft" :
                        entry.action === "REJECT" ? "bg-danger-soft" :
                        entry.action === "EXPORT" ? "bg-primary-soft" :
                        "bg-warning-soft"
                      }`}>
                        <i className={`fa-solid ${
                          entry.action === "APPROVE" ? "fa-check text-success" :
                          entry.action === "REJECT" ? "fa-arrow-rotate-left text-danger" :
                          entry.action === "EXPORT" ? "fa-file-export text-primary" :
                          "fa-paper-plane text-warning"
                        } text-xs`}></i>
                      </div>
                      <div className="flex-1">
                        <span className="text-text-strong text-sm font-medium">
                          {formatEnum(entry.action)}
                        </span>
                        {entry.metadata.fromState && entry.metadata.toState && (
                          <span className="text-text-muted text-sm ml-2">
                            {formatEnum(entry.metadata.fromState)} → {formatEnum(entry.metadata.toState)}
                          </span>
                        )}
                        {entry.metadata.comment && (
                          <p className="text-text-muted text-sm italic">
                            "{entry.metadata.comment}"
                          </p>
                        )}
                      </div>
                      <span className="text-text-faint text-xs font-mono whitespace-nowrap">
                        {new Date(entry.timestamp).toLocaleString()}
                      </span>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="text-text-muted text-sm">No audit events yet</p>
              )}
            </div>
          )}
        </>
      )}

      {showExport && (
        <div
          className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
          onClick={() => !exporting && setShowExport(false)}
        >
          <div
            className="bg-bg-canvas rounded-xl border border-border-default shadow-lg p-6 max-w-md w-full"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-text-strong flex items-center gap-2">
                <i className="fa-solid fa-file-export text-primary"></i> Export evidence pack
              </h2>
              <button
                onClick={() => !exporting && setShowExport(false)}
                className="text-text-muted hover:text-text-strong"
                disabled={exporting}
              >
                <i className="fa-solid fa-xmark"></i>
              </button>
            </div>

            <div className="flex flex-col gap-4">
              <div>
                <p className="text-sm text-text-strong mb-2 font-medium">Format</p>
                <div className="grid grid-cols-3 gap-2">
                  {(["PDF", "JSON", "CSV"] as ExportFormat[]).map((f) => (
                    <button
                      key={f}
                      onClick={() => setExportFormat(f)}
                      disabled={exporting}
                      className={`border rounded-lg px-3 py-2.5 text-sm flex flex-col items-center gap-1 transition-colors ${
                        exportFormat === f
                          ? "border-primary bg-primary-soft text-primary"
                          : "border-border-default text-text-muted hover:bg-bg-inset"
                      }`}
                    >
                      <i className={
                        f === "PDF" ? "fa-solid fa-file-pdf" :
                        f === "JSON" ? "fa-solid fa-file-code" :
                        "fa-solid fa-file-csv"
                      }></i>
                      {f}
                    </button>
                  ))}
                </div>
                <p className="text-xs text-text-muted mt-2">
                  {exportFormat === "PDF" && "Auditor-facing evidence pack with rendered logs and images."}
                  {exportFormat === "JSON" && "Machine-readable export for tool ingestion (raw markdown)."}
                  {exportFormat === "CSV" && "One row per log, spreadsheet-friendly."}
                </p>
              </div>

              <label className="flex items-start gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  checked={includeExported}
                  onChange={(e) => setIncludeExported(e.target.checked)}
                  disabled={exporting}
                  className="mt-1"
                />
                <span className="text-sm text-text-body">
                  Include already-exported logs
                  <span className="block text-xs text-text-muted">
                    Default: approved logs only. Tick to re-include logs from a prior export.
                  </span>
                </span>
              </label>

              <div className="text-xs text-text-muted bg-warning-soft border border-warning/30 rounded-lg p-3 flex items-start gap-2">
                <i className="fa-solid fa-triangle-exclamation mt-0.5 text-warning"></i>
                <span>
                  Exporting marks included logs as EXPORTED (immutable). This is recorded in the audit trail.
                </span>
              </div>

              <div className="flex gap-2 justify-end">
                <button
                  onClick={() => setShowExport(false)}
                  disabled={exporting}
                  className="border border-border-default rounded-lg px-4 py-2 text-sm hover:bg-bg-inset transition-colors"
                >
                  Cancel
                </button>
                <button
                  onClick={handleExport}
                  disabled={exporting}
                  className="bg-primary text-white rounded-lg px-4 py-2 text-sm font-medium hover:bg-primary-hover disabled:opacity-50 flex items-center gap-2 transition-colors"
                >
                  {exporting ? (
                    <><i className="fa-solid fa-circle-notch fa-spin"></i> Generating...</>
                  ) : (
                    <><i className="fa-solid fa-download"></i> Download {exportFormat}</>
                  )}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
