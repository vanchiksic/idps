package iDPS.gui.sidepanel;

import iDPS.controllers.BuffController;
import iDPS.controllers.BuffController.Buff;
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


public class BuffPanelBuffs extends JPanel implements PropertyChangeListener {
	
	private MainFrame mainFrame;
	private BuffController controller;
	
	private EnumMap<Buff,JCheckBox> boxes;
	
	public BuffPanelBuffs(MainFrame mainFrame, BuffController buffController) {
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
		int y = 1;
		boxes = new EnumMap<Buff,JCheckBox>(Buff.class);
		for (Buff b: Buff.values()) {
			name = b.name();
			tooltip = null;
			c.gridx = 0; c.gridy = y;
			enabled = true;
			switch (b) {
				case attackPower:
					name = "Attack Power Buff";
					tooltip = "Blessing of Might, Battle Shout";
					break;
				case attackPowerImp:
					name = "Imp. Attack Power Buff";
					tooltip = "Improved BoM, Imp. Battle Shout";
					c.insets = new Insets(0,20,0,0);
					enabled = controller.hasBuff(Buff.attackPower);
					break;
				case attackPowerMult:
					name = "10% Attack Power Buff";
					tooltip = "Unleashed Rage";
					break;
				case damage:
					name = "3% Damage Buff";
					tooltip = "Ferocious Inspiration";
					break;
				case meleHaste:
					name = "16% Mele Haste";
					tooltip = "Windfury Totem";
					break;
				case meleHasteImp:
					name = "20% Mele Haste";
					tooltip = "Imp. Windfury Totem, Imp. Icy Talons";
					c.insets = new Insets(0,20,0,0);
					enabled = controller.hasBuff(Buff.meleHaste);
					break;
				case spellCrit:
					name = "5% Spell Crit Buff";
					tooltip = "Moonkin Aura, Elemental Oath";
					break;
				case physicalCrit:
					name = "5% Physical Crit Buff";
					tooltip = "Leader of the Pact, Rampage";
					break;
				case statsAdditive:
					name = "Additive Stats Buff";
					tooltip = "Mark of the Wild";
					break;
				case statsAdditiveImp:
					name = "Imp. Additive Stats Buff";
					tooltip = "Imp. Mark of the Wild";
					c.insets = new Insets(0,20,0,0);
					enabled = controller.hasBuff(Buff.statsAdditive);
					break;
				case statsMultiplicative:
					name = "10% Stats Buff";
					tooltip = "Blessing of Kings";
					break;
				case agilityStrength:
					name = "Agility/Strength Buff";
					tooltip = "SoE Totem, Horn of the Winter";
					break;
				case agilityStrengthImp:
					name = "Imp. Agility/Strength Buff";
					tooltip = "Improved SoE Totem";
					c.insets = new Insets(0,20,0,0);
					enabled = controller.hasBuff(Buff.agilityStrength);
					break;
				case partyHit:
					name = "1% Hit PartyBuff";
					tooltip = "Heroic Presence";
					break;
			}
			box = new BuffBox(b, name);
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
			if (evt.getPropertyName().equals("buffs")) {
				for (Buff b: Buff.values())
					handleChange(b);
			} else if (evt.getPropertyName().startsWith("buff_")) {
				String s = evt.getPropertyName().substring(5);
				try {
					Buff b = Buff.valueOf(s);
					handleChange(b);
					return;
				} catch (IllegalArgumentException e) {}
			}
		}
	}
	
	private void handleChange(Buff b) {
		boxes.get(b).setSelected(controller.hasBuff(b));
		switch (b) {
		case attackPower:
			boxes.get(Buff.attackPowerImp).setEnabled(controller.hasBuff(b));
			break;
		case meleHaste:
			boxes.get(Buff.meleHasteImp).setEnabled(controller.hasBuff(b));
			break;
		case statsAdditive:
			boxes.get(Buff.statsAdditiveImp).setEnabled(controller.hasBuff(b));
			break;
		case agilityStrength:
			boxes.get(Buff.agilityStrengthImp).setEnabled(controller.hasBuff(b));
			break;
		}
	}
	
	private class BuffBox extends JCheckBox implements ActionListener {
		
		private Buff buff;
		
		public BuffBox(Buff buff, String name) {
			super(name);
			this.buff = buff;
			setFocusable(false);
			setSelected(controller.hasBuff(buff));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			controller.setBuff(buff, isSelected());
			mainFrame.showStats();
		}
		
	}

}
