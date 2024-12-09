package controller;

import dto.ConferenceDTO;
import dto.FeedbackDTO;
import dto.SessionDTO;
import dto.UserDTO;
import exception.ConferenceException;
import exception.SessionException;
import exception.UserException;
import response.ResponseEntity;
import service.FeedbackService;
import service.UserService;
import service.conference.ConferenceService;
import service.session.SessionService;
import util.LoggerUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AttendeeController {

    private final UserService userService;
    private final ConferenceService conferenceService;
    private final SessionService sessionService;
    private final FeedbackService feedbackService;

    public AttendeeController(UserService userService, ConferenceService conferenceService, SessionService sessionService, FeedbackService feedbackService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.sessionService = sessionService;
        this.feedbackService = feedbackService;
    }

    public ResponseEntity<Void> registerForConference(String attendeeId, String conferenceId) {
        try {
             conferenceService.registerAttendeeToConference(conferenceId, attendeeId);
             return ResponseEntity.success();
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to register attendee to conference: %s", e.getMessage()));
            return ResponseEntity.error("An unexpected error occurred when registering you for the conference. Please try again later.");
        }
    }

    public ResponseEntity<Void> registerForSession(String attendeeId, String sessionId) {
        try {
            sessionService.registerAttendee(sessionId, attendeeId);
            return ResponseEntity.success();
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to register attendee for session: %s", e.getMessage()));
            return ResponseEntity.error("An unexpected error occurred when registering you for the session. Please try again later.");
        }
    }

    public ResponseEntity<Void> submitFeedback(FeedbackDTO feedbackDTO) {
        try {
            feedbackService.submit(feedbackDTO);
            return ResponseEntity.success();
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to submit feedback: %s %s", e.getMessage(), e));
            return ResponseEntity.error(e.getMessage());
        }
    }

    public ResponseEntity<ConferenceDTO> getConference(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            return ResponseEntity.success(conferenceDTO);
        } catch (ConferenceException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to retrieve conference with id '%s': %s", conferenceId, e.getMessage()));
            LoggerUtil.getInstance().logError(String.format("Failed to get conference: %s", e.getMessage()));
            return ResponseEntity.error("An unexpected error occurred when retrieving conference data.");
        }
    }

    public ResponseEntity<SessionDTO> getSession(String sessionId) {
        try {
            SessionDTO sessionDTO = sessionService.getById(sessionId);
            return ResponseEntity.success(sessionDTO);
        } catch (SessionException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to retrieve session with id '%s': %s", sessionId, e.getMessage()));
            return ResponseEntity.error("An unexpected error occurred when retrieving session data. Please try again later.");
        }
    }

    public ResponseEntity<List<SessionDTO>> getPersonalSchedule(String attendeeId, String conferenceId) {
        try {
            // getting the sessions in conference
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            Set<String> sessionsInConference = conferenceDTO.getSessions();

            // retrieving sessions attendee is registered for within the conference
            List<SessionDTO> sessionDTOS = sessionService.findAllById(sessionsInConference);

            List<SessionDTO> attendeeSessions = sessionDTOS.stream()
                    .filter(sessionDTO -> sessionDTO.getRegisteredAttendees().contains(attendeeId))
                    .toList();
            return ResponseEntity.success(attendeeSessions);
        } catch (Exception e) {
            LoggerUtil.getInstance().logInfo(String.format("Failed to fetch attendee '%s' personal schedule for conference '%s': %s",
                    attendeeId, conferenceId, e.getMessage() + e));
            return ResponseEntity.error("An unexpected error occurred when retrieving your personal schedule " +
                    "for this conference. Please try again later.");
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
            LoggerUtil.getInstance().logError(String.format("Failed to get upcoming conferences: %s", e.getMessage()));
            return ResponseEntity.error("An unexpected error occurred when retrieving upcoming conferences. Please try again later.");
        }
    }

    public ResponseEntity<List<ConferenceDTO>> getRegisteredConferences(String attendeeId) {
        try {
            Set<String> registeredConferencesIds = userService.findRegisteredConferencesForAttendee(attendeeId);
            List<ConferenceDTO> registeredConferences = conferenceService.findAllById(registeredConferencesIds);
            return ResponseEntity.success(registeredConferences);
        } catch (UserException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to get registered conferences: %s", e.getMessage()));
            return ResponseEntity.error("An unexpected error occurred when retrieving your registered conferences. Please try again later.");
        }
    }


    public ResponseEntity<List<SessionDTO>> getConferenceSessions(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            List<SessionDTO> sessionDTOS = sessionService.findAllById(conferenceDTO.getSessions());
            return ResponseEntity.success(sessionDTOS);
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to get conference sessions: %s", e.getMessage()));
            return ResponseEntity.error("An unexpected error occurred when retrieving conference sessions. Please try again later.");
        }
    }

    public ResponseEntity<List<SessionDTO>> getUpcomingConferenceSessions(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            List<SessionDTO> sessionDTOS = sessionService.findAllUpcomingById(conferenceDTO.getSessions());
            return ResponseEntity.success(sessionDTOS);
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to get upcoming conference sessions: %s", e.getMessage()));
            return ResponseEntity.error("An unexpected error occurred when retrieving upcoming sessions. Please try again later.");
        }
    }


    public ResponseEntity<String> getOrganizerName(String organizerId) {
        try {
            String organizerName = userService.getNameById(organizerId);
            return ResponseEntity.success(organizerName);
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to get name for user with id '%s': %s %s", organizerId, e.getMessage(), e));
            return ResponseEntity.error("An unexpected error occurred when fetching session details. Please try again later.");
        }
    }

    public ResponseEntity<List<UserDTO>> getSpeakersInConference(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            List<UserDTO> speakers = userService.findAllById(conferenceDTO.getSpeakers());
            return ResponseEntity.success(speakers);
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to get speakers in conference '%s': %s %s", conferenceId, e.getMessage(), e));
            return ResponseEntity.error("An unexpected error occurred when fetching speakers in the conference. Please try again later.");
        }
    }

    public ResponseEntity<Map<String, String>> getSpeakerBios(Set<String> speakerIds) {
        try {
            Map<String, String> speakerBios = userService.findSpeakerBiosById(speakerIds);
            return ResponseEntity.success(speakerBios);
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to fetch speaker bios: %s %s", e.getMessage(), e));
            return ResponseEntity.error("An unexpected error occurred when retrieving speaker bios. Please try again later.");
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

    public ResponseEntity<Void> leaveSession(String sessionId, String attendeeId) {
        try {
            sessionService.unregisterAttendee(sessionId, attendeeId);
            return ResponseEntity.success();
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to unregister attendee from session: %s", e.getMessage()));
            return ResponseEntity.error("An unexpected error occurred when unregistering you from the session. Please try again later.");
        }
    }
}
