package utility;

import java.util.List;
import java.util.logging.Logger;

import consoleRunner.utility.LoggingUtil;

public class ConstantsUtil {

	public static Logger log = LoggingUtil.DEFAULT_LOGGER;

	public static final int LIST_LIMIT = 10;
	public static final double MINIMUM_DEPOSIT_AMOUNT = 2000.0;
	public static final List<ModifiableField> USER_MODIFIABLE_FIELDS;
	public static final List<ModifiableField> EMPLOYEE_MODIFIABLE_FIELDS;
	public static final List<ModifiableField> ADMIN_MODIFIABLE_FIELDS;

	static {
		List<ModifiableField> tempList = List.of(ModifiableField.ADDRESS, ModifiableField.PHONE, ModifiableField.EMAIL);
		USER_MODIFIABLE_FIELDS = tempList;

		tempList.addAll(List.of(ModifiableField.FIRST_NAME, ModifiableField.LAST_NAME, ModifiableField.DATE_OF_BIRTH,
				ModifiableField.AADHAAR_NUMBER, ModifiableField.PAN_NUMBER));
		EMPLOYEE_MODIFIABLE_FIELDS = tempList;

		tempList.addAll(List.of(ModifiableField.ROLE, ModifiableField.BRANCH_ID));
		ADMIN_MODIFIABLE_FIELDS = tempList;
	}

	public static enum ModifiableField {
		ADDRESS, PHONE, EMAIL, DATE_OF_BIRTH, GENDER, AADHAAR_NUMBER, PAN_NUMBER, FIRST_NAME, LAST_NAME, ROLE,
		BRANCH_ID, STATUS, TYPE
	}

	public static enum UserType {
		CUSTOMER, EMPLOYEE
	}

	public static enum EmployeeType {
		EMPLOYEE, ADMIN
	}

	public static enum Status {
		ACTIVE, INACTIVE, CLOSED, FROZEN
	}

	public static enum TransactionType {
		CREDIT, DEBIT
	}

	public static enum AccountType {
		SAVINGS, CURRENT, SALARY
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

	public static enum TransactionHistoryLimit {
		RECENT, ONE_MONTH, THREE_MONTH, SIX_MONTH;

		private static final long ONE_MONTH_MILLIS = 2592000000L;

		public long getDuration() {
			long transactionDuration = ONE_MONTH_MILLIS;
			switch (this) {
			case ONE_MONTH:
				transactionDuration *= 1;
				break;
			case THREE_MONTH:
				transactionDuration *= 3;
				break;
			case SIX_MONTH:
				transactionDuration *= 6;
				break;
			default:
				transactionDuration = 0;
				break;
			}
			return System.currentTimeMillis() - transactionDuration;
		}
	}
}
