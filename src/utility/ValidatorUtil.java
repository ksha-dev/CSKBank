package utility;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.regex.Pattern;

import exceptions.AppException;
import exceptions.messages.InvalidInputMessage;
import utility.ConstantsUtil.Gender;

public class ValidatorUtil {

	private static final String PIN_REGEX = "^\\d{4}$";
	private static final String MOBILE_NUMBER_REGEX = "^[7-9]\\d{9}$";
	private static final String EMAIL_REGEX = "^[a-zA-Z0-9._+-]+@[a-zA-Z0-9.-]+\\.[a-zA-z]{2,}+$";
	private static final String PAN_REGEX = "^[A-Z]{3}[ABCFGHLJPT][A-Z]\\d{4}[A-Z]$";
	private static final String PASSWORD_REGEX = "^((?=[^\\d])(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!_+-@^#$%&]).{8,20})$";

	// Common Validators
	public static boolean isObjectNull(Object object) {
		return object == null;
	}

	public static void validateObject(Object object) throws AppException {
		if (isObjectNull(object)) {
			throw new AppException(InvalidInputMessage.NULL_OBJECT_ENCOUNTERED);
		}
	}

	public static <T> void validateCollection(Collection<T> collection) throws AppException {
		validateObject(collection);
		for (T element : collection) {
			validateObject(element);
		}
	}

	public static void validatePositiveNumber(long number) throws AppException {
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
		if (firstName.length() < 4) {
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

	public static void validatePIN(String pin) throws AppException {
		validateObject(pin);
		if (!Pattern.compile(PIN_REGEX).matcher(pin).find()) {
			throw new AppException("Invalid PIN value obtained");
		}
	}

	public static void validateId(long id) throws AppException {
		if (id < 1) {
			throw new AppException(InvalidInputMessage.INVALID_ID);
		}
	}

	public static void validateAmount(double amount) throws AppException {
		if (amount < 1) {
			throw new AppException(InvalidInputMessage.INVALID_AMOUNT);
		}
	}

	public static void validateGender(String gender) throws AppException {
		try {
			Gender.valueOf(gender.toUpperCase());
		} catch (Exception e) {
			throw new AppException(InvalidInputMessage.INVALID_GENDER);
		}
	}
}

//
//<style>
//body {
//    background-color: #121212;
//    color: #fff;
//    font-family: Arial, sans-serif;
//    margin: 0;
//    padding: 0;
//    display: flex;
//    justify-content: center;
//    align-items: center;
//    height: 100vh;
//}
//
//.container {
//    background-color: #333;
//    padding: 20px;
//    border-radius: 25px;
//    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
//    max-width: 400px;
//    width: 100%;
//}
//
//h1 {
//    text-align: center;
//}
//
//form {
//    display: flex;
//    flex-direction: column;
//}
//
//label {
//    margin-bottom: 5px;
//}
//
//input[type="number"],
//input[type="password"] {
//    margin: 10px;
//    padding: 10px;
//    margin-bottom: 15px;
//    border: 1px solid #555;
//    border-radius: 10px;
//    background-color: #444;
//    color: #fff;
//    outline: none;
//}
//
//input[type="submit"] {
//    margin: 10px;
//    padding: 10px;
//    border: none;
//    border-radius: 10px;
//    background-color: #007bff;
//    color: #fff;
//    cursor: pointer;
//    transition: background-color 0.1s ease;
//}
//
//input[type="submit"]:hover {
//    background-color: #0056b3;
//}
//
//p {
//    text-align: center;
//}
//</style>