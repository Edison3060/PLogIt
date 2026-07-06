import { useParams, useNavigate } from "react-router-dom";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { fetchEngagement, generateJoinCode } from "../lib/engagements";

export default function EngagementDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [copied, setCopied] = useState(false);

  const { data: engagement, isLoading } = useQuery({
    queryKey: ["engagement", id],
    queryFn: () => fetchEngagement(Number(id)),
    enabled: !!id,
  });

  const handleGenerateCode = async () => {
    if (!id) return;
    const result = await generateJoinCode(Number(id));
    navigator.clipboard.writeText(result.code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  if (isLoading) {
    return (
      <div className="max-w-6xl mx-auto p-6">
        <p className="text-text-muted">Loading...</p>
      </div>
    );
  }

  if (!engagement) {
    return (
      <div className="max-w-6xl mx-auto p-6">
        <p className="text-text-muted">Engagement not found</p>
      </div>
    );
  }

  const isLeader = engagement.role === "LEADER";

  return (
    <div className="max-w-6xl mx-auto p-6">
      <button
        onClick={() => navigate("/")}
        className="text-text-muted text-sm mb-4 hover:text-text-strong"
      >
        ← Back to dashboard
      </button>

      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold text-text-strong">
            {engagement.name}
          </h1>
          <p className="text-text-muted text-sm">
            {isLeader ? "You are the leader" : "You are a member"}
          </p>
        </div>
        {isLeader && (
          <button
            onClick={handleGenerateCode}
            className="bg-primary text-white rounded px-4 py-2 text-sm font-medium hover:bg-primary-hover"
          >
            Generate join code
          </button>
        )}
      </div>

      {copied && (
        <p className="text-primary text-sm mb-4">
          Join code copied to clipboard!
        </p>
      )}

      <div className="bg-bg-card rounded-lg border border-border-subtle p-6 mb-6">
        <h2 className="font-medium text-text-strong mb-2">Description</h2>
        <p className="text-text-body whitespace-pre-wrap">{engagement.description}</p>
      </div>

      <div className="bg-bg-card rounded-lg border border-border-subtle p-6">
        <h2 className="font-medium text-text-strong mb-3">Team</h2>
        <ul className="divide-y divide-border-subtle">
          {engagement.members.map((m) => (
            <li key={m.id} className="py-2 flex items-center justify-between">
              <div>
                <span className="text-text-strong">{m.displayName}</span>
                <span className="text-text-muted text-sm ml-2">{m.email}</span>
              </div>
              <span
                className={`text-xs px-2 py-1 rounded font-mono ${
                  m.role === "LEADER"
                    ? "bg-primary-soft text-primary"
                    : "bg-bg-inset text-text-muted"
                }`}
              >
                {m.role}
              </span>
            </li>
          ))}
        </ul>
      </div>

      <p className="text-text-muted text-sm mt-6">
        Logs board coming in Slice 6 · Engagement editing in Slice 5
      </p>
    </div>
  );
}
