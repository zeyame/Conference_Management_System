package domain.model;

public class Speaker extends User {

    public Speaker() {
        super(null, null, null, null, null);
    }

    public Speaker(String id, String email, String name, String hashedPassword, UserRole role) {
        super(id, email, name, hashedPassword, role);
    }
}
