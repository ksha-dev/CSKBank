package helpers;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.logging.Logger;

import consoleRunner.utility.LoggingUtil;
import exceptions.AppException;
import exceptions.messages.InvalidInputMessage;
import utility.ValidatorUtil;
import utility.ConstantsUtil.Gender;
import utility.ConstantsUtil.UserType;
import utility.ConvertorUtil;

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
		long temp = ConvertorUtil.convertToMilliSeconds(dateOfBirth);
		ValidatorUtil.validateDateOfBirth(temp);
		this.dateOfBirth = temp;
	}

	public void setGender(String gender) throws AppException {
		ValidatorUtil.validateObject(gender);
		try {
			this.gender = Gender.valueOf(gender.toUpperCase());
		} catch (Exception e) {
			throw new AppException(InvalidInputMessage.INVALID_GENDER);
		}
	}

	public void setAddress(String address) throws AppException {
		ValidatorUtil.validateObject(address);
		this.address = address;
	}

	public void setPhone(long mobileNumber) throws AppException {
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
		return ConvertorUtil.convertLongToLocalDate(dateOfBirth);
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

	public long getPhone() {
		return mobileNumber;
	}

	public String getEmail() throws AppException {
		ValidatorUtil.validateEmail(email);
		return email;
	}

	public UserType getType() {
		return type;
	}
}