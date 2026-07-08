import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useLogs } from "../hooks/useLogs";
import { useEngagements } from "../hooks/useEngagements";
import {
  ACTIVITY_TYPES,
  OUTCOMES,
  REVIEW_STATES,
  formatActivityType,
  formatEnum,
  outcomeBadgeClass,
  reviewStateBadgeClass,
} from "../lib/logs";

const PAGE_SIZE = 10;

const selectClass =
  "bg-bg-canvas border border-border-default rounded-lg px-3 py-2 text-text-strong text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-colors cursor-pointer";

export default function LogBoard() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const engagementId = Number(id);

  const [search, setSearch] = useState("");
  const [activityType, setActivityType] = useState("");
  const [outcome, setOutcome] = useState("");
  const [reviewState, setReviewState] = useState("");
  const [page, setPage] = useState(0);

  const queryParams = {
    activityType: activityType || undefined,
    outcome: outcome || undefined,
    reviewState: reviewState || undefined,
    search: search || undefined,
    page,
    size: PAGE_SIZE,
  };

  const { data, isLoading, isFetching } = useLogs(engagementId, queryParams);
  const { data: engagements } = useEngagements();

  const isLeader =
    engagements?.find((e) => e.id === engagementId)?.role === "LEADER";
  const inReviewQueue = reviewState === "SUBMITTED";

  const totalPages = data?.totalPages ?? 0;
  const hasFilters = search || activityType || outcome || reviewState;

  const resetFilters = () => {
    setSearch("");
    setActivityType("");
    setOutcome("");
    setReviewState("");
    setPage(0);
  };

  const onFilterChange = (
    setter: (v: string) => void,
    value: string
  ) => {
    setter(value);
    setPage(0);
  };

  return (
    <div className="max-w-7xl mx-auto p-6 md:p-8">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-text-strong flex items-center gap-3">
            <i className="fa-solid fa-list-check text-primary"></i>
            Activity Logs
          </h1>
          <p className="text-text-muted text-sm mt-1">
            Capture and track engagement activity.
          </p>
        </div>
        <div className="flex items-center gap-2">
          {isLeader && (
            <button
              onClick={() =>
                onFilterChange(
                  setReviewState,
                  inReviewQueue ? "" : "SUBMITTED"
                )
              }
              className={`rounded-lg px-4 py-2 text-sm font-medium flex items-center gap-2 transition-colors ${
                inReviewQueue
                  ? "bg-warning-soft text-warning border border-warning/40"
                  : "bg-bg-inset text-text-strong hover:bg-border-subtle border border-border-default"
              }`}
            >
              <i className="fa-solid fa-clipboard-check"></i> Review Queue
            </button>
          )}
          <button
            onClick={() => navigate(`/engagements/${engagementId}/logs/new`)}
            className="bg-primary text-white rounded-lg px-4 py-2 text-sm font-medium hover:bg-primary-hover flex items-center gap-2 transition-colors"
          >
            <i className="fa-solid fa-plus"></i> New Log
          </button>
        </div>
      </div>

      <div className="bg-bg-card border border-border-default rounded-xl shadow-sm p-4 mb-6 flex flex-wrap gap-3 items-center">
        <div className="relative flex-1 min-w-[220px]">
          <i className="fa-solid fa-magnifying-glass absolute left-3 top-1/2 -translate-y-1/2 text-text-faint text-sm pointer-events-none"></i>
          <input
            type="text"
            value={search}
            onChange={(e) => onFilterChange(setSearch, e.target.value)}
            placeholder="Search title or description..."
            className="bg-bg-canvas border border-border-default rounded-lg pl-9 pr-3 py-2 w-full text-text-strong text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
          />
        </div>
        <select
          value={activityType}
          onChange={(e) => onFilterChange(setActivityType, e.target.value)}
          className={selectClass}
        >
          <option value="">All types</option>
          {ACTIVITY_TYPES.map((t) => (
            <option key={t} value={t}>
              {formatActivityType(t)}
            </option>
          ))}
        </select>
        <select
          value={outcome}
          onChange={(e) => onFilterChange(setOutcome, e.target.value)}
          className={selectClass}
        >
          <option value="">All outcomes</option>
          {OUTCOMES.map((o) => (
            <option key={o} value={o}>
              {formatEnum(o)}
            </option>
          ))}
        </select>
        <select
          value={reviewState}
          onChange={(e) => onFilterChange(setReviewState, e.target.value)}
          className={selectClass}
        >
          <option value="">All states</option>
          {REVIEW_STATES.map((s) => (
            <option key={s} value={s}>
              {formatEnum(s)}
            </option>
          ))}
        </select>
        {hasFilters && (
          <button
            onClick={resetFilters}
            className="text-text-muted text-sm hover:text-danger flex items-center gap-1 px-2 transition-colors"
          >
            <i className="fa-solid fa-xmark"></i> Clear
          </button>
        )}
      </div>

      {isLoading ? (
        <div className="text-center py-16 text-text-muted">
          <i className="fa-solid fa-circle-notch fa-spin text-3xl mb-3"></i>
          <p>Loading logs...</p>
        </div>
      ) : data && data.items.length > 0 ? (
        <>
          <div className="bg-bg-card rounded-xl border border-border-default shadow-sm overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-bg-inset border-b border-border-default">
                  <tr className="text-left text-[11px] text-text-muted uppercase tracking-wider">
                    <th className="px-4 py-3 font-semibold">Timestamp</th>
                    <th className="px-4 py-3 font-semibold">Author</th>
                    <th className="px-4 py-3 font-semibold">Title</th>
                    <th className="px-4 py-3 font-semibold">Type</th>
                    <th className="px-4 py-3 font-semibold">Target</th>
                    <th className="px-4 py-3 font-semibold">Outcome</th>
                    <th className="px-4 py-3 font-semibold">State</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border-subtle">
                  {data.items.map((log) => (
                    <tr
                      key={log.id}
                      onClick={() =>
                        navigate(`/engagements/${engagementId}/logs/${log.id}`)
                      }
                      className="hover:bg-bg-inset cursor-pointer transition-colors"
                    >
                      <td className="px-4 py-3 text-text-muted text-xs font-mono whitespace-nowrap">
                        {new Date(log.createdAt).toLocaleString()}
                      </td>
                      <td className="px-4 py-3 text-text-strong text-sm font-medium whitespace-nowrap">
                        {log.authorDisplayName}
                      </td>
                      <td className="px-4 py-3 text-text-strong text-sm font-medium">
                        {log.title}
                      </td>
                      <td className="px-4 py-3 text-text-body text-sm">
                        {formatActivityType(log.activityType)}
                      </td>
                      <td className="px-4 py-3 text-text-body text-sm font-mono">
                        {log.target || "-"}
                      </td>
                      <td className="px-4 py-3">
                        <span
                          className={`text-[10px] px-2 py-1 rounded font-bold uppercase tracking-wide ${outcomeBadgeClass(
                            log.outcome
                          )}`}
                        >
                          {formatEnum(log.outcome)}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <span
                          className={`text-[10px] px-2 py-1 rounded font-bold uppercase tracking-wide ${reviewStateBadgeClass(
                            log.reviewState
                          )}`}
                        >
                          {formatEnum(log.reviewState)}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <div className="flex items-center justify-between mt-4 px-1">
            <p className="text-text-muted text-sm">
              {isFetching ? (
                <span className="flex items-center gap-2">
                  <i className="fa-solid fa-circle-notch fa-spin"></i> Updating...
                </span>
              ) : (
                <>
                  {data.totalElements} log{data.totalElements !== 1 ? "s" : ""}
                  {hasFilters && " (filtered)"}
                </>
              )}
            </p>
            {totalPages > 1 && (
              <div className="flex items-center gap-2">
                <button
                  onClick={() => setPage(Math.max(0, page - 1))}
                  disabled={page === 0}
                  className="border border-border-default rounded-lg px-3 py-1.5 text-sm text-text-strong hover:bg-bg-inset disabled:opacity-50 flex items-center gap-1 transition-colors"
                >
                  <i className="fa-solid fa-chevron-left"></i> Prev
                </button>
                <span className="text-text-muted text-sm font-mono px-2">
                  {page + 1} / {totalPages}
                </span>
                <button
                  onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
                  disabled={page >= totalPages - 1}
                  className="border border-border-default rounded-lg px-3 py-1.5 text-sm text-text-strong hover:bg-bg-inset disabled:opacity-50 flex items-center gap-1 transition-colors"
                >
                  Next <i className="fa-solid fa-chevron-right"></i>
                </button>
              </div>
            )}
          </div>
        </>
      ) : (
        <div className="bg-bg-card border border-dashed border-border-default rounded-xl p-12 text-center">
          <div className="w-16 h-16 mx-auto rounded-full bg-bg-inset flex items-center justify-center mb-4">
            <i className="fa-solid fa-clipboard text-text-faint text-2xl"></i>
          </div>
          <h3 className="text-lg font-bold text-text-strong mb-1">
            {hasFilters ? "No logs match your filters" : "No logs yet"}
          </h3>
          <p className="text-text-muted text-sm">
            {hasFilters ? "Try adjusting or clearing your filters." : "Create one to get started."}
          </p>
        </div>
      )}
    </div>
  );
}
