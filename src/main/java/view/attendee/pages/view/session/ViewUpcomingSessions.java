package view.attendee.pages.view.session;

import dto.ConferenceDTO;
import dto.SessionDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.observers.ConferenceEventObserver;
import view.attendee.observers.SessionEventObserver;
import view.attendee.pages.view.conference.ViewRegisteredConferencePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewUpcomingSessions extends JPanel {
    private final UserDTO attendee;
    private final String conferenceId;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;
    private ConferenceDTO conferenceDTO;
    private List<SessionDTO> upcomingSessions;

    public ViewUpcomingSessions(UserDTO attendee, String conferenceId, UIEventMediator eventMediator, Navigator navigator) {
        this.attendee = attendee;
        this.conferenceId = conferenceId;
        this.eventMediator = eventMediator;
        this.navigator = navigator;

        setLayout(new BorderLayout());

        fetchSessions();
        fetchConference();

        createPageContent();
    }

    private void createPageContent() {
        // header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(String.format("Upcoming Sessions in '%s'", conferenceDTO.getName()), this::handleBackButton, 380);
        add(headerPanel, BorderLayout.NORTH);

        if (!upcomingSessions.isEmpty()) {
            // split panel for registered and unregistered sessions
            JScrollPane scrollPane = UIComponentFactory.createSessionsScrollPane(upcomingSessions, "View Session", this::handleViewSessionButton);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            add(scrollPane, BorderLayout.CENTER);
        } else {
            JPanel emptyStatePanel = UIComponentFactory.createEmptyStatePanel("There are currently no upcoming sessions open for " +
                    "registration in this conference. Please check back later for new updates!");
            add(emptyStatePanel, BorderLayout.CENTER);
        }
    }

    // data fetchers
    private void fetchSessions() {
        eventMediator.publishEvent(
                SessionEventObserver.class,
                observer -> observer.onGetUpcomingSessionsForConference(conferenceId, this::onSessionsFetched)
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
        this.upcomingSessions = filterUnregisteredSessions(sessionDTOs);
    }

    private void onConferenceFetched(ConferenceDTO conferenceDTO, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.conferenceDTO = conferenceDTO;
    }

    // Joption Pane helpers
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // button handlers
    private void handleBackButton(ActionEvent e) {
        ViewRegisteredConferencePage viewRegisteredConferencePage = new ViewRegisteredConferencePage(attendee, eventMediator, navigator, conferenceId);
        navigator.navigateTo(viewRegisteredConferencePage);
    }

    private void handleViewSessionButton(ActionEvent e) {
        // handle viewing a session
        JButton sourceButton = (JButton) e.getSource();
        String sessionId = (String) sourceButton.getClientProperty("sessionId");

        ViewSessionPage viewUnregisteredSessionPage = new ViewUnregisteredSessionPage(attendee, sessionId, eventMediator, navigator);
        navigator.navigateTo(viewUnregisteredSessionPage);
    }

    // filters
    private List<SessionDTO> filterUnregisteredSessions(List<SessionDTO> sessions) {
        return sessions.stream()
            .filter(sessionDTO -> !sessionDTO.getRegisteredAttendees().contains(attendee.getId()))
            .collect(Collectors.toList());
    }

}
