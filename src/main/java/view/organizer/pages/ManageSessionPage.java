package view.organizer.pages;

import dto.SessionDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;

public class ManageSessionPage {
    // dependencies
    private final SessionDTO sessionDTO;
    private final OrganizerObserver organizerObserver;

    // main panel
    private final JPanel mainContentPanel;

    // buttons
    private final JButton backButton;
    private final JButton editSessionButton = UIComponentFactory.createStyledButton("Edit Session");
    private final JButton deleteSessionButton = UIComponentFactory.createStyledButton("Delete Session");
    private final JButton viewAttendeesButton = UIComponentFactory.createStyledButton("View Registered Attendees");
    private final JButton viewAttendanceButton = UIComponentFactory.createStyledButton("View Attendance");
    private final JButton viewFeedbackButton = UIComponentFactory.createStyledButton("View Feedback");

    public ManageSessionPage(OrganizerObserver organizerObserver, SessionDTO sessionDTO) {
        this.sessionDTO = sessionDTO;
        this.organizerObserver = organizerObserver;
        this.mainContentPanel = new JPanel(new BorderLayout());

        // Create back button
        backButton = UIComponentFactory.createBackButton(e -> this.organizerObserver.onNavigateBackRequest());

        // Adjust view registered attendees button size
        this.viewAttendeesButton.setPreferredSize(new Dimension(200, 40));

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
        JPanel baseHeaderPanel = UIComponentFactory.createHeaderPanel(sessionDTO.getName(), backButton);
        baseHeaderPanel.add(Box.createRigidArea(new Dimension(450, 0)));
        baseHeaderPanel.add(editSessionButton);
        baseHeaderPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        baseHeaderPanel.add(deleteSessionButton);

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
        UIComponentFactory.addLabelToPanel(detailsPanel, "Session Details", new Font("Arial", Font.BOLD, 20), gbc, 0, 0, 2);

        // Reset gridwidth and restore normal insets for subsequent rows
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 5, 15, 5);

        // adding speaker
        UIComponentFactory.addLabelToPanel(detailsPanel, "Speaker: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 1, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, sessionDTO.getSpeakerName(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 1, 1);

        // adding description
        UIComponentFactory.addLabelToPanel(detailsPanel, "Description:", new Font("Arial", Font.PLAIN, 18), gbc, 0, 2, 1);
        UIComponentFactory.addTextAreaToPanel(detailsPanel, sessionDTO.getDescription(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 2, 1);

        // adding room
        UIComponentFactory.addLabelToPanel(detailsPanel, "Room: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 3, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, sessionDTO.getRoom(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 3, 1);

        // adding session date
        UIComponentFactory.addLabelToPanel(detailsPanel, "Date: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 4, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, sessionDTO.getDate().toString(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 4, 1);

        // adding start time
        UIComponentFactory.addLabelToPanel(detailsPanel, "Start Time: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 5, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, sessionDTO.getStartTime().toString(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 5, 1);

        // adding end time
        UIComponentFactory.addLabelToPanel(detailsPanel, "End Time: ", new Font("Arial", Font.PLAIN, 18), gbc, 0, 6, 1);
        UIComponentFactory.addLabelToPanel(detailsPanel, sessionDTO.getEndTime().toString(), new Font("Arial", Font.PLAIN, 18), gbc, 1, 6, 1);

        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 30));

        return detailsPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        footerPanel.add(viewAttendeesButton);
        footerPanel.add(viewAttendanceButton);
        footerPanel.add(viewFeedbackButton);

        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        return footerPanel;
    }

    private void setUpListeners() {
//        editSessionButton.addActionListener(e -> organizerObserver.onEditSessionRequest());
//        deleteSessionButton.addActionListener(e -> organizerObserver.onDeleteSessionRequest());
//        viewAttendeesButton.addActionListener(e -> organizerObserver.onViewSessionAttendeesRequest(sessionDTO.getId(), sessionDTO.getName()));
//        viewAttendanceButton.addActionListener(e -> organizerObserver.onViewSessionAttendanceRequest(sessionDTO.getId()));
//        viewFeedbackButton.addActionListener(e -> organizerObserver.onViewSessionFeedbackRequest(sessionDTO.getId()));
    }
}
