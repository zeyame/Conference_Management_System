package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.Conference;

import java.util.*;

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

    @Override
    protected TypeReference<Map<String, Conference>> getTypeReference() {
        return new TypeReference<Map<String, Conference>>() {};
    }
}
