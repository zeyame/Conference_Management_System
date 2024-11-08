package controller;

import domain.model.UserRole;
import dto.RegistrationDTO;
import dto.UserDTO;
import service.AuthenticationService;
import service.UserService;
import ui.*;
import util.EmailService;
import util.LoggerUtil;
import util.UIFactory;

import java.util.Optional;

public class MainController {
    private final AuthenticationService authService;
    private final UserService userService;
    private final EmailService emailService;

    public MainController(AuthenticationService authService, UserService userService, EmailService emailService) {
        this.authService = authService;
        this.userService = userService;
        this.emailService = emailService;
    }

    public boolean validateRegistration(RegistrationDTO registrationDTO) {
        return authService.validateRegistration(registrationDTO);
    }
    public boolean validateLogin(String email, char[] password) {
        return authService.validateLogin(email, password);
    }

    public void registerUser(RegistrationDTO validatedDTO) {
        userService.registerUser(validatedDTO);
        emailService.sendWelcomeEmail(validatedDTO.getEmail(), validatedDTO.getName());
        LoggerUtil.getInstance().logInfo("Registration is successful for user: \n" + validatedDTO);
    }

    public void loginUser(String email) {
        Optional<UserDTO> optionalUserDTO = userService.findByEmail(email);
        if (optionalUserDTO.isPresent()) {
            UserDTO userDTO = optionalUserDTO.get();

            UserUI userUI = UIFactory.createUserUI(userDTO);
            userUI.display();

            LoggerUtil.getInstance().logInfo("User with email '" + email + "' has been successfully logged in.");
        }
    }

    public void navigateToLoginPage() {
        new LoginUI(this);
    }

    public void navigateToRegistrationPage() {
        new RegistrationUI(this);
    }
}
