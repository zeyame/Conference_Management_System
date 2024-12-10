package view.attendee.pages.view.conference;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.navigation.Navigator;
import view.event.UIEventMediator;
import view.observers.ConferenceEventObserver;
import view.attendee.pages.HomePage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewUpcomingConferencePage extends ViewConferencePage {
    public ViewUpcomingConferencePage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator, String conferenceId) {
        super(attendee, eventMediator, navigator, conferenceId);
        createPageContent();
    }

    protected JPanel getFooterPanel() {
        JButton registerButton = UIComponentFactory.createStyledButton("Register");
        registerButton.addActionListener(this::handleRegisterButton);

        JPanel registerButtonPanel = UIComponentFactory.createButtonPanel(registerButton);
        registerButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 30));

        add(registerButtonPanel, BorderLayout.SOUTH);
        return registerButtonPanel;
    }


    private void onRegisteredForConference(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }

        showSuccess(String.format("You have successfully been registered to attend '%s'", conferenceDTO.getName()));
        HomePage homePage = new HomePage(attendee, eventMediator, navigator);
        navigator.navigateTo(homePage);
    }

    @Override
    protected void handleBackButton(ActionEvent e) {
        HomePage homePage = new HomePage(attendee, eventMediator, navigator);
        navigator.navigateTo(homePage);
    }


    // button handlers
    private void handleRegisterButton(ActionEvent e) {
        eventMediator.publishEvent(
                ConferenceEventObserver.class,
                observer -> observer.onRegisterForAConference(attendee.getId(), conferenceDTO.getId(), this::onRegisteredForConference)
        );
    }

}