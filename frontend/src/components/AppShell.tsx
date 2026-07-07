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
      <header className="bg-header text-white h-14 flex items-center px-6 justify-between sticky top-0 z-10">
        <button
          onClick={() => navigate("/")}
          className="flex items-center gap-2 hover:opacity-90"
        >
          <i className="fa-solid fa-shield-halved text-primary"></i>
          <span className="font-semibold text-lg">PLogIt</span>
        </button>
        <div className="flex items-center gap-4">
          {user && (
            <div className="flex items-center gap-2 text-sm">
              <i className="fa-solid fa-user text-white/60"></i>
              <span className="text-white/90">{user.displayName}</span>
            </div>
          )}
          <button
            onClick={() => setDark(!dark)}
            className="text-sm border border-white/20 rounded-md w-8 h-8 flex items-center justify-center hover:bg-white/10"
            title={dark ? "Switch to light" : "Switch to dark"}
          >
            <i className={dark ? "fa-solid fa-sun" : "fa-solid fa-moon"}></i>
          </button>
          {user && (
            <button
              onClick={handleLogout}
              disabled={logout.isPending}
              className="text-sm border border-white/20 rounded-md px-3 h-8 flex items-center gap-2 hover:bg-white/10 disabled:opacity-50"
            >
              <i className="fa-solid fa-right-from-bracket"></i>
              <span>Sign out</span>
            </button>
          )}
        </div>
      </header>
      <main className="flex-1 bg-bg-app">{children}</main>
    </div>
  );
}
