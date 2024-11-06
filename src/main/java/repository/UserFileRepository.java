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
    private static final Map<String, User> userCache = new HashMap<>();

    public UserFileRepository() {
        loadUsersFromFile();
    }

    @Override
    public boolean save(User user) {
        // save user in memory first
        boolean savedToMemory = saveInMemory(user);
        if (!savedToMemory) return false;

        // save user to file storage
        try {
            saveUsersToFile();
            return true;
        } catch (UserRegistrationException e) {
            LoggerUtil.getInstance().logError("Failed to save user with email '" + user.getEmail() + "' to file storage.");
            return false;
        }
    }

    @Override
    public void removeFromMemory(User user) {
        userCache.remove(user.getEmail());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userCache.get(email));
    }

    private void loadUsersFromFile() {
        JsonFileHandler.loadData(FILE_PATH, new TypeReference<Map<String, User>>() {})
                .ifPresent(userCache::putAll);
    }

    private boolean saveInMemory(User user) {
        // update in memory storage
        try {
            userCache.put(user.getEmail(), user);
            return true;
        } catch (Exception e) {
            LoggerUtil.getInstance().logError("Failed to save user with email '" + user.getEmail() + "' to in-memory storage.");
            return false;
        }
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
            JsonFileHandler.saveData(FILE_PATH, userCache);
        } catch (IOException e) {
            LoggerUtil.getInstance().logError("Error occurred when attempting to write to file with path '" + FILE_PATH + "' in the saveUsersToFile method of the UserFileRepository class.");
            throw UserRegistrationException.savingData();
        }
    }

}
