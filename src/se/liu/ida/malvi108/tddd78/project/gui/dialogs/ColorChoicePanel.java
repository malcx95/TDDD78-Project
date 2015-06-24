package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

import se.liu.ida.malvi108.tddd78.project.appointments.StandardAppointment;
import se.liu.ida.malvi108.tddd78.project.gui.dialogs.CalendarDialog.ColorChoice;
import se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations.StandardAppRectangle;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.TimePoint;
import se.liu.ida.malvi108.tddd78.project.time.TimeSpan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The color chooser panel for the <code>CalendarDialog</code>, which contains a JCheckbox,
 * from which a list of default colors can be chosen as well as a <code>StandardAppRectangle</code>
 * as a preview for the selected color. The JCheckbox also contains a custom option which shows
 * a JColorChooser to choose a custom color.
 */
public final class ColorChoicePanel extends JPanel implements ActionListener
{
    private static final int EXAMPLE_START_HOUR = 12;
    private static final int EXAMPLE_END_HOUR = 13;
    private StandardAppRectangle preview;
    private final static TimePoint PREVIEW_START = new TimePoint(EXAMPLE_START_HOUR, 0);
    private final static TimePoint PREVIEW_END = new TimePoint(EXAMPLE_END_HOUR, 0);
    private Color selectedColor;
    private final static int PREVIEW_HEIGHT = 100;
    private final static int PREVIEW_WIDTH = 220;
    private final static int THIS_HEIGHT = 150;
    private final static int THIS_WIDTH = 150;
    private JComboBox<ColorChoice> colorChooser;

    public ColorChoicePanel(JComboBox<ColorChoice> colorChooser, Color initialColor){
	super(new BorderLayout(0, 10));
	this.colorChooser = colorChooser;
	add(colorChooser, BorderLayout.PAGE_START);
	selectedColor = initialColor;
	preview = new StandardAppRectangle(createPreviewAppointment(), selectedColor);
	preview.setSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
	this.add(preview, BorderLayout.CENTER);
	this.colorChooser.addActionListener(this);
	setPreferredSize(new Dimension(THIS_WIDTH, THIS_HEIGHT));
    }

    /**
     * Creates an appointment for the preview app-rectangle to show.
     */
    private StandardAppointment createPreviewAppointment(){
	return new StandardAppointment(new TimeSpan(PREVIEW_START, PREVIEW_END), Date.getToday(), "Aktivitet", null, null, null);
    }
    /**
     * Invoked when an action occurs.
     */
    @Override public void actionPerformed(final ActionEvent e) {
	ColorChoice choice = (ColorChoice) colorChooser.getSelectedItem();
	if (choice.equals(ColorChoice.CUSTOM)){ // if the user wants a custom mainColor
	    JColorChooser jcc = new JColorChooser();
	    PreviewPanel previewPanel = new PreviewPanel(jcc.getColor());
	    jcc.setPreviewPanel(previewPanel);
	    JOptionPane.showMessageDialog(this, jcc, "Anpassad färg",
					  JOptionPane.PLAIN_MESSAGE, null);
	    selectedColor = jcc.getColor();
	} else {
	    selectedColor = choice.getColor();
	}
	preview.setForeground(selectedColor);
    }

    private final class PreviewPanel extends JPanel {
	private PreviewPanel(Color initColor) {
    	    StandardAppRectangle previewRect = new StandardAppRectangle(createPreviewAppointment(), initColor);
	    this.add(previewRect);
	}
	@Override public void setForeground(final Color fg) {
    	    super.setForeground(fg);
	    for (Component component : getComponents()) {
		component.setForeground(fg);
	    }
	}
    }

    public Color getSelectedColor() {
	return selectedColor;
    }
}