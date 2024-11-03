package controller;

import ui.LoginUI;
import ui.RegistrationUI;

public class MainController {

    public void navigateToLoginPage() {
        new LoginUI(this);
    }

    public void navigateToRegistrationPage() {
        new RegistrationUI(this);
    }
}
