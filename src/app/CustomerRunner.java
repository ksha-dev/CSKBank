package app;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import api.mysql.MySQLQueryUtil.Fields;
import exceptions.AppException;
import exceptions.messages.ActivityExceptionMessages;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.Transaction;
import operations.CustomerOperations;
import utility.InputUtil;
import utility.LoggingUtil;
import utility.ValidatorUtil;
import utility.HelperUtil;
import utility.HelperUtil.TransactionType;

public class CustomerRunner {

	public static Logger log = LoggingUtil.DEFAULT_LOGGER;

	public static void run(CustomerRecord customer) throws AppException {
		boolean isProgramActive = true;
		int runnerOperations = 7;
		CustomerOperations activity = new CustomerOperations(customer);

		while (isProgramActive) {

			if (!AppRunner.serverConnectionActive) {
				isProgramActive = false;
				log.info(ActivityExceptionMessages.SERVER_CONNECTION_LOST.toString());
				break;
			}

			log.info("=".repeat(15) + "CUSTOMER PORTAL" + "=".repeat(15)
					+ "\nEnter a number to perform the following operation : " + "\n1 - View Profile Details"
					+ "\n2 - Accounts" + "\n3 - View Transactions of an Account" + "\n4 - Transfer Amount"
					+ "\n5 - View Branch Details of an account" + "\n6 - Update Profile Details"
					+ "\n7 - Update password" + "\n\nTo logout, enter 0\n" + "-".repeat(30));

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
					Map<Long, Account> accounts = activity.getAssociatedAccounts();
					HelperUtil.showListOfAccounts(accounts);
					break;
				}

				case 3: {
					Map<Long, Account> accounts = activity.getAssociatedAccounts();
					HelperUtil.showListOfAccounts(accounts);
					long accountNumber = 0;
					if (accounts.size() == 1) {
						accountNumber = (long) accounts.keySet().toArray()[0];
					} else {
						log.info("Enter account number : ");
						accountNumber = InputUtil.getPositiveLong();
					}
					if (accounts.containsKey(accountNumber)) {
						List<Transaction> transactions = (activity.getTransactionsOfAccount(accountNumber));
						HelperUtil.showListOfTransactions(transactions);
					} else {
						log.info("Invalid Account number");
					}
				}
					break;

				case 4: {
					boolean isTransferInsideBank = false;
					log.info(
							"Is transfer within bank or outside bank : (Enter y for within (or) hit enter for outside)");
					if (InputUtil.getString().equals("y")) {
						isTransferInsideBank = true;
					}
					Map<Long, Account> accounts = activity.getAssociatedAccounts();
					Transaction transaction = new Transaction();
					long accountNumber = 0;
					if (accounts.size() == 1) {
						accountNumber = (long) accounts.keySet().toArray()[0];
						log.info(accountNumber + "");
					} else {
						log.info("Enter account number : ");
						accountNumber = InputUtil.getPositiveLong();

					}
					if (accounts.containsKey(accountNumber)) {
						transaction.setViewerAccountNumber(accountNumber);
					} else {
						throw new AppException("Invalid Selection");
					}
					log.info("Enter Account number of the account to transfer the money into : ");
					long transferAccountNumber = InputUtil.getLong();
					transaction.setTransactedAccountNumber(transferAccountNumber);

					log.info("Enter the amount to tranfer : ");
					double amount = InputUtil.getDouble();
					transaction.setTransactionAmount(amount);

					log.info("Enter Remarks : ");
					String remarks = InputUtil.getString();

					if (isTransferInsideBank) {
						transaction.setRemarks(remarks);
					} else {
						transaction.setRemarks("A/c No:" + transferAccountNumber + "/" + remarks);
					}
					transaction.setTransactionType(TransactionType.DEBIT.toString());
					transaction.setUserID(customer.getUserID());

					long id = activity.tranferMoney(transaction, isTransferInsideBank);
					log.info("Transaction Successful");
					log.info("Transaction ID : " + id);
				}
					break;

				case 5: {
					Map<Long, Account> accounts = activity.getAssociatedAccounts();
					HelperUtil.showListOfAccounts(accounts);
					long accountNumber = 0;
					if (accounts.size() == 1) {
						accountNumber = (long) accounts.keySet().toArray()[0];
					} else {
						log.info("Enter account number : ");
						accountNumber = InputUtil.getPositiveLong();
					}
					if (accounts.containsKey(accountNumber)) {
						activity.getBranchDetailsOfAccount(accounts.get(accountNumber).getBranchID()).logBranch();
					}
				}
					break;

				case 6: {
					List<Fields> modifiableFields = List.of(Fields.EMAIL, Fields.ADDRESS);

					log.info("Select a number to update : ");
					int fieldCount = modifiableFields.size();
					for (int i = 0; i < fieldCount; i++) {
						log.info((i + 1) + " : " + modifiableFields.get(i));
					}
					int selectedNumber = InputUtil.getPositiveInteger();
					if (selectedNumber > 0 && selectedNumber <= fieldCount) {
						Fields selectedField = modifiableFields.get(selectedNumber - 1);
						String change = InputUtil.getString();
						if (selectedField == Fields.EMAIL) {
							ValidatorUtil.validateEmail(change);
						}
						if (activity.updateUserDetails(customer.getUserID(), selectedField, change)) {
							log.info("Update successful");
						}
					}
				}
					break;

				case 7: {
					log.info("Enter current password : ");
					String currentPassword = InputUtil.getString();
					log.info("Enter new password : ");
					String newPassword = InputUtil.getString();
					log.info("Re-enter new password for confirmation : ");
					String newPasswordConfirm = InputUtil.getString();

					ValidatorUtil.validatePassword(currentPassword);
					ValidatorUtil.validatePassword(newPasswordConfirm);
					if (newPassword.equals(newPasswordConfirm)) {
						if (activity.updatePassword(customer.getUserID(), currentPassword, newPasswordConfirm)) {
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
}