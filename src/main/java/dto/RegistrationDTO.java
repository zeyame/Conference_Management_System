package dto;

import domain.model.user.UserRole;

public class RegistrationDTO {
    private final String email;
    private final String name;
    private final String speakerBio;
    private final String employeeId;
    private char[] password;
    private final UserRole userRole;


    public RegistrationDTO(String email, String name, String speakerBio, String employeeId, char[] password, UserRole userRole) {
        this.email = email;
        this.name = name;
        this.speakerBio = speakerBio;
        this.employeeId = employeeId;
        this.password = password;
        this.userRole = userRole;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSpeakerBio() {
        return speakerBio;
    }

    public String getEmployeeId() {return this.employeeId;}

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    @Override
    public String toString() {
        return "RegistrationDTO{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", userRole=" + userRole +
                '}';
    }
}
