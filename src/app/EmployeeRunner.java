package app;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.logging.Logger;

import exceptions.ActivityExceptionMessages;
import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;
import operations.EmployeeOperations;
import utility.InputUtil;
import utility.LoggingUtil;
import utility.HelperUtil;

public class EmployeeRunner {

	public static Logger log = LoggingUtil.DEFAULT_LOGGER;

	public static void run(EmployeeRecord employee) throws AppException {
		boolean isProgramActive = true;
		int runnerOperations = 5;
		EmployeeOperations activity = new EmployeeOperations(employee);

		while (isProgramActive) {

			if (!AppRunner.serverConnectionActive) {
				isProgramActive = false;
				log.info(ActivityExceptionMessages.SERVER_CONNECTION_LOST.toString());
				break;
			}

			log.info("\n" + "=".repeat(15) + "EMPLOYEE PORTAL" + "=".repeat(15)
					+ "\nEnter a number to perform the following operation : " + "\n1 - View Profile Details"
					+ "\n2 - View list of accounts of your branch" + "\n3 - View a customer details of an account"
					+ "\n4 - Open a new account for a new customer" + "\n5 - View transactions of an account"
					+ "\n\nTo logout, enter 0\n" + "-".repeat(30));

			int choice = -1;
			do {
				try {
					log.info("Enter your choice (0 to " + runnerOperations + "): ");
					choice = InputUtil.getInteger();
				} catch (Exception e) {
					log.info(e.getMessage());
				}
			} while (choice < 0 || choice > runnerOperations);

			try {
				switch (choice) {
				case 0:
					log.info("Enter 'YES' to confirm logout : ");
					if (InputUtil.getString().equals("YES")) {
						isProgramActive = false;
						log.info("Logged out successfully.");
					} else {
						log.info("Logout cancelled");
					}
					break;

				case 1:
					activity.getEmployeeRecord().logUserRecord();
					break;

				case 2: {
					List<Account> accounts = activity.getListOfAccountsInBranch();
					HelperUtil.showListOfAccounts(accounts);
				}
					break;

				case 3: {
					List<Account> accounts = activity.getListOfAccountsInBranch();
					int numberOfAccounts = accounts.size();
					HelperUtil.showListOfAccounts(accounts);
					log.info("Enter the serial number of the account number you want to view the customer : ");
					int serialNumber = InputUtil.getPositiveInteger();
					if (serialNumber > 0 && serialNumber <= numberOfAccounts) {
						activity.getCustomerRecord(accounts.get(serialNumber - 1).getUserID()).logUserRecord();
					} else {
						log.info(serialNumber + "");
					}
				}
					break;

				case 4:
					createCustomer(activity);
					break;

				case 5: {
					List<Account> accounts = activity.getListOfAccountsInBranch();
					int numberOfAccounts = accounts.size();
					HelperUtil.showListOfAccounts(accounts);
					log.info("Enter the associated serial number : ");
					int selectedNumber = InputUtil.getPositiveInteger();
					if (selectedNumber <= numberOfAccounts && selectedNumber > 0) {
						List<Transaction> transactions = (activity
								.getListOfTransactions(accounts.get(selectedNumber - 1).getAccountNumber()));
						HelperUtil.showListOfTransactions(transactions);
					}
				}
					break;

				default:
					log.info("The choice is invalid");
					break;
				}
			} catch (Exception e) {
				log.info(e.getMessage());
			}
		}
	}

	private static void createCustomer(EmployeeOperations activity) throws AppException {
		CustomerRecord customer = new CustomerRecord();
		log.info("Enter the following details : ");

		log.info("Enter First name : ");
		customer.setFirstName(InputUtil.getString());

		log.info("Enter Last name : ");
		customer.setLastName(InputUtil.getString());

		log.info("Enter Gender : ");
		customer.setGender(InputUtil.getString());

		log.info("Enter Phone number : ");
		customer.setMobileNumber(InputUtil.getPositiveLong());

		log.info("Enter Email : ");
		customer.setEmail(InputUtil.getString());

		log.info("Enter Address : ");
		customer.setAddress(InputUtil.getString());

		log.info("Enter Date of birth in DDMMYYYY Format : ");
		String dateOfBirth = InputUtil.getString();
		if (dateOfBirth.length() != 8) {
			throw new AppException("The date of birth entered is incorrect. Please follow the given order");
		}
		int date = Integer.parseInt(dateOfBirth.substring(0, 2));
		int month = Integer.parseInt(dateOfBirth.substring(2, 4));
		int year = Integer.parseInt(dateOfBirth.substring(4, 8));

		customer.setDateOfBirth(ZonedDateTime.of(year, month, date, 0, 0, 0, 0, ZoneId.systemDefault()));

		log.info("Enter Aadhaar Number : ");
		customer.setAadhaarNumber(InputUtil.getPositiveLong());

		log.info("Enter PAN Number : ");
		customer.setPanNumber(InputUtil.getString());

		customer.logUserRecord();
		log.info("Enter 'y' to confirm creation, 'N' to cancel");
		if (InputUtil.getString().charAt(0) == 'y') {
			Account account = activity.createNewCustomerAndAccount(customer, "SAVINGS", 10000.0);
			customer = activity.getCustomerRecord(customer.getUserID());

			log.info("-".repeat(40));
			log.info("CUSTOMER AND ACCOUNT HAS BEEN CREATED SUCCESSFULLY");
			customer.logUserRecord();
			account.logAccount();
		} else {
			log.info("Operation cancelled.");
		}
	}
}