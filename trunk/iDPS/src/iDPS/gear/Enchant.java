package iDPS.gear;

import iDPS.Attributes;
import iDPS.Persistency;
import iDPS.Player;
import iDPS.Player.Profession;
import iDPS.gear.Item.SlotType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jdom.Document;
import org.jdom.Element;

public class Enchant implements Comparable<Enchant>, Rateable {
	
	private static HashMap<Integer,Enchant> map = null;
	
	private int id;
	private SlotType slot;
	private String name;
	private Attributes attr;
	
	private float comparedDPS;
	
	@SuppressWarnings("unchecked")
	private Enchant(Element element) {
		this();
		id = Integer.parseInt(element.getAttributeValue("id"));
		
		if (element.getAttribute("profession") != null) {
			Profession p = Profession.valueOf(element.getAttributeValue("profession"));
			if (!Player.getInstance().hasProfession(p))
				id = 0;
		}
		
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
		if (map == null)
			map = new HashMap<Integer,Enchant>();
		Set<Integer> keys = new TreeSet<Integer>();
		Document doc = Persistency.openXML(Persistency.FileType.Enchants);
		Element root = doc.getRootElement();
		for (Element e: (List<Element>) root.getChildren()) {
			Enchant en = new Enchant(e);
			if (en.getId()>0) {
				keys.add(en.getId());
				if (!map.containsKey(en.getId()))
					map.put(en.getId(), en);
			}
		}
		Set<Integer> diff = new TreeSet<Integer>(map.keySet());
		diff.removeAll(keys);
		for (int i: diff)
			map.remove(i);
	}
	
	public static Enchant find(int id) {
		return map.get(id);
	}
	
	public static ArrayList<Enchant> findSlot(SlotType slotType) {
		if (map == null)
			Enchant.load();
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

}
