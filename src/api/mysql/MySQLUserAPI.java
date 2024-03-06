package api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.UserAPI;
import api.mysql.MySQLQuery.Fields;
import api.mysql.MySQLQuery.Schemas;
import exceptions.AppException;
import exceptions.messages.APIExceptionMessage;
import helpers.Account;
import helpers.Branch;
import helpers.Transaction;
import helpers.UserRecord;
import utility.ConvertorUtil;
import utility.ConstantsUtil;
import utility.ConstantsUtil.ModifiableField;
import utility.ConstantsUtil.TransactionHistoryLimit;
import utility.ConstantsUtil.TransactionType;
import utility.ValidatorUtil;

public class MySQLUserAPI implements UserAPI {

	@Override
	public boolean userAuthentication(int userId, String password) throws AppException {
		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectField(Fields.PASSWORD);
		queryBuilder.fromTable(Schemas.CREDENTIALS);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.USER_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setInt(1, userId);
			try (ResultSet authenticationResult = statement.executeQuery()) {
				if (authenticationResult.next()) {
					if (authenticationResult.getString(1).equals(ConvertorUtil.passwordHasher(password))) {
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
	public boolean userConfimration(int userId, String pin) throws AppException {
		ValidatorUtil.validatePositiveNumber(userId);
		ValidatorUtil.validatePIN(pin);

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectField(Fields.PIN);
		queryBuilder.fromTable(Schemas.CREDENTIALS);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.USER_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setInt(1, userId);
			try (ResultSet authenticationResult = statement.executeQuery()) {
				if (authenticationResult.next()) {
					if (authenticationResult.getString(1).equals(ConvertorUtil.passwordHasher(pin))) {
						return true;
					} else {
						throw new AppException(APIExceptionMessage.USER_CONFIRMATION_FAILED);
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
	public UserRecord getUserDetails(int userId) throws AppException {

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectField(Fields.ALL);
		queryBuilder.fromTable(Schemas.USERS);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.USER_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setInt(1, userId);
			try (ResultSet record = statement.executeQuery()) {
				if (record.next()) {
					UserRecord user = null;
					String type = record.getString("type");
					switch (type) {
					case "CUSTOMER":
						user = MySQLAPIUtil.getCustomerRecord(userId);
						break;
					case "EMPLOYEE":
						user = MySQLAPIUtil.getEmployeeRecord(userId);
						break;
					}
					return MySQLConversionUtil.updateUserRecord(record, user);
				} else {
					throw new AppException(APIExceptionMessage.USER_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public Map<Long, Account> getAccountsOfUser(int userId) throws AppException {
		Map<Long, Account> accounts = new HashMap<Long, Account>();

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectField(Fields.ALL);
		queryBuilder.fromTable(Schemas.ACCOUNTS);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.USER_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setInt(1, userId);
			try (ResultSet accountRS = statement.executeQuery()) {
				while (accountRS.next()) {
					accounts.put(accountRS.getLong(1), MySQLConversionUtil.convertToAccount(accountRS));
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
		return accounts;
	}

	@Override
	public List<Transaction> getTransactionsOfAccount(long accountNumber, int pageNumber,
			TransactionHistoryLimit timeLimit) throws AppException {
		ValidatorUtil.validatePositiveNumber(accountNumber);
		List<Transaction> transactions = new ArrayList<Transaction>();

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectField(Fields.ALL);
		queryBuilder.fromTable(Schemas.TRANSACTIONS);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.VIEWER_ACCOUNT_NUMBER);
		if (!(timeLimit == TransactionHistoryLimit.RECENT)) {
			queryBuilder.and();
			queryBuilder.fieldGreaterThan(Fields.TIME_STAMP);
		}
		queryBuilder.sortField(Fields.TRANSACTION_ID, true);
		queryBuilder.limit(ConstantsUtil.LIST_LIMIT);
		queryBuilder.offset((pageNumber - 1) * ConstantsUtil.LIST_LIMIT);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setLong(1, accountNumber);
			if (!(timeLimit == TransactionHistoryLimit.RECENT)) {
				statement.setLong(2, timeLimit.getDuration());
			}
			try (ResultSet transactionRS = statement.executeQuery()) {
				while (transactionRS.next()) {
					transactions.add(MySQLConversionUtil.convertToTransaction(transactionRS));
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
		return transactions;
	}

	@Override
	public Account getAccountDetails(long accountNumber) throws AppException {

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectField(Fields.ALL);
		queryBuilder.fromTable(Schemas.ACCOUNTS);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.ACCOUNT_NUMBER);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setLong(1, accountNumber);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					return MySQLConversionUtil.convertToAccount(result);
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
		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectField(Fields.BALANCE);
		queryBuilder.addField(Fields.STATUS);
		queryBuilder.fromTable(Schemas.ACCOUNTS);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.ACCOUNT_NUMBER);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
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
			if (!MySQLAPIUtil.updateBalanceInAccount(transaction.getViewerAccountNumber(),
					transaction.getClosingBalance())) {
				throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
			}

			// create sender transaction
			MySQLAPIUtil.createSenderTransactionRecord(transaction);
			long transactionId = transaction.getTransactionId();

			if (isTransferOutsideBank) {

				// get the account details of receiver
				Account recepientAccount = getAccountDetails(transaction.getTransactedAccountNumber());

				// update the balance in receiver account
				if (!MySQLAPIUtil.updateBalanceInAccount(transaction.getTransactedAccountNumber(),
						recepientAccount.getBalance() + transaction.getTransactedAmount())) {
					throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
				}
				recepientAccount.setBalance(recepientAccount.getBalance() + transaction.getTransactedAmount());

				// create receiver transaction
				Transaction reverseTransactionRecord = new Transaction();
				reverseTransactionRecord.setTransactionId(transactionId);
				reverseTransactionRecord.setUserId(recepientAccount.getUserId());
				reverseTransactionRecord.setViewerAccountNumber(transaction.getTransactedAccountNumber());
				reverseTransactionRecord.setTransactedAccountNumber(transaction.getViewerAccountNumber());
				reverseTransactionRecord.setTransactionAmount(transaction.getTransactedAmount());
				reverseTransactionRecord.setTransactionType(TransactionType.CREDIT.toString());
				reverseTransactionRecord.setRemarks(transaction.getRemarks());
				reverseTransactionRecord.setclosingBalance(recepientAccount.getBalance());
				MySQLAPIUtil.createReceiverTransactionRecord(reverseTransactionRecord);
			}
			ServerConnection.endTransaction();
			return transactionId;
		} catch (AppException e) {
			ServerConnection.reverseTransaction();
			throw e;
		}
	}

	@Override
	public Branch getBrachDetails(int branchId) throws AppException {
		ValidatorUtil.validatePositiveNumber(branchId);

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectField(Fields.ALL);
		queryBuilder.fromTable(Schemas.BRANCH);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.BRANCH_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setInt(1, branchId);
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
	public boolean updateProfile(int userId, ModifiableField field, Object value) throws AppException {
		ValidatorUtil.validatePositiveNumber(userId);
		ValidatorUtil.validateObject(value);
		ValidatorUtil.validateObject(field);

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.update(Schemas.USERS);
		queryBuilder.setField(Fields.valueOf(field.toString()));
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.USER_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setObject(1, value);
			statement.setInt(2, userId);
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
	public boolean updatePassword(int customerId, String oldPassword, String newPassword) throws AppException {
		ValidatorUtil.validatePositiveNumber(customerId);
		ValidatorUtil.validatePassword(oldPassword);
		ValidatorUtil.validatePassword(newPassword);

		if (!userAuthentication(customerId, oldPassword)) {
			throw new AppException("The current password entered is wrong. Failed to change password.");
		}

		if (newPassword.equals(oldPassword)) {
			throw new AppException("New password cannot be the same as old password.");
		}

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.update(Schemas.CREDENTIALS);
		queryBuilder.setField(Fields.PASSWORD);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.USER_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setString(1, ConvertorUtil.passwordHasher(newPassword));
			statement.setInt(2, customerId);
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