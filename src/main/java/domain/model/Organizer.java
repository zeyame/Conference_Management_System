package domain.model;

import java.util.HashSet;
import java.util.Set;

public class Organizer extends User {

    private final Set<String> managedConferences;
    public Organizer(String id, String email, String name, String hashedPassword, UserRole role) {
        super(id, email, name, hashedPassword, role);
        this.managedConferences = new HashSet<>();
    }

    public Set<String> getManagedConferences() {
        return managedConferences;
    }

    public void addConference(String conferenceId) {
        managedConferences.add(conferenceId);
    }

    public void removeConference(String conferenceId) {
        managedConferences.remove(conferenceId);
    }
}
