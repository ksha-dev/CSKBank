package utility;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.regex.Pattern;

import exceptions.AppException;
import exceptions.InvalidInputMessage;

public class ValidatorUtil {

	public static final String MOBILE_NUMBER_REGEX = "^[7-9]\\d{9}$";
	public static final String EMAIL_REGEX = "^[a-zA-Z0-9._+-]+@[a-zA-Z0-9.-]+\\.[a-zA-z]{2,}+$";
	public static final String PASSWORD_REGEX = "^((?=[^\\d])(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!_+-@^#$%&]).{8,20})$";
	public static final String PAN_REGEX = "^[A-Z]{3}[ABCFGHLJPT][A-Z]\\d{4}[A-Z]$";

	public static boolean isObjectNull(Object object) {
		return object == null;
	}

	public static void validateObject(Object object) throws AppException {
		if (isObjectNull(object)) {
			throw new AppException(InvalidInputMessage.NULL_OBJECT_ENCOUNTERED);
		}
	}

	public static void validatePostiveNumber(long number) throws AppException {
		if (number < 0) {
			throw new AppException(InvalidInputMessage.POSITIVE_INTEGER_REQUIRED);
		}
	}

	public static void validateMobileNumber(long mobileNumber) throws AppException {
		if (!Pattern.matches(MOBILE_NUMBER_REGEX, mobileNumber + "")) {
			throw new AppException(InvalidInputMessage.INVALID_MOBILE_NUMBER);
		}
	}

	public static void validateEmail(String email) throws AppException {
		validateObject(email);
		if (!Pattern.matches(EMAIL_REGEX, email)) {
			throw new AppException(InvalidInputMessage.INVALID_EMAIL);
		}
	}

	public static boolean validatePassword(String password) throws AppException {
		validateObject(password);
		if (!Pattern.matches(PASSWORD_REGEX, password)) {
			throw new AppException(InvalidInputMessage.INVALID_PASSWORD);
		}
		return true;
	}

	// Bank details validators

	public static void validateAadhaarNumber(long aadhaarNumber) throws AppException {
		if (aadhaarNumber < 0 && aadhaarNumber > 999999999999L) {
			throw new AppException(InvalidInputMessage.INVALID_AADHAAR_NUMBER);
		}
	}

	public static void validatePANNumber(String panNumber) throws AppException {
		validateObject(panNumber);
		if (!Pattern.compile(PAN_REGEX).matcher(panNumber).find()) {
			throw new AppException(InvalidInputMessage.INVALID_PAN_NUMBER);
		}
	}

	public static void validateFirstName(String firstName) throws AppException {
		validateObject(firstName);
		if (firstName.length() < 3) {
			throw new AppException(InvalidInputMessage.INVALID_FIRST_NAME);
		}
	}

	public static void validateLastName(String lastName) throws AppException {
		validateObject(lastName);
		if (lastName.length() < 1) {
			throw new AppException(InvalidInputMessage.INVALID_LAST_NAME);
		}
	}

	public static void validateDateOfBirth(long dateOfBirth) throws AppException {
		if (dateOfBirth > System.currentTimeMillis()
				|| dateOfBirth < LocalDateTime.now().minusYears(100).toInstant(ZoneOffset.UTC).toEpochMilli()) {
			throw new AppException(InvalidInputMessage.INVALID_DOB);
		}
	}
}