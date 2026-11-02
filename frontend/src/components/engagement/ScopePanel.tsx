import type { EngagementDetail } from "../../lib/engagements";

export default function ScopePanel({ engagement }: { engagement: EngagementDetail }) {
  return (
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

  );
}
