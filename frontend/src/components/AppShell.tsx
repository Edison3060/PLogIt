import { ReactNode, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useCurrentUser, useLogout } from "../hooks/useAuth";

interface AppShellProps {
  children: ReactNode;
}

export default function AppShell({ children }: AppShellProps) {
  const [dark, setDark] = useState(
    document.documentElement.classList.contains("dark")
  );
  const { data: user } = useCurrentUser();
  const logout = useLogout();
  const navigate = useNavigate();

  useEffect(() => {
    localStorage.setItem("plogit-theme", dark ? "dark" : "light");
    document.documentElement.classList.toggle("dark", dark);
  }, [dark]);

  const handleLogout = () => {
    logout.mutate(undefined, {
      onSettled: () => navigate("/login"),
    });
  };

  return (
    <div className="min-h-screen flex flex-col">
      <header className="bg-header text-white h-14 flex items-center px-4 justify-between">
        <div className="flex items-center gap-2">
          <span className="font-semibold text-lg">PLogIt</span>
        </div>
        <div className="flex items-center gap-3">
          {user && (
            <span className="text-sm text-white/80">
              {user.displayName}
            </span>
          )}
          <button
            onClick={() => setDark(!dark)}
            className="text-sm border border-white/20 rounded px-2 py-1 hover:bg-white/10"
          >
            {dark ? "Light" : "Dark"}
          </button>
          {user && (
            <button
              onClick={handleLogout}
              disabled={logout.isPending}
              className="text-sm border border-white/20 rounded px-2 py-1 hover:bg-white/10 disabled:opacity-50"
            >
              {logout.isPending ? "..." : "Sign out"}
            </button>
          )}
        </div>
      </header>
      <main className="flex-1 bg-bg-app">{children}</main>
    </div>
  );
}
