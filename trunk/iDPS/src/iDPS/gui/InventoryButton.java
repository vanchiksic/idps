package iDPS.gui;

import iDPS.Player;
import iDPS.gear.Gear;
import iDPS.gear.Gem;
import iDPS.gear.Armor;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class InventoryButton extends JButton implements ActionListener, MouseListener {
	
	private int slot;
	private SocketButton[] socketButtons;

	public InventoryButton(int slot) {
		super();
		URL url = InventoryButton.class.getResource("/images/inv_misc_questionmark.png");
		if (url != null) {
			setIcon(new InventoryIcon(url));
		}
		this.slot = slot;
		socketButtons = new SocketButton[3];
		for (int i=0; i<=2; i++) {
			socketButtons[i] = new SocketButton(slot,i);
			socketButtons[i].setVisible(true);
		}
		Border b, b1, b2;
		b1 = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		b2 = BorderFactory.createLineBorder(Color.GRAY, 2);
		b = BorderFactory.createCompoundBorder(b1, b2);
		setBorder(b);
		setFocusable(false);
		addActionListener(this);
		addMouseListener(this);
		if (slot == 5 || slot == 6)
			setEnabled(false);
	}
	
	public void changeToItem(Armor item) {
		Gem[] gems = Player.getInstance().getSetup().getGems(slot);
		changeToItem(item, gems);
	}
	
	public void changeToItem(Armor item, Gem[] gems) {
		for (SocketButton sb: socketButtons)
			sb.setVisible(false);
		Border b, b1, b2;
		b1 = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		b2 = BorderFactory.createLineBorder(Color.GRAY, 2);
		b = BorderFactory.createCompoundBorder(b1, b2);
		setBorder(b);
		if (item == null) {
			URL url = InventoryButton.class.getResource("/images/inv_misc_questionmark.png");
			setIcon(new InventoryIcon(url));
			setToolTipText("");
			return;
		}
		if (item.getIcon() != null)
			changeIcon(item.getIcon());
		String s = item.getToolTip();
		Gear g = Player.getInstance().getSetup();
		if (g.isEnchanted(slot)) {
			s = s.replaceAll("</?html>", "");
			s += "<span style=\"text-decoration:none;\">"+g.getEnchant(slot).getName()+"</span>";
			s = "<html>"+s+"</html>";
		}
		setToolTipText(s);

		if (Player.getInstance().getSetup().isEnchanted(slot)) {
			b2 = BorderFactory.createLineBorder(Color.GREEN, 2);
			b = BorderFactory.createCompoundBorder(b1, b2);
			setBorder(b);
		}
		
		for (int i=0; i<=2; i++) {
			if (item.getSocket(i) != null) {
				socketButtons[i].updateColor();
				socketButtons[i].socketGem(gems[i]);
			  socketButtons[i].setVisible(true);
			}
		}
			
	}
	
	private void changeIcon(String name) {
		ChangeIconTask task = new ChangeIconTask(name);
		task.execute();
	}
	
	protected SocketButton getSocketButton(int index) {
		return socketButtons[index];
	}
	
	protected int getSlot() {
		return slot;
	}

	public void actionPerformed(ActionEvent e) {
		MainFrame f = MainFrame.getInstance();
		SelectItemPanel ip = new SelectItemPanel(slot);
		f.getSideScroll().setViewportView(ip);
	}
	
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			MainFrame f = MainFrame.getInstance();
			SelectEnchantPanel ep = new SelectEnchantPanel(slot);
			f.getSideScroll().setViewportView(ep);
		}
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}
	
	private class ChangeIconTask extends SwingWorker<Void,Void> {
		
		private String name;
		
		public ChangeIconTask(String name) {
			this.name = name;
		}

		@Override
		protected Void doInBackground() throws Exception {
			URL url, url1, url2;
			url1 = InventoryButton.class.getResource("/images/"+name+".png");
			url = url1;
			if (url == null) {
				try {
					//url2 = new URL("http://eu.wowarmory.com/wow-icons/_images/64x64/"+name+".jpg");
					url2 = new URL("http://db.mmo-champion.com/static/img/icons/"+name+".png");
					url = url2;
					setIcon(new InventoryIcon(ImageIO.read(url)));
					ReadableByteChannel rbc = Channels.newChannel(url2.openStream());
					FileOutputStream fos = new FileOutputStream(new File("./files/images/"+name+".png"));
					fos.getChannel().transferFrom(rbc, 0, 1 << 24);
				}
				catch (MalformedURLException e) {}
				catch (IOException e) {}
			} else {
				setIcon(new InventoryIcon(ImageIO.read(url)));
				
			}
			return null;
		}
	}
	
	private class InventoryIcon extends ImageIcon {
		
		public InventoryIcon(URL url) {
			super();
			Image image;
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
		
		public InventoryIcon(Image image) {
			super();
			image = createImage(new FilteredImageSource(image.getSource(),
	        new CropImageFilter(5, 5, 54, 54)));
			setImage(image);
		}
		
	}
	
}
