import controller.MainController;
import domain.factory.UserFactory;
import repository.UserFileRepository;
import service.AuthenticationService;
import service.UserService;
import ui.LoginUI;
import ui.RegistrationUI;

public class Main {
    public static void main(String[] args) {
        new RegistrationUI(new MainController(new AuthenticationService(new UserService(new UserFileRepository()))));
    }
}
