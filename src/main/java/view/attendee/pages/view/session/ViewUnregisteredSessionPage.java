package view.attendee.pages.view.session;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.navigation.Navigator;
import view.event.UIEventMediator;
import view.observers.SessionEventObserver;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewUnregisteredSessionPage extends ViewSessionPage {
    public ViewUnregisteredSessionPage(UserDTO attendee, String sessionId, UIEventMediator eventMediator, Navigator navigator) {
        super(attendee, sessionId, eventMediator, navigator);
    }

    @Override
    protected JPanel createFooterPanel() {
        JButton registerButton = UIComponentFactory.createStyledButton("Register");
        registerButton.addActionListener(this::handleRegisterButton);

        JPanel buttonPanel = UIComponentFactory.createButtonPanel(registerButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 100));
        return buttonPanel;
    }

    // button handlers
    @Override
    protected void handleBackButton(ActionEvent e) {
        ViewUpcomingSessions viewUpcomingSessions = new ViewUpcomingSessions(userDTO, sessionDTO.getConferenceId(), eventMediator, navigator);
        navigator.navigateTo(viewUpcomingSessions);
    }

    private void handleRegisterButton(ActionEvent e) {
        eventMediator.publishEvent(
                SessionEventObserver.class,
                observer -> observer.onRegisterForSession(userDTO.getId(), sessionDTO.getId(), this::onRegisteredForSession)
        );
    }

    // callback responders
    private void onRegisteredForSession(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }

        showSuccess(String.format("You have successfully been registered to attend session '%s'.", sessionDTO.getName()));
        ViewPersonalSchedulePage viewPersonalSchedulePage = new ViewPersonalSchedulePage(userDTO, eventMediator, navigator, sessionDTO.getConferenceId());
        navigator.navigateTo(viewPersonalSchedulePage, false);
    }

}
