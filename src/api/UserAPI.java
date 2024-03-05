package api;

import java.util.List;
import java.util.Map;

import api.mysql.MySQLQueryUtil.Fields;
import exceptions.AppException;
import helpers.Account;
import helpers.Branch;
import helpers.Transaction;
import helpers.UserRecord;

public interface UserAPI {

	public boolean authenticateUser(int userID, String password) throws AppException;

	public UserRecord getUserDetails(int userID) throws AppException;

	public Account getAccountDetails(long accountNumber) throws AppException;

	public double getBalanceInAccount(long accoutNumber) throws AppException;

	public Map<Long, Account> getAccountsOfUser(int userID) throws AppException;

	public List<Transaction> getTransactionsOfAccount(long accountNumber) throws AppException;

	public long transferAmount(Transaction transaction, boolean isTransferOutsideBank) throws AppException;

	public Branch getBrachDetails(int branchID) throws AppException;

	public boolean updateProfile(int userID, Fields field, Object value) throws AppException;

	public boolean updatePassword(int customerID, String oldPassword, String newPassword) throws AppException;
}
