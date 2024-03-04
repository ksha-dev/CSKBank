package api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;

import exceptions.AppException;
import exceptions.messages.APIExceptionMessage;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;
import helpers.UserRecord;
import utility.HelperUtil;
import utility.ValidatorUtil;

class MySQLUtil {

	// CONVERSIONS
	protected static EmployeeRecord convertToEmployeeRecord(ResultSet record) throws AppException {
		ValidatorUtil.validateObject(record);
		EmployeeRecord employeeRecord = new EmployeeRecord();
		try {
			employeeRecord.setUserID(record.getInt(1));
			employeeRecord.setRole(record.getInt(2));
			employeeRecord.setBranchID(record.getInt(3));
		} catch (SQLException e) {
		}
		return employeeRecord;
	}

	protected static CustomerRecord convertToCustomerRecord(ResultSet record) throws AppException {
		ValidatorUtil.validateObject(record);
		CustomerRecord customerRecord = new CustomerRecord();
		try {
			customerRecord.setUserID(record.getInt(1));
			customerRecord.setAadhaarNumber(record.getLong(2));
			customerRecord.setPanNumber(record.getString(3));
		} catch (SQLException e) {
		}
		return customerRecord;
	}

	protected static UserRecord updateUserRecord(ResultSet record, UserRecord user) throws AppException {
		ValidatorUtil.validateObject(record);
		ValidatorUtil.validateObject(user);
		try {
			user.setUserID(record.getInt(1));
			user.setFirstName(record.getString(2));
			user.setLastName(record.getString(3));
			user.setDateOfBirth(record.getLong(4));
			user.setGender(record.getString(5));
			user.setAddress(record.getString(6));
			user.setMobileNumber(record.getLong(7));
			user.setEmail(record.getString(8));
			user.setStatus(record.getString(9));
			user.setType(record.getString(10));
		} catch (SQLException e) {
		}
		return user;
	}

	protected static Account convertToAccount(ResultSet accountRS) throws AppException {
		ValidatorUtil.validateObject(accountRS);
		Account account = new Account();
		try {
			account.setAccountNumber(accountRS.getLong(1));
			account.setUserID(accountRS.getInt(2));
			account.setType(accountRS.getString(3));
			account.setBranchID(accountRS.getInt(4));
			account.setOpeningDate(accountRS.getLong(5));
			account.setBalance(accountRS.getDouble(6));
			account.setStatus(accountRS.getString(7));
		} catch (SQLException e) {
		}
		return account;
	}

	protected static Transaction convertToTransaction(ResultSet transactionRS) throws AppException {
		ValidatorUtil.validateObject(transactionRS);
		Transaction transaction = null;
		try {
			transaction = new Transaction();
			transaction.setTransactionID(transactionRS.getLong(1));
			transaction.setUserID(transactionRS.getInt(2));
			transaction.setViewerAccountNumber(transactionRS.getLong(3));
			transaction.setTransactedAccountNumber(transactionRS.getLong(4));
			transaction.setTransactionAmount(transactionRS.getDouble(5));
			transaction.setTransactionType(transactionRS.getString(6));
			transaction.setclosingBalance(transactionRS.getDouble(7));
			transaction.setDateTime(transactionRS.getLong(8));
			transaction.setRemarks(transactionRS.getString(9));
		} catch (SQLException e) {
		}
		return transaction;
	}

	// TRANSACTION QUERIES
	protected static EmployeeRecord getEmployeeRecord(int userID) throws AppException {
		try (PreparedStatement employeeStatement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.EMPLOYEE_DETAILS_PS.getQuery())) {
			employeeStatement.setInt(1, userID);
			try (ResultSet employeeResult = employeeStatement.executeQuery()) {
				if (employeeResult.next()) {
					return convertToEmployeeRecord(employeeResult);
				} else {
					throw new AppException(APIExceptionMessage.EMPLOYEE_RECORD_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	protected static CustomerRecord getCustomerRecord(int userID) throws AppException {
		try (PreparedStatement customerStatement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CUSTOMER_DETAILS_PS.getQuery())) {
			customerStatement.setInt(1, userID);
			try (ResultSet customerResult = customerStatement.executeQuery()) {
				if (customerResult.next()) {
					return convertToCustomerRecord(customerResult);
				} else {
					throw new AppException(APIExceptionMessage.CUSTOMER_RECORD_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	protected static boolean updateBalanceInAccount(long accountNumber, double balance) throws AppException {
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

	protected static void createSenderTransactionRecord(Transaction transaction) throws AppException {
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

	protected static void createReceiverTransactionRecord(Transaction receiverTransaction) throws AppException {
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

	
}
