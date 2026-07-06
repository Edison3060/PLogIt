import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useRegister } from "../hooks/useAuth";

export default function Register() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [error, setError] = useState("");

  const register = useRegister();
  const navigate = useNavigate();

  const onSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    if (password.length < 8) {
      setError("Password must be at least 8 characters");
      return;
    }
    register.mutate(
      { email, password, displayName },
      {
        onSuccess: () => navigate("/"),
        onError: (err: Error) => setError(err.message),
      }
    );
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-bg-app">
      <div className="bg-bg-card rounded-lg shadow-sm p-8 w-full max-w-md border border-border-subtle">
        <h1 className="text-2xl font-semibold text-text-strong mb-1">
          Create account
        </h1>
        <p className="text-text-muted mb-6 text-sm">
          Register for a PLogIt account
        </p>
        <form onSubmit={onSubmit} className="flex flex-col gap-4">
          <label className="flex flex-col gap-1">
            <span className="text-sm text-text-strong">Display name</span>
            <input
              type="text"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              required
              maxLength={100}
              className="bg-bg-canvas border border-border-default rounded px-3 py-2 text-text-strong focus:outline-none focus:border-primary"
            />
          </label>
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
              minLength={8}
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
            disabled={register.isPending}
            className="bg-primary text-white rounded px-4 py-2 font-medium hover:bg-primary-hover disabled:opacity-50"
          >
            {register.isPending ? "Creating account..." : "Register"}
          </button>
        </form>
        <p className="text-text-muted text-sm mt-4 text-center">
          Have an account? <Link to="/login" className="text-primary">Sign in</Link>
        </p>
      </div>
    </div>
  );
}
