package domain.model;

public class Speaker extends User {

    public Speaker() {}

    public Speaker(String id, String email, String name, String hashedPassword, UserRole role) {
        super(id, email, name, hashedPassword, role);
    }
}
