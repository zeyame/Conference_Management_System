package controller;

import dto.ConferenceDTO;
import dto.FeedbackDTO;
import dto.SessionDTO;
import dto.UserDTO;
import exception.*;
import response.ResponseEntity;
import service.ConferenceService;
import service.FeedbackService;
import service.SessionService;
import service.UserService;
import util.LoggerUtil;

import java.time.LocalDate;
import java.util.*;

public class OrganizerController {
    private final UserService userService;
    private final ConferenceService conferenceService;
    private final SessionService sessionService;
    private final FeedbackService feedbackService;

    public OrganizerController(UserService userService, ConferenceService conferenceService, SessionService sessionService, FeedbackService feedbackService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.sessionService = sessionService;
        this.feedbackService = feedbackService;
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
            sessionService.createOrUpdate(sessionDTO, false);
            return ResponseEntity.success();
        } catch (SessionException e) {
            return ResponseEntity.error(e.getMessage());
        }
    }

    public ResponseEntity<Void> updateSession(SessionDTO updatedSessionDTO) {
        try {
            sessionService.createOrUpdate(updatedSessionDTO, true);
            LoggerUtil.getInstance().logInfo(String.format("Session '%s' was updated successfully.", updatedSessionDTO.getName()));
            return ResponseEntity.success();
        } catch (ConferenceNotFoundException e) {
            LoggerUtil.getInstance().logError("Failed to update session '%s' as conference id does not exist.");
            return ResponseEntity.error("Failed to update session data due to invalid conference id provided.");
        } catch (SessionException e) {
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
            List<ConferenceDTO> conferenceDTOS = conferenceService.findAllById(conferenceIds);
            return ResponseEntity.success(conferenceDTOS);
        } catch (InvalidUserRoleException | UserNotFoundException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to retrieve managed conferences for user with email '%s' due to the following reasons: %s", email, e.getMessage()));
            return ResponseEntity.error("Error retrieving managed conferences. Please try again later.");
        }
    }

    public ResponseEntity<List<UserDTO>> getConferenceAttendees(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            Set<String> attendeeIds = conferenceDTO.getAttendees();
            List<UserDTO> attendees = userService.findAllById(attendeeIds);
            LoggerUtil.getInstance().logInfo(String.format("Successfully retrieved attendees for '%s'.", conferenceDTO.getName()));
            return ResponseEntity.success(attendees);
        } catch (ConferenceNotFoundException e) {
            return ResponseEntity.error(String.format("Could not find attendees for the conference with id '%s' as it does not exist.", conferenceId));
        }
    }

    public ResponseEntity<List<SessionDTO>> getConferenceSessions(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.getById(conferenceId);
            Set<String> sessionIds = conferenceDTO.getSessions();
            List<SessionDTO> sessions = sessionService.findAllById(sessionIds);
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
            List<UserDTO> attendees = userService.findAllById(attendeeIds);
            LoggerUtil.getInstance().logInfo(String.format("Successfully retrieved attendees for session '%s'.", sessionDTO.getName()));
            return ResponseEntity.success(attendees);
        } catch (SessionNotFoundException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to retrieve session details as session with id '%s' does not exist.", sessionId));
            return ResponseEntity.error(String.format("Session with id '%s' does not exist.", sessionId));
        }
    }

    public ResponseEntity<List<FeedbackDTO>> getSessionFeedback(String sessionId) {
        try {
            SessionDTO sessionDTO = sessionService.getById(sessionId);
            Set<String> feedbackIds = sessionDTO.getFeedback();
            List<FeedbackDTO> feedback = feedbackService.findAllById(feedbackIds);
            LoggerUtil.getInstance().logInfo(String.format("Successfully retrieved all feedback for session with id'%s'.", sessionId));
            return ResponseEntity.success(feedback);
        } catch (SessionNotFoundException e) {
            LoggerUtil.getInstance().logError(String.format("Could not retrieve feedback for session wit id '%s' as it does not exist.", sessionId));
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

}
