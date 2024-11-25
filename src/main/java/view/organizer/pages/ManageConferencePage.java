package view.organizer.pages;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;

public class ManageConferencePage {
    // dependencies
    private final ConferenceDTO conferenceDTO;
    private final OrganizerObserver organizerObserver;
    private final UserDTO userDTO;

    // main panel
    private final JPanel mainContentPanel;

    // buttons
    private final JButton backButton;
    private final JButton editConferenceButton = UIComponentFactory.createStyledButton("Edit Conference");
    private final JButton deleteConferenceButton = UIComponentFactory.createStyledButton("Delete Conference");
    private final JButton viewAttendeesButton = UIComponentFactory.createStyledButton("View Attendees");
    private final JButton viewSessionsButton = UIComponentFactory.createStyledButton("View Sessions");
    private final JButton viewSpeakersButton = UIComponentFactory.createStyledButton("View Speakers");
    private final JButton viewFeedbackButton = UIComponentFactory.createStyledButton("View Feedback");

    public ManageConferencePage(ConferenceDTO conferenceDTO, UserDTO userDTO, OrganizerObserver organizerObserver) {
        this.conferenceDTO = conferenceDTO;
        this.userDTO = userDTO;
        this.organizerObserver = organizerObserver;
        this.mainContentPanel = new JPanel(new BorderLayout());

        // Create back button
        backButton = UIComponentFactory.createBackButton(e -> this.organizerObserver.onNavigateBackRequest());

        // Adjust back button size
        Dimension smallerSize = new Dimension(25, 25);
        backButton.setPreferredSize(smallerSize);
        backButton.setMinimumSize(smallerSize);
        backButton.setMaximumSize(smallerSize);
        backButton.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        setUpListeners();
    }

    public JPanel createPageContent() {
        // refresh page
        mainContentPanel.removeAll();

        // add main components to the page
        mainContentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainContentPanel.add(createDetailsPanel(), BorderLayout.CENTER);
        mainContentPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        return mainContentPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel baseHeaderPanel = UIComponentFactory.createHeaderPanel(conferenceDTO.getName(), backButton);
        baseHeaderPanel.add(Box.createRigidArea(new Dimension(350, 0)));
        baseHeaderPanel.add(editConferenceButton);
        baseHeaderPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        baseHeaderPanel.add(deleteConferenceButton);

        return baseHeaderPanel;
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = UIComponentFactory.createDefaultGridBagConstraints();

        // Increase vertical spacing with larger top/bottom insets
        gbc.insets = new Insets(15, 5, 15, 5);  // Changed from (5,5,5,5) to (15,5,15,5)
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // adding header with extra bottom spacing
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 25, 5);  // Extra bottom padding for header
        UIComponentFactory.addLabelToPanel(detailsPanel, "Conference Details", new Font("Arial", Font.BOLD, 20), gbc, 0, 0, 2);

        // Reset gridwidth and restore normal insets for subsequent rows
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 5, 15, 5);

        // adding organizer
        UIComponentFactory.addLabelToPanel(detailsPanel, "Organized by: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 1, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, userDTO.getName(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 1, 1);

        // adding description
        UIComponentFactory.addLabelToPanel(detailsPanel, "Description:", new Font("Arial", Font.PLAIN, 18), gbc, 0, 2, 1);
        UIComponentFactory.addTextAreaToPanel(detailsPanel, conferenceDTO.getDescription(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 2, 1);

        // adding start date
        UIComponentFactory.addLabelToPanel(detailsPanel, "Start Date: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 3, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, conferenceDTO.getStartDate().toString(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 3, 1);

        // adding end date
        UIComponentFactory.addLabelToPanel(detailsPanel, "End Date: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 4, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, conferenceDTO.getEndDate().toString(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 4, 1);

        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 150));

        return detailsPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        footerPanel.add(viewAttendeesButton);
        footerPanel.add(viewSessionsButton);
        footerPanel.add(viewSpeakersButton);
        footerPanel.add(viewFeedbackButton);

        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        return footerPanel;
    }

    private void setUpListeners() {
        editConferenceButton.addActionListener(e -> organizerObserver.onEditConferenceRequest());
        deleteConferenceButton.addActionListener(e -> organizerObserver.onDeleteConferenceRequest());
        viewAttendeesButton.addActionListener(e -> organizerObserver.onViewAttendeesRequest(conferenceDTO.getId(), conferenceDTO.getName()));
        viewSessionsButton.addActionListener(e -> organizerObserver.onViewSessionsRequest(conferenceDTO.getId(), conferenceDTO.getName()));
        viewSpeakersButton.addActionListener(e -> organizerObserver.onViewSpeakersRequest());
        viewFeedbackButton.addActionListener(e -> organizerObserver.onViewFeedbackRequest());
    }
}
