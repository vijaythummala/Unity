import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";
import accountService from "../../services/accountService";
import transactionService from "../../services/transactionService";
import "../../css/UserDashboard.css";

const UserDashboard = () => {
  const userId = localStorage.getItem("userId");
  const userFirstName = localStorage.getItem("userFirstName");

  const [userAccounts, setUserAccounts] = useState([]);
  const [primaryAccount, setPrimaryAccount] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [totalBalance, setTotalBalance] = useState(0.0);

  const [showRequestModal, setShowRequestModal] = useState(false);
  const [showCloseModal, setShowCloseModal] = useState(false);
  const [showWithdrawModal, setShowWithdrawModal] = useState(false);
  const [showDepositModal, setShowDepositModal] = useState(false);
  const [showTransferModal, setShowTransferModal] = useState(false);

  const [selectedAccountType, setSelectedAccountType] = useState("SAVINGS");
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [lastSelectedAccount, setLastSelectedAccount] = useState(null);
  const [transactionAmount, setTransactionAmount] = useState("");
  const [transferFrom, setTransferFrom] = useState("");
  const [transferTo, setTransferTo] = useState("");
  const [transferAmount, setTransferAmount] = useState("");

  const [notification, setNotification] = useState("");

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    if (notification) {
      const timer = setTimeout(() => setNotification(""), 2000);
      return () => clearTimeout(timer);
    }
  }, [notification]);

  const fetchData = async () => {
    try {
      const primary = await accountService.getUserPrimaryAccount(userId);
      setPrimaryAccount(primary);

      const accounts = await accountService.getUserAccounts(userId);
      setUserAccounts(accounts);

      const total = accounts.reduce((sum, acc) => sum + acc.balance, 0);
      setTotalBalance(total);

      if (primary && !primary.message) {
        const txns = await transactionService.getUserTransactions(userId, primary.id);
        setTransactions(
          txns && txns.length > 0
            ? txns.length < 3
              ? [...txns, ...Array(3 - txns.length).fill({ createdAt: "-", type: "-", amount: 0, status: "-" })]
              : txns
            : Array(3).fill({ createdAt: "-", type: "-", amount: 0, status: "-" })
        );
      } else {
        setTransactions(Array(3).fill({ createdAt: "-", type: "-", amount: 0, status: "-" }));
      }
    } catch (error) {
      console.error("Error fetching dashboard data:", error);
    }
  };

  const handleSelectAccount = async (account) => {
    if (selectedAccount && selectedAccount.id === account.id) {
      setSelectedAccount(null);
    } else {
      setSelectedAccount(account);
      setLastSelectedAccount(account);
      const txns = await transactionService.getUserTransactions(userId, account.id);
      setTransactions(
        txns && txns.length > 0
          ? txns.length < 3
            ? [...txns, ...Array(3 - txns.length).fill({ createdAt: "-", type: "-", amount: 0, status: "-" })]
            : txns
          : Array(3).fill({ createdAt: "-", type: "-", amount: 0, status: "-" })
      );
    }
  };

  const handleOpenRequestModal = () => setShowRequestModal(true);
  const handleRequestBankAccount = async (e) => {
    e.preventDefault();
    const requestBody = {
      userId: parseInt(userId, 10),
      accountType: selectedAccountType,
      requestType: "NEW_BANK_ACCOUNT",
    };
    try {
      await accountService.requestBankAccount(requestBody);
      setShowRequestModal(false);
      setNotification("Account request submitted successfully.");
      fetchData();
    } catch (error) {
      console.error("Error requesting bank account:", error);
      setShowRequestModal(false);
      setNotification("Request failed");
    }
  };

  const handleOpenCloseModal = () => setShowCloseModal(true);
  const handleCloseBankAccount = async () => {
    if (!selectedAccount) return;
    const requestBody = {
      userId: parseInt(userId, 10),
      accountType: selectedAccount.accountType,
      requestType: "CLOSE_BANK_ACCOUNT",
      bankAccountId: selectedAccount.id,
    };
    try {
      await accountService.requestBankAccount(requestBody);
      setShowCloseModal(false);
      setSelectedAccount(null);
      setNotification("Account close request submitted successfully.");
      fetchData();
    } catch (error) {
      console.error("Error closing bank account:", error);
      setShowCloseModal(false);
      setNotification("Request failed");
    }
  };

  const handleOpenWithdrawModal = () => setShowWithdrawModal(true);
  const handleSubmitWithdraw = async (e) => {
    e.preventDefault();
    const requestBody = {
      userId: parseInt(userId, 10),
      bankAccountId: selectedAccount.id,
      type: "WITHDRAW",
      amount: parseFloat(transactionAmount),
    };
    try {
      await transactionService.createTransaction(requestBody);
      setShowWithdrawModal(false);
      setTransactionAmount("");
      setNotification("Withdrawal submitted successfully.");
      fetchData();
    } catch (error) {
      console.error("Error processing withdrawal:", error);
      setShowWithdrawModal(false);
      setNotification("Request failed");
    }
  };

  const handleOpenDepositModal = () => setShowDepositModal(true);
  const handleSubmitDeposit = async (e) => {
    e.preventDefault();
    const requestBody = {
      userId: parseInt(userId, 10),
      bankAccountId: selectedAccount.id,
      type: "DEPOSIT",
      amount: parseFloat(transactionAmount),
    };
    try {
      await transactionService.createTransaction(requestBody);
      setShowDepositModal(false);
      setTransactionAmount("");
      setNotification("Deposit submitted successfully.");
      fetchData();
    } catch (error) {
      console.error("Error processing deposit:", error);
      setShowDepositModal(false);
      setNotification("Request failed");
    }
  };

  const handleOpenTransferModal = () => setShowTransferModal(true);
  const handleSubmitTransfer = async (e) => {
    e.preventDefault();
    try {
      await transactionService.internalTransfer(
        parseInt(userId, 10),
        parseInt(transferFrom, 10),
        parseInt(transferTo, 10),
        parseFloat(transferAmount)
      );
      setShowTransferModal(false);
      setTransferFrom("");
      setTransferTo("");
      setTransferAmount("");
      setNotification("Transfer submitted successfully.");
      fetchData();
    } catch (error) {
      console.error("Error processing transfer:", error);
      setShowTransferModal(false);
      setNotification("Request failed");
    }
  };

  return (
    <div className="dashboard-container">
      {/* <Navbar /> */}

      {notification && (
        <div className="notification-popup">
          {notification}
          <div className="notification-progress"></div>
        </div>
      )}

      <div className="dashboard-content">
        <h1 className="dashboard-heading">Welcome, {userFirstName || "User"} 👋</h1>

        <div className="account-summary">
          <h2>
            Total Balance: <span>${totalBalance.toFixed(2)}</span>
          </h2>
        </div>

        <div className="accounts-section">
          <div className="accounts-header">
            <h2 className="section-title">🏦 Your Accounts</h2>
            {selectedAccount ? (
              <button className="dashboard-btn" onClick={handleOpenCloseModal}>
                Close Bank Account
              </button>
            ) : (
              <button className="dashboard-btn" onClick={handleOpenRequestModal}>
                Request Bank Account
              </button>
            )}
          </div>
          {userAccounts && userAccounts.length > 0 ? (
            <div className="accounts-table-container">
              <table className="account-table">
                <thead>
                  <tr>
                    <th>Account Type</th>
                    <th>Account Number</th>
                    <th>Balance</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {userAccounts.map((acc) => {
                    const isSelected = selectedAccount && selectedAccount.id === acc.id;
                    return (
                      <tr
                        key={acc.id}
                        className={isSelected ? "selected-row" : ""}
                        onClick={() => handleSelectAccount(acc)}
                      >
                        <td>{acc.accountType}</td>
                        <td>{acc.accountNumber}</td>
                        <td>${acc.balance.toFixed(2)}</td>
                        <td>{acc.status}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="no-accounts">
              <p>No active bank accounts found.</p>
            </div>
          )}
        </div>

        <div className="transactions-section">
          <h2>📜 Recent Transactions</h2>
          <div className="transactions-table-container">
            <table className="transaction-table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Type</th>
                  <th>Amount</th>
                  <th>Status</th>
                  <th>Account</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((txn, index) => (
                  <tr key={index} className={txn.createdAt === "-" ? "placeholder-row" : ""}>
                    <td>{txn.createdAt !== "-" ? txn.createdAt.split(" ")[0] : "-"}</td>
                    <td>{txn.type}</td>
                    <td
                      className={
                        txn.createdAt !== "-" &&
                        (txn.type === "DEPOSIT" || txn.type === "INTERNAL_TRANSFER_CREDIT")
                          ? "text-green-500"
                          : txn.createdAt !== "-"
                          ? "text-red-500"
                          : ""
                      }
                    >
                      {txn.amount ? `$${txn.amount.toFixed(2)}` : "-"}
                    </td>
                    <td>{txn.status}</td>
                    <td>
                      {lastSelectedAccount
                        ? lastSelectedAccount.accountNumber
                        : primaryAccount
                        ? primaryAccount.accountNumber
                        : "-"}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="quick-actions">
          <button className="dashboard-btn" disabled={!selectedAccount} onClick={handleOpenWithdrawModal}>
            🔄 Withdraw Money
          </button>
          <button className="dashboard-btn" disabled={!selectedAccount} onClick={handleOpenDepositModal}>
            💰 Deposit Money
          </button>
          <button className="dashboard-btn" disabled={userAccounts.length < 2} onClick={handleOpenTransferModal}>
            🔁 Transfer Money
          </button>
        </div>
      </div>

      {showRequestModal && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <h2 className="modal-title">Request Bank Account</h2>
            <form onSubmit={handleRequestBankAccount}>
              <div className="mb-4">
                <label className="block font-medium mb-1">Account Type</label>
                <select className="modal-select" value={selectedAccountType} onChange={(e) => setSelectedAccountType(e.target.value)}>
                  <option value="SAVINGS">SAVINGS</option>
                  <option value="CHECKING">CHECKING</option>
                </select>
              </div>
              <div className="flex justify-end space-x-3">
                <button type="button" className="modal-cancel-btn" onClick={() => setShowRequestModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="modal-submit-btn">
                  Submit
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showCloseModal && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <h2 className="modal-title">Close Bank Account</h2>
            <p className="mb-4">Are you sure you want to close this bank account?</p>
            <div className="flex justify-end space-x-3">
              <button type="button" className="modal-cancel-btn" onClick={() => setShowCloseModal(false)}>
                Cancel
              </button>
              <button type="button" className="modal-submit-btn" onClick={handleCloseBankAccount}>
                Confirm
              </button>
            </div>
          </div>
        </div>
      )}

      {showWithdrawModal && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <h2 className="modal-title">Withdraw Money</h2>
            <form onSubmit={handleSubmitWithdraw}>
              <div className="mb-4">
                <label className="block font-medium mb-1">Amount</label>
                <input
                  type="number"
                  step="0.01"
                  className="modal-input"
                  value={transactionAmount}
                  onChange={(e) => setTransactionAmount(e.target.value)}
                  required
                />
              </div>
              <div className="flex justify-end space-x-3">
                <button type="button" className="modal-cancel-btn" onClick={() => setShowWithdrawModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="modal-submit-btn">
                  Submit
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showDepositModal && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <h2 className="modal-title">Deposit Money</h2>
            <form onSubmit={handleSubmitDeposit}>
              <div className="mb-4">
                <label className="block font-medium mb-1">Amount</label>
                <input
                  type="number"
                  step="0.01"
                  className="modal-input"
                  value={transactionAmount}
                  onChange={(e) => setTransactionAmount(e.target.value)}
                  required
                />
              </div>
              <div className="flex justify-end space-x-3">
                <button type="button" className="modal-cancel-btn" onClick={() => setShowDepositModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="modal-submit-btn">
                  Submit
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showTransferModal && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <h2 className="modal-title">Internal Transfer</h2>
            <form onSubmit={handleSubmitTransfer}>
              <div className="mb-4">
                <label className="block font-medium mb-1">From Account</label>
                <select className="modal-select" value={transferFrom} onChange={(e) => setTransferFrom(e.target.value)} required>
                  <option value="">Select account</option>
                  {userAccounts.map((acc) => (
                    <option key={acc.id} value={acc.id}>
                      {acc.accountNumber}
                    </option>
                  ))}
                </select>
              </div>
              <div className="mb-4">
                <label className="block font-medium mb-1">To Account</label>
                <select className="modal-select" value={transferTo} onChange={(e) => setTransferTo(e.target.value)} required>
                  <option value="">Select account</option>
                  {userAccounts.map((acc) => (
                    <option key={acc.id} value={acc.id}>
                      {acc.accountNumber}
                    </option>
                  ))}
                </select>
              </div>
              <div className="mb-4">
                <label className="block font-medium mb-1">Amount</label>
                <input
                  type="number"
                  step="0.01"
                  className="modal-input"
                  value={transferAmount}
                  onChange={(e) => setTransferAmount(e.target.value)}
                  required
                />
              </div>
              <div className="flex justify-end space-x-3">
                <button type="button" className="modal-cancel-btn" onClick={() => setShowTransferModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="modal-submit-btn">
                  Submit
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default UserDashboard;
