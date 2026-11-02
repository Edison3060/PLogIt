import type { AuditLogEntry } from "../../lib/engagements";
import { formatEnum } from "../../lib/logs";

interface AuditPanelProps {
  auditLog: AuditLogEntry[] | undefined;
  isLoading: boolean;
}

export default function AuditPanel({ auditLog, isLoading }: AuditPanelProps) {
  return (
            <div className="bg-bg-card rounded-xl border border-border-default shadow-sm p-6">
              <h2 className="font-semibold text-text-strong mb-4 flex items-center gap-2">
                <i className="fa-solid fa-shield-halved text-text-muted"></i> Audit log
              </h2>
              {isLoading ? (
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
                        (entry.action === "EXPORT" || entry.action === "EXPORT_ENGAGEMENT") ? "bg-primary-soft" :
                        "bg-warning-soft"
                      }`}>
                        <i className={`fa-solid ${
                          entry.action === "APPROVE" ? "fa-check text-success" :
                          entry.action === "REJECT" ? "fa-arrow-rotate-left text-danger" :
                          (entry.action === "EXPORT" || entry.action === "EXPORT_ENGAGEMENT") ? "fa-file-export text-primary" :
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
                        {entry.metadata.format && (
                          <p className="text-text-muted text-sm">
                            {entry.metadata.format} · {entry.metadata.logCount} logs
                          </p>
                        )}
                        {entry.metadata.changedFields && (
                          <p className="text-text-muted text-sm">
                            Updated: {entry.metadata.changedFields.join(", ")}
                          </p>
                        )}
                        {entry.metadata.memberId != null && (
                          <p className="text-text-muted text-sm">Member #{entry.metadata.memberId}</p>
                        )}
                        {entry.metadata.previousLeaderId != null && entry.metadata.newLeaderId != null && (
                          <p className="text-text-muted text-sm">
                            Leader #{entry.metadata.previousLeaderId} → #{entry.metadata.newLeaderId}
                          </p>
                        )}
                        <p className="text-text-faint text-xs">Actor #{entry.actorId}</p>
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

  );
}
