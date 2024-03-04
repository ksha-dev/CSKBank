package api.mysql;

public class MySQLSchameUtil {
	public static enum Schemas {
		USERS, EMPLOYEES, CUSTOMERS, ACCOUNTS, TRANSACTIONS, BRANCH, CREDENTIALS;

		public String getName() {
			return this.toString().toLowerCase();
		}
	}

	public static enum UserFields {
		USER_ID, PASSWORD, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, GENDER, ADDRESS, MOBILE, EMAIL, STATUS, TYPE;

		public String getName() {
			return this.toString().toLowerCase();
		}
	}

	public static enum CustomerFields {
		AADHAAR_NUMBER, PAN_NUMBER, USER_ID;

		public String getName() {
			return this.toString().toLowerCase();
		}
	}

	public static enum EmployeeFields {
		USER_ID, ROLE, BRANCH_ID;

		public String getName() {
			return this.toString().toLowerCase();
		}
	}

	public static enum AccountFields {
		BRANCH_ID, ACCOUNT_NUMBER, OPENING_DATE, BALANCE, CLOSING_BALANCE, USER_ID;

		public String getName() {
			return this.toString().toLowerCase();
		}
	}

	public static enum TransactionFields {
		USER_ID, TRANSACTION_ID, REMARKS, VIEWER_ACCOUNT_NUMBER, TRANSACTED_ACCOUNT_NUMBER, TRANSACTED_AMOUNT,
		TRANSACTION_TYPE, TIME_STAMP;

		public String getName() {
			return this.toString().toLowerCase();
		}
	}
}
