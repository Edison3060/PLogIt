import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useRegister } from "../hooks/useAuth";

const inputClass =
  "bg-bg-canvas border border-border-default rounded-lg pl-10 pr-3 py-2.5 w-full text-text-strong focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-colors";

export default function Register() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [error, setError] = useState("");
  const [dark, setDark] = useState(
    document.documentElement.classList.contains("dark")
  );

  const register = useRegister();
  const navigate = useNavigate();

  useEffect(() => {
    localStorage.setItem("plogit-theme", dark ? "dark" : "light");
    document.documentElement.classList.toggle("dark", dark);
  }, [dark]);

  const onSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    if (password.length < 8) {
      setError("Password must be at least 8 characters");
      return;
    }
    if (password !== confirmPassword) {
      setError("Passwords do not match");
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
    <div className="min-h-screen flex items-center justify-center bg-bg-app p-4 relative">
      <button
        onClick={() => setDark(!dark)}
        className="absolute top-4 right-4 w-10 h-10 rounded-full bg-bg-card border border-border-default flex items-center justify-center text-text-muted hover:text-text-strong hover:bg-bg-inset transition-colors"
        title={dark ? "Switch to light" : "Switch to dark"}
      >
        <i className={`fa-solid ${dark ? "fa-sun" : "fa-moon"}`}></i>
      </button>
      <div className="w-full max-w-md">
        <div className="flex flex-col items-center mb-6">
          <div className="w-16 h-16 rounded-2xl bg-primary flex items-center justify-center shadow-lg mb-4">
            <i className="fa-solid fa-user-plus text-white text-2xl"></i>
          </div>
          <h1 className="text-2xl font-bold text-text-strong">Create account</h1>
          <p className="text-text-muted text-sm mt-1">
            Register for a PLogIt workspace
          </p>
        </div>

        <div className="bg-bg-card rounded-xl shadow-sm border border-border-default p-6 md:p-8">
          <form onSubmit={onSubmit} className="flex flex-col gap-4">
            <label className="flex flex-col gap-1.5">
              <span className="text-sm font-medium text-text-strong">Display name</span>
              <div className="relative">
                <i className="fa-solid fa-user absolute left-3 top-1/2 -translate-y-1/2 text-text-faint text-sm pointer-events-none"></i>
                <input
                  type="text"
                  value={displayName}
                  onChange={(e) => setDisplayName(e.target.value)}
                  required
                  maxLength={100}
                  placeholder="Your name"
                  className={inputClass}
                />
              </div>
            </label>
            <label className="flex flex-col gap-1.5">
              <span className="text-sm font-medium text-text-strong">Email</span>
              <div className="relative">
                <i className="fa-solid fa-envelope absolute left-3 top-1/2 -translate-y-1/2 text-text-faint text-sm pointer-events-none"></i>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  placeholder="you@example.com"
                  className={inputClass}
                />
              </div>
            </label>
            <label className="flex flex-col gap-1.5">
              <span className="text-sm font-medium text-text-strong">Password</span>
              <div className="relative">
                <i className="fa-solid fa-key absolute left-3 top-1/2 -translate-y-1/2 text-text-faint text-sm pointer-events-none"></i>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  minLength={8}
                  placeholder="At least 8 characters"
                  className={inputClass}
                />
              </div>
            </label>
            <label className="flex flex-col gap-1.5">
              <span className="text-sm font-medium text-text-strong">Confirm password</span>
              <div className="relative">
                <i className="fa-solid fa-key absolute left-3 top-1/2 -translate-y-1/2 text-text-faint text-sm pointer-events-none"></i>
                <input
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                  minLength={8}
                  placeholder="Re-enter your password"
                  className={inputClass}
                />
              </div>
            </label>
            {error && (
              <div className="bg-danger-soft border border-danger/30 text-danger rounded-lg p-3 text-sm flex items-center gap-2 font-medium">
                <i className="fa-solid fa-circle-exclamation"></i>
                <span>{error}</span>
              </div>
            )}
            <button
              type="submit"
              disabled={register.isPending}
              className="bg-primary text-white rounded-lg px-4 py-2.5 font-medium hover:bg-primary-hover disabled:opacity-50 flex items-center justify-center gap-2 transition-colors mt-2"
            >
              {register.isPending ? (
                <>
                  <i className="fa-solid fa-circle-notch fa-spin"></i> Creating account...
                </>
              ) : (
                <>
                  <i className="fa-solid fa-user-plus"></i> Register
                </>
              )}
            </button>
          </form>
        </div>

        <p className="text-text-muted text-sm mt-6 text-center">
          Have an account?{" "}
          <Link to="/login" className="text-primary font-medium hover:underline">
            Sign in
          </Link>
        </p>
      </div>
    </div>
  );
}
