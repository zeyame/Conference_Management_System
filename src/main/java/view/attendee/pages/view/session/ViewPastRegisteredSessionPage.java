package view.attendee.pages.view.session;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewPastRegisteredSessionPage extends ViewSessionPage {

    public ViewPastRegisteredSessionPage(UserDTO attendee, String sessionId, UIEventMediator eventMediator, Navigator navigator) {
        super(attendee, sessionId, eventMediator, navigator);
    }

    @Override
    protected JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));

        JButton provideSessionFeedbackButton = UIComponentFactory.createStyledButton("Provide Session Feedback");
        JButton provideSpeakerFeedbackButton = UIComponentFactory.createStyledButton("Provide Speaker Feedback");

        provideSessionFeedbackButton.addActionListener(this::handleProvideSessionFeedbackButton);
        provideSpeakerFeedbackButton.addActionListener(this::handleProvideSpeakerFeedbackButton);

        footerPanel.add(provideSessionFeedbackButton);
        footerPanel.add(provideSpeakerFeedbackButton);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 80));

        return footerPanel;
    }

    @Override
    protected void handleBackButton(ActionEvent e) {
        ViewPersonalSchedulePage viewPersonalSchedulePage = new ViewPersonalSchedulePage(attendee, eventMediator, navigator, sessionDTO.getConferenceId());
        navigator.navigateTo(viewPersonalSchedulePage);
    }

    private void handleProvideSessionFeedbackButton(ActionEvent e) {}

    private void handleProvideSpeakerFeedbackButton(ActionEvent e) {}
}
