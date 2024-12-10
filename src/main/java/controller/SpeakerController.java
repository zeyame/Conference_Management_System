package controller;

import dto.SessionDTO;
import response.ResponseEntity;
import service.UserService;
import service.session.SessionService;
import util.LoggerUtil;

import java.util.List;
import java.util.Set;

public class SpeakerController {

    private final UserService userService;
    private final SessionService sessionService;

    public SpeakerController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    public ResponseEntity<List<SessionDTO>> getAssignedSessions(String speakerId) {
        try {
            Set<String> speakerSessionIds = userService.findAssignedSessionsForSpeaker(speakerId);
            List<SessionDTO> sessionDTOs = sessionService.findAllById(speakerSessionIds);
            return ResponseEntity.success(sessionDTOs);
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to get assigned sessions for speaker with id '%s': %s. %s", speakerId, e.getMessage(), e));
            return ResponseEntity.error("An unexpected error occurred when fetching your assigned sessions. Please try again later.");
        }
    }

    public ResponseEntity<Void> updateBio(String speakerId, String newBio) {
        try {
            userService.updateSpeakerBio(speakerId, newBio);
            return ResponseEntity.success();
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to update bio for speaker with id '%s': %s, %s", speakerId, e.getMessage(), e));
            return ResponseEntity.error("An unexpected error occurred when updating your bio. Please try again later.");
        }
    }
}
