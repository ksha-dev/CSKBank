package operations;

import java.util.List;

import api.GeneralAPI;
import api.mysql.MySQLGeneralAPI;
import exceptions.APIExceptionMessage;
import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.Transaction;
import utility.ValidatorUtil;

public class CustomerOperations {

	private CustomerRecord currentCustomer;

	private GeneralAPI api = new MySQLGeneralAPI();

	public CustomerOperations(CustomerRecord customer) throws AppException {
		ValidatorUtil.validateObject(customer);
		ValidatorUtil.validatePostiveNumber(customer.getUserID());

		this.currentCustomer = customer;
	}

	public CustomerRecord getCustomerRecord() throws AppException {
		try {
			currentCustomer = (CustomerRecord) api.getUserDetails(currentCustomer.getUserID());
		} catch (AppException e) {
			throw new AppException(APIExceptionMessage.CANNOT_FETCH_DETAILS);
		}
		return currentCustomer;
	}

	public List<Account> getAssociatedAccounts() throws AppException {
		List<Account> listOfAssociatedAccounts;
		listOfAssociatedAccounts = api.getAccountsOfUser(currentCustomer.getUserID());
		return listOfAssociatedAccounts;
	}

	public List<Transaction> getTransactionsOfAccount(long accountNumber) throws AppException {
		List<Transaction> listOfTransactions;
		listOfTransactions = api.getTransactionsOfAccount(accountNumber);
		return listOfTransactions;
	}

	public double getAccountBalance(long accountNumber) throws AppException {
		ValidatorUtil.validatePostiveNumber(accountNumber);
		return api.getBalanceInAccount(accountNumber);
	}
	
	public long tranferMoney(Transaction helperTransaction, boolean isTransferOutsideBank) throws AppException {
		ValidatorUtil.validateObject(helperTransaction);
		return api.transferAmount(helperTransaction, isTransferOutsideBank);
	}
}
