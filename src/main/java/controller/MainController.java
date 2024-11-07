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

    public boolean validateRegistration(RegistrationDTO registrationDTO) {
        return authService.validateRegistration(registrationDTO);
    }

    public void registerUser(RegistrationDTO validatedDTO) {
        userService.registerUser(validatedDTO);
        emailService.sendWelcomeEmail(validatedDTO.getEmail(), validatedDTO.getName());
    }

    public void loginUser(String email, char[] password) {
        boolean isLoginValid = authService.validateLogin(email, password);
    }
    public void navigateToLoginPage() {
        new LoginUI(this);
    }

    public void navigateToRegistrationPage() {
        new RegistrationUI(this);
    }
}
