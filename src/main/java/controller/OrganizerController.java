package controller;

import domain.model.Session;
import dto.ConferenceDTO;
import dto.SessionDTO;
import dto.UserDTO;
import exception.*;
import response.ResponseEntity;
import service.ConferenceService;
import service.SessionService;
import service.UserService;
import util.LoggerUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class OrganizerController {
    private final UserService userService;
    private final ConferenceService conferenceService;
    private final SessionService sessionService;

    public OrganizerController(UserService userService, ConferenceService conferenceService, SessionService sessionService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.sessionService = sessionService;
    }

    public ResponseEntity<Void> createConference(ConferenceDTO conferenceDTO) {
        String conferenceId = null;
        try {
            conferenceId = conferenceService.create(conferenceDTO);
            userService.addNewManagedConferenceForOrganizer(conferenceDTO.getOrganizerId(), conferenceId);

            return ResponseEntity.success();
        } catch (SavingDataException e) {
            // rolling back conference creation if adding the conference to the organizer's managed conferences failed
            if (conferenceId != null) {
                conferenceService.deleteById(conferenceId);
            }

            LoggerUtil.getInstance().logError("Error occurred during conference creation or adding conference to organizer's managed conferences: " + e.getMessage());
            return ResponseEntity.error("An error occurred while creating the conference. Please try again later.");
        } catch (Exception e) {
            LoggerUtil.getInstance().logError("An unexpected error occurred during conference creation: " + e.getMessage());
            return ResponseEntity.error("An unexpected error occurred. Please try again later.");
        }
    }

    public ResponseEntity<Void> createSession(SessionDTO sessionDTO) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(sessionDTO.getConferenceId());

            // create the session
            String sessionId = sessionService.create(sessionDTO, conferenceDTO);

            // assign a reference to the session to speaker
            assignSessionToSpeaker(sessionId, sessionDTO);

            // add a reference to the session to conference
            conferenceService.registerSession(conferenceDTO.getId(), sessionId);

            return ResponseEntity.success();
        } catch (ConferenceNotFoundException e) {
            LoggerUtil.getInstance().logError("Session creation failed due to invalid conference id.");
            return ResponseEntity.error(String.format("Conference with id '%s' does not exist.", sessionDTO.getConferenceId()));
        } catch (SessionCreationException | SavingDataException e) {
            return ResponseEntity.error(e.getMessage());
        }
    }

    public ResponseEntity<List<UserDTO>> getRegisteredSpeakers() {
        return ResponseEntity.success(userService.findAllSpeakers());
    }

    public ResponseEntity<ConferenceDTO> getManagedConference(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            return ResponseEntity.success(conferenceDTO);
        } catch (ConferenceNotFoundException e) {
            LoggerUtil.getInstance().logError("Conference with id '" + conferenceId + "' could not be found.");
            return ResponseEntity.error(e.getMessage());
        }
    }

    public ResponseEntity<List<ConferenceDTO>> getManagedConferences(String email) {
        try {
            Set<String> conferenceIds = userService.findManagedConferencesForOrganizer(email);
            List<ConferenceDTO> conferenceDTOS = conferenceService.findByIds(conferenceIds);
            return ResponseEntity.success(conferenceDTOS);
        } catch (InvalidUserRoleException | UserNotFoundException e) {
            LoggerUtil.getInstance().logError("Failed to retrieve managed conferences for user with email '" + email + "' due to the following reason: " + e.getMessage());
            return ResponseEntity.error("Error retrieving managed conferences. Please try again later.");
        }
    }

    public ResponseEntity<List<UserDTO>> getConferenceAttendees(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            Set<String> attendeeIds = conferenceDTO.getAttendees();
            List<UserDTO> attendees = userService.findByIds(attendeeIds);
            LoggerUtil.getInstance().logInfo("Successfully retrieved attendees for '" + conferenceDTO.getName() + "'.");
            return ResponseEntity.success(attendees);
        } catch (ConferenceNotFoundException e) {
            return ResponseEntity.error("Could not find attendees for the conference with id '" + conferenceId + "' as it does not exist.");
        }
    }

    public ResponseEntity<List<SessionDTO>> getConferenceSessions(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            Set<String> sessionIds = conferenceDTO.getSessions();
            System.out.println("Session ids size in '" + conferenceDTO.getName() + "': " + sessionIds.size());
            List<SessionDTO> sessions = sessionService.findByIds(sessionIds);
            System.out.println("Sessions size retrieved from service: " + sessions.size());
            LoggerUtil.getInstance().logInfo("Successfully retrieved sessions for '" + conferenceDTO.getName() + "'.");
            return ResponseEntity.success(sessions);
        } catch (ConferenceNotFoundException e) {
            return ResponseEntity.error("Could not find attendees for the conference with id '" + conferenceId + "' as it does not exist.");
        }
    }

    public ResponseEntity<SessionDTO> getSessionDetails(String sessionId) {
        try {
            SessionDTO sessionDTO = sessionService.getById(sessionId);
            return ResponseEntity.success(sessionDTO);
        } catch (SessionNotFoundException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to retrieve session details as session with id '%s' does not exist.", sessionId));
            return ResponseEntity.error(String.format("Session with id '%s' does not exist.", sessionId));
        }
    }

    public ResponseEntity<List<UserDTO>> getSessionAttendees(String sessionId) {
        try {
            SessionDTO sessionDTO = sessionService.getById(sessionId);
            Set<String> attendeeIds = sessionDTO.getRegisteredAttendees();
            List<UserDTO> attendees = userService.findByIds(attendeeIds);
            LoggerUtil.getInstance().logInfo(String.format("Successfully retrieved attendees for session '%s'.", sessionDTO.getName()));
            return ResponseEntity.success(attendees);
        } catch (SessionNotFoundException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to retrieve session details as session with id '%s' does not exist.", sessionId));
            return ResponseEntity.error(String.format("Session with id '%s' does not exist.", sessionId));
        }
    }

    public ResponseEntity<Void> validateConferenceData(ConferenceDTO conferenceDTO) {
        // implement validation logic
        String name = conferenceDTO.getName();
        LocalDate startDate = conferenceDTO.getStartDate(), endDate = conferenceDTO.getEndDate();

        // ensure conference name is available
        if (conferenceService.isNameTaken(name)) {
            LoggerUtil.getInstance().logWarning("Validation failed for conference creation. Conference name '" + name + "' is already taken.");
            return ResponseEntity.error("The provided conference name is already taken.");
        }

        // ensure selected time period is available
        if (!conferenceService.isTimePeriodAvailable(startDate, endDate)) {
            LoggerUtil.getInstance().logWarning("Validation failed for conference creation. Dates provided for the conference are not available..");
            return ResponseEntity.error("Another conference is already registered to take place within the time period provided. Please choose different dates.");
        }

        LoggerUtil.getInstance().logInfo("Validation successful for conference: " + name + " with dates: " + startDate + " - " + endDate);
        return ResponseEntity.success();
    }


    private void assignSessionToSpeaker(String sessionId, SessionDTO sessionDTO) {
        try {
            userService.assignNewSessionForSpeaker(
                    sessionDTO.getSpeakerId(),
                    sessionId,
                    LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime()),
                    LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime())
            );
        } catch (UserNotFoundException | InvalidUserRoleException | SavingDataException e) {
            // rollback session creation if assigning session to speaker fails
            sessionService.deleteById(sessionId);
            LoggerUtil.getInstance().logError("Session creation failed: " + e.getMessage());
            throw handleAssignmentError(e, sessionDTO.getSpeakerId());
        }
    }

    private SessionCreationException handleAssignmentError(Exception e, String speakerId) {
        if (e instanceof UserNotFoundException) {
            return SessionCreationException.invalidSpeaker("Speaker with id '" + speakerId + "' does not exist.");
        } else if (e instanceof InvalidUserRoleException) {
            return SessionCreationException.invalidSpeaker("User with id '" + speakerId + "' does not have speaker permissions.");
        } else {
            return SessionCreationException.savingFailure("An unexpected error occurred when assigning session to speaker.");
        }
    }

}
