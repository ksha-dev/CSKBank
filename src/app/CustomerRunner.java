package app;

import java.util.List;
import java.util.logging.Logger;

import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import operations.CustomerOperations;
import utility.InputUtil;
import utility.LoggingUtil;

public class CustomerRunner {

	public static Logger log = LoggingUtil.DEFAULT_LOGGER;

	public static void run(CustomerRecord customer) throws AppException {
		boolean isProgramActive = true;
		int runnerOperations = 5;
		CustomerOperations activity = new CustomerOperations(customer);

		while (isProgramActive) {
			log.info("=".repeat(15) + "CUSTOMER PORTAL" + "=".repeat(15)
					+ "\nEnter a number to perform the following operation : " + "\n1 - View Profile Details"
					+ "\n2 - Accounts" + "\n3 - View Balance of an Account" + "\n4 - Transfer Amount"
					+ "\n5 - Update Profile" + "" + "\n\nTo logout, enter 0\n" + "-".repeat(30));

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
				case 0: {
					log.info("Enter 'YES' to confirm logout : ");
					if (InputUtil.getString().equals("YES")) {
						isProgramActive = false;
						log.info("Logged out successfully.");
					} else {
						log.info("Logout cancelled");
					}
					break;
				}

				case 1: {
					activity.getCustomerRecord().logUserRecord();
					break;
				}

				case 2: {
					List<Account> accounts = activity.getAssociatedAccounts();
					if (accounts.isEmpty()) {
						log.info("No accounts are mapped with the user yet.");
					} else {
						log.info("Accounts mapped to the user : " + accounts.size());
						accounts.forEach((account) -> account.logAccount());
					}
					break;
				}

				case 3: {
					List<Account> accounts = activity.getAssociatedAccounts();
					int numberOfAccounts = accounts.size();
					if (numberOfAccounts > 0) {
						log.info("Please select an account to view balance");
						for (int i = 0; i < numberOfAccounts; i++) {
							log.info(i + 1 + " -> " + accounts.get(i).getAccountNumber());
						}

						log.info("Enter the associated number : ");
						int selectedNumber = InputUtil.getPositiveInteger();
						if (selectedNumber <= numberOfAccounts && selectedNumber > 0) {
							accounts.get(selectedNumber - 1).logAccount();
						
						} else {
							log.info("Invalid Selection");
						}
					} else {
						log.info("No accounts are mapped to the user");
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
}