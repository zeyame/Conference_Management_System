package view.attendee;

import javax.swing.*;

public interface Navigator {
    void navigateTo(JPanel page);
    void navigateTo(JPanel page, boolean addToStack);
    void navigateBack();
    boolean canNavigateBack();
}

