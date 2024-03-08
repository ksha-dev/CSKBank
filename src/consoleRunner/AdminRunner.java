package consoleRunner;

import java.util.Map;
import java.util.logging.Logger;

import consoleRunner.utility.InputUtil;
import consoleRunner.utility.LoggingUtil;
import exceptions.AppException;
import exceptions.messages.ActivityExceptionMessages;
import helpers.EmployeeRecord;
import operations.AdminOperations;
import utility.ConstantsUtil;

class AdminRunner {

	public Logger log = LoggingUtil.DEFAULT_LOGGER;

	public void run(EmployeeRecord employee) throws AppException {
		AdminOperations operations = new AdminOperations(employee);
		LoggingUtil.logEmployeeRecord(employee);
		boolean isProgramActive = true;
		int runnerOperations = 8;

		while (isProgramActive) {

			if (!AppRunner.serverConnectionActive) {
				isProgramActive = false;
				log.info(ActivityExceptionMessages.SERVER_CONNECTION_LOST.toString());
				break;
			}

			log.info("=".repeat(15) + "ADMIN PORTAL" + "=".repeat(15)
					+ "\nEnter a number to perform the following operation : "
					+ "\n1 - Get all the employees in your branch" + "\n2 - Get all the employees of any branch"
					+ "\n3 - Get all the employee records" + "\n\nTo go back, enter 0\n" + "-".repeat(30));

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
					isProgramActive = false;
					break;
				}

				case 1: {
					int pageNumber = 1;
					boolean isListFull = false;
					do {
						Map<Integer, EmployeeRecord> employees = operations.getEmployeesInBrach(employee.getBranchId(),
								pageNumber);
						LoggingUtil.logEmployeeRecordList(employees);
						isListFull = employees.size() == ConstantsUtil.LIST_LIMIT;
						if (isListFull) {
							log.info("Enter 1 to go to next page (or) 0 to exit : ");
							if (InputUtil.getPositiveInteger() == 1) {
								pageNumber++;
							} else {
								isListFull = false;
							}
						}
					} while (isListFull);
				}
					break;

				default:
					log.info("The choice is invalid");
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info(e.getMessage());
			}
		}
	}
}