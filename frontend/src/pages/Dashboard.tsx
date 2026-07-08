import { useNavigate } from "react-router-dom";
import { useCurrentUser } from "../hooks/useAuth";
import { useEngagements } from "../hooks/useEngagements";

export default function Dashboard() {
  const { data: user } = useCurrentUser();
  const { data: engagements, isLoading } = useEngagements();
  const navigate = useNavigate();

  return (
    <div className="max-w-7xl mx-auto p-6 md:p-8">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 mb-8">
        <div>
          <h1 className="text-3xl font-bold text-text-strong">
            Welcome, {user?.displayName ?? "Pentester"}
          </h1>
          <p className="text-text-muted text-sm mt-1">
            Here is an overview of your active security engagements.
          </p>
        </div>
        <button
          onClick={() => navigate('/engagements')}
          className="px-4 py-2.5 rounded-lg text-sm font-medium transition-colors flex items-center gap-2 bg-primary text-white hover:bg-primary-hover"
        >
          <i className="fa-solid fa-folder-plus"></i>
          Manage Engagements
        </button>
      </div>

      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-text-strong flex items-center gap-2">
          <i className="fa-solid fa-folder text-text-muted"></i>
          Active Engagements
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
