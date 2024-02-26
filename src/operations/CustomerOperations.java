package operations;

import java.util.List;

import apis.API;
import apis.mysql.MySQLAPI;
import exceptions.APIExceptionMessage;
import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.Transaction;
import utility.ValidatorUtil;

public class CustomerOperations {
	
	private CustomerRecord currentCustomer;

	private API api = new MySQLAPI();

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
}
