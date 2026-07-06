import { useState } from "react";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const onSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError("Authentication not wired yet (Slice 3)");
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-bg-app">
      <div className="bg-bg-card rounded-lg shadow-sm p-8 w-full max-w-md border border-border-subtle">
        <h1 className="text-2xl font-semibold text-text-strong mb-1">
          PLogIt
        </h1>
        <p className="text-text-muted mb-6 text-sm">
          Sign in to your pentest logging workspace
        </p>
        <form onSubmit={onSubmit} className="flex flex-col gap-4">
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Email</span>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary"
            />
          </label>
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Password</span>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary"
            />
          </label>
          {error && (
            <p className="text-red-600 text-sm" role="alert">
              {error}
            </p>
          )}
          <button
            type="submit"
            className="bg-primary text-white rounded px-4 py-2 font-medium hover:bg-primary-hover"
          >
            Sign in
          </button>
        </form>
      </div>
    </div>
  );
}
