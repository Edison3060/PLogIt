import type { EngagementDetail } from "../../lib/engagements";

interface OverviewPanelProps {
  engagement: EngagementDetail;
  onViewLogs: () => void;
}

export default function OverviewPanel({ engagement, onViewLogs }: OverviewPanelProps) {
  return (
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
                onClick={onViewLogs}
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

  );
}
