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
import api.mysql.MySQLQueryUtil.Fields;
import api.mysql.MySQLQueryUtil.Schemas;
import exceptions.AppException;
import exceptions.messages.APIExceptionMessage;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.Transaction;
import helpers.UserRecord;
import utility.HelperUtil;
import utility.ValidatorUtil;
import utility.HelperUtil.TransactionType;

public class MySQLEmployeeAPI extends MySQLUserAPI implements EmployeeAPI {

	private void createUserRecord(UserRecord user) throws AppException {
		ValidatorUtil.validateObject(user);

		MySQLQueryUtil queryBuilder = new MySQLQueryUtil();
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
					user.setUserID(key.getInt(1));
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
		ValidatorUtil.validatePositiveNumber(user.getUserID());

		MySQLQueryUtil queryBuilder = new MySQLQueryUtil();
		queryBuilder.insertInto(Schemas.CREDENTIALS);
		queryBuilder.insertValuePlaceholders(2);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setInt(1, user.getUserID());
			statement.setString(2, HelperUtil.passwordHasher(user.getFirstName().substring(0, 4) + "@"
					+ user.getDateOfBirthInLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE).substring(4, 8)));
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

			MySQLQueryUtil queryBuilder = new MySQLQueryUtil();
			queryBuilder.insertInto(Schemas.CUSTOMERS);
			queryBuilder.insertValuePlaceholders(3);
			queryBuilder.end();

			PreparedStatement statement = ServerConnection.getServerConnection()
					.prepareStatement(queryBuilder.getQuery());
			statement.setInt(1, customer.getUserID());
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
	public long createAccount(int customerID, String type, int branchID) throws AppException {
		ValidatorUtil.validatePositiveNumber(branchID);
		ValidatorUtil.validatePositiveNumber(customerID);

		MySQLQueryUtil queryBuilder = new MySQLQueryUtil();
		queryBuilder.insertInto(Schemas.ACCOUNTS);
		queryBuilder.insertFields(
				List.of(Fields.USER_ID, Fields.TYPE, Fields.BRANCH_ID, Fields.OPENING_DATE));
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, customerID);
			statement.setString(2, type);
			statement.setInt(3, branchID);
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
	public Map<Long, Account> viewAccountsInBranch(int branchID) throws AppException {
		Map<Long, Account> accounts = new HashMap<Long, Account>();

		MySQLQueryUtil queryBuilder = new MySQLQueryUtil();
		queryBuilder.selectField(Fields.ALL);
		queryBuilder.fromTable(Schemas.ACCOUNTS);
		queryBuilder.where(Fields.BRANCH_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setInt(1, branchID);
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
	public long depositAmount(long accountNumber, double amount) throws AppException {
		try {
			ServerConnection.startTransaction();

			Account customerAccount = getAccountDetails(accountNumber);
			if (!MySQLAPIUtil.updateBalanceInAccount(accountNumber, customerAccount.getBalance() + amount)) {
				throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
			}

			Transaction depositTransaction = new Transaction();
			depositTransaction.setUserID(customerAccount.getUserID());
			depositTransaction.setViewerAccountNumber(accountNumber);
			depositTransaction.setTransactionAmount(amount);
			depositTransaction.setTransactionType(TransactionType.CREDIT.toString());
			depositTransaction.setclosingBalance(customerAccount.getBalance() + amount);
			depositTransaction.setRemarks("BANK_DEPOSIT");

			MySQLAPIUtil.createSenderTransactionRecord(depositTransaction);
			ServerConnection.endTransaction();
			return depositTransaction.getTransactionID();

		} catch (AppException e) {
			ServerConnection.reverseTransaction();
			throw e;
		}
	}

	@Override
	public long withdrawAmount(long accountNumber, double amount) throws AppException {
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
			withdrawTransaction.setUserID(customerAccount.getUserID());
			withdrawTransaction.setViewerAccountNumber(accountNumber);
			withdrawTransaction.setTransactionAmount(amount);
			withdrawTransaction.setTransactionType(TransactionType.DEBIT.toString());
			withdrawTransaction.setclosingBalance(customerAccount.getBalance() - amount);
			withdrawTransaction.setRemarks("BANK_WITHDRAWAL");

			MySQLAPIUtil.createSenderTransactionRecord(withdrawTransaction);
			ServerConnection.endTransaction();
			return withdrawTransaction.getTransactionID();

		} catch (AppException e) {
			ServerConnection.reverseTransaction();
			throw e;
		}
	}

	@Override
	public boolean updateCustomerDetails(int customerID, Fields field, Object value) throws AppException {
		ValidatorUtil.validatePositiveNumber(customerID);
		ValidatorUtil.validateObject(value);
		ValidatorUtil.validateObject(field);

		MySQLQueryUtil queryBuilder = new MySQLQueryUtil();
		queryBuilder.update(Schemas.CUSTOMERS);
		queryBuilder.setField(field);
		queryBuilder.where(Fields.USER_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setObject(1, value);
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
//	public boolean createEmployee(EmployeeRecord employee) throws AppException {
//		ValidatorUtil.validateObject(employee);
//		ValidatorUtil.validatePostiveNumber(employee.getUserID());
//		try (PreparedStatement statement = ServerConnection.getServerConnection()
//				.prepareStatement(MySQLQuery.CREATE_EMPLOYEE_PS.getQuery())) {
//			statement.setString(1, employee.getFirstName());
//			statement.setString(2, employee.getLastName());
//			statement.setString(3, employee.getGender().toString());
//			int response = statement.executeUpdate();
//			if (response == 1) {
//				return true;
//			} else {
//				throw new AppException(APIExceptionMessage.USER_CREATION_FAILED);
//			}
//		} catch (SQLException e) {
//			throw new AppException(e.getMessage());
//		}
//	}
//	
//private int createUser(UserRecord user) throws AppException {
//ValidatorUtil.validateObject(user);
//try {
//	ServerConnection.getServerConnection().setAutoCommit(false);
//	int userID = createUserRecord(user);
//	user.setUserID(userID);
//	createCredentialRecord(user);
//	ServerConnection.getServerConnection().commit();
//	return userID;
//} catch (AppException | SQLException e) {
//	try {
//		ServerConnection.getServerConnection().rollback();
//	} catch (SQLException sqlE) {
//	}
//	throw new AppException(APIExceptionMessage.USER_CREATION_FAILED);
//} finally {
//	try {
//		ServerConnection.getServerConnection().setAutoCommit(true);
//	} catch (SQLException e) {
//		throw new AppException(APIExceptionMessage.NO_SERVER_CONNECTION);
//	}
//}
//
//}