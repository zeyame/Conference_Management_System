package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.User;
import util.file.JsonFileHandler;
import util.LoggerUtil;

import java.util.*;
import java.util.stream.Collectors;

public class UserRepository extends BaseRepository<User> {
    public UserRepository() {
        super("src/main/resources/data/users.json");
    }

    public List<Optional<User>> findAllById(Set<String> ids) {
        return ids.stream()
                .map(id -> Optional.ofNullable(cache.get(id)))
                .collect(Collectors.toList());
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
