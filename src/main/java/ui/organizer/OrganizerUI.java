package ui.organizer;

import controller.OrganizerController;
import dto.ConferenceDTO;
import dto.ConferenceFormDTO;
import dto.UserDTO;
import exception.SavingDataException;
import response.ResponseEntity;
import ui.UserUI;
import ui.organizer.pages.AddConferencePage;
import ui.organizer.pages.HomePage;
import ui.organizer.pages.ManageConferencePage;
import util.LoggerUtil;
import util.UIComponentFactory;

import java.util.Collections;
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

    // constants for subpage names
    private final String HOME_PAGE = "Home Page";
    private final String ADD_CONFERENCE_PAGE = "Add Conference Page";
    private final String MANAGE_CONFERENCE_PAGE = "Manage Conference Page";

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
        LoggerUtil.getInstance().logInfo("Request to get managed conferences for user with email '" + email + "' received.");

        ResponseEntity<List<ConferenceDTO>> managedConferencesResponse = organizerController.getManagedConferences(email);
        if (!managedConferencesResponse.isSuccess()) {
            showError(HOME_PAGE, managedConferencesResponse.getErrorMessage());
            return Collections.emptyList();
        }

        return managedConferencesResponse.getData();
    }

    @Override
    public void onManageConferenceRequest(String conferenceId) {
        LoggerUtil.getInstance().logInfo("Request to manage a conference with id '" + conferenceId + "' received.");

        ResponseEntity<ConferenceDTO> managedConferenceResponse = organizerController.getManagedConference(conferenceId);
        if (!managedConferenceResponse.isSuccess()) {
            showError(HOME_PAGE, managedConferenceResponse.getErrorMessage());
            return;
        }

        // navigating from the "Home Page" to the "Manage Conference Page" of the requested conference
        ConferenceDTO conferenceDTO = managedConferenceResponse.getData();
        ManageConferencePage manageConferencePage = new ManageConferencePage(conferenceDTO, userDTO, this);
        replacePage(MANAGE_CONFERENCE_PAGE, manageConferencePage.createPageContent());
    }


    @Override
    public void onAddConferenceRequest() {
        LoggerUtil.getInstance().logInfo("Request to add a new conference received.");

        // add and navigate to the new Add Conference Page
        AddConferencePage addConferencePage = new AddConferencePage(userDTO, this);
        replacePage(ADD_CONFERENCE_PAGE, addConferencePage.createPageContent());
    }


    @Override
    public void onSubmitConferenceFormRequest(ConferenceFormDTO conferenceFormDTO) {
        LoggerUtil.getInstance().logInfo("Request to create conference received. Proceeding with validation.");

        ResponseEntity<Void> validationResponse = organizerController.validateConferenceData(conferenceFormDTO);
        if (!validationResponse.isSuccess()) {
            showError(ADD_CONFERENCE_PAGE, validationResponse.getErrorMessage());
            return;
        }

        ResponseEntity<Void> createConferenceResponse = organizerController.createConference(conferenceFormDTO);
        if (!createConferenceResponse.isSuccess()) {
            showError(ADD_CONFERENCE_PAGE, createConferenceResponse.getErrorMessage());
            return;
        }

        // navigate back to an updated home page with the new conference added
        HomePage homePage = new HomePage(userDTO, this);
        replacePage(HOME_PAGE, homePage.createPageContent());
        showSuccess(HOME_PAGE, "The '" + conferenceFormDTO.getName() + "' conference has successfully been added to your managed conferences.");
    }

    @Override
    public void onEditConferenceRequest() {

    }

    @Override
    public void onDeleteConferenceRequest() {

    }

    @Override
    public void onViewAttendeesRequest() {

    }

    @Override
    public void onViewSessionsRequest() {

    }

    @Override
    public void onViewSpeakersRequest() {

    }

    @Override
    public void onViewFeedbackRequest() {

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

    private void replacePage(String pageId, Component newPageContent) {
        if (subpages.containsKey(pageId)) {
            contentPanel.remove(subpages.get(pageId));
            subpages.remove(pageId);
        }
        subpages.put(pageId, newPageContent);
        contentPanel.add(newPageContent, pageId);
        cardLayout.show(contentPanel, pageId);
    }

    private void showSuccess(String pageId, String message) {
        JOptionPane.showMessageDialog(
                subpages.get(pageId),
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String pageId, String message) {
        JOptionPane.showMessageDialog(
                subpages.get(pageId),
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

}
