package view.attendee.pages.view.session;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewRegisteredSessionPage extends ViewSessionPage {
    private JButton leaveSession;
    private JButton provideSessionFeedbackButton;
    private JButton provideSpeakerFeedbackButton;

    public ViewRegisteredSessionPage(UserDTO attendee, String sessionId, UIEventMediator eventMediator, Navigator navigator) {
        super(attendee, sessionId, eventMediator, navigator);
        setUpListeners();
    }

    @Override
    protected void createPageContent() {
        super.createPageContent();

        this.leaveSession = UIComponentFactory.createStyledButton("Leave Session");
        this.provideSessionFeedbackButton = UIComponentFactory.createStyledButton("Provide Session Feedback");
        this.provideSpeakerFeedbackButton = UIComponentFactory.createStyledButton("Provide Speaker Feedback");

        // add footer panel
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        footerPanel.add(provideSessionFeedbackButton);
        footerPanel.add(provideSpeakerFeedbackButton);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        return footerPanel;
    }


    // button handlers
    private void setUpListeners() {
        this.leaveSession.addActionListener(this::handleLeaveSessionButton);
        this.provideSessionFeedbackButton.addActionListener(this::handleProvideSessionFeedbackButton);
        this.provideSpeakerFeedbackButton.addActionListener(this::handleProvideSpeakerFeedbackButton);
    }

    private void handleLeaveSessionButton(ActionEvent e) {

    }

    private void handleProvideSessionFeedbackButton(ActionEvent e) {

    }

    private void handleProvideSpeakerFeedbackButton(ActionEvent e) {

    }


}
