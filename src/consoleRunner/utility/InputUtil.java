package consoleRunner.utility;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.InputMismatchException;
import java.util.Scanner;

import exceptions.AppException;
import exceptions.messages.InvalidInputMessage;
import utility.HelperUtil;
import utility.ValidatorUtil;

public class InputUtil {

	private static final Scanner scanner = new Scanner(System.in);

	public static int getInteger() throws AppException {
		try {
			return scanner.nextInt();
		} catch (InputMismatchException e) {
			throw new AppException(InvalidInputMessage.INVALID_INTEGER_INPUT);
		} finally {
			scanner.nextLine();
		}
	}

	public static int getPositiveInteger() throws AppException {
		int value = getInteger();
		if (value < 0) {
			throw new AppException(InvalidInputMessage.POSITIVE_INTEGER_REQUIRED);
		}
		return value;
	}

	public static long getLong() throws AppException {
		try {
			return scanner.nextLong();
		} catch (InputMismatchException e) {
			throw new AppException(InvalidInputMessage.INVALID_INTEGER_INPUT);
		} finally {
			scanner.nextLine();
		}
	}

	public static long getPositiveLong() throws AppException {
		long value = getLong();
		if (value < 0) {
			throw new AppException(InvalidInputMessage.POSITIVE_INTEGER_REQUIRED);
		}
		return value;

	}

	public static String getString() {
		return scanner.nextLine();
	}

	public static double getDouble() throws AppException {
		try {
			return scanner.nextDouble();
		} catch (InputMismatchException e) {
			throw new AppException(InvalidInputMessage.INVALID_INTEGER_INPUT);
		} finally {
			scanner.nextLine();
		}
	}

	public static double getPositiveDouble() throws AppException {
		double value = getDouble();
		if (value < 0) {
			throw new AppException(InvalidInputMessage.POSITIVE_INTEGER_REQUIRED);
		}
		return value;
	}

	public static ZonedDateTime getDate() throws AppException {
		String dateOfBirth = getString();
		if (dateOfBirth.length() != 8) {
			throw new AppException(InvalidInputMessage.INVALID_DATE_INPUT);
		}
		int date = Integer.parseInt(dateOfBirth.substring(0, 2));
		int month = Integer.parseInt(dateOfBirth.substring(2, 4));
		int year = Integer.parseInt(dateOfBirth.substring(4, 8));
		try {
			return ZonedDateTime.of(year, month, date, 0, 0, 0, 0, ZoneId.systemDefault());
		} catch (Exception e) {
			throw new AppException(InvalidInputMessage.INVALID_DATE_INPUT);
		}
	}

	public static String getPIN() throws AppException {
		HelperUtil.log.info("Enter 4 digit secure PIN to confirm your request : ");
		String pin = getString();
		ValidatorUtil.validatePIN(pin);
		return pin;
	}
}
