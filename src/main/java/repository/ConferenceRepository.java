package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.Conference;
import java.util.*;
import java.util.stream.Collectors;

public class ConferenceRepository extends BaseRepository<Conference> {

    private static ConferenceRepository instance;
    private ConferenceRepository() {
        super("src/main/resources/data/conferences.json");
    }

    // ensures only one instance of the repository is used throughout
    public static synchronized ConferenceRepository getInstance() {
        if (instance == null) {
            instance = new ConferenceRepository();
        }
        return instance;
    }

    public Optional<Conference> findByName(String name) {
        return cache.values()
                .stream()
                .filter(conference -> conference.getName().equalsIgnoreCase(name))
                .findAny();
    }

    public List<Optional<Conference>> findAllById(Set<String> ids) {
        return ids.stream()
                .map(id -> Optional.ofNullable(cache.get(id)))
                .collect(Collectors.toList());
    }

    @Override
    protected TypeReference<Map<String, Conference>> getTypeReference() {
        return new TypeReference<Map<String, Conference>>() {};
    }
}
