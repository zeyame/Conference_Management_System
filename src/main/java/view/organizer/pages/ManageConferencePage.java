package view.organizer.pages;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;

public class ManageConferencePage {
    // Dependencies
    private final ConferenceDTO conferenceDTO;
    private final OrganizerObserver organizerObserver;
    private final UserDTO userDTO;

    // Main panel
    private final JPanel mainContentPanel;

    // Buttons
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
        backButton = UIComponentFactory.createBackButton(e -> organizerObserver.onNavigateBackRequest());

        // Adjust back button size
        Dimension smallerSize = new Dimension(25, 25);
        backButton.setPreferredSize(smallerSize);
        backButton.setMinimumSize(smallerSize);
        backButton.setMaximumSize(smallerSize);
        backButton.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        setUpListeners();
    }

    public JPanel createPageContent() {
        mainContentPanel.removeAll();
        mainContentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainContentPanel.add(createDetailsPanel(), BorderLayout.CENTER);
        mainContentPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        return mainContentPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        // Title label with fixed width
        JLabel titleLabel = UIComponentFactory.createStyledLabel(conferenceDTO.getName(), new Font("Sans serif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Panel for the buttons (edit and delete)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(editConferenceButton);
        buttonPanel.add(deleteConferenceButton);

        // Add the components to header
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = UIComponentFactory.createDefaultGridBagConstraints();

        // adding header
        UIComponentFactory.addLabelToPanel(detailsPanel, "Conference Details", new Font("Arial", Font.BOLD, 20), gbc, 0, 0, 2);

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

        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));
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
        viewAttendeesButton.addActionListener(e -> organizerObserver.onViewAttendeesRequest());
        viewSessionsButton.addActionListener(e -> organizerObserver.onViewSessionsRequest());
        viewSpeakersButton.addActionListener(e -> organizerObserver.onViewSpeakersRequest());
        viewFeedbackButton.addActionListener(e -> organizerObserver.onViewFeedbackRequest());
    }
}
