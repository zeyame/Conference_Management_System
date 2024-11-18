package controller;

import dto.ConferenceDTO;
import dto.ConferenceFormDTO;
import exception.*;
import response.ResponseEntity;
import service.ConferenceService;
import service.UserService;
import util.LoggerUtil;

import java.util.*;

public class OrganizerController {
    private final UserService userService;
    private final ConferenceService conferenceService;

    public OrganizerController(UserService userService, ConferenceService conferenceService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
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

    public ResponseEntity<Void> validateConferenceData(ConferenceFormDTO conferenceFormDTO) {
        // implement validation logic
        String name = conferenceFormDTO.getName();
        Date startDate = conferenceFormDTO.getStartDate(), endDate = conferenceFormDTO.getEndDate();

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

    public ResponseEntity<Void> createConference(ConferenceFormDTO conferenceFormDTO) {
        String conferenceId = null;
        try {
            conferenceId = conferenceService.create(conferenceFormDTO);
            userService.addNewManagedConferenceForOrganizer(conferenceFormDTO.getOrganizerId(), conferenceId);

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
