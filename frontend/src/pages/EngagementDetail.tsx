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
import OverviewPanel from "../components/engagement/OverviewPanel";
import ScopePanel from "../components/engagement/ScopePanel";
import TeamPanel from "../components/engagement/TeamPanel";
import AuditPanel from "../components/engagement/AuditPanel";
import ExportDialog from "../components/engagement/ExportDialog";

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
      queryClient.invalidateQueries({ queryKey: ["audit", engagementId] });
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
      queryClient.invalidateQueries({ queryKey: ["audit", engagementId] });
      setSuccess("Member removed");
      setTimeout(() => setSuccess(""), 3000);
    },
    onError: (err: Error) => setError(err.message),
  });

  const transferMut = useMutation({
    mutationFn: (newLeaderId: number) => transferLeadership(Number(id), newLeaderId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["engagement", id] });
      queryClient.invalidateQueries({ queryKey: ["audit", engagementId] });
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
    queryClient.invalidateQueries({ queryKey: ["audit", engagementId] });
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
      queryClient.invalidateQueries({ queryKey: ["audit", engagementId] });
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
            <OverviewPanel engagement={engagement} onViewLogs={() => navigate(`/engagements/${engagement.id}/logs`)} />
          )}

          {tab === "scope" && <ScopePanel engagement={engagement} />}

          {tab === "team" && (
            <TeamPanel engagement={engagement} isLeader={isLeader} showTransfer={showTransfer}
              onToggleTransfer={() => setShowTransfer(!showTransfer)}
              onTransfer={(memberId) => transferMut.mutate(memberId)}
              onRemove={(memberId) => removeMut.mutate(memberId)} />
          )}

          {tab === "audit" && isLeader && (
            <AuditPanel auditLog={auditLog} isLoading={auditLoading} />
          )}
        </>
      )}

      {showExport && (
        <ExportDialog exportFormat={exportFormat} includeExported={includeExported} exporting={exporting}
          onClose={() => setShowExport(false)} onFormatChange={setExportFormat}
          onIncludeExportedChange={setIncludeExported} onExport={handleExport} />
      )}
    </div>
  );
}
