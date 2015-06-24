package se.liu.ida.malvi108.tddd78.project.gui.miscellaneous;

/**
 * The type of click that a <code>ViewComponent</code> or <code>AppointmentIllustration</code>
 * recieves, which is sent as a parameter to it's <code>notifyListeners</code> method.
 *
 * @see se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations.AppointmentIllustration
 * @see se.liu.ida.malvi108.tddd78.project.gui.views.view_components.ViewComponent
 */
public enum ClickType
{
    /**
     * Sent as a parameter when a component is clicked.
     */
    CLICKED,
    /**
     * Sent as a parameter when a component is double-clicked.
     */
    DOUBLE_CLICKED
}
