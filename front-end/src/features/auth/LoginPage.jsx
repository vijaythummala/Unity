import { useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../../services/authService";
import "../../css/LoginPage.css";

const LoginPage = () => {
  const [credentials, setCredentials] = useState({ username: "", password: "" });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const response = await authService.login(credentials);
      const user = response.data;
      if (!user || !user.id || !user.username) {
        throw new Error("Invalid response from server");
      }

       localStorage.setItem("userId", user.id);
       localStorage.setItem("username", user.username);
       localStorage.setItem("userFirstName",user.firstName);
       localStorage.setItem("userRole",user.role);

      if (user.role === "ADMIN") {
        navigate("/admin-dashboard");
      } else {
        navigate("/dashboard");
      }
    } catch (err) {
      const errorMessage =
        err.response?.data?.message ||
        err.response?.data ||
        "Invalid login credentials.";
      setError(errorMessage);
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <h2 className="login-heading">Sign in to Unity Bank</h2>

        {error && <p className="login-error">{error}</p>}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="form-label">Username</label>
            <input
              type="text"
              name="username"
              value={credentials.username}
              onChange={handleChange}
              className="login-input"
              required
            />
          </div>

          <div>
            <label className="form-label">Password</label>
            <input
              type="password"
              name="password"
              value={credentials.password}
              onChange={handleChange}
              className="login-input"
              required
            />
          </div>

          <button type="submit" className="login-btn">
            Login
          </button>
        </form>

        <div className="text-center mt-3">
          <p className="text-sm">
            Don't have an account?{" "}
            <span onClick={() => navigate("/register")} className="login-link">
              Create an account
            </span>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
