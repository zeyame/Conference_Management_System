package view.organizer;

import controller.OrganizerController;
import dto.ConferenceDTO;
import dto.SessionDTO;
import dto.UserDTO;
import response.ResponseEntity;
import view.UserUI;
import view.organizer.pages.*;
import util.LoggerUtil;
import util.ui.UIComponentFactory;
import view.organizer.pages.add.AddConferencePage;
import view.organizer.pages.add.AddPage;
import view.organizer.pages.add.AddSessionPage;
import view.organizer.pages.manage.ManageConferencePage;
import view.organizer.pages.manage.ManagePage;
import view.organizer.pages.manage.ManageSessionPage;
import view.organizer.pages.view.ViewAttendeesPage;
import view.organizer.pages.view.ViewListPage;
import view.organizer.pages.view.ViewSessionsPage;

import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.List;

public class OrganizerUI extends JFrame implements UserUI, OrganizerObserver {
    private final OrganizerController organizerController;
    private final UserDTO userDTO;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final Deque<String> navigationStack;

    // constants for subpage names
    private final String HOME_PAGE = "Home Page";
    private final String MANAGE_CONFERENCE_PAGE = "Manage Conference Page";
    private final String MANAGE_SESSION_PAGE = "Manage Session Page";
    private final String ADD_CONFERENCE_PAGE = "Add Conference Page";
    private final String ADD_SESSION_PAGE = "Add Session Page";
    private final String VIEW_ATTENDEES_PAGE = "View Attendees Page";
    private final String VIEW_SESSIONS_PAGE = "View Sessions Page";

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

        // initialize the navigation stack
        navigationStack = new ArrayDeque<>();

