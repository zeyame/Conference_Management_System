package controller;

import dto.ConferenceDTO;
import exception.InvalidUserRoleException;
import exception.UserNotFoundException;
import service.ConferenceService;
import service.UserService;
import util.LoggerUtil;

import java.util.Collections;
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
}
