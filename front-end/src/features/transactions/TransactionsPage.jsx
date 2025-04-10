// Transactions.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../css/Transactions.css';
import transactionService from '../../services/transactionService';

const Transactions = () => {
  const [currentPage, setCurrentPage] = useState(0);
  const [allTransactions, setAllTransactions] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [selectedTransaction, setSelectedTransaction] = useState(null);
  const recordsPerPage = 10;

  const user = JSON.parse(localStorage.getItem("user"));
  const userId = user?.id;
  const bankAccountId = 101; // TODO: Make dynamic
  const navigate = useNavigate();

  const fetchTransactions = async () => {
    if (!userId) return;

    try {
      setLoading(true);
      const data = await transactionService.getUserTransactions(userId, bankAccountId, 0, 1000);
      setAllTransactions(data);
      setTotalPages(Math.ceil(data.length / recordsPerPage));
      setLoading(false);
    } catch (error) {
      console.error('Error fetching transactions:', error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTransactions();
  }, [userId, bankAccountId]);

  const startIndex = currentPage * recordsPerPage;
  const currentTransactions = allTransactions.slice(startIndex, startIndex + recordsPerPage);

  const handlePageChange = (pageNumber) => {
    if (pageNumber >= 0 && pageNumber < totalPages) {
      setCurrentPage(pageNumber);
    }
  };

  const handleRowClick = (transaction) => {
    setSelectedTransaction(transaction);
  };

  const handleBack = () => {
    setSelectedTransaction(null);
  };

  const handleRefresh = () => {
    fetchTransactions();
    setCurrentPage(0);
  };

  if (!userId) {
    return (
      <div className="container">
        <div className="message">Please <a href="/login">log in</a> to view transactions</div>
      </div>
    );
  }

  if (loading) return <div className="loading">Loading transactions...</div>;

  // Details View
  if (selectedTransaction) {
    return (
      <div className="container">
        <h1>Transaction Details</h1>
        <div className="transaction-details">
          <p><strong>ID:</strong> {selectedTransaction.id}</p>
          <p><strong>User ID:</strong> {selectedTransaction.userId}</p>
          <p><strong>Account ID:</strong> {selectedTransaction.bankAccountId}</p>
          <p><strong>Amount:</strong> ${selectedTransaction.amount.toFixed(2)}</p>
          <p><strong>Type:</strong> {selectedTransaction.type}</p>
          <p><strong>Status:</strong> {selectedTransaction.status}</p>
          <p><strong>Reference:</strong> {selectedTransaction.paymentReferenceId}</p>
          <p><strong>Date:</strong> {selectedTransaction.createdAt}</p>
        </div>
        <button className="back-btn" onClick={handleBack}>⬅ Back to Transactions</button>
      </div>
    );
  }

  // Grid View
  return (
    <div className="container">
      <div className="grid-header">
        <h1>💳 Transaction History</h1>
        <button className="refresh-btn" onClick={handleRefresh} title="Refresh Transactions">
          Refresh
        </button>
      </div>
      <div className="transaction-grid">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>User ID</th>
              <th>Account ID</th>
              <th>Amount</th>
              <th>Type</th>
              <th>Status</th>
              <th>Reference</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {currentTransactions.map((transaction) => (
              <tr 
                key={transaction.id} 
                onClick={() => handleRowClick(transaction)}
                className="clickable-row"
              >
                <td>{transaction.id}</td>
                <td>{transaction.userId}</td>
                <td>{transaction.bankAccountId}</td>
                <td>${transaction.amount.toFixed(2)}</td>
                <td>{transaction.type}</td>
                <td className={`status-${transaction.status.toLowerCase()}`}>
                  {transaction.status}
                </td>
                <td>{transaction.paymentReferenceId}</td>
                <td>{transaction.createdAt}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="pagination">
        <button
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage === 0}
        >
          Previous
        </button>

        {Array.from({ length: totalPages }, (_, i) => (
          <button
            key={i}
            onClick={() => handlePageChange(i)}
            className={currentPage === i ? 'active' : ''}
          >
            {i + 1}
          </button>
        ))}

        <button
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={currentPage === totalPages - 1 || totalPages === 0}
        >
          Next
        </button>
      </div>
      <div className="pagination-info">
        Showing {currentTransactions.length} of {allTransactions.length} transactions
      </div>
    </div>
  );
};

export default Transactions;