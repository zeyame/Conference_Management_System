package controller;

import ui.LoginUI;
import ui.RegistrationUI;

public class MainController {

    public void navigateToLogin() {
        new LoginUI(this);
    }

    public void navigateToRegister() {
        new RegistrationUI(this);
    }
}
