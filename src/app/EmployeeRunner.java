package app;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import api.mysql.MySQLQueryUtil.Fields;
import exceptions.AppException;
import exceptions.messages.ActivityExceptionMessages;
import helpers.Account;
import helpers.Branch;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;
import operations.EmployeeOperations;
import utility.InputUtil;
import utility.LoggingUtil;
import utility.ValidatorUtil;
import utility.HelperUtil;

public class EmployeeRunner {

	public static Logger log = LoggingUtil.DEFAULT_LOGGER;

	public static void run(EmployeeRecord employee) throws AppException {
		boolean isProgramActive = true;
		int runnerOperations = 11;
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
					+ "\n6 - Open a new account for an existing customer" + "\n7 - View Employee Branch Details"
					+ "\n8 - Deposit money into an account" + "\n9 - Withdraw money from an account"
					+ "\n10 - Update Customer details" + "\n11 - Update Password" + "\n\nTo logout, enter 0\n"
					+ "-".repeat(30));

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
					Map<Long, Account> accounts = activity.getListOfAccountsInBranch();
					HelperUtil.showListOfAccounts(accounts);
				}
					break;

				case 3: {
					Map<Long, Account> accounts = activity.getListOfAccountsInBranch();
					HelperUtil.showListOfAccounts(accounts);
					long accountNumber = 0;
					if (accounts.size() == 1) {
						accountNumber = (long) accounts.keySet().toArray()[0];
					} else {
						log.info("Enter account number : ");
						accountNumber = InputUtil.getPositiveLong();
					}

					if (accounts.containsKey(accountNumber)) {
						activity.getCustomerRecord(accounts.get(accountNumber).getUserID()).logUserRecord();
					}
				}
					break;

				case 4:
					createCustomer(activity);
					break;

				case 5: {
					Map<Long, Account> accounts = activity.getListOfAccountsInBranch();
					HelperUtil.showListOfAccounts(accounts);
					long accountNumber = 0;
					if (accounts.size() == 1) {
						accountNumber = (long) accounts.keySet().toArray()[0];
					} else {
						log.info("Enter account number : ");
						accountNumber = InputUtil.getPositiveLong();
					}

					if (accounts.containsKey(accountNumber)) {
						HelperUtil.showListOfTransactions(activity.getListOfTransactions(accountNumber));
					}
				}
					break;

				case 6: {
					log.info("Enter Customer ID : ");
					int customerId = InputUtil.getPositiveInteger();
					CustomerRecord customer = activity.getCustomerRecord(customerId);
					log.info("Enter deposit amount : ");
					double amount = InputUtil.getPositiveDouble();
					Account account = activity.createAccountForExistingCustomer(customer.getUserID(), "SAVINGS",
							amount);

					log.info("-".repeat(40));
					log.info("ACCOUNT HAS BEEN CREATED SUCCESSFULLY");
					account.logAccount();
				}
					break;

				case 7: {
					Branch branch = activity.getBrachDetails(employee.getBranchID());
					branch.logBranch();
				}
					break;

				case 8: {
					log.info("Enter account number to deposit amount : ");
					long accountNumber = InputUtil.getPositiveLong();

					log.info("Enter deposit amount : ");
					double amount = InputUtil.getPositiveDouble();

					long transactionID = activity.depositAmount(accountNumber, amount);

					log.info("Deposit Successful!.\nTransaction ID : " + transactionID);
				}
					break;

				case 9: {
					log.info("Enter account number to withdraw amount : ");
					long accountNumber = InputUtil.getPositiveLong();

					log.info("Enter amount to withdraw : ");
					double amount = InputUtil.getPositiveDouble();

					long transactionID = activity.withdrawAmount(accountNumber, amount);

					log.info("Withdrawal Successful!.\nTransaction ID : " + transactionID);
				}
					break;

				case 10: {
					log.info("Enter customer ID : ");
					int customerID = InputUtil.getPositiveInteger();
					if (customerID < 1) {
						log.info("Customer ID cannot be 0");
					} else {
						List<Fields> fields = List.of(Fields.ADDRESS, Fields.EMAIL, Fields.DATE_OF_BIRTH, Fields.MOBILE,
								Fields.AADHAAR_NUMBER, Fields.PAN_NUMBER);
						int fieldCount = fields.size();
						for (int i = 0; i < fieldCount; i++) {
							log.info((i + 1) + " : " + fields.get(i));
						}
						log.info("Select a field to update. Enter the corresponding number");
						int selection = InputUtil.getPositiveInteger();
						if (selection > 0 && selection <= fieldCount) {
							Fields field = fields.get(selection - 1);
							Object value = null;
							if (field.equals(Fields.EMAIL)) {
								log.info("Enter Email Address : ");
								value = InputUtil.getString();
								ValidatorUtil.validateEmail(value.toString());
							} else if (field.equals(Fields.DATE_OF_BIRTH)) {
								log.info("Enter Date of birth in DDMMYYYY Format : ");
								String dateOfBirth = InputUtil.getString();
								if (dateOfBirth.length() != 8) {
									throw new AppException(
											"The date of birth entered is incorrect. Please follow the given order");
								}
								int date = Integer.parseInt(dateOfBirth.substring(0, 2));
								int month = Integer.parseInt(dateOfBirth.substring(2, 4));
								int year = Integer.parseInt(dateOfBirth.substring(4, 8));
								value = ZonedDateTime.of(year, month, date, 0, 0, 0, 0, ZoneId.systemDefault())
										.toInstant().toEpochMilli();
							} else if (field.equals(Fields.MOBILE)) {
								value = InputUtil.getPositiveLong();
								ValidatorUtil.validateMobileNumber((long) value);
							} else if (field.equals(Fields.AADHAAR_NUMBER)) {
								value = InputUtil.getPositiveLong();
								ValidatorUtil.validateAadhaarNumber((long) value);
							} else if (field.equals(Fields.PAN_NUMBER)) {
								value = InputUtil.getString();
								ValidatorUtil.validatePANNumber(value.toString());
							}

							if (activity.updateCustomerDetails(customerID, field, value)) {
								log.info("Update Successful");
							}
						}
					}
				}
					break;

				case 11: {
					log.info("Enter current password : ");
					String currentPassword = InputUtil.getString();
					log.info("Enter new password : ");
					String newPassword = InputUtil.getString();
					log.info("Re-enter new password for confirmation : ");
					String newPasswordConfirm = InputUtil.getString();

					ValidatorUtil.validatePassword(currentPassword);
					ValidatorUtil.validatePassword(newPasswordConfirm);
					if (newPassword.equals(newPasswordConfirm)) {
						if (activity.updatePassword(employee.getUserID(), currentPassword, newPasswordConfirm)) {
							log.info("Your password has been changed.");
							log.info("Logging out.");
							isProgramActive = false;
						}
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

		log.info("Enter deposit amount : ");
		double amount = InputUtil.getPositiveDouble();

		log.info("Enter 'y' to confirm creation, 'N' to cancel");
		if (InputUtil.getString().charAt(0) == 'y') {
			Account account = activity.createNewCustomerAndAccount(customer, "SAVINGS", amount);
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