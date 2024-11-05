package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.User;
import util.JsonFileHandler;
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

    private void saveUsersToFile() {
        JsonFileHandler.saveData(FILE_PATH, users);
    }

    private void loadUsersFromFile() {
        JsonFileHandler.loadData(FILE_PATH, new TypeReference<Map<String, User>>() {})
                .ifPresent(users::putAll);
    }
}
