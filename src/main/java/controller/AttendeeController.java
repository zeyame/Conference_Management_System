package controller;

import dto.ConferenceDTO;
import dto.UserDTO;
import exception.ConferenceException;
import exception.UserException;
import response.ResponseEntity;
import service.UserService;
import service.conference.ConferenceService;
import util.LoggerUtil;

import java.util.List;
import java.util.Set;

public class AttendeeController {

    private final UserService userService;
    private final ConferenceService conferenceService;

    public AttendeeController(UserService userService, ConferenceService conferenceService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
    }

    public ResponseEntity<Void> registerForConference(String attendeeId, String conferenceId) {
        try {
             conferenceService.registerAttendeeToConference(conferenceId, attendeeId);
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
            LoggerUtil.getInstance().logError(String.format("Conference with id '%s' does not exist.", conferenceId));
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

    public ResponseEntity<String> getOrganizerName(String organizerId) {
        try {
            String organizerName = userService.getNameById(organizerId);
            return ResponseEntity.success(organizerName);
        } catch (UserException e) {
            return ResponseEntity.error(e.getMessage());
        }
    }
}
