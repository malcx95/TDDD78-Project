package se.liu.ida.malvi108.tddd78.project.listeners;

import se.liu.ida.malvi108.tddd78.project.gui.views.view_components.ViewComponent;

/**
 * Listens to a <code>ViewComponent</code> being clicked or double clicked.
 */
public interface ViewComponentListener
{
    /**
     * Invoked when a <code>ViewComponent</code> is clicked.
     */
    void viewComponentClicked();

    /**
     * Invoked when a <code>ViewComponent</code> is clicked.
     * @param source The <code>ViewComponent</code> that was clicked.
     */
    void viewComponentDoubleClicked(ViewComponent source);
}
