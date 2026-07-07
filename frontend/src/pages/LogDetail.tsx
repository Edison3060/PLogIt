import { useParams, useNavigate } from "react-router-dom";
import { useLog } from "../hooks/useLogs";
import { useCurrentUser } from "../hooks/useAuth";
import {
  formatActivityType,
  formatEnum,
  outcomeBadgeClass,
  reviewStateBadgeClass,
} from "../lib/logs";

export default function LogDetailPage() {
  const { id, logId } = useParams<{ id: string; logId: string }>();
  const navigate = useNavigate();
  const { data: user } = useCurrentUser();
  const { data: log, isLoading } = useLog(logId);

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto p-6 flex items-center gap-2 text-text-muted">
        <i className="fa-solid fa-circle-notch fa-spin"></i>
        <span>Loading...</span>
      </div>
    );
  }

  if (!log) {
    return (
      <div className="max-w-4xl mx-auto p-6">
        <p className="text-text-muted">Log not found</p>
      </div>
    );
  }

  const isAuthor = user?.id === log.authorId;
  const canEdit = log.reviewState === "DRAFT" && isAuthor;

  return (
    <div className="max-w-4xl mx-auto p-6">
      <button
        onClick={() => navigate(`/engagements/${id}/logs`)}
        className="text-text-muted text-sm mb-4 hover:text-text-strong flex items-center gap-2"
      >
        <i className="fa-solid fa-arrow-left"></i> Back to logs
      </button>

      <div className="flex items-start justify-between mb-6 gap-4">
        <div className="flex-1">
          <h1 className="text-2xl font-semibold text-text-strong">
            {log.title}
          </h1>
          <div className="flex flex-wrap items-center gap-3 mt-2 text-text-muted text-sm">
            <span className="flex items-center gap-1">
              <i className="fa-solid fa-user"></i>
              {log.authorDisplayName}
            </span>
            <span className="flex items-center gap-1">
              <i className="fa-solid fa-clock"></i>
              {new Date(log.createdAt).toLocaleString()}
            </span>
            {log.lastEditedAt !== log.createdAt && (
              <span className="text-text-faint">
                (edited {new Date(log.lastEditedAt).toLocaleString()})
              </span>
            )}
          </div>
        </div>
        <div className="flex flex-col gap-2 items-end">
          <div className="flex gap-2">
            <span
              className={`text-xs px-2 py-1 rounded font-mono ${outcomeBadgeClass(
                log.outcome
              )}`}
            >
              <i className="fa-solid fa-flag"></i>{" "}
              {formatEnum(log.outcome)}
            </span>
            <span
              className={`text-xs px-2 py-1 rounded font-mono ${reviewStateBadgeClass(
                log.reviewState
              )}`}
            >
              <i className="fa-solid fa-check-circle"></i>{" "}
              {formatEnum(log.reviewState)}
            </span>
          </div>
          {canEdit && (
            <button
              onClick={() =>
                navigate(`/engagements/${id}/logs/${log.id}/edit`)
              }
              className="bg-primary text-white rounded px-3 py-1.5 text-sm font-medium hover:bg-primary-hover flex items-center gap-2"
            >
              <i className="fa-solid fa-pen"></i> Edit
            </button>
          )}
        </div>
      </div>

      {log.rejectionComment && (
        <div className="bg-red-50 dark:bg-red-950/30 border border-red-200 dark:border-red-900 rounded-lg p-4 mb-4">
          <h3 className="text-sm font-medium text-red-700 dark:text-red-400 flex items-center gap-2 mb-1">
            <i className="fa-solid fa-comment-dots"></i> Rejection feedback
          </h3>
          <p className="text-red-600 dark:text-red-300 text-sm whitespace-pre-wrap">
            {log.rejectionComment}
          </p>
        </div>
      )}

      <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-6">
        <div className="bg-bg-card rounded-lg border border-border-subtle p-3">
          <p className="text-text-muted text-xs mb-1">Activity type</p>
          <p className="text-text-strong text-sm">
            {formatActivityType(log.activityType)}
          </p>
        </div>
        <div className="bg-bg-card rounded-lg border border-border-subtle p-3">
          <p className="text-text-muted text-xs mb-1">Target</p>
          <p className="text-text-strong text-sm font-mono">
            {log.target || "-"}
          </p>
        </div>
        <div className="bg-bg-card rounded-lg border border-border-subtle p-3">
          <p className="text-text-muted text-xs mb-1">Tool</p>
          <p className="text-text-strong text-sm">
            {log.toolUsed || "-"}
          </p>
        </div>
        <div className="bg-bg-card rounded-lg border border-border-subtle p-3">
          <p className="text-text-muted text-xs mb-1">Tags</p>
          <div className="flex flex-wrap gap-1">
            {log.tags && log.tags.length > 0 ? (
              log.tags.map((tag, i) => (
                <span
                  key={i}
                  className="text-xs bg-bg-inset text-text-muted px-1.5 py-0.5 rounded font-mono"
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

      <div className="bg-bg-card rounded-lg border border-border-subtle p-6 mb-4">
        <h2 className="font-medium text-text-strong mb-2 flex items-center gap-2">
          <i className="fa-solid fa-align-left text-text-muted"></i> Description
        </h2>
        <p className="text-text-body whitespace-pre-wrap">{log.description}</p>
      </div>

      <div className="bg-bg-card rounded-lg border border-border-subtle p-6 mb-4">
        <h2 className="font-medium text-text-strong mb-2 flex items-center gap-2">
          <i className="fa-solid fa-clipboard-check text-text-muted"></i> Result
        </h2>
        <p className="text-text-body whitespace-pre-wrap">{log.result}</p>
      </div>

      {log.codeBlock && (
        <div className="bg-bg-card rounded-lg border border-border-subtle p-6 mb-4">
          <h2 className="font-medium text-text-strong mb-2 flex items-center gap-2">
            <i className="fa-solid fa-code text-text-muted"></i>
            {log.codeLanguage ? `Code (${log.codeLanguage})` : "Code"}
          </h2>
          <pre className="bg-bg-inset rounded p-4 overflow-x-auto text-sm text-text-body font-mono">
            <code>{log.codeBlock}</code>
          </pre>
        </div>
      )}

      <p className="text-text-muted text-sm mt-6 flex items-center gap-2">
        <i className="fa-solid fa-info-circle"></i>
        Markdown rendering arrives in Slice 9
      </p>
    </div>
  );
}
