package view.attendee;

import controller.AttendeeController;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.NavigationManager;
import view.UserUI;
import view.attendee.observers.AttendeeConferenceManager;
import view.attendee.observers.ConferenceEventObserver;
import view.attendee.pages.HomePage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;

public class AttendeeUI extends JFrame implements UserUI, Navigator {

    private final AttendeeController attendeeController;
    private final UserDTO userDTO;
    private final UIEventMediator eventMediator;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final NavigationManager navigationManager;

    public AttendeeUI(AttendeeController attendeeController, UserDTO userDTO) {
        this.attendeeController = attendeeController;
        this.userDTO = userDTO;
        this.eventMediator = new UIEventMediator();
        this.contentPanel = new JPanel(new CardLayout());
        this.cardLayout = new CardLayout();
        this.navigationManager = new NavigationManager(contentPanel);

        // frame configuration
        setTitle("Organizer Landing Page");
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
        eventMediator.registerObserver(ConferenceEventObserver.class, new AttendeeConferenceManager(attendeeController));

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
        navigationManager.navigateTo(page);
    }

    @Override
    public void navigateBack() {
        navigationManager.navigateBack();
    }

    @Override
    public boolean canNavigateBack() {
        return navigationManager.canNavigateBack();
    }


    private void initializeHomePage() {
        HomePage attendeeHomePage = new HomePage(userDTO, this.eventMediator, this);
        navigationManager.navigateTo(attendeeHomePage);
    }

}
