package service;

import dto.ConferenceDTO;
import dto.SessionDTO;
import dto.UserDTO;
import service.conference.ConferenceService;
import service.session.SessionService;

import java.time.LocalDateTime;
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

    public void addFeedbackToSession(String feedbackId, String sessionId) {
        sessionService.addFeedback(sessionId, feedbackId);
    }

    public SessionDTO getSessionById(String sessionId) {
        return sessionService.getById(sessionId);
    }

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

    public void addSessionToAttendee(SessionDTO sessionDTO, String attendeeId) {
        userService.addSessionToAttendee(attendeeId, sessionDTO);
    }

    public void assignNewSessionForSpeaker(SessionDTO sessionDTO) {
        userService.assignNewSessionForSpeaker(sessionDTO);
    }

    public void addFeedbackToSpeaker(String feedbackId, String speakerId) {
        userService.addFeedbackToSpeaker(speakerId, feedbackId);
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
        userService.unassignConferenceFromOrganizer(organizerId, conferenceId);
    }

    public void removeConferenceFromAttendee(String conferenceId, String attendeeId) {
        userService.removeConferenceFromAttendee(attendeeId, conferenceId);
    }

    public void unassignSessionFromSpeaker(String sessionId, String speakerId) {
        userService.unassignSessionFromSpeaker(speakerId, sessionId);
    }

    public void removeSessionFromAttendee(String sessionId, String attendeeId) {
        userService.removeSessionFromAttendee(attendeeId, sessionId);
    }


    // CONFERENCE RELATED METHODS  - CRUD ORDERED
    public void registerSessionInConference(SessionDTO sessionDTO) {
        conferenceService.registerSession(sessionDTO);
    }

    public void addFeedbackToConference(String feedbackId, String conferenceId) {
        conferenceService.addFeedback(conferenceId, feedbackId);
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
