import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import EngagementDetailPage from "./pages/EngagementDetail";
import LogBoard from "./pages/LogBoard";
import LogDetailPage from "./pages/LogDetail";
import LogForm from "./pages/LogForm";
import AppShell from "./components/AppShell";
import ProtectedRoute from "./components/ProtectedRoute";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route
        path="/*"
        element={
          <ProtectedRoute>
            <AppShell>
              <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/engagements/:id" element={<EngagementDetailPage />} />
                <Route path="/engagements/:id/logs" element={<LogBoard />} />
                <Route path="/engagements/:id/logs/new" element={<LogForm />} />
                <Route path="/engagements/:id/logs/:logId" element={<LogDetailPage />} />
                <Route path="/engagements/:id/logs/:logId/edit" element={<LogForm />} />
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            </AppShell>
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}
