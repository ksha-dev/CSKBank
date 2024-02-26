package exceptions;

public enum ActivityExceptionMessages {

	INVALID_EMPLOYEE_RECORD("The employee record obtained is not valid.");

	private String message;

	private ActivityExceptionMessages(String message) {
		this.message = message;
	}

	public String toString() {
		return message;
	}

}
