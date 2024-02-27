package app;

// Github token - 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

import java.util.logging.Logger;

import exceptions.APIExceptionMessage;
import exceptions.AppException;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.UserRecord;
import operations.HomeOperations;

import utility.InputUtil;
import utility.LoggingUtil;
import utility.ValidatorUtil;
import utility.SchemaUtil.UserTypes;

public class AppRunner {
	public static final Logger log = LoggingUtil.DEFAULT_LOGGER;

	public static void main(String... args) throws AppException {
		int runnerChoices = 3;
		boolean isAppActive = true;
		log.info("-".repeat(30) + "\n" + String.format("%25s", "WELCOME TO CSK BANK\n") + "-".repeat(30));
		while (isAppActive) {

			log.info("=".repeat(15) + "HOME" + "=".repeat(15) + "\nEnter a number to perform the following operation : "
					+ "\n1 - Sign in to access your information" + "\n2 - Apply for a new account"
					+ "\n\nTo exit, enter 0\n" + "-".repeat(30));

			int choice = -1;
			do {

				log.info("Enter your choice : ");
				choice = InputUtil.getPositiveInteger();
				if (choice < 0 || choice > runnerChoices) {
					log.warning("Invalid number. Please enter a number between 0 and " + runnerChoices);
				}
			} while (choice < 0 || choice > runnerChoices);

			try {
				switch (choice) {
				case 0:
					log.info("-".repeat(40));
					log.info("Thank you for visiting us");
					isAppActive = false;
					break;

				case 1: {
					HomeOperations activity = new HomeOperations();
					UserRecord user = null;
					int numberOfAttemps = 3;
					while (ValidatorUtil.isObjectNull(user) && numberOfAttemps > 0) {
						try {
							log.info("Enter your User ID (or 0 to exit): ");
							int userID = InputUtil.getPositiveInteger();
							if (userID == 0) {
								break;
							}
							log.info("Enter your password : ");
							String password = InputUtil.getString();
							user = activity.authenticateUser(userID, password);
						} catch (AppException e) {
							log.info(e.getMessage());
							if (e.getMessage().equals(APIExceptionMessage.USER_AUNTHENTICATION_FAILED.toString())) {
								numberOfAttemps--;
							}
							if (numberOfAttemps > 1) {
								log.info("You have " + numberOfAttemps + " more attemps to login");
							} else {
								log.info("Login Failed. Please try again after sometime.");
							}
						}
					}
					if (!ValidatorUtil.isObjectNull(user)) {
						if (user.getType().equals(UserTypes.EMPLOYEE)) {
							EmployeeRunner.run((EmployeeRecord) user);
						} else if (user.getType().equals(UserTypes.CUSTOMER)) {
							CustomerRunner.run((CustomerRecord) user);
						}
					}
				}
					break;
				}
			} catch (Exception e) {
				LoggingUtil.logSever(e);
			}
			log.info("-".repeat(40));
		}
	}
}
