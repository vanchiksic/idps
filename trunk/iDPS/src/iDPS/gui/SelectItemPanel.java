package iDPS.gui;

import iDPS.gear.Armor;
import iDPS.gear.ItemComparison;
import iDPS.gear.Armor.SlotType;

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

public class SelectItemPanel extends JPanel {
	
	private MainFrame mainFrame;
	
	public SelectItemPanel(MainFrame mainFrame, int slot) {
		super();
		this.mainFrame = mainFrame;
		SlotType[] slotMap = {
				SlotType.Head, SlotType.Neck, SlotType.Shoulder, SlotType.Back, SlotType.Chest, null,
				null, SlotType.Wrist, SlotType.Hands, SlotType.Waist, SlotType.Legs, SlotType.Feet,
				SlotType.Finger, SlotType.Finger, SlotType.Trinket, SlotType.Trinket,
				SlotType.MainHand, SlotType.OffHand, SlotType.Ranged,
		};
		
		ItemComparison ic = new ItemComparison(mainFrame.getSetup(), slot, slotMap[slot]);
		ArrayList<Armor> comparedItems = ic.getComparedItems();
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		Iterator<Armor> iter = comparedItems.iterator();
		int j = 0;
		Armor item, curItem = mainFrame.getSetup().getItem(slot);

		JLabel label;
		SelectItemButton button;
		float diff;
		while (iter.hasNext()) {
			item = iter.next();
			
			button = new SelectItemButton(item, slot);
			if (item == curItem)
				button.setSelected(true);
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(0, 0, 0, 7);
			c.gridx = 0; c.gridy = j; c.gridheight = 3; c.gridwidth = 1;
			add(button, c);
			
			JLabel jl = new InventoryIcon(item);
			c.insets = new Insets(0, 0, 0, 7);
			c.gridx = 1; c.gridy = j; c.gridheight = 3; c.gridwidth = 1;
			add(jl, c);
			
			label = new JLabel(item.getName());
			c.insets = new Insets(0, 0, 0, 0);
			c.gridx = 2; c.gridy = j; c.gridheight = 1;
			add(label, c);
			
			label = new JLabel(String.format("%.2f", item.getComparedDPS()));
			label.setHorizontalAlignment(JLabel.RIGHT);
			c.gridx = 3; c.gridy = j;
			add(label, c);
			
			if (item.getTag() != null)
				label = new JLabel(item.getTag());
			else
				label = new JLabel(String.format("Level: %d", item.getLvl()));
			c.gridx = 2; c.gridy = j+1;
			add(label, c);
			
			if (curItem != null)
				diff = item.getComparedDPS() - curItem.getComparedDPS();
			else
				diff = item.getComparedDPS();
			label = new JLabel(String.format("%+.2f", diff));
			label.setHorizontalAlignment(JLabel.RIGHT);
			c.gridx = 3; c.gridy = j+1;
			add(label, c);
			
			c.insets = new Insets(5, 0, 0, 0);
			c.gridx = 2; c.gridy = j+2; c.gridwidth = 2;
			add(new RatingPanel(item, ic.getMaxDPS()), c);
			
			if (iter.hasNext()) {
				JSeparator sep = new JSeparator();
				c.insets = new Insets(3, 0, 3, 0);
				c.gridx = 1; c.gridy = j+3; c.gridwidth = 3;
				add(sep, c);
				j += 1;
			}
			
			j += 3;
		}
		setBorder(new EmptyBorder(new Insets(3,6,6,6)));
	}
	
	private class SelectItemButton extends JRadioButton implements ActionListener {
		
		private Armor item;
		private int slot;
		
		public SelectItemButton(Armor item, int slot) {
			this.item = item;
			this.slot = slot;
			setFocusable(false);
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			mainFrame.getSideScroll().setViewportView(new JPanel());
			mainFrame.getSetup().setItem(slot, item);
			mainFrame.refreshItem(slot);
			mainFrame.showStats();
		}

	}

}
