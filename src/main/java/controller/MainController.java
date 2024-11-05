package controller;

import dto.RegistrationDTO;
import service.AuthenticationService;
import ui.LoginUI;
import ui.RegistrationUI;

public class MainController {
    private final AuthenticationService authService;

    public MainController(AuthenticationService authService) {
        this.authService = authService;
    }

    public void registerUser(RegistrationDTO registrationDTO) {
        authService.validateRegistration(registrationDTO);
    }
    public void navigateToLoginPage() {
        new LoginUI(this);
    }

    public void navigateToRegistrationPage() {
        new RegistrationUI(this);
    }
}
