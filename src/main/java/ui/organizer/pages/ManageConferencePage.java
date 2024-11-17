package ui.organizer.pages;

import dto.ConferenceDTO;
import dto.UserDTO;
import ui.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;

public class ManageConferencePage {
    // dependencies
    private final ConferenceDTO conferenceDTO;
    private final OrganizerObserver organizerObserver;
    private final UserDTO userDTO;

    // main panel
    private final JPanel mainContentPanel;

    // buttons
    private final JButton editConferenceButton;
    private final JButton deleteConferenceButton;
    private final JButton viewAttendeesButton;
    private final JButton viewSessionsButton;
    private final JButton viewSpeakersButton;
    private final JButton viewFeedbackButton;


    public ManageConferencePage(ConferenceDTO conferenceDTO, UserDTO userDTO, OrganizerObserver organizerObserver) {
        this.conferenceDTO = conferenceDTO;
        this.userDTO = userDTO;
        this.organizerObserver = organizerObserver;

        // initializing components
        this.mainContentPanel = new JPanel(new BorderLayout());
        this.editConferenceButton = new JButton("Edit Conference");
        this.deleteConferenceButton = new JButton("Delete Conference");
        this.viewAttendeesButton = new JButton("View Attendees");
        this.viewSessionsButton = new JButton("View Sessions");
        this.viewSpeakersButton = new JButton("View Speakers");
        this.viewFeedbackButton = new JButton("View Feedback");

        // setting up event listeners
        setUpListeners();
    }

    public JPanel createPageContent() {
        mainContentPanel.removeAll();

        JPanel headerPanel = createHeaderPanel();
        JPanel detailsPanel = createDetailsPanel();
        JPanel footerPanel = createFooterPanel();

        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(detailsPanel, BorderLayout.CENTER);
        mainContentPanel.add(footerPanel, BorderLayout.SOUTH);

        return mainContentPanel;
    }

    private JPanel createHeaderPanel() {
        final String conferenceName = conferenceDTO.getName();

        JPanel headerPanel = new JPanel(new BorderLayout());

        // title
        JLabel titleLabel = new JLabel(conferenceName, JLabel.CENTER);
        titleLabel.setFont(new Font("Sans serif", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // edit/delete buttons
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.add(editConferenceButton);
        topRightPanel.add(deleteConferenceButton);
        headerPanel.add(topRightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createDetailsPanel() {
        final String organizerName = userDTO.getName();
        final String conferenceDescription = conferenceDTO.getDescription();
        final Date conferenceStartDate = conferenceDTO.getStartDate();
        final Date conferenceEndDate = conferenceDTO.getEndDate();

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        // adding labels
        detailsPanel.add(new JLabel("Organized by: " + organizerName));
        detailsPanel.add(new JLabel("Description: " + conferenceDescription));
        detailsPanel.add(new JLabel("Start Date: " + conferenceStartDate));
        detailsPanel.add(new JLabel("End Date: " + conferenceEndDate));

        return detailsPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.add(viewAttendeesButton);
        footerPanel.add(viewSessionsButton);
        footerPanel.add(viewSessionsButton);
        footerPanel.add(viewFeedbackButton);

        return footerPanel;
    }


    private void setUpListeners() {
        editConferenceButton.addActionListener(this::handleEditConferenceClick);
        deleteConferenceButton.addActionListener(this::handleDeleteConferenceClick);
        viewAttendeesButton.addActionListener(this::handleViewAttendeesClick);
        viewSessionsButton.addActionListener(this::handleViewSessionsClick);
        viewSpeakersButton.addActionListener(this::handleViewSpeakersClick);
        viewFeedbackButton.addActionListener(this::handleViewFeedbackClick);
    }

    private void handleEditConferenceClick(ActionEvent e) {
        organizerObserver.onEditConferenceRequest();
    }

    private void handleDeleteConferenceClick(ActionEvent e) {
        organizerObserver.onDeleteConferenceRequest();
    }

    private void handleViewAttendeesClick(ActionEvent e) {
        organizerObserver.onViewAttendeesRequest();
    }

    private void handleViewSessionsClick(ActionEvent e) {
        organizerObserver.onViewSessionsRequest();
    }

    private void handleViewSpeakersClick(ActionEvent e) {
        organizerObserver.onViewSpeakersRequest();
    }

    private void handleViewFeedbackClick(ActionEvent e) {
        organizerObserver.onViewFeedbackRequest();
    }
}
