package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class JsonFileHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> Optional<T> loadData(String filePath, TypeReference<T> typeReference) {
        try {
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
