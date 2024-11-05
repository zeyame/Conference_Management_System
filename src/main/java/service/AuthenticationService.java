package service;

import dto.RegistrationDTO;
import exception.UserRegistrationException;
import util.PasswordService;

public class AuthenticationService {
    private final UserService userService;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }
    public RegistrationDTO validateRegistration(RegistrationDTO registrationDTO) {
        // check if email is not already registered
        String email = registrationDTO.getEmail();
        if (userService.isEmailRegistered(email)) {
            throw UserRegistrationException.emailExists();
        }

        // hashing password
        char[] plainPassword = registrationDTO.getPassword();
        String hashedPassword = PasswordService.hashPassword(plainPassword);

        // modifying the dto to store the hashed password
        char[] hashedPasswordChars = hashedPassword.toCharArray();
        registrationDTO.setPassword(hashedPasswordChars);

        return registrationDTO;
    }
}
