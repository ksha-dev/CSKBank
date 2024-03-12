package api;

import java.util.List;
import java.util.Map;

import exceptions.AppException;
import modules.Account;
import modules.Branch;
import modules.Transaction;
import modules.UserRecord;
import utility.ConstantsUtil.ModifiableField;
import utility.ConstantsUtil.TransactionHistoryLimit;

public interface UserAPI {

	public boolean userAuthentication(int userID, String password) throws AppException;

	public boolean userConfimration(int userID, String pin) throws AppException;

	public UserRecord getUserDetails(int userID) throws AppException;

	public boolean updateProfileDetails(int userID, ModifiableField field, Object value) throws AppException;

	public boolean updatePassword(int customerID, String oldPassword, String newPassword) throws AppException;

	public Branch getBrachDetails(int branchID) throws AppException;

	public Map<Long, Account> getAccountsOfUser(int userID) throws AppException;

	public Account getAccountDetails(long accountNumber) throws AppException;

	public double getBalanceInAccount(long accoutNumber) throws AppException;

	public long transferAmount(Transaction transaction, boolean isTransferOutsideBank) throws AppException;

	public List<Transaction> getTransactionsOfAccount(long accountNumber, int pageNumber,
			TransactionHistoryLimit timeLimit) throws AppException;
}
