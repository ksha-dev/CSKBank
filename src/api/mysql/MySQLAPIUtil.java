package api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import api.mysql.MySQLQueryUtil.Fields;
import api.mysql.MySQLQueryUtil.Schemas;
import exceptions.AppException;
import exceptions.messages.APIExceptionMessage;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;

class MySQLAPIUtil {

	protected static void createReceiverTransactionRecord(Transaction receiverTransaction) throws AppException {
		MySQLQueryUtil queryBuilder = new MySQLQueryUtil();
		queryBuilder.insertInto(Schemas.TRANSACTIONS);
		queryBuilder.insertValuePlaceholders(9);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setLong(1, receiverTransaction.getTransactionId());
			statement.setInt(2, receiverTransaction.getUserId());
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

	protected static void createSenderTransactionRecord(Transaction transaction) throws AppException {
		MySQLQueryUtil queryBuilder = new MySQLQueryUtil();
		queryBuilder.insertInto(Schemas.TRANSACTIONS);
		queryBuilder.insertFields(List.of(Fields.USER_ID, Fields.VIEWER_ACCOUNT_NUMBER,
				Fields.TRANSACTED_ACCOUNT_NUMBER, Fields.TRANSACTED_AMOUNT, Fields.TRANSACTION_TYPE,
				Fields.CLOSING_BALANCE, Fields.TIME_STAMP, Fields.REMARKS));
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, transaction.getUserId());
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
					transaction.setTransactionId(keys.getLong(1));
				} else {
					throw new AppException(APIExceptionMessage.TRANSACTION_FAILED);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	protected static boolean updateBalanceInAccount(long accountNumber, double balance) throws AppException {
		MySQLQueryUtil queryBuilder = new MySQLQueryUtil();
		queryBuilder.update(Schemas.ACCOUNTS);
		queryBuilder.setField(Fields.BALANCE);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.ACCOUNT_NUMBER);
		queryBuilder.and();
		queryBuilder.fieldEquals(Fields.STATUS);
		queryBuilder.end();

		try (PreparedStatement updateAccountBalance = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			updateAccountBalance.setDouble(1, balance);
			updateAccountBalance.setLong(2, accountNumber);
			updateAccountBalance.setString(3, "ACTIVE");
			int response = updateAccountBalance.executeUpdate();
			return response == 1;
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	protected static CustomerRecord getCustomerRecord(int userId) throws AppException {
		MySQLQueryUtil queryBuilder = new MySQLQueryUtil();
		queryBuilder.selectField(Fields.ALL);
		queryBuilder.fromTable(Schemas.CUSTOMERS);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.USER_ID);
		queryBuilder.end();

		try (PreparedStatement customerStatement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			customerStatement.setInt(1, userId);
			try (ResultSet customerResult = customerStatement.executeQuery()) {
				if (customerResult.next()) {
					return MySQLConversionUtil.convertToCustomerRecord(customerResult);
				} else {
					throw new AppException(APIExceptionMessage.CUSTOMER_RECORD_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	// TRANSACTION QUERIES
	protected static EmployeeRecord getEmployeeRecord(int userId) throws AppException {
		MySQLQueryUtil queryBuilder = new MySQLQueryUtil();
		queryBuilder.selectField(Fields.ALL);
		queryBuilder.fromTable(Schemas.EMPLOYEES);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.USER_ID);
		queryBuilder.end();

		try (PreparedStatement employeeStatement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			employeeStatement.setInt(1, userId);
			try (ResultSet employeeResult = employeeStatement.executeQuery()) {
				if (employeeResult.next()) {
					return MySQLConversionUtil.convertToEmployeeRecord(employeeResult);
				} else {
					throw new AppException(APIExceptionMessage.EMPLOYEE_RECORD_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

}
