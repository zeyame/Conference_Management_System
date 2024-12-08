package view.organizer.pages.manage;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ManageConferencePage extends ManagePage {
    private final ConferenceDTO conferenceDTO;
    private final UserDTO userDTO;

    private final JButton viewAttendeesButton = UIComponentFactory.createStyledButton("View Attendees");
    private final JButton viewSessionsButton = UIComponentFactory.createStyledButton("View Sessions");
    private final JButton viewSpeakersButton = UIComponentFactory.createStyledButton("View Speakers");
    private final JButton viewFeedbackButton = UIComponentFactory.createStyledButton("View Feedback");

    public ManageConferencePage(OrganizerObserver organizerObserver, ConferenceDTO conferenceDTO, UserDTO userDTO) {
        super(organizerObserver, "Edit Conference", "Delete Conference");
        this.conferenceDTO = conferenceDTO;
        this.userDTO = userDTO;

        setUpListeners();
    }

    @Override
    protected String getHeaderTitle() {
        return conferenceDTO.getName();
    }

    @Override
    protected JPanel createDetailsPanel() {
        return UIComponentFactory.createConferenceDetailsPanel(conferenceDTO, userDTO.getName(), 0);
    }


    @Override
    protected JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        footerPanel.add(viewAttendeesButton);
        footerPanel.add(viewSessionsButton);
        footerPanel.add(viewSpeakersButton);
        footerPanel.add(viewFeedbackButton);

        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        return footerPanel;
    }

    @Override
    protected void setUpListeners() {
        editButton.addActionListener(e -> organizerObserver.onEditConferenceRequest(conferenceDTO));
        deleteButton.addActionListener(this::handleDeleteConferenceClick);
        viewAttendeesButton.addActionListener(e -> organizerObserver.onViewAttendeesRequest(conferenceDTO.getId()));
        viewSessionsButton.addActionListener(e -> organizerObserver.onViewSessionsRequest(conferenceDTO.getId()));
        viewSpeakersButton.addActionListener(e -> organizerObserver.onViewSpeakersRequest(conferenceDTO.getId()));
        viewFeedbackButton.addActionListener(e -> organizerObserver.onViewConferenceFeedbackRequest(conferenceDTO.getId()));
    }

    private void handleDeleteConferenceClick(ActionEvent event) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                String.format("Are you sure you want to delete the conference '%s'?", conferenceDTO.getName()),
                "Confirm Conference Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            organizerObserver.onDeleteConferenceRequest(conferenceDTO.getId());
        } else if (choice == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    "Conference deletion canceled.",
                    "Canceled",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
