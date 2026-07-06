import { ReactNode, useState, useEffect } from "react";

interface AppShellProps {
  children: ReactNode;
}

export default function AppShell({ children }: AppShellProps) {
  const [dark, setDark] = useState(
    document.documentElement.classList.contains("dark")
  );

  useEffect(() => {
    localStorage.setItem("plogit-theme", dark ? "dark" : "light");
    document.documentElement.classList.toggle("dark", dark);
  }, [dark]);

  return (
    <div className="min-h-screen flex flex-col">
      <header className="bg-header text-white h-14 flex items-center px-4 justify-between">
        <div className="flex items-center gap-2">
          <span className="font-semibold text-lg">PLogIt</span>
        </div>
        <button
          onClick={() => setDark(!dark)}
          className="text-sm border border-white/20 rounded px-2 py-1 hover:bg-white/10"
        >
          {dark ? "Light" : "Dark"}
        </button>
      </header>
      <main className="flex-1 bg-bg-app">{children}</main>
    </div>
  );
}
