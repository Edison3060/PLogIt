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
    setTimeout(() => setCopied(false), 3000);
  };

  if (isLoading) {
    return (
      <div className="max-w-6xl mx-auto p-6 flex items-center gap-2 text-text-muted">
        <i className="fa-solid fa-circle-notch fa-spin"></i>
        <span>Loading...</span>
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
        className="text-text-muted text-sm mb-4 hover:text-text-strong flex items-center gap-2"
      >
        <i className="fa-solid fa-arrow-left"></i> Back to dashboard
      </button>

      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold text-text-strong flex items-center gap-3">
            <i className="fa-solid fa-folder text-text-muted"></i>
            {engagement.name}
          </h1>
          <p className="text-text-muted text-sm flex items-center gap-2 mt-1">
            <i
              className={
                isLeader
                  ? "fa-solid fa-crown text-amber-500"
                  : "fa-solid fa-user"
              }
            ></i>
            {isLeader ? "You are the leader" : "You are a member"}
          </p>
        </div>
        {isLeader && (
          <button
            onClick={handleGenerateCode}
            className="bg-primary text-white rounded px-4 py-2 text-sm font-medium hover:bg-primary-hover flex items-center gap-2"
          >
            <i className="fa-solid fa-ticket"></i>
            Generate join code
          </button>
        )}
      </div>

      {copied && (
        <div className="flex items-center gap-2 text-green-700 dark:text-green-400 text-sm bg-green-50 dark:bg-green-950/30 rounded p-2 mb-4">
          <i className="fa-solid fa-circle-check"></i>
          <span>Join code copied to clipboard!</span>
        </div>
      )}

      <div className="grid gap-4 md:grid-cols-2 mb-6">
        {engagement.startDate && (
          <div className="bg-bg-card rounded-lg border border-border-subtle p-4 flex items-center gap-3">
            <i className="fa-solid fa-calendar-day text-primary"></i>
            <div>
              <p className="text-text-muted text-xs">Start date</p>
              <p className="text-text-strong font-mono">{engagement.startDate}</p>
            </div>
          </div>
        )}
        {engagement.dueDate && (
          <div className="bg-bg-card rounded-lg border border-border-subtle p-4 flex items-center gap-3">
            <i className="fa-solid fa-calendar-xmark text-red-500"></i>
            <div>
              <p className="text-text-muted text-xs">Due date</p>
              <p className="text-text-strong font-mono">{engagement.dueDate}</p>
            </div>
          </div>
        )}
      </div>

      <div className="bg-bg-card rounded-lg border border-border-subtle p-6 mb-6">
        <h2 className="font-medium text-text-strong mb-2 flex items-center gap-2">
          <i className="fa-solid fa-align-left text-text-muted"></i>
          Description
        </h2>
        <p className="text-text-body whitespace-pre-wrap">{engagement.description}</p>
      </div>

      <div className="bg-bg-card rounded-lg border border-border-subtle p-6">
        <h2 className="font-medium text-text-strong mb-3 flex items-center gap-2">
          <i className="fa-solid fa-users text-text-muted"></i>
          Team ({engagement.members.length})
        </h2>
        <ul className="divide-y divide-border-subtle">
          {engagement.members.map((m) => (
            <li key={m.id} className="py-3 flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-full bg-primary-soft flex items-center justify-center">
                  <i className="fa-solid fa-user text-primary text-sm"></i>
                </div>
                <div>
                  <span className="text-text-strong">{m.displayName}</span>
                  <span className="text-text-muted text-sm ml-2">{m.email}</span>
                </div>
              </div>
              <span
                className={`text-xs px-2 py-1 rounded font-mono flex items-center gap-1 ${
                  m.role === "LEADER"
                    ? "bg-amber-100 dark:bg-amber-950/30 text-amber-700 dark:text-amber-400"
                    : "bg-bg-inset text-text-muted"
                }`}
              >
                <i
                  className={
                    m.role === "LEADER"
                      ? "fa-solid fa-crown"
                      : "fa-solid fa-user"
                  }
                ></i>
                {m.role}
              </span>
            </li>
          ))}
        </ul>
      </div>

      <p className="text-text-muted text-sm mt-6 flex items-center gap-2">
        <i className="fa-solid fa-info-circle"></i>
        Logs board coming in Slice 6 · Engagement editing in Slice 5
      </p>
    </div>
  );
}
