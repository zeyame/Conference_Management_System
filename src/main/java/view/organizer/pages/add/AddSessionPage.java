package view.organizer.pages.add;

import dto.SessionDTO;
import dto.UserDTO;
import exception.FormValidationException;
import util.ui.FormBuilder;
import util.ui.UIComponentFactory;
import util.validation.FormValidator;
import view.organizer.OrganizerObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class AddSessionPage extends AddPage {
    protected final String conferenceId;
    protected final String conferenceName;
    protected final List<UserDTO> speakers;

    // form fields
    protected final JTextField roomField;
    protected final JSpinner dateSpinner;
    protected final JSpinner startTimeSpinner;
    protected final JSpinner endTimeSpinner;
    protected JComboBox<UserDTO> speakerDropdown;

    public AddSessionPage(OrganizerObserver organizerObserver, String conferenceId, String conferenceName, List<UserDTO> speakers) {
        super(organizerObserver);
        this.conferenceId = conferenceId;
        this.conferenceName = conferenceName;
        this.speakers = speakers;

        // initialize components
        this.roomField = new JTextField(17);
        this.dateSpinner = UIComponentFactory.createDateSpinner();
        this.startTimeSpinner = UIComponentFactory.createTimeSpinner();
        this.endTimeSpinner = UIComponentFactory.createTimeSpinner();
        initializeJComboBox();

        // Set up event listeners
        setUpListeners();
    }

    @Override
    protected String getFormTitle() {
        return "Add a New Session";
    }

    @Override
    protected JPanel createForm(JLabel formHeaderLabel) {
        FormBuilder formBuilder = new FormBuilder(10);

        formBuilder.addFullWidthComponent(formHeaderLabel, 0)
                .addLabel("Name", 1, 0)
                .addComponent(nameField, 1, 1)
                .addLabel("Description", 2, 0)
                .addComponent(descriptionField, 2, 1)
                .addLabel("Speaker", 3, 0)
                .addComponent(speakerDropdown, 3, 1)
                .addLabel("Room", 4, 0)
                .addComponent(roomField, 4, 1)
                .addLabel("Date", 5, 0)
                .addComponent(dateSpinner, 5, 1)
                .addLabel("Start Time", 6, 0)
                .addComponent(startTimeSpinner, 6, 1)
                .addLabel("End Time", 7, 0)
                .addComponent(endTimeSpinner, 7, 1)
                .addFullWidthComponent(submitButton, 8);

        JPanel sessionFormPanel =  formBuilder.build();

        // adjust form size
        sessionFormPanel.setPreferredSize(new Dimension(300, 900));
        sessionFormPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 150, 0));

        return sessionFormPanel;
    }

    private void initializeJComboBox() {
        // initialize the JComboBox with UserDTO objects
        this.speakerDropdown = new JComboBox<>(speakers.toArray(new UserDTO[0]));

        // set custom renderer to display the name of each speaker
        this.speakerDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof UserDTO userDTO) {
                    setText(userDTO.getName());
                }
                return this;
            }
        });
    }

    protected void handleSubmitSessionDetails(ActionEvent e) {
        String sessionName = nameField.getText();
        String sessionDescription = descriptionField.getText();
        UserDTO speaker = (UserDTO) speakerDropdown.getSelectedItem();
        String room = roomField.getText();
        LocalDate date = extractLocalDate((Date) dateSpinner.getValue());
        LocalTime startTime = extractLocalTime((Date) startTimeSpinner.getValue());
        LocalTime endTime = extractLocalTime((Date) endTimeSpinner.getValue());

        try {
            FormValidator.validateSessionForm(sessionName, sessionDescription, speaker, room, date, startTime, endTime);
        } catch (FormValidationException ex) {
            JOptionPane.showMessageDialog(mainContentPanel, ex.getMessage(), "Form Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SessionDTO sessionDTO = SessionDTO.builder(conferenceId, speaker.getId(), speaker.getName(),
                sessionName,
                room, date, startTime, endTime)
                .setDescription(sessionDescription)
                .build();

        organizerObserver.onSubmitSessionFormRequest(sessionDTO, conferenceName);
    }

    protected LocalDate extractLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    protected LocalTime extractLocalTime(Date time) {
        return time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    private void setUpListeners() {
        submitButton.addActionListener(this::handleSubmitSessionDetails);
    }

}
