package view.attendee.pages.view.session;

import dto.ConferenceDTO;
import dto.SessionDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.observers.SessionEventObserver;
import view.attendee.pages.view.conference.ViewRegisteredConferencePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewSessionsPage extends JPanel {
    private final UserDTO attendee;
    private final ConferenceDTO conference;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;
    private Map<String, SessionDTO> registeredSessions;
    private Map<String, SessionDTO> unregisteredSessions;

    public ViewSessionsPage(UserDTO attendee, ConferenceDTO conference, UIEventMediator eventMediator, Navigator navigator) {
        this.attendee = attendee;
        this.conference = conference;
        this.eventMediator = eventMediator;
        this.navigator = navigator;

        fetchSessions();

        createPageContent();
    }

    private void createPageContent() {
        setLayout(new BorderLayout());

        // header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(String.format("Sessions in '%s'", conference.getName()), this::handleBackButton, 400);
        add(headerPanel, BorderLayout.NORTH);

        // split panel for registered and unregistered sessions
        JSplitPane splitPane = createSplitPane();
        splitPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(splitPane, BorderLayout.CENTER);
    }

    private JSplitPane createSplitPane() {
        // create panels for ongoing and upcoming conferences
        JPanel registeredPanel = createSessionPanel("Registered", registeredSessions.values().stream().toList());
        JPanel unregisteredPanel = createSessionPanel("Unregistered", unregisteredSessions.values().stream().toList());

        Dimension equalSize = new Dimension(400, 0);
        registeredPanel.setPreferredSize(equalSize);
        unregisteredPanel.setPreferredSize(equalSize);

        // create a split pane with the two panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, registeredPanel, unregisteredPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);
        return splitPane;
    }

    private JPanel createSessionPanel(String title, List<SessionDTO> sessionDTOs) {
        JPanel sessionsPanel = new JPanel();
        sessionsPanel.setLayout(new BorderLayout());

        // header label
        JLabel headerLabel = new JLabel(title, SwingConstants.CENTER);
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        sessionsPanel.add(headerLabel, BorderLayout.NORTH);

        // Scrollable list of conferences
        JScrollPane scrollPane = createSessionsScrollPane(sessionDTOs);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        sessionsPanel.add(scrollPane, BorderLayout.CENTER);

        return sessionsPanel;
    }

    private JScrollPane createSessionsScrollPane(List<SessionDTO> sessionDTOs) {
        JPanel sessionsPanel = new JPanel();
        sessionsPanel.setLayout(new BoxLayout(sessionsPanel, BoxLayout.Y_AXIS));
        sessionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 0));

        for (SessionDTO sessionDTO : sessionDTOs) {
            sessionsPanel.add(UIComponentFactory.createSessionPanel(sessionDTO, this::handleViewSessionButton, "View Session"));
            sessionsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JScrollPane scrollPane = new JScrollPane(sessionsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);
        return scrollPane;
    }


    // data fetchers
    private void fetchSessions() {
        eventMediator.publishEvent(
                SessionEventObserver.class,
                observer -> observer.onGetSessionsForConference(conference.getId(), this::onSessionsFetched)
        );
    }

    // callback responders
    private void onSessionsFetched(List<SessionDTO> sessionDTOs, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.registeredSessions = filerRegisteredSessions(sessionDTOs);
        this.unregisteredSessions = filterUnregisteredSessions(sessionDTOs);
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
        ViewRegisteredConferencePage viewRegisteredConferencePage = new ViewRegisteredConferencePage(attendee, eventMediator, navigator, conference.getId());
        navigator.navigateTo(viewRegisteredConferencePage);
    }

    private void handleViewSessionButton(ActionEvent e) {
        // handle viewing a session
        JButton sourceButton = (JButton) e.getSource();
        String sessionId = (String) sourceButton.getClientProperty("sessionId");

        if (registeredSessions.containsKey(sessionId)) {
            ViewRegisteredSessionPage viewRegisteredSessionPage = new ViewRegisteredSessionPage();
            navigator.navigateTo(viewRegisteredSessionPage);
        } else {
            ViewUnregisteredSessionPage viewUnregisteredSessionPage = new ViewUnregisteredSessionPage();
            navigator.navigateTo(viewUnregisteredSessionPage);
        }
    }

    // filters
    private Map<String, SessionDTO> filerRegisteredSessions(List<SessionDTO> sessions) {
        Map<String, SessionDTO> registeredSessions = new HashMap<>();
        sessions.stream()
                .filter(session -> session.getRegisteredAttendees().contains(attendee.getId()))
                .forEach(filteredSession -> registeredSessions.put(filteredSession.getId(), filteredSession));

        return registeredSessions;
    }

    private Map<String, SessionDTO> filterUnregisteredSessions(List<SessionDTO> sessions) {
        Map<String, SessionDTO> unregisteredSessions = new HashMap<>();
        sessions.stream()
            .filter(sessionDTO -> !sessionDTO.getRegisteredAttendees().contains(attendee.getId()))
            .forEach(filteredSession -> unregisteredSessions.put(filteredSession.getId(), filteredSession));

        return unregisteredSessions;
    }

}
