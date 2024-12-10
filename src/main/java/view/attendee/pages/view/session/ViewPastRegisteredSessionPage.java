package view.attendee.pages.view.session;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.navigation.Navigator;
import view.event.UIEventMediator;
import view.attendee.pages.form.ProvideFeedbackPage;
import view.attendee.pages.form.ProvideSessionFeedbackPage;

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

        JButton provideFeedback = UIComponentFactory.createStyledButton("Provide Feedback");

        provideFeedback.addActionListener(this::handleProvideSessionFeedbackButton);

        footerPanel.add(provideFeedback);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 80));

        return footerPanel;
    }

    @Override
    protected void handleBackButton(ActionEvent e) {
        ViewPersonalSchedulePage viewPersonalSchedulePage = new ViewPersonalSchedulePage(userDTO, eventMediator, navigator, sessionDTO.getConferenceId());
        navigator.navigateTo(viewPersonalSchedulePage);
    }


    private void handleProvideSessionFeedbackButton(ActionEvent e) {
        if (!sessionDTO.getPresentAttendees().contains(userDTO.getId())) {
            showError("You did not attend this session and so you cannot provide feedback.");
            return;
        }

        ProvideFeedbackPage provideFeedbackPage = new ProvideSessionFeedbackPage(userDTO, eventMediator, navigator, sessionDTO);
        navigator.navigateTo(provideFeedbackPage);
    }
}
