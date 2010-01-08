package iDPS;

import iDPS.gear.Item.Faction;

import java.util.ArrayList;

import org.jdom.Document;
import org.jdom.Element;

public class Race {
	
	public enum Type { BloodElf, Dwarf, Gnome, Human, NightElf, Orc, Troll, Undead }
	private static ArrayList<Race> all;
	
	private Type type;
	private String name;
	private Faction faction;
	private Attributes attr;
	
	public Race(Type r) {
		type = r;
		name = r.name();
		attr = new Attributes();
		attr.setAtp(140);
		switch (r) {
			case BloodElf:
				faction = Faction.Horde;
				attr.setAgi(191);
				attr.setStr(110);
				break;
			case Dwarf:
				faction = Faction.Alliance;
				attr.setAgi(185);
				attr.setStr(115);
				break;
			case Gnome:
				faction = Faction.Alliance;
				attr.setAgi(192);
				attr.setStr(108);
				break;
			case Human:
				faction = Faction.Alliance;
				attr.setAgi(189);
				attr.setStr(113);
				break;
			case NightElf:
				faction = Faction.Alliance;
				attr.setAgi(194);
				attr.setStr(110);
				break;
			case Orc:
				faction = Faction.Horde;
				attr.setAgi(191);
				attr.setStr(110);
				break;
			case Troll:
				faction = Faction.Horde;
				attr.setAgi(191);
				attr.setStr(114);
				break;
			case Undead:
				faction = Faction.Horde;
				attr.setAgi(187);
				attr.setStr(112);
				break;
		}
	}
	
	public static void load() {
		all = new ArrayList<Race>(Type.values().length);
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		String defRace = doc.getRootElement().getChild("races").getAttributeValue("default");
		for (Type t: Type.values()) {
			Race r = new Race(t);
			all.add(r);
			if (r.getName().equals(defRace))
				Player.getInstance().setRace(r);
		}
	}
	
	public static void save() {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element races = doc.getRootElement().getChild("races");
		races.setAttribute("default", Player.getInstance().getRace().getName());
		Persistency.saveXML(doc, Persistency.FileType.Settings);
	}
	
	public static ArrayList<Race> getAll() {
		return all;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public Attributes getAttr() {
		return attr;
	}

	public Faction getFaction() {
		return faction;
	}

}
