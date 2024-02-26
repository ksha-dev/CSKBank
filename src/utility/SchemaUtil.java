package utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import exceptions.AppException;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.UserRecord;

public class SchemaUtil {

	public static enum Schemas {
		USERS("users"), EMPLOYEES("employees"), CUSTOMERS("customers"), ACCOUNTS("accounts"),
		TRANSACTIONS("transactions"), BRANCH("branch");

		private String schemaName;

		private Schemas(String schemaName) {
			this.schemaName = schemaName;
		}

		public String toString() {
			return this.schemaName;
		}
	}

	public static enum ColumnNames {
		USER_ID("user_id"), PASSWORD("password"), FIRST_NAME("first_name"), LAST_NAME("last_name"),
		DOB("date_of_birth"), GENDER("gender"), ADDRESS("address"), MOBILE("mobile"), EMAIL("email"), STATUS("status"),
		TYPE("type"), BRANCH_ID("branch_id");

		private String schemaColumnName;

		private ColumnNames(String columnName) {
			this.schemaColumnName = columnName;
		}

		public String toString() {
			return this.schemaColumnName;
		}
	}

	public static enum UserTypes {
		CUSTOMER, EMPLOYEE
	}

	public static enum UserStatus {
		ACTIVE, INACTIVE, BLOCKED
	}

	public static enum Gender {
		MALE, FEMALE, OTHER;

		public String getGendersString() {
			StringBuilder string = new StringBuilder();
			for (Gender gender : Gender.values()) {
				string.append(gender.toString() + " ");
			}
			return string.toString();
		}

	}

	public static EmployeeRecord convertToEmployeeRecord(ResultSet record) throws AppException {
		ValidatorUtil.validateObject(record);
		EmployeeRecord employeeRecord = new EmployeeRecord();
		try {
			employeeRecord.setUserID(record.getInt(1));
			employeeRecord.setFirstName(record.getString(3));
			employeeRecord.setLastName(record.getString(4));
			employeeRecord.setDateOfBirthInMills(record.getLong(5));
			employeeRecord.setGender(record.getString(6));
			employeeRecord.setAddress(record.getString(7));
			employeeRecord.setMobileNumber(record.getLong(8));
			employeeRecord.setEmail(record.getString(9));
			employeeRecord.setStatus(record.getString(10));
			employeeRecord.setType(record.getString(11));
			employeeRecord.setRole(record.getInt(13));
			employeeRecord.setBranchID(record.getInt(14));
		} catch (SQLException e) {
		}
		return employeeRecord;
	}

	public static CustomerRecord convertToCustomerRecord(ResultSet record) throws AppException {
		ValidatorUtil.validateObject(record);
		CustomerRecord customerRecord = new CustomerRecord();
		try {
			customerRecord.setUserID(record.getInt(1));
			customerRecord.setFirstName(record.getString(3));
			customerRecord.setLastName(record.getString(4));
			customerRecord.setDateOfBirthInMills(record.getLong(5));
			customerRecord.setGender(record.getString(6));
			customerRecord.setAddress(record.getString(7));
			customerRecord.setMobileNumber(record.getLong(8));
			customerRecord.setEmail(record.getString(9));
			customerRecord.setStatus(record.getString(10));
			customerRecord.setType(record.getString(11));
			customerRecord.setAadhaarNumber(record.getLong(13));
			customerRecord.setPanNumber(record.getString(14));
		} catch (SQLException e) {
		}
		return customerRecord;
	}

	public static UserRecord convertToUserRecord(ResultSet record) throws AppException {
		ValidatorUtil.validateObject(record);
		UserRecord userRecord = null;
		try {
			String type = record.getString("type");
			switch (type) {
			case "EMPLOYEE":
				userRecord = convertToEmployeeRecord(record);
				break;

			case "CUSTOMER":
				userRecord = convertToCustomerRecord(record);
				break;
			}
		} catch (SQLException e) {
		}
		return userRecord;
	}
}
