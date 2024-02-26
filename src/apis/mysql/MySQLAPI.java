package apis.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import apis.API;
import exceptions.APIExceptionMessage;
import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;
import helpers.UserRecord;
import utility.ValidatorUtil;

final public class MySQLAPI implements API {

	@Override
	public boolean authenticateUser(int userID, String password) throws AppException {
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CREDENTIAL_CHECK_PS.getQuery())) {
			statement.setInt(1, userID);
			try (ResultSet authenticationResult = statement.executeQuery()) {
				if (authenticationResult.next()) {
					if (authenticationResult.getString(2).equals(password)) {
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
					case "CUSTOMER": {
						try (PreparedStatement customerStatement = ServerConnection.getServerConnection()
								.prepareStatement(MySQLQuery.CUSTOMER_DETAILS_PS.getQuery())) {
							customerStatement.setInt(1, userID);
							try (ResultSet customerResult = customerStatement.executeQuery()) {
								if (customerResult.next()) {
									user = MySQLUtil.convertToCustomerRecord(customerResult);
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
						break;
					case "EMPLOYEE":
						try (PreparedStatement employeeStatement = ServerConnection.getServerConnection()
								.prepareStatement(MySQLQuery.EMPLOYEE_DETAILS_PS.getQuery())) {
							employeeStatement.setInt(1, userID);
							try (ResultSet employeeResult = employeeStatement.executeQuery()) {
								if (employeeResult.next()) {
									user = MySQLUtil.convertToEmployeeRecord(employeeResult);
								}
							} catch (SQLException e) {
							}
						}

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
	public Account getAccountDetails(int accountNumber) throws AppException {
		Account account = new Account();
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.ACCOUNT_DETAILS_PS.getQuery())) {
			statement.setInt(1, accountNumber);
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
	public int createAccount(int customerID, String type, int branchID, double deposiAmount) throws AppException {
		String createAccountQuery = "INSERT INTO accounts(user_id, type, branch_id, opening_date, balance) VALUES(?,?,?,?,?);";
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(createAccountQuery)) {
			statement.setInt(1, customerID);
			statement.setString(2, type);
			statement.setInt(3, branchID);
			statement.setLong(4, System.currentTimeMillis());
			statement.setDouble(5, deposiAmount);

			int response = statement.executeUpdate();
			try (ResultSet key = statement.getGeneratedKeys()) {
				if (key.next() && response == 1) {
					return key.getInt(1);
				} else {
					throw new AppException(APIExceptionMessage.ACCOUNT_CREATION_FAILED);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public int createUser(UserRecord user) throws AppException {
		ValidatorUtil.validateObject(user);
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CREATE_USER_PS.getQuery())) {
			statement.setString(1, user.getFirstName());
			statement.setString(2, user.getLastName());
			statement.setString(3, user.getGender().toString());
			statement.setString(4, user.getAddress());
			statement.setLong(5, user.getMobileNumber());
			statement.setString(6, user.getEmail());

			int response = statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next() && response == 1) {
					return keys.getInt(1);
				} else {
					throw new AppException(APIExceptionMessage.USER_CREATION_FAILED);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public boolean createCustomer(CustomerRecord customer) throws AppException {
		ValidatorUtil.validateObject(customer);
		ValidatorUtil.validatePostiveNumber(customer.getUserID());
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CREATE_CUSTOMER_PS.getQuery())) {
			statement.setInt(1, customer.getUserID());
			statement.setLong(2, customer.getAadhaarNumber());
			statement.setString(3, customer.getPanNumber());
			int response = statement.executeUpdate();
			if (response == 1) {
				return true;
			} else {
				throw new AppException(APIExceptionMessage.USER_CREATION_FAILED);
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public boolean createEmployee(EmployeeRecord employee) throws AppException {
		ValidatorUtil.validateObject(employee);
		ValidatorUtil.validatePostiveNumber(employee.getUserID());
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(MySQLQuery.CREATE_EMPLOYEE_PS.getQuery())) {
			statement.setString(1, employee.getFirstName());
			statement.setString(2, employee.getLastName());
			statement.setString(3, employee.getGender().toString());
			int response = statement.executeUpdate();
			if (response == 1) {
				return true;
			} else {
				throw new AppException(APIExceptionMessage.USER_CREATION_FAILED);
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}
}

//@Override
//	public CustomerRecord getCustomerDetails(int userID) throws AppException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public EmployeeRecord getEmployeeDetails(int userID) throws AppException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//package apis;

//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//
//import apis.mysql.ServerConnection;
//import exceptions.APIExceptionMessage;
//import exceptions.AppException;
//import helpers.CustomerRecord;
//import helpers.UserRecord;
//import utility.SchemaUtil;
//import utility.ValidatorUtil;
//
//public class EmployeeAPI extends UserAPI {
//
//	public List<CustomerRecord> getBranchCumstomers(int employeeID, int branchID) throws AppException {
//		List<CustomerRecord> listOfCustomers = new ArrayList<CustomerRecord>();
//
//		String getBranchCustomersQuery = "SELECT DISTINCT users.*, customers.* FROM users, customers, accounts, employees "
//				+ "WHERE employees.branch_id = accounts.branch_id AND accounts.user_id = customers.user_id;";
//		try (Statement statement = ServerConnection.getServerConnection().createStatement();
//				ResultSet resultSet = statement.executeQuery(getBranchCustomersQuery)) {
//			while (resultSet.next()) {
//				listOfCustomers.add((CustomerRecord) SchemaUtil.convertToCustomerRecord(resultSet));
//			}
//			if (listOfCustomers.isEmpty()) {
//				throw new AppException(APIExceptionMessage.NO_RECORDS_FOUND);
//			}
//			return listOfCustomers;
//		} catch (SQLException e) {
//			throw new AppException(APIExceptionMessage.UNKNOWN_ERROR);
//		}
//	}
//
//	public boolean createNewUserRecord(UserRecord user) throws AppException {
//		ValidatorUtil.validateObject(user);
//		String createNewUserRecordQuery = "INSERT INTO users(password, first_name, last_name, date_of_birth, gender, address, "
//				+ "mobile, email, status, type) VALUE(?,?,?,?,?,?,?,?,'ACTIVE', 'CUSTOMER');";
////		String getLastInsertedUserID = "SELECT LAST_INSERT_ID();";
//
//		try (PreparedStatement statement = ServerConnection.getServerConnection()
//				.prepareStatement(createNewUserRecordQuery, PreparedStatement.RETURN_GENERATED_KEYS);
////				Statement indexStatement = ServerConnection.getServerConnection().createStatement()
//		) {
//			statement.setString(1, randomPasswordGenerator());
//			statement.setString(2, user.getFirstName());
//			statement.setString(3, user.getLastName());
//			statement.setLong(4, user.getDateOfBirthInMills());
//			statement.setString(5, user.getGender().toString());
//			statement.setString(6, user.getAddress());
//			statement.setLong(7, user.getMobileNumber());
//			statement.setString(8, user.getEmail());
//
//			int response = statement.executeUpdate();
//			ResultSet result = statement.getGeneratedKeys();
//
////			if (response == 1) {
////			try (ResultSet result = statement.executeQuery()) {
//			if (result.next()) {
//				user.setUserID(result.getInt(1));
//				System.out.println(user.getUserID());
//				return true;
//			} else {
//				throw new AppException(APIExceptionMessage.USER_CREATION_FAILED);
//			}
////			} catch (SQLException e) {
//////			}
////			}
//
////			return false;
//		} catch (
//
//		SQLException e) {
//			throw new AppException(e.getMessage());
//		}
//	}
//
//	public boolean createNewCustomerRecord(CustomerRecord customer) throws AppException {
//		ValidatorUtil.validateObject(customer);
//		ValidatorUtil.validatePostiveNumber(customer.getUserID());
//
//		String createNewCustomerRecordQuery = "INSERT INTO customers VALUE(" + customer.getUserID() + ", "
//				+ customer.getAadhaarNumber() + ", '" + customer.getPanNumber() + "')";
//
//		try (Statement statement = ServerConnection.getServerConnection().createStatement()) {
//			int affectedRows = statement.executeUpdate(createNewCustomerRecordQuery);
//			if (affectedRows == 1) {
//				return true;
//			} else {
//				throw new AppException(APIExceptionMessage.USER_CREATION_FAILED);
//			}
//		} catch (SQLException e) {
//			throw new AppException(e.getMessage());
//		}
//	}
//
//	private int passwordLength = 8;
//
//	private String randomPasswordGenerator() {
//		String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
//		StringBuilder s = new StringBuilder(passwordLength);
//		int y;
//		for (y = 0; y < passwordLength; y++) {
//			int index = (int) (alphaNumericString.length() * Math.random());
//			s.append(alphaNumericString.charAt(index));
//		}
//		return s.toString();
//	}
//}
