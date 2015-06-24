package se.liu.ida.malvi108.tddd78.project.main;
import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class.
 *
 * @see MainFrame
 */
public final class PlanItApplication
{
    private PlanItApplication(){}
    private final static Logger LOGGER = Logger.getLogger(PlanItApplication.class.getName());

    public static void main(String[] args) {
        LOGGER.log(Level.INFO, "Starting PlanIt...");
        JFrame mainFrame = new MainFrame();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	mainFrame.setVisible(true);
        LOGGER.log(Level.INFO, "PlanIt started.");
    }
}