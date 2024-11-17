package repository;

import domain.model.Conference;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConferenceRepository {
    public boolean save(Conference conference);
    public Optional<Conference> findById(String id);
    public Optional<Conference> findByName(String name);
    public List<Conference> findAll();
    public List<Conference> findByIds(Set<String> ids);
    public void deleteById(String id);
}
