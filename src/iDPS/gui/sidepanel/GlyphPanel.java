package iDPS.gui.sidepanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumMap;

import iDPS.Glyphs.Glyph;
import iDPS.controllers.GlyphsController;
import iDPS.gui.MainFrame;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class GlyphPanel extends JPanel implements ActionListener, PropertyChangeListener {
	
	private final MainFrame mainFrame;
	private final GlyphsController ctrl;
	
	private EnumMap<Glyph,GlyphBox> boxes;
	private JButton done;
	
	public GlyphPanel(MainFrame mainFrame) {
		super(new GridBagLayout());
		this.mainFrame = mainFrame;
		ctrl = mainFrame.getApp().getGlyphsController();
		ctrl.addPropertyChangeListener(this);
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		
		boxes = new EnumMap<Glyph,GlyphBox>(Glyph.class);
		int i = 0;
		for (Glyph g: Glyph.values()) {
			GlyphBox box = new GlyphBox(g);
			boxes.put(g, box);
			c.gridx = 1; c.gridy = i;
			add(box, c);
			i++;
		}
		
		c.gridx = 1; c.gridy = i;
		add(Box.createRigidArea(new Dimension(100,20)), c);
				
		done = new JButton("done");
		done.setFocusable(false);
		done.addActionListener(this);
		c.gridx = 1; c.gridy = i+1;
		c.anchor = GridBagConstraints.CENTER;
		add(done, c);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == done)
			mainFrame.getSideScroll().showDefaultPanel();
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == ctrl) {
			Glyph g = Glyph.valueOf(evt.getPropertyName());
			boxes.get(g).setSelected((Boolean) evt.getNewValue());
		}
	}

	private class GlyphBox extends JCheckBox implements ActionListener {
		
		private final Glyph glyph;
		
		public GlyphBox(Glyph g) {
			super(g.name());
			this.glyph = g;
			switch (glyph) {
			case Mut:
				setText("Mutilate");
				break;
			case HfB:
				setText("Hunger for Blood");
				break;
			case SS:
				setText("Sinister Strike");
				break;
			case KS:
				setText("Killing Spree");
				break;
			case SnD:
				setText("Slice and Dice");
				break;
			case AR:
				setText("Adrenaline Rush");
				break;
			case Rup:
				setText("Rupture");
				break;
			case Evi:
				setText("Eviscerate");
				break;
			case EA:
				setText("Expose Armor");
				break;
			case BF:
				setText("Blade Flurry");
				break;
			case SD:
				setText("Shadow Dance");
				break;
			}
			setSelected(ctrl.hasGlyph(glyph));
			setFocusable(false);
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			ctrl.setGlyph(glyph, isSelected());
			mainFrame.showStats();
		}
		
	}

}
