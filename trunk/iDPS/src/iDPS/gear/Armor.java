package iDPS.gear;

import iDPS.Attributes;
import iDPS.FilterController;
import iDPS.Persistency;
import iDPS.Launcher;
import iDPS.FilterController.Filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;


public class Armor extends Item {
	
	public enum SlotType { Back, Chest, Feet, Finger, Hands, Head,
		Legs, Neck, Ranged, Shoulder, Trinket, Waist, OneHand, MainHand, OffHand, Wrist }
	public enum Faction { Both, Alliance, Horde }
	public enum Tier { Tier9, Tier10 }
	
	public enum SocketType { Red, Blue, Yellow, Meta, Prismatic };

	private static HashMap<Integer,Armor> map = null;
	private static HashMap<Integer,Armor> fullmap = null;
	
	private String tag;
	private SlotType slot;
	private Tier tier;
	private Faction faction;
	
	private Attributes socketBonus;
	private ArrayList<SocketType> sockets;
	
	@SuppressWarnings("unchecked")
	public Armor(Element element) {
		super(element);
		tier = null;
		tag = null;
		sockets = new ArrayList<SocketType>();
		socketBonus = new Attributes();
		faction = Faction.Both;
		
		List<Element> childs = element.getChildren();
		Iterator<Element> i = childs.iterator();
		while (i.hasNext()) {
			Element e = i.next();
			String s = e.getName();
			if (s.equals("tag"))
				tag = e.getText();
			else if (s.equals("slot"))
				slot = SlotType.valueOf(e.getText());
			else if (s.equals("faction"))
				faction = Faction.valueOf(e.getText());
			else if (s.equals("sockets")) {
				List<Element> childs2 = e.getChildren();
				Iterator<Element> iter2 = childs2.iterator();
				while (iter2.hasNext()) {
					Element e2 = iter2.next();
					String s2 = e2.getName();
					if (s2.equals("socket")) {		
						int index = Integer.parseInt(e2.getAttributeValue("index"));
						SocketType type = SocketType.valueOf(e2.getText());
						sockets.add(index, type);
					} else if (s2.equals("bonus")) {
						socketBonus = new Attributes(e2);
					}
				}
			}
		}
		
		checkTierSet();
	}
	
	public Armor(int id) {
		this();
		setId(id);
	}
	
	public Armor() {
		super();
		tier = null;
		tag = null;
		sockets = new ArrayList<SocketType>();
		socketBonus = new Attributes();
		faction = Faction.Both;
	}
	
	public int getMaxSocketIndex() {
		return sockets.size()-1;
	}
	
	private void checkTierSet() {
		int[] tier9 = {48218,48219,48220,48221,48222,
						48223,48224,48225,48226,48227,
						48228,48229,48230,48231,48232,
						48233,48234,48235,48236,48237,
						48238,48239,48240,48241,48242,
						48243,48244,48245,48246,48247};
		if (getId() >= tier9[0] && getId() <= tier9[14]) {
			for (int idT: tier9) {
				if (getId() == idT) {
					tier = Tier.Tier9;
					break;
				}
			}
		}
		int[] tier10 = {50087,50088,50089,50090,50105,
						51185,51186,51187,51188,51189,
						51250,51251,51252,51253,51254};
		if (getId() >= tier10[0] && getId() <= tier10[14]) {
			for (int idT: tier10) {
				if (getId() == idT) {
					tier = Tier.Tier10;
					break;
				}
			}
		}
	}

	public SlotType getSlot() {
		return slot;
	}
	
	public SocketType getSocket(int index) {
		if (sockets.size()>index)
			return sockets.get(index);
		return SocketType.Prismatic;
	}
	
