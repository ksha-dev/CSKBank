package apis.mysql;

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
import helpers.Transaction;
import helpers.UserRecord;
import utility.SchemaUtil;
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

	public boolean transferAmount(int customerID, long userAccountNumber, long transactedAccountNumber, double amount)
			throws AppException {
		try {
			ServerConnection.getServerConnection().setAutoCommit(false);
			PreparedStatement verificationStatement = ServerConnection.getServerConnection().prepareStatement(
					"SELECT userID, balance from accounts WHERE account_number = ? AND status = 'ACTIVE';");
			verificationStatement.setLong(1, userAccountNumber);
			double availableBalance = 0;
			try (ResultSet verifyResults = verificationStatement.executeQuery()) {
				if (verifyResults.next() && verifyResults.getInt(1) == customerID) {
					availableBalance = verifyResults.getDouble(2);
				}
			}

			if (availableBalance < amount) {
				throw new AppException();
			}

			PreparedStatement updateAccountBalance = ServerConnection.getServerConnection().prepareStatement(
					"UPDATE accounts SET balance = ? WHERE account_number = ? AND status = 'ACTIVE';");
			updateAccountBalance.setDouble(1, availableBalance - amount);
			updateAccountBalance.executeUpdate();
			updateAccountBalance.close();

			PreparedStatement addUserTransaction = ServerConnection.getServerConnection().prepareStatement(
					"INSERT INTO transactions(user_id, viewer_account_number, transacted_account_number, transacted_amount, transacted_amount,"
							+ " transaction_type, closing_balance, time_stamp, remarks, status) VALUE(?,?,?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			addUserTransaction.setInt(1, customerID);
			addUserTransaction.setLong(2, userAccountNumber);
			addUserTransaction.setLong(3, transactedAccountNumber);
			addUserTransaction.setDouble(4, amount);

		} catch (SQLException e) {
			throw new AppException();
		}
		return false;
	}

	@Override
	public boolean transferAmount(long accountNumber, double amount) throws AppException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getBalanceInAccount(long accountNumber) throws AppException {
		try (PreparedStatement statement = ServerConnection.getServerConnection().prepareStatement(null)) {
			statement.setLong(1, accountNumber);
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					return result.getDouble(1);
				}
			}
		} catch (SQLException e) {
			throw new AppException();
		}
		return 0.0;
	}
}
