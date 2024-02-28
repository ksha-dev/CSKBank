package utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import exceptions.AppException;
import helpers.Account;

public class SchemaUtil {

	public static Logger log = LoggingUtil.DEFAULT_LOGGER;

//	public static enum Schemas {
//		USERS("users"), EMPLOYEES("employees"), CUSTOMERS("customers"), ACCOUNTS("accounts"),
//		TRANSACTIONS("transactions"), BRANCH("branch");
//
//		private String schemaName;
//
//		private Schemas(String schemaName) {
//			this.schemaName = schemaName;
//		}
//
//		public String toString() {
//			return this.schemaName;
//		}
//	}

//	public static enum ColumnNames {
//		USER_ID("user_id"), PASSWORD("password"), FIRST_NAME("first_name"), LAST_NAME("last_name"),
//		DOB("date_of_birth"), GENDER("gender"), ADDRESS("address"), MOBILE("mobile"), EMAIL("email"), STATUS("status"),
//		TYPE("type"), BRANCH_ID("branch_id");
//
//		private String schemaColumnName;
//
//		private ColumnNames(String columnName) {
//			this.schemaColumnName = columnName;
//		}
//
//		public String toString() {
//			return this.schemaColumnName;
//		}
//	}

	public static enum UserTypes {
		CUSTOMER, EMPLOYEE
	}

	public static enum UserStatus {
		ACTIVE, INACTIVE, BLOCKED
	}

	public static enum TransactionType {
		CREDIT, DEBIT
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
	
	public static  ZonedDateTime convertLongToLocalDate(long dateTime) {
		return ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault());
	}

	public static String passwordHasher(String password) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] hash = digest.digest(password.getBytes("UTF-8"));
			final StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < hash.length; i++) {
				final String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception ex) {
		}
		return null;
	}

	public static void showListOfAccounts(List<Account> accounts) throws AppException {
		int numberOfAccounts = accounts.size();
		if (accounts.isEmpty()) {
			log.info("No accounts found");
		} else {
			log.info("SNO | ACCOUNT NUMBER | CUSTOMER ID | START DATE |  BALANCE   | STATUS");
			log.info("-".repeat(70));
			for (int i = 0; i < numberOfAccounts; i++) {
				Account account = accounts.get(i);
				log.info(String.format("%3d | %014d | %11d | %s | %10.2f | %s", i + 1, account.getAccountNumber(),
						account.getUserID(), account.getOpeningDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
						account.getBalance(), account.getStatus()));
			}
		}
	}
}
