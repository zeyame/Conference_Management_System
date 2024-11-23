package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.Conference;
import domain.model.Session;
import util.JsonFileHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionFileRepository implements SessionRepository {

    private static final String FILE_PATH = "src/main/resources/data/sessions.json";
    private static final Map<String, Session> sessionCache = new HashMap<>();

    public SessionFileRepository() {
        loadSessionsFromFile();
    }

    @Override
    public Optional<Session> findById(String id) {
        return Optional.ofNullable(sessionCache.get(id));
    }


    private void loadSessionsFromFile() {
        JsonFileHandler.loadData(FILE_PATH, new TypeReference<Map<String, Session>>() {})
                .ifPresent(sessionCache::putAll);
    }
}
