package repository;

import domain.model.Session;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SessionRepository {

    boolean save(Session session);
    Optional<Session> findById(String id);

    List<Session> findAllById(Set<String> ids);

    void deleteById(String id);
}
