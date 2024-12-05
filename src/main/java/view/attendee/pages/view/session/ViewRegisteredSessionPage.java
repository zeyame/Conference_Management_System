package view.attendee.pages.view.session;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.observers.SessionEventObserver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewRegisteredSessionPage extends ViewSessionPage {
    public ViewRegisteredSessionPage(UserDTO attendee, String sessionId, UIEventMediator eventMediator, Navigator navigator) {
        super(attendee, sessionId, eventMediator, navigator);
    }

    @Override
    protected void createPageContent() {
        // header with a leave button
        JPanel headerPanel = createHeaderPanelWithLeaveButton();
        add(headerPanel, BorderLayout.NORTH);

        // session details
        JPanel sessionDetails = UIComponentFactory.createSessionDetailsPanel(sessionDTO);
        sessionDetails.setBorder(BorderFactory.createEmptyBorder(0, 130, 40, 0));
        add(sessionDetails, BorderLayout.CENTER);

        // footer
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    @Override
    protected JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));

        JButton provideSessionFeedbackButton = UIComponentFactory.createStyledButton("Provide Session Feedback");
        JButton provideSpeakerFeedbackButton = UIComponentFactory.createStyledButton("Provide Speaker Feedback");

        provideSessionFeedbackButton.setPreferredSize(new Dimension(270, 40));
        provideSpeakerFeedbackButton.setPreferredSize(new Dimension(270, 40));

        provideSessionFeedbackButton.addActionListener(this::handleProvideSessionFeedbackButton);
        provideSpeakerFeedbackButton.addActionListener(this::handleProvideSpeakerFeedbackButton);

        footerPanel.add(provideSessionFeedbackButton);
        footerPanel.add(provideSpeakerFeedbackButton);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 80));

        return footerPanel;
    }

    private JPanel createHeaderPanelWithLeaveButton() {
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(sessionDTO.getName(), this::handleBackButton, 460);
        headerPanel.add(Box.createRigidArea(new Dimension(500, 0)));

        JButton leaveSessionButton = UIComponentFactory.createStyledButton("Leave Session");
        leaveSessionButton.addActionListener(this::handleLeaveSessionButton);

        headerPanel.add(leaveSessionButton);

        return headerPanel;
    }


    // button handlers
    private void handleLeaveSessionButton(ActionEvent e) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                String.format("Are you sure you want to leave '%s'?", sessionDTO.getName()),
                "Confirm Session Leave",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            eventMediator.publishEvent(
                    SessionEventObserver.class,
                    observer -> observer.onLeaveSession(sessionId, attendee.getId(), this::onLeaveSession)
            );
        } else if (choice == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    "Session leave canceled.",
                    "Canceled",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void handleProvideSessionFeedbackButton(ActionEvent e) {

    }

    private void handleProvideSpeakerFeedbackButton(ActionEvent e) {

    }

    private void onLeaveSession(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        showSuccess(String.format("You have successfully left the session '%s'.", sessionDTO.getName()));

        // navigate back to view session page to display the updated list of upcoming sessions
        ViewSessionsPage viewSessionsPage = new ViewSessionsPage(attendee, sessionDTO.getConferenceId(), eventMediator, navigator);
        navigator.navigateTo(viewSessionsPage, false);
    }


}
