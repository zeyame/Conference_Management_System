package controller;

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

    public ResponseEntity<List<UserDTO>> getRegisteredSpeakers() {
        return ResponseEntity.success(userService.findAllSpeakers());
    }

    public ResponseEntity<ConferenceDTO> getManagedConference(String conferenceId) {
        try {
            ConferenceDTO conferenceDTO = conferenceService.findById(conferenceId);
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
            ConferenceDTO conferenceDTO = conferenceService.findById(conferenceId);
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
            ConferenceDTO conferenceDTO = conferenceService.findById(conferenceId);
            Set<String> sessionIds = conferenceDTO.getSessions();
            List<SessionDTO> sessions = sessionService.findByIds(sessionIds);
            LoggerUtil.getInstance().logInfo("Successfully retrieved sessions for '" + conferenceDTO.getName() + "'.");
            return ResponseEntity.success(sessions);
        } catch (ConferenceNotFoundException e) {
            return ResponseEntity.error("Could not find attendees for the conference with id '" + conferenceId + "' as it does not exist.");
        }
    }

    public ResponseEntity<Void> validateConferenceData(ConferenceDTO conferenceDTO) {
        // implement validation logic
        String name = conferenceDTO.getName();
        LocalDate startDate = conferenceDTO.getStartDate(), endDate = conferenceDTO.getEndDate();

        if (conferenceService.isNameTaken(name)) {
            LoggerUtil.getInstance().logWarning("Validation failed for conference creation. Conference name '" + name + "' is already taken.");
            return ResponseEntity.error("The provided conference name is already taken.");
        }

        if (!conferenceService.isTimePeriodAvailable(startDate, endDate)) {
            LoggerUtil.getInstance().logWarning("Validation failed for conference creation. Dates provided for the conference are not available..");
            return ResponseEntity.error("Another conference is already registered to take place within the time period provided. Please choose different dates.");
        }

        LoggerUtil.getInstance().logInfo("Validation successful for conference: " + name + " with dates: " + startDate + " - " + endDate);
        return ResponseEntity.success();
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
}
