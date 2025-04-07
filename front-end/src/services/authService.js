import api from "./api";

const authService = {
  login: async (credentials) => {
    const response = await api.post("user-service/users/login", credentials);
    return response;
  },

  register: async (userData) => {
    const response = await api.post("user-service/users/register", userData);
    return response.data;
  },
};

export default authService;
