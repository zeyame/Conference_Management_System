package view.attendee.pages.view.session;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.navigation.Navigator;
import view.event.UIEventMediator;
import view.observers.SessionEventObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewUpcomingRegisteredSessionPage extends ViewSessionPage {
    public ViewUpcomingRegisteredSessionPage(UserDTO attendee, String sessionId, UIEventMediator eventMediator, Navigator navigator) {
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
    protected  JPanel createFooterPanel() {
        return new JPanel();
    }

    private JPanel createHeaderPanelWithLeaveButton() {
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(sessionDTO.getName(), this::handleBackButton, 470);
        headerPanel.add(Box.createRigidArea(new Dimension(430, 0)));

        JButton leaveSessionButton = UIComponentFactory.createStyledButton("Leave Session");
        leaveSessionButton.addActionListener(this::handleLeaveSessionButton);

        headerPanel.add(leaveSessionButton);

        return headerPanel;
    }


    // button handlers
    @Override
    protected void handleBackButton(ActionEvent e) {
        ViewPersonalSchedulePage viewPersonalSchedulePage = new ViewPersonalSchedulePage(userDTO, eventMediator, navigator, sessionDTO.getConferenceId());
        navigator.navigateTo(viewPersonalSchedulePage);
    }

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
                    observer -> observer.onLeaveSession(sessionId, userDTO.getId(), this::onLeaveSession)
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

    private void onLeaveSession(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        showSuccess(String.format("You have successfully left the session '%s'.", sessionDTO.getName()));

        // navigate back to view session page to display the updated list of upcoming sessions
        ViewUpcomingSessions viewSessionsPage = new ViewUpcomingSessions(userDTO, sessionDTO.getConferenceId(), eventMediator, navigator);
        navigator.navigateTo(viewSessionsPage, false);
    }


}
