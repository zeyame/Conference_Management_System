package controller;

import dto.RegistrationDTO;
import service.AuthenticationService;
import service.UserService;
import ui.LoginUI;
import ui.RegistrationUI;

import java.util.EnumMap;

public class MainController {
    private final AuthenticationService authService;
    private final UserService userService;

    public MainController(AuthenticationService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    public void registerUser(RegistrationDTO registrationDTO) {
        RegistrationDTO validatedDTO = authService.validateRegistration(registrationDTO);
        userService.registerUser(validatedDTO);
        // send welcome email
    }
    public void navigateToLoginPage() {
        new LoginUI(this);
    }

    public void navigateToRegistrationPage() {
        new RegistrationUI(this);
    }
}
