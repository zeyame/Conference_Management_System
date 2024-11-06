package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class JsonFileHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> Optional<T> loadData(String filePath, TypeReference<T> typeReference) {
        File file = new File(filePath);
        // check if file is empty still
        if (file.exists() && file.length() == 0) {
            LoggerUtil.getInstance().logInfo("The file at path '" + filePath + "' is empty and so there are no users to be loaded into the user repository at this stage.");
            return Optional.empty();
        }

        try {
            // if file exists and contains data, attempt to load
            return Optional.of(objectMapper.readValue(new File(filePath), typeReference));
        } catch (IOException e) {
            LoggerUtil.getInstance().logError("Error loading data from file with path '" + filePath + "' in the loadData method of the JsonFileHandler class.");
            return Optional.empty();
        }
    }

    public static <T> void saveData(String filePath, T data) throws IOException {
        objectMapper.writeValue(new File(filePath), data);
    }


}
