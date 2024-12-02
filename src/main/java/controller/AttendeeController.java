package controller;

import dto.ConferenceDTO;
import exception.UserException;
import response.ResponseEntity;
import service.UserService;
import service.conference.ConferenceService;

import java.util.List;
import java.util.Set;

public class AttendeeController {

    private final UserService userService;
    private final ConferenceService conferenceService;

    public AttendeeController(UserService userService, ConferenceService conferenceService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
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
}
