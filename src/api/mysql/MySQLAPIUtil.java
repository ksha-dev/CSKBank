package api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import api.mysql.MySQLQuery.Column;
import api.mysql.MySQLQuery.Schemas;
import exceptions.AppException;
import exceptions.messages.APIExceptionMessage;
import modules.CustomerRecord;
import modules.EmployeeRecord;
import modules.Transaction;
import modules.UserRecord;
import utility.ConstantsUtil.Status;
import utility.ValidatorUtil;

class MySQLAPIUtil {

	static void createReceiverTransactionRecord(Transaction receiverTransaction) throws AppException {
		ValidatorUtil.validateObject(receiverTransaction);
		MySQLQuery queryBuilder = new MySQLQuery();
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
			statement.setString(6, receiverTransaction.getTransactionType().getTransactionTypeId() + "");
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

	static void createSenderTransactionRecord(Transaction transaction) throws AppException {
		ValidatorUtil.validateObject(transaction);
		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.insertInto(Schemas.TRANSACTIONS);
		queryBuilder.insertColumns(List.of(Column.USER_ID, Column.VIEWER_ACCOUNT_NUMBER,
				Column.TRANSACTED_ACCOUNT_NUMBER, Column.TRANSACTED_AMOUNT, Column.TRANSACTION_TYPE,
				Column.CLOSING_BALANCE, Column.TIME_STAMP, Column.REMARKS));
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, transaction.getUserId());
			statement.setLong(2, transaction.getViewerAccountNumber());
			statement.setLong(3, transaction.getTransactedAccountNumber());
			statement.setDouble(4, transaction.getTransactedAmount());
			statement.setString(5, transaction.getTransactionType().getTransactionTypeId() + "");
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

	static boolean updateBalanceInAccount(long accountNumber, double balance) throws AppException {
		ValidatorUtil.validateId(accountNumber);
		ValidatorUtil.validateAmount(balance);
		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.update(Schemas.ACCOUNTS);
		queryBuilder.setColumn(Column.BALANCE);
		queryBuilder.separator();
		queryBuilder.columnEquals(Column.STATUS);
		queryBuilder.separator();
		queryBuilder.columnEquals(Column.LAST_TRANSACTED_AT);
		queryBuilder.where();
		queryBuilder.columnEquals(Column.ACCOUNT_NUMBER);
		queryBuilder.and();
		queryBuilder.not();
		queryBuilder.columnEquals(Column.STATUS);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setDouble(1, balance);
			statement.setString(2, Status.ACTIVE.getStatusId() + "");
			statement.setLong(3, System.currentTimeMillis());
			statement.setLong(4, accountNumber);
			statement.setString(5, Status.CLOSED.getStatusId() + "");
			int response = statement.executeUpdate();
			return response == 1;
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	static CustomerRecord getCustomerRecord(int userId) throws AppException {
		ValidatorUtil.validateId(userId);
		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectColumn(Column.ALL);
		queryBuilder.fromSchema(Schemas.CUSTOMERS);
		queryBuilder.where();
		queryBuilder.columnEquals(Column.USER_ID);
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
	static EmployeeRecord getEmployeeRecord(int userId) throws AppException {
		ValidatorUtil.validateId(userId);
		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectColumn(Column.ALL);
		queryBuilder.fromSchema(Schemas.EMPLOYEES);
		queryBuilder.where();
		queryBuilder.columnEquals(Column.USER_ID);
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

	static void getAndUpdateUserRecord(UserRecord userRecord) throws AppException {
		ValidatorUtil.validateObject(userRecord);
		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectColumn(Column.ALL);
		queryBuilder.fromSchema(Schemas.USERS);
		queryBuilder.where();
		queryBuilder.columnEquals(Column.USER_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setInt(1, userRecord.getUserId());
			try (ResultSet record = statement.executeQuery()) {
				if (record.next()) {
					MySQLConversionUtil.updateUserRecord(record, userRecord);
				} else {
					throw new AppException(APIExceptionMessage.USER_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

}
