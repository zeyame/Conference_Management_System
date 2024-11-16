package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import exception.SavingDataException;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class JsonFileHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static <T> Optional<T> loadData(String filePath, TypeReference<T> typeReference) {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            LoggerUtil.getInstance().logInfo("File at path '" + filePath + "' is either missing or empty.");
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(file, typeReference));
        } catch (IOException e) {
            LoggerUtil.getInstance().logError("Failed to load data from file at path '" + filePath + "': " + e.getMessage());
            return Optional.empty();
        }
    }

    public static <T> boolean saveDataWithRetry(T data, String filePath, int maxRetries) {
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                saveData(filePath, data);
                return true;
            } catch (IOException e) {
                retryCount++;
                LoggerUtil.getInstance().logError("Attempt " + retryCount + " to save data to file at '" + filePath + "' failed.");

                if (retryCount >= maxRetries) {
                    LoggerUtil.getInstance().logError("Exceeded maximum retry attempts to save data to file at '" + filePath + "'.");
                    return false;
                }

                // Exponential backoff for retries
                try {
                    Thread.sleep((long) Math.pow(2, retryCount) * 100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }

    private static <T> void saveData(String filePath, T data) throws IOException {
        File file = new File(filePath);

        // Create the file if it doesn't exist
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Unable to create file at path: " + filePath);
            }
        }

        // Save data to file
        objectMapper.writeValue(file, data);
    }
}
