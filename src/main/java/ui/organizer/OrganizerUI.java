package ui.organizer;

import controller.OrganizerController;
import dto.ConferenceDTO;
import dto.ConferenceFormDTO;
import dto.UserDTO;
import exception.ConferenceCreationException;
import ui.UserUI;
import ui.organizer.pages.AddConferencePage;
import ui.organizer.pages.HomePage;
import util.LoggerUtil;
import util.UIComponentFactory;

import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class OrganizerUI extends JFrame implements UserUI, OrganizerObserver {
    private final OrganizerController organizerController;
    private final UserDTO userDTO;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    private final String HOME_PAGE = "Home Page";
    private final String ADD_CONFERENCE_PAGE = "Add Conference Page";

    // map used for quick retrieval of organizer subpages need for routing
    Map<String, Component> subpages = new HashMap<>();

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

        initializePages();
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
        // check if the "Add Conference Page" is already in the subpages map
        if (!subpages.containsKey("Add Conference Page")) {
            AddConferencePage addConferencePage = new AddConferencePage(userDTO, this);
            subpages.put("Add Conference Page", addConferencePage.createPageContent());
            contentPanel.add(addConferencePage.createPageContent(), "Add Conference Page");
        }

        // navigate to the add conference page
        cardLayout.show(contentPanel, "Add Conference Page");
    }


    @Override
    public void onSubmitConferenceFormRequest(ConferenceFormDTO conferenceFormDTO) {
        // call organizer controller to valid conference (check name and date availability)
        try {
            LoggerUtil.getInstance().logInfo("Request to create conference received. Proceeding with validation.");
            organizerController.validateConferenceData(conferenceFormDTO);
            organizerController.createConference(conferenceFormDTO);
        } catch (ConferenceCreationException e) {
            JOptionPane.showMessageDialog(
                    subpages.get(ADD_CONFERENCE_PAGE),
                    e.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void initializePages() {
        // initialize home page
        HomePage homePage = new HomePage(userDTO, this);
        JPanel homeContent = homePage.createPageContent();
        subpages.put(HOME_PAGE, homeContent);
        contentPanel.add(homeContent, HOME_PAGE);

        // initialize add conference page
        AddConferencePage addConferencePage = new AddConferencePage(userDTO, this);
        JPanel addConferenceContent = addConferencePage.createPageContent();
        subpages.put(ADD_CONFERENCE_PAGE, addConferenceContent);
        contentPanel.add(addConferenceContent, ADD_CONFERENCE_PAGE);

        // show home page by default
        cardLayout.show(contentPanel, HOME_PAGE);
    }


}
