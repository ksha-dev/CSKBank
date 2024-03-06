package utility;

import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

	public static ZonedDateTime convertLongToLocalDate(long dateTime) {
		return ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault());
	}
}
