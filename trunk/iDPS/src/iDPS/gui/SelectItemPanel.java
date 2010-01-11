package iDPS.gui;

import iDPS.Player;
import iDPS.gear.Item;
import iDPS.gear.ItemComparison;
import iDPS.gear.Item.SlotType;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

public class SelectItemPanel extends JPanel {
	
	public SelectItemPanel(int slot) {
		super();
		
		SlotType[] slotMap = {
				SlotType.Head, SlotType.Neck, SlotType.Shoulder, SlotType.Back, SlotType.Chest, null,
				null, SlotType.Wrist, SlotType.Hands, SlotType.Waist, SlotType.Legs, SlotType.Feet,
				SlotType.Finger, SlotType.Finger, SlotType.Trinket, SlotType.Trinket,
				SlotType.MainHand, SlotType.OffHand, SlotType.Ranged,
		};
		
		ItemComparison ic = new ItemComparison(Player.getInstance().getEquipped(), slot, slotMap[slot]);
		ArrayList<Item> comparedItems = ic.getComparedItems();
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		Iterator<Item> iter = comparedItems.iterator();
		int j = 0;
		Item item, curItem = Player.getInstance().getEquipped().getItem(slot);

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
			
			JLabel jl = new JLabel(new InventoryIcon(item));
			jl.setToolTipText(item.getToolTip());
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
				diff = 0;
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
		
		private Item item;
		private int slot;
		
		public SelectItemButton(Item item, int slot) {
			this.item = item;
			this.slot = slot;
			setFocusable(false);
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			MainFrame.getInstance().getSideScroll().setViewportView(new JPanel());
			Player p = Player.getInstance();
			p.getEquipped().setItem(slot, item);
			MainFrame.getInstance().refreshItem(slot);
			MainFrame.getInstance().showStats();
		}

	}
	
	private class InventoryIcon extends ImageIcon {
		
		public InventoryIcon(Item item) {
			super();
			Image image;
			URL url = SelectItemPanel.class.getResource("/images/"+item.getIcon()+".png");
			if (url == null)
				url = InventoryButton.class.getResource("/images/inv_misc_questionmark.png");
			try {
				image = ImageIO.read(url);
				image = createImage(new FilteredImageSource(image.getSource(),
		        new CropImageFilter(5, 5, 54, 54)));
				setImage(image);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
