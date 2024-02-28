package api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import api.EmployeeAPI;
import exceptions.APIExceptionMessage;
import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.Transaction;
import helpers.UserRecord;
import utility.HelperUtil;
import utility.ValidatorUtil;
import utility.HelperUtil.TransactionType;

public class MySQLEmployeeAPI extends MySQLGeneralAPI implements EmployeeAPI {

	private void createUserRecord(UserRecord user) throws AppException {
		ValidatorUtil.validateObject(user);
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CREATE_USER_PS.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, user.getFirstName());
			statement.setString(2, user.getLastName());
			statement.setLong(3, user.getDateOfBirthInMills());
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
		ValidatorUtil.validatePostiveNumber(user.getUserID());
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CREATE_CREDENTIAL_PS.getQuery())) {
			statement.setInt(1, user.getUserID());
			statement.setString(2, HelperUtil.passwordHasher(user.getFirstName().substring(0, 3) + "@"
					+ user.getDateOfBirth().format(DateTimeFormatter.BASIC_ISO_DATE).substring(4, 8)));
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
			PreparedStatement statement = ServerConnection.getServerConnection()
					.prepareStatement(MySQLQuery.CREATE_CUSTOMER_PS.getQuery());
			statement.setInt(1, customer.getUserID());
			statement.setLong(2, customer.getAadhaarNumber());
			statement.setString(3, customer.getPanNumber());
			int response = statement.executeUpdate();
			statement.close();
			if (response == 1) {
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
	public long createAccount(int customerID, String type, int branchID, double deposiAmount) throws AppException {
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CREATE_ACCOUNT_PS.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, customerID);
			statement.setString(2, type);
			statement.setInt(3, branchID);
			statement.setLong(4, System.currentTimeMillis());
			statement.setDouble(5, deposiAmount);

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
	public List<Account> viewAccountsInBranch(int branchID) throws AppException {
		List<Account> accounts = new ArrayList<Account>();
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.ACCOUNTS_IN_BRANCH_PS.getQuery())) {
			statement.setInt(1, branchID);
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
	public long depositAmount(long accountNumber, double amount) throws AppException {
		try {
			ServerConnection.startTransaction();

			Account customerAccount = getAccountDetails(accountNumber);
			if (!MySQLUtil.updateBalanceInAccount(accountNumber, customerAccount.getBalance() + amount)) {
				throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
			}

			Transaction depositTransaction = new Transaction();
			depositTransaction.setUserID(customerAccount.getUserID());
			depositTransaction.setViewerAccountNumber(accountNumber);
			depositTransaction.setTransactionAmount(amount);
			depositTransaction.setTransactionType(TransactionType.CREDIT.toString());
			depositTransaction.setclosingBalance(customerAccount.getBalance() + amount);
			depositTransaction.setRemarks("BANK_DEPOSIT");

			MySQLUtil.createSenderTransactionRecord(depositTransaction);
			ServerConnection.endTransaction();
			return depositTransaction.getTransactionID();

		} catch (AppException e) {
			ServerConnection.reverseTransaction();
			throw e;
		}
	}

	@Override
	public long withdrawAmount(long accountNumber, double amount) throws AppException {

		return 0;
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