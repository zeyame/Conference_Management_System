import controller.MainController;
import repository.UserRepository;
import service.UserService;
import view.LoginUI;
import util.email.EmailService;

public class Main {
    public static void main(String[] args) {
        new LoginUI(new MainController(new UserService(new UserRepository()), EmailService.getInstance()));
    }
}


