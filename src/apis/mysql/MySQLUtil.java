package apis.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;
import helpers.UserRecord;
import utility.ValidatorUtil;

class MySQLUtil {

	protected static EmployeeRecord convertToEmployeeRecord(ResultSet record) throws AppException {
		ValidatorUtil.validateObject(record);
		EmployeeRecord employeeRecord = new EmployeeRecord();
		try {
			employeeRecord.setUserID(record.getInt(1));
			employeeRecord.setRole(record.getInt(2));
			employeeRecord.setBranchID(record.getInt(3));
		} catch (SQLException e) {
		}
		return employeeRecord;
	}

	protected static CustomerRecord convertToCustomerRecord(ResultSet record) throws AppException {
		ValidatorUtil.validateObject(record);
		CustomerRecord customerRecord = new CustomerRecord();
		try {
			customerRecord.setUserID(record.getInt(1));
			customerRecord.setAadhaarNumber(record.getLong(2));
			customerRecord.setPanNumber(record.getString(3));
		} catch (SQLException e) {
		}
		return customerRecord;
	}

	protected static UserRecord updateUserRecord(ResultSet record, UserRecord user) throws AppException {
		ValidatorUtil.validateObject(record);
		ValidatorUtil.validateObject(user);
		try {
			user.setUserID(record.getInt(1));
			user.setFirstName(record.getString(2));
			user.setLastName(record.getString(3));
			user.setDateOfBirthInMills(record.getLong(4));
			user.setGender(record.getString(5));
			user.setAddress(record.getString(6));
			user.setMobileNumber(record.getLong(7));
			user.setEmail(record.getString(8));
			user.setStatus(record.getString(9));
			user.setType(record.getString(10));
		} catch (SQLException e) {
		}
		return user;
	}

	protected static Account convertToAccount(ResultSet accountRS) throws AppException {
		ValidatorUtil.validateObject(accountRS);
		Account account = new Account();
		try {
			account.setAccountNumber(accountRS.getLong(1));
			account.setUserID(accountRS.getInt(2));
			account.setType(accountRS.getString(3));
			account.setBranchID(accountRS.getInt(4));
			account.setOpeningDate(accountRS.getLong(5));
			account.setBalance(accountRS.getDouble(6));
			account.setStatus(accountRS.getString(7));
		} catch (SQLException e) {
		}
		return account;
	}

	protected static Transaction convertToTransaction(ResultSet transactionRS) throws AppException {
		ValidatorUtil.validateObject(transactionRS);
		Transaction transaction = null;
		try {
			transaction = new Transaction(transactionRS.getLong(1));
			transaction.setUserID(transactionRS.getInt(2));
			transaction.setViewerAccountNumber(transactionRS.getLong(3));
			transaction.setTransactedAccountNumber(transactionRS.getLong(4));
			transaction.setTransactionAmount(transactionRS.getDouble(5));
			transaction.setTransactionType(transactionRS.getString(6));
			transaction.setclosingBalance(transactionRS.getDouble(7));
			transaction.setDateTime(transactionRS.getLong(8));
			transaction.setRemarks(transactionRS.getString(9));
			transaction.setStatus(transactionRS.getString(10));
		} catch (SQLException e) {
		}
		return transaction;
	}
}
