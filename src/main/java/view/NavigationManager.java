package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class NavigationManager {
    private final JPanel mainContentPanel;
    private final Deque<JPanel> pageStack;
    private JPanel currentPage;

    public NavigationManager(JPanel mainContentPanel) {
        this.mainContentPanel = mainContentPanel;
        this.pageStack = new ArrayDeque<>();
    }

    public void navigateTo(JPanel newPage) {
        if (currentPage != null) {
            pageStack.offer(currentPage);
        }

        // Update the main content panel to show the new page
        mainContentPanel.add(newPage, newPage.getClass().getSimpleName()); // Use class name as the card name
        ((CardLayout) mainContentPanel.getLayout()).show(mainContentPanel, newPage.getClass().getSimpleName());

        setCurrentPage(newPage);
    }

    public void navigateBack() {
        if (canNavigateBack()) {
            // pop the current page from the stack and navigate back to the previous page
            JPanel previousPage = pageStack.pollLast();
            mainContentPanel.add(previousPage, previousPage.getClass().getSimpleName());
            ((CardLayout) mainContentPanel.getLayout()).show(mainContentPanel, previousPage.getClass().getSimpleName());
            setCurrentPage(previousPage);
        }
    }

    public void clearHistory() {
        pageStack.clear();
    }

    public boolean canNavigateBack() {
        return !this.pageStack.isEmpty();
    }

    private void setCurrentPage(JPanel page) {
        this.currentPage = page;
    }

}
