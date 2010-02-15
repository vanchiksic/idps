package iDPS.gui;

import iDPS.BuffController;
import iDPS.BuffController.Buff;
import iDPS.BuffController.Debuff;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumMap;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;


public class BuffPanel extends JPanel implements PropertyChangeListener {
	
	private MainFrame mainFrame;
	private BuffController controller;
	
	private EnumMap<Buff,JCheckBox> buffBoxes;
	private EnumMap<Debuff,JCheckBox> debuffBoxes;
	
	public BuffPanel(MainFrame mainFrame, BuffController buffController) {
		super(new GridBagLayout());
		this.mainFrame = mainFrame;
		controller = buffController;
		controller.addPropertyChangeListener(this);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		JLabel label = new JLabel("Buffs");
		Font f = label.getFont().deriveFont(17F);
		label.setFont(f);
		c.gridx = 0; c.gridy = 0; c.insets = new Insets(5,5,2,0);
		add(label, c);
		
		String name, tooltip;
		JCheckBox box;
		boolean enabled;
		int y = 1;
		buffBoxes = new EnumMap<Buff,JCheckBox>(Buff.class);
		for (Buff b: Buff.values()) {
			name = b.name();
			tooltip = null;
			c.gridx = 0; c.gridy = y; c.insets = new Insets(0,0,0,0);
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
				case food:
					name = "Food";
					break;
				case foodAgi:
					name = "Agility";
					tooltip = "+40 AGI";
					c.insets = new Insets(0,20,0,0);
					enabled = controller.hasBuff(Buff.food);
					break;
				case foodArp:
					name = "Armor Penetration";
					tooltip = "+40 ARP";
					c.insets = new Insets(0,20,0,0);
					enabled = controller.hasBuff(Buff.food);
					break;
				case foodAtp:
					name = "Attack Power";
					tooltip = "+80 ATP";
					c.insets = new Insets(0,20,0,0);
					enabled = controller.hasBuff(Buff.food);
					break;
				case foodExp:
					name = "Expertise";
					tooltip = "+40 EXP";
					c.insets = new Insets(0,20,0,0);
					enabled = controller.hasBuff(Buff.food);
					break;
				case foodHst:
					name = "Haste";
					tooltip = "+40 HST";
					c.insets = new Insets(0,20,0,0);
					enabled = controller.hasBuff(Buff.food);
					break;
			}
			box = new BuffBox(b, name);
			box.setEnabled(enabled);
			box.setToolTipText(tooltip);
			buffBoxes.put(b, box);
			add(box, c);
			y++;
		}
		
		c.gridx = 0; c.gridy = y; c.insets = new Insets(5,0,0,0);
		add(new JSeparator(), c);
		label = new JLabel("Debuffs");
		label.setFont(f);
		c.gridx = 0; c.gridy = y+1; c.insets = new Insets(5,5,2,0);
		add(label, c);
		y+=2;
		
		debuffBoxes = new EnumMap<Debuff,JCheckBox>(Debuff.class);
		for (Debuff b: Debuff.values()) {
			name = b.name();
			tooltip = null;
			c.gridx = 0; c.gridy = y; c.insets = new Insets(0,0,0,0);
			enabled = true;
			switch (b) {
				case armorMajor:
					name = "Major Armor Debuff";
					tooltip = "Sunder Armor, Expose Armor";
					break;
				case armorMajorMaintain:
					name = "Myself maintaining EA";
					c.insets = new Insets(0,20,0,0);
					enabled = controller.hasDebuff(Debuff.armorMajor);
					break;
				case armorMinor:
					name = "Minor Armor Debuff";
					tooltip = "Faerie Fire";
					break;
				case crit:
					name = "3% Crit Debuff";
					tooltip = "Master Poisoner";
					break;
				case physicalDamage:
					name = "4% Physical Damage Debuff";
					tooltip = "Savage Combat";
					break;
				case spellCrit:
					name = "5% Spell Crit Debuff";
					tooltip = "Imp. Scorch, Winter's Chill";
					break;
				case spellDamage:
					name = "13% Spell Damage Debuff";
					tooltip = "CoE, Ebon Plaguebringer";
					break;
				case spellHit:
					name = "3% Spell Hit Debuff";
					tooltip = "Misery, Imp. Faerie Fire";
					break;
			}
			box = new DebuffBox(b, name);
			box.setEnabled(enabled);
			box.setToolTipText(tooltip);
			debuffBoxes.put(b, box);
			add(box, c);
			y++;
		}
		

		
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		try {
			Buff b = Buff.valueOf(evt.getPropertyName());
			handleBuffChange(b);
			return;
		} catch (IllegalArgumentException e) {
			
		} finally {
			try {
				Debuff db = Debuff.valueOf(evt.getPropertyName());
				handleDebuffChange(db);
				return;
			} catch (IllegalArgumentException e) {
				
			}
		}
	}
	
	private void handleBuffChange(Buff b) {
		buffBoxes.get(b).setSelected(controller.hasBuff(b));
		switch (b) {
		case attackPower:
			buffBoxes.get(Buff.attackPowerImp).setEnabled(controller.hasBuff(b));
			break;
		case meleHaste:
			buffBoxes.get(Buff.meleHasteImp).setEnabled(controller.hasBuff(b));
			break;
		case statsAdditive:
			buffBoxes.get(Buff.statsAdditiveImp).setEnabled(controller.hasBuff(b));
			break;
		case agilityStrength:
			buffBoxes.get(Buff.agilityStrengthImp).setEnabled(controller.hasBuff(b));
			break;
		case food:
			buffBoxes.get(Buff.foodAgi).setEnabled(controller.hasBuff(b));
			buffBoxes.get(Buff.foodArp).setEnabled(controller.hasBuff(b));
			buffBoxes.get(Buff.foodAtp).setEnabled(controller.hasBuff(b));
			buffBoxes.get(Buff.foodExp).setEnabled(controller.hasBuff(b));
			buffBoxes.get(Buff.foodHst).setEnabled(controller.hasBuff(b));
			break;
			
		}
		mainFrame.showStats();
	}
	
	private void handleDebuffChange(Debuff b) {
		debuffBoxes.get(b).setSelected(controller.hasDebuff(b));
		if (b == Debuff.armorMajor)
			debuffBoxes.get(Debuff.armorMajorMaintain).setEnabled(controller.hasDebuff(b));
		
		mainFrame.showStats();
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
		}
		
	}
	
	private class DebuffBox extends JCheckBox implements ActionListener {
		
		private Debuff debuff;
		
		public DebuffBox(Debuff debuff, String name) {
			super(name);
			this.debuff = debuff;
			setFocusable(false);
			setSelected(controller.hasDebuff(debuff));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			controller.setDebuff(debuff, isSelected());
		}
		
	}

}
