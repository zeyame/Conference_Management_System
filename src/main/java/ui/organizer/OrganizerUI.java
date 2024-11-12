package ui.organizer;

import controller.OrganizerController;
import dto.ConferenceDTO;
import dto.UserDTO;
import ui.UserUI;
import util.UIComponentFactory;
import java.util.List;
import javax.swing.*;
import java.awt.*;

public class OrganizerUI extends JFrame implements UserUI, OrganizerCallback {
    private final OrganizerController organizerController;
    private final UserDTO userDTO;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    public OrganizerUI(OrganizerController organizerController, UserDTO userDTO) {
        this.organizerController = organizerController;
        this.userDTO = userDTO;

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

        // main content panel of the frame
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel, BorderLayout.CENTER);

        // add home page to the card layout
        HomePage homePage = new HomePage(userDTO, this);
        contentPanel.add(homePage.createPageContent(), "Home Page");
        cardLayout.show(contentPanel, "Home Page");
    }

    @Override
    public void display() {
        setVisible(true);
        toFront();
    }

    @Override
    public List<ConferenceDTO> onGetManagedConferencesRequest(String email) {
        return organizerController.getManagedConferences(email);
    }

    @Override
    public void onAddConferenceRequest() {
        AddConferencePage addConferencePage = new AddConferencePage(userDTO, this);
        contentPanel.add(addConferencePage.createPageContent(), "Add Conference Page");

        // navigate to the add conference page
        cardLayout.show(contentPanel, "Add Conference Page");
    }

}
