package helpers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Logger;

import consoleRunner.utility.LoggingUtil;
import exceptions.AppException;
import utility.ValidatorUtil;
import utility.HelperUtil.Gender;
import utility.HelperUtil.Status;
import utility.HelperUtil.UserType;

public abstract class UserRecord {

	private int userId;
	private String firstName;
	private String lastName;
	private long dateOfBirth;
	private Gender gender;
	private String address;
	private long mobileNumber;
	private String email;
	private UserType type;

	public void setUserId(int userId) {
		this.userId = userId;
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

	public void setType(String type) throws AppException {
		ValidatorUtil.validateObject(type);
		this.type = UserType.valueOf(type);
	}

	public int getUserId() {
		return userId;
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
	public UserType getType() {
		return type;
	}

	protected void logUserRecord() {
		Logger log = LoggingUtil.DEFAULT_LOGGER;
		log.info("-".repeat(40));
		log.info(String.format("%-40s", getType() + " DETAILS"));
		log.info("-".repeat(40));
		log.info(String.format("%-20s", "USER ID") + " : " + getUserId());
		log.info(String.format("%-20s", "FIRST NAME") + " : " + firstName);
		log.info(String.format("%-20s", "LAST NAME") + " : " + lastName);
		log.info(String.format("%-20s", "DATE OF BIRTH") + " : " + getDateOfBirthInLocalDate());
		log.info(String.format("%-20s", "GENDER") + " : " + getGender());
		log.info(String.format("%-20s", "ADDRESS") + " : " + address);
		log.info(String.format("%-20s", "E-MAIL") + " : " + email);
	}
}