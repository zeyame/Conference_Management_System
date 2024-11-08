package service;

import dto.RegistrationDTO;
import dto.UserDTO;
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

        return true;
    }

    public boolean validateLogin(String email, char[] password) {
        // if email does not exist
        if (!userService.isEmailRegistered(email)) {
            return false;
        }

        Optional<UserDTO> authenticatedUserDTO = userService.findAuthenticatedByEmail(email);
        if (authenticatedUserDTO.isPresent()) {
            UserDTO authenticatedUser = authenticatedUserDTO.get();

            String hashedPassword = authenticatedUser.getHashedPassword();
            return PasswordService.verifyPassword(password, hashedPassword);
        }

        return false;
    }
}
