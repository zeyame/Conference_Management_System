package view.organizer.pages;

import dto.ConferenceFormDTO;
import dto.UserDTO;
import exception.FormValidationException;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;

public class AddConferencePage {
    private final UserDTO userDTO;
    private final OrganizerObserver organizerObserver;

    // main panel
    private final JPanel mainContentPanel;

    // conference form fields
    private final JTextField nameField;
    private final JTextField descriptionField;
    private final JSpinner startDateTimeSpinner;
    private final JSpinner endDateTimeSpinner;
    private final JButton submitButton;

    public AddConferencePage(UserDTO userDTO, OrganizerObserver organizerObserver) {
        this.userDTO = userDTO;
        this.organizerObserver = organizerObserver;

        // initialize the components
        this.mainContentPanel = new JPanel();
        this.nameField = new JTextField(17);
        this.descriptionField = new JTextField(17);
        this.startDateTimeSpinner = createDateTimeSpinner();
        this.endDateTimeSpinner = createDateTimeSpinner();
        this.submitButton = new JButton("Submit");

        // set up event listeners for the page
        setUpListeners();
    }

    public JPanel createPageContent() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));

        // Add the conference form directly, no need for additional header
        JPanel conferenceForm = createConferenceForm();
        mainContentPanel.add(conferenceForm);

        // Optionally, add space at the bottom if needed to create separation from the next section
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        return mainContentPanel;
    }

    private JPanel createConferenceForm() {
        JPanel conferenceFormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 20, 10);  // Add spacing between fields

        // header label centered
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel headerLabel = new JLabel("Add a New Conference");
        headerLabel.setFont(new Font("Sans serif", Font.BOLD, 20));
        conferenceFormPanel.add(headerLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;

        // name label and field
        gbc.gridx = 0; gbc.gridy = 1;
        conferenceFormPanel.add(new JLabel("Name"), gbc);

        gbc.gridx = 1;
        conferenceFormPanel.add(nameField, gbc);

        // description label and field
        gbc.gridx = 0; gbc.gridy = 2;
        conferenceFormPanel.add(new JLabel("Description"), gbc);

        gbc.gridx = 1;
        conferenceFormPanel.add(descriptionField, gbc);

        // start date-time label and field
        gbc.gridx = 0; gbc.gridy = 3;
        conferenceFormPanel.add(new JLabel("Start Date and Time"), gbc);

        gbc.gridx = 1;
        conferenceFormPanel.add(startDateTimeSpinner, gbc);

        // end date-time label and field
        gbc.gridx = 0; gbc.gridy = 4;
        conferenceFormPanel.add(new JLabel("End Date and Time"), gbc);

        gbc.gridx = 1;
        conferenceFormPanel.add(endDateTimeSpinner, gbc);

        // submit button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        submitButton.setFocusPainted(false);
        conferenceFormPanel.add(submitButton, gbc);

        // Make the form taller by setting preferred size
        conferenceFormPanel.setPreferredSize(new Dimension(300, 800));  // Adjust height and width
        conferenceFormPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 150, 0));

        return conferenceFormPanel;
    }


    private JSpinner createDateTimeSpinner() {
        JSpinner dateTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateTimeSpinner, "MM/dd/yyyy HH:mm");
        dateTimeSpinner.setEditor(dateEditor);
        dateTimeSpinner.setPreferredSize(new Dimension(150, 30));
        return dateTimeSpinner;
    }

    private void setUpListeners() {
        submitButton.addActionListener(this::handleSubmitConferenceDetails);
    }

    private void handleSubmitConferenceDetails(ActionEvent e) {
        String conferenceName = nameField.getText();
        String conferenceDescription = descriptionField.getText();
        Date startDate = (Date) startDateTimeSpinner.getValue();
        Date endDate = (Date) endDateTimeSpinner.getValue();

        try {
            validateConferenceForm(conferenceName, conferenceDescription, startDate, endDate);
        } catch (FormValidationException ex) {
            JOptionPane.showMessageDialog(mainContentPanel, ex.getMessage(), "Form Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ConferenceFormDTO conferenceFormDTO = new ConferenceFormDTO(userDTO.getId(), conferenceName, conferenceDescription, startDate, endDate);

        // publish event to organizer ui that user wants to submit the conference form
        organizerObserver.onSubmitConferenceFormRequest(conferenceFormDTO);
    }

    private void validateConferenceForm(String conferenceName, String conferenceDescription, Date startDate, Date endDate) {

        if (conferenceName.isEmpty() || conferenceDescription.isEmpty() || startDate == null || endDate == null) {
            throw new FormValidationException("All fields must be filled out.");
        }

        if (startDate.equals(endDate) || startDate.after(endDate)) {
            throw new FormValidationException("Start date and time must be before end date and time.");
        }
    }
}
