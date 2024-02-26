package apis;

import java.util.List;

import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;
import helpers.UserRecord;

public interface API {
	
	public boolean authenticateUser(int userID, String password) throws AppException;

	public UserRecord getUserDetails(int userID) throws AppException;

	public int createUser(UserRecord user) throws AppException;

	public boolean createCustomer(CustomerRecord customer) throws AppException;
	
	public boolean createEmployee(EmployeeRecord employee) throws AppException;

	public int createAccount(int customerID, String type, int branchID, double deposiAmount) throws AppException;

	public Account getAccountDetails(int accountNumber) throws AppException;
	
	public List<Account> getAccountsOfUser(int userID) throws AppException;
	
	public List<Account> viewAccountsInBranch(int branchID) throws AppException;
	
	public List<Transaction> getTransactionsOfAccount(long accountNumber) throws AppException;
}
