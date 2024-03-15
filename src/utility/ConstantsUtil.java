package utility;

import java.util.ArrayList;
import java.util.List;

import exceptions.AppException;
import exceptions.messages.InvalidInputMessage;

public class ConstantsUtil {

	public static final int LIST_LIMIT = 10;
	public static final double MINIMUM_DEPOSIT_AMOUNT = 2000.0;
	public static final List<ModifiableField> USER_MODIFIABLE_FIELDS;
	public static final List<ModifiableField> EMPLOYEE_MODIFIABLE_FIELDS;
	public static final List<ModifiableField> ADMIN_MODIFIABLE_FIELDS;
	
	public static final int CACHE_SIZE = 30;


	static {
		List<ModifiableField> tempList = new ArrayList<>();
		tempList.addAll(List.of(ModifiableField.ADDRESS, ModifiableField.PHONE, ModifiableField.EMAIL));
		USER_MODIFIABLE_FIELDS = List.copyOf(tempList);

		tempList.addAll(List.of(ModifiableField.FIRST_NAME, ModifiableField.LAST_NAME, ModifiableField.GENDER,
				ModifiableField.DATE_OF_BIRTH, ModifiableField.AADHAAR_NUMBER, ModifiableField.PAN_NUMBER));
		EMPLOYEE_MODIFIABLE_FIELDS = List.copyOf(tempList);

		tempList.addAll(List.of(ModifiableField.BRANCH_ID));
		ADMIN_MODIFIABLE_FIELDS = List.copyOf(tempList);
	}

	public static enum ModifiableField {
		ADDRESS, PHONE, EMAIL, DATE_OF_BIRTH, GENDER, AADHAAR_NUMBER, PAN_NUMBER, FIRST_NAME, LAST_NAME, BRANCH_ID,
		STATUS, TYPE
	}

	public static enum UserType {
		CUSTOMER(0), EMPLOYEE(1), ADMIN(2);

		private int userTypeId;

		private UserType(int userTypeId) {
			this.userTypeId = userTypeId;
		}

		public int getUserTypeId() {
			return this.userTypeId;
		}

		public static UserType getUserType(int userTypeId) throws AppException {
			switch (userTypeId) {
			case 0:
				return CUSTOMER;
			case 1:
				return EMPLOYEE;
			case 2:
				return ADMIN;
			default:
				throw new AppException(InvalidInputMessage.INVALID_INTEGER_INPUT);
			}
		}
	}

	public static enum Status {
		ACTIVE(0), INACTIVE(1), CLOSED(3), FROZEN(2);

		private int statusId;

		Status(int statusID) {
			this.statusId = statusID;
		}

		public int getStatusId() {
			return this.statusId;
		}

		public static Status getStatus(int statusId) throws AppException {
			switch (statusId) {
			case 0:
				return ACTIVE;
			case 1:
				return INACTIVE;
			case 2:
				return FROZEN;
			case 3:
				return CLOSED;

			default:
				throw new AppException(InvalidInputMessage.INVALID_INTEGER_INPUT);
			}
		}
	}

	public static enum TransactionType {
		CREDIT(1), DEBIT(0);

		private int transactionTypeId;

		private TransactionType(int transactionTypeId) {
			this.transactionTypeId = transactionTypeId;
		}

		public int getTransactionTypeId() {
			return this.transactionTypeId;
		}

		public static TransactionType getTransactionType(int transactionTypeId) throws AppException {
			switch (transactionTypeId) {
			case 0:
				return DEBIT;
			case 1:
				return CREDIT;
			default:
				throw new AppException(InvalidInputMessage.INVALID_INTEGER_INPUT);
			}
		}
	}

	public static enum AccountType {
		SAVINGS(0), CURRENT(1), SALARY(2);

		private int accountTypeId;

		private AccountType(int accountTypeId) {
			this.accountTypeId = accountTypeId;
		}

		public int getAccountTypeId() {
			return this.accountTypeId;
		}

		public static AccountType getAccountType(int accountTypeId) throws AppException {
			switch (accountTypeId) {
			case 0:
				return SAVINGS;
			case 1:
				return CURRENT;
			case 2:
				return SALARY;

			default:
				throw new AppException(InvalidInputMessage.INVALID_INTEGER_INPUT);
			}
		}
	}

	public static enum Gender {
		MALE(0), FEMALE(1), OTHER(2);

		private int genderId;

		private Gender(int genderId) {
			this.genderId = genderId;
		}

		public int getGenderId() {
			return this.genderId;
		}

		public static Gender getGender(int genderId) throws AppException {
			switch (genderId) {
			case 0:
				return MALE;
			case 1:
				return FEMALE;
			case 2:
				return OTHER;

			default:
				throw new AppException(InvalidInputMessage.INVALID_INTEGER_INPUT);
			}
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
