package service;

import dto.ConferenceDTO;
import dto.SessionDTO;
import dto.UserDTO;
import service.conference.ConferenceService;
import service.session.SessionService;

import java.util.List;
import java.util.Set;

public class ServiceMediator {

    private final UserService userService;
    private final ConferenceService conferenceService;
    private final SessionService sessionService;
    private final FeedbackService feedbackService;

    public ServiceMediator(UserService userService, ConferenceService conferenceService, SessionService sessionService, FeedbackService feedbackService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
        this.sessionService = sessionService;
        this.feedbackService = feedbackService;
    }

    // SESSION RELATED METHODS - CRUD ORDERED
    public List<SessionDTO> findSessionsByIds(Set<String> sessionIds) {
        return sessionService.findAllById(sessionIds);
    }

    public void deleteAllSessionsById(Set<String> sessionIds) {
        sessionService.deleteAllById(sessionIds);
    }


    // USER RELATED METHODS  - CRUD ORDERED
    public void assignConferenceToOrganizer(String conferenceId, String organizerId) {
        userService.assignConferenceToOrganizer(organizerId, conferenceId);
    }

    public void addConferenceToAttendee(String conferenceId, String attendeeId) {
        userService.addConferenceToAttendee(attendeeId, conferenceId);
    }

    public void assignNewSessionForSpeaker(SessionDTO sessionDTO) {
        userService.assignNewSessionForSpeaker(sessionDTO);
    }

    public UserDTO getUserById(String userId) {
        return userService.getBydId(userId);
    }

    public List<UserDTO> findAllAttendees() {
        return userService.findAllAttendees();
    }

    public List<UserDTO> findAllSpeakers() {
        return userService.findAllSpeakers();
    }

    public List<UserDTO> findAllUsersById(Set<String> userIds) {
        return userService.findAllById(userIds);
    }

    public void unassignConferenceFromOrganizer(String conferenceId, String organizerId) {
        userService.unassignConferenceFromOrgaizer(organizerId, conferenceId);
    }

    public void removeConferenceFromAttendee(String conferenceId, String attendeeId) {
        userService.removeConferenceFromAttendee(attendeeId, conferenceId);
    }

    public void unassignSessionFromSpeaker(String sessionId, String speakerId) {
        userService.unassignSessionFromSpeaker(speakerId, sessionId);
    }


    // CONFERENCE RELATED METHODS  - CRUD ORDERED
    public void registerSessionInConference(SessionDTO sessionDTO) {
        conferenceService.registerSession(sessionDTO);
    }
    public ConferenceDTO getConferenceById(String conferenceId) {
        return conferenceService.getById(conferenceId);
    }

    public void removeSessionFromConference(String sessionId, String conferenceId) {
        conferenceService.removeSession(conferenceId, sessionId);
    }


    // FEEDBACK RELATED METHODS - CRUD ORDERED
    public void deleteAllFeedbackById(Set<String> feedbackIds) {
        feedbackService.deleteAllById(feedbackIds);
    }

}
