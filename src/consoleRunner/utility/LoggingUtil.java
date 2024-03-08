package consoleRunner.utility;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;
import helpers.UserRecord;
import utility.ConvertorUtil;
import utility.ConstantsUtil;
import utility.ValidatorUtil;

public class LoggingUtil {
	private static final String DEFAULT_LOGGER_NAME = ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
			+ "_CSK_Bank.log";
	public static final Logger DEFAULT_LOGGER = Logger.getLogger(DEFAULT_LOGGER_NAME);
	public static final String DEFAULT_FILE_PATH = System.getProperty("user.dir");
	public static final String DEFAULT_SEPARATOR = File.separator;
	public static final String DEFAULT_LOGGER_PATH = DEFAULT_FILE_PATH + DEFAULT_SEPARATOR + "logs";

	static {
//		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s\n");

		try {
			checkPath(DEFAULT_LOGGER_PATH);
			FileHandler fileHander = new FileHandler(DEFAULT_LOGGER_PATH + DEFAULT_SEPARATOR + DEFAULT_LOGGER_NAME);
			DEFAULT_LOGGER.addHandler(fileHander);
			Formatter formatter = new SimpleFormatter();
			fileHander.setFormatter(formatter);
		} catch (Exception e) {
		}
	}

	public static void checkPath(String path) throws AppException {
		ValidatorUtil.validateObject(path);
		if (!path.trim().isEmpty()) {
			File pathDir = new File(path);
			if (!pathDir.exists()) {
				pathDir.mkdir();
			}
		}
	}

	public static void logSever(Exception e) {
		DEFAULT_LOGGER.log(Level.SEVERE, e.getMessage());
	}

	public static void logAccountsList(Map<Long, Account> accounts) throws AppException {
		ValidatorUtil.validateObject(accounts);
		if (accounts.isEmpty()) {
			DEFAULT_LOGGER.info("No accounts found");
		} else {
			DEFAULT_LOGGER.info("-".repeat(65));
			DEFAULT_LOGGER.info("ACCOUNT NUMBER | CUSTOMER ID | START DATE |  BALANCE   | STATUS");
			DEFAULT_LOGGER.info("-".repeat(65));
			accounts.forEach((id,
					account) -> DEFAULT_LOGGER.info(String.format("%14d | %11d | %s | %10.2f | %s",
							account.getAccountNumber(), account.getUserId(),
							account.getOpeningDateInLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE),
							account.getBalance(), account.getStatus())));
			DEFAULT_LOGGER.info("-".repeat(65));
		}
	}

	private static void logUserRecord(UserRecord user) throws AppException {
		ValidatorUtil.validateObject(user);
		Logger log = LoggingUtil.DEFAULT_LOGGER;
		DEFAULT_LOGGER.info("-".repeat(40));
		DEFAULT_LOGGER.info(String.format("%-40s", user.getType() + " DETAILS"));
		DEFAULT_LOGGER.info("-".repeat(40));
		DEFAULT_LOGGER.info(String.format("%-20s", "USER ID") + " : " + user.getUserId());
		DEFAULT_LOGGER.info(String.format("%-20s", "FIRST NAME") + " : " + user.getFirstName());
		DEFAULT_LOGGER.info(String.format("%-20s", "LAST NAME") + " : " + user.getLastName());
		DEFAULT_LOGGER.info(String.format("%-20s", "DATE OF BIRTH") + " : " + user.getDateOfBirthInLocalDate());
		DEFAULT_LOGGER.info(String.format("%-20s", "GENDER") + " : " + user.getGender());
		DEFAULT_LOGGER.info(String.format("%-20s", "ADDRESS") + " : " + user.getAddress());
		DEFAULT_LOGGER.info(String.format("%-20s", "E-MAIL") + " : " + user.getEmail());
	}

	public static void logCustomerRecord(CustomerRecord customer) throws AppException {
		logUserRecord(customer);
		DEFAULT_LOGGER.info(String.format("%-20s", "AADHAAR") + " : " + customer.getAadhaarNumber());
		DEFAULT_LOGGER.info(String.format("%-20s", "PAN") + " : " + customer.getPanNumber());
	}

	public static void logEmployeeRecord(EmployeeRecord employee) throws AppException {
		logUserRecord(employee);
		LoggingUtil.DEFAULT_LOGGER.info(String.format("%-20s", "ROLE") + " : " + employee.getRole());
		LoggingUtil.DEFAULT_LOGGER.info(String.format("%-20s", "BRANCH ID") + " : " + employee.getBranchId());

	}

	public static void logTransactionsList(List<Transaction> transactions) throws AppException {
		ValidatorUtil.validateObject(transactions);
		if (transactions.isEmpty()) {

		}
		DEFAULT_LOGGER.info("-".repeat(115));
		DEFAULT_LOGGER.info("   ID   | PARTICULARS" + " ".repeat(29)
				+ " | ACCOUNT NUMBER |    DATE    |   AMOUNT   |   BALANCE  | TYPE");
		DEFAULT_LOGGER.info("-".repeat(115));
		transactions.forEach((transaction) -> ConstantsUtil.log
				.info(String.format(" %-6d | %-40s | %14d | %s | %10.2f | %10.2f | %s ", transaction.getTransactionId(),
						transaction.getRemarks(), transaction.getTransactedAccountNumber(),
						ConvertorUtil.convertLongToLocalDate(transaction.getTimeStamp())
								.format(DateTimeFormatter.ISO_LOCAL_DATE),
						transaction.getTransactedAmount(), transaction.getClosingBalance(),
						transaction.getTransactionType())));
		DEFAULT_LOGGER.info("-".repeat(115));
	}

	public static void logEmployeeRecordList(Map<Integer, EmployeeRecord> employees) throws AppException {
		ValidatorUtil.validateObject(employees);
		employees.forEach((i, e) -> {
			try {
				logEmployeeRecord(e);
			} catch (AppException e1) {
				DEFAULT_LOGGER.info(e1.getMessage());
			}
		});
	}
}
