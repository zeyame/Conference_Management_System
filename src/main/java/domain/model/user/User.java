package domain.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "role", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Organizer.class, name = "Organizer"),
        @JsonSubTypes.Type(value = Attendee.class, name = "Attendee"),
        @JsonSubTypes.Type(value = Speaker.class, name = "Speaker")
})
public abstract class User {
    private final String id;
    private final String email;
    private final String name;
    private final String hashedPassword;
    @JsonProperty("role")
    private final UserRole role;

    // no-arg constructor for JSON serialization/de-serialization
    private User() {
        this.id = null;
        this.email = null;
        this.name = null;
        this.hashedPassword = null;
        this.role = null;
    }

    public User(String id, String email, String name, String hashedPassword, UserRole role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.role = role;
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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

}
