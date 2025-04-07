import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "../features/auth/LoginPage.jsx";
import UserDashboardPage from "../features/dashboard/UserDashboard.jsx";
import RegisterPage from "../features/auth/RegisterPage.jsx";
import AdminDashboardPage from "../features/dashboard/AdminDashboard.jsx";

const AppRoutes = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/dashboard" element={<UserDashboardPage />} />
        <Route path="/admin-dashboard" element={<AdminDashboardPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Routes>
    </Router>
  );
};

export default AppRoutes;
