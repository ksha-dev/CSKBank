package consoleRunner;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import consoleRunner.utility.InputUtil;
import consoleRunner.utility.LoggingUtil;
import exceptions.AppException;
import exceptions.messages.ActivityExceptionMessages;
import helpers.Account;
import helpers.Branch;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;
import operations.EmployeeOperations;
import utility.ValidatorUtil;
import utility.ConstantsUtil.AccountType;
import utility.ConstantsUtil.EmployeeType;
import utility.ConstantsUtil.ModifiableField;
import utility.ConstantsUtil.TransactionHistoryLimit;
import utility.ConstantsUtil;

class EmployeeRunner {

	public static Logger log = LoggingUtil.DEFAULT_LOGGER;

	public static void run(EmployeeRecord employee) throws AppException {
		boolean isProgramActive = true;
		int runnerOperations = 12;
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
					+ "\n10 - Update Customer details" + "\n11 - Update Password"
					+ (employee.getRole() == EmployeeType.ADMIN ? "\n12 - Go to Admin Portal" : "")
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
					LoggingUtil.logEmployeeRecord(activity.getEmployeeRecord());
					break;

				case 2: {
					int pageNumber = 1;
					boolean isListObtained = false;
					while (!isListObtained) {
						Map<Long, Account> accounts = activity.getListOfAccountsInBranch(pageNumber);
						LoggingUtil.logAccountsList(accounts);
						if (accounts.size() == ConstantsUtil.LIST_LIMIT) {
							log.info("Enter 1 to go to next page (or) 0 to exit : ");
							int select = InputUtil.getPositiveInteger();
							if (select == 1) {
								pageNumber++;
							} else {
								isListObtained = true;
							}
						} else {
							isListObtained = true;
						}
					}
				}
					break;

				case 3: {
					log.info("Enter customer Id : ");
					int customerId = InputUtil.getPositiveInteger();
					if (customerId > 0) {
						LoggingUtil.logCustomerRecord(activity.getCustomerRecord(customerId));
					}
				}
					break;

				case 4:
					createCustomer(activity);
					break;

				case 5: {
					log.info("Enter account number to get transactions : ");
					long accountNumber = InputUtil.getPositiveLong();
					if (accountNumber > 0) {
						int pageNumber = 1;
						boolean isListObtained = false;
						log.info("Enter Transaction History Size : (0 - Recent, 1, 3, or 6 months) ");
						int history = InputUtil.getPositiveInteger();
						TransactionHistoryLimit limit = null;
						switch (history) {
						case 0:
							limit = TransactionHistoryLimit.RECENT;
							break;
						case 1:
							limit = TransactionHistoryLimit.ONE_MONTH;
							break;
						case 3:
							limit = TransactionHistoryLimit.THREE_MONTH;
							break;
						case 6:
							limit = TransactionHistoryLimit.SIX_MONTH;
							break;
						default:
							throw new AppException("Invalid Transaction history Limit");
						}
						while (!isListObtained) {
							List<Transaction> transactions = activity.getListOfTransactions(accountNumber, pageNumber,
									limit);
							LoggingUtil.logTransactionsList(transactions);
							if (transactions.size() == ConstantsUtil.LIST_LIMIT) {
								log.info("Enter 1 to go to next page (or) 0 to exit : ");
								int select = InputUtil.getPositiveInteger();
								if (select == 1) {
									pageNumber++;
								} else {
									isListObtained = true;
								}
							} else {
								isListObtained = true;
							}
						}
					}
				}
					break;

				case 6: {
					log.info("Enter Customer Id : ");
					int customerId = InputUtil.getPositiveInteger();
					CustomerRecord customer = activity.getCustomerRecord(customerId);
					log.info("Enter deposit amount : ");
					double amount = InputUtil.getPositiveDouble();

					int i = 0;
					for (AccountType type : AccountType.values()) {
						log.info((i + 1) + " : " + type);
					}
					log.info("Enter the associated number to select the type of account : ");
					int typeSelection = InputUtil.getInteger();
					if (typeSelection > 0 && typeSelection <= (i + 1)) {
						Account account = activity.createAccountForExistingCustomer(customer.getUserId(),
								AccountType.values()[typeSelection - 1], amount);
						log.info("-".repeat(40));
						log.info("ACCOUNT HAS BEEN CREATED SUCCESSFULLY");
						account.logAccount();
					} else {
						log.info("Invalid Selection");
					}

				}
					break;

				case 7: {
					Branch branch = activity.getBrachDetails(employee.getBranchId());
					LoggingUtil.logBrach(branch);
				}
					break;

				case 8: {
					log.info("Enter account number to deposit amount : ");
					long accountNumber = InputUtil.getPositiveLong();

					log.info("Enter deposit amount : ");
					double amount = InputUtil.getPositiveDouble();

					long transactionId = activity.depositAmount(accountNumber, amount);

					log.info("Deposit Successful!.\nTransaction Id : " + transactionId);
				}
					break;

				case 9: {
					log.info("Enter account number to withdraw amount : ");
					long accountNumber = InputUtil.getPositiveLong();
					log.info("Enter amount to withdraw : ");
					double amount = InputUtil.getPositiveDouble();
					long transactionId = activity.withdrawAmount(accountNumber, amount);
					log.info("Withdrawal Successful!.\nTransaction Id : " + transactionId);
				}
					break;

				case 10: {
					log.info("Enter customer Id : ");
					int customerId = InputUtil.getPositiveInteger();
					if (customerId < 1) {
						log.info("Customer Id cannot be 0");
					} else {
						int i = 0;
						for (ModifiableField modifyField : ModifiableField.values()) {
							log.info((i += 1) + " : " + modifyField);
						}
						log.info("Select a field to update. Enter the corresponding number");
						int selection = InputUtil.getPositiveInteger();
						if (selection > 0 && selection <= i + 1) {
							ModifiableField field = ModifiableField.values()[selection - 1];
							Object value = null;
							if (field.equals(ModifiableField.EMAIL)) {
								log.info("Enter Email Address : ");
								value = InputUtil.getString();
								ValidatorUtil.validateEmail(value.toString());
							} else if (field.equals(ModifiableField.DATE_OF_BIRTH)) {
								log.info("Enter Date of birth in DDMMYYYY Format : ");
								value = InputUtil.getDate().toInstant().toEpochMilli();
							} else if (field.equals(ModifiableField.PHONE)) {
								log.info("Enter mobile number : ");
								value = InputUtil.getPositiveLong();
								ValidatorUtil.validateMobileNumber((long) value);
							} else if (field.equals(ModifiableField.AADHAAR_NUMBER)) {
								log.info("Enter Aadhaar number : ");
								value = InputUtil.getPositiveLong();
								ValidatorUtil.validateAadhaarNumber((long) value);
							} else if (field.equals(ModifiableField.PAN_NUMBER)) {
								log.info("Enter PAN Number : ");
								value = InputUtil.getString();
								ValidatorUtil.validatePANNumber(value.toString());
							} else {
								log.info("Enter Change Value : ");
								value = InputUtil.getString();
							}

							if (activity.updateCustomerDetails(customerId, field, value)) {
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
						if (activity.updatePassword(employee.getUserId(), currentPassword, newPasswordConfirm)) {
							log.info("Your password has been changed.");
							log.info("Logging out.");
							isProgramActive = false;
						}
					}

				}
					break;

				case 12: {
					if (employee.getRole() == EmployeeType.ADMIN) {
						new AdminRunner().run(employee);
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
		customer.setPhone(InputUtil.getPositiveLong());

		log.info("Enter Email : ");
		customer.setEmail(InputUtil.getString());

		log.info("Enter Address : ");
		customer.setAddress(InputUtil.getString());

		log.info("Enter Date of birth in DDMMYYYY Format : ");
		customer.setDateOfBirth(InputUtil.getDate());

		log.info("Enter Aadhaar Number : ");
		customer.setAadhaarNumber(InputUtil.getPositiveLong());

		log.info("Enter PAN Number : ");
		customer.setPanNumber(InputUtil.getString());

		LoggingUtil.logCustomerRecord(customer);

		log.info("Enter deposit amount : ");
		double amount = InputUtil.getPositiveDouble();

		log.info("Enter 'y' to confirm creation, 'N' to cancel");
		if (InputUtil.getString().charAt(0) == 'y') {
			int i = 0;
			for (AccountType type : AccountType.values()) {
				log.info((i + 1) + " : " + type);
			}
			log.info("Enter the associated number to select the type of account : ");
			int typeSelection = InputUtil.getInteger();
			if (typeSelection > 0 && typeSelection <= (i + 1)) {
				Account account = activity.createNewCustomerAndAccount(customer,
						AccountType.values()[typeSelection - 1], amount);
				log.info("-".repeat(40));
				log.info("CUSTOMER AND ACCOUNT HAS BEEN CREATED SUCCESSFULLY");
				activity.getCustomerRecord(account.getUserId());
				account.logAccount();
			} else {
				log.info("Invalid Selection");
			}
		} else {
			log.info("Operation cancelled.");
		}
	}
}