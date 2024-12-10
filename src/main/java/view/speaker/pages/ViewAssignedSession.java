package view.speaker.pages;

import dto.UserDTO;
import view.navigation.Navigator;
import view.event.UIEventMediator;
import view.attendee.pages.view.session.ViewSessionPage;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewAssignedSession extends ViewSessionPage {

    public ViewAssignedSession(UserDTO speaker, String sessionId, UIEventMediator eventMediator, Navigator navigator) {
        super(speaker, sessionId, eventMediator, navigator);
    }

    @Override
    protected JPanel createFooterPanel() {
        return new JPanel();
    }

    @Override
    protected void handleBackButton(ActionEvent e) {
        HomePage homePage = new HomePage(userDTO, eventMediator, navigator);
        navigator.navigateTo(homePage);
    }
}
