package utility;

import java.util.List;
import java.util.logging.Logger;

import consoleRunner.utility.LoggingUtil;

public class HelperUtil {

	public static Logger log = LoggingUtil.DEFAULT_LOGGER;

	public static final int LIST_LIMIT = 10;
	public static final double MINIMUM_DEPOSIT_AMOUNT = 2000.0;
	public static final List<ModifiableField> USER_MODIFIABLE_FIELDS = List.of(ModifiableField.ADDRESS,
			ModifiableField.MOBILE, ModifiableField.EMAIL);

	public static enum ModifiableField {
		ADDRESS, MOBILE, EMAIL, DATE_OF_BIRTH, GENDER, AADHAAR_NUMBER, PAN_NUMBER, FIRST_NAME, LAST_NAME
	}

	public static enum UserType {
		CUSTOMER, EMPLOYEE
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

		public long getDuration() {
			long transactionDuration = 2592000000L;
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
