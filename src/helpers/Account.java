package helpers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import exceptions.AppException;
import utility.LoggingUtil;
import utility.ValidatorUtil;

public class Account {

	private long accountNumber;
	private int userID;
	private int branchID;
	private String type;
	private long openingDate;
	private double balance;
	private String status;

	public Account() {
	}

	public void setAccountNumber(long accoutNumber) throws AppException {
		ValidatorUtil.validatePositiveNumber(accoutNumber);
		this.accountNumber = accoutNumber;
	}

	public void setUserID(int userID) throws AppException {
		ValidatorUtil.validatePositiveNumber(userID);
		this.userID = userID;
	}

	public void setBranchID(int branchID) throws AppException {
		ValidatorUtil.validatePositiveNumber(branchID);
		this.branchID = branchID;
	}

	public void setOpeningDate(long openingDate) throws AppException {
		ValidatorUtil.validatePositiveNumber(openingDate);
		this.openingDate = openingDate;
	}

	public void setStatus(String status) throws AppException {
		ValidatorUtil.validateObject(status);
		this.status = status;
	}

	public void setType(String type) throws AppException {
		ValidatorUtil.validateObject(type);
		this.type = type;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public long getAccountNumber() {
		return this.accountNumber;
	}

	public int getUserID() {
		return this.userID;
	}

	public int getBranchID() {
		return this.branchID;
	}

	public String getAccountType() {
		return this.type;
	}

	public String getStatus() {
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
		log.info(String.format("%-20s", "CUSTOMER ID") + " : " + getUserID());
		log.info(String.format("%-20s", "OPENING DATE") + " : "
				+ getOpeningDateInLocalDateTime().format(DateTimeFormatter.ISO_DATE));
		log.info(String.format("%-20s", "BRANCH CODE") + " : " + getBranchID());
	}

}
