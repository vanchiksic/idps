package iDPS.gui;

import iDPS.gear.Rateable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

class RatingPanel extends JPanel {
	
	private float dps;
	private float fill;
	
	public RatingPanel(Rateable item, float maxDPS) {
		if (item != null)
			dps = item.getComparedDPS();
		fill = dps/maxDPS;
		setSize(new Dimension(300,10));
		setPreferredSize(new Dimension(300,10));
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, (int)(299*fill), 9);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, 299, 9);
	}
	
}
