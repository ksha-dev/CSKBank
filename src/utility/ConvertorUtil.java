package utility;

import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import exceptions.AppException;
import helpers.UserRecord;

public class ConvertorUtil {

	public static String passwordHasher(String password) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] hash = digest.digest(password.getBytes("UTF-8"));
			final StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < hash.length; i++) {
				final String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception ex) {
		}
		return null;
	}

	public static ZonedDateTime convertLongZonedDateTime(long dateTime) {
		return ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault());
	}

	public static LocalDate convertLongToLocalDate(long dateTime) {
		return LocalDate.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault());
	}

	public static long convertToMilliSeconds(ZonedDateTime dateTime) throws AppException {
		ValidatorUtil.validateObject(dateTime);
		return dateTime.toInstant().toEpochMilli();
	}

	public static String passwordGenerator(UserRecord user) throws AppException {
		ValidatorUtil.validateObject(user);
		return passwordHasher(user.getFirstName().substring(0, 4) + "@"
				+ user.getDateOfBirthInLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE).substring(4, 8));
	}
}
