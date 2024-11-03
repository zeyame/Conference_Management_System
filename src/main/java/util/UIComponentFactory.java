package util;

import javax.swing.*;
import java.awt.*;

public class UIComponentFactory {

    public static JPanel getRoleSelectionPanel() {
        JPanel roleSelectionPanel = new JPanel();
        roleSelectionPanel.setLayout(new BoxLayout(roleSelectionPanel, BoxLayout.Y_AXIS));

        // role selection subtitle
        JPanel roleLabelPanel = new JPanel();
        roleLabelPanel.setLayout(new BoxLayout(roleLabelPanel, BoxLayout.X_AXIS));
        JLabel roleLabel = new JLabel("Choose your role");

        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align

        roleLabelPanel.add(roleLabel);

        // radio buttons
        JPanel radioButtonPanel = getRadioButtonPanel();

        roleSelectionPanel.add(roleLabelPanel);
        roleSelectionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        roleSelectionPanel.add(radioButtonPanel);
        roleSelectionPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        return roleSelectionPanel;
    }

    private static JPanel getRadioButtonPanel() {
        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.X_AXIS));

        // create radio buttons
        JRadioButton organizerRadioButton = new JRadioButton("Organizer");
        JRadioButton attendeeRadioButton = new JRadioButton("Attendee");
        JRadioButton speakerRadioButton = new JRadioButton("Speaker");

        // remove the focus indicator around the buttons
        organizerRadioButton.setFocusPainted(false);
        attendeeRadioButton.setFocusPainted(false);
        speakerRadioButton.setFocusPainted(false);

        // group the radio buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(organizerRadioButton);
        buttonGroup.add(attendeeRadioButton);
        buttonGroup.add(speakerRadioButton);

        radioButtonPanel.add(organizerRadioButton);
        radioButtonPanel.add(attendeeRadioButton);
        radioButtonPanel.add(speakerRadioButton);
        return radioButtonPanel;
    }

}
