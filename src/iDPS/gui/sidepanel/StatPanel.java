package iDPS.gui.sidepanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class StatPanel extends JPanel {
	
	PieValue[] slices;
	
	StatPanel() {
		slices = new PieValue[4];
    slices[0] = new PieValue(25, Color.red);
    slices[1] = new PieValue(33, Color.green);
    slices[2] = new PieValue(21, Color.yellow);
    slices[3] = new PieValue(15, Color.blue);
	}
	
	public void paint(Graphics g) {
    Rectangle b = this.getBounds();
    g.drawRect(b.x, b.y, b.width-1, b.height-1);
    b.x += 10; b.y += 10;
    b.height -= 21; b.width -= 21;
    Graphics2D g2 = (Graphics2D) g;
    drawPie(g2, b, slices);
	}
	
	public void drawPie(Graphics2D g, Rectangle area, PieValue[] slices) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	    // Get total value of all slices
	    double total = 0.0D;
	    for (int i=0; i<slices.length; i++) {
	        total += slices[i].value;
	    }

	    double curValue = 0D;
	    int startAngle = 0, endAngle = 0;
	    for (int i=0; i<slices.length; i++) {
	    	curValue += slices[i].value;
	    	if (i != 0)
	    		startAngle = endAngle;
	    	endAngle = (int) Math.round(curValue * 360 / total);
	      
	      int arcAngle = endAngle - startAngle;

	      g.setColor(slices[i].color);
	      g.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);

	        
	    }
	}
	
	private class PieValue {
    double value;
    Color color;

    public PieValue(double value, Color color) {
        this.value = value;
        this.color = color;
    }
}

}
