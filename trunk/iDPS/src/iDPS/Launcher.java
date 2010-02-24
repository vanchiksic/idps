package iDPS;

import javax.swing.JOptionPane;

import iDPS.gear.Enchant;
import iDPS.gear.Gem;
import iDPS.gear.Armor;

public class Launcher {
	
	private static Application app;
	
	public static void main(String[] args) {
		
		String version = System.getProperty("java.version");
		System.out.println("Java version "+version+" detected.");
		char major = version.charAt(2);
		if (major < '6') {
		  System.err.println("Java 6 required");
		  JOptionPane.showMessageDialog(null, "Java 6 (1.6) required\r\nYou are using Java "+version, "Your Java Version is too old", JOptionPane.ERROR_MESSAGE); 
		  return;
		}
		
		if (System.getProperty("os.name").startsWith("Mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "iDPS");
		}
		Persistency.createXML();
		
		Race.load();
		System.out.println("Races loaded.");
		Armor.load();
		System.out.println("Items loaded.");
		Gem.load();
		System.out.println("Gems loaded.");
		Enchant.load();
		System.out.println("Enchants loaded.");
		
		app = new Application();
		app.initialize();
	}
	
	public static Application getApp() {
		return app;
	}

}
