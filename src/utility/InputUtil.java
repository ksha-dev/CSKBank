package utility;

import java.util.InputMismatchException;
import java.util.Scanner;

import exceptions.messages.InvalidInputMessage;

public class InputUtil {

	private static final Scanner scanner = new Scanner(System.in);

	public static int getInteger() {
		Integer obtainedInteger = null;

		while (ValidatorUtil.isObjectNull(obtainedInteger)) {
			try {
				obtainedInteger = scanner.nextInt();
			} catch (InputMismatchException e) {
				LoggingUtil.DEFAULT_LOGGER.warning(InvalidInputMessage.INVALID_INTEGER_INPUT.toString());
			} finally {
				scanner.nextLine();
			}
		}
		return obtainedInteger;
	}

	public static int getPositiveInteger() {
		Integer obtainedInteger = null;
		do {
			obtainedInteger = getInteger();
			if (obtainedInteger < 0) {
				LoggingUtil.DEFAULT_LOGGER.warning(InvalidInputMessage.POSITIVE_INTEGER_REQUIRED.toString());
			}
		} while (obtainedInteger < 0);

		return obtainedInteger;
	}

	public static long getLong() {
		Long obtainedLong = null;

		while (ValidatorUtil.isObjectNull(obtainedLong)) {
			try {
				obtainedLong = scanner.nextLong();
			} catch (InputMismatchException e) {
				LoggingUtil.DEFAULT_LOGGER.warning(InvalidInputMessage.INVALID_INTEGER_INPUT.toString());
			} finally {
				scanner.nextLine();
			}
		}
		return obtainedLong;
	}

	public static long getPositiveLong() {
		Long obtainedLong = null;
		do {
			obtainedLong = getLong();
			if (obtainedLong < 0) {
				LoggingUtil.DEFAULT_LOGGER.warning(InvalidInputMessage.POSITIVE_INTEGER_REQUIRED.toString());
			}
		} while (obtainedLong < 0);

		return obtainedLong;
	}

	public static String getString() {
		return scanner.nextLine();
	}
	
	public static double getDouble() {
		Double value = null;

		while (ValidatorUtil.isObjectNull(value)) {
			try {
				value = scanner.nextDouble();
			} catch (InputMismatchException e) {
				LoggingUtil.DEFAULT_LOGGER.warning(InvalidInputMessage.INVALID_INTEGER_INPUT.toString());
			} finally {
				scanner.nextLine();
			}
		}
		return value;
	}

	public static double getPositiveDouble() {
		Double value = null;
		do {
			value = getDouble();
			if (value < 0) {
				LoggingUtil.DEFAULT_LOGGER.warning(InvalidInputMessage.POSITIVE_INTEGER_REQUIRED.toString());
			}
		} while (value < 0);
		return value;
	}

}
