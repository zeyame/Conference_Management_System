package dto;

import domain.model.UserRole;

public class AuthenticatedUserDTO {
    private final String id;
    private final String email;
    private final String hashedPassword;

    public AuthenticatedUserDTO(String id, String email, String hashedPassword) {
        this.id = id;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

}
