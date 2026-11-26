import { ReactNode, useState, useEffect, useRef } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useCurrentUser, useLogout } from "../hooks/useAuth";
import { useEngagements } from "../hooks/useEngagements";

interface AppShellProps {
  children: ReactNode;
}

function pageTitle(pathname: string): string {
  if (pathname === "/") return "Dashboard";
  if (pathname === "/engagements") return "Manage Engagements";
  if (pathname === "/settings") return "Settings";
  if (pathname.includes("/logs/new")) return "New Log";
  if (pathname.includes("/logs/") && pathname.includes("/edit")) return "Edit Log";
  if (pathname.includes("/logs/")) return "Log Detail";
  if (pathname.includes("/logs")) return "Activity Logs";
  if (pathname.includes("/engagements/")) return "Engagement";
  return "PLogIt";
}

export default function AppShell({ children }: AppShellProps) {
  const { data: user } = useCurrentUser();
  const logout = useLogout();
  const navigate = useNavigate();
  const location = useLocation();
  const { data: engagements } = useEngagements();
  const [mobileOpen, setMobileOpen] = useState(false);
  const mobileToggle = useRef<HTMLButtonElement>(null);
  const sidebarHome = useRef<HTMLButtonElement>(null);
  const pageHeading = useRef<HTMLHeadingElement>(null);

  useEffect(() => {
    setMobileOpen(false);
  }, [location.pathname]);

  useEffect(() => {
    if (mobileOpen) sidebarHome.current?.focus();
  }, [mobileOpen]);

  const closeMobileNavigation = () => {
    setMobileOpen(false);
    mobileToggle.current?.focus();
  };

  const navigateFromSidebar = (path: string) => {
    if (mobileOpen) {
      setMobileOpen(false);
      pageHeading.current?.focus();
    }
    navigate(path);
  };

  const handleLogout = () => {
    logout.mutate(undefined, {
      onSettled: () => navigate("/login"),
    });
  };

  const currentEngagement = (() => {
    const match = location.pathname.match(/^\/engagements\/(\d+)/);
    if (!match) return null;
    const id = Number(match[1]);
    return engagements?.find((e) => e.id === id) ?? null;
  })();

  const globalNavItems = [
    { label: "Dashboard", icon: "fa-gauge-high", path: "/", active: location.pathname === "/" },
    { label: "Engagements", icon: "fa-folder-tree", path: "/engagements", active: location.pathname === "/engagements" },
    { label: "Settings", icon: "fa-gear", path: "/settings", active: location.pathname === "/settings" },
  ];

  const engagementNavItems: { label: string; icon: string; path: string; active: boolean }[] = [];
  if (currentEngagement) {
    engagementNavItems.push({
      label: currentEngagement.name,
      icon: "fa-folder",
      path: `/engagements/${currentEngagement.id}`,
      active: location.pathname === `/engagements/${currentEngagement.id}`,
    });
    engagementNavItems.push({
      label: "Activity Logs",
      icon: "fa-list-check",
      path: `/engagements/${currentEngagement.id}/logs`,
      active: location.pathname.includes("/logs"),
    });
  }

  return (
    <div className="min-h-screen flex bg-bg-app">
      {mobileOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-30 md:hidden"
          onClick={closeMobileNavigation}
        />
      )}

      <aside
        id="app-navigation"
        onKeyDown={(event) => {
          if (mobileOpen && event.key === "Escape") {
            event.preventDefault();
            closeMobileNavigation();
          }
        }}
        className={`fixed md:sticky top-0 left-0 h-screen w-64 bg-bg-header text-white flex flex-col z-40 transition-transform duration-200 ${
          mobileOpen ? "visible translate-x-0" : "invisible -translate-x-full md:visible md:translate-x-0"
        }`}
      >
        <button
          ref={sidebarHome}
          onClick={() => navigateFromSidebar("/")}
          className="flex items-center gap-3 px-5 h-16 border-b border-white/10 hover:bg-white/5 transition-colors"
        >
          <i className="fa-solid fa-shield-halved text-primary text-xl"></i>
          <div className="text-left">
            <div className="font-semibold text-lg leading-none">PLogIt</div>
            <div className="text-[10px] text-white/50 uppercase tracking-wider mt-0.5">
              Pentest Logging
            </div>
          </div>
        </button>

        <nav className="flex-1 px-3 py-4 flex flex-col gap-1 overflow-y-auto">
          <p className="px-3 text-[10px] uppercase tracking-wider text-white/40 mb-1">
            Menu
          </p>
          {globalNavItems.map((item) => (
            <button
              key={item.path}
              onClick={() => navigateFromSidebar(item.path)}
              aria-current={item.active ? "page" : undefined}
              className={`flex items-center gap-3 px-3 py-2 rounded text-sm transition-colors ${
                item.active
                  ? "bg-primary text-white font-medium"
                  : "text-white/70 hover:bg-white/10 hover:text-white"
              }`}
            >
              <i className={`fa-solid ${item.icon} w-4 text-center`}></i>
              <span className="truncate">{item.label}</span>
            </button>
          ))}

          {engagementNavItems.length > 0 && (
            <>
              <p className="px-3 text-[10px] uppercase tracking-wider text-white/40 mt-4 mb-1">
                Current Engagement
              </p>
              {engagementNavItems.map((item) => (
                <button
                  key={item.path}
                  onClick={() => navigateFromSidebar(item.path)}
                  aria-current={item.active ? "page" : undefined}
                  className={`flex items-center gap-3 px-3 py-2 rounded text-sm transition-colors ${
                    item.active
                      ? "bg-primary text-white font-medium"
                      : "text-white/70 hover:bg-white/10 hover:text-white"
                  }`}
                >
                  <i className={`fa-solid ${item.icon} w-4 text-center`}></i>
                  <span className="truncate">{item.label}</span>
                </button>
              ))}
            </>
          )}
        </nav>

        <div className="px-3 py-4 border-t border-white/10 flex flex-col gap-1">

          {user && (
            <>
              <div className="flex items-center gap-3 px-3 py-2 rounded text-sm text-white/70">
                <div className="w-7 h-7 rounded-full bg-primary flex items-center justify-center text-xs font-semibold shrink-0">
                  {(user.displayName || "?").charAt(0).toUpperCase()}
                </div>
                <span className="truncate">{user.displayName}</span>
              </div>
              <button
                onClick={handleLogout}
                disabled={logout.isPending}
                className="flex items-center gap-3 px-3 py-2 rounded text-sm text-white/70 hover:bg-danger hover:text-white transition-colors w-full disabled:opacity-50"
              >
                <i className="fa-solid fa-right-from-bracket w-4 text-center"></i>
                <span>Sign out</span>
              </button>
            </>
          )}
        </div>
      </aside>

      <div className="flex-1 flex flex-col min-w-0">
        <header className="sticky top-0 z-20 bg-bg-canvas border-b border-border-subtle h-16 flex items-center px-4 md:px-6 justify-between">
          <div className="flex items-center gap-3 min-w-0">
            <button
              ref={mobileToggle}
              type="button"
              aria-label="Toggle navigation"
              aria-expanded={mobileOpen}
              aria-controls="app-navigation"
              onClick={() => setMobileOpen((open) => !open)}
              className="md:hidden text-text-muted hover:text-text-strong p-1"
            >
              <i className="fa-solid fa-bars text-lg"></i>
            </button>
            <h1 ref={pageHeading} tabIndex={-1} className="text-lg font-semibold text-text-strong truncate">
              {pageTitle(location.pathname)}
            </h1>
          </div>
          <div className="hidden sm:flex items-center gap-2 text-sm text-text-muted">
            <i className="fa-regular fa-clock"></i>
            <span>
              {new Date().toLocaleDateString(undefined, {
                weekday: "short",
                month: "short",
                day: "numeric",
              })}
            </span>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto">{children}</main>
      </div>
    </div>
  );
}
