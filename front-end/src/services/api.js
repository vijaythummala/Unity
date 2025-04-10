import axios from "axios";

const api = axios.create({
  baseURL:  "http://localhost:8080/",
  headers: { "Content-Type": "application/json" },
});

// const api = axios.create({
//   baseURL: "/api", // Use relative path for API requests
//   headers: { "Content-Type": "application/json" },
// });


export default api;