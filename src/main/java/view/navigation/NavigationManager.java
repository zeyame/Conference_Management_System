package view.navigation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

public class NavigationManager {
    private final JPanel mainContentPanel;
    private final Deque<Supplier<JPanel>> pageStack;
    private final CardLayout cardLayout;
    private JPanel currentPage;

    public NavigationManager(JPanel mainContentPanel) {
        this.mainContentPanel = mainContentPanel;
        this.pageStack = new ArrayDeque<>();
        this.cardLayout = (CardLayout) this.mainContentPanel.getLayout();
    }

    public void navigateTo(Supplier<JPanel> pageSupplier, boolean addToStack) {
        if (currentPage != null && addToStack) {
            pageStack.offer(() -> currentPage);
        }

        JPanel newPage = pageSupplier.get();

        // update the main content panel to show the new page
        String pageId = newPage.getClass().getSimpleName();
        mainContentPanel.add(newPage, pageId);
        cardLayout.show(mainContentPanel, pageId);

        setCurrentPage(newPage);
    }

    // overloaded navigateTo method with default behavior
    public void navigateTo(Supplier<JPanel> pageSupplier) {
        navigateTo(pageSupplier, true);
    }

    public void navigateBack() {
        if (canNavigateBack()) {

            // Get the previous page supplier from the stack
            Supplier<JPanel> previousPageSupplier = pageStack.pollLast();
            JPanel previousPage = previousPageSupplier.get();

            String pageId = previousPage.getClass().getSimpleName();
            mainContentPanel.add(previousPage, pageId);
            cardLayout.show(mainContentPanel, pageId);
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