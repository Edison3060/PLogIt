import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useLog, useCreateLog, useUpdateLog } from "../hooks/useLogs";
import {
  ACTIVITY_TYPES,
  OUTCOMES,
  formatActivityType,
  formatEnum,
  type LogFormData,
} from "../lib/logs";

const EMPTY_FORM: LogFormData = {
  activityType: "RECONNAISSANCE",
  title: "",
  description: "",
  result: "",
  target: "",
  toolUsed: "",
  outcome: "IN_PROGRESS",
  tags: [],
  codeBlock: "",
  codeLanguage: "",
};

const inputClass =
  "bg-bg-canvas border border-border-default rounded-lg px-3 py-2.5 text-text-strong text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-colors w-full";

const selectClass = `${inputClass} cursor-pointer`;

export default function LogForm() {
  const { id, logId } = useParams<{ id: string; logId: string }>();
  const navigate = useNavigate();
  const engagementId = Number(id);
  const isEdit = !!logId;

  const { data: existingLog } = useLog(logId);
  const createMut = useCreateLog(engagementId);
  const updateMut = useUpdateLog(engagementId);

  const [form, setForm] = useState<LogFormData>(EMPTY_FORM);
  const [tagInput, setTagInput] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (isEdit && existingLog) {
      setForm({
        activityType: existingLog.activityType,
        title: existingLog.title,
        description: existingLog.description,
        result: existingLog.result,
        target: existingLog.target || "",
        toolUsed: existingLog.toolUsed || "",
        outcome: existingLog.outcome,
        tags: existingLog.tags || [],
        codeBlock: existingLog.codeBlock || "",
        codeLanguage: existingLog.codeLanguage || "",
      });
    }
  }, [isEdit, existingLog]);

  const setField = <K extends keyof LogFormData>(
    key: K,
    value: LogFormData[K]
  ) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const addTag = () => {
    const trimmed = tagInput.trim();
    if (trimmed && !form.tags.includes(trimmed)) {
      setField("tags", [...form.tags, trimmed]);
    }
    setTagInput("");
  };

  const removeTag = (tag: string) => {
    setField(
      "tags",
      form.tags.filter((t) => t !== tag)
    );
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    const onSuccess = () => {
      navigate(`/engagements/${engagementId}/logs`);
    };
    const onError = (err: Error) => setError(err.message);

    if (isEdit && logId) {
      updateMut.mutate({ logId, data: form }, { onSuccess, onError });
    } else {
      createMut.mutate(form, { onSuccess, onError });
    }
  };

  const pending = createMut.isPending || updateMut.isPending;

  return (
    <div className="max-w-3xl mx-auto p-6 md:p-8">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-text-strong flex items-center gap-3">
          <i className={`fa-solid ${isEdit ? "fa-pen" : "fa-plus"} text-primary`}></i>
          {isEdit ? "Edit Log" : "New Log"}
        </h1>
        <p className="text-text-muted text-sm mt-1">
          {isEdit ? "Update the activity record." : "Record a new piece of engagement activity."}
        </p>
      </div>

      {error && (
        <div className="bg-danger-soft border border-danger/30 text-danger px-4 py-3 rounded-lg mb-4 flex items-center gap-2 text-sm font-medium">
          <i className="fa-solid fa-circle-exclamation"></i>
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="flex flex-col gap-5">
        <div className="bg-bg-card border border-border-default rounded-xl shadow-sm p-5 flex flex-col gap-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <label className="flex flex-col gap-1.5">
              <span className="text-sm font-medium text-text-strong">Activity type *</span>
              <select
                value={form.activityType}
                onChange={(e) => setField("activityType", e.target.value)}
                className={selectClass}
              >
                {ACTIVITY_TYPES.map((t) => (
                  <option key={t} value={t}>
                    {formatActivityType(t)}
                  </option>
                ))}
              </select>
            </label>
            <label className="flex flex-col gap-1.5">
              <span className="text-sm font-medium text-text-strong">Outcome *</span>
              <select
                value={form.outcome}
                onChange={(e) => setField("outcome", e.target.value)}
                className={selectClass}
              >
                {OUTCOMES.map((o) => (
                  <option key={o} value={o}>
                    {formatEnum(o)}
                  </option>
                ))}
              </select>
            </label>
          </div>

          <label className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">Title *</span>
            <input
              type="text"
              value={form.title}
              onChange={(e) => setField("title", e.target.value)}
              required
              maxLength={255}
              placeholder="e.g. Port scan of external perimeter"
              className={inputClass}
            />
          </label>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <label className="flex flex-col gap-1.5">
              <span className="text-sm font-medium text-text-strong">Target</span>
              <input
                type="text"
                value={form.target}
                onChange={(e) => setField("target", e.target.value)}
                placeholder="e.g. 10.0.0.5"
                className={`${inputClass} font-mono`}
              />
            </label>
            <label className="flex flex-col gap-1.5">
              <span className="text-sm font-medium text-text-strong">Tool used</span>
              <input
                type="text"
                value={form.toolUsed}
                onChange={(e) => setField("toolUsed", e.target.value)}
                placeholder="e.g. nmap, burp, sqlmap"
                className={inputClass}
              />
            </label>
          </div>
        </div>

        <div className="bg-bg-card border border-border-default rounded-xl shadow-sm p-5 flex flex-col gap-4">
          <label className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">Description *</span>
            <textarea
              value={form.description}
              onChange={(e) => setField("description", e.target.value)}
              required
              placeholder="What did you do? Markdown supported."
              className={`${inputClass} min-h-[110px] resize-y`}
            />
          </label>

          <label className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">Result *</span>
            <textarea
              value={form.result}
              onChange={(e) => setField("result", e.target.value)}
              required
              placeholder="What did you find?"
              className={`${inputClass} min-h-[110px] resize-y`}
            />
          </label>
        </div>

        <div className="bg-bg-card border border-border-default rounded-xl shadow-sm p-5 flex flex-col gap-4">
          <div className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">Tags</span>
            <div className="flex gap-2">
              <input
                type="text"
                value={tagInput}
                onChange={(e) => setTagInput(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    e.preventDefault();
                    addTag();
                  }
                }}
                placeholder="Add a tag and press Enter"
                className={inputClass}
              />
              <button
                type="button"
                onClick={addTag}
                className="bg-bg-inset border border-border-default rounded-lg px-4 py-2 text-sm text-text-strong hover:bg-border-subtle transition-colors shrink-0"
              >
                <i className="fa-solid fa-plus"></i>
              </button>
            </div>
            {form.tags.length > 0 && (
              <div className="flex flex-wrap gap-2 mt-2">
                {form.tags.map((tag) => (
                  <span
                    key={tag}
                    className="text-xs bg-primary-soft text-primary px-2.5 py-1 rounded-lg font-mono flex items-center gap-1.5"
                  >
                    {tag}
                    <button
                      type="button"
                      onClick={() => removeTag(tag)}
                      className="text-primary/60 hover:text-danger transition-colors"
                    >
                      <i className="fa-solid fa-xmark"></i>
                    </button>
                  </span>
                ))}
              </div>
            )}
          </div>

          <div className="flex flex-col gap-1.5">
            <span className="text-sm font-medium text-text-strong">Code block (optional)</span>
            <div className="grid grid-cols-1 md:grid-cols-4 gap-2">
              <input
                type="text"
                value={form.codeLanguage}
                onChange={(e) => setField("codeLanguage", e.target.value)}
                placeholder="Language (e.g. bash)"
                maxLength={40}
                className={`${inputClass} font-mono md:col-span-1`}
              />
              <textarea
                value={form.codeBlock}
                onChange={(e) => setField("codeBlock", e.target.value)}
                placeholder="Paste command output, code, etc."
                className={`${inputClass} font-mono min-h-[90px] md:col-span-3 resize-y`}
              />
            </div>
          </div>
        </div>

        <div className="flex gap-2">
          <button
            type="submit"
            disabled={pending}
            className="bg-primary text-white rounded-lg px-5 py-2.5 text-sm font-medium hover:bg-primary-hover disabled:opacity-50 flex items-center gap-2 transition-colors"
          >
            {pending ? (
              <>
                <i className="fa-solid fa-circle-notch fa-spin"></i> Saving...
              </>
            ) : (
              <>
                <i className="fa-solid fa-floppy-disk"></i>
                {isEdit ? "Save changes" : "Create log"}
              </>
            )}
          </button>
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="border border-border-default rounded-lg px-5 py-2.5 text-sm text-text-strong hover:bg-bg-inset transition-colors"
          >
            Cancel
          </button>
        </div>
      </form>

      <p className="text-text-muted text-sm mt-6 flex items-center gap-2">
        <i className="fa-solid fa-circle-info text-text-faint"></i>
        New logs save as Draft. Submit for review from the log detail page.
      </p>
    </div>
  );
}
