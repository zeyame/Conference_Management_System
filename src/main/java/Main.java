import controller.MainController;
import repository.UserFileRepository;
import service.UserService;
import view.LoginUI;
import util.EmailService;

public class Main {
    public static void main(String[] args) {
        new LoginUI(new MainController(new UserService(new UserFileRepository()), EmailService.getInstance()));
    }
}


