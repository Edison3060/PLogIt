import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useLogs } from "../hooks/useLogs";
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
    <div className="max-w-6xl mx-auto p-6">
      <button
        onClick={() => navigate(`/engagements/${engagementId}`)}
        className="text-text-muted text-sm mb-4 hover:text-text-strong flex items-center gap-2"
      >
        <i className="fa-solid fa-arrow-left"></i> Back to engagement
      </button>

      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-semibold text-text-strong flex items-center gap-3">
          <i className="fa-solid fa-list-check text-text-muted"></i>
          Logs
        </h1>
        <button
          onClick={() => navigate(`/engagements/${engagementId}/logs/new`)}
          className="bg-primary text-white rounded px-4 py-2 text-sm font-medium hover:bg-primary-hover flex items-center gap-2"
        >
          <i className="fa-solid fa-plus"></i> New Log
        </button>
      </div>

      <div className="bg-bg-card rounded-lg border border-border-subtle p-4 mb-4 flex flex-wrap gap-3 items-center">
        <div className="relative flex-1 min-w-[200px]">
          <i className="fa-solid fa-magnifying-glass absolute left-3 top-1/2 -translate-y-1/2 text-text-faint text-sm"></i>
          <input
            type="text"
            value={search}
            onChange={(e) => onFilterChange(setSearch, e.target.value)}
            placeholder="Search title or description..."
            className="bg-bg-canvas border border-border-default rounded pl-9 pr-3 py-2 w-full text-text-strong focus:outline-none focus:border-primary text-sm"
          />
        </div>
        <select
          value={activityType}
          onChange={(e) => onFilterChange(setActivityType, e.target.value)}
          className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary text-sm"
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
          className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary text-sm"
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
          className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary text-sm"
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
            className="text-text-muted text-sm hover:text-text-strong flex items-center gap-1 px-2"
          >
            <i className="fa-solid fa-xmark"></i> Clear
          </button>
        )}
      </div>

      {isLoading ? (
        <div className="flex items-center gap-2 text-text-muted">
          <i className="fa-solid fa-circle-notch fa-spin"></i>
          <span>Loading logs...</span>
        </div>
      ) : data && data.items.length > 0 ? (
        <>
          <div className="bg-bg-card rounded-lg border border-border-subtle overflow-hidden">
            <table className="w-full">
              <thead className="bg-bg-inset border-b border-border-subtle">
                <tr className="text-left text-xs text-text-muted uppercase">
                  <th className="px-4 py-3 font-medium">Timestamp</th>
                  <th className="px-4 py-3 font-medium">Author</th>
                  <th className="px-4 py-3 font-medium">Title</th>
                  <th className="px-4 py-3 font-medium">Type</th>
                  <th className="px-4 py-3 font-medium">Target</th>
                  <th className="px-4 py-3 font-medium">Outcome</th>
                  <th className="px-4 py-3 font-medium">State</th>
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
                    <td className="px-4 py-3 text-text-muted text-sm font-mono whitespace-nowrap">
                      {new Date(log.createdAt).toLocaleString()}
                    </td>
                    <td className="px-4 py-3 text-text-strong text-sm">
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
                        className={`text-xs px-2 py-1 rounded font-mono ${outcomeBadgeClass(
                          log.outcome
                        )}`}
                      >
                        {formatEnum(log.outcome)}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <span
                        className={`text-xs px-2 py-1 rounded font-mono ${reviewStateBadgeClass(
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

          <div className="flex items-center justify-between mt-4">
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
                  className="border border-border-default rounded px-3 py-1 text-sm text-text-strong hover:bg-bg-inset disabled:opacity-50 flex items-center gap-1"
                >
                  <i className="fa-solid fa-chevron-left"></i> Prev
                </button>
                <span className="text-text-muted text-sm font-mono">
                  {page + 1} / {totalPages}
                </span>
                <button
                  onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
                  disabled={page >= totalPages - 1}
                  className="border border-border-default rounded px-3 py-1 text-sm text-text-strong hover:bg-bg-inset disabled:opacity-50 flex items-center gap-1"
                >
                  Next <i className="fa-solid fa-chevron-right"></i>
                </button>
              </div>
            )}
          </div>
        </>
      ) : (
        <div className="bg-bg-card rounded-lg border border-border-subtle p-12 text-center">
          <i className="fa-solid fa-clipboard text-text-faint text-3xl mb-3"></i>
          <p className="text-text-muted">
            {hasFilters ? "No logs match your filters" : "No logs yet"}
          </p>
          {!hasFilters && (
            <p className="text-text-muted text-sm mt-1">
              Create one to get started
            </p>
          )}
        </div>
      )}
    </div>
  );
}
