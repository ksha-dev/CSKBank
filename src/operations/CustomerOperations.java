package operations;

import java.util.List;

import api.UserAPI;
import api.mysql.MySQLGeneralAPI;
import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.Transaction;
import utility.ValidatorUtil;

public class CustomerOperations {

	private CustomerRecord currentCustomer;

	private UserAPI api = new MySQLGeneralAPI();

	public CustomerOperations(CustomerRecord customer) throws AppException {
		ValidatorUtil.validateObject(customer);
		ValidatorUtil.validatePostiveNumber(customer.getUserID());
		this.currentCustomer = customer;
	}

	public CustomerRecord getCustomerRecord() throws AppException {
		return (CustomerRecord) api.getUserDetails(currentCustomer.getUserID());
	}

	public List<Account> getAssociatedAccounts() throws AppException {
		return api.getAccountsOfUser(currentCustomer.getUserID());
	}

	public List<Transaction> getTransactionsOfAccount(long accountNumber) throws AppException {
		return api.getTransactionsOfAccount(accountNumber);
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
