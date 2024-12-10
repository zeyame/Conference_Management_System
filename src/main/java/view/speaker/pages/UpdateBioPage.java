package view.speaker.pages;

import dto.UserDTO;
import util.ui.FormBuilder;
import util.ui.UIComponentFactory;
import view.navigation.Navigator;
import view.event.UIEventMediator;
import view.observers.SessionEventObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UpdateBioPage extends JPanel {
    private final UserDTO speaker;
    private final UIEventMediator eventMediator;
    private final Navigator navigator;

    public UpdateBioPage(UserDTO speaker, UIEventMediator eventMediator, Navigator navigator) {
        this.speaker = speaker;
        this.eventMediator = eventMediator;
        this.navigator = navigator;

        setLayout(new BorderLayout());

        createPageContent();
    }

    private void createPageContent() {
        // header panel
        JPanel headerPanel = UIComponentFactory.createHeaderPanel("Update Your Bio", this::handleBackAction, 540);
        add(headerPanel, BorderLayout.NORTH);

        // Create the form
        FormBuilder formBuilder = new FormBuilder(10);
        JTextArea bioTextArea = new JTextArea(5, 30);
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);
        JScrollPane bioScrollPane = new JScrollPane(bioTextArea);

        JButton updateButton = new JButton("Update");

        // Add functionality to the button
        updateButton.addActionListener(e -> handleUpdateButton(e, bioTextArea));

        // Adding components to the form
        formBuilder
                .addLabel("Update Bio:", 0, 0)
                .addFullWidthComponent(bioScrollPane, 1)
                .addFullWidthComponent(updateButton, 2);

        JPanel formPanel = formBuilder.build();
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 180, 0));

        // Add the form to the center of the page
        add(formPanel, BorderLayout.CENTER);

    }

    private void handleUpdateButton(ActionEvent e, JTextArea bioTextArea) {
        String updatedBio = bioTextArea.getText().trim();
        if (updatedBio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bio cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            eventMediator.publishEvent(
                    SessionEventObserver.class,
                    observer -> observer.onUpdateSpeakerBioRequest(speaker.getId(), updatedBio, this::onBioUpdated)
            );
            JOptionPane.showMessageDialog(this, "Bio updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onBioUpdated(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showError(errorMessage);
        }
        showSuccess("You have successfully updated your bio!");
        HomePage homePage = new HomePage(speaker, eventMediator, navigator);
        navigator.navigateTo(homePage, false);
    }

    private void handleBackAction(ActionEvent e) {
        HomePage homePage = new HomePage(speaker, eventMediator, navigator);
        navigator.navigateTo(homePage);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }


    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

}
