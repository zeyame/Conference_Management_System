package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.User;
import exception.UserRegistrationException;
import util.JsonFileHandler;
import util.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserFileRepository implements UserRepository {
    private static final String FILE_PATH = "src/main/resources/data/users.json";
    private static final Map<String, User> users = new HashMap<>();

    public UserFileRepository() {
        loadUsersFromFile();
    }

    @Override
    public synchronized void save(User user) {
        // update in memory storage
        users.put(user.getEmail(), user);

        // update file storage
        saveUsersToFile();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    private void loadUsersFromFile() {
        JsonFileHandler.loadData(FILE_PATH, new TypeReference<Map<String, User>>() {})
                .ifPresent(users::putAll);
    }

    private void saveUsersToFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LoggerUtil.getInstance().logError("Error occurred when creating a new empty file with path '" + FILE_PATH + "' in the saveUsersToFile method of the UserFileRepository class.");
                throw UserRegistrationException.savingData();
            }
        }
        try {
            JsonFileHandler.saveData(FILE_PATH, users);
        } catch (IOException e) {
            LoggerUtil.getInstance().logError("Error occurred when attempting to write to file with path '" + FILE_PATH + "' in the saveUsersToFile method of the UserFileRepository class.");
            throw UserRegistrationException.savingData();
        }
    }

}
