package view.organizer.pages;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class HomePage extends JPanel {
    private final OrganizerObserver organizerObserver;
    private final UserDTO userDTO;
    private final List<ConferenceDTO> managedConferences;
    private final JButton addConferenceButton;

    public HomePage(OrganizerObserver organizerObserver, UserDTO userDTO) {
        this.organizerObserver = organizerObserver;
        this.userDTO = userDTO;
        this.managedConferences = organizerObserver.onGetManagedConferencesRequest(userDTO.getEmail());

        // initialize components
        this.addConferenceButton = new JButton("Add Conference");

        // set up listener
        this.addConferenceButton.addActionListener(e -> organizerObserver.onAddConferenceRequest(this.userDTO.getName()));
    }

    public JPanel createPageContent() {
        JPanel homePanel = new JPanel(new BorderLayout());

        // add header with back button
        homePanel.add(createHomePageHeader(), BorderLayout.NORTH);

        if (managedConferences.isEmpty()) {
            JPanel emptyStatePanel = UIComponentFactory.createEmptyStatePanel("You currently have no conferences under your management. " +
                    "As the organizer, you have the full control to add and manage conferences. " +
                    "Start by creating your first conference to begin organizing sessions and managing attendees.", 0);
            emptyStatePanel.setBorder(BorderFactory.createEmptyBorder(270, 50, 0, 0));
            homePanel.add(emptyStatePanel, BorderLayout.CENTER);
        } else {
            // add scrollable container with conferences
            JScrollPane scrollPane = UIComponentFactory.createConferenceScrollPane(managedConferences, this::handleManageConferenceButton, "Manage Conference");
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            homePanel.add(scrollPane, BorderLayout.CENTER);
        }

        // "add conference" button
        JPanel addConferenceButtonPanel = UIComponentFactory.createButtonPanel(addConferenceButton);
        homePanel.add(addConferenceButtonPanel, BorderLayout.SOUTH);

        return homePanel;
    }

    private JPanel createHomePageHeader() {
        // header panel using BorderLayout to position back button and title
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));

        // page title in the center
        JLabel headerLabel = new JLabel("Your Managed Conferences");
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);  // Ensure it's centered

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(this::handleLogoutButton);

        headerPanel.add(Box.createRigidArea(new Dimension(540, 0)));
        headerPanel.add(headerLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(430, 0)));
        headerPanel.add(logoutButton);

        return headerPanel;
    }

    private void handleManageConferenceButton(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        String conferenceId = (String) sourceButton.getClientProperty("conferenceId");

        // publish event to organizer observer for manage conference click
        organizerObserver.onManageConferenceRequest(conferenceId);
    }


    private void handleLogoutButton(ActionEvent e) {
        int choice = showConfirmDialog();

        if (choice == JOptionPane.YES_OPTION) {
            organizerObserver.onLogoutRequest();
        }
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
}
