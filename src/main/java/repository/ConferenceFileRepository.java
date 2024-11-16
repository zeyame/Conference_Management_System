package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.Conference;
import exception.ConferenceCreationException;
import util.JsonFileHandler;
import util.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ConferenceFileRepository implements ConferenceRepository {

    private static final String FILE_PATH = "src/main/resources/data/conferences.json";
    private static final Map<String, Conference> conferenceCache = new HashMap<>();         // K: Conference Id, V: Conference Object

    public ConferenceFileRepository() {
        loadConferencesFromFile();
    }

    @Override
    public boolean save(Conference conference) {
        // save user in memory first
        boolean savedToMemory = saveInMemory(conference);
        if (!savedToMemory) return false;

        // save user to file storage
        try {
            saveConferencesToFile();
            return true;
        } catch (ConferenceCreationException e) {
            LoggerUtil.getInstance().logError("Failed to save conference with name '" + conference.getName() + "' to file storage.");
            return false;
        }
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
    public void removeFromMemory(Conference conference) {
        conferenceCache.remove(conference.getId());
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

    private void loadConferencesFromFile() {
        JsonFileHandler.loadData(FILE_PATH, new TypeReference<Map<String, Conference>>() {})
                .ifPresent(conferenceCache::putAll);
    }

    private void saveConferencesToFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LoggerUtil.getInstance().logError("Error occurred when creating a new empty file with path '" + FILE_PATH + "' in the saveConferencesToFile method of the ConferencesFileRepository class.");
                throw ConferenceCreationException.savingData();
            }
        }
        try {
            JsonFileHandler.saveData(FILE_PATH, conferenceCache);
        } catch (IOException e) {
            LoggerUtil.getInstance().logError("Error occurred when attempting to write to file with path '" + FILE_PATH + "' in the saveConferences method of the ConferenceFileRepository class.");
            throw ConferenceCreationException.savingData();
        }
    }
}
