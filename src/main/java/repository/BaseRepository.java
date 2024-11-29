package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import util.LoggerUtil;
import util.file.JsonFileHandler;
import java.util.*;

public abstract class BaseRepository<T> {

    protected final String filePath;
    protected final Map<String, T> cache;

    protected BaseRepository(String filePath) {
        this.filePath = filePath;
        this.cache = new HashMap<>();
        loadFromFile();
    }

    public boolean save(T entity, String id) {
        // save user in memory first
        boolean savedToMemory = saveInMemory(entity, id);
        if (!savedToMemory) return false;

        // save user to file storage
        boolean isSavedToFile = JsonFileHandler.saveDataWithRetry(cache, filePath, 3);
        if (!isSavedToFile) {
            LoggerUtil.getInstance().logError(String.format("Failed to save entity with id '%s' to file storage. Rolling back in-memory save. Entity's class: '%s'.", id, entity.getClass().getSimpleName()));
            removeFromMemory(id);
            return false;
        }

        return true;
    }


    public Optional<T> findById(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(cache.values());
    }


    public boolean deleteById(String id) {
        T entity = cache.get(id);
        removeFromMemory(id);
        boolean isSavedToFile = JsonFileHandler.saveDataWithRetry(cache, filePath, 3);
        if (!isSavedToFile) {
            // rolling back the delete so that in-memory storage is synced up with file storage
            saveInMemory(entity, id);
            LoggerUtil.getInstance().logError(String.format("Failed to delete entity with id '%s'. Entity's class: %s", id, entity.getClass().getSimpleName()));
            return false;
        }
        return true;
    }


    protected abstract TypeReference<Map<String, T>> getTypeReference();

    private boolean saveInMemory(T entity, String id) {
        // update in memory storage
        try {
            cache.put(id, entity);
            return true;
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to save entity with id '%s' to in-memory storage.", id));
            return false;
        }
    }

    private void removeFromMemory(String id) {
        cache.remove(id);
    }


    private void loadFromFile() {
        JsonFileHandler.loadData(filePath, getTypeReference())
                .ifPresent(cache::putAll);
    }

}
