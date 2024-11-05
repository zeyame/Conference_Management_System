package domain.model;

public class Attendee extends User {
    public Attendee(String id, String email, String name, String hashedPassword, UserRole userRole) {
        super(id, email, name, hashedPassword, userRole);
    }
}
