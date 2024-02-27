package app;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.logging.Logger;

import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import operations.EmployeeOperations;
import utility.InputUtil;
import utility.LoggingUtil;

public class EmployeeRunner {

	public static Logger log = LoggingUtil.DEFAULT_LOGGER;

	public static void run(EmployeeRecord employee) throws AppException {
		boolean isProgramActive = true;
		int runnerOperations = 4;
		EmployeeOperations activity = new EmployeeOperations(employee);

		while (isProgramActive) {
			log.info("\n" + "=".repeat(15) + "EMPLOYEE PORTAL" + "=".repeat(15)
					+ "\nEnter a number to perform the following operation : " + "\n1 - View Profile Details"
					+ "\n2 - View list of accounts of your branch" + "\n3 - View a customer details of an account"
					+ "\n4 - Open a new account for a new customer" + "" + "\n\nTo logout, enter 0\n" + "-".repeat(30));

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

				case 2:
					List<Account> accounts = activity.getListOfAccountsInBranch();
					if (accounts.isEmpty()) {
						log.info("No accounts have been openned in your branch");
					} else {
						accounts.forEach((account) -> account.logAccount());
					}
					break;

				case 3: {
					log.info("Enter Customer ID to fetch details : ");
					int customerID = InputUtil.getPositiveInteger();
					activity.getCustomerRecord(customerID).logUserRecord();
				}
					break;

				case 4:
					CustomerRecord customer = new CustomerRecord();
					log.info("Enter the following details : ");

					boolean checkStatusOfField = false;
					while (!checkStatusOfField) {
						try {
							log.info("Enter First name : ");
							customer.setFirstName(InputUtil.getString());
							checkStatusOfField = true;
						} catch (AppException e) {
							log.info(e.getMessage());
						}
					}

					checkStatusOfField = false;
					while (!checkStatusOfField) {
						try {
							log.info("Enter Last name : ");
							customer.setLastName(InputUtil.getString());
							checkStatusOfField = true;
						} catch (AppException e) {
							log.info(e.getMessage());
						}
					}

					checkStatusOfField = false;
					while (!checkStatusOfField) {
						try {
							log.info("Enter Gender : ");
							customer.setGender(InputUtil.getString());
							checkStatusOfField = true;
						} catch (AppException e) {
							log.info(e.getMessage());
						}
					}

					checkStatusOfField = false;
					while (!checkStatusOfField) {
						try {
							log.info("Enter Phone number name : ");
							customer.setMobileNumber(InputUtil.getPositiveLong());
							checkStatusOfField = true;
						} catch (AppException e) {
							log.info(e.getMessage());
						}
					}

					checkStatusOfField = false;
					while (!checkStatusOfField) {
						try {
							log.info("Enter Email : ");
							customer.setEmail(InputUtil.getString());
							checkStatusOfField = true;
						} catch (AppException e) {
							log.info(e.getMessage());
						}
					}

					checkStatusOfField = false;
					while (!checkStatusOfField) {
						try {
							log.info("Enter Address : ");
							customer.setAddress(InputUtil.getString());
							checkStatusOfField = true;
						} catch (AppException e) {
							log.info(e.getMessage());
						}
					}

					checkStatusOfField = false;
					while (!checkStatusOfField) {
						try {
							log.info("Enter Date of birth in DDMMYYYY Format : ");
							String dateOfBirth = InputUtil.getString();
							if (dateOfBirth.length() != 8) {
								throw new AppException(
										"The date of birth entered is incorrect. Please follow the given order");
							}
							int date = Integer.parseInt(dateOfBirth.substring(0, 2));
							int month = Integer.parseInt(dateOfBirth.substring(2, 4));
							int year = Integer.parseInt(dateOfBirth.substring(4, 8));

							customer.setDateOfBirth(
									ZonedDateTime.of(year, month, date, 0, 0, 0, 0, ZoneId.systemDefault()));
							checkStatusOfField = true;
						} catch (Exception e) {
							log.info(e.getMessage());
						}
					}

					checkStatusOfField = false;
					while (!checkStatusOfField) {
						try {
							log.info("Enter Aadhaar Number : ");
							customer.setAadhaarNumber(InputUtil.getPositiveLong());
							checkStatusOfField = true;
						} catch (AppException e) {
							log.info(e.getMessage());
						}
					}

					checkStatusOfField = false;
					while (!checkStatusOfField) {
						try {
							log.info("Enter PAN Number : ");
							customer.setPanNumber(InputUtil.getString());
							checkStatusOfField = true;
						} catch (AppException e) {
							log.info(e.getMessage());
						}
					}

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