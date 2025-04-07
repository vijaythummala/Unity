// src/AdminDashboardPage.jsx
import React, { useEffect, useState } from 'react';
import dashboardService from '../../services/dashboardService';
import {
  Button,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  CircularProgress,
  Box,
  Card,
  CardContent,
  Checkbox,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
} from '@mui/material';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { styled } from '@mui/material/styles';
import PeopleIcon from '@mui/icons-material/People';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';

const theme = createTheme({
  palette: {
    primary: { main: '#1976d2' },
    secondary: { main: '#dc3545' },
    success: { main: '#28a745' },
    warning: { main: '#ffa726' },
  },
  typography: {
    h1: { fontSize: '2rem', fontWeight: 600 },
    h2: { fontSize: '1.5rem', fontWeight: 500 },
  },
});

const KPICard = styled(Card)(({ theme }) => ({
  flex: 1,
  background: 'linear-gradient(135deg, #ffffff 0%, #f5f7fa 100%)',
  borderRadius: '12px',
  boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
  transition: 'transform 0.2s ease-in-out',
  '&:hover': {
    transform: 'scale(1.03)',
  },
}));

// Styled component for the notification message
const NotificationPopup = styled(Box)(({ theme, isError }) => ({
  position: 'fixed',
  top: '20px',
  left: '50%',
  transform: 'translateX(-50%)',
  backgroundColor: isError ? '#f8d7da' : '#d4edda',
  color: isError ? '#721c24' : '#155724',
  padding: '10px 20px',
  borderRadius: '5px',
  boxShadow: '0 2px 5px rgba(0,0,0,0.2)',
  zIndex: 1000,
  animation: 'fadeInOut 2s ease-in-out',
  '@keyframes fadeInOut': {
    '0%': { opacity: 0 },
    '10%': { opacity: 1 },
    '90%': { opacity: 1 },
    '100%': { opacity: 0 },
  },
}));

