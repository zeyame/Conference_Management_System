package view.organizer.pages.manage;

import dto.ConferenceDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;

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
        editButton.addActionListener(e -> organizerObserver.onEditConferenceRequest());
        deleteButton.addActionListener(e -> organizerObserver.onDeleteConferenceRequest());
        viewAttendeesButton.addActionListener(e -> organizerObserver.onViewAttendeesRequest(conferenceDTO.getId()));
        viewSessionsButton.addActionListener(e -> organizerObserver.onViewSessionsRequest(conferenceDTO.getId()));
        viewSpeakersButton.addActionListener(e -> organizerObserver.onViewSpeakersRequest());
        viewFeedbackButton.addActionListener(e -> organizerObserver.onViewConferenceFeedbackRequest(conferenceDTO.getId()));
    }
}
