import type { ExportFormat } from "../../lib/engagements";

interface ExportDialogProps {
  exportFormat: ExportFormat;
  includeExported: boolean;
  exporting: boolean;
  onClose: () => void;
  onFormatChange: (format: ExportFormat) => void;
  onIncludeExportedChange: (include: boolean) => void;
  onExport: () => void;
}

export default function ExportDialog({ exportFormat, includeExported, exporting, onClose, onFormatChange, onIncludeExportedChange, onExport }: ExportDialogProps) {
  return (
        <div
          className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
          onClick={() => !exporting && onClose()}
        >
          <div
            className="bg-bg-canvas rounded-xl border border-border-default shadow-lg p-6 max-w-md w-full"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-text-strong flex items-center gap-2">
                <i className="fa-solid fa-file-export text-primary"></i> Export evidence pack
              </h2>
              <button
                onClick={() => !exporting && onClose()}
                className="text-text-muted hover:text-text-strong"
                disabled={exporting}
              >
                <i className="fa-solid fa-xmark"></i>
              </button>
            </div>

            <div className="flex flex-col gap-4">
              <div>
                <p className="text-sm text-text-strong mb-2 font-medium">Format</p>
                <div className="grid grid-cols-3 gap-2">
                  {(["PDF", "JSON", "CSV"] as ExportFormat[]).map((f) => (
                    <button
                      key={f}
                      onClick={() => onFormatChange(f)}
                      disabled={exporting}
                      className={`border rounded-lg px-3 py-2.5 text-sm flex flex-col items-center gap-1 transition-colors ${
                        exportFormat === f
                          ? "border-primary bg-primary-soft text-primary"
                          : "border-border-default text-text-muted hover:bg-bg-inset"
                      }`}
                    >
                      <i className={
                        f === "PDF" ? "fa-solid fa-file-pdf" :
                        f === "JSON" ? "fa-solid fa-file-code" :
                        "fa-solid fa-file-csv"
                      }></i>
                      {f}
                    </button>
                  ))}
                </div>
                <p className="text-xs text-text-muted mt-2">
                  {exportFormat === "PDF" && "Auditor-facing evidence pack with rendered logs and images."}
                  {exportFormat === "JSON" && "Machine-readable export for tool ingestion (raw markdown)."}
                  {exportFormat === "CSV" && "One row per log, spreadsheet-friendly."}
                </p>
              </div>

              <label className="flex items-start gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  checked={includeExported}
                  onChange={(e) => onIncludeExportedChange(e.target.checked)}
                  disabled={exporting}
                  className="mt-1"
                />
                <span className="text-sm text-text-body">
                  Include already-exported logs
                  <span className="block text-xs text-text-muted">
                    Default: approved logs only. Tick to re-include logs from a prior export.
                  </span>
                </span>
              </label>

              <div className="text-xs text-text-muted bg-warning-soft border border-warning/30 rounded-lg p-3 flex items-start gap-2">
                <i className="fa-solid fa-triangle-exclamation mt-0.5 text-warning"></i>
                <span>
                  Exporting marks included logs as EXPORTED (immutable). This is recorded in the audit trail.
                </span>
              </div>

              <div className="flex gap-2 justify-end">
                <button
                  onClick={() => onClose()}
                  disabled={exporting}
                  className="border border-border-default rounded-lg px-4 py-2 text-sm hover:bg-bg-inset transition-colors"
                >
                  Cancel
                </button>
                <button
                  onClick={onExport}
                  disabled={exporting}
                  className="bg-primary text-white rounded-lg px-4 py-2 text-sm font-medium hover:bg-primary-hover disabled:opacity-50 flex items-center gap-2 transition-colors"
                >
                  {exporting ? (
                    <><i className="fa-solid fa-circle-notch fa-spin"></i> Generating...</>
                  ) : (
                    <><i className="fa-solid fa-download"></i> Download {exportFormat}</>
                  )}
                </button>
              </div>
            </div>
          </div>
        </div>

  );
}
