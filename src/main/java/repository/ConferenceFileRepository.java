package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.Conference;
import util.JsonFileHandler;
import util.LoggerUtil;

import java.util.*;
import java.util.stream.Collectors;

public class ConferenceFileRepository implements ConferenceRepository {

    private static final String FILE_PATH = "src/main/resources/data/conferences.json";
    private static final Map<String, Conference> conferenceCache = new HashMap<>();         // K: Conference ID, V: Conference Object

    public ConferenceFileRepository() {
        loadConferencesFromFile();
    }

    @Override
    public boolean save(Conference conference) {
        // save conference in memory first
        boolean savedToMemory = saveInMemory(conference);
        if (!savedToMemory) return false;

        // save conference to file storage
        boolean isSavedToFile = JsonFileHandler.saveDataWithRetry(conferenceCache, FILE_PATH, 3);
        if (!isSavedToFile) {
            // rolling back conference creation so that in-memory storage is synced up to file storage
            removeFromMemory(conference.getId());
            return false;
        }
        return true;
    }

    @Override
    public Optional<Conference> findById(String id) {
        return Optional.ofNullable(conferenceCache.get(id));
    }

    @Override
    public Optional<Conference> findByName(String name) {
        return conferenceCache.values()
                .stream()
                .filter(conference -> conference.getName().equalsIgnoreCase(name))
                .findAny();
    }

    @Override
    public List<Conference> findAll() {
        return conferenceCache.values().stream().toList();
    }

    @Override
    public List<Conference> findByIds(Set<String> ids) {
        return conferenceCache.values().stream()
                .filter(conference -> ids.contains(conference.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        Conference conference = conferenceCache.get(id);
        removeFromMemory(id);
        boolean isSavedToFile = JsonFileHandler.saveDataWithRetry(conferenceCache, FILE_PATH, 3);
        if (!isSavedToFile) {
            // rolling back the delete so that in-memory storage is synced up with file storage
            saveInMemory(conference);
            LoggerUtil.getInstance().logError("Failed to delete conference with name: " + conference.getName());
        }
    }

    private boolean saveInMemory(Conference conference) {
        // update in memory storage
        try {
            conferenceCache.put(conference.getId(), conference);
            return true;
        } catch (Exception e) {
            LoggerUtil.getInstance().logError("Failed to save conference with name '" + conference.getName() + "' to in-memory storage.");
            return false;
        }
    }

    private void removeFromMemory(String id) {
        conferenceCache.remove(id);
    }

    private void loadConferencesFromFile() {
        JsonFileHandler.loadData(FILE_PATH, new TypeReference<Map<String, Conference>>() {})
                .ifPresent(conferenceCache::putAll);
    }
}
