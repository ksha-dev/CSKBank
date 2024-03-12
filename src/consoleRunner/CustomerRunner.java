package consoleRunner;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import consoleRunner.utility.InputUtil;
import consoleRunner.utility.LoggingUtil;
import exceptions.AppException;
import exceptions.messages.ActivityExceptionMessages;
import modules.Account;
import modules.Branch;
import modules.CustomerRecord;
import modules.Transaction;
import operations.CustomerOperations;
import utility.ValidatorUtil;
import utility.ConstantsUtil;
import utility.ConstantsUtil.ModifiableField;
import utility.ConstantsUtil.TransactionHistoryLimit;
import utility.ConstantsUtil.TransactionType;

class CustomerRunner {

	public static Logger log = LoggingUtil.DEFAULT_LOGGER;

	public static void run(CustomerRecord customer) throws AppException {
		boolean isProgramActive = true;
		int runnerOperations = 8;
		CustomerOperations operations = new CustomerOperations(customer);

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
					+ "\n7 - Update password" + "\n8 - View recent transactions" + "\n\nTo logout, enter 0\n"
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
					LoggingUtil.logCustomerRecord(operations.getCustomerRecord());
					break;
				}

				case 2: {
					Map<Long, Account> accounts = operations.getAssociatedAccounts();
					LoggingUtil.logAccountsList(accounts);
					break;
				}

				case 3: {
					Map<Long, Account> accounts = operations.getAssociatedAccounts();
					long accountNumber = 0;
					if (accounts.size() == 1) {
						accountNumber = (long) accounts.keySet().toArray()[0];
					} else {
						LoggingUtil.logAccountsList(accounts);
						log.info("Enter account number : ");
						accountNumber = InputUtil.getPositiveLong();
					}
					if (accounts.containsKey(accountNumber)) {
						boolean isTransactionListObtained = false;
						int pageNumber = 1;
						log.info("Enter Transaction History Size : (1, 3, or 6 months) ");
						int history = InputUtil.getPositiveInteger();
						TransactionHistoryLimit limit = null;
						switch (history) {
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
							throw new IllegalArgumentException("Invalid Transaction history Limit");
						}
						while (!isTransactionListObtained) {
							List<Transaction> transactions = (operations.getTransactionsOfAccount(accountNumber,
									pageNumber, limit));
							LoggingUtil.logTransactionsList(transactions);
							if (transactions.size() == ConstantsUtil.LIST_LIMIT) {
								log.info("Enter 1 to go to next page (or) 0 to exit : ");
								int select = InputUtil.getPositiveInteger();
								if (select == 1) {
									pageNumber++;
								} else {
									isTransactionListObtained = true;
								}
							} else {
								isTransactionListObtained = true;
							}
						}
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
					Map<Long, Account> accounts = operations.getAssociatedAccounts();
					Transaction transaction = new Transaction();
					long accountNumber = 0;
					if (accounts.size() == 1) {
						accountNumber = (long) accounts.keySet().toArray()[0];
						log.info(accountNumber + "");
					} else {
						log.info("Enter account number to transfer amount from: ");
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
					transaction.setTransactedAmount(amount);

					log.info("Enter Remarks : ");
					String remarks = InputUtil.getString();

					if (isTransferInsideBank) {
						transaction.setRemarks(remarks);
					} else {
						transaction.setRemarks("A/c No:" + transferAccountNumber + "/" + remarks);
					}
					transaction.setTransactionType(TransactionType.DEBIT.getTransactionTypeId());
					transaction.setUserId(customer.getUserId());

					log.info("Enter PIN to confirm : ");
					String pin = InputUtil.getPIN();

					long id = operations.tranferMoney(transaction, isTransferInsideBank, pin);
					log.info("Transaction Successful");
					log.info("Transaction Id : " + id);
				}
					break;

				case 5: {
					Map<Long, Account> accounts = operations.getAssociatedAccounts();
					LoggingUtil.logAccountsList(accounts);
					long accountNumber = 0;
					if (accounts.size() == 1) {
						accountNumber = (long) accounts.keySet().toArray()[0];
					} else {
						log.info("Enter account number : ");
						accountNumber = InputUtil.getPositiveLong();
					}
					if (accounts.containsKey(accountNumber)) {
						Branch branch = operations.getBranchDetailsOfAccount(accounts.get(accountNumber).getBranchId());
						LoggingUtil.logBrach(branch);
					}
				}
					break;

				case 6: {

					log.info("Select a number to update : ");
					int fieldCount = ConstantsUtil.USER_MODIFIABLE_FIELDS.size();
					for (int i = 0; i < fieldCount; i++) {
						log.info((i + 1) + " : " + ConstantsUtil.USER_MODIFIABLE_FIELDS.get(i));
					}
					int selectedNumber = InputUtil.getPositiveInteger();
					if (selectedNumber > 0 && selectedNumber <= fieldCount) {
						ModifiableField selectedField = ConstantsUtil.USER_MODIFIABLE_FIELDS.get(selectedNumber - 1);
						Object change = null;
						if (selectedField == ModifiableField.EMAIL) {
							change = InputUtil.getString();
							ValidatorUtil.validateEmail(change.toString());
						} else if (selectedField == ModifiableField.PHONE) {
							change = InputUtil.getPositiveLong();
							ValidatorUtil.validateMobileNumber((long) change);
						} else {
							change = InputUtil.getString();
						}
						log.info("Enter PIN to confirm changes : ");
						String pin = InputUtil.getPIN();
						if (operations.updateUserDetails(customer.getUserId(), selectedField, change, pin)) {
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

					log.info("Enter your 4 digit PIN to confirm : ");
					String pin = InputUtil.getPIN();
					if (newPassword.equals(newPasswordConfirm)) {
						if (operations.updatePassword(customer.getUserId(), currentPassword, newPasswordConfirm, pin)) {
							log.info("Your password has been changed.");
							log.info("Logging out.");
							isProgramActive = false;
						}
					}
				}
					break;

				case 8: {
					Map<Long, Account> accounts = operations.getAssociatedAccounts();
					long accountNumber = 0;
					if (accounts.size() == 1) {
						accountNumber = (long) accounts.keySet().toArray()[0];
					} else {
						LoggingUtil.logAccountsList(accounts);
						log.info("Enter account number : ");
						accountNumber = InputUtil.getPositiveLong();
					}
					if (accounts.containsKey(accountNumber)) {
						List<Transaction> transactions = (operations.getTransactionsOfAccount(accountNumber, 1,
								TransactionHistoryLimit.RECENT));
						LoggingUtil.logTransactionsList(transactions);

					} else {
						log.info("Invalid Account number");
					}
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