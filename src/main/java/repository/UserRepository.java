package repository;

import domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    boolean save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findAllById(Set<String> ids);



}
