package se.liu.ida.malvi108.tddd78.project.gui.miscellaneous;

import javax.swing.JComponent;
import java.awt.*;

/**
 * A graphic, colored circle.
 */
public class GraphicCircle extends JComponent
{
    private Color color;
    private int circleSize;
    /**
     * The number of pixels it should be adjusted down.
     */
    private int positionY;

    public GraphicCircle(Color color, int size){
	circleSize = size;
	this.color = color;
	this.setPreferredSize(new Dimension(circleSize + 1, circleSize + 1));
	positionY = 0;
    }

    @Override protected void paintComponent(final Graphics g) {
	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g;
	g2d.setColor(color);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			     RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.fillOval(0, circleSize /2 + positionY, circleSize, circleSize);
	g2d.setColor(color.darker());
	g2d.drawOval(0, circleSize /2 + positionY, circleSize, circleSize);
    }

    public void setPositionY(final int positionY) {
	this.positionY = positionY;
    }

    @Override public void setForeground(final Color fg) {
	color = fg;
	super.setForeground(fg);
    }
}
