package iDPS.gear;

import iDPS.Attributes;
import iDPS.Persistency;
import iDPS.Player;
import iDPS.Player.Profession;
import iDPS.gear.Socket.SocketType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;


public class Item implements Comparable<Item>, Rateable {
	
	public enum SlotType { Back, Chest, Feet, Finger, Hands, Head,
		Legs, Neck, Ranged, Shoulder, Trinket, Waist, OneHand, MainHand, OffHand, Wrist }
	public enum Faction { Both, Alliance, Horde }
	public enum Tier { Tier9, Tier10 }

	private static HashMap<Integer,Item> map = null;
	
	private Attributes attr;
	private float comparedDPS;
	private String icon, tag;
	private int id;
	private int ilvl;
	private String name;
	private SlotType slot;
	private Tier tier;
	private Faction faction;
	
	private Attributes socketBonus;
	private Socket[] sockets;
	
	@SuppressWarnings("unchecked")
	public Item(Element element) {
		this();
		id = Integer.parseInt(element.getAttributeValue("id"));
		
		List<Element> childs = element.getChildren();
		Iterator<Element> i = childs.iterator();
		while (i.hasNext()) {
			Element e = i.next();
			String s = e.getName();
			if (s.equals("name"))
				name = e.getText();
			else if (s.equals("icon"))
				icon = e.getText();
			else if (s.equals("tag"))
				tag = e.getText();
			else if (s.equals("slot"))
				slot = SlotType.valueOf(e.getText());
			else if (s.equals("faction"))
				faction = Faction.valueOf(e.getText());
			else if (s.equals("attributes"))
				attr = new Attributes(e);
			else if (s.equals("lvl"))
				ilvl = Integer.parseInt(e.getText());
			else if (s.equals("sockets")) {
				List<Element> childs2 = e.getChildren();
				Iterator<Element> iter2 = childs2.iterator();
				while (iter2.hasNext()) {
					Element e2 = iter2.next();
					String s2 = e2.getName();
					if (s2.equals("socket")) {		
						int index = Integer.parseInt(e2.getAttributeValue("index"));
						if (e2.getText().equals("Red"))
							sockets[index] = new Socket(this, index, SocketType.Red);
						else if (e2.getText().equals("Blue"))
							sockets[index] = new Socket(this, index, SocketType.Blue);
						else if (e2.getText().equals("Yellow"))
							sockets[index] = new Socket(this, index, SocketType.Yellow);
						else if (e2.getText().equals("Meta"))
							sockets[index] = new Socket(this, index, SocketType.Meta);
					} else if (s2.equals("bonus")) {
						socketBonus = new Attributes(e2);
					}
				}
			}
		}
		if (slot == SlotType.Waist)
			setExtraSocket(true);
		if (((slot == SlotType.Wrist) || (slot == SlotType.Hands))
				&& Player.getInstance().hasProfession(Profession.Blacksmithing))
			setExtraSocket(true);
		
		checkTierSet();
	}
	
	public Item(int id) {
		this();
		this.id = id;
	}
	
	public Item() {
		tier = null;
		icon = null;
		tag = null;
		attr = new Attributes();
		sockets = new Socket[3];
		socketBonus = new Attributes();
		comparedDPS = 0;
		faction = Faction.Both;
	}
	
	private void setExtraSocket(boolean b) {
		if (b) {
			for (int j=0; j<=2; j++) {
				if (sockets[j] == null || sockets[j].getType() == SocketType.Prismatic) {
					sockets[j] = new Socket(this, j, SocketType.Prismatic);
					break;
				}
			}
		} else {
			for (int j=2; j>=0; j--) {
				if (sockets[j] != null && sockets[j].getType() == SocketType.Prismatic) {
					sockets[j] = null;
					break;
				}
			}
		}
	}
	
	public int getMaxSocketIndex() {
		for (int i=0; i<=2; i++) {
			if (sockets[i] == null)
				return (i-1);
		}
		return 2;
	}
	
	private void checkTierSet() {
		int[] tier9 = {48218,48219,48220,48221,48222,
				 					 48223,48224,48225,48226,48227,
				           48228,48229,48230,48231,48232,
				           48233,48234,48235,48236,48237,
									 48238,48239,48240,48241,48242,
									 48243,48244,48245,48246,48247};
		if (id >= tier9[0] && id <= tier9[14]) {
			for (int idT: tier9) {
				if (id == idT) {
					tier = Tier.Tier9;
					break;
				}
			}
		}
		int[] tier10 = {50087,50088,50089,50090,50105,
										51185,51186,51187,51188,51189,
										51250,51251,51252,51253,51254};
		if (id >= tier10[0] && id <= tier10[14]) {
			for (int idT: tier10) {
				if (id == idT) {
					tier = Tier.Tier10;
					break;
				}
			}
		}
	}
	
	public int compareTo(Item o) {
		if (comparedDPS > o.comparedDPS)
			return -1;
		else if (comparedDPS < o.comparedDPS)
			return 1;
		return 0;
	}
	
	public Attributes getAttributes() {
		return attr;
	}
	
	public float getComparedDPS() {
		return comparedDPS;
	}

	public String getIcon() {
		return icon;
	}
	
	public int getId() {
		return id;
	}

