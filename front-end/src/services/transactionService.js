import api from "./api";

const transactionService = {
  getUserTransactions: async (userId, bankAccountId, page = 0, limit = 3) => {
    const response = await api.get("/transaction-service/transactions/fetchTransactions", {
      params: { userId, bankAccountId, page, limit },
    });
    return response.data;
  },

  createTransaction: async (transactionData) => {
    // transactionData should include userId, bankAccountId, type, and amount
    const response = await api.post("/transaction-service/transactions/create", transactionData);
    return response.data;
  },

  internalTransfer: async (userId, fromBankAccountId, toBankAccountId, amount) => {
    const response = await api.post("/transaction-service/transactions/internalTransfer", null, {
      params: { userId, fromBankAccountId, toBankAccountId, amount },
    });
    return response.data;
  },
};

export default transactionService;
