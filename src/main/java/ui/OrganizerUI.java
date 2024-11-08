package ui;

import dto.UserDTO;

import javax.swing.*;

public class OrganizerUI extends JFrame implements UserUI {
    private final UserDTO userDTO;
    public OrganizerUI(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Override
    public void display() {
        setTitle("Hello " + userDTO.getName());
        setVisible(true);
    }
}
