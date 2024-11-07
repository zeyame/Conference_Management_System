import controller.MainController;
import repository.UserFileRepository;
import service.AuthenticationService;
import service.UserService;
import ui.LoginUI;
import ui.RegistrationUI;
import util.EmailService;

public class Main {
    public static void main(String[] args) {
        new LoginUI(new MainController(new AuthenticationService(new UserService(new UserFileRepository())), new UserService(new UserFileRepository()), EmailService.getInstance()));
    }
}
