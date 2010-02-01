package iDPS.gui;

import iDPS.Player;
import iDPS.gear.Gear;
import iDPS.gear.Gem;
import iDPS.gear.GemComparison;

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

public class SelectGemPanel extends JPanel {
	
	public SelectGemPanel(int slot, int index) {
		super();
		GemComparison gc;
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		int j = 0;
		Gem gem, curGem;
		Gear gear = Player.getInstance().getSetup();
		curGem = gear.getGem(slot,index);
		gc = new GemComparison(gear, slot, index);
		ArrayList<Gem> comparedGems = gc.getComparedGems();
		Iterator<Gem> iter = comparedGems.iterator();

		JLabel label;
		SelectGemButton button;
		float diff;
		while (iter.hasNext()) {
			gem = iter.next();
			
			button = new SelectGemButton(gem, slot, index);
			if (gem == curGem)
				button.setSelected(true);
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(0, 0, 0, 10);
			c.gridx = 0; c.gridy = j; c.gridheight = 3; c.gridwidth = 1;
			add(button, c);
			
			JLabel jl = new InventoryIcon(gem);
			jl.setToolTipText(gem.getToolTip());
			c.insets = new Insets(0, 0, 0, 7);
			c.gridx = 1; c.gridy = j; c.gridheight = 3; c.gridwidth = 1;
			add(jl, c);
			
			label = new JLabel(gem.getName());
			c.insets = new Insets(0, 0, 0, 0);
			c.gridx = 2; c.gridy = j; c.gridheight = 1;
			add(label, c);
			
			label = new JLabel(String.format("%.2f", gem.getComparedDPS()));
			label.setHorizontalAlignment(JLabel.RIGHT);
			c.gridx = 3; c.gridy = j;
			add(label, c);
			
			label = new JLabel(gem.getAttr().toString());
			c.gridx = 2; c.gridy = j+1;
			add(label, c);
			
			if (curGem != null)
				diff = gem.getComparedDPS() - curGem.getComparedDPS();
			else
				diff = 0;
			label = new JLabel(String.format("%+.2f", diff));
			label.setHorizontalAlignment(JLabel.RIGHT);
			c.gridx = 3; c.gridy = j+1;
			add(label, c);
			
			c.insets = new Insets(5, 0, 0, 0);
			c.gridx = 2; c.gridy = j+2; c.gridwidth = 2;
			add(new RatingPanel(gem, gc.getMaxDPS()), c);
			
			if (iter.hasNext()) {
				JSeparator sep = new JSeparator();
				c.insets = new Insets(3, 0, 0, 0);
				c.gridx = 0; c.gridy = j+3; c.gridwidth = 3;
				add(sep, c);
				j += 1;
			}
			
			j += 3;
		}
		setBorder(new EmptyBorder(new Insets(3,6,6,6)));

	}
	
	private class SelectGemButton extends JRadioButton implements ActionListener {
		
		private Gem gem;
		private int slot, index;
		
		public SelectGemButton(Gem gem, int slot, int index) {
			this.gem = gem;
			this.slot = slot;
			this.index = index;
			setFocusable(false);
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			MainFrame.getInstance().getSideScroll().setViewportView(new JPanel());
			Player p = Player.getInstance();
			p.getSetup().setGem(slot, index, gem);
			MainFrame.getInstance().showGem(gem, slot, index);
			MainFrame.getInstance().showStats();
		}

	}

}
