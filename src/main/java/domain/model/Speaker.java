package domain.model;

import java.time.LocalDateTime;
import java.util.*;

public class Speaker extends User {
    private final Map<LocalDateTime, AssignedSession> assignedSessions;          // K: Session Time, V: Session ID

    public Speaker() {
        super(null, null, null, null, null);
        this.assignedSessions = new TreeMap<>();
    }

    public Speaker(String id, String email, String name, String hashedPassword, UserRole role) {
        super(id, email, name, hashedPassword, role);
        this.assignedSessions = new TreeMap<>();
    }

    public boolean isAvailable(LocalDateTime startTime, LocalDateTime endTime) {
        return assignedSessions.values()
                .stream()
                .noneMatch(assignedSession -> assignedSession.overlapsWith(startTime, endTime));
    }

    public void assignSession(String sessionId, LocalDateTime start, LocalDateTime end) {
        if (!isAvailable(start, end)) {
            return;
        }
        assignedSessions.put(start, new AssignedSession(sessionId, start, end));
    }


    public static class AssignedSession {
        private final String id;
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;

        public AssignedSession() {
            this.id = null;
            this.startTime = null;
            this.endTime = null;
        }

        public AssignedSession(String id, LocalDateTime startTime, LocalDateTime endTime) {
            this.id = id;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public boolean overlapsWith(LocalDateTime otherStart, LocalDateTime otherEnd) {
            return !this.startTime.isAfter(otherEnd) && !this.endTime.isBefore(otherStart);
        }

        public String getId() {
            return this.id;
        }

        public LocalDateTime getStartTime() {
            return this.startTime;
        }

        public LocalDateTime getEndTime() {
            return this.endTime;
        }
    }

    public Map<LocalDateTime, AssignedSession> getAssignedSessions() {
        return new HashMap<>(this.assignedSessions);          // defensive copy
    }
}
