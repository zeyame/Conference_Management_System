package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.User;
import exception.SavingDataException;
import exception.UserRegistrationException;
import util.JsonFileHandler;
import util.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UserFileRepository implements UserRepository {
    private static final String FILE_PATH = "src/main/resources/data/users.json";
    private static final Map<String, User> userCache = new HashMap<>();         // K: User Email, V: User Object

    public UserFileRepository() {
        loadUsersFromFile();
    }

    @Override
    public boolean save(User user) {
        // save user in memory first
        boolean savedToMemory = saveInMemory(user);
        if (!savedToMemory) return false;

        // save user to file storage
        boolean isSavedToFile = JsonFileHandler.saveDataWithRetry(userCache, FILE_PATH, 3);
        if (!isSavedToFile) {
            LoggerUtil.getInstance().logWarning("Failed to save user with email '" + user.getEmail() + "' to file storage. Rolling back in-memory save.");
            removeFromMemory(user);
            return false;
        }

        return true;
    }

    @Override
    public Optional<User> findById(String id) {
        return userCache.values()
                .stream()
                .filter(user -> id.equals(user.getId()))
                .findAny();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userCache.get(email));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userCache.values());
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

    private void removeFromMemory(User user) {
        userCache.remove(user.getEmail());
    }

    private void loadUsersFromFile() {
        JsonFileHandler.loadData(FILE_PATH, new TypeReference<Map<String, User>>() {})
                .ifPresent(userCache::putAll);
    }
}
