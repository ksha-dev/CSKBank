package utility;

import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import exceptions.AppException;
import helpers.Account;
import helpers.Transaction;

public class HelperUtil {

	public static Logger log = LoggingUtil.DEFAULT_LOGGER;

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

	public static ZonedDateTime convertLongToLocalDate(long dateTime) {
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

	public static void showListOfAccounts(Map<Long, Account> accounts) throws AppException {
		if (accounts.isEmpty()) {
			log.info("No accounts found");
		} else {
			log.info("-".repeat(65));
			log.info("ACCOUNT NUMBER | CUSTOMER ID | START DATE |  BALANCE   | STATUS");
			log.info("-".repeat(65));
			accounts.forEach((id,
					account) -> log.info(String.format("%14d | %11d | %s | %10.2f | %s", account.getAccountNumber(),
							account.getUserID(),
							account.getOpeningDateInLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE),
							account.getBalance(), account.getStatus())));
			log.info("-".repeat(65));
		}
	}

	public static void showListOfTransactions(List<Transaction> transactions) {
		log.info("-".repeat(95));
		log.info("   ID   | TITLE                | ACCOUNT NUMBER |    DATE    |   AMOUNT   |   BALANCE  | TYPE");
		log.info("-".repeat(95));
		transactions.forEach((transaction) -> log.info(
				String.format(" %-6d | %-20s | %14d | %s | %10.2f | %10.2f | %6s ", transaction.getTransactionID(),
						transaction.getRemarks(), transaction.getTransactedAccountNumber(),
						HelperUtil.convertLongToLocalDate(transaction.getDateTime())
								.format(DateTimeFormatter.ISO_LOCAL_DATE),
						transaction.getTransactedAmount(), transaction.getClosingBalance(),
						transaction.getTransactionType())));
		log.info("-".repeat(95));
	}

}
