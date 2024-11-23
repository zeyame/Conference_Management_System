package view.organizer.pages;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class HomePage {
    private final UserDTO userDTO;
    private final OrganizerObserver organizerObserver;
    private final JButton addConferenceButton;

    public HomePage(UserDTO userDTO, OrganizerObserver organizerObserver) {
        this.userDTO = userDTO;
        this.organizerObserver = organizerObserver;

        // initialize components
        this.addConferenceButton = new JButton("Add Conference");

        // set up listener
        this.addConferenceButton.addActionListener(e -> organizerObserver.onAddConferenceRequest(userDTO.getName()));
    }

    public JPanel createPageContent() {
        JPanel homePanel = new JPanel(new BorderLayout());

        // add header with back button
        homePanel.add(createHomePageHeader(), BorderLayout.NORTH);

        // add scrollable container with conferences
        JScrollPane scrollPane = createConferenceScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        homePanel.add(scrollPane, BorderLayout.CENTER);

        // "add conference" button
        JPanel addConferenceButtonPanel = UIComponentFactory.createButtonPanel(addConferenceButton);
        homePanel.add(addConferenceButtonPanel, BorderLayout.SOUTH);

        return homePanel;
    }

    private JPanel createHomePageHeader() {
        // header panel using BorderLayout to position back button and title
        JPanel headerPanel = new JPanel(new BorderLayout());

        // page title in the center
        JLabel headerLabel = new JLabel("Your Managed Conferences");
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);  // Ensure it's centered
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JScrollPane createConferenceScrollPane() {
        JPanel conferencesPanel = new JPanel();
        conferencesPanel.setLayout(new BoxLayout(conferencesPanel, BoxLayout.Y_AXIS));
        conferencesPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 0));

        // publishing an event to organizer observer to fetch managed conferences for organizer
        List<ConferenceDTO> conferences = organizerObserver.onGetManagedConferencesRequest(userDTO.getEmail());
        for (ConferenceDTO conference : conferences) {
            conferencesPanel.add(createConferencePanel(conference));
            conferencesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JScrollPane scrollPane = new JScrollPane(conferencesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(7);
        return scrollPane;
    }

    private JPanel createConferencePanel(ConferenceDTO conference) {
        JPanel conferencePanel = new JPanel();
        conferencePanel.setLayout(new BoxLayout(conferencePanel, BoxLayout.Y_AXIS));

        // name of conference
        JLabel nameLabel = new JLabel(conference.getName());
        nameLabel.setFont(new Font("Sans serif", Font.BOLD, 16));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // start date of conference
        JLabel dateLabel = new JLabel("Date: " + conference.getStartDate().toString());
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));

        // manage conference button
        JButton manageButton = new JButton("Manage Conference");
        manageButton.setFocusPainted(false);
        manageButton.putClientProperty("conferenceId", conference.getId());
        manageButton.addActionListener(this::handleManageConferenceButton);

        conferencePanel.add(nameLabel);
        conferencePanel.add(dateLabel);
        conferencePanel.add(manageButton);

        return conferencePanel;
    }

    private void handleManageConferenceButton(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        String conferenceId = (String) sourceButton.getClientProperty("conferenceId");

        // publish event to organizer observer for manage conference click
        organizerObserver.onManageConferenceRequest(conferenceId);
    }
}
