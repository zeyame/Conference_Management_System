package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.Conference;
import domain.model.User;
import util.JsonFileHandler;

import java.util.*;
import java.util.stream.Collectors;

public class ConferenceFileRepository implements ConferenceRepository {

    private static final String FILE_PATH = "src/main/resources/data/conferences.json";
    private static final Map<String, Conference> conferenceCache = new HashMap<>();

    public ConferenceFileRepository() {
        loadConferencesFromFile();
    }

    @Override
    public List<Conference> findByIds(Set<String> ids) {
        return conferenceCache.values().stream()
                .filter(conference -> ids.contains(conference.getId()))
                .collect(Collectors.toList());
    }

    private void loadConferencesFromFile() {
        JsonFileHandler.loadData(FILE_PATH, new TypeReference<Map<String, Conference>>() {})
                .ifPresent(conferenceCache::putAll);
    }
}
