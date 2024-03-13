package consoleRunner;

import java.util.logging.Logger;

import consoleRunner.utility.InputUtil;
import consoleRunner.utility.LoggingUtil;
import modules.CustomerRecord;
import modules.EmployeeRecord;
import modules.UserRecord;
import operations.AppOperations;
import utility.ValidatorUtil;
import utility.ConstantsUtil.UserType;

public class AppRunner {
	public static final Logger log = LoggingUtil.DEFAULT_LOGGER;

	public static boolean serverConnectionActive = true;

	public static void runConsoleApp(String... args) {
		int runnerChoices = 3;
		boolean isAppActive = true;

		AppOperations operations = new AppOperations();

		log.info("-".repeat(30) + "\n" + String.format("%25s", "WELCOME TO CSK BANK\n") + "-".repeat(30));
		while (isAppActive) {

			log.info("=".repeat(15) + "HOME" + "=".repeat(15) + "\nEnter a number to perform the following operation : "
					+ "\n1 - Sign in to access your information" + "\n2 - Apply for a new account"
					+ "\n\nTo exit, enter 0\n" + "-".repeat(30));

			int choice = -1;
			try {
				do {
					log.info("Enter your choice : ");
					choice = InputUtil.getPositiveInteger();
					if (choice < 0 || choice > runnerChoices) {
						log.warning("Invalid number. Please enter a number between 0 and " + runnerChoices);
					}
				} while (choice < 0 || choice > runnerChoices);

				switch (choice) {
				case 0:
					log.info("-".repeat(40));
					log.info("Thank you for visiting us");
					isAppActive = false;
					break;

				case 1: {
					UserRecord user = null;

					log.info("Enter your User ID (or 0 to exit): ");
					int userID = InputUtil.getPositiveInteger();
					if (userID == 0) {
						break;
					}
					log.info("Enter your password : ");
					String password = InputUtil.getString();
					ValidatorUtil.validatePassword(password);
					user = operations.getUser(userID, password);
					if (user.getType() == UserType.CUSTOMER) {
						LoggingUtil.logCustomerRecord((CustomerRecord) user);
						CustomerRunner.run((CustomerRecord) user);
					} else if (user.getType() == UserType.EMPLOYEE || user.getType() == UserType.ADMIN) {
						LoggingUtil.logEmployeeRecord((EmployeeRecord) user);
						EmployeeRunner.run((EmployeeRecord) user);
					}
				}
					break;
				}
			} catch (Exception e) {

				LoggingUtil.logSever(e);
				e.printStackTrace();
			}
			log.info("-".repeat(40));
		}
	}
}
