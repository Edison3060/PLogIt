import { useState, useRef, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useLog, useLogHistory, useTransitionLog } from "../hooks/useLogs";
import { useCurrentUser } from "../hooks/useAuth";
import {
  formatActivityType,
  formatEnum,
  outcomeBadgeClass,
  reviewStateBadgeClass,
  fetchAttachments,
  uploadAttachment,
  attachmentUrl,
  formatFileSize,
  type Attachment,
} from "../lib/logs";
import type { ReviewAction } from "../lib/logs";

export default function LogDetailPage() {
  const { id, logId } = useParams<{ id: string; logId: string }>();
  const navigate = useNavigate();
  const { data: user } = useCurrentUser();
  const { data: log, isLoading } = useLog(logId);
  const transition = useTransitionLog(Number(id));
  const { data: history, isLoading: historyLoading } = useLogHistory(logId);

  const [showReject, setShowReject] = useState(false);
  const [rejectComment, setRejectComment] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [showHistory, setShowHistory] = useState(false);
  const [attachments, setAttachments] = useState<Attachment[]>([]);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const loadAttachments = async (lid: string) => {
    try {
      const list = await fetchAttachments(lid);
      setAttachments(list);
    } catch {
      setAttachments([]);
    }
  };

  useEffect(() => {
    if (logId) loadAttachments(logId);
  }, [logId]);

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file || !logId) return;
    setUploading(true);
    setError(null);
    try {
      await uploadAttachment(logId, file);
      await loadAttachments(logId);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Upload failed");
    } finally {
      setUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = "";
    }
  };

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto p-8 text-center text-text-muted">
        <i className="fa-solid fa-circle-notch fa-spin text-3xl mb-3"></i>
        <p>Loading...</p>
      </div>
    );
  }

  if (!log) {
    return (
      <div className="max-w-4xl mx-auto p-8">
        <div className="bg-bg-card border border-dashed border-border-default rounded-xl p-12 text-center">
          <i className="fa-solid fa-file-circle-question text-text-faint text-3xl mb-3"></i>
          <p className="text-text-muted">Log not found</p>
        </div>
      </div>
    );
  }

  const isAuthor = user?.id === log.authorId;
  const isLeader = user?.id === log.leaderId;
  const canEdit = log.reviewState === "DRAFT" && (isAuthor || isLeader);
  const canSubmit = log.reviewState === "DRAFT" && (isAuthor || isLeader);
  const canReview = log.reviewState === "SUBMITTED" && isLeader;

  const onTransition = (action: ReviewAction, comment?: string) => {
    setError(null);
    transition.mutate(
      { logId: log.id, action, comment },
      {
        onError: (err: Error) => setError(err.message),
        onSuccess: () => setShowReject(false),
      }
    );
  };

  const onSubmitReject = () => {
    if (!rejectComment.trim()) {
      setError("A rejection comment is required");
      return;
    }
    onTransition("REJECT", rejectComment.trim());
  };

  const metaCards = [
    { label: "Activity type", value: formatActivityType(log.activityType), icon: "fa-tag", mono: false },
    { label: "Target", value: log.target || "-", icon: "fa-crosshairs", mono: true },
    { label: "Tool", value: log.toolUsed || "-", icon: "fa-screwdriver-wrench", mono: false },
  ];

  return (
    <div className="max-w-4xl mx-auto p-6 md:p-8">
      <div className="flex items-start justify-between mb-6 gap-4">
        <div className="flex-1 min-w-0">
          <h1 className="text-2xl font-bold text-text-strong">{log.title}</h1>
          <div className="flex flex-wrap items-center gap-x-4 gap-y-1 mt-2 text-text-muted text-sm">
            <span className="flex items-center gap-1.5">
              <i className="fa-solid fa-user text-text-faint"></i>
              {log.authorDisplayName}
            </span>
            <span className="flex items-center gap-1.5">
              <i className="fa-solid fa-clock text-text-faint"></i>
              {new Date(log.createdAt).toLocaleString()}
            </span>
            {log.lastEditedAt !== log.createdAt && (
              <span className="text-text-faint text-xs">
                edited {new Date(log.lastEditedAt).toLocaleString()}
              </span>
            )}
          </div>
        </div>
        <div className="flex flex-col gap-2 items-end shrink-0">
          <div className="flex gap-2">
            <span
              className={`text-[10px] px-2 py-1 rounded font-bold uppercase tracking-wide flex items-center gap-1 ${outcomeBadgeClass(
                log.outcome
              )}`}
            >
              <i className="fa-solid fa-flag"></i>
              {formatEnum(log.outcome)}
            </span>
            <span
              className={`text-[10px] px-2 py-1 rounded font-bold uppercase tracking-wide flex items-center gap-1 ${reviewStateBadgeClass(
                log.reviewState
              )}`}
            >
              <i className="fa-solid fa-circle-check"></i>
              {formatEnum(log.reviewState)}
            </span>
          </div>
          {canEdit && (
            <button
              onClick={() =>
                navigate(`/engagements/${id}/logs/${log.id}/edit`)
              }
              className="bg-bg-inset text-text-strong rounded-lg px-3 py-1.5 text-sm font-medium hover:bg-border-subtle flex items-center gap-2 transition-colors"
            >
              <i className="fa-solid fa-pen"></i> Edit
            </button>
          )}
        </div>
      </div>

      {error && (
        <div className="bg-danger-soft border border-danger/30 text-danger rounded-lg p-3 mb-4 text-sm flex items-center gap-2 font-medium">
          <i className="fa-solid fa-circle-exclamation"></i> {error}
        </div>
      )}

      {log.rejectionComment && (
        <div className="bg-danger-soft border border-danger/30 rounded-xl p-4 mb-4">
          <h3 className="text-sm font-semibold text-danger flex items-center gap-2 mb-1">
            <i className="fa-solid fa-comment-dots"></i> Rejection feedback
          </h3>
          <p className="text-danger/90 text-sm whitespace-pre-wrap">
            {log.rejectionComment}
          </p>
        </div>
      )}

      {(canSubmit || canReview) && (
        <div className="bg-bg-card border border-border-default rounded-xl shadow-sm p-4 mb-4 flex flex-wrap items-center gap-3">
          {canSubmit && (
            <button
              onClick={() => onTransition("SUBMIT")}
              disabled={transition.isPending}
              className="bg-primary text-white rounded-lg px-4 py-2 text-sm font-medium hover:bg-primary-hover disabled:opacity-50 flex items-center gap-2 transition-colors"
            >
              <i className="fa-solid fa-paper-plane"></i> Submit for review
            </button>
          )}
          {canReview && (
            <>
              <span className="text-text-muted text-sm mr-1">
                Awaiting your review:
              </span>
              <button
                onClick={() => onTransition("APPROVE")}
                disabled={transition.isPending}
                className="bg-success text-white rounded-lg px-4 py-2 text-sm font-medium hover:opacity-90 disabled:opacity-50 flex items-center gap-2 transition-colors"
              >
                <i className="fa-solid fa-check"></i> Approve
              </button>
              <button
                onClick={() => {
                  setError(null);
                  setShowReject(true);
                }}
                disabled={transition.isPending}
                className="bg-danger text-white rounded-lg px-4 py-2 text-sm font-medium hover:opacity-90 disabled:opacity-50 flex items-center gap-2 transition-colors"
              >
                <i className="fa-solid fa-arrow-rotate-left"></i> Reject
              </button>
            </>
          )}
        </div>
      )}

      {showReject && (
        <div className="bg-bg-card border border-danger/40 rounded-xl shadow-sm p-4 mb-4">
          <h3 className="text-sm font-semibold text-text-strong mb-2 flex items-center gap-2">
            <i className="fa-solid fa-comment-dots text-danger"></i> Rejection
            comment (required)
          </h3>
          <textarea
            value={rejectComment}
            onChange={(e) => setRejectComment(e.target.value)}
            rows={3}
            placeholder="Explain what needs to be fixed before resubmission..."
            className="bg-bg-canvas border border-border-default rounded-lg px-3 py-2 w-full text-text-strong text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
          />
          <div className="flex gap-2 mt-2">
            <button
              onClick={onSubmitReject}
              disabled={transition.isPending}
              className="bg-danger text-white rounded-lg px-4 py-2 text-sm font-medium hover:opacity-90 disabled:opacity-50 flex items-center gap-2 transition-colors"
            >
              <i className="fa-solid fa-paper-plane"></i> Send back to draft
            </button>
            <button
              onClick={() => {
                setShowReject(false);
                setRejectComment("");
                setError(null);
              }}
              className="text-text-muted rounded-lg px-4 py-2 text-sm hover:text-text-strong hover:bg-bg-inset transition-colors"
            >
              Cancel
            </button>
          </div>
        </div>
      )}

      <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-6">
        {metaCards.map((m) => (
          <div key={m.label} className="bg-bg-card rounded-xl border border-border-default shadow-sm p-3">
            <p className="text-text-faint text-[11px] mb-1 flex items-center gap-1 uppercase tracking-wide font-semibold">
              <i className={`fa-solid ${m.icon}`}></i>
              {m.label}
            </p>
            <p className={`text-text-strong text-sm ${m.mono ? "font-mono" : ""}`}>
              {m.value}
            </p>
          </div>
        ))}
        <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-3">
          <p className="text-text-faint text-[11px] mb-1 flex items-center gap-1 uppercase tracking-wide font-semibold">
            <i className="fa-solid fa-hashtag"></i>
            Tags
          </p>
          <div className="flex flex-wrap gap-1">
            {log.tags && log.tags.length > 0 ? (
              log.tags.map((tag, i) => (
                <span
                  key={i}
                  className="text-[11px] bg-primary-soft text-primary px-1.5 py-0.5 rounded font-mono"
                >
                  {tag}
                </span>
              ))
            ) : (
              <p className="text-text-muted text-sm">-</p>
            )}
          </div>
        </div>
      </div>

      <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6 mb-4">
        <h2 className="font-semibold text-text-strong mb-3 flex items-center gap-2">
          <i className="fa-solid fa-align-left text-primary"></i> Description
        </h2>
        <div
          className="text-text-body prose prose-sm dark:prose-invert max-w-none"
          dangerouslySetInnerHTML={{ __html: log.descriptionHtml || log.description }}
        />
      </div>

      <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6 mb-4">
        <h2 className="font-semibold text-text-strong mb-3 flex items-center gap-2">
          <i className="fa-solid fa-clipboard-check text-success"></i> Result
        </h2>
        <div
          className="text-text-body prose prose-sm dark:prose-invert max-w-none"
          dangerouslySetInnerHTML={{ __html: log.resultHtml || log.result }}
        />
      </div>

      {log.codeBlock && (
        <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6 mb-4">
          <h2 className="font-semibold text-text-strong mb-3 flex items-center gap-2">
            <i className="fa-solid fa-code text-text-muted"></i>
            {log.codeLanguage ? `Code (${log.codeLanguage})` : "Code"}
          </h2>
          <pre className="bg-bg-inset rounded-lg p-4 overflow-x-auto text-sm text-text-body font-mono border border-border-subtle">
            <code>{log.codeBlock}</code>
          </pre>
        </div>
      )}

      <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6 mb-4">
        <h2 className="font-semibold text-text-strong mb-3 flex items-center gap-2">
          <i className="fa-solid fa-paperclip text-text-muted"></i> Attachments
        </h2>
        <div className="flex flex-wrap gap-3 mb-3">
          {attachments.length > 0 ? (
            attachments.map((att) => (
              <div
                key={att.id}
                className="relative group w-32 h-32 rounded-lg border border-border-default overflow-hidden bg-bg-inset"
              >
                <img
                  src={attachmentUrl(log.id, att.id)}
                  alt={att.filename}
                  className="w-full h-full object-cover"
                />
                <div className="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity flex flex-col items-center justify-center gap-1 p-1">
                  <a
                    href={attachmentUrl(log.id, att.id)}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-white text-xs flex items-center gap-1 hover:text-white/80"
                  >
                    <i className="fa-solid fa-expand"></i> View
                  </a>
                  <span className="text-white/70 text-[10px] truncate w-full text-center" title={att.filename}>
                    {att.filename}
                  </span>
                  <span className="text-white/50 text-[10px]">{formatFileSize(att.size)}</span>
                </div>
              </div>
            ))
          ) : (
            <p className="text-text-muted text-sm">No attachments yet</p>
          )}
        </div>
        {canEdit && (
          <div>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/png,image/jpeg,image/gif,image/webp"
              onChange={handleUpload}
              disabled={uploading}
              className="hidden"
              id="attachment-upload"
            />
            <label
              htmlFor="attachment-upload"
              className="bg-bg-inset border border-border-default rounded-lg px-4 py-2 text-sm text-text-strong hover:bg-border-subtle flex items-center gap-2 cursor-pointer w-fit transition-colors"
            >
              {uploading ? (
                <><i className="fa-solid fa-circle-notch fa-spin"></i> Uploading...</>
              ) : (
                <><i className="fa-solid fa-upload"></i> Upload image</>
              )}
            </label>
          </div>
        )}
      </div>

      <div>
        <button
          onClick={() => setShowHistory(!showHistory)}
          className="text-text-muted text-sm hover:text-text-strong flex items-center gap-2 mb-3 transition-colors"
        >
          <i className={`fa-solid fa-chevron-${showHistory ? "down" : "right"}`}></i>
          Edit history {history && history.length > 0 && `(${history.length})`}
        </button>
        {showHistory && (
          <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-4">
            {historyLoading ? (
              <div className="flex items-center gap-2 text-text-muted text-sm">
                <i className="fa-solid fa-circle-notch fa-spin"></i> Loading...
              </div>
            ) : history && history.length > 0 ? (
              <div className="space-y-3">
                {history.map((v) => (
                  <div key={v.id} className="border-b border-border-subtle pb-3 last:border-0 last:pb-0">
                    <div className="flex items-center gap-2 text-sm mb-1">
                      <span className="font-mono text-text-strong font-semibold bg-bg-inset px-2 py-0.5 rounded">
                        v{v.versionNumber}
                      </span>
                      <span className="text-text-faint">
                        {new Date(v.editedAt).toLocaleString()}
                      </span>
                    </div>
                    <p className="text-text-body text-sm">
                      <span className="text-text-muted">Title:</span>{" "}
                      {v.snapshot.title || "-"}
                    </p>
                    <p className="text-text-body text-sm">
                      <span className="text-text-muted">Result:</span>{" "}
                      {v.snapshot.result || "-"}
                    </p>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-text-muted text-sm">No edit history</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
