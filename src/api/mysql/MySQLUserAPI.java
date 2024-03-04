package api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.UserAPI;
import api.mysql.MySQLSchameUtil.UserFields;
import exceptions.AppException;
import exceptions.messages.APIExceptionMessage;
import helpers.Account;
import helpers.Branch;
import helpers.Transaction;
import helpers.UserRecord;
import utility.HelperUtil;
import utility.HelperUtil.TransactionType;
import utility.ValidatorUtil;

public class MySQLUserAPI implements UserAPI {

	@Override
	public boolean authenticateUser(int userID, String password) throws AppException {
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CREDENTIAL_CHECK_PS.getQuery())) {
			statement.setInt(1, userID);
			try (ResultSet authenticationResult = statement.executeQuery()) {
				if (authenticationResult.next()) {
					if (authenticationResult.getString(2).equals(HelperUtil.passwordHasher(password))) {
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
						user = MySQLUtil.getCustomerRecord(userID);
						break;
					case "EMPLOYEE":
						user = MySQLUtil.getEmployeeRecord(userID);
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
	public Map<Long, Account> getAccountsOfUser(int userID) throws AppException {
		Map<Long, Account> accounts = new HashMap<Long, Account>();
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.USER_ACCOUNT_DETAILS_PS.getQuery())) {
			statement.setInt(1, userID);
			try (ResultSet accountRS = statement.executeQuery()) {
				while (accountRS.next()) {
					accounts.put(accountRS.getLong(1), MySQLUtil.convertToAccount(accountRS));
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
		return accounts;
	}

	@Override
	public List<Transaction> getTransactionsOfAccount(long accountNumber) throws AppException {
		ValidatorUtil.validatePositiveNumber(accountNumber);
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
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.ACCOUNT_DETAILS_PS.getQuery())) {
			statement.setLong(1, accountNumber);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					return MySQLUtil.convertToAccount(result);
				} else {
					throw new AppException(APIExceptionMessage.ACCOUNT_RECORD_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public double getBalanceInAccount(long accountNumber) throws AppException {
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.ACCOUNT_BALANCE_PS.getQuery())) {
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

	@Override
	public long transferAmount(Transaction transaction, boolean isTransferOutsideBank) throws AppException {
		try {
			ServerConnection.startTransaction();

			// check balance in sender's account
			double availableBalance = getBalanceInAccount(transaction.getViewerAccountNumber());
			if (availableBalance < transaction.getTransactedAmount()) {
				throw new AppException(APIExceptionMessage.INSUFFICIENT_BALANCE);
			}
			transaction.setclosingBalance(availableBalance - transaction.getTransactedAmount());

			// reduce balance in sender's account
			if (!MySQLUtil.updateBalanceInAccount(transaction.getViewerAccountNumber(),
					transaction.getClosingBalance())) {
				throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
			}

			// create sender transaction
			MySQLUtil.createSenderTransactionRecord(transaction);
			long transactionId = transaction.getTransactionID();

			if (isTransferOutsideBank) {

				// get the account details of receiver
				Account recepientAccount = getAccountDetails(transaction.getTransactedAccountNumber());

				// update the balance in receiver account
				if (!MySQLUtil.updateBalanceInAccount(transaction.getTransactedAccountNumber(),
						recepientAccount.getBalance() + transaction.getTransactedAmount())) {
					throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
				}
				recepientAccount.setBalance(recepientAccount.getBalance() + transaction.getTransactedAmount());

				// create receiver transaction
				Transaction reverseTransactionRecord = new Transaction();
				reverseTransactionRecord.setTransactionID(transactionId);
				reverseTransactionRecord.setUserID(recepientAccount.getUserID());
				reverseTransactionRecord.setViewerAccountNumber(transaction.getTransactedAccountNumber());
				reverseTransactionRecord.setTransactedAccountNumber(transaction.getViewerAccountNumber());
				reverseTransactionRecord.setTransactionAmount(transaction.getTransactedAmount());
				reverseTransactionRecord.setTransactionType(TransactionType.CREDIT.toString());
				reverseTransactionRecord.setRemarks(transaction.getRemarks());
				reverseTransactionRecord.setclosingBalance(recepientAccount.getBalance());
				MySQLUtil.createReceiverTransactionRecord(reverseTransactionRecord);
			}
			ServerConnection.endTransaction();
			return transactionId;
		} catch (AppException e) {
			ServerConnection.reverseTransaction();
			throw e;
		}
	}

	@Override
	public Branch getBrachDetails(int branchID) throws AppException {
		ValidatorUtil.validatePositiveNumber(branchID);

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.BRANCH_DETAILS_PS.getQuery())) {
			statement.setInt(1, branchID);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					Branch branch = new Branch(result.getInt(1));
					branch.setAddress(result.getString(2));
					branch.setPhone(result.getLong(3));
					branch.setEmail(result.getString(4));
					branch.setIFSCCode(result.getString(5));
					return branch;
				} else {
					throw new AppException(APIExceptionMessage.BRANCH_DETAILS_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			throw new AppException(APIExceptionMessage.CANNOT_FETCH_DETAILS);
		}
	}

	@Override
	public boolean updateProfile(int userID, UserFields field, Object value) throws AppException {
		ValidatorUtil.validatePositiveNumber(userID);
		ValidatorUtil.validateObject(value);
		ValidatorUtil.validateObject(field);
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(String.format(MySQLQuery.UPDATE_USER_DETAILS_PS.getQuery(), field.getName()))) {
			statement.setObject(1, value);
			statement.setInt(2, userID);
			int response = statement.executeUpdate();
			if (response == 1) {
				return true;
			} else {
				throw new AppException(APIExceptionMessage.UPDATE_FAILED);
			}
		} catch (SQLException e) {
			throw new AppException(APIExceptionMessage.CANNOT_FETCH_DETAILS);
		}
	}

	@Override
	public boolean updatePassword(int customerID, String oldPassword, String newPassword) throws AppException {
		ValidatorUtil.validatePositiveNumber(customerID);
		ValidatorUtil.validatePassword(oldPassword);
		ValidatorUtil.validatePassword(newPassword);

		if (!authenticateUser(customerID, oldPassword)) {
			throw new AppException("The current password entered is wrong. Failed to change password.");
		}

		if (newPassword.equals(oldPassword)) {
			throw new AppException("New password cannot be the same as old password.");
		}

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.UPDATE_PASSWORD_PS.getQuery())) {
			statement.setString(1, HelperUtil.passwordHasher(newPassword));
			statement.setInt(2, customerID);
			int response = statement.executeUpdate();
			if (response == 1) {
				return true;
			} else {
				throw new AppException(APIExceptionMessage.UPDATE_FAILED);
			}
		} catch (SQLException e) {
			throw new AppException(APIExceptionMessage.CANNOT_FETCH_DETAILS);
		}
	}
}