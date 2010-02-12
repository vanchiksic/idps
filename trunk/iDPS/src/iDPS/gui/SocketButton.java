package iDPS.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import iDPS.gear.Setup;
import iDPS.gear.Gem;
import iDPS.gear.Armor.SocketType;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class SocketButton extends JButton implements ActionListener {
	
	private int slot;
	private int index;
	private MainFrame mainFrame;
	
	public SocketButton(MainFrame mainFrame, int slot, int index) {
		this.slot = slot;
		this.index = index;
		this.mainFrame = mainFrame;
		URL url = InventoryButton.class.getResource("/images/Socket_Meta.png");
		Border b1 = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		Border b2 = BorderFactory.createLineBorder(Color.GRAY, 2);
		setIcon(new ImageIcon(url));
		Border b = BorderFactory.createCompoundBorder(b1, b2);
		setBorder(b);
		setFocusable(false);
		addActionListener(this);
	}
	
	protected void updateColor() {
		Setup gear = mainFrame.getSetup();
		SocketType socket = gear.getItem(slot).getSocket(index);
		URL url;
		Border b, b1 = BorderFactory.createEmptyBorder(1, 1, 1, 1), b2;
		switch (socket) {
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
		URL url = InventoryButton.class.getResource("/images/"+gem.getIcon()+".jpg");
		setIcon(new ImageIcon(url));
	}

	public void actionPerformed(ActionEvent e) {
		MainFrame f = MainFrame.getInstance();
		SelectGemPanel ip = new SelectGemPanel(mainFrame, slot, index);
		f.getSideScroll().setViewportView(ip);
	}
	
	class DashBorder extends LineBorder {

		//make getters and setters for stroke as exercise
		BasicStroke stroke = new BasicStroke(2, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 5f, new float[]{5f}, 0);

		public DashBorder(Color color, int thickness) {
			super(color, thickness);
			// TODO Auto-generated constructor stub
		}

		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setStroke(stroke);
			super.paintBorder(c, g2d, x, y, width, height);
			g2d.dispose();
		}
	}

}
