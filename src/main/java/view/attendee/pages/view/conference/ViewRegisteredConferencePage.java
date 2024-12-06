package view.attendee.pages.view.conference;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.observers.ConferenceEventObserver;
import view.attendee.pages.HomePage;
import view.attendee.pages.view.session.ViewPersonalSchedulePage;
import view.attendee.pages.view.session.ViewUpcomingSessions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewRegisteredConferencePage extends ViewConferencePage {
    // buttons
    private JButton leaveConferenceButton;
    private JButton viewPersonalSchedule;
    private JButton viewSessionsButton;
    private JButton viewSpeakersButton;
    private JButton provideFeedbackButton;

    public ViewRegisteredConferencePage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator, String conferenceId) {
        super(attendee, eventMediator, navigator, conferenceId);

        initializeButtons();

        setUpListeners();

        createPageContent();
    }

    @Override
    protected void createPageContent() {
        // header panel with leave button
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(conferenceDTO.getName(), this::handleBackButton, 400);
        headerPanel.add(Box.createRigidArea(new Dimension(450, 0)));
        headerPanel.add(leaveConferenceButton);
        add(headerPanel, BorderLayout.NORTH);

        // details panel
        JPanel conferenceDetails = UIComponentFactory.createConferenceDetailsPanel(conferenceDTO, organizerName);
        conferenceDetails.setBorder(BorderFactory.createEmptyBorder(0, 30, 40, 0));
        add(conferenceDetails, BorderLayout.CENTER);

        // footer panel
        JPanel footerPanel = getFooterPanel();
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 100));
        add(footerPanel, BorderLayout.SOUTH);
    }

    @Override
    protected JPanel getFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        footerPanel.add(viewPersonalSchedule);
        footerPanel.add(viewSessionsButton);
        footerPanel.add(viewSpeakersButton);
        footerPanel.add(provideFeedbackButton);

        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        return footerPanel;
    }

    private void initializeButtons() {
        // initialize buttons
        this.leaveConferenceButton = UIComponentFactory.createStyledButton("Leave Conference");
        this.viewPersonalSchedule = UIComponentFactory.createStyledButton("Your Personal Schedule");
        this.viewSessionsButton = UIComponentFactory.createStyledButton("View Upcoming Sessions");
        this.viewSpeakersButton = UIComponentFactory.createStyledButton("View Speakers");
        this.provideFeedbackButton = UIComponentFactory.createStyledButton("Provide Feedback");
    }

    private void onLeaveConference(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }

        showSuccess(String.format("You have successfully left the conference '%s'.", conferenceDTO.getName()));

        // navigate back to home page to display the updated list of upcoming conferences that attendee is not registered for
        HomePage homePage = new HomePage(attendee, eventMediator, navigator);
        navigator.navigateTo(homePage, false);
    }



    // button handlers
    private void setUpListeners() {
        this.leaveConferenceButton.addActionListener(this::handleLeaveConferenceButton);
        this.viewPersonalSchedule.addActionListener(this::handleViewPersonalScheduleButton);
        this.viewSessionsButton.addActionListener(this::handleViewUpcomingSessionsButton);
    }

    private void handleLeaveConferenceButton(ActionEvent e) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                String.format("Are you sure you want to leave '%s'?", conferenceDTO.getName()),
                "Confirm Conference Leave",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            eventMediator.publishEvent(
                    ConferenceEventObserver.class,
                    observer -> observer.onLeaveConference(attendee.getId(), conferenceDTO.getId(), this::onLeaveConference)
            );
        } else if (choice == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    "Conference leave canceled.",
                    "Canceled",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void handleViewPersonalScheduleButton(ActionEvent e) {
        ViewPersonalSchedulePage viewPersonalSchedulePage = new ViewPersonalSchedulePage(attendee, eventMediator, navigator, conferenceId);
        navigator.navigateTo(viewPersonalSchedulePage);
    }

    private void handleViewUpcomingSessionsButton(ActionEvent e) {
        ViewUpcomingSessions viewSessionsPage = new ViewUpcomingSessions(attendee, conferenceDTO.getId(), eventMediator, navigator);
        navigator.navigateTo(viewSessionsPage);
    }

}
