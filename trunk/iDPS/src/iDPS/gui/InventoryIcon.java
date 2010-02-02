package iDPS.gui;

import iDPS.gear.Item;

import java.awt.Image;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class InventoryIcon extends JLabel {
	
	public InventoryIcon(Item item) {
		super();
		Image image;
		URL url = null;
		if (item != null) {
			url = SelectItemPanel.class.getResource("/images/"+item.getIcon()+".png");
			setToolTipText(item.getToolTip());
		}
		if (url == null)
			url = InventoryButton.class.getResource("/images/inv_misc_questionmark.png");
		try {
			image = ImageIO.read(url);
			image = createImage(new FilteredImageSource(image.getSource(),
	        new CropImageFilter(5, 5, 54, 54)));
			setIcon(new ImageIcon(image));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
