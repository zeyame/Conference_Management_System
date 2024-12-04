package view.attendee.pages.view.session;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewUnregisteredSessionPage extends ViewSessionPage {
    // dependencies
    private JButton registerButton;

    public ViewUnregisteredSessionPage(UserDTO attendee, String sessionId, UIEventMediator eventMediator, Navigator navigator) {
        super(attendee, sessionId, eventMediator, navigator);
        setUpListeners();
    }

    @Override
    protected void createPageContent() {
        super.createPageContent();

        // register button
        this.registerButton = UIComponentFactory.createStyledButton("Register");
        JPanel buttonPanel = UIComponentFactory.createButtonPanel(registerButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 100));
        add(buttonPanel, BorderLayout.SOUTH);
    }


    // button handlers
    private void setUpListeners() {
        this.registerButton.addActionListener(this::handleRegisterButton);
    }

    private void handleRegisterButton(ActionEvent e) {

    }

}