	public int getLvl() {
		return ilvl;
	}
	
	public void setLvl(int lvl) {
		this.ilvl = lvl;
	}
	
	public String getName() {
		return name;
	}

	public SlotType getSlot() {
		return slot;
	}
	
	public Socket getSocket(int index) {
		if (sockets.length>index)
			return sockets[index];
		return null;
	}
	
	public boolean hasSockets() {
		for (Socket s: sockets) {
			if (s != null)
				return true;
		}
		return false;
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
	
	public void setComparedDPS(float comparedDPS) {
		this.comparedDPS = comparedDPS;
	}
	
	public String toString() {
		return name+" ("+id+")";
	}
	
	public String getToolTip() {
		String s = "<html><b>"+name+"</b>";
		s += attr.getToolTip();
		if (hasSockets()) {
			s += "Socket Bonus";
			s += socketBonus.getToolTip();
		}
		s += "</html>";
		return s;
	}
	
	public static Item find(int id) {
		if (map == null)
			Item.load();
		if (map.containsKey(id))
			return map.get(id);
		return null;
	}
	
	public static ArrayList<Item> findSlot(SlotType slotType) {
		if (map == null)
			Item.load();
		ArrayList<Item> matches = new ArrayList<Item>();
		Collection<Item> items = map.values();
		Iterator<Item> iter = items.iterator();
		while (iter.hasNext()) {
			Item item = iter.next();
			if (item.matchesSlot(slotType) && (item.getFaction() == Faction.Both ||
					(item.getFaction() == Player.getInstance().getFaction())))
				matches.add(item);
		}
		return matches;
	}
	
	public static ArrayList<Item> findWeapon(Weapon.weaponType type) {
		if (map == null)
			Item.load();
		ArrayList<Item> matches = new ArrayList<Item>();
		Collection<Item> items = map.values();
		Iterator<Item> iter = items.iterator();
		while (iter.hasNext()) {
			Item item = iter.next();
			if (item instanceof Weapon) {
				Weapon weapon = (Weapon) item;
				if (weapon.getType() == type && (item.getFaction() == Faction.Both ||
						(item.getFaction() == Player.getInstance().getFaction())))
					matches.add(item);
			}
		}
		return matches;
	}

	public static ArrayList<Item> getAll() {
		if (map == null)
			Item.load();
		return new ArrayList<Item>(map.values());
	}

	@SuppressWarnings("unchecked")
	public static void load() {
		if (map == null)
			map = new HashMap<Integer,Item>();
		Set<Integer> keys = new TreeSet<Integer>();
		Document doc = Persistency.openXML(Persistency.FileType.Items);
		Item item;
		Element root = doc.getRootElement();
		for (Element e: (List<Element>) root.getChildren()) {
			String s = e.getChildText("slot");
			if (s != null && (s.equals("MainHand") || s.equals("OneHand")))
				item = new Weapon(e);
			else
				item = new Item(e);
			if (item.getId()>0) {
				keys.add(item.getId());
				if (!map.containsKey(item.getId()))
					map.put(item.getId(), item);
			}
			Set<Integer> diff = new TreeSet<Integer>(map.keySet());
			diff.removeAll(keys);
			for (int i: diff)
				map.remove(i);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void save() {
		Document doc = Persistency.openXML(Persistency.FileType.Items);
		Element root = doc.getRootElement();
		root.removeContent();
		TreeSet<Integer> keys = new TreeSet<Integer>(map.keySet());
		for (Integer key: keys) {
			Item item = map.get(key);
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
			Element eAttr = item.attr.toXML(null);
			eItem.getChildren().add(eAttr);
			if (item.hasSockets()) {
				Element eSockets = new Element("sockets");
				for (int index=0; index<=2; index++) {
					Socket s = item.getSocket(index);
					if (s == null)
						break;
					Element eSocket = new Element("socket");
					eSocket.setAttribute("index", String.valueOf(index));
					eSocket.setText(s.getType().name());
					eSockets.getChildren().add(eSocket);
				}
				Element eBonus = item.getSocketBonus().toXML("bonus");
				eSockets.getChildren().add(eBonus);
				eItem.getChildren().add(eSockets);
			}
			eSub = new Element("lvl");
			eSub.setText(String.valueOf(item.getLvl()));
			eItem.getChildren().add(eSub);
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
			eSub = new Element("icon");
			eSub.setText(item.getIcon());
			eItem.getChildren().add(eSub);
			
			root.getChildren().add(eItem);
		}
		Persistency.saveXML(doc, Persistency.FileType.Items);
	}
	
	public static void add(Item item) {
		if (item.id <= 0)
			return;
		if (map == null)
			Item.load();
		map.put(item.id, item);
	}
	
	public static void setBlacksmith(boolean b) {
		Collection<Item> items, items1, items2;
		items1 = findSlot(SlotType.Wrist);
		items2 = findSlot(SlotType.Hands);
		items = items1; items.addAll(items2);
		for (Item i: items)
			i.setExtraSocket(b);
	}

	public Attributes getSocketBonus() {
		return socketBonus;
	}

	public Tier getTier() {
		return tier;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSlot(SlotType slot) {
		this.slot = slot;
	}

	public void setSockets(Socket[] sockets) {
		this.sockets = sockets;
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

}
