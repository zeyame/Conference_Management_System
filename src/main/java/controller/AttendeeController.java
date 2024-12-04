package controller;

import dto.ConferenceDTO;
import dto.SessionDTO;
import dto.UserDTO;
import exception.ConferenceException;
import exception.SessionException;
import exception.UserException;
import response.ResponseEntity;
import service.UserService;
import service.conference.ConferenceService;
import service.session.SessionService;
import util.LoggerUtil;

import java.util.List;
import java.util.Set;

public class AttendeeController {

    private final UserService userService;
    private final ConferenceService conferenceService;
    private final SessionService sessionService;

    public AttendeeController(UserService userService, ConferenceService conferenceService, SessionService sessionService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.sessionService = sessionService;
    }

    public ResponseEntity<Void> registerForConference(String attendeeId, String conferenceId) {
        try {
             conferenceService.registerAttendeeToConference(conferenceId, attendeeId);
             return ResponseEntity.success();
        } catch (Exception e) {
            return ResponseEntity.error(e.getMessage());
        }
    }

    public ResponseEntity<Void> registerForSession(String attendeeId, String sessionId) {
        try {
            sessionService.registerAttendee(sessionId, attendeeId);
            return ResponseEntity.success();
        } catch (Exception e) {
            return ResponseEntity.error(e.getMessage());
        }
    }

    public ResponseEntity<ConferenceDTO> getConference(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            return ResponseEntity.success(conferenceDTO);
        } catch (ConferenceException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to retrieve conference with id '%s': %s", conferenceId, e.getMessage()));
            return ResponseEntity.error(e.getMessage());
        }
    }

    public ResponseEntity<SessionDTO> getSession(String sessionId) {
        try {
            SessionDTO sessionDTO = sessionService.getById(sessionId);
            return ResponseEntity.success(sessionDTO);
        } catch (SessionException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to retrieve session with id '%s': %s", sessionId, e.getMessage()));
            return ResponseEntity.error(e.getMessage());
        }
    }

    public ResponseEntity<List<ConferenceDTO>> getUpcomingConferences(String attendeeId) {
        try {
            Set<String> registeredConferences = userService.findRegisteredConferencesForAttendee(attendeeId);
            List<ConferenceDTO> upcomingConferences = conferenceService.findAllUpcoming();

            List<ConferenceDTO> upcomingNotRegisteredConferences = upcomingConferences.stream()
                    .filter(conferenceDTO -> !registeredConferences.contains(conferenceDTO.getId()))
                    .toList();

            return ResponseEntity.success(upcomingNotRegisteredConferences);
        } catch (UserException e) {
            return ResponseEntity.error(e.getMessage());
        }
    }

    public ResponseEntity<List<ConferenceDTO>> getRegisteredConferences(String attendeeId) {
        try {
            Set<String> registeredConferencesIds = userService.findRegisteredConferencesForAttendee(attendeeId);
            List<ConferenceDTO> registeredConferences = conferenceService.findAllById(registeredConferencesIds);
            return ResponseEntity.success(registeredConferences);
        } catch (UserException e) {
            return ResponseEntity.error(e.getMessage());
        }
    }


    public ResponseEntity<List<SessionDTO>> getConferenceSessions(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            List<SessionDTO> sessionDTOS = sessionService.findAllById(conferenceDTO.getSessions());
            return ResponseEntity.success(sessionDTOS);
        } catch (Exception e) {
            return ResponseEntity.error(e.getMessage());
        }
    }

    public ResponseEntity<List<SessionDTO>> getUpcomingConferenceSessions(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            List<SessionDTO> sessionDTOS = sessionService.findAllUpcomingById(conferenceDTO.getSessions());
            return ResponseEntity.success(sessionDTOS);
        } catch (Exception e) {
            return ResponseEntity.error(e.getMessage());
        }
    }

    public ResponseEntity<String> getOrganizerName(String organizerId) {
        try {
            String organizerName = userService.getNameById(organizerId);
            return ResponseEntity.success(organizerName);
        } catch (UserException e) {
            return ResponseEntity.error(e.getMessage());
        }
    }

    public ResponseEntity<Void> leaveConference(String attendeeId, String conferenceId) {
        try {
            conferenceService.removeAttendee(conferenceId, attendeeId);
            return ResponseEntity.success();
        } catch (Exception e) {
            return ResponseEntity.error("An unexpected error occurred when removing you from conference. Please try again later.");
        }
    }
}
