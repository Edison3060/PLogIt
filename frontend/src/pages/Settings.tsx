import { useState, useEffect } from "react";
import { useCurrentUser } from "../hooks/useAuth";

export default function Settings() {
  const { data: user } = useCurrentUser();
  const [dark, setDark] = useState(
    document.documentElement.classList.contains("dark")
  );

  useEffect(() => {
    localStorage.setItem("plogit-theme", dark ? "dark" : "light");
    document.documentElement.classList.toggle("dark", dark);
  }, [dark]);

  return (
    <div className="max-w-4xl mx-auto p-6 md:p-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-text-strong">Settings</h1>
        <p className="text-text-muted text-sm mt-1">
          Manage your account preferences and application settings.
        </p>
      </div>

      <div className="grid gap-6">
        <section className="bg-bg-card border border-border-default rounded-xl shadow-sm overflow-hidden">
          <div className="p-5 border-b border-border-subtle bg-bg-inset/30">
            <h2 className="text-lg font-semibold text-text-strong flex items-center gap-2">
              <i className="fa-regular fa-user text-primary"></i>
              Profile Information
            </h2>
          </div>
          <div className="p-6 flex flex-col gap-6">
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 rounded-full bg-primary-soft flex items-center justify-center text-primary text-2xl font-bold">
                {(user?.displayName || "?").charAt(0).toUpperCase()}
              </div>
              <div>
                <p className="font-semibold text-text-strong text-lg">{user?.displayName}</p>
                <p className="text-text-muted">{user?.email}</p>
              </div>
            </div>
            
            <div className="grid sm:grid-cols-2 gap-4">
              <div className="flex flex-col gap-1.5">
                <span className="text-sm font-medium text-text-strong">Display Name</span>
                <input
                  type="text"
                  readOnly
                  value={user?.displayName || ""}
                  className="bg-bg-inset border border-border-default rounded-lg px-3 py-2 text-text-muted cursor-not-allowed"
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <span className="text-sm font-medium text-text-strong">Email Address</span>
                <input
                  type="email"
                  readOnly
                  value={user?.email || ""}
                  className="bg-bg-inset border border-border-default rounded-lg px-3 py-2 text-text-muted cursor-not-allowed"
                />
              </div>
            </div>
          </div>
        </section>

        <section className="bg-bg-card border border-border-default rounded-xl shadow-sm overflow-hidden">
          <div className="p-5 border-b border-border-subtle bg-bg-inset/30">
            <h2 className="text-lg font-semibold text-text-strong flex items-center gap-2">
              <i className="fa-solid fa-wand-magic-sparkles text-primary"></i>
              Appearance
            </h2>
          </div>
          <div className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="font-medium text-text-strong">Theme Preference</h3>
                <p className="text-sm text-text-muted mt-0.5">Toggle between light and dark mode.</p>
              </div>
              <button
                onClick={() => setDark(!dark)}
                className="flex items-center gap-2 px-4 py-2 bg-bg-inset border border-border-default rounded-lg text-text-strong hover:bg-border-subtle transition-colors font-medium text-sm"
              >
                <i className={`fa-solid ${dark ? "fa-sun text-warning" : "fa-moon text-primary"} w-4 text-center`}></i>
                {dark ? "Light Mode" : "Dark Mode"}
              </button>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
