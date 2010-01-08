package iDPS.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import iDPS.Player;
import iDPS.gear.Gear;
import iDPS.gear.Gem;
import iDPS.gear.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;

public class SocketButton extends JButton implements ActionListener {
	
	private int slot;
	private int index;
	
	public SocketButton(int slot, int index) {
		this.slot = slot;
		this.index = index;
		URL url = InventoryButton.class.getResource("/images/Socket_Meta.png");
		setIcon(new ImageIcon(url));
		//setBorder(new EmptyBorder(2,2,2,2));
		setFocusable(false);
		addActionListener(this);
	}
	
	protected void updateColor() {
		Gear gear = Player.getInstance().getEquipped();
		Socket socket = gear.getItem(slot).getSocket(index);
		URL url;
		Border b, b1 = BorderFactory.createEmptyBorder(1, 1, 1, 1), b2;
		switch (socket.getType()) {
			case Red:
				url = InventoryButton.class.getResource("/images/Socket_Red.png");
				b2 = BorderFactory.createLineBorder(Color.RED, 2);
				break;
			case Yellow:
				url = InventoryButton.class.getResource("/images/Socket_Yellow.png");
				b2 = BorderFactory.createLineBorder(Color.YELLOW, 2);
				break;
			case Blue:
				url = InventoryButton.class.getResource("/images/Socket_Blue.png");
				b2 = BorderFactory.createLineBorder(Color.BLUE, 2);
				break;
			default:
				url = InventoryButton.class.getResource("/images/Socket_Meta.png");
				b2 = BorderFactory.createLineBorder(Color.GRAY, 2);
				break;
		}
		setIcon(new ImageIcon(url));
		b = BorderFactory.createCompoundBorder(b1, b2);
		setBorder(b);
	}
	
	public void socketGem(Gem gem) {
		if (gem == null) {
			setToolTipText("");
			updateColor();
			return;
		}
		setToolTipText(gem.getToolTip());
		URL url = null;
		switch (gem.getColor()) {
			case Red:
				url = InventoryButton.class.getResource("/images/inv_jewelcrafting_gem_37.jpg");
				break;
			case Orange:
				url = InventoryButton.class.getResource("/images/inv_jewelcrafting_gem_39.jpg");
				break;
			case Yellow:
				url = InventoryButton.class.getResource("/images/inv_jewelcrafting_gem_38.jpg");
				break;
			case Purple:
				url = InventoryButton.class.getResource("/images/inv_jewelcrafting_gem_40.jpg");
				break;
			case Green:
				url = InventoryButton.class.getResource("/images/inv_jewelcrafting_gem_41.jpg");
				break;
			case Blue:
				url = InventoryButton.class.getResource("/images/inv_jewelcrafting_gem_42.jpg");
				break;
			case Meta:
				url = InventoryButton.class.getResource("/images/inv_jewelcrafting_shadowspirit_02.jpg");
				break;
			default:
				url = InventoryButton.class.getResource("/images/inv_misc_gem_pearl_12.jpg");
				break;
		}
		setIcon(new ImageIcon(url));
	}

	public void actionPerformed(ActionEvent e) {
		MainFrame f = MainFrame.getInstance();
		SelectGemPanel ip = new SelectGemPanel(slot, index);
		f.getSideScroll().setViewportView(ip);
	}

}
