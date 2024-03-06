package helpers;

import exceptions.AppException;
import utility.HelperUtil.TransactionType;
import utility.ValidatorUtil;

public class Transaction {

	private long transactionId;
	private int userId;
	private long viewerAccountNumber;
	private long transactedAccountNumber;
	private TransactionType transactionType;
	private double transactionAmount;
	private double closingBalance;
	private long dateTime;
	private String remarks;

	public Transaction() throws AppException {
	}
	
	public void setTransactionId(long transactionId) throws AppException {
		ValidatorUtil.validatePositiveNumber(transactionId);
		this.transactionId = transactionId;
	}
	
	public void setUserId(int userId) throws AppException {
		ValidatorUtil.validatePositiveNumber(userId);
		this.userId = userId;
	}

	public void setViewerAccountNumber(long viewerAccountNumber) throws AppException {
		ValidatorUtil.validatePositiveNumber(viewerAccountNumber);
		this.viewerAccountNumber = viewerAccountNumber;
	}

	public void setTransactedAccountNumber(long transactedAccountNumber) throws AppException {
		ValidatorUtil.validatePositiveNumber(transactedAccountNumber);
		this.transactedAccountNumber = transactedAccountNumber;
	}

	public void setTransactionType(String transactionType) throws AppException {
		ValidatorUtil.validateObject(transactionType);
		this.transactionType = TransactionType.valueOf(transactionType);
	}

	public void setTransactionAmount(double amount) {
		this.transactionAmount = amount;
	}

	public void setclosingBalance(double closingBalance) {
		this.closingBalance = closingBalance;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}

	public void setRemarks(String remarks) throws AppException {
		ValidatorUtil.validateObject(remarks);
		this.remarks = remarks;
	}

	// getters

	public long getTransactionId() {
		return this.transactionId;
	}

	public int getUserId() {
		return this.userId;
	}

	public long getViewerAccountNumber() {
		return this.viewerAccountNumber;
	}

	public long getTransactedAccountNumber() {
		return this.transactedAccountNumber;
	}

	public TransactionType getTransactionType() {
		return this.transactionType;
	}

	public double getTransactedAmount() {
		return this.transactionAmount;
	}

	public double getClosingBalance() {
		return this.closingBalance;
	}

	public long getDateTime() {
		return this.dateTime;
	}

	public String getRemarks() {
		return this.remarks;
	}
}
