package iDPS.gear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import iDPS.Attributes;
import iDPS.Persistency;
import iDPS.gear.Setup.Profession;
import iDPS.gear.Armor.SocketType;
import iDPS.gui.MainFrame;


public class Gem extends Item {
	
	public enum GemColor { Orange, Red, Yellow, Purple, Blue, Green, Prismatic, Meta };
	
	private static HashMap<Integer,Gem> map = null;
	private static HashMap<Integer,Gem> fullmap = null;
	
	private Profession profession;
	private GemColor color;
	
	@SuppressWarnings("unchecked")
	private Gem(Element element) {
		super(element);
		
		if (element.getAttribute("profession") != null)
			profession = Profession.valueOf(element.getAttributeValue("profession"));
		else
			profession = null;

		List<Element> childs = element.getChildren();
		Iterator<Element> i = childs.iterator();
		while (i.hasNext()) {
			Element e = i.next();
			String s = e.getName();
			if (s.equals("color"))
				color = GemColor.valueOf(e.getText());
		}
	}
	
	private Gem(int id, String name, Attributes attr, GemColor color) {
		this();
		this.color = color;
	}
	
	private Gem() {
		super();
		color = GemColor.Prismatic;
	}
	
	public GemColor getColor() {
		return color;
	}
	
	public boolean isMatch(SocketType s) {
		if (color == GemColor.Prismatic)
			return (s != SocketType.Meta);
		switch (s) {
			case Red:
				return (color == GemColor.Red || color == GemColor.Orange || color == GemColor.Purple);
			case Yellow:
				return (color == GemColor.Yellow || color == GemColor.Orange || color == GemColor.Green);
			case Blue:
				return (color == GemColor.Blue || color == GemColor.Purple || color == GemColor.Green);
			case Meta:
				return (color == GemColor.Meta);
			default:
				return true;
		}
	}

	public static Gem find(int id) {
		if (fullmap != null && fullmap.containsKey(id))
			return fullmap.get(id);
		return null;
	}
	
	public static ArrayList<Gem> findSocket(SocketType s) {
		ArrayList<Gem> matches = new ArrayList<Gem>();
		for (Gem gem: getAll()) {
			if (gem.isMatch(s))
				matches.add(gem);
		}
		return matches;
	}

	public static ArrayList<Gem> getAll() {
		return new ArrayList<Gem>(map.values());
	}
	
	@SuppressWarnings("unchecked")
	public static void load() {
		fullmap = new HashMap<Integer,Gem>();
		Document doc = Persistency.openXML(Persistency.FileType.Gems);
		Gem gem;
		Element root = doc.getRootElement();
		for (Element e: (List<Element>) root.getChildren()) {
			gem = new Gem(e);
			if (gem.getId()>0)
				fullmap.put(gem.getId(), gem);
		}
		//limit();
	}
	
	public static void limit() {
		map = new HashMap<Integer,Gem>();
		for (Gem g: fullmap.values()) {
			if (g.profession == null || MainFrame.getInstance().getSetup().hasProfession(g.profession))
				map.put(g.getId(), g);
		}
	}

}
