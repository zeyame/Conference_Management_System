package repository;

import domain.model.User;

import java.util.Optional;

public interface UserRepository {
    public void save(User user);
    public Optional<User> findByEmail(String email);
}
