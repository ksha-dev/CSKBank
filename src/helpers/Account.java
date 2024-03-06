package helpers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import consoleRunner.utility.LoggingUtil;
import exceptions.AppException;
import utility.HelperUtil.AccountType;
import utility.HelperUtil.Status;
import utility.ValidatorUtil;

public class Account {

	private long accountNumber;
	private int userId;
	private int branchId;
	private AccountType type;
	private long openingDate;
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

	public void setStatus(String status) throws AppException {
		ValidatorUtil.validateObject(status);
		this.status = Status.valueOf(status);
	}

	public void setType(String type) throws AppException {
		ValidatorUtil.validateObject(type);
		this.type = AccountType.valueOf(type);
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

	public LocalDate getOpeningDateInLocalDateTime() {
		return LocalDate.ofInstant(Instant.ofEpochMilli(openingDate), ZoneId.systemDefault());
	}

	public double getBalance() {
		return this.balance;
	}

	public void logAccount() {
		Logger log = LoggingUtil.DEFAULT_LOGGER;
		log.info("-".repeat(40));
		log.info("ACCOUNT DETAILS");
		log.info("-".repeat(40));
		log.info(String.format("%-20s", "ACCOUNT NUMBER") + " : " + getAccountNumber());
		log.info(String.format("%-20s", "ACCOUNT BALANCE") + " : " + getBalance());
		log.info(String.format("%-20s", "ACCOUNT TYPE") + " : " + getAccountType());
		log.info(String.format("%-20s", "ACCOUNT STATUS") + " : " + getStatus());
		log.info(String.format("%-20s", "CUSTOMER Id") + " : " + getUserId());
		log.info(String.format("%-20s", "OPENING DATE") + " : "
				+ getOpeningDateInLocalDateTime().format(DateTimeFormatter.ISO_DATE));
		log.info(String.format("%-20s", "BRANCH CODE") + " : " + getBranchId());
	}

}
