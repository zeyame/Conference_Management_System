package controller;

import dto.ConferenceDTO;
import dto.ConferenceFormDTO;
import exception.ConferenceCreationException;
import exception.InvalidUserRoleException;
import exception.SavingDataException;
import exception.UserNotFoundException;
import service.ConferenceService;
import service.UserService;
import util.LoggerUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class OrganizerController {
    private final UserService userService;
    private final ConferenceService conferenceService;

    public OrganizerController(UserService userService, ConferenceService conferenceService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
    }

    public List<ConferenceDTO> getManagedConferences(String email) {
        try {
            Set<String> conferenceIds = userService.findManagedConferencesForOrganizer(email);
            return conferenceService.findByIds(conferenceIds);

        } catch (InvalidUserRoleException | UserNotFoundException e) {
            LoggerUtil.getInstance().logError("Failed to retrieve managed conferences for user with email '" + email + "' due to the following reason: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public void validateConferenceData(ConferenceFormDTO conferenceFormDTO) {
        // implement validation logic
        String name = conferenceFormDTO.getName();
        Date startDate = conferenceFormDTO.getStartDate(), endDate = conferenceFormDTO.getEndDate();

        if (conferenceService.isNameTaken(name)) {
            LoggerUtil.getInstance().logWarning("Validation failed for conference creation. Conference name '" + name + "' is already taken.");
            throw ConferenceCreationException.nameTaken();
        }

        if (!conferenceService.isTimePeriodAvailable(startDate, endDate)) {
            LoggerUtil.getInstance().logWarning("Validation failed for conference creation. Dates provided for the conference are not available..");
            throw ConferenceCreationException.dateUnavailable();
        }

        LoggerUtil.getInstance().logInfo("Validation successful for conference: " + name + " with dates: " + startDate + " - " + endDate);
    }

    public void createConference(ConferenceFormDTO conferenceFormDTO) {
        String conferenceId = conferenceService.create(conferenceFormDTO);
        try {
            userService.addNewManagedConferenceForOrganizer(conferenceFormDTO.getOrganizerId(), conferenceId);
        } catch (Exception e) {
            // rolling back conference creation if adding the conference to the organizer's managed conferences failed
            conferenceService.deleteById(conferenceId);

            // propagate saving data exception upwards for user friendly error
            if (e instanceof SavingDataException) throw e;
        }
    }
}
