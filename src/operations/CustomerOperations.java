package operations;

import java.util.List;
import java.util.Map;

import api.UserAPI;
import api.mysql.MySQLQueryUtil.Fields;
import api.mysql.MySQLUserAPI;
import exceptions.AppException;
import helpers.Account;
import helpers.Branch;
import helpers.CustomerRecord;
import helpers.Transaction;
import utility.ValidatorUtil;

public class CustomerOperations {

	private CustomerRecord currentCustomer;

	private UserAPI api = new MySQLUserAPI();

	public CustomerOperations(CustomerRecord customer) throws AppException {
		ValidatorUtil.validateObject(customer);
		ValidatorUtil.validatePositiveNumber(customer.getUserID());
		this.currentCustomer = customer;
	}

	public CustomerRecord getCustomerRecord() throws AppException {
		return (CustomerRecord) api.getUserDetails(currentCustomer.getUserID());
	}

	public Map<Long, Account> getAssociatedAccounts() throws AppException {
		return api.getAccountsOfUser(currentCustomer.getUserID());
	}

	public List<Transaction> getTransactionsOfAccount(long accountNumber) throws AppException {
		return api.getTransactionsOfAccount(accountNumber);
	}

	public double getAccountBalance(long accountNumber) throws AppException {
		ValidatorUtil.validatePositiveNumber(accountNumber);
		return api.getBalanceInAccount(accountNumber);
	}

	public long tranferMoney(Transaction helperTransaction, boolean isTransferOutsideBank) throws AppException {
		ValidatorUtil.validateObject(helperTransaction);
		return api.transferAmount(helperTransaction, isTransferOutsideBank);
	}

	public Branch getBranchDetailsOfAccount(int branchID) throws AppException {
		return api.getBrachDetails(branchID);
	}

	public boolean updateUserDetails(int userID, Fields field, Object value) throws AppException {
		return api.updateProfile(userID, field, value);
	}

	public boolean updatePassword(int customerID, String oldPassword, String newPassword) throws AppException {
		return api.updatePassword(customerID, oldPassword, newPassword);
	}
}
