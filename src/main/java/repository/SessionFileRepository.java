package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.Conference;
import domain.model.Session;
import util.LoggerUtil;
import util.file.JsonFileHandler;

import java.util.*;
import java.util.stream.Collectors;

public class SessionFileRepository implements SessionRepository {

    private static final String FILE_PATH = "src/main/resources/data/sessions.json";
    private static final Map<String, Session> sessionCache = new HashMap<>();

    public SessionFileRepository() {
        loadSessionsFromFile();
    }

    @Override
    public boolean save(Session session) {
        // save conference in memory first
        boolean savedToMemory = saveInMemory(session);
        if (!savedToMemory) return false;

        // save conference to file storage
        boolean isSavedToFile = JsonFileHandler.saveDataWithRetry(sessionCache, FILE_PATH, 3);
        if (!isSavedToFile) {
            // rolling back conference creation so that in-memory storage is synced up to file storage
            removeFromMemory(session.getId());
            return false;
        }
        return true;
    }

    @Override
    public Optional<Session> findById(String id) {
        return Optional.ofNullable(sessionCache.get(id));
    }

    @Override
    public List<Session> findAllById(Set<String> ids) {
        return ids.stream()
                .map(sessionCache::get)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        Session session = sessionCache.get(id);
        removeFromMemory(id);
        boolean isSavedToFile = JsonFileHandler.saveDataWithRetry(sessionCache, FILE_PATH, 3);
        if (!isSavedToFile) {
            // rolling back the delete so that in-memory storage is synced up with file storage
            saveInMemory(session);
            LoggerUtil.getInstance().logError("Failed to delete session with name: " + session.getName());
        }
    }


    private boolean saveInMemory(Session session) {
        // update in memory storage
        try {
            sessionCache.put(session.getId(), session);
            return true;
        } catch (Exception e) {
            LoggerUtil.getInstance().logError("Failed to save session with name '" + session.getName() + "' to in-memory storage.");
            return false;
        }
    }

    private void removeFromMemory(String id) {
        sessionCache.remove(id);
    }

    private void loadSessionsFromFile() {
        JsonFileHandler.loadData(FILE_PATH, new TypeReference<Map<String, Session>>() {})
                .ifPresent(sessionCache::putAll);
    }
}
