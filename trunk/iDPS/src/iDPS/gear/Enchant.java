package iDPS.gear;

import iDPS.Attributes;
import iDPS.Persistency;
import iDPS.gear.Armor.SlotType;
import iDPS.gear.Setup.Profession;
import iDPS.gui.MainFrame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

public class Enchant implements Comparable<Enchant>, Rateable {
	
	private static HashMap<Integer,Enchant> map = null;
	private static HashMap<Integer,Enchant> fullmap = null;
	private static HashMap<Integer,Enchant> spellmap = null;
	
	private int id;
	private int spellid;
	private Profession profession;
	private SlotType slot;
	private String name;
	private Attributes attr;
	
	private float comparedDPS;
	
	@SuppressWarnings("unchecked")
	private Enchant(Element element) {
		this();
		id = Integer.parseInt(element.getAttributeValue("id"));
		spellid = Integer.parseInt(element.getAttributeValue("spellid"));
		
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
			else if (s.equals("attributes"))
				attr = new Attributes(e);
			else if (s.equals("slot"))
				slot = SlotType.valueOf(e.getText());
		}
	}
	
	private Enchant() {
		
	}

	public int compareTo(Enchant o) {
		if (comparedDPS > o.comparedDPS)
			return -1;
		else if (comparedDPS < o.comparedDPS)
			return 1;
		return 0;
	}

	public float getComparedDPS() {
		return comparedDPS;
	}
	
	public void setComparedDPS(float dps) {
		comparedDPS = dps;
	}
	
	@SuppressWarnings("unchecked")
	public static void load() {
		fullmap = new HashMap<Integer,Enchant>();
		spellmap = new HashMap<Integer,Enchant>();
		Document doc = Persistency.openXML(Persistency.FileType.Enchants);
		Element root = doc.getRootElement();
		for (Element e: (List<Element>) root.getChildren()) {
			Enchant en = new Enchant(e);
			if (en.getId()>0)
				fullmap.put(en.getId(), en);
			if (en.getSpellId()>0)
				spellmap.put(en.getSpellId(), en);
		}
		limit();
	}
	
	public static void limit() {
		map = new HashMap<Integer,Enchant>();
		for (Enchant e: fullmap.values()) {
			if (e.profession == null || MainFrame.getInstance().getSetup().hasProfession(e.profession))
				map.put(e.id, e);
		}
	}
	
	public static Enchant find(int id) {
		if (fullmap != null && fullmap.containsKey(id))
			return fullmap.get(id);
		else if (spellmap != null)
			return spellmap.get(id);
		return null;
	}
	
	public static ArrayList<Enchant> findSlot(SlotType slotType) {
		ArrayList<Enchant> matches = new ArrayList<Enchant>();
		Collection<Enchant> enchants = map.values();
		for (Enchant e: enchants) {
			if (e.matchesSlot(slotType))
				matches.add(e);
		}
		return matches;
	}
	
	public boolean matchesSlot(SlotType st) {
		if (st == slot)
			return true;
		switch (st) {
			case MainHand:
			case OffHand:
				return (slot == SlotType.OneHand);
		}
		return false;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Attributes getAttributes() {
		return attr;
	}

	public int getSpellId() {
		return spellid;
	}

}
