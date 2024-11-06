package controller;

import dto.RegistrationDTO;
import service.AuthenticationService;
import service.UserService;
import ui.LoginUI;
import ui.RegistrationUI;
import util.EmailService;

public class MainController {
    private final AuthenticationService authService;
    private final UserService userService;
    private final EmailService emailService;

    public MainController(AuthenticationService authService, UserService userService, EmailService emailService) {
        this.authService = authService;
        this.userService = userService;
        this.emailService = emailService;
    }

    public void registerUser(RegistrationDTO registrationDTO) {
        RegistrationDTO validatedDTO = authService.validateRegistration(registrationDTO);
        userService.registerUser(validatedDTO);
        emailService.sendWelcomeEmail(registrationDTO.getEmail(), registrationDTO.getName());
    }
    public void navigateToLoginPage() {
        new LoginUI(this);
    }

    public void navigateToRegistrationPage() {
        new RegistrationUI(this);
    }
}
