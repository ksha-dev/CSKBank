package api;

import java.util.List;
import java.util.Map;

import exceptions.AppException;
import helpers.Account;
import helpers.Branch;
import helpers.Transaction;
import helpers.UserRecord;
import utility.HelperUtil.ModifiableField;
import utility.HelperUtil.TransactionHistoryLimit;

public interface UserAPI {

	public boolean authenticateUser(int userID, String password) throws AppException;

	public boolean userConfimration(int userID, String pin) throws AppException;

	public UserRecord getUserDetails(int userID) throws AppException;

	public boolean updateProfile(int userID, ModifiableField field, Object value) throws AppException;

	public boolean updatePassword(int customerID, String oldPassword, String newPassword) throws AppException;

	public Branch getBrachDetails(int branchID) throws AppException;
	
	public Map<Long, Account> getAccountsOfUser(int userID) throws AppException;

	public Account getAccountDetails(long accountNumber) throws AppException;

	public double getBalanceInAccount(long accoutNumber) throws AppException;

	public long transferAmount(Transaction transaction, boolean isTransferOutsideBank) throws AppException;

	public List<Transaction> getTransactionsOfAccount(long accountNumber, int pageNumber,
			TransactionHistoryLimit timeLimit) throws AppException;
}
