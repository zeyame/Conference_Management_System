package service;

import dto.RegistrationDTO;
import dto.UserDTO;
import exception.UserLoginException;
import util.LoggerUtil;
import util.PasswordService;

import java.util.Optional;

public class AuthenticationService {
    private final UserService userService;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }
    public boolean validateRegistration(RegistrationDTO registrationDTO) {
        // check if email is not already registered
        String email = registrationDTO.getEmail();
        if (userService.isEmailRegistered(email)) {
            return false;
        }

        // hashing password
        char[] plainPassword = registrationDTO.getPassword();
        String hashedPassword = PasswordService.hashPassword(plainPassword);

        // modifying the dto to store the hashed password
        char[] hashedPasswordChars = hashedPassword.toCharArray();
        registrationDTO.setPassword(hashedPasswordChars);

        LoggerUtil.getInstance().logInfo("Registration info has been successfully validated for user:\n" + registrationDTO);
        return true;
    }
}
