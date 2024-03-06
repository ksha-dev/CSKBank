package api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.EmployeeAPI;
import api.mysql.MySQLQuery.Fields;
import api.mysql.MySQLQuery.Schemas;
import exceptions.AppException;
import exceptions.messages.APIExceptionMessage;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;
import helpers.UserRecord;
import utility.ConvertorUtil;
import utility.ConstantsUtil;
import utility.ValidatorUtil;
import utility.ConstantsUtil.AccountType;
import utility.ConstantsUtil.ModifiableField;
import utility.ConstantsUtil.Status;
import utility.ConstantsUtil.TransactionType;

public class MySQLEmployeeAPI extends MySQLUserAPI implements EmployeeAPI {

	private void createUserRecord(UserRecord user) throws AppException {
		ValidatorUtil.validateObject(user);

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.insertInto(Schemas.USERS);
		queryBuilder.insertFields(List.of(Fields.FIRST_NAME, Fields.LAST_NAME, Fields.DATE_OF_BIRTH, Fields.GENDER,
				Fields.ADDRESS, Fields.MOBILE, Fields.EMAIL));
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, user.getFirstName());
			statement.setString(2, user.getLastName());
			statement.setLong(3, user.getDateOfBirth());
			statement.setString(4, user.getGender().toString());
			statement.setString(5, user.getAddress());
			statement.setLong(6, user.getMobileNumber());
			statement.setString(7, user.getEmail());

