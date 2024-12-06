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
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(String.format("Your personal schedule for '%s'", conferenceDTO.getName()), this::handleBackButton, 300);
        add(headerPanel, BorderLayout.NORTH);

        // split panel for registered and unregistered sessions
        JSplitPane splitPane = UIComponentFactory.createSplitPane(
                "Upcoming",
                "Past",
                upcomingSessions.values().stream().toList(),
                pastSessions.values().stream().toList(),
                "View Session",
                this::handleViewSessionButton
        );
        splitPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(splitPane, BorderLayout.CENTER);
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

    private void filterPastSessions(List<SessionDTO> sessionDTOS) {
        sessionDTOS.stream()
                .filter(sessionDTO -> !isUpcoming(sessionDTO))
                .forEach(sessionDTO -> pastSessions.put(sessionDTO.getId(), sessionDTO));
    }

    private boolean isUpcoming(SessionDTO sessionDTO) {
        return LocalDateTime.of(sessionDTO.getDate(), sessionDTO.getStartTime()).isAfter(LocalDateTime.now());
    }

    // button handlers
    private void handleViewSessionButton(ActionEvent e) {

    }

    private void handleBackButton(ActionEvent e) {
        ViewConferencePage viewConferencePage = new ViewRegisteredConferencePage(attendee, eventMediator, navigator, conferenceId);
        navigator.navigateTo(viewConferencePage);
    }
}
