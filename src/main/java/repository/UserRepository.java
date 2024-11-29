package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.User;
import util.file.JsonFileHandler;
import util.LoggerUtil;

import java.util.*;
import java.util.stream.Collectors;

public class UserRepository extends BaseRepository<User> {
    private static UserRepository instance;

    private UserRepository() {
        super("src/main/resources/data/users.json");
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public Optional<User> findByEmail(String email) {
        return cache.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    protected TypeReference<Map<String, User>> getTypeReference() {
        return new TypeReference<Map<String, User>>(){};
    }
}