	public boolean hasSockets() {
		return (sockets.size()>0);
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
	
	public String getToolTip() {
		String s = "<html><body style=\"padding:4px;background-color:#070c20;color:white;font-family:Verdana,sans-serif;font-size:8px;\"><p style=\"font-weight:bold;font-size:8px;margin:0 0 0 0;\">"+getName()+"</p>";
		s += "<p style=\"margin:0 0 6px 0;\">Level: " + getLvl() + "</p>";
		s += getAttr().getToolTip();
		if (hasSockets()) {
			s += "<p style=\"margin:6px 0 0 0;\">Socket Bonus:</p>";
			s += socketBonus.getMinToolTip();
		}
		s += "</body></html>";
		return s;
	}
	
	public static Armor find(int id) {
		if (fullmap != null && fullmap.containsKey(id))
			return fullmap.get(id);
		return null;
	}
	
	public static ArrayList<Armor> findSlot(SlotType slotType) {
		ArrayList<Armor> matches = new ArrayList<Armor>();
		Collection<Armor> items = map.values();
		Iterator<Armor> iter = items.iterator();
		while (iter.hasNext()) {
			Armor item = iter.next();
			if (item.matchesSlot(slotType) && (item.getFaction() == Faction.Both ||
					(item.getFaction() == Launcher.getApp().getSetup().getRace().getFaction())))
				matches.add(item);
		}
		return matches;
	}
	
	public static ArrayList<Armor> findWeapon(Weapon.weaponType type) {
		ArrayList<Armor> matches = new ArrayList<Armor>();
		Collection<Armor> items = map.values();
		Iterator<Armor> iter = items.iterator();
		while (iter.hasNext()) {
			Armor item = iter.next();
			if (item instanceof Weapon) {
				Weapon weapon = (Weapon) item;
				if (weapon.getType() == type && (item.getFaction() == Faction.Both ||
						(item.getFaction() == Launcher.getApp().getSetup().getRace().getFaction())))
					matches.add(item);
			}
		}
		return matches;
	}

	public static ArrayList<Armor> getAll() {
		return new ArrayList<Armor>(map.values());
	}

	@SuppressWarnings("unchecked")
	public static void load() {
		fullmap = new HashMap<Integer,Armor>();
		Document doc = Persistency.openXML(Persistency.FileType.Items);
		Element root = doc.getRootElement();
		mapItemList((List<Element>) root.getChildren());
		
		//Get custom items from settings file
		Document docSettings = Persistency.openXML(Persistency.FileType.Settings);
		Element itemSettings = docSettings.getRootElement().getChild("items");
		if (itemSettings != null) 
			mapItemList((List<Element>) itemSettings.getChildren());
	}
	
	private static void mapItemList(List<Element> items) {
		Armor item;
		for (Element e: items) {
			String s = e.getChildText("slot");
			if (s != null && (s.equals("MainHand") || s.equals("OneHand") || s.equals("OffHand")))
				item = new Weapon(e);
			else
				item = new Armor(e);
			if (item.getId()>0)
				fullmap.put(item.getId(), item);
		}		
	}
	
	public static void limit() {
		//System.out.println("> Limiting Equippable Items");
		map = new HashMap<Integer,Armor>();
		FilterController fc = Launcher.getApp().getFilterController();
		for (Armor i: fullmap.values()) {
			if (i.getFilter().size() == 0 || fc.isOneFilterChecked(i.getFilter()))
				map.put(i.getId(), i);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void save() {
		Document doc = Persistency.openXML(Persistency.FileType.Items);
		Element root = doc.getRootElement();
		root.removeContent();
		TreeSet<Integer> keys = new TreeSet<Integer>(map.keySet());
		for (Integer key: keys) {
			Armor item = map.get(key);
			Element eSub, eItem = new Element("item");
			eItem.setAttribute("id", String.valueOf(item.getId()));
			eSub = new Element("name");
			eSub.addContent(new CDATA(item.getName()));
			eItem.getChildren().add(eSub);
			eSub = new Element("slot");
			eSub.setText(item.getSlot().name());
			eItem.getChildren().add(eSub);
			if (item instanceof Weapon) {
				Weapon w = (Weapon) item;
				eSub = new Element("type");
				eSub.setText(w.getType().name());
				eItem.getChildren().add(eSub);
				eSub = new Element("speed");
				eSub.setText(String.valueOf(w.getSpeed()));
				eItem.getChildren().add(eSub);
				eSub = new Element("dps");
				eSub.setText(String.valueOf(w.getDps()));
				eItem.getChildren().add(eSub);
			}
			Element eAttr = item.getAttr().toXML(null);
			eItem.getChildren().add(eAttr);
			if (item.hasSockets()) {
				Element eSockets = new Element("sockets");
				for (int index=0; index<=item.getMaxSocketIndex(); index++) {
					SocketType s = item.getSocket(index);
					Element eSocket = new Element("socket");
					eSocket.setAttribute("index", String.valueOf(index));
					eSocket.setText(s.name());
					eSockets.getChildren().add(eSocket);
				}
				Element eBonus = item.getSocketBonus().toXML("bonus");
				eSockets.getChildren().add(eBonus);
				eItem.getChildren().add(eSockets);
			}
			eSub = new Element("lvl");
			eSub.setText(String.valueOf(item.getLvl()));
			eItem.getChildren().add(eSub);
			if (item.getUniqueLimit()>0) {
				eSub = new Element("unique");
				eSub.setAttribute("max", String.valueOf(item.getUniqueLimit()));
				eSub.setText(item.getUniqueName());
				eItem.getChildren().add(eSub);
			}
			if (item.getTag() != null && item.getTag().length()>0) {
				eSub = new Element("tag");
				eSub.setText(item.getTag());
				eItem.getChildren().add(eSub);
			}
			if (item.faction != Faction.Both) {
				eSub = new Element("faction");
				eSub.setText(item.faction.name());
				eItem.getChildren().add(eSub);
			}
			if (item.getFilter().size() > 0) {
				eSub = new Element("filters");
				Element eSub2;
				for (Filter f: item.getFilter()) {
					eSub2 = new Element("filter");
					eSub2.setText(f.name());
					eSub.getChildren().add(eSub2);
				}
				eItem.getChildren().add(eSub);
			}
			eSub = new Element("icon");
			eSub.setText(item.getIcon());
			eItem.getChildren().add(eSub);
			
			root.getChildren().add(eItem);
		}
		Persistency.saveXML(doc, Persistency.FileType.Items);
	}
	
	public static void add(Armor item) {
		if (item.getId() <= 0)
			return;
		if (map == null)
			Armor.load();
		map.put(item.getId(), item);
	}

	public Attributes getSocketBonus() {
		return socketBonus;
	}

	public Tier getTier() {
		return tier;
	}

	public void setSlot(SlotType slot) {
		this.slot = slot;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Faction getFaction() {
		return faction;
	}

	public void setFaction(Faction faction) {
		this.faction = faction;
	}
	
	protected void setSockets(ArrayList<SocketType> sockets) {
		this.sockets = sockets;
	}

}
