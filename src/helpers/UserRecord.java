package helpers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Logger;

import exceptions.AppException;
import utility.ValidatorUtil;
import utility.LoggingUtil;
import utility.HelperUtil.Gender;
import utility.HelperUtil.UserStatus;
import utility.HelperUtil.UserTypes;

public abstract class UserRecord {

	private int userID;
	private String firstName;
	private String lastName;
	private long dateOfBirth;
	private Gender gender;
	private String address;
	private long mobileNumber;
	private String email;
	private UserStatus status;
	private UserTypes type;

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public void setFirstName(String firstName) throws AppException {
		ValidatorUtil.validateFirstName(firstName);
		this.firstName = firstName;
	}

	public void setLastName(String lastName) throws AppException {
		ValidatorUtil.validateLastName(lastName);
		this.lastName = lastName;
	}

	public void setDateOfBirth(long dateOfBirth) throws AppException {
		ValidatorUtil.validateDateOfBirth(dateOfBirth);
		this.dateOfBirth = dateOfBirth;
	}

	public void setDateOfBirth(ZonedDateTime dateOfBirth) throws AppException {
		ValidatorUtil.validateDateOfBirth(dateOfBirth.toInstant().toEpochMilli());
		this.dateOfBirth = dateOfBirth.toInstant().toEpochMilli();
	}

	public void setGender(String gender) throws AppException {
		ValidatorUtil.validateObject(gender);
		try {
			this.gender = Gender.valueOf(gender.toUpperCase());
		} catch (Exception e) {
			throw new AppException(
					"Invalid Gender. Please enter one of the following gender : " + Gender.OTHER.getGendersString());
		}
	}

	public void setAddress(String address) throws AppException {
		ValidatorUtil.validateObject(address);
		this.address = address;
	}

	public void setMobileNumber(long mobileNumber) throws AppException {
		ValidatorUtil.validateMobileNumber(mobileNumber);
		this.mobileNumber = mobileNumber;
	}

	public void setEmail(String email) throws AppException {
		ValidatorUtil.validateEmail(email);
		this.email = email;
	}

	public void setStatus(String status) throws AppException {
		ValidatorUtil.validateObject(status);
		this.status = UserStatus.valueOf(status);
	}

	public void setType(String type) throws AppException {
		ValidatorUtil.validateObject(type);
		this.type = UserTypes.valueOf(type);
	}

	public int getUserID() {
		return userID;
	}

	public String getFirstName() throws AppException {
		ValidatorUtil.validateFirstName(firstName);
		return firstName;
	}

	public String getLastName() throws AppException {
		ValidatorUtil.validateLastName(lastName);
		return lastName;
	}

	public LocalDate getDateOfBirthInLocalDate() {
		return LocalDate.ofInstant(Instant.ofEpochMilli(dateOfBirth), ZoneId.systemDefault());
	}

	public long getDateOfBirth() {
		return dateOfBirth;
	}

	public Gender getGender() {
		return gender;
	}

	public String getAddress() {
		return ValidatorUtil.isObjectNull(address) ? "-" : address;
	}

	public long getMobileNumber() {
		return mobileNumber;
	}

	public String getEmail() throws AppException {
		ValidatorUtil.validateEmail(email);
		return email;
	}

	public UserStatus getStatus() {
		return status;
	}

	public UserTypes getType() {
		return type;
	}

	protected void logUserRecord() {
		Logger log = LoggingUtil.DEFAULT_LOGGER;
		log.info("-".repeat(40));
		log.info(String.format("%-40s", getType() + " DETAILS"));
		log.info("-".repeat(40));
		log.info(String.format("%-20s", "FIRST NAME") + " : " + firstName);
		log.info(String.format("%-20s", "LAST NAME") + " : " + lastName);
		log.info(String.format("%-20s", "DATE OF BIRTH") + " : " + getDateOfBirthInLocalDate());
		log.info(String.format("%-20s", "GENDER") + " : " + getGender());
		log.info(String.format("%-20s", "ADDRESS") + " : " + address);
		log.info(String.format("%-20s", "MAIL ID") + " : " + email);
		log.info(String.format("%-20s", "STATUS") + " : " + getStatus());
	}
}