package iDPS.gui;

import iDPS.BuffCentral;
import iDPS.BuffCentral.Buff;
import iDPS.BuffCentral.Debuff;

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
import javax.swing.JSeparator;


public class BuffPanel extends JPanel implements PropertyChangeListener {
	
	BuffCentral central;
	
	private EnumMap<Buff,JCheckBox> buffBoxes;
	private EnumMap<Debuff,JCheckBox> debuffBoxes;
	
	public BuffPanel(BuffCentral buffCentral) {
		super(new GridBagLayout());
		
		central = buffCentral;
		central.addPropertyChangeListener(this);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		String name;
		JCheckBox box;
		int y = 0;
		buffBoxes = new EnumMap<Buff,JCheckBox>(Buff.class);
		for (Buff b: Buff.values()) {
			name = b.name();
			switch (b) {
				case statsAdditive:
					name = "Additive Stats Buff";
					break;
			}
			box = new BuffBox(b, name);
			buffBoxes.put(b, box);
			c.gridx = 0; c.gridy = y; c.insets = new Insets(0,0,0,0);
			add(box, c);
			y++;
		}
		debuffBoxes = new EnumMap<Debuff,JCheckBox>(Debuff.class);
		for (Debuff b: Debuff.values()) {
			name = b.name();
			switch (b) {
				case armorMajor:
					name = "Major Armor Debuff";
					break;
			}
			box = new DebuffBox(b, name);
			debuffBoxes.put(b, box);
			c.gridx = 0; c.gridy = y; c.insets = new Insets(0,0,0,0);
			add(box, c);
			y++;
		}
		

		
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		try {
			Buff b = Buff.valueOf(evt.getPropertyName());
			handleBuffChange(b);
		} catch (IllegalArgumentException e) {
			
		} finally {
			try {
				Debuff db = Debuff.valueOf(evt.getPropertyName());
				handleDebuffChange(db);
			} catch (IllegalArgumentException e) {
				
			}
		}
	}
	
	private void handleBuffChange(Buff b) {
		buffBoxes.get(b).setSelected(central.hasBuff(b));
	}
	
	private void handleDebuffChange(Debuff b) {
		debuffBoxes.get(b).setSelected(central.hasDebuff(b));
		if (b == Debuff.armorMajor)
			debuffBoxes.get(Debuff.armorMajorMaintain).setEnabled(central.hasDebuff(b));
	}
	
	private class BuffBox extends JCheckBox implements ActionListener {
		
		private Buff buff;
		
		public BuffBox(Buff buff, String name) {
			super(name);
			this.buff = buff;
			setFocusable(false);
			setSelected(central.hasBuff(buff));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			central.setBuff(buff, isSelected());
		}
		
	}
	
	private class DebuffBox extends JCheckBox implements ActionListener {
		
		private Debuff debuff;
		
		public DebuffBox(Debuff debuff, String name) {
			super(name);
			this.debuff = debuff;
			setFocusable(false);
			setSelected(central.hasDebuff(debuff));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			central.setDebuff(debuff, isSelected());
		}
		
	}

}
