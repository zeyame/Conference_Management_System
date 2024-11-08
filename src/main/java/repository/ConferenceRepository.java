package repository;

import domain.model.Conference;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConferenceRepository {
    public List<Conference> findByIds(Set<String> ids);
}
