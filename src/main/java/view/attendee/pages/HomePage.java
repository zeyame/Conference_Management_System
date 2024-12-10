package view.attendee.pages;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.LoggerUtil;
import util.ui.UIComponentFactory;
import view.navigation.Navigator;
import view.event.UIEventMediator;
import view.observers.ConferenceEventObserver;
import view.attendee.pages.view.conference.ViewConferencePage;
import view.attendee.pages.view.conference.ViewRegisteredConferencesPage;
import view.attendee.pages.view.conference.ViewUpcomingConferencePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class HomePage extends JPanel {

    private final UserDTO attendee;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;

    private final JButton viewRegisteredConferences;
    private List<ConferenceDTO> upcomingConferences;

    public HomePage(UserDTO userDTO, UIEventMediator eventMediator, Navigator navigator) {
        this.attendee = userDTO;
        this.eventMediator = eventMediator;
        this.navigator = navigator;

        // set up view registered conferences button
        this.viewRegisteredConferences = new JButton("Your Registered Conferences");
        this.viewRegisteredConferences.addActionListener(this::handleViewRegisteredConferences);

        // publishing an event to get an updated list of the upcoming conferences
        fetchUpcomingConferences();

        createPageContent();
    }

    private void createPageContent() {
        setLayout(new BorderLayout());

        // add header with back button
        add(createHomePageHeader(), BorderLayout.NORTH);

        // add scrollable container with conferences or display no upcoming conferences message
        if (!upcomingConferences.isEmpty()) {
            JScrollPane scrollPane = UIComponentFactory.createConferenceScrollPane(upcomingConferences, this::handleViewConferenceButton, "View Conference");
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            add(scrollPane, BorderLayout.CENTER);
        } else {
            JPanel emptyStatePanel = UIComponentFactory.createEmptyStatePanel("It looks like there are no upcoming conferences " +
                    "available that you haven’t already registered for. Maybe check out your registered conferences.", 60);
            add(emptyStatePanel, BorderLayout.CENTER);
        }

        // "View Registered Conferences" button
        JPanel viewRegisteredConferencesPanel = UIComponentFactory.createButtonPanel(viewRegisteredConferences);
        add(viewRegisteredConferencesPanel, BorderLayout.SOUTH);
    }


    private JPanel createHomePageHeader() {
        // header panel using BorderLayout to position back button and title
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));

        // page title in the center
        JLabel headerLabel = new JLabel("Upcoming Conferences");
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(this::handleLogoutButton);


        headerPanel.add(Box.createRigidArea(new Dimension(550, 0)));
        headerPanel.add(headerLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(450, 0)));
        headerPanel.add(logoutButton);

        return headerPanel;
    }

    private void fetchUpcomingConferences() {
        this.eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onGetUpcomingConferences(attendee.getId(), this::onUpcomingConferencesFetched)
        );
    }

    private void onUpcomingConferencesFetched(List<ConferenceDTO> conferenceDTOs, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }

        LoggerUtil.getInstance().logInfo(String.format("Upcoming conferences for attendee '%s' fetched.", attendee.getName()));
        this.upcomingConferences = conferenceDTOs;
    }

    private void handleLogoutButton(ActionEvent e) {
        int choice = showConfirmDialog();

        if (choice == JOptionPane.YES_OPTION) {
            navigator.logout();
        }
    }

    private void handleViewConferenceButton(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        String conferenceId = (String) sourceButton.getClientProperty("conferenceId");

        ViewConferencePage viewUpcomingConferencePage = new ViewUpcomingConferencePage(this.attendee, this.eventMediator, this.navigator, conferenceId);
        navigator.navigateTo(viewUpcomingConferencePage);
    }

    private void handleViewRegisteredConferences(ActionEvent e) {
        // navigate to View Registered Conferences Page
        ViewRegisteredConferencesPage viewRegisteredConferencesPage = new ViewRegisteredConferencesPage(attendee, this.eventMediator, this.navigator);
        navigator.navigateTo(viewRegisteredConferencesPage);
    }


    private int showConfirmDialog() {
        return JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}