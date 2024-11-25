package repository;

import domain.model.Conference;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConferenceRepository {
    boolean save(Conference conference);
    Optional<Conference> findById(String id);
    Optional<Conference> findByName(String name);
    List<Conference> findAll();
    List<Conference> findByIds(Set<String> ids);
    void deleteById(String id);
}
