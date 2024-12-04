package view.organizer.pages.manage;

import dto.SessionDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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
        return UIComponentFactory.createSessionDetailsPanel(sessionDTO);
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
        editButton.addActionListener(e -> organizerObserver.onEditSessionRequest(sessionDTO));
        deleteButton.addActionListener(this::handleDeleteSessionRequest);
        viewAttendeesButton.addActionListener(e -> organizerObserver.onViewSessionAttendeesRequest(sessionDTO.getId()));
        viewAttendanceButton.addActionListener(e -> organizerObserver.onViewSessionAttendanceRequest(sessionDTO.getId()));
        viewFeedbackButton.addActionListener(e -> organizerObserver.onViewSessionFeedbackRequest(sessionDTO.getId()));
    }

    private void handleDeleteSessionRequest(ActionEvent event) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                String.format("Are you sure you want to delete the session '%s'?", sessionDTO.getName()),
                "Confirm Session Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            organizerObserver.onDeleteSessionRequest(sessionDTO.getId());
        } else if (choice == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    "Session deletion canceled.",
                    "Canceled",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

}
