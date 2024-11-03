import controller.MainController;
import ui.LoginUI;
import ui.RegistrationUI;

public class Main {
    public static void main(String[] args) {
        new LoginUI(new MainController());
    }
}
