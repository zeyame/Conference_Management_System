package service.session;

import dto.ConferenceDTO;
import dto.SessionDTO;
import exception.SessionException;
import exception.UserException;
import service.UserService;
import util.LoggerUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public class SessionValidator {
    private final UserService userService;
    private final BiPredicate<String, Set<String>> isNameTaken;

    public SessionValidator(UserService userService, BiPredicate<String, Set<String>> isNameTaken) {
        this.userService = userService;
        this.isNameTaken = isNameTaken;
    }

    public void validateData(SessionDTO sessionDTO, ConferenceDTO conferenceDTO, List<SessionDTO> conferenceSessions, boolean isUpdate) {
        if (sessionDTO == null || conferenceDTO == null || conferenceSessions == null) {
            throw new IllegalArgumentException("SessionDTO, ConferenceDTO, and conference sessions cannot be null.");
        }

        Set<String> sessionIds = conferenceDTO.getSessions();
        if (isUpdate) {
            // removing session id from conference sessions, if it exists, for correct validation of session name and time in the case of updates
            removeSessionFromList(sessionDTO.getId(), conferenceSessions);
            sessionIds.remove(sessionDTO.getId());

            // removing session from speaker's schedule for correct speaker availability validation in case of updates
            try {
                userService.unassignSessionFromSpeaker(sessionDTO.getSpeakerId(), sessionDTO.getId());
            } catch (IllegalArgumentException | UserException e) {
                LoggerUtil.getInstance().logError(String.format("Session validation failed: %s", e.getMessage()));
                throw SessionException.validationFailure("An unexpected error occurred when validating session data. Please try again later.");
            }
        }

        validateSessionName(sessionDTO.getName(), sessionIds, this.isNameTaken);
        validateSpeakerAvailability(sessionDTO);
        validateSessionTime(sessionDTO, conferenceSessions, conferenceDTO.getStartDate());
    }

    private void validateSessionName(String sessionName, Set<String> sessionIds, BiPredicate<String, Set<String>> isNameTaken) {
        if (sessionName == null || sessionName.isEmpty()) {
            throw new IllegalArgumentException("Session and conference names cannot be null or empty.");
        }

        if (isNameTaken.test(sessionName, sessionIds)) {
            LoggerUtil.getInstance().logError("Session name validation failed: Session name is taken.");
            throw SessionException.nameTaken("A session with this name is already registered to be held at this conference. Please choose a different name.");
        }
    }

    private void validateSpeakerAvailability(SessionDTO sessionDTO) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("SessionDTO cannot be null.");
        }

        String speakerId = sessionDTO.getSpeakerId();
        LocalDateTime sessionStart = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime());
        LocalDateTime sessionEnd = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime());

        try {
            boolean isAvailable = userService.isSpeakerAvailable(speakerId, sessionStart, sessionEnd);
            if (!isAvailable) {
                LoggerUtil.getInstance().logError("Speaker is not available to give session.");
                throw SessionException.unavailableSpeaker("The chosen speaker is not available for the selected time. " +
                        "Please choose a different speaker or change the session timing.");
            }
        } catch (IllegalArgumentException | UserException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to validate speaker availability: %s", e.getMessage()));
            throw SessionException.validationFailure("An unexpected error occurred when checking speaker availability for session. Please try again later.");
        }
    }

    private void validateSessionTime(SessionDTO sessionDTO, List<SessionDTO> conferenceSessions, LocalDate conferenceStartDate) {
        if (sessionDTO == null || conferenceSessions == null || conferenceStartDate == null) {
            throw new IllegalArgumentException("Invalid parameters provided when validating session time. SessionDTO, ConferenceSessions, and ConferenceStartDate cannot be null.");
        }

        if (sessionDTO.getDate().isBefore(conferenceStartDate)) {
            throw SessionException.validationFailure(String.format("The session date you selected is earlier than the start date of the conference '%s'. " +
                    "Please select a date on or after the conference's start date.", conferenceStartDate));
        }

        LocalDateTime sessionStart = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime());
        LocalDateTime sessionEnd = LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime());

        // check for any conflicting sessions within the conference
        conferenceSessions.stream()
                .filter(session -> session.overlapsWith(sessionStart, sessionEnd))
                .findFirst()
                .ifPresent(conflictingSession -> {
                    LoggerUtil.getInstance().logError("Session date and time validation failed as another session is " +
                            "already set to take place in the selected time.");
                    throw SessionException.timeUnavailable(String.format(
                            "The session '%s' is already registered to take place within the time period you selected. " +
                                    "Please choose a different time slot.", conflictingSession.getName()));
                });
    }

    private void removeSessionFromList(String id, List<SessionDTO> sessionDTOS) {
        sessionDTOS.removeIf(sessionDTO -> id.equals(sessionDTO.getId()));
    }

}
