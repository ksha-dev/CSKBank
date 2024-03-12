package modules;

import java.time.LocalDate;

import exceptions.AppException;
import utility.ConstantsUtil.AccountType;
import utility.ConstantsUtil.Status;
import utility.ConvertorUtil;
import utility.ValidatorUtil;

public class Account {

	private long accountNumber;
	private int userId;
	private int branchId;
	private AccountType type;
	private long openingDate;
	private long lastTransactionAt;
	private double balance;
	private Status status;

	public Account() {
	}

	public void setAccountNumber(long accoutNumber) throws AppException {
		ValidatorUtil.validatePositiveNumber(accoutNumber);
		this.accountNumber = accoutNumber;
	}

	public void setUserId(int userId) throws AppException {
		ValidatorUtil.validatePositiveNumber(userId);
		this.userId = userId;
	}

	public void setBranchId(int branchId) throws AppException {
		ValidatorUtil.validatePositiveNumber(branchId);
		this.branchId = branchId;
	}

	public void setOpeningDate(long openingDate) throws AppException {
		ValidatorUtil.validatePositiveNumber(openingDate);
		this.openingDate = openingDate;
	}

	public void setLastTransactedAt(long lastTransactionDateTime) throws AppException {
		ValidatorUtil.validatePositiveNumber(lastTransactionDateTime);
		this.openingDate = lastTransactionDateTime;
	}

	public void setStatus(int statusId) throws AppException {
		this.status = Status.getStatus(statusId);
	}

	public void setType(int accountTypeId) throws AppException {
		this.type = AccountType.getAccountType(accountTypeId);
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public long getAccountNumber() {
		return this.accountNumber;
	}

	public int getUserId() {
		return this.userId;
	}

	public int getBranchId() {
		return this.branchId;
	}

	public AccountType getAccountType() {
		return this.type;
	}

	public Status getStatus() {
		return this.status;
	}

	public long getOpeningDate() {
		return this.openingDate;
	}

	public long getLastTransactedAt() {
		return this.lastTransactionAt;
	}

	public LocalDate getOpeningDateInLocalDateTime() {
		return ConvertorUtil.convertLongToLocalDate(this.openingDate);
	}

	public double getBalance() {
		return this.balance;
	}
}
