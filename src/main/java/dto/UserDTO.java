package dto;

import domain.model.UserRole;

public class UserDTO {
    private final String id;
    private final String email;
    private final String name;
    private final String hashedPassword;
    private final UserRole role;

    private UserDTO(String id, String email, String name, String hashedPassword, UserRole role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    public static UserDTO withoutPassword(String id, String email, String name, UserRole role) {
        return new UserDTO(id, email, name, null, role);
    }

    public static UserDTO withPassword(String id, String email, String name, String hashedPassword, UserRole role) {
        return new UserDTO(id, email, name, hashedPassword, role);
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public UserRole getRole() {
        return role;
    }
}
