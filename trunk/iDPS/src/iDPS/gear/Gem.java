package iDPS.gear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import iDPS.Attributes;
import iDPS.Persistency;
import iDPS.Player;
import iDPS.Player.Profession;
import iDPS.gear.Socket.SocketType;


public class Gem implements Comparable<Gem>, Rateable {
	
	public enum GemColor { Orange, Red, Yellow, Purple, Blue, Green, Prismatic, Meta };
	
	private static HashMap<Integer,Gem> map = null;
	private static HashMap<Integer,Gem> fullmap = null;
	
	private int id;
	private Profession profession;
	private Attributes attr;
	private GemColor color;
	private float comparedDPS;
	private String name;
	
	@SuppressWarnings("unchecked")
	private Gem(Element element) {
		this();
		id = Integer.parseInt(element.getAttributeValue("id"));
		
		if (element.getAttribute("profession") != null)
			profession = Profession.valueOf(element.getAttributeValue("profession"));
		else
			profession = null;

		List<Element> childs = element.getChildren();
		Iterator<Element> i = childs.iterator();
		while (i.hasNext()) {
			Element e = i.next();
			String s = e.getName();
			if (s.equals("name"))
				name = e.getText();
			else if (s.equals("color"))
				color = GemColor.valueOf(e.getText());
			else if (s.equals("agi"))
				attr.setAgi(Float.parseFloat(e.getText()));
			else if (s.equals("str"))
				attr.setStr(Float.parseFloat(e.getText()));
			else if (s.equals("arp"))
				attr.setArp(Float.parseFloat(e.getText()));
			else if (s.equals("atp"))
				attr.setAtp(Float.parseFloat(e.getText()));
			else if (s.equals("cri"))
				attr.setCri(Float.parseFloat(e.getText()));
			else if (s.equals("exp"))
				attr.setExp(Float.parseFloat(e.getText()));
			else if (s.equals("hit"))
				attr.setHit(Float.parseFloat(e.getText()));
			else if (s.equals("hst"))
				attr.setHst(Float.parseFloat(e.getText()));
		}
	}
	
	private Gem(int id, String name, Attributes attr, GemColor color) {
		super();
		this.id = id;
		this.name = name;
		this.attr = attr;
		this.color = color;
	}
	
	private Gem() {
		id = 0;
		attr = new Attributes();
		color = GemColor.Prismatic;
		comparedDPS = 0;
	}
	
	public Attributes getAttributes() {
		return attr;
	}
	
	public GemColor getColor() {
		return color;
	}

	public float getComparedDPS() {
		return comparedDPS;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isMatch(Socket s) {
		if (color == GemColor.Prismatic)
			return (s.getType() != SocketType.Meta);
		switch (s.getType()) {
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

	public void setComparedDPS(float comparedDPS) {
		this.comparedDPS = comparedDPS;
	}
	
	public String toString() {
		return name;
	}
	
	public String getToolTip() {
		String s = "<html><b>"+name+"</b>";
		s += attr.getToolTip();
		s += "</html>";
		return s;
	}
	
	public int compareTo(Gem o) {
		if (comparedDPS > o.comparedDPS)
			return -1;
		else if (comparedDPS < o.comparedDPS)
			return 1;
		return 0;
	}

	public static Gem find(int id) {
		return fullmap.get(id);
	}
	
	public static ArrayList<Gem> findSocket(Socket s) {
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
		limit();
	}
	
	public static void limit() {
		map = new HashMap<Integer,Gem>();
		for (Gem g: fullmap.values()) {
			if (g.profession == null || Player.getInstance().hasProfession(g.profession))
				map.put(g.id, g);
		}
	}

}
