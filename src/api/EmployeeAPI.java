package api;

import java.util.List;

import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.UserRecord;

public interface EmployeeAPI extends UserAPI {

	public boolean createCustomer(CustomerRecord customer) throws AppException;

	public long createAccount(int customerID, String type, int branchID, double deposiAmount) throws AppException;

	public List<Account> viewAccountsInBranch(int branchID) throws AppException;

	public long depositAmount(long accoutNumber, double amount) throws AppException;

	public long withdrawAmount(long accountNumber, double amount) throws AppException;
}
