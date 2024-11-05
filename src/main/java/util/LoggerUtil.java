package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
    private static LoggerUtil instance;
    private final Logger logger;

    private LoggerUtil() {
        this.logger = LoggerFactory.getLogger(LoggerUtil.class);
    }

    public static synchronized LoggerUtil getInstance() {
        if (instance == null) {
            instance = new LoggerUtil();
        }
        return instance;
    }

    public  void logInfo(String info) {
        logger.info(info);
    }

    public void logDebug(String debug) {
        logger.debug(debug);
    }

    public void logWarning(String warning) {
        logger.warn(warning);
    }

    public void logError(String error) {
        logger.error(error);
    }

}
