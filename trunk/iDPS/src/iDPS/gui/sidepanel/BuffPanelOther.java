package iDPS.gui.sidepanel;

import iDPS.BuffController;
import iDPS.BuffController.Other;
import iDPS.gui.MainFrame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumMap;

import javax.swing.JCheckBox;
import javax.swing.JPanel;


public class BuffPanelOther extends JPanel implements PropertyChangeListener {
	
	private MainFrame mainFrame;
	private BuffController controller;
	
	private EnumMap<Other,JCheckBox> boxes;
	
	public BuffPanelOther(MainFrame mainFrame, BuffController buffController) {
		super(new GridBagLayout());
		this.mainFrame = mainFrame;
		controller = buffController;
		controller.addPropertyChangeListener(this);
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 0;
		c.insets = new Insets(10,0,0,0);
		
		String name, tooltip;
		JCheckBox box;
		boolean enabled;
		int y = 0;
		boxes = new EnumMap<Other,JCheckBox>(Other.class);
		for (Other b: Other.values()) {
			name = b.name();
			tooltip = null;
			c.gridx = 0; c.gridy = y;
			enabled = true;
			switch (b) {
				case bloodlust:
					name = "Temporary Haste Raidbuff";
					tooltip = "Bloodlust, Heroism";
					break;
				case hysteria:
					name = "Hysteria on Cooldown";
					tooltip = "Bloodlust, Heroism";
					break;
				case tott:
					name = "Tricks of the Trade";
					break;
				case tottglyphed:
					name = "Glyphed";
					c.insets = new Insets(0,20,0,0);
					enabled = controller.hasOther(Other.tott);
					break;
				case totttalented:
					name = "Talented";
					c.insets = new Insets(0,20,0,0);
					enabled = controller.hasOther(Other.tott);
					break;
			}
			box = new OtherBox(b, name);
			box.setEnabled(enabled);
			box.setToolTipText(tooltip);
			boxes.put(b, box);
			add(box, c);
			c.insets = new Insets(0,0,0,0);
			y++;
		}
		
		c.gridx = 0; c.gridy = y; c.weighty = 1;
		add(new JPanel(), c);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof BuffController) {
			if (evt.getPropertyName().equals("other")) {
				for (Other b: Other.values())
					handleChange(b);
			} else if (evt.getPropertyName().startsWith("other_")) {
				String s = evt.getPropertyName().substring(6);
				try {
					Other b = Other.valueOf(s);
					handleChange(b);
					return;
				} catch (IllegalArgumentException e) {}
			}
		}
	}
	
	private void handleChange(Other b) {
		boxes.get(b).setSelected(controller.hasOther(b));
		switch (b) {
		case tott:
			boxes.get(Other.tottglyphed).setEnabled(controller.hasOther(b));
			boxes.get(Other.totttalented).setEnabled(controller.hasOther(b));
			break;
		}
	}
	
	private class OtherBox extends JCheckBox implements ActionListener {
		
		private Other buff;
		
		public OtherBox(Other buff, String name) {
			super(name);
			this.buff = buff;
			setFocusable(false);
			setSelected(controller.hasOther(buff));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			controller.setOther(buff, isSelected());
			mainFrame.showStats();
		}
		
	}

}
