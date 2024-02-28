package app;

import java.util.List;
import java.util.logging.Logger;

import exceptions.ActivityExceptionMessages;
import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.Transaction;
import operations.CustomerOperations;
import utility.InputUtil;
import utility.LoggingUtil;
import utility.HelperUtil;
import utility.HelperUtil.TransactionType;

public class CustomerRunner {

	public static Logger log = LoggingUtil.DEFAULT_LOGGER;

	public static void run(CustomerRecord customer) throws AppException {
		boolean isProgramActive = true;
		int runnerOperations = 5;
		CustomerOperations activity = new CustomerOperations(customer);

		while (isProgramActive) {
			
			if(!AppRunner.serverConnectionActive) {
				isProgramActive = false;
				log.info(ActivityExceptionMessages.SERVER_CONNECTION_LOST.toString());
				break;
			}
			
			log.info("=".repeat(15) + "CUSTOMER PORTAL" + "=".repeat(15)
					+ "\nEnter a number to perform the following operation : " + "\n1 - View Profile Details"
					+ "\n2 - Accounts" + "\n3 - View Transactions of an Account" + "\n4 - Transfer Amount"
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
					HelperUtil.showListOfAccounts(accounts);
					break;
				}

				case 3: {
					List<Account> accounts = activity.getAssociatedAccounts();
					int numberOfAccounts = accounts.size();
					HelperUtil.showListOfAccounts(accounts);
					log.info("Enter the associated serial number : ");
					int selectedNumber = InputUtil.getPositiveInteger();
					if (selectedNumber <= numberOfAccounts && selectedNumber > 0) {
						List<Transaction> transactions = (activity
								.getTransactionsOfAccount(accounts.get(selectedNumber - 1).getAccountNumber()));
						HelperUtil.showListOfTransactions(transactions);
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
					List<Account> accounts = activity.getAssociatedAccounts();
					Transaction transaction = new Transaction();
					if (accounts.size() == 1) {
						transaction.setViewerAccountNumber(accounts.get(0).getAccountNumber());
					} else {
						HelperUtil.showListOfAccounts(accounts);
						log.info("Select an account to transfer money ");
						int selectedNumber = InputUtil.getPositiveInteger();
						if (selectedNumber > 0 && selectedNumber <= accounts.size()) {
							transaction.setViewerAccountNumber(accounts.get(selectedNumber - 1).getAccountNumber());
						} else
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