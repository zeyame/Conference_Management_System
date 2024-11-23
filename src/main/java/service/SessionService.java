package service;

import domain.model.Session;
import dto.SessionDTO;
import repository.SessionRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SessionService {

    private final UserService userService;
    private final SessionRepository sessionRepository;

    public SessionService(UserService userService, SessionRepository sessionRepository) {
        this.userService = userService;
        this.sessionRepository = sessionRepository;
    }

    public List<SessionDTO> findByIds(Set<String> ids) {
        // retrieve session objects corresponding to provided ids
        List<Session> sessions =
                ids.stream()
                .map(sessionRepository::findById)
                .flatMap(Optional::stream)
                .toList();

        // retrieve the speaker id for each session
        Set<String> speakerIds = sessions.stream()
                                        .map(Session::getSpeakerId)
                                        .collect(Collectors.toSet());

        // retrieve the speaker name corresponding to each speaker id
        Map<String, String> speakerIdToNameMap = userService.findNamesByIds(speakerIds);

        // map the session objects to session data transfer objects (DTO)
        return sessions.stream()
                       .map(session -> mapToDTO(session, speakerIdToNameMap.get(session.getSpeakerId())))
                       .collect(Collectors.toList());
    }

    private SessionDTO mapToDTO(Session session, String speakerName) {
        return SessionDTO.builder(
                session.getConferenceId(),
                session.getSpeakerId(),
                speakerName,
                session.getName(),
                session.getDescription(),
                session.getRoom(),
                session.getDate(),
                session.getStartTime(),
                session.getEndTime()
        ).setId(session.getId())
         .setRegisteredAttendees(session.getRegisteredAttendees())
         .setAttendanceRecord(session.getAttendanceRecord())
         .build();
    }
}
