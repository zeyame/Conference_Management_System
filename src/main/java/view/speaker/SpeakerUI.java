package view.speaker;

import controller.AttendeeController;
import controller.MainController;
import controller.SpeakerController;
import dto.UserDTO;
import repository.UserRepository;
import service.UserService;
import util.email.EmailService;
import util.ui.UIComponentFactory;
import view.navigation.NavigationManager;
import view.event.UIEventMediator;
import view.UserUI;
import view.observers.AttendeeSessionManager;
import view.observers.SessionEventObserver;
import view.authentication.LoginUI;
import view.navigation.Navigator;
import view.speaker.pages.HomePage;

import javax.swing.*;
import java.awt.*;

public class SpeakerUI extends JFrame implements UserUI, Navigator {

    private final SpeakerController speakerController;
    private final AttendeeController attendeeController;
    private final UserDTO userDTO;
    private final UIEventMediator eventMediator;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final NavigationManager navigationManager;

    public SpeakerUI(SpeakerController speakerController, AttendeeController attendeeController, UserDTO userDTO) {
        this.speakerController = speakerController;
        this.attendeeController = attendeeController;
        this.userDTO = userDTO;
        this.eventMediator = new UIEventMediator();
        this.contentPanel = new JPanel(new CardLayout());
        this.cardLayout = new CardLayout();
        this.navigationManager = new NavigationManager(contentPanel);

        // frame configuration
        setSize(new Dimension(1400, 800));
        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // centering the UI
        setLocationRelativeTo(null);

        // welcome message
        JPanel welcomePanel = UIComponentFactory.createWelcomePanel(userDTO.getName());
        add(welcomePanel, BorderLayout.NORTH);

        // hiding the welcome message after three seconds
        new Timer(3000, e -> welcomePanel.setVisible(false)).start();

        // add main content panel of the frame
        add(contentPanel, BorderLayout.CENTER);

        // register observers in event mediator
        registerObservers();

        // initialize home page
        initializeHomePage();
    }

    // USER UI INTERFACE
    @Override
    public void display() {
        setVisible(true);
        toFront();
    }


    // NAVIGATOR INTERFACE
    @Override
    public void navigateTo(JPanel page) {
        navigationManager.navigateTo(() -> page);
    }

    @Override
    public void navigateTo(JPanel page, boolean addToStack) {
        navigationManager.navigateTo(() -> page, addToStack);
    }

    @Override
    public void navigateBack() {
        navigationManager.navigateBack();
    }

    @Override
    public boolean canNavigateBack() {
        return navigationManager.canNavigateBack();
    }

    @Override
    public void logout() {
        new LoginUI(new MainController(new UserService(UserRepository.getInstance()), EmailService.getInstance()));
        this.dispose();
    }

    private void registerObservers() {
        eventMediator.registerObserver(
                SessionEventObserver.class,
                new AttendeeSessionManager(attendeeController, speakerController)
        );
    }

    private void initializeHomePage() {
        HomePage homePage = new HomePage(userDTO, eventMediator, this);
        navigationManager.navigateTo(() -> homePage);
    }
}
