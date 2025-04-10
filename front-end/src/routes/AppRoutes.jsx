import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from "../components/Navbar"; // Adjust path as needed
import Transactions from "../features/transactions/TransactionsPage.jsx"; // Adjust path as needed
import LoginPage from "../features/auth/LoginPage.jsx";
import UserDashboardPage from "../features/dashboard/UserDashboard.jsx";
import RegisterPage from "../features/auth/RegisterPage.jsx";
import AdminDashboardPage from "../features/dashboard/AdminDashboard.jsx";
// Add other page components as needed
// import Payments from "../components/Payments";
// import Transfers from "../components/Transfers";
// import Profile from "../components/Profile";
// import Notifications from "../components/Notifications";

const AppRoutes = () => {
  return (
    <Router>
      {/*  Add Navbar here to appear on all pages */}
      <Navbar />
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<LoginPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Protected routes */}
        <Route path="/dashboard" element={<UserDashboardPage />} />
        <Route path="/admin-dashboard" element={<AdminDashboardPage />} />
        <Route path="/transactions" element={<Transactions />} />
        
        {/* Add other routes from your Navbar */}
        {/* <Route path="/payments" element={<Payments />} />
        <Route path="/transfers" element={<Transfers />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/notifications" element={<Notifications />} /> */}
        {/* Note: /admin route from Navbar is mapped to /admin-dashboard */}
      </Routes>
    </Router>
  );
};

export default AppRoutes;