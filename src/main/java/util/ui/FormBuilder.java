package util.ui;

import javax.swing.*;
import java.awt.*;

public class FormBuilder {
    private final JPanel panel;
    private final GridBagConstraints gbc;

    public FormBuilder(int padding) {
        panel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(padding, padding, padding, padding);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
    }

    public FormBuilder addLabel(String text, int row, int col) {
        JLabel label = new JLabel(text);
        gbc.gridx = col;
        gbc.gridy = row;
        panel.add(label, gbc);
        return this;
    }

    public FormBuilder addComponent(Component component, int row, int col) {
        gbc.gridx = col;
        gbc.gridy = row;
        panel.add(component, gbc);
        return this;
    }

    public FormBuilder addFullWidthComponent(Component component, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(component, gbc);
        gbc.gridwidth = 1;
        return this;
    }

    public JPanel build() {
        return panel;
    }
}

