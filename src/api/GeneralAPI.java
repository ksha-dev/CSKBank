package api;

import java.util.List;

import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;
import helpers.UserRecord;

public interface GeneralAPI {
	
	public boolean authenticateUser(int userID, String password) throws AppException;

	public UserRecord getUserDetails(int userID) throws AppException;
	
//	public long createAccount(int customerID, String type, int branchID, double deposiAmount) throws AppException;

	public Account getAccountDetails(long accountNumber) throws AppException;
	
	public double getBalanceInAccount(long accoutNumber) throws AppException;
	
	public List<Account> getAccountsOfUser(int userID) throws AppException;
		
	public List<Transaction> getTransactionsOfAccount(long accountNumber) throws AppException;
	
	public long transferAmount(Transaction transaction, boolean isTransferOutsideBank) throws AppException;
}
