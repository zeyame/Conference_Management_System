package view.organizer;

import controller.OrganizerController;
import dto.ConferenceDTO;
import dto.FeedbackDTO;
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
import view.organizer.pages.add.EditSessionPage;
import view.organizer.pages.manage.ManageConferencePage;
import view.organizer.pages.manage.ManagePage;
import view.organizer.pages.manage.ManageSessionPage;
import view.organizer.pages.view.*;

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
    private final String EDIT_SESSION_PAGE = "Edit Session Page";
    private final String VIEW_ATTENDEES_PAGE = "View Attendees Page";
    private final String VIEW_SESSIONS_PAGE = "View Sessions Page";
    private final String VIEW_SESSION_ATTENDANCE_PAGE = "View Session Attendance Page";
    private final String VIEW_SESSION_FEEDBACK_PAGE = "View Session Feedback Page";

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

        // fetching conference data
        ConferenceDTO conferenceDTO = fetchConference(conferenceId);

        // navigating from the "Home Page" to the "Manage Conference Page" of the requested conference
        ManagePage manageConferencePage = new ManageConferencePage(this, conferenceDTO, userDTO);
        navigateTo(MANAGE_CONFERENCE_PAGE, manageConferencePage.createPageContent(), true);
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
        navigateTo(MANAGE_SESSION_PAGE, manageSessionPage.createPageContent(), true);
    }


    @Override
    public void onAddConferenceRequest(String organizerName) {
        LoggerUtil.getInstance().logInfo("Request to add a new conference received from organizer '" + organizerName + "'.");

        //  navigate to a new Add Conference Page
        AddPage addConferencePage = new AddConferencePage(this, userDTO);
        navigateTo(ADD_CONFERENCE_PAGE, addConferencePage.createPageContent(), true);
    }

    @Override
    public void onAddSessionRequest(String conferenceId) {
        LoggerUtil.getInstance().logInfo(String.format("Request to add a new session to conference with id '%s' received.", conferenceId));

        // fetch conference data
        ConferenceDTO conferenceDTO = fetchConference(conferenceId);
        if (conferenceDTO == null) {
            return;
        }

        // get registered speakers that can be assigned to new session
        List<UserDTO> speakers = fetchSpeakers();

        // navigate to a new Add Session Page
        AddPage addSessionPage = new AddSessionPage(this, conferenceId, conferenceDTO.getName(), speakers);
        navigateTo(ADD_SESSION_PAGE, addSessionPage.createPageContent(), true);
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
        navigateTo(HOME_PAGE, homePage.createPageContent(), true);

        showSuccess(HOME_PAGE, "The '" + conferenceDTO.getName() + "' conference has successfully been added to your managed conferences.");
    }

    @Override
    public void onSubmitSessionFormRequest(SessionDTO sessionDTO) {
        LoggerUtil.getInstance().logInfo("Request to create session '" + sessionDTO.getName() + "' received.");

        ResponseEntity<Void> createSessionResponse = organizerController.createSession(sessionDTO);
        if (!createSessionResponse.isSuccess()) {
            showError(getCurrentPageId(), createSessionResponse.getErrorMessage());
            return;
        }

        // fetch conference data
        ConferenceDTO conference = fetchConference(sessionDTO.getConferenceId());
        if (conference == null) {
            return;
        }

        // get sessions for conference
        List<SessionDTO> sessions = fetchSessions(conference.getId());

        ViewListPage<SessionDTO> viewSessionsPage = new ViewSessionsPage(this, conference.getId(), conference.getName(), sessions);
        navigateTo(VIEW_SESSIONS_PAGE, viewSessionsPage.createPageContent(), false);

        // display success message
        showSuccess(getCurrentPageId(), String.format("Session '%s' has successfully been created!", sessionDTO.getName()));
    }

    @Override
    public void onEditConferenceRequest() {

    }

    @Override
    public void onEditSessionRequest(SessionDTO sessionDTO) {
        LoggerUtil.getInstance().logInfo(String.format("Request to edit session '%s' received,", sessionDTO.getName()));

        List<UserDTO> speakers = fetchSpeakers();

        AddPage editSessionPage = new EditSessionPage(this, sessionDTO.getConferenceId(), speakers, sessionDTO);
        navigateTo(EDIT_SESSION_PAGE, editSessionPage.createPageContent(), true);
    }

    @Override
    public void onUpdateSessionFormRequest(SessionDTO updatedSessionDTO) {
        LoggerUtil.getInstance().logInfo(String.format("Request to updated session '%s' received.", updatedSessionDTO.getName()));

        ResponseEntity<Void> updateSessionResponse = organizerController.updateSession(updatedSessionDTO);
        if (!updateSessionResponse.isSuccess()) {
            showError(getCurrentPageId(), updateSessionResponse.getErrorMessage());
            return;
        }

        String conferenceId = updatedSessionDTO.getConferenceId();

        // get conference data
        ConferenceDTO conference = fetchConference(conferenceId);
        if (conference == null) {
            return;
        }

        // get sessions for conference
        List<SessionDTO> sessions = fetchSessions(conferenceId);

        ViewListPage<SessionDTO> viewSessionsPage = new ViewSessionsPage(this, conferenceId, conference.getName(), sessions);
        navigateTo(VIEW_SESSIONS_PAGE, viewSessionsPage.createPageContent(), false);

        // display success message
        showSuccess(getCurrentPageId(), String.format("Session '%s' has successfully been updated!", updatedSessionDTO.getName()));
    }

    @Override
    public void onDeleteConferenceRequest() {

    }

    @Override
    public void onDeleteSessionRequest(String sessionId) {
        LoggerUtil.getInstance().logInfo(String.format("Request to delete session with id '%s' received.", sessionId));

        SessionDTO sessionDTO = fetchSession(sessionId);
        if (sessionDTO == null) {
            return;
        }

        ResponseEntity<Void> deleteSessionResponse = organizerController.deleteSession(sessionId);
        if (!deleteSessionResponse.isSuccess()) {
            showError(getCurrentPageId(), deleteSessionResponse.getErrorMessage());
            return;
        }

        List<SessionDTO> updatedSessions = fetchSessions(sessionDTO.getConferenceId());
        ViewListPage<SessionDTO> viewSessionsPage = new ViewSessionsPage(this, sessionDTO.getConferenceId(), sessionDTO.getName(), updatedSessions);

        navigateTo(VIEW_SESSIONS_PAGE, viewSessionsPage.createPageContent(), false);
        showSuccess(VIEW_SESSIONS_PAGE, String.format("The session '%s' has successfully been deleted.", sessionDTO.getName()));
    }

    @Override
    public void onViewAttendeesRequest(String conferenceId) {
        LoggerUtil.getInstance().logInfo(String.format("Request to view attendees for conference with id '%s' received.", conferenceId));

        // get conference data
        ConferenceDTO conference = fetchConference(conferenceId);
        if (conference == null) {
            return;
        }

        // get attendees for conference
        ResponseEntity<List<UserDTO>> conferenceAttendeesResponse = organizerController.getConferenceAttendees(conferenceId);
        if (!conferenceAttendeesResponse.isSuccess()) {
            showError(MANAGE_CONFERENCE_PAGE, conferenceAttendeesResponse.getErrorMessage());
            return;
        }

        List<UserDTO> conferenceAttendees = conferenceAttendeesResponse.getData();
        ViewListPage<UserDTO> viewAttendeesPage = new ViewAttendeesPage(this, conference.getName(), conferenceAttendees);
        navigateTo(VIEW_ATTENDEES_PAGE, viewAttendeesPage.createPageContent(), true);
    }

    @Override
    public void onViewSessionsRequest(String conferenceId) {
        LoggerUtil.getInstance().logInfo(String.format("Request to view sessions for conference with id '%s' received.", conferenceId));

        // get conference data
        ConferenceDTO conference = fetchConference(conferenceId);
        if (conference == null) {
            return;
        }

        // get conference sessions
        List<SessionDTO> sessions = fetchSessions(conferenceId);

        ViewListPage<SessionDTO> viewSessionsPage = new ViewSessionsPage(this, conferenceId, conference.getName(), sessions);
        navigateTo(VIEW_SESSIONS_PAGE, viewSessionsPage.createPageContent(), true);
    }

    @Override
    public void onViewSpeakersRequest() {

    }

    @Override
    public void onViewConferenceFeedbackRequest(String conferenceId) {

    }

    @Override
    public void onViewSessionFeedbackRequest(String sessionId) {
        LoggerUtil.getInstance().logInfo(String.format("Request to view feedback for session with id '%s' received.", sessionId));

        // get session data
        SessionDTO sessionDTO = fetchSession(sessionId);
        if (sessionDTO == null) {
            return;
        }

        ResponseEntity<List<FeedbackDTO>> feedbackResponse = organizerController.getSessionFeedback(sessionId);
        if (!feedbackResponse.isSuccess()) {
            showError(getCurrentPageId(), feedbackResponse.getErrorMessage());
            return;
        }

        List<FeedbackDTO> feedback = feedbackResponse.getData();
        ViewListPage<FeedbackDTO> viewFeedbackPage = new ViewFeedbackPage(this, sessionDTO.getName(), feedback);
        navigateTo(VIEW_SESSION_FEEDBACK_PAGE, viewFeedbackPage.createPageContent(), true);
    }

    @Override
    public void onNavigateBackRequest() {
        navigateBack();
    }

    @Override
    public void onViewSessionAttendeesRequest(String sessionId) {
        LoggerUtil.getInstance().logInfo(String.format("Request to view registered attendees for session with id '%s' received.", sessionId));

        SessionDTO sessionDTO = fetchSession(sessionId);
        if (sessionDTO == null) {
            return;
        }

        List<UserDTO> attendees = fetchSessionAttendees(sessionId);

        ViewListPage<UserDTO> viewAttendeesPage = new ViewAttendeesPage(this, sessionDTO.getName(), attendees);
        navigateTo(VIEW_ATTENDEES_PAGE, viewAttendeesPage.createPageContent(), true);
    }

    @Override
    public void onViewSessionAttendanceRequest(String sessionId) {
        LoggerUtil.getInstance().logInfo(String.format("Request to view attendance record for session with id '%s' received.", sessionId));

        SessionDTO sessionDTO = fetchSession(sessionId);
        if (sessionDTO == null) {
            return;
        }

        List<UserDTO> registeredAttendees = fetchSessionAttendees(sessionId);
        Set<String> presentAttendees = sessionDTO.getPresentAttendees();
        float attendanceRecord = sessionDTO.getAttendanceRecord();

        ViewListPage<UserDTO> viewSessionAttendancePage = new ViewSessionAttendancePage(this, registeredAttendees, presentAttendees, sessionDTO.getName(), attendanceRecord);
        navigateTo(VIEW_SESSION_ATTENDANCE_PAGE, viewSessionAttendancePage.createPageContent(), true);
    }

    private ConferenceDTO fetchConference(String conferenceId) {
        ResponseEntity<ConferenceDTO> managedConferenceResponse = organizerController.getManagedConference(conferenceId);
        if (!managedConferenceResponse.isSuccess()) {
            showError(getCurrentPageId(), managedConferenceResponse.getErrorMessage());
            return null;
        }
        return managedConferenceResponse.getData();
    }

    private List<SessionDTO> fetchSessions(String conferenceId) {
        ResponseEntity<List<SessionDTO>> sessionsResponse = organizerController.getConferenceSessions(conferenceId);
        if (!sessionsResponse.isSuccess()) {
            showError(getCurrentPageId(), sessionsResponse.getErrorMessage());
            return new ArrayList<>();
        }
        return sessionsResponse.getData();
    }

    private List<UserDTO> fetchSpeakers() {
        ResponseEntity<List<UserDTO>> speakersResponse = organizerController.getRegisteredSpeakers();
        if (!speakersResponse.isSuccess()) {
            showError(getCurrentPageId(), speakersResponse.getErrorMessage());
            return new ArrayList<>();
        }

        return speakersResponse.getData();
    }

    private SessionDTO fetchSession(String sessionId) {

        ResponseEntity<SessionDTO> sessionResponse = organizerController.getSessionDetails(sessionId);
        if (!sessionResponse.isSuccess()) {
            showError(getCurrentPageId(), sessionResponse.getErrorMessage());
            return null;
        }

        return sessionResponse.getData();
    }

    private List<UserDTO> fetchSessionAttendees(String sessionId) {
        ResponseEntity<List<UserDTO>> sessionAttendeesResponse = organizerController.getSessionAttendees(sessionId);
        if (!sessionAttendeesResponse.isSuccess()) {
            showError(getCurrentPageId(), sessionAttendeesResponse.getErrorMessage());
            return new ArrayList<>();
        }

        return sessionAttendeesResponse.getData();
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

    private void navigateTo(String pageId, Component newPageContent, boolean pushToStack) {
        String currentPage = getCurrentPageId();
        if (currentPage != null && pushToStack) {
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
