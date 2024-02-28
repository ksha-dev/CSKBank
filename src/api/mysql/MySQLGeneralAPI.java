package api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import api.GeneralAPI;
import exceptions.APIExceptionMessage;
import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;
import helpers.UserRecord;
import utility.SchemaUtil;
import utility.SchemaUtil.TransactionType;
import utility.ValidatorUtil;

public class MySQLGeneralAPI implements GeneralAPI {

	@Override
	public boolean authenticateUser(int userID, String password) throws AppException {
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CREDENTIAL_CHECK_PS.getQuery())) {
			statement.setInt(1, userID);
			try (ResultSet authenticationResult = statement.executeQuery()) {
				if (authenticationResult.next()) {
					if (authenticationResult.getString(2).equals(SchemaUtil.passwordHasher(password))) {
						return true;
					} else {
						throw new AppException(APIExceptionMessage.USER_AUNTHENTICATION_FAILED);
					}
				} else {
					throw new AppException(APIExceptionMessage.USER_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	private EmployeeRecord getEmployeeRecord(int userID) throws AppException {
		try (PreparedStatement employeeStatement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.EMPLOYEE_DETAILS_PS.getQuery())) {
			employeeStatement.setInt(1, userID);
			try (ResultSet employeeResult = employeeStatement.executeQuery()) {
				if (employeeResult.next()) {
					return MySQLUtil.convertToEmployeeRecord(employeeResult);
				} else {
					throw new AppException(APIExceptionMessage.EMPLOYEE_RECORD_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	private CustomerRecord getCustomerRecord(int userID) throws AppException {
		try (PreparedStatement customerStatement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CUSTOMER_DETAILS_PS.getQuery())) {
			customerStatement.setInt(1, userID);
			try (ResultSet customerResult = customerStatement.executeQuery()) {
				if (customerResult.next()) {
					return MySQLUtil.convertToCustomerRecord(customerResult);
				} else {
					throw new AppException(APIExceptionMessage.CUSTOMER_RECORD_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public UserRecord getUserDetails(int userID) throws AppException {
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.USER_DETAILS_PS.getQuery())) {
			statement.setInt(1, userID);
			try (ResultSet record = statement.executeQuery()) {
				if (record.next()) {
					UserRecord user = null;
					String type = record.getString("type");
					switch (type) {
					case "CUSTOMER":
						user = getCustomerRecord(userID);
						break;
					case "EMPLOYEE":
						user = getEmployeeRecord(userID);
						break;
					}
					return MySQLUtil.updateUserRecord(record, user);
				} else {
					throw new AppException(APIExceptionMessage.USER_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public List<Account> getAccountsOfUser(int userID) throws AppException {
		List<Account> accounts = new ArrayList<Account>();
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.USER_ACCOUNT_DETAILS_PS.getQuery())) {
			statement.setInt(1, userID);
			try (ResultSet accountRS = statement.executeQuery()) {
				while (accountRS.next()) {
					accounts.add(MySQLUtil.convertToAccount(accountRS));
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
		return accounts;
	}

	@Override
	public List<Transaction> getTransactionsOfAccount(long accountNumber) throws AppException {
		ValidatorUtil.validatePostiveNumber(accountNumber);
		List<Transaction> transactions = new ArrayList<Transaction>();
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.ACCOUNT_TRANSACTION_DETAILS_PS.getQuery())) {
			statement.setLong(1, accountNumber);
			try (ResultSet transactionRS = statement.executeQuery()) {
				while (transactionRS.next()) {
					transactions.add(MySQLUtil.convertToTransaction(transactionRS));
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
		return transactions;
	}

	@Override
	public Account getAccountDetails(long accountNumber) throws AppException {
		Account account = new Account();
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.ACCOUNT_DETAILS_PS.getQuery())) {
			statement.setLong(1, accountNumber);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					account = MySQLUtil.convertToAccount(result);
				}
			}
			return account;
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	private boolean updateBalanceInAccountRecord(long accountNumber, double balance) throws AppException {
		try (PreparedStatement updateAccountBalance = ServerConnection.getServerConnection()
				.prepareStatement("UPDATE accounts SET balance = ? WHERE account_number = ? AND status = 'ACTIVE';")) {
			updateAccountBalance.setDouble(1, balance);
			updateAccountBalance.setLong(2, accountNumber);
			int response = updateAccountBalance.executeUpdate();
			return response == 1;
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	private void createSenderTransactionRecord(Transaction transaction) throws AppException {
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CREATE_NEW_TRANSACTION_PS.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, transaction.getUserID());
			statement.setLong(2, transaction.getViewerAccountNumber());
			statement.setLong(3, transaction.getTransactedAccountNumber());
			statement.setDouble(4, transaction.getTransactedAmount());
			statement.setString(5, transaction.getTransactionType().toString());
			statement.setDouble(6, transaction.getClosingBalance());
			statement.setLong(7, System.currentTimeMillis());
			statement.setString(8, transaction.getRemarks());

			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next()) {
					transaction.setTransactionID(keys.getLong(1));
				} else {
					throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	private void createReceiverTransactionRecord(Transaction receiverTransaction) throws AppException {
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CREATE_TRANSACTION_PS.getQuery())) {
			statement.setLong(1, receiverTransaction.getTransactionID());
			statement.setInt(2, receiverTransaction.getUserID());
			statement.setLong(3, receiverTransaction.getViewerAccountNumber());
			statement.setLong(4, receiverTransaction.getTransactedAccountNumber());
			statement.setDouble(5, receiverTransaction.getTransactedAmount());
			statement.setString(6, receiverTransaction.getTransactionType().toString());
			statement.setDouble(7, receiverTransaction.getClosingBalance());
			statement.setLong(8, System.currentTimeMillis());
			statement.setString(9, receiverTransaction.getRemarks());

			int response = statement.executeUpdate();
			if (response != 1) {
				throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public long transferAmount(Transaction transaction, boolean isTransferOutsideBank) throws AppException {
		try {
			ServerConnection.startTransaction();
			double availableBalance = getBalanceInAccount(transaction.getViewerAccountNumber());

			if (availableBalance < transaction.getTransactedAmount()) {
				throw new AppException(APIExceptionMessage.INSUFFICIENT_BALANCE);
			}
			transaction.setclosingBalance(availableBalance - transaction.getTransactedAmount());

			if (!updateBalanceInAccountRecord(transaction.getViewerAccountNumber(), transaction.getClosingBalance())) {
				throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
			}
			createSenderTransactionRecord(transaction);
			long transactionId = transaction.getTransactionID();

			if (isTransferOutsideBank) {
				Account recepientAccount = getAccountDetails(transaction.getTransactedAccountNumber());
				if (!updateBalanceInAccountRecord(transaction.getTransactedAccountNumber(),
						recepientAccount.getBalance() + transaction.getTransactedAmount())) {
					throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
				}
				recepientAccount.setBalance(recepientAccount.getBalance() + transaction.getTransactedAmount());

				Transaction reverseTransactionRecord = new Transaction();
				reverseTransactionRecord.setTransactionID(transactionId);
				reverseTransactionRecord.setUserID(recepientAccount.getUserID());
				reverseTransactionRecord.setViewerAccountNumber(transaction.getTransactedAccountNumber());
				reverseTransactionRecord.setTransactedAccountNumber(transaction.getViewerAccountNumber());
				reverseTransactionRecord.setTransactionAmount(transaction.getTransactedAmount());
				reverseTransactionRecord.setTransactionType(TransactionType.CREDIT.toString());
				reverseTransactionRecord.setRemarks(transaction.getRemarks());
				reverseTransactionRecord.setclosingBalance(recepientAccount.getBalance());
				createReceiverTransactionRecord(reverseTransactionRecord);
			}
			ServerConnection.endTransaction();
			return transactionId;
		} catch (AppException e) {
			ServerConnection.reverseTransaction();
			throw e;
		}
	}

	@Override
	public double getBalanceInAccount(long accountNumber) throws AppException {
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement("SELECT balance, status FROM accounts WHERE account_number = ?;")) {
			statement.setLong(1, accountNumber);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next() && result.getString(2).equals("ACTIVE")) {
					return result.getDouble(1);
				} else {
					throw new AppException(APIExceptionMessage.BALANCE_ACQUISITION_FAILED);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}
}