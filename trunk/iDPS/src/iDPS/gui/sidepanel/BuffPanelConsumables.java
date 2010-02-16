package iDPS.gui.sidepanel;

import iDPS.BuffController;
import iDPS.BuffController.Consumable;
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


public class BuffPanelConsumables extends JPanel implements PropertyChangeListener {
	
	private MainFrame mainFrame;
	private BuffController controller;
	
	private EnumMap<Consumable,JCheckBox> boxes;
	
	public BuffPanelConsumables(MainFrame mainFrame, BuffController buffController) {
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
		boxes = new EnumMap<Consumable,JCheckBox>(Consumable.class);
		for (Consumable b: Consumable.values()) {
			name = b.name();
			tooltip = null;
			c.gridx = 0; c.gridy = y;
			enabled = true;
			switch (b) {
				case flask:
					name = "Flask: Attack Power";
					tooltip = "Flask of Endless Rage";
					break;
				case foodAgi:
					name = "Food: Agility";
					tooltip = "+40 AGI";
					break;
				case foodArp:
					name = "Food: Armor Penetration";
					tooltip = "+40 ARP";
					break;
				case foodAtp:
					name = "Food: Attack Power";
					tooltip = "+80 ATP";
					break;
				case foodExp:
					name = "Food: Expertise";
					tooltip = "+40 EXP";
					break;
				case foodHit:
					name = "Food: Hit";
					tooltip = "+40 HIT";
					break;
				case foodHst:
					name = "Food: Haste";
					tooltip = "+40 HST";
					break;
			}
			box = new ConsumableBox(b, name);
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
			if (evt.getPropertyName().equals("consumables")) {
				for (Consumable b: Consumable.values())
					handleChange(b);
			} else if (evt.getPropertyName().startsWith("consumable_")) {
				String s = evt.getPropertyName().substring(11);
				try {
					Consumable b = Consumable.valueOf(s);
					handleChange(b);
					return;
				} catch (IllegalArgumentException e) {}
			}
		}
	}
	
	private void handleChange(Consumable b) {
		boxes.get(b).setSelected(controller.hasConsumable(b));
		
		mainFrame.showStats();
	}
	
	private class ConsumableBox extends JCheckBox implements ActionListener {
		
		private Consumable buff;
		
		public ConsumableBox(Consumable buff, String name) {
			super(name);
			this.buff = buff;
			setFocusable(false);
			setSelected(controller.hasConsumable(buff));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			controller.setConsumable(buff, isSelected());
		}
		
	}

}