			statement.executeUpdate();
			try (ResultSet key = statement.getGeneratedKeys()) {
				if (key.next()) {
					user.setUserId(key.getInt(1));
					createCredentialRecord(user);
				} else {
					throw new AppException(APIExceptionMessage.USER_CREATION_FAILED);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	private void createCredentialRecord(UserRecord user) throws AppException {
		ValidatorUtil.validatePositiveNumber(user.getUserId());

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.insertInto(Schemas.CREDENTIALS);
		queryBuilder.insertValuePlaceholders(2);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setInt(1, user.getUserId());
			statement.setString(2, ConvertorUtil.passwordGenerator(user));
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public boolean createCustomer(CustomerRecord customer) throws AppException {
		ValidatorUtil.validateObject(customer);
		try {
			ServerConnection.startTransaction();

			// create user record
			createUserRecord(customer);

			// create customer record
			MySQLQuery queryBuilder = new MySQLQuery();
			queryBuilder.insertInto(Schemas.CUSTOMERS);
			queryBuilder.insertValuePlaceholders(3);
			queryBuilder.end();

			PreparedStatement statement = ServerConnection.getServerConnection()
					.prepareStatement(queryBuilder.getQuery());
			statement.setInt(1, customer.getUserId());
			statement.setLong(2, customer.getAadhaarNumber());
			statement.setString(3, customer.getPanNumber());
			int response = statement.executeUpdate();
			statement.close();
			if (response == 1) {
				ServerConnection.endTransaction();
				return true;
			} else {
				throw new AppException(APIExceptionMessage.USER_CREATION_FAILED);
			}
		} catch (SQLException | AppException e) {
			ServerConnection.reverseTransaction();
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public long createAccount(int customerId, AccountType type, int branchId) throws AppException {
		ValidatorUtil.validatePositiveNumber(branchId);
		ValidatorUtil.validatePositiveNumber(customerId);

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.insertInto(Schemas.ACCOUNTS);
		queryBuilder.insertFields(List.of(Fields.USER_ID, Fields.TYPE, Fields.BRANCH_ID, Fields.OPENING_DATE));
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, customerId);
			statement.setString(2, type.toString());
			statement.setInt(3, branchId);
			statement.setLong(4, System.currentTimeMillis());

			int response = statement.executeUpdate();
			try (ResultSet key = statement.getGeneratedKeys()) {
				if (key.next() && response == 1) {
					return key.getLong(1);
				} else {
					throw new AppException(APIExceptionMessage.ACCOUNT_CREATION_FAILED);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public Map<Long, Account> viewAccountsInBranch(int branchId, int pageNumber) throws AppException {
		Map<Long, Account> accounts = new HashMap<Long, Account>();

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectField(Fields.ALL);
		queryBuilder.fromTable(Schemas.ACCOUNTS);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.BRANCH_ID);
		queryBuilder.sortField(Fields.OPENING_DATE, true);
		queryBuilder.limit(ConstantsUtil.LIST_LIMIT);
		queryBuilder.offset((pageNumber - 1) * ConstantsUtil.LIST_LIMIT);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setInt(1, branchId);
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
	public long depositAmount(long accountNumber, double amount, EmployeeRecord employee) throws AppException {
		try {
			ServerConnection.startTransaction();

			Account customerAccount = getAccountDetails(accountNumber);

			if (!MySQLAPIUtil.updateBalanceInAccount(accountNumber, customerAccount.getBalance() + amount)) {
				throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
			}

			Transaction depositTransaction = new Transaction();
			depositTransaction.setUserId(customerAccount.getUserId());
			depositTransaction.setViewerAccountNumber(accountNumber);
			depositTransaction.setTransactionAmount(amount);
			depositTransaction.setTransactionType(TransactionType.CREDIT.toString());
			depositTransaction.setclosingBalance(customerAccount.getBalance() + amount);
			depositTransaction.setRemarks(
					"Bank-Deposit/BranchId-" + employee.getBranchId() + "/EmployeeId-" + employee.getUserId());

			MySQLAPIUtil.createSenderTransactionRecord(depositTransaction);
			ServerConnection.endTransaction();
			return depositTransaction.getTransactionId();

		} catch (AppException e) {
			ServerConnection.reverseTransaction();
			throw e;
		}
	}

	@Override
	public long withdrawAmount(long accountNumber, double amount, EmployeeRecord employee) throws AppException {
		try {
			ServerConnection.startTransaction();

			Account customerAccount = getAccountDetails(accountNumber);
			if (customerAccount.getBalance() < amount) {
				throw new AppException(APIExceptionMessage.INSUFFICIENT_BALANCE);
			}

			if (!MySQLAPIUtil.updateBalanceInAccount(accountNumber, customerAccount.getBalance() - amount)) {
				throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
			}

			Transaction withdrawTransaction = new Transaction();
			withdrawTransaction.setUserId(customerAccount.getUserId());
			withdrawTransaction.setViewerAccountNumber(accountNumber);
			withdrawTransaction.setTransactionAmount(amount);
			withdrawTransaction.setTransactionType(TransactionType.DEBIT.toString());
			withdrawTransaction.setclosingBalance(customerAccount.getBalance() - amount);
			withdrawTransaction.setRemarks(
					"Bank-Withdrawal/BranchId-" + employee.getBranchId() + "/EmployeeId-" + employee.getUserId());
			MySQLAPIUtil.createSenderTransactionRecord(withdrawTransaction);
			ServerConnection.endTransaction();
			return withdrawTransaction.getTransactionId();

		} catch (AppException e) {
			ServerConnection.reverseTransaction();
			throw e;
		}
	}

	@Override
	public boolean changeAccountStatus(long accountNumber, Status status, int branchId, String pin)
			throws AppException {

		ValidatorUtil.validateId(branchId);
		ValidatorUtil.validateId(accountNumber);
		ValidatorUtil.validateObject(pin);
		ValidatorUtil.validateObject(status);

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.update(Schemas.ACCOUNTS);
		queryBuilder.setField(Fields.STATUS);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.ACCOUNT_NUMBER);
		queryBuilder.and();
		queryBuilder.fieldEquals(Fields.BRANCH_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setString(1, status.toString());
			statement.setLong(2, accountNumber);
			statement.setInt(3, branchId);

			int response = statement.executeUpdate();
			if (response == 1) {
				return true;
			} else {
				throw new AppException(APIExceptionMessage.STATUS_UPDATE_FAILED);
			}
		} catch (SQLException e) {
			throw new AppException(e);
		}
	}
}