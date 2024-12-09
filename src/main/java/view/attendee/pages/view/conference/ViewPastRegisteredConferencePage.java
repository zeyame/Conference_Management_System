package view.attendee.pages.view.conference;

import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.pages.form.ProvideConferenceFeedbackPage;
import view.attendee.pages.form.ProvideFeedbackPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewPastRegisteredConferencePage extends ViewConferencePage {

    public ViewPastRegisteredConferencePage(UserDTO attendee, UIEventMediator eventMediator, Navigator navigator, String conferenceId) {
        super(attendee, eventMediator, navigator, conferenceId);

        createPageContent();
    }

    @Override
    protected JPanel getFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));

        JButton provideFeedbackButton = UIComponentFactory.createStyledButton("Provide Feedback");
        JButton requestCertificateButton = UIComponentFactory.createStyledButton("Request Certificate");

        provideFeedbackButton.addActionListener(this::handleProvideFeedbackButton);
        requestCertificateButton.addActionListener(this::handleRequestCertificateButton);

        footerPanel.add(provideFeedbackButton);
        footerPanel.add(requestCertificateButton);

        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        return footerPanel;
    }


    private void handleProvideFeedbackButton(ActionEvent e) {
        ProvideFeedbackPage provideFeedbackPage = new ProvideConferenceFeedbackPage(attendee, eventMediator, navigator, conferenceDTO);
        navigator.navigateTo(provideFeedbackPage);
    }

    private void handleRequestCertificateButton(ActionEvent e) {

    }
}
