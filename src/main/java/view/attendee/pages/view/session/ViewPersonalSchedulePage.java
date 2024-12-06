package view.attendee.pages.view.session;

import dto.ConferenceDTO;
import dto.SessionDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.observers.ConferenceEventObserver;
import view.attendee.observers.SessionEventObserver;
import view.attendee.pages.view.conference.ViewConferencePage;
import view.attendee.pages.view.conference.ViewRegisteredConferencePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPersonalSchedulePage extends JPanel {
    // dependencies
    private final UserDTO attendee;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;
    private final String conferenceId;

    // fetched data
    private ConferenceDTO conferenceDTO;
    private final Map<String, SessionDTO> upcomingSessions = new HashMap<>();
    private final Map<String, SessionDTO> ongoingSessions = new HashMap<>();
    private final Map<String, SessionDTO> pastSessions = new HashMap<>();

    public ViewPersonalSchedulePage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator, String conferenceId) {
        this.attendee = attendee;
        this.eventMediator = eventMediator;
        this.navigator = navigator;
        this.conferenceId = conferenceId;

        setLayout(new BorderLayout());

        fetchSessions();
        fetchConference();

        createPageContent();
    }

    private void createPageContent() {
        // header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(String.format("Your personal schedule for '%s'", conferenceDTO.getName()), this::handleBackButton, 350);
        add(headerPanel, BorderLayout.NORTH);

        if (upcomingSessions.isEmpty() && pastSessions.isEmpty()) {
            JPanel emptyStatePanel = UIComponentFactory.createEmptyStatePanel("You haven't added any sessions to your personal schedule for this conference yet." +
                    " Explore the conference's available sessions and start building your schedule!");
            add(emptyStatePanel, BorderLayout.CENTER);
        } else {
            // split panel for upcoming and ongoing sessions
            JSplitPane leftSplitPane = UIComponentFactory.createSplitPane(
                    "Upcoming",
                    "Ongoing",
                    upcomingSessions.values().stream().toList(),
                    ongoingSessions.values().stream().toList(),
                    "View Session",
                    this::handleViewSessionButton
            );
            leftSplitPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0,0));

            // right panel for past sessions
            JPanel pastSessionsScrollPanePanel = UIComponentFactory.createSessionsScrollPanePanel(
                    "Past",
                    pastSessions.values().stream().toList(),
                    "View Session",
                    this::handleViewSessionButton
            );
            Dimension equalSize = new Dimension(400, 0);
            pastSessionsScrollPanePanel.setPreferredSize(equalSize);
            pastSessionsScrollPanePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

            JSplitPane splitPane = new JSplitPane(
                    JSplitPane.HORIZONTAL_SPLIT,
                    leftSplitPane,
                    pastSessionsScrollPanePanel
            );

            splitPane.setResizeWeight(0.5);
            splitPane.setDividerSize(5);
            splitPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            add(splitPane, BorderLayout.CENTER);
        }
    }

    // data fetches
    private void fetchSessions() {
        eventMediator.publishEvent(
                SessionEventObserver.class,
                observer -> observer.onGetPersonalSchedule(attendee.getId(), conferenceId, this::onSessionsFetched)
        );
    }

    private void fetchConference() {
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onConferenceSelected(conferenceId, this::onConferenceFetched)
        );
    }


    // callback responders
    private void onSessionsFetched(List<SessionDTO> sessionDTOs, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }

        filterUpcomingSessions(sessionDTOs);
        filterOngoingSessions(sessionDTOs);
        filterPastSessions(sessionDTOs);
    }

    private void onConferenceFetched(ConferenceDTO conferenceDTO, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.conferenceDTO = conferenceDTO;
    }


    // JOPTION PANE HELPER
    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // filtering helpers
    private void filterUpcomingSessions(List<SessionDTO> sessionDTOS) {
        sessionDTOS.stream()
                .filter(this::isUpcoming)
                .forEach(sessionDTO -> upcomingSessions.put(sessionDTO.getId(), sessionDTO));
    }

    private void filterOngoingSessions(List<SessionDTO> sessionDTOS) {
        sessionDTOS.stream()
                .filter(sessionDTO -> !isUpcoming(sessionDTO) && !isPast(sessionDTO))
                .forEach(sessionDTO -> ongoingSessions.put(sessionDTO.getId(), sessionDTO));
    }

    private void filterPastSessions(List<SessionDTO> sessionDTOS) {
        sessionDTOS.stream()
                .filter(this::isPast)
                .forEach(sessionDTO -> pastSessions.put(sessionDTO.getId(), sessionDTO));
    }

    private boolean isUpcoming(SessionDTO sessionDTO) {
        return LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime()).isAfter(LocalDateTime.now());
    }

    private boolean isPast(SessionDTO sessionDTO) {
        return LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getEndTime()).isBefore(LocalDateTime.now());
    }

    // button handlers
    private void handleViewSessionButton(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        String sessionId = (String) sourceButton.getClientProperty("sessionId");

        if (upcomingSessions.containsKey(sessionId)) {
            ViewSessionPage viewSessionPage = new ViewUpcomingRegisteredSessionPage(attendee, sessionId, eventMediator, navigator);
            navigator.navigateTo(viewSessionPage);
        } else if (pastSessions.containsKey(sessionId)) {
            ViewSessionPage viewSessionPage = new ViewPastRegisteredSessionPage(attendee, sessionId, eventMediator, navigator);
            navigator.navigateTo(viewSessionPage);
        } else {
            ViewSessionPage viewSessionPage = new ViewOngoingRegisteredSessionPage(attendee, sessionId, eventMediator, navigator);
            navigator.navigateTo(viewSessionPage);
        }
    }

    private void handleBackButton(ActionEvent e) {
        ViewConferencePage viewConferencePage = new ViewRegisteredConferencePage(attendee, eventMediator, navigator, conferenceId);
        navigator.navigateTo(viewConferencePage);
    }
}