const AdminDashboardPage = () => {
  const adminId = localStorage.getItem('userId');
  const userRole = localStorage.getItem('userRole');

  const [allUsers, setAllUsers] = useState([]);
  const [pendingRequests, setPendingRequests] = useState([]);
  const [allRequests, setAllRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedUsers, setSelectedUsers] = useState([]);
  const [selectedRequests, setSelectedRequests] = useState([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [actionType, setActionType] = useState(null);
  const [actionIds, setActionIds] = useState([]);
  const [action, setAction] = useState('');
  const [comment, setComment] = useState('');
  const [processing, setProcessing] = useState(false); // Loader state
  const [message, setMessage] = useState({ text: '', isError: false }); // Response message

  useEffect(() => {
    if (!adminId) {
      console.warn('No user ID available in localStorage; skipping data fetch');
      setLoading(false);
      return;
    }

    if (userRole !== 'ADMIN') {
      setLoading(false);
      return;
    }

    const fetchData = async () => {
      try {
        const [users] = await Promise.all([
          dashboardService.getAllUsers(),
          // dashboardService.getPendingAccountRequests(),
          // dashboardService.getAllAccountRequests(),
        ]);
        console.log('Fetched all users:', users);
        setAllUsers(Array.isArray(users) ? users : []);
      } catch (err) {
        console.error('Error fetching dashboard data:', err);
        setAllUsers([]);
        setPendingRequests([]);
        setAllRequests([]);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [adminId, userRole]);

  useEffect(() => {
    if (message.text) {
      const timer = setTimeout(() => setMessage({ text: '', isError: false }), 2000);
      return () => clearTimeout(timer);
    }
  }, [message]);

  const handleAction = async (type, ids, action, comment = '') => {
    if (!adminId || userRole !== 'ADMIN') {
      alert('You must be logged in as an admin to perform this action');
      return;
    }

    setProcessing(true); // Show loader
    try {
      if (type === 'user') {
        await dashboardService.actionOnUser(ids, adminId, action, comment);
        setAllUsers(allUsers.filter(user => !ids.includes(user.id)));
        setSelectedUsers([]);
        setMessage({ text: `${action === 'APPROVE' ? 'Approved' : 'Rejected'} user(s) successfully`, isError: false });
      } else if (type === 'account-request') {
        await dashboardService.actionOnAccountRequest(ids, action, comment);
        setPendingRequests(pendingRequests.filter(req => req.request_id !== ids));
        setMessage({ text: `${action === 'APPROVE' ? 'Approved' : 'Rejected'} account request(s) successfully`, isError: false });
      }
    } catch (err) {
      console.error(`Error processing ${type} action:`, err);
      setMessage({ text: `Failed to ${action.toLowerCase()} ${type === 'user' ? 'user(s)' : 'account request(s)'}: ${err.message}`, isError: true });
    } finally {
      setProcessing(false); // Hide loader
    }
  };

  const handleOpenDialog = (type, ids, action) => {
    setActionType(type);
    setActionIds(Array.isArray(ids) ? ids : [ids]);
    setAction(action);
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setComment('');
    setAction('');
    setActionIds([]);
  };

  const handleSubmitAction = () => {
    handleAction(actionType, actionIds, action, comment);
    handleCloseDialog();
  };

  const handleBulkAction = (type, action) => {
    const selected = type === 'user' ? selectedUsers : selectedRequests;
    if (selected.length > 0) {
      handleOpenDialog(type, selected, action);
    }
  };

  const handleSelectAll = (type, checked) => {
    if (type === 'user') {
      const pendingUserIds = allUsers.filter(user => user.status === 'PENDING').map(user => user.id);
      setSelectedUsers(checked ? pendingUserIds : []);
    } else {
      setSelectedRequests(checked ? pendingRequests.map(req => req.request_id) : []);
    }
  };

  const handleSelectItem = (type, id) => {
    if (type === 'user') {
      setSelectedUsers(prev =>
        prev.includes(id) ? prev.filter(item => item !== id) : [...prev, id]
      );
    } else {
      setSelectedRequests(prev =>
        prev.includes(id) ? prev.filter(item => item !== id) : [...prev, id]
      );
    }
  };

  if (!adminId) {
    return (
      <Box sx={{ maxWidth: 1200, mx: 'auto', p: 3 }}>
        <Typography variant="h1" align="center" gutterBottom>
          Admin Dashboard
        </Typography>
        <Typography variant="h6" color="error" align="center">
          Please log in as an admin to view this dashboard.
        </Typography>
      </Box>
    );
  }

  if (userRole !== 'ADMIN') {
    return (
      <Box sx={{ maxWidth: 1200, mx: 'auto', p: 3 }}>
        <Typography variant="h1" align="center" gutterBottom>
          Admin Dashboard
        </Typography>
        <Typography variant="h6" color="error" align="center">
          Access Denied: You do not have permission to view this dashboard.
        </Typography>
      </Box>
    );
  }

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 5 }}>
        <CircularProgress />
      </Box>
    );
  }

  const pendingUsers = allUsers.filter(user => user.status === 'PENDING');
  const userKpis = {
    pending: allUsers.filter(u => u.status === 'PENDING').length,
    approved: allUsers.filter(u => u.status === 'APPROVED').length,
    rejected: allUsers.filter(u => u.status === 'REJECTED').length,
  };

  const requestKpis = {
    pending: allRequests.filter(r => r.status === 'PENDING').length,
    approved: allRequests.filter(r => r.status === 'APPROVED').length,
    rejected: allRequests.filter(r => r.status === 'REJECTED').length,
  };

  const isBulkSelected = (type) => (type === 'user' ? selectedUsers.length > 0 : selectedRequests.length > 0);

  return (
    <ThemeProvider theme={theme}>
      <Box sx={{ maxWidth: 1200, mx: 'auto', p: 3, position: 'relative' }}>
        {/* Notification Message */}
        {message.text && (
          <NotificationPopup isError={message.isError}>
            {message.text}
          </NotificationPopup>
        )}

        {/* Processing Loader */}
        {processing && (
          <Box
            sx={{
              position: 'fixed',
              top: 0,
              left: 0,
              width: '100%',
              height: '100%',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              backgroundColor: 'rgba(0, 0, 0, 0.5)',
              zIndex: 2000,
            }}
          >
            <CircularProgress size={60} />
          </Box>
        )}

        <Typography variant="h1" align="center" gutterBottom>
          Admin Dashboard
        </Typography>
        {/* KPI Cards */}
        <Box sx={{ display: 'flex', gap: 2, mb: 4 }}>
          <KPICard>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <PeopleIcon sx={{ fontSize: 40, color: 'primary.main' }} />
              <Box>
                <Typography variant="h6" color="primary">Users</Typography>
                <Typography sx={{ color: 'warning.main' }}>
                  Pending: {userKpis.pending}
                </Typography>
                <Typography sx={{ color: 'success.main' }}>
                  Approved: {userKpis.approved}
                </Typography>
                <Typography sx={{ color: 'secondary.main' }}>
                  Rejected: {userKpis.rejected}
                </Typography>
              </Box>
            </CardContent>
          </KPICard>
          <KPICard>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <AccountBalanceIcon sx={{ fontSize: 40, color: 'primary.main' }} />
              <Box>
                <Typography variant="h6" color="primary">Account Requests</Typography>
                <Typography sx={{ color: 'warning.main' }}>
                  Pending: {requestKpis.pending}
                </Typography>
                <Typography sx={{ color: 'success.main' }}>
                  Approved: {requestKpis.approved}
                </Typography>
                <Typography sx={{ color: 'secondary.main' }}>
                  Rejected: {requestKpis.rejected}
                </Typography>
              </Box>
            </CardContent>
          </KPICard>
        </Box>

        {/* Pending Users Section */}
        <Typography variant="h2" gutterBottom>
          Pending Users ({pendingUsers.length})
        </Typography>
        {pendingUsers.length > 0 && (
          <Box sx={{ mb: 4 }}>
            <Box sx={{ mb: 1 }}>
              <Button
                variant="contained"
                color="success"
                disabled={selectedUsers.length === 0 || processing}
                onClick={() => handleBulkAction('user', 'APPROVE')}
                sx={{ mr: 1 }}
              >
                Approve Selected
              </Button>
              <Button
                variant="contained"
                color="secondary"
                disabled={selectedUsers.length === 0 || processing}
                onClick={() => handleBulkAction('user', 'REJECT')}
              >
                Reject Selected
              </Button>
            </Box>
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell padding="checkbox">
                      <Checkbox
                        checked={selectedUsers.length === pendingUsers.length}
                        onChange={(e) => handleSelectAll('user', e.target.checked)}
                        disabled={processing}
                      />
                    </TableCell>
                    <TableCell>ID</TableCell>
                    <TableCell>Username</TableCell>
                    <TableCell>Name</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell align="center">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {pendingUsers.map(user => (
                    <TableRow key={user.id}>
                      <TableCell padding="checkbox">
                        <Checkbox
                          checked={selectedUsers.includes(user.id)}
                          onChange={() => handleSelectItem('user', user.id)}
                          disabled={processing}
                        />
                      </TableCell>
                      <TableCell>{user.id}</TableCell>
                      <TableCell>{user.username}</TableCell>
                      <TableCell>{user.first_name} {user.last_name}</TableCell>
                      <TableCell>{user.email}</TableCell>
                      <TableCell align="center">
                        <Button
                          variant="contained"
                          color="success"
                          size="small"
                          sx={{ mr: 1 }}
                          onClick={() => handleOpenDialog('user', user.id, 'APPROVE')}
                          disabled={isBulkSelected('user') || processing}
                        >
                          Approve
                        </Button>
                        <Button
                          variant="contained"
                          color="secondary"
                          size="small"
                          onClick={() => handleOpenDialog('user', user.id, 'REJECT')}
                          disabled={isBulkSelected('user') || processing}
                        >
                          Reject
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        )}

        {/* Pending Account Requests Section */}
        <Typography variant="h2" gutterBottom>
          Pending Account Requests ({pendingRequests.length})
        </Typography>
        {pendingRequests.length > 0 && (
          <Box>
            <Box sx={{ mb: 1 }}>
              <Button
                variant="contained"
                color="success"
                disabled={selectedRequests.length === 0 || processing}
                onClick={() => handleBulkAction('account-request', 'APPROVE')}
                sx={{ mr: 1 }}
              >
                Approve Selected
              </Button>
              <Button
                variant="contained"
                color="secondary"
                disabled={selectedRequests.length === 0 || processing}
                onClick={() => handleBulkAction('account-request', 'REJECT')}
              >
                Reject Selected
              </Button>
            </Box>
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell padding="checkbox">
                      <Checkbox
                        checked={selectedRequests.length === pendingRequests.length}
                        onChange={(e) => handleSelectAll('account-request', e.target.checked)}
                        disabled={processing}
                      />
                    </TableCell>
                    <TableCell>Request ID</TableCell>
                    <TableCell>User</TableCell>
                    <TableCell>Account Type</TableCell>
                    <TableCell>Request Type</TableCell>
                    <TableCell align="center">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {pendingRequests.map(req => (
                    <TableRow key={req.request_id}>
                      <TableCell padding="checkbox">
                        <Checkbox
                          checked={selectedRequests.includes(req.request_id)}
                          onChange={() => handleSelectItem('account-request', req.request_id)}
                          disabled={processing}
                        />
                      </TableCell>
                      <TableCell>{req.request_id}</TableCell>
                      <TableCell>{req.first_name} {req.last_name}</TableCell>
                      <TableCell>{req.account_type}</TableCell>
                      <TableCell>{req.request_type}</TableCell>
                      <TableCell align="center">
                        <Button
                          variant="contained"
                          color="success"
                          size="small"
                          sx={{ mr: 1 }}
                          onClick={() => handleOpenDialog('account-request', req.request_id, 'APPROVE')}
                          disabled={isBulkSelected('account-request') || processing}
                        >
                          Approve
                        </Button>
                        <Button
                          variant="contained"
                          color="secondary"
                          size="small"
                          onClick={() => handleOpenDialog('account-request', req.request_id, 'REJECT')}
                          disabled={isBulkSelected('account-request') || processing}
                        >
                          Reject
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        )}

        {/* Action Comment Dialog */}
        <Dialog open={openDialog} onClose={handleCloseDialog}>
          <DialogTitle>{action === 'APPROVE' ? 'Approve' : 'Reject'} {actionType === 'user' ? 'User(s)' : 'Account Request'}</DialogTitle>
          <DialogContent>
            <TextField
              label="Comment"
              fullWidth
              multiline
              rows={4}
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              sx={{ mt: 1 }}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDialog} color="primary" disabled={processing}>
              Cancel
            </Button>
            <Button
              onClick={handleSubmitAction}
              color={action === 'APPROVE' ? 'success' : 'secondary'}
              disabled={processing}
            >
              {action === 'APPROVE' ? 'Approve' : 'Reject'}
            </Button>
          </DialogActions>
        </Dialog>
      </Box>
    </ThemeProvider>
  );
};

export default AdminDashboardPage;  