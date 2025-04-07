import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../css/Navbar.css";

const Navbar = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);

  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <span className="navbar-brand">🏦 Unity Bank</span>

        <ul className="navbar-links">
          <li><Link to="/dashboard">🏠 Dashboard</Link></li>
          <li><Link to="/transactions">💳 Transactions</Link></li>
          <li><Link to="/payments">💸 Payments</Link></li>
          <li><Link to="/transfers">🔁 Transfers</Link></li>
          <li><Link to="/profile">⚙ Profile</Link></li>
          <li><Link to="/notifications">🔔 Notifications</Link></li>

          {user && user.role === "ADMIN" && (
            <li><Link to="/admin">🛠 Admin Panel</Link></li>
          )}
        </ul>

        <button className="logout-btn" onClick={handleLogout}>⏻ Logout</button>
      </div>
    </nav>
  );
};

export default Navbar;
