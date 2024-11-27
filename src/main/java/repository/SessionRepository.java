package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.Session;
import util.LoggerUtil;
import util.file.JsonFileHandler;

import java.util.*;
import java.util.stream.Collectors;

public class SessionRepository extends BaseRepository<Session> {
    private static SessionRepository instance;

    private SessionRepository() {
        super("src/main/resources/data/sessions.json");
    }

    public static synchronized SessionRepository getInstance() {
        if (instance == null) {
            instance = new SessionRepository();
        }
        return instance;
    }

    public List<Optional<Session>> findAllById(Set<String> ids) {
        return ids.stream()
                .map(id -> Optional.ofNullable(cache.get(id)))
                .collect(Collectors.toList());
    }

    @Override
    protected TypeReference<Map<String, Session>> getTypeReference() {
        return new TypeReference<Map<String, Session>>() {};
    }
}
