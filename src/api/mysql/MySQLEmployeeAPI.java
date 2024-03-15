package api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.EmployeeAPI;
import api.mysql.MySQLQuery.Column;
import api.mysql.MySQLQuery.Schemas;
import exceptions.AppException;
import exceptions.messages.APIExceptionMessage;
import modules.Account;
import modules.CustomerRecord;
import modules.EmployeeRecord;
import modules.Transaction;
import modules.UserRecord;
import utility.ConvertorUtil;
import utility.ConstantsUtil;
import utility.ValidatorUtil;
import utility.ConstantsUtil.AccountType;
import utility.ConstantsUtil.Status;
import utility.ConstantsUtil.TransactionType;

public class MySQLEmployeeAPI extends MySQLUserAPI implements EmployeeAPI {

	private void createCredentialRecord(UserRecord user) throws AppException {
		ValidatorUtil.validatePositiveNumber(user.getUserId());

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.insertInto(Schemas.CREDENTIALS);
		queryBuilder.insertValuePlaceholders(3);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setInt(1, user.getUserId());
			statement.setString(2, ConvertorUtil.passwordGenerator(user));
			statement.setString(3, ConvertorUtil.pinGenerator(user));
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	protected void createUserRecord(UserRecord user) throws AppException {
		ValidatorUtil.validateObject(user);

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.insertInto(Schemas.USERS);
		queryBuilder.insertColumns(List.of(Column.FIRST_NAME, Column.LAST_NAME, Column.DATE_OF_BIRTH, Column.GENDER,
				Column.ADDRESS, Column.PHONE, Column.EMAIL, Column.TYPE));
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, user.getFirstName());
			statement.setString(2, user.getLastName());
			statement.setLong(3, user.getDateOfBirth());
			statement.setString(4, user.getGender().getGenderId() + "");
			statement.setString(5, user.getAddress());
			statement.setLong(6, user.getPhone());
			statement.setString(7, user.getEmail());
			statement.setString(8, user.getType().getUserTypeId() + "");

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

	@Override
	public int createCustomer(CustomerRecord customer) throws AppException {
		try {
			ServerConnection.startTransaction();
			createUserRecord(customer);

			MySQLQuery queryBuilder = new MySQLQuery();
			queryBuilder.insertInto(Schemas.CUSTOMERS);
			queryBuilder.insertValuePlaceholders(3);
			queryBuilder.end();

			try (PreparedStatement statement = ServerConnection.getServerConnection()
					.prepareStatement(queryBuilder.getQuery())) {
				statement.setInt(1, customer.getUserId());
				statement.setLong(2, customer.getAadhaarNumber());
				statement.setString(3, customer.getPanNumber());
				int response = statement.executeUpdate();
				if (response == 1) {
					ServerConnection.endTransaction();
					return customer.getUserId();
				} else {
					throw new AppException(APIExceptionMessage.USER_CREATION_FAILED);
				}
			}
		} catch (SQLException e) {
			ServerConnection.reverseTransaction();
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public long createAccount(int customerId, AccountType type, int branchId) throws AppException {
		ValidatorUtil.validateId(branchId);
		ValidatorUtil.validateId(customerId);
		ValidatorUtil.validateObject(type);

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.insertInto(Schemas.ACCOUNTS);
		queryBuilder.insertColumns(List.of(Column.USER_ID, Column.TYPE, Column.BRANCH_ID, Column.OPENING_DATE));
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, customerId);
			statement.setString(2, type.getAccountTypeId() + "");
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
		ValidatorUtil.validateId(branchId);
		ValidatorUtil.validateId(pageNumber);
		Map<Long, Account> accounts = new HashMap<Long, Account>();

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectColumn(Column.ALL);
		queryBuilder.fromSchema(Schemas.ACCOUNTS);
		queryBuilder.where();
		queryBuilder.columnEquals(Column.BRANCH_ID);
		queryBuilder.sortField(Column.ACCOUNT_NUMBER, true);
		queryBuilder.limit(ConstantsUtil.LIST_LIMIT);
		queryBuilder.offset(ConvertorUtil.convertPageToOffset(pageNumber));
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
		ValidatorUtil.validateAmount(amount);
		ValidatorUtil.validateObject(employee);
		try {
			ServerConnection.startTransaction();
			Account customerAccount = getAccountDetails(accountNumber);

			if (!MySQLAPIUtil.updateBalanceInAccount(accountNumber, customerAccount.getBalance() + amount)) {
				throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
			}

			Transaction depositTransaction = new Transaction();
			depositTransaction.setUserId(customerAccount.getUserId());
			depositTransaction.setViewerAccountNumber(accountNumber);
			depositTransaction.setTransactedAmount(amount);
			depositTransaction.setTransactionType(TransactionType.CREDIT.getTransactionTypeId());
			depositTransaction.setClosingBalance(customerAccount.getBalance() + amount);
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
		ValidatorUtil.validateAmount(amount);
		ValidatorUtil.validateObject(employee);
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
			withdrawTransaction.setTransactedAmount(amount);
			withdrawTransaction.setTransactionType(TransactionType.DEBIT.getTransactionTypeId());
			withdrawTransaction.setClosingBalance(customerAccount.getBalance() - amount);
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

		Status currentStatus = getAccountDetails(accountNumber).getStatus();
		if (currentStatus == Status.CLOSED) {
			throw new AppException(APIExceptionMessage.CANNOT_MODIFY_STATUS);
		} else if (currentStatus == status) {
			throw new AppException(APIExceptionMessage.STATUS_ALREADY_SET);
		}

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.update(Schemas.ACCOUNTS);
		queryBuilder.setColumn(Column.STATUS);
		queryBuilder.where();
		queryBuilder.columnEquals(Column.ACCOUNT_NUMBER);
		queryBuilder.and();
		queryBuilder.columnEquals(Column.BRANCH_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setString(1, status.getStatusId() + "");
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