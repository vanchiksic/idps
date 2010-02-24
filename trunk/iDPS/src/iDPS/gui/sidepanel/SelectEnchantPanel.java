package iDPS.gui.sidepanel;

import iDPS.gear.Setup;
import iDPS.gear.Enchant;
import iDPS.gear.EnchantComparison;
import iDPS.gui.MainFrame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

public class SelectEnchantPanel extends JPanel {
	
	private MainFrame mainFrame;
	
	public SelectEnchantPanel(MainFrame mainFrame, int slot) {
		super();
		this.mainFrame = mainFrame;
		EnchantComparison ec;
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		int j = 0;
		Enchant e, curEnchant;
		Setup gear = mainFrame.getApp().getSetup();
		curEnchant = gear.getEnchant(slot);
		ec = new EnchantComparison(gear, slot);
		ArrayList<Enchant> comparedEnchants = ec.getComparedEnchants();
		Iterator<Enchant> iter = comparedEnchants.iterator();

		JLabel label;
		SelectEnchantButton button;
		float diff;
		while (iter.hasNext()) {
			e = iter.next();
			button = new SelectEnchantButton(e, slot);
			if (e == curEnchant)
				button.setSelected(true);
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(0, 0, 0, 10);
			c.gridx = 0; c.gridy = j; c.gridheight = 2; c.gridwidth = 1;
			add(button, c);
			
			label = new JLabel(e.getName());
			c.insets = new Insets(0, 0, 0, 0);
			c.gridx = 1; c.gridy = j; c.gridheight = 1;
			add(label, c);
			
			if (curEnchant != null)
				diff = e.getComparedDPS() - curEnchant.getComparedDPS();
			else
				diff = e.getComparedDPS();
			label = new JLabel(String.format("%.2f (%+.2f)", e.getComparedDPS(), diff));
			label.setHorizontalAlignment(JLabel.RIGHT);
			c.gridx = 2; c.gridy = j;
			add(label, c);
			
			c.insets = new Insets(5, 0, 0, 0);
			c.gridx = 1; c.gridy = j+1; c.gridwidth = 2;
			add(new RatingPanel(e, ec.getMaxDPS()), c);
			
			JSeparator sep = new JSeparator();
			c.insets = new Insets(3, 0, 0, 0);
			c.gridx = 0; c.gridy = j+2; c.gridwidth = 3;
			add(sep, c);
			
			j += 3;
		}
		
		// None Button
		button = new SelectEnchantButton(null, slot);
		if (curEnchant == null)
			button.setSelected(true);
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 0, 0, 10);
		c.gridx = 0; c.gridy = j; c.gridheight = 2; c.gridwidth = 1;
		add(button, c);
		
		label = new JLabel("None");
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 1; c.gridy = j; c.gridheight = 1;
		add(label, c);
		
		if (curEnchant != null)
			diff = 0 - curEnchant.getComparedDPS();
		else
			diff = 0;
		label = new JLabel(String.format("%.2f (%+.2f)", 0F, diff));
		label.setHorizontalAlignment(JLabel.RIGHT);
		c.gridx = 2; c.gridy = j;
		add(label, c);
		
		c.insets = new Insets(5, 0, 0, 0);
		c.gridx = 1; c.gridy = j+1; c.gridwidth = 2;
		add(new RatingPanel(null, ec.getMaxDPS()), c);
		
		setBorder(new EmptyBorder(new Insets(3,6,6,6)));

	}
	
	private class SelectEnchantButton extends JRadioButton implements ActionListener {
		
		private Enchant enchant;
		private int slot;
		
		public SelectEnchantButton(Enchant enchant, int slot) {
			this.enchant = enchant;
			this.slot = slot;
			setFocusable(false);
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			mainFrame.showSidePanel();
			mainFrame.getApp().getSetup().setEnchant(slot, enchant);
			mainFrame.refreshItem(slot);
			mainFrame.showStats();
		}

	}

}
