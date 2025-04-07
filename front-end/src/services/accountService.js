import api from "./api";

const accountService = {
  getUserAccounts: async (userId, status = "ACTIVE", page = 0, limit = 10) => {
    const response = await api.get("/account-service/accounts/getUserAccounts", {
      params: { userId, status, page, limit },
    });
    return response.data;
  },

  getUserPrimaryAccount: async (userId) => {
    const response = await api.get("/account-service/accounts/getPrimaryAccount", {
      params: { userId },
    });
    return response.data;
  },

  getUserBalance: async (userId, bankAccountId) => {
    const response = await api.get("/account-service/accounts/getBalance", {
      params: { userId, bankAccountId },
    });
    return response.data;
  },

  requestBankAccount: async (requestData) => {
    const response = await api.post("/account-service/accounts/request", requestData);
    return response.data;
  },
};

export default accountService;
