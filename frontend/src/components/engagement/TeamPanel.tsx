import type { EngagementDetail } from "../../lib/engagements";

interface TeamPanelProps {
  engagement: EngagementDetail;
  isLeader: boolean;
  showTransfer: boolean;
  onToggleTransfer: () => void;
  onTransfer: (memberId: number) => void;
  onRemove: (memberId: number) => void;
}

export default function TeamPanel({ engagement, isLeader, showTransfer, onToggleTransfer, onTransfer, onRemove }: TeamPanelProps) {
  return (
            <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6">
              <div className="flex items-center justify-between mb-4">
                <h2 className="font-semibold text-text-strong flex items-center gap-2">
                  <i className="fa-solid fa-users text-text-muted"></i> Team ({engagement.members.length})
                </h2>
                {isLeader && engagement.members.length > 1 && (
                  <button
                    onClick={onToggleTransfer}
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
                              onClick={() => onTransfer(m.id)}
                              className="text-xs border border-primary text-primary rounded-lg px-2 py-1 hover:bg-primary-soft transition-colors"
                            >
                              Make leader
                            </button>
                          )}
                          <button
                            onClick={() => onRemove(m.id)}
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

  );
}
