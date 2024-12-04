package view.attendee.pages.view.session;

import dto.SessionDTO;
import dto.UserDTO;
import util.ui.UIComponentFactory;
import view.attendee.Navigator;
import view.attendee.UIEventMediator;
import view.attendee.observers.SessionEventObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class ViewSessionPage extends JPanel {
    // dependencies
    protected final UserDTO attendee;
    protected final String sessionId;
    protected final UIEventMediator eventMediator;
    protected final Navigator navigator;

    // fetched data
    protected SessionDTO sessionDTO;

    public ViewSessionPage(UserDTO attendee, String sessionId, UIEventMediator eventMediator, Navigator navigator) {
        this.attendee = attendee;
        this.sessionId = sessionId;
        this.eventMediator = eventMediator;
        this.navigator = navigator;

        fetchSession();

        setLayout(new BorderLayout());

        createPageContent();
    }

    protected void createPageContent() {
        removeAll();

        // header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel(sessionDTO.getName(), this::handleBackButton, 500);
        add(headerPanel, BorderLayout.NORTH);

        // details panel
        JPanel sessionDetails = UIComponentFactory.createSessionDetailsPanel(sessionDTO);
        sessionDetails.setBorder(BorderFactory.createEmptyBorder(0, 190, 40, 0));
        add(sessionDetails, BorderLayout.CENTER);

        // footer
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    protected abstract JPanel createFooterPanel();


    // Joption Pane helpers
    protected void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }


    // data fetchers
    private void fetchSession() {
        eventMediator.publishEvent(
                SessionEventObserver.class,
                observer -> observer.onGetSession(sessionId, this::onSessionFetched)
        );
    }

    // callback responders
    private void onSessionFetched(SessionDTO sessionDTO, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
            return;
        }
        this.sessionDTO = sessionDTO;
    }


    protected void handleBackButton(ActionEvent e) {
        ViewSessionsPage viewSessionsPage = new ViewSessionsPage(attendee, sessionDTO.getConferenceId(), eventMediator, navigator);
        navigator.navigateTo(viewSessionsPage);
    }


}
