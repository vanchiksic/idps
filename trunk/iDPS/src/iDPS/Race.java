package iDPS;

import iDPS.gear.Armor.Faction;
import iDPS.gui.MainFrame;

import java.util.ArrayList;
import java.util.HashMap;

public class Race {
	
	public enum Type { BloodElf, Dwarf, Gnome, Human, NightElf, Orc, Troll, Undead }
	private static HashMap<Integer,Race> map = null;

	private int id;
	private Type type;
	private String name;
	private Faction faction;
	private Attributes attr;
	
	public Race() {
		attr = new Attributes();
	}
	
	public Race(Type r) {
		this();
		attr.setAtp(140);
		type = r;
		name = r.name();
		switch (r) {
			case BloodElf:
				id = 1;
				faction = Faction.Horde;
				attr.setAgi(191);
				attr.setStr(110);
				break;
			case Dwarf:
				id = 2;
				faction = Faction.Alliance;
				attr.setAgi(185);
				attr.setStr(115);
				break;
			case Gnome:
				id = 3;
				faction = Faction.Alliance;
				attr.setAgi(192);
				attr.setStr(108);
				break;
			// Gnome id = 4
			case Human:
				id = 5;
				faction = Faction.Alliance;
				attr.setAgi(189);
				attr.setStr(113);
				break;
			case NightElf:
				id = 6;
				faction = Faction.Alliance;
				attr.setAgi(194);
				attr.setStr(110);
				break;
			case Orc:
				id = 7;
				faction = Faction.Horde;
				attr.setAgi(191);
				attr.setStr(110);
				break;
			case Troll:
				id = 8;
				faction = Faction.Horde;
				attr.setAgi(191);
				attr.setStr(114);
				break;
			case Undead:
				id = 9;
				faction = Faction.Horde;
				attr.setAgi(187);
				attr.setStr(112);
				break;
		}
	}
	
	public static Race find(int id) {
		if (map.containsKey(id))
			return map.get(id);
		return null;
	}
	
	public static void load() {
		map = new HashMap<Integer,Race>();
		for (Type t: Type.values()) {
			Race r = new Race(t);
			map.put(r.id, r);
		}
		
		MainFrame.getInstance().getMyMenuBar().createRacesMenu();
	}
	
	public static ArrayList<Race> getAll() {
		if (map != null)
			return new ArrayList<Race>(map.values());
		return new ArrayList<Race>();
	}

	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
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
