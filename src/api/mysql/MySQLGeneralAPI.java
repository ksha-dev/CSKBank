package api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import api.UserAPI;
import exceptions.APIExceptionMessage;
import exceptions.AppException;
import helpers.Account;
import helpers.Transaction;
import helpers.UserRecord;
import utility.HelperUtil;
import utility.HelperUtil.TransactionType;
import utility.ValidatorUtil;

public class MySQLGeneralAPI implements UserAPI {

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
			if (!MySQLUtil.updateBalanceInAccount(transaction.getViewerAccountNumber(), transaction.getClosingBalance())) {
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

}