import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useLogin } from "../hooks/useAuth";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const login = useLogin();
  const navigate = useNavigate();

  const onSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    login.mutate(
      { email, password },
      {
        onSuccess: () => navigate("/"),
        onError: (err: Error) => setError(err.message),
      }
    );
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-bg-app">
      <div className="bg-bg-card rounded-lg shadow-sm p-8 w-full max-w-md border border-border-subtle">
        <div className="text-center mb-6">
          <i className="fa-solid fa-shield-halved text-primary text-3xl mb-2"></i>
          <h1 className="text-2xl font-semibold text-text-strong">PLogIt</h1>
          <p className="text-text-muted text-sm">
            Sign in to your pentest logging workspace
          </p>
        </div>
        <form onSubmit={onSubmit} className="flex flex-col gap-4">
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Email</span>
            <div className="relative">
              <i className="fa-solid fa-envelope absolute left-3 top-1/2 -translate-y-1/2 text-text-faint text-sm"></i>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                placeholder="you@example.com"
                className="bg-bg-canvas border border-border-default rounded pl-9 pr-3 py-2 w-full text-text-strong focus:outline-none focus:border-primary"
              />
            </div>
          </label>
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Password</span>
            <div className="relative">
              <i className="fa-solid fa-key absolute left-3 top-1/2 -translate-y-1/2 text-text-faint text-sm"></i>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                placeholder="••••••••"
                className="bg-bg-canvas border border-border-default rounded pl-9 pr-3 py-2 w-full text-text-strong focus:outline-none focus:border-primary"
              />
            </div>
          </label>
          {error && (
            <div className="flex items-center gap-2 text-red-600 text-sm bg-red-50 dark:bg-red-950/30 rounded p-2">
              <i className="fa-solid fa-circle-exclamation"></i>
              <span>{error}</span>
            </div>
          )}
          <button
            type="submit"
            disabled={login.isPending}
            className="bg-primary text-white rounded px-4 py-2 font-medium hover:bg-primary-hover disabled:opacity-50 flex items-center justify-center gap-2"
          >
            {login.isPending ? (
              <>
                <i className="fa-solid fa-circle-notch fa-spin"></i> Signing in...
              </>
            ) : (
              <>
                <i className="fa-solid fa-right-to-bracket"></i> Sign in
              </>
            )}
          </button>
        </form>
        <p className="text-text-muted text-sm mt-6 text-center">
          No account?{" "}
          <Link to="/register" className="text-primary hover:underline">
            Register
          </Link>
        </p>
      </div>
    </div>
  );
}
