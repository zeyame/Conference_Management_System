package view.organizer.pages;

import dto.ConferenceDTO;
import dto.UserDTO;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;

public class ManageConferencePage {
    // Dependencies
    private final ConferenceDTO conferenceDTO;
    private final OrganizerObserver organizerObserver;
    private final UserDTO userDTO;

    // Main panel
    private final JPanel mainContentPanel;

    // Buttons
    private final JButton editConferenceButton = createStyledButton("Edit Conference");
    private final JButton deleteConferenceButton = createStyledButton("Delete Conference");
    private final JButton viewAttendeesButton = createStyledButton("View Attendees");
    private final JButton viewSessionsButton = createStyledButton("View Sessions");
    private final JButton viewSpeakersButton = createStyledButton("View Speakers");
    private final JButton viewFeedbackButton = createStyledButton("View Feedback");

    public ManageConferencePage(ConferenceDTO conferenceDTO, UserDTO userDTO, OrganizerObserver organizerObserver) {
        this.conferenceDTO = conferenceDTO;
        this.userDTO = userDTO;
        this.organizerObserver = organizerObserver;
        this.mainContentPanel = new JPanel(new BorderLayout());

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
        JLabel titleLabel = createStyledLabel(conferenceDTO.getName(), new Font("Sans serif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(editConferenceButton);
        buttonPanel.add(deleteConferenceButton);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createDefaultGridBagConstraints();

        // adding header
        addLabelToPanel(detailsPanel, "Conference Details", new Font("Arial", Font.BOLD, 20), gbc, 0, 0, 2);

        // adding organizer
        addLabelToPanel(detailsPanel, "Organized by: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 1, 1);
        addLabelToPanel(detailsPanel, userDTO.getName(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 1, 1);

        // adding description
        addLabelToPanel(detailsPanel, "Description:", new Font("Arial", Font.PLAIN, 18), gbc, 0, 2, 1);
        addTextAreaToPanel(detailsPanel, conferenceDTO.getDescription(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 2, 1);

        // adding start date
        addLabelToPanel(detailsPanel, "Start Date: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 3, 1);
        addLabelToPanel(detailsPanel, conferenceDTO.getStartDate().toString(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 3, 1);

        // adding end date
        addLabelToPanel(detailsPanel, "End Date: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 4, 1);
        addLabelToPanel(detailsPanel, conferenceDTO.getEndDate().toString(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 4, 1);

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

    // Helper methods for UI components
    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setFocusPainted(false);
        return button;
    }

    private static JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    private static void addLabelToPanel(JPanel panel, String text, Font font, GridBagConstraints gbc, int x, int y, int width) {
        JLabel label = createStyledLabel(text, font);
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        panel.add(label, gbc);
    }

    private static void addTextAreaToPanel(JPanel panel, String text, Font font, GridBagConstraints gbc, int x, int y, int width) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(font);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        panel.add(textArea, gbc);
    }

    private static GridBagConstraints createDefaultGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }
}
