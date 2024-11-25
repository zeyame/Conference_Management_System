package view.organizer.pages.manage;

import dto.SessionDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;

public class ManageSessionPage extends ManagePage {
    // dependencies
    private final SessionDTO sessionDTO;

    // buttons
    private final JButton viewAttendeesButton = UIComponentFactory.createStyledButton("View Registered Attendees");
    private final JButton viewAttendanceButton = UIComponentFactory.createStyledButton("View Attendance");
    private final JButton viewFeedbackButton = UIComponentFactory.createStyledButton("View Feedback");

    public ManageSessionPage(OrganizerObserver organizerObserver, SessionDTO sessionDTO) {
        super(organizerObserver, "Edit Session", "Delete Session");
        this.sessionDTO = sessionDTO;

        // Adjust view registered attendees button size
        this.viewAttendeesButton.setPreferredSize(new Dimension(200, 40));

        setUpListeners();
    }

    @Override
    protected String getHeaderTitle() {
        return sessionDTO.getName();
    }

    @Override
    protected JPanel createDetailsPanel() {
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

    @Override
    protected JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        footerPanel.add(viewAttendeesButton);
        footerPanel.add(viewAttendanceButton);
        footerPanel.add(viewFeedbackButton);

        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        return footerPanel;
    }

    @Override
    protected void setUpListeners() {
//        editSessionButton.addActionListener(e -> organizerObserver.onEditSessionRequest());
//        deleteSessionButton.addActionListener(e -> organizerObserver.onDeleteSessionRequest());
//        viewAttendeesButton.addActionListener(e -> organizerObserver.onViewSessionAttendeesRequest(sessionDTO.getId(), sessionDTO.getName()));
//        viewAttendanceButton.addActionListener(e -> organizerObserver.onViewSessionAttendanceRequest(sessionDTO.getId()));
//        viewFeedbackButton.addActionListener(e -> organizerObserver.onViewSessionFeedbackRequest(sessionDTO.getId()));
    }
}
