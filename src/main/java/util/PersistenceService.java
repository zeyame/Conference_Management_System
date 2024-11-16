package util;

import exception.UserRegistrationException;

import java.util.function.Function;

public class PersistenceService {
    public static <T> boolean saveWithRetry(T entity, Function<T, Boolean> saveFunction, int maxRetries) {
        boolean isSavedToFile = false;
        int retryCount = 0;

        while (retryCount < maxRetries && !isSavedToFile) {
            isSavedToFile = saveFunction.apply(entity);
            if (!isSavedToFile) {
                retryCount++;
                LoggerUtil.getInstance().logError("Attempt " + retryCount + " to save entity to file failed.");

                // waiting for some time before retrying again
                try {
                    Thread.sleep((long) Math.pow(2, retryCount) * 100);
                } catch (InterruptedException e) {
                    // if thread is interrupted during retries
                    Thread.currentThread().interrupt();
                    LoggerUtil.getInstance().logError("Retry operation to save entity to file storage was interrupted.");
                    return false;
                }
            }
        }
        return isSavedToFile;
    }
}
