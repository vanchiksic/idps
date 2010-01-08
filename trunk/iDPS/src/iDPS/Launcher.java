package iDPS;

import iDPS.gear.Enchant;
import iDPS.gear.Gear;
import iDPS.gear.Gem;
import iDPS.gear.Item;
import iDPS.gui.MainFrame;

public class Launcher {
	
	public static void main(String[] args) {
		if (System.getProperty("os.name").startsWith("Mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "iDPS");
		}
		Persistency.createXML();
		Race.load();
		Player.getInstance().loadProfessions();
		Talents.load();
		Item.load();
		Gem.load();
		Enchant.load();
		Gear.load();
		MainFrame.getInstance();
	}

}
