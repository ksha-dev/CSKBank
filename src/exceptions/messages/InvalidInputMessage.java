package exceptions.messages;

public enum InvalidInputMessage {
	INVALID_FIRST_NAME("The first name must contain atleast 4 characters."),
	INVALID_LAST_NAME("The last name must contain atleast 1 characters."),
	INVALID_MOBILE_NUMBER("The mobile number should contain 10 digits. First digit should be between 7 and 9"),
	INVALID_EMAIL("The email address is invalid. Please check the email address"),
	INVALID_AADHAAR_NUMBER("The Aadhaar number is invalid. Make sure the number is correct and has 12 digits."),
	INVALID_PAN_NUMBER("The PAN number is invalid. Make sure it is the correct PAN number"),
	INVALID_DOB("The date of birth cannot exceed the current date and time and should be valid"),
	INVALID_ID("An Identifier value cannot be less than 1"),
	INVALID_PASSWORD(
			"The password must contain a minimum of 8 characters with both upper and lower case, start with a letter, have a special character and a number"),
	INVALID_INTEGER_INPUT("A number is expected. Please enter a valid integer."),
	INVALID_DATE_INPUT("The date of birth entered is incorrect. Please ensure that the order is correct"),
	POSITIVE_INTEGER_REQUIRED("A positive value value is expected. Please enter a positive integer."),

	NULL_OBJECT_ENCOUNTERED("A valid object is expected. Please provide an appropriate object");

	private String message;

	private InvalidInputMessage(String message) {
		this.message = message;
	}

	public String toString() {
		return message;
	}
}
