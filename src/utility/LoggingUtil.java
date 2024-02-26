package utility;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import exceptions.AppException;

public class LoggingUtil {
	private static final String DEFAULT_LOGGER_NAME = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
			+ "_CSK_Bank.log";
	public static final Logger DEFAULT_LOGGER = Logger.getLogger(DEFAULT_LOGGER_NAME);
	public static final String DEFAULT_FILE_PATH = System.getProperty("user.dir");
	public static final String DEFAULT_SEPARATOR = File.separator;
	public static final String DEFAULT_LOGGER_PATH = DEFAULT_FILE_PATH + DEFAULT_SEPARATOR + "logs";

	static {
//		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
	System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

		try {
			checkPath(DEFAULT_LOGGER_PATH);
			FileHandler fileHander = new FileHandler(DEFAULT_LOGGER_PATH + DEFAULT_SEPARATOR + DEFAULT_LOGGER_NAME);
			DEFAULT_LOGGER.addHandler(fileHander);
			Formatter formatter = new SimpleFormatter();
			fileHander.setFormatter(formatter);
		} catch (Exception e) {
		}
	}

	public static void checkPath(String path) throws AppException {
		ValidatorUtil.validateObject(path);
		if (!path.trim().isEmpty()) {
			File pathDir = new File(path);
			if (!pathDir.exists()) {
				pathDir.mkdir();
			}
		}
	}

	public static void logSever(Exception e) {
		DEFAULT_LOGGER.log(Level.SEVERE, e.getMessage());
	}
}
