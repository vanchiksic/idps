package iDPS.gui.sidepanel;

import iDPS.controllers.BuffController;
import iDPS.controllers.BuffController.Debuff;
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


public class BuffPanelDebuffs extends JPanel implements PropertyChangeListener {
	
	private MainFrame mainFrame;
	private BuffController controller;
	
	private EnumMap<Debuff,JCheckBox> boxes;
	
	public BuffPanelDebuffs(MainFrame mainFrame, BuffController buffController) {
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
		boxes = new EnumMap<Debuff,JCheckBox>(Debuff.class);
		for (Debuff b: Debuff.values()) {
			name = b.name();
			tooltip = null;
			c.gridx = 0; c.gridy = y;
			enabled = true;
			switch (b) {
				case armorMajor:
					name = "Major Armor Debuff";
					tooltip = "Sunder Armor, Expose Armor";
					break;
				case armorMinor:
					name = "Minor Armor Debuff";
					tooltip = "Faerie Fire";
					break;
				case bleed:
					name = "30% Bleed Damage";
					tooltip = "Mangle";
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
			if (evt.getPropertyName().equals("debuffs")) {
				for (Debuff b: Debuff.values())
					boxes.get(b).setSelected(controller.hasDebuff(b));
			} else if (evt.getPropertyName().startsWith("debuff_")) {
				String s = evt.getPropertyName().substring(7);
				try {
					Debuff db = Debuff.valueOf(s);
					boxes.get(db).setSelected(controller.hasDebuff(db));
					return;
				} catch (IllegalArgumentException e) {}
			}
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
			mainFrame.showStats();
		}
		
	}

}