        initializeHomePage();
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
            showError(getCurrentPageId(), managedConferencesResponse.getErrorMessage());
            return Collections.emptyList();
        }

        return managedConferencesResponse.getData();
    }

    @Override
    public void onManageConferenceRequest(String conferenceId) {
        LoggerUtil.getInstance().logInfo("Request to manage a conference with id '" + conferenceId + "' received.");

        ResponseEntity<ConferenceDTO> managedConferenceResponse = organizerController.getManagedConference(conferenceId);
        if (!managedConferenceResponse.isSuccess()) {
            showError(getCurrentPageId(), managedConferenceResponse.getErrorMessage());
            return;
        }

        // navigating from the "Home Page" to the "Manage Conference Page" of the requested conference
        ConferenceDTO conferenceDTO = managedConferenceResponse.getData();
        ManagePage manageConferencePage = new ManageConferencePage(this, conferenceDTO, userDTO);
        navigateTo(MANAGE_CONFERENCE_PAGE, manageConferencePage.createPageContent());
    }

    @Override
    public void onManageSessionRequest(String sessionId) {
        LoggerUtil.getInstance().logInfo("Request to manage session with id '" + sessionId + "' received.");

        ResponseEntity<SessionDTO> sessionResponse = organizerController.getSessionDetails(sessionId);
        if (!sessionResponse.isSuccess()) {
            showError(getCurrentPageId(), sessionResponse.getErrorMessage());
            return;
        }

        // navigating from the "View Sessions Page" to the "Manage Session Page" of the requested session
        SessionDTO sessionDTO = sessionResponse.getData();
        ManagePage manageSessionPage = new ManageSessionPage(this, sessionDTO);
        navigateTo(MANAGE_SESSION_PAGE, manageSessionPage.createPageContent());
    }


    @Override
    public void onAddConferenceRequest(String organizerName) {
        LoggerUtil.getInstance().logInfo("Request to add a new conference received from organizer '" + organizerName + "'.");

        //  navigate to a new Add Conference Page
        AddPage addConferencePage = new AddConferencePage(this, userDTO);
        navigateTo(ADD_CONFERENCE_PAGE, addConferencePage.createPageContent());
    }

    @Override
    public void onAddSessionRequest(String conferenceId, String conferenceName) {
        LoggerUtil.getInstance().logInfo("Request to add a new session to conference '" + conferenceId + "' received.");

        // get registered speakers that can be assigned to new session
        ResponseEntity<List<UserDTO>> speakersResponse = organizerController.getRegisteredSpeakers();
        if (!speakersResponse.isSuccess()) {
            showError(getCurrentPageId(), speakersResponse.getErrorMessage());
            return;
        }

        List<UserDTO> speakers = speakersResponse.getData();
        // navigate to a new Add Session Page
        AddPage addSessionPage = new AddSessionPage(this, conferenceId, conferenceName, speakers);
        navigateTo(ADD_SESSION_PAGE, addSessionPage.createPageContent());
    }


    @Override
    public void onSubmitConferenceFormRequest(ConferenceDTO conferenceDTO) {
        LoggerUtil.getInstance().logInfo("Request to create conference received. Proceeding with validation.");

        ResponseEntity<Void> validationResponse = organizerController.validateConferenceData(conferenceDTO);
        if (!validationResponse.isSuccess()) {
            showError(ADD_CONFERENCE_PAGE, validationResponse.getErrorMessage());
            return;
        }

        ResponseEntity<Void> createConferenceResponse = organizerController.createConference(conferenceDTO);
        if (!createConferenceResponse.isSuccess()) {
            showError(ADD_CONFERENCE_PAGE, createConferenceResponse.getErrorMessage());
            return;
        }

        // navigate back to an updated home page with the new conference added
        HomePage homePage = new HomePage(userDTO, this);
        navigateTo(HOME_PAGE, homePage.createPageContent());

        showSuccess(HOME_PAGE, "The '" + conferenceDTO.getName() + "' conference has successfully been added to your managed conferences.");
    }

    @Override
    public void onSubmitSessionFormRequest(SessionDTO sessionDTO, String conferenceName) {
        LoggerUtil.getInstance().logInfo("Request to create session '" + sessionDTO.getName() + "' received.");

        ResponseEntity<Void> createSessionResponse = organizerController.createSession(sessionDTO);
        if (!createSessionResponse.isSuccess()) {
            showError(getCurrentPageId(), createSessionResponse.getErrorMessage());
            return;
        }

        // navigate to an updated view sessions page
        onViewSessionsRequest(sessionDTO.getConferenceId(), conferenceName);

        // display success message
        showSuccess(getCurrentPageId(), String.format("Session '%s' has successfully been added!", sessionDTO.getName()));
    }

    @Override
    public void onEditConferenceRequest() {

    }

    @Override
    public void onDeleteConferenceRequest() {

    }

    @Override
    public void onViewAttendeesRequest(String conferenceId, String conferenceName) {
        LoggerUtil.getInstance().logInfo(String.format("Request to view attendees for conference '%s' received.", conferenceName));

        // get attendees for conference
        ResponseEntity<List<UserDTO>> conferenceAttendeesResponse = organizerController.getConferenceAttendees(conferenceId);
        if (!conferenceAttendeesResponse.isSuccess()) {
            showError(MANAGE_CONFERENCE_PAGE, conferenceAttendeesResponse.getErrorMessage());
            return;
        }

        List<UserDTO> conferenceAttendees = conferenceAttendeesResponse.getData();
        ViewListPage<UserDTO> viewAttendeesPage = new ViewAttendeesPage(this, conferenceName, conferenceAttendees);
        navigateTo(VIEW_ATTENDEES_PAGE, viewAttendeesPage.createPageContent());
    }

    @Override
    public void onViewSessionsRequest(String conferenceId, String conferenceName) {
        LoggerUtil.getInstance().logInfo("Request to view sessions for conference '" + conferenceName + "' received.");

        // get sessions for conference
        ResponseEntity<List<SessionDTO>> sessionsResponse = organizerController.getConferenceSessions(conferenceId);
        if (!sessionsResponse.isSuccess()) {
            showError(MANAGE_CONFERENCE_PAGE, sessionsResponse.getErrorMessage());
            return;
        }

        List<SessionDTO> sessions = sessionsResponse.getData();
        ViewListPage<SessionDTO> viewSessionsPage = new ViewSessionsPage(this, conferenceId, conferenceName, sessions);
        navigateTo(VIEW_SESSIONS_PAGE, viewSessionsPage.createPageContent());
    }

    @Override
    public void onViewSpeakersRequest() {

    }

    @Override
    public void onViewFeedbackRequest() {

    }

    @Override
    public void onNavigateBackRequest() {
        navigateBack();
    }

    @Override
    public void onViewSessionAttendeesRequest(String sessionId, String sessionName) {
        LoggerUtil.getInstance().logInfo(String.format("Request to view registered attendees for session '%s' received.", sessionName));

        ResponseEntity<List<UserDTO>> sessionAttendeesResponse = organizerController.getSessionAttendees(sessionId);
        if (!sessionAttendeesResponse.isSuccess()) {
            showError(getCurrentPageId(), sessionAttendeesResponse.getErrorMessage());
            return;
        }

        List<UserDTO> attendees = sessionAttendeesResponse.getData();
        ViewListPage<UserDTO> viewAttendeesPage = new ViewAttendeesPage(this, sessionName, attendees);
        navigateTo(VIEW_ATTENDEES_PAGE, viewAttendeesPage.createPageContent());
    }

    private void initializeHomePage() {
        // initialize home page
        HomePage homePage = new HomePage(userDTO, this);
        JPanel homeContent = homePage.createPageContent();
        subpages.put(HOME_PAGE, homeContent);
        contentPanel.add(homeContent, HOME_PAGE);

        // show home page by default
        cardLayout.show(contentPanel, HOME_PAGE);
    }

    private void navigateTo(String pageId, Component newPageContent) {
        String currentPage = getCurrentPageId();
        if (currentPage != null) {
            navigationStack.push(currentPage);
        }

        replacePage(pageId, newPageContent);
    }

    private void navigateBack() {
        if (!navigationStack.isEmpty()) {
            String lastPageId = navigationStack.pop();
            cardLayout.show(contentPanel, lastPageId);
        }
    }

    private String getCurrentPageId() {
        for (Map.Entry<String, Component> componentEntry: subpages.entrySet()) {
            if (componentEntry.getValue().isVisible()) {
                return componentEntry.getKey();
            }
        }
        return null;
    }

    private void replacePage(String pageId, Component newPageContent) {
        if (subpages.containsKey(pageId)) {
            contentPanel.remove(subpages.get(pageId));
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
