package repository;

import domain.model.User;

import java.util.Optional;

public interface UserRepository {
    public boolean save(User user);
    public Optional<User> findByEmail(String email);

    public void removeFromMemory(User user);
}
