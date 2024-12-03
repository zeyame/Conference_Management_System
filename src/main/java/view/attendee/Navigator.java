package view.attendee;

import javax.swing.*;

public interface Navigator {
    void navigateTo(JPanel page);
    void navigateBack();
    boolean canNavigateBack();
}

