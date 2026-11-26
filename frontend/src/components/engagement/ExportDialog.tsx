import { useLayoutEffect, useRef } from "react";
import type { ExportFormat } from "../../lib/engagements";

interface ExportDialogProps {
  exportFormat: ExportFormat;
  includeExported: boolean;
  exporting: boolean;
  error: string;
  onClose: () => void;
  onFormatChange: (format: ExportFormat) => void;
  onIncludeExportedChange: (include: boolean) => void;
  onExport: () => void;
}

const formats: { id: ExportFormat; icon: string; description: string }[] = [
  { id: "PDF", icon: "fa-file-pdf", description: "Rendered logs and images for audit review." },
  { id: "JSON", icon: "fa-file-code", description: "Raw Markdown and attachment filenames for tool ingestion." },
  { id: "CSV", icon: "fa-file-csv", description: "One row per log for spreadsheets." },
];

const focusClass = "focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-text-strong";

export default function ExportDialog({
  exportFormat, includeExported, exporting, error, onClose, onFormatChange, onIncludeExportedChange, onExport,
}: ExportDialogProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const closeRef = useRef<HTMLButtonElement>(null);

  useLayoutEffect(() => {
    const dialog = dialogRef.current;
    if (!dialog) return;
    const opener = document.activeElement;
    dialog.showModal();
    closeRef.current?.focus();
    return () => {
      dialog.close();
      if (opener instanceof HTMLElement) opener.focus();
    };
  }, []);

  return (
    <dialog
      ref={dialogRef}
      aria-labelledby="export-dialog-title"
      onCancel={(event) => {
        event.preventDefault();
        if (!exporting) onClose();
      }}
      onClick={(event) => {
        if (event.target === event.currentTarget && !exporting) onClose();
      }}
      className="m-auto max-h-[calc(100dvh-2rem)] w-[calc(100%-2rem)] max-w-md overflow-y-auto rounded-xl border border-border-default bg-bg-canvas p-0 text-[color:var(--text-body)] shadow-lg backdrop:bg-black/50"
    >
      <div className="p-4 sm:p-6">
        <div className="mb-4 flex items-center justify-between gap-3">
          <h2 id="export-dialog-title" className="flex items-center gap-2 text-lg font-semibold text-text-strong">
            <i className="fa-solid fa-file-export" aria-hidden="true"></i>
            Export evidence pack
          </h2>
          <button
            ref={closeRef}
            type="button"
            aria-label="Close export dialog"
            onClick={onClose}
            className={`rounded p-2 text-text-strong hover:bg-bg-inset ${focusClass}`}
            disabled={exporting}
          >
            <i className="fa-solid fa-xmark" aria-hidden="true"></i>
          </button>
        </div>

        <div className="flex flex-col gap-4">
          <fieldset>
            <legend className="mb-2 text-sm font-medium text-text-strong">Format</legend>
            <div className="grid grid-cols-3 gap-2">
              {formats.map(({ id, icon }) => (
                <button
                  key={id}
                  type="button"
                  onClick={() => onFormatChange(id)}
                  disabled={exporting}
                  aria-pressed={exportFormat === id}
                  className={`flex flex-col items-center gap-1 rounded-lg border px-2 py-2.5 text-sm text-text-strong ${focusClass} ${
                    exportFormat === id ? "border-border-strong bg-bg-inset font-semibold" : "border-border-default hover:bg-bg-inset"
                  }`}
                >
                  <i className={`fa-solid ${icon}`} aria-hidden="true"></i>
                  {id}
                </button>
              ))}
            </div>
            <p className="mt-2 text-sm text-[color:var(--text-body)]">
              {formats.find(({ id }) => id === exportFormat)?.description}
            </p>
          </fieldset>

          <label className="flex cursor-pointer items-start gap-2">
            <input
              type="checkbox"
              checked={includeExported}
              onChange={(e) => onIncludeExportedChange(e.target.checked)}
              disabled={exporting}
              className={`mt-1 ${focusClass}`}
            />
            <span className="text-sm text-[color:var(--text-body)]">
              Include already-exported logs
              <span className="block">Approved logs are included by default. Draft and submitted logs are excluded.</span>
            </span>
          </label>

          <p className="flex items-start gap-2 rounded-lg border border-border-default bg-bg-inset p-3 text-sm text-[color:var(--text-body)]">
            <i className="fa-solid fa-triangle-exclamation mt-0.5" aria-hidden="true"></i>
            Export marks newly included approved logs as exported and records the export in the audit trail.
          </p>

          <div role="alert">
            {error && <p className="rounded-lg border border-border-default bg-bg-inset p-3 text-sm text-text-strong">{error}</p>}
          </div>

          <div className="flex flex-wrap justify-end gap-2">
            <button
              type="button"
              onClick={onClose}
              disabled={exporting}
              className={`rounded-lg border border-border-default px-3 py-2 text-sm text-text-strong hover:bg-bg-inset ${focusClass}`}
            >
              Cancel
            </button>
            <button
              type="button"
              onClick={onExport}
              disabled={exporting}
              className={`rounded-lg bg-text-strong px-3 py-2 text-sm font-medium text-bg-canvas hover:bg-[color:var(--text-body)] disabled:opacity-50 ${focusClass}`}
            >
              {exporting ? "Generating..." : `Download ${exportFormat}`}
            </button>
          </div>
        </div>
      </div>
    </dialog>
  );
}
