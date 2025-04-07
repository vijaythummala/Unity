// dashboardService.js
import api from "./api"; // Adjust path as needed

const dashboardService = {
  // Fetch all users from API
  getAllUsers: async () => {
    try {
      const response = await api.get("/users/getAllUsers");
      console.log('API response for all users:', response.data);
      return response.data; // Expected: array of { id, status, actionBy, comments, ... }
    } catch (error) {
      console.error('Error fetching all users:', error);
      throw error;
    }
  },

  // Fetch pending account requests from API
  getPendingAccountRequests: async () => {
    try {
      const response = await api.get("/admin/pending-account-requests");
      console.log('API response for pending account requests:', response.data);
      return response.data; // Expected: array of request objects
    } catch (error) {
      console.error('Error fetching pending account requests:', error);
      throw error;
    }
  },

  // Fetch all account requests for KPI data
  getAllAccountRequests: async () => {
    try {
      const response = await api.get("/admin/all-account-requests");
      console.log('API response for all account requests:', response.data);
      return response.data; // Expected: array of request objects with status
    } catch (error) {
      console.error('Error fetching all account requests:', error);
      throw error;
    }
  },

  // Approve or reject users (single or bulk)
  actionOnUser: async (userIds, adminId, action, comments = '') => {
    try {
      const requestBody = {
        userIds: Array.isArray(userIds) ? userIds : [userIds], // Ensure array for single or bulk
        adminId,
        action,
        comments,
      };
      const response = await api.put("/users/approveOrRejectUsers", requestBody);
      console.log(`Users ${userIds} ${action} response:`, response.data);
      return response.data; // Expected: "Users processed successfully!"
    } catch (error) {
      console.error(`Error processing users ${userIds} ${action}:`, error);
      throw error;
    }
  },

  // Action on account request (Approve/Reject) - unchanged for now
  actionOnAccountRequest: async (requestId, action, reason = '') => {
    try {
      const response = await api.post(`/admin/account-request/${requestId}/action`, { action, reason });
      console.log(`Request ${requestId} ${action} response:`, response.data);
      return response.data; // Expected: { success: true }
    } catch (error) {
      console.error(`Error processing request ${requestId} ${action}:`, error);
      throw error;
    }
  },
};

export default dashboardService;