package repository;

import domain.model.Session;

import java.util.Optional;

public interface SessionRepository {

    Optional<Session> findById(String id);
}
