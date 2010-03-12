package iDPS.gear;

import iDPS.Application;
import iDPS.Attributes;
import iDPS.Glyphs;
import iDPS.Persistency;
import iDPS.Race;
import iDPS.Talents;
import iDPS.BuffController.Buff;
import iDPS.BuffController.Consumable;
import iDPS.BuffController.Debuff;
import iDPS.BuffController.Other;
import iDPS.gear.Armor.SocketType;
import iDPS.model.Calculations;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;


public class Setup implements Comparable<Setup> {
	
	public enum Profession { Alchemy, Blacksmithing, Enchanting, Engineering, Inscription,
		Jewelcrafting, Leatherworking, Skinning, Tailoring };
	
	private static HashMap<Integer,Setup> map = null;
	private static int nextFreeId = 1;
	
	private Attributes attr;
	private HashMap<Integer,Integer> containsMap;
	private Enchant[] enchants;
	private Gem[][] gems;
	private int id = 0;
	private Armor[] items;
	private String name;
	
	private Talents talents;
	private Glyphs glyphs;
	private Race race;
	private boolean useTotT;
	private boolean useRupture;
	private boolean useExpose;
	private EnumSet<Profession> professions;
	private EnumMap<Buff,Boolean> buffs;
	private EnumMap<Consumable,Boolean> consumables;
	private EnumMap<Debuff,Boolean> debuffs;
	private EnumMap<Other,Boolean> other;
		
	private boolean[] socketBonus;
	
	private EnumMap<Armor.Tier,Integer> tiers;
	private HashMap<String, int[]> uniqueMap;
	
	public Setup() {
		items = new Armor[19];
		gems = new Gem[19][3];
		enchants = new Enchant[19];
		socketBonus = new boolean[19];
		attr = new Attributes();
		containsMap = new HashMap<Integer,Integer>();
		uniqueMap = new HashMap<String,int[]>();
		tiers = new EnumMap<Armor.Tier,Integer>(Armor.Tier.class);
		for (Armor.Tier t: Armor.Tier.values())
			tiers.put(t, 0);
		talents = new Talents();
		glyphs = new Glyphs();
		race = new Race();
		professions = EnumSet.noneOf(Profession.class);
		
		buffs = new EnumMap<Buff,Boolean>(Buff.class);
		for (Buff b: Buff.values())
			buffs.put(b, false);
		consumables = new EnumMap<Consumable,Boolean>(Consumable.class);
		for (Consumable b: Consumable.values())
			consumables.put(b, false);
		debuffs = new EnumMap<Debuff,Boolean>(Debuff.class);
		for (Debuff b: Debuff.values())
			debuffs.put(b, false);
		other = new EnumMap<Other,Boolean>(Other.class);
		for (Other b: Other.values())
			other.put(b, false);
	}
	
	@SuppressWarnings("unchecked")
	public Setup(Element element) {
		this();
		try {
			id = Integer.parseInt(element.getAttributeValue("id"));
		} catch(NumberFormatException e) {
			id = 0;
		}
		if (id >= Setup.nextFreeId)
			Setup.nextFreeId = id+1;
		Iterator<Element> iter = element.getChildren().iterator();
		while (iter.hasNext()) {
			Element eGear = iter.next();
			String s = eGear.getName();
			if (s.equals("name"))
				name = eGear.getText();
			else if (s.equals("talents"))
				talents = new Talents(eGear);
			else if (s.equals("glyphs"))
				glyphs = new Glyphs(eGear);
			else if (s.equals("race"))
				race = Race.find(Integer.parseInt(eGear.getAttributeValue("id")));
			else if (s.equals("professions")) {
				Iterator<Element> iter2 = eGear.getChildren().iterator();
				while (iter2.hasNext()) {
					Element eProf = iter2.next();
					professions.add(Profession.valueOf(eProf.getText()));
				}
			}
			else if (s.equals("items")) {
				Iterator<Element> iter2 = eGear.getChildren().iterator();
				while (iter2.hasNext()) {
					Element eItem = iter2.next();
					int iid = Integer.parseInt(eItem.getAttributeValue("id"));
					int slot = Integer.parseInt(eItem.getAttributeValue("slot"));
					Armor item = Armor.find(iid);
					if (item!=null)
						setItem(slot, item);
					Iterator<Element> iter3 = eItem.getChildren().iterator();
					while (iter3.hasNext()) {
						Element eGem = iter3.next();
						int index = Integer.parseInt(eGem.getAttributeValue("index"));
						int gemId = Integer.parseInt(eGem.getAttributeValue("id"));
						setGem(slot,index,Gem.find(gemId));
					}
				}
			} else if (s.equals("enchants")) {
				Iterator<Element> iter2 = eGear.getChildren().iterator();
				while (iter2.hasNext()) {
					Element eEnchant = iter2.next();
					int id = Integer.parseInt(eEnchant.getAttributeValue("id"));
					int slot = Integer.parseInt(eEnchant.getAttributeValue("slot"));
					Enchant enchant = Enchant.find(id);
					if (enchant!=null)
						setEnchant(slot, enchant);
				}
			} else if (s.equals("buffs")) {
				for (Element e: (List<Element>) eGear.getChildren()) {
					if (e.getName().equals("buff")) {
						try {
							Buff b = Buff.valueOf(e.getText());
							buffs.put(b, true);
						} catch (IllegalArgumentException ex) {}
					} else if (e.getName().equals("consumable")) {
						try {
							Consumable b = Consumable.valueOf(e.getText());
							consumables.put(b, true);
						} catch (IllegalArgumentException ex) { }	
					} else if (e.getName().equals("debuff")) {
						try {
							Debuff b = Debuff.valueOf(e.getText());
							debuffs.put(b, true);
						} catch (IllegalArgumentException ex) { }
					} else if (e.getName().equals("other")) {
						try {
							Other b = Other.valueOf(e.getText());
							other.put(b, true);
						} catch (IllegalArgumentException ex) { }
					}
				}
			} else if (s.equals("rotation")) {
				for (Element e: (List<Element>) eGear.getChildren()) {
					if (e.getName().equals("rupture"))
						useRupture = Boolean.parseBoolean(e.getText());
					if (e.getName().equals("tott"))
						useTotT = Boolean.parseBoolean(e.getText());
					if (e.getName().equals("expose"))
						useExpose = Boolean.parseBoolean(e.getText());
				}
			}
		}
	}
	
	public Setup(Setup copy) {
		this();
		id = copy.id;
		name = copy.name;
		items = copy.items.clone();
		gems = copy.gems.clone();
		for (int i=0; i<gems.length; i++)
			gems[i] = copy.gems[i].clone();
		enchants = copy.enchants.clone();
		socketBonus = copy.socketBonus.clone();
		attr = copy.attr.clone();
		containsMap = new HashMap<Integer,Integer>(copy.containsMap);
		uniqueMap = new HashMap<String,int[]>();
		for (String s: copy.uniqueMap.keySet()) {
			int[] vect = copy.uniqueMap.get(s).clone();
			uniqueMap.put(s, vect);
		}
		tiers = new EnumMap<Armor.Tier,Integer>(copy.tiers);
		talents = copy.talents.clone();
		glyphs = copy.glyphs.clone();
		race = copy.race;
		professions = copy.professions.clone();
		buffs = copy.buffs.clone();
		consumables = copy.consumables.clone();
		debuffs = copy.debuffs.clone();
		other = copy.other.clone();
		useRupture = copy.useRupture;
		useTotT = copy.useTotT;
		useExpose = copy.useExpose;
	}
	
	public Setup(String name) {
		this();
		this.id = Setup.nextFreeId;
		Setup.nextFreeId++;
		this.name = name;
	}
	
	public boolean canAdd(Item i) {
		if (i == null)
			return true;
		if (i.getUniqueLimit()>0) {
			int[] vect;
			if (uniqueMap.containsKey(i.getUniqueName())) {
				vect = uniqueMap.get(i.getUniqueName());
				if (vect[0] >= vect[1])
					return false;
			}
		}
		return true;
	}
	
	public void reset() {
		for (int i=0; i<=18; i++) {
			setItem(i, null);
			setEnchant(i, null);
		}
	}
	
	public Setup clone() {
		Setup g = new Setup(this);
		return g;
	}
	
	public void clearId() {
		id = 0;
	}
	
	public int compareTo(Setup o) {
		return name.compareTo(o.name);
	}
	
	public int contains(int id) {
		if (containsMap.containsKey(id))
			return containsMap.get(id);
		return 0;
	}
	
	public boolean containsAny(int... ids) {
		for (int id: ids) {
			if (containsMap.containsKey(id))
				return true;
		}
		return false;
	}
	
	private void containsDec(Item item) {
		if (item.getUniqueLimit()>0) {
			int[] vect;
			if (uniqueMap.containsKey(item.getUniqueName())) {
				vect = uniqueMap.get(item.getUniqueName());
				if (vect[0] > 0)
					vect[0]--;
			}
		}
		if (containsMap.containsKey(item.getId())) {
			int c = containsMap.get(item.getId())-1;
			if (c>0)
				containsMap.put(item.getId(), c--);
			else
				containsMap.remove(item.getId());
		}
	}
	
	private boolean containsInc(Item item) {
		if (item.getUniqueLimit()>0) {
			int[] vect;
			if (uniqueMap.containsKey(item.getUniqueName())) {
				vect = uniqueMap.get(item.getUniqueName());
				if (vect[0] >= vect[1])
					return false;
				vect[0]++;
			} else {
				vect = new int[2];
				vect[0] = 1;
				vect[1] = item.getUniqueLimit();
				uniqueMap.put(item.getUniqueName(), vect);
			}
		}
		int c = (containsMap.containsKey(item.getId())) ? containsMap.get(item.getId()) : 0;
		c++;
		containsMap.put(item.getId(), c);
		return true;
	}
	
	public void gemBest(int slot) {
		Calculations m = Calculations.createInstance();
		Setup gear;
		Armor item = getItem(slot);
		if (item == null || !item.hasSockets())
			return;
		Gem[] gemsAny = new Gem[3], gemsMatch = new Gem[3], gemsTemp, gemsFinal;
		GemComparison gc;
		float dpsAny = 0, dpsMatch = 0;
		// Iteration 1: Any Color
		gear = this.clone();
		for (int index=0; index<=2; index++) {
			if (item.getSocket(index) == null)
				continue;
			gc = new GemComparison(this, slot, index, true);
			gear.setGem(slot, index, gc.getBestGem());
			gemsAny[index] = gc.getBestGem();
		}
		m.calculate(gear);
		dpsAny = m.getTotalDPS();
		if (isSocketBonusActive(slot))
			dpsMatch = dpsAny;
		else {
			// Iteration 2: Matching Color
			int[][] runs;
			if (this.getItem(slot).getMaxSocketIndex() == 1)
				runs = new int[][] {{0}};
			if (this.getItem(slot).getMaxSocketIndex() == 2)
				runs = new int[][] {{0,1},{1,0}};
			else
				runs = new int[][] {{0,1,2},{0,2,1},{1,0,2},{1,2,0},{2,1,0},{2,0,1}};
			for (int[] run: runs) {
				gemsTemp = new Gem[3];
				gear = this.clone();
				for (int index: run) {
					if (item.getSocket(index) == null)
						continue;
					gc = new GemComparison(this, slot, index, false);
					gear.setGem(slot, index, gc.getBestGem());
					gemsTemp[index] = gc.getBestGem();
				}
				m.calculate(gear);
				if (m.getTotalDPS() > dpsMatch) {
					dpsMatch = m.getTotalDPS();
					gemsMatch = gemsTemp;
				}
			}
		}
		// Pick Best Setup
		if (dpsAny > dpsMatch)
			gemsFinal = gemsAny;
		else
			gemsFinal =  gemsMatch;
		// Apply Gems
		for (int index=0; index<=2; index++) {
			if (gemsFinal[index] == null)
				continue;
			setGem(slot, index, gemsFinal[index]);
		}
	}
	
	public Attributes getAttributes() {
		Attributes attr = new Attributes(this.attr);
		if (getRace() != null)
			attr.add(getRace().getAttr());
		if (hasProfession(Profession.Skinning))
			attr.incCri(40F);
		return attr;
	}
	
	public Enchant getEnchant(int slot) {
		return enchants[slot];
	}
	
	public Gem getGem(int slot, int index) {
		return gems[slot][index];
	}
	
	public Gem[] getGems(int slot) {
		return gems[slot];
	}
	
	public int getId() {
		return id;
	}
	
	public Armor getItem(int slot) {
		return items[slot];
	}
	
	public String getName() {
		return name;
	}
	
	public int getTier10() {
		return tiers.get(Armor.Tier.Tier10);
	}
	
	public int getTier9() {
		return tiers.get(Armor.Tier.Tier9);
	}
	
	public Weapon getWeapon1() {
		Weapon weapon = (Weapon) getItem(16);
		if (weapon == null)
			weapon = new Weapon(Weapon.weaponType.Dagger);
		return weapon;
	}
	
	public Weapon getWeapon2() {
		Weapon weapon = (Weapon) getItem(17);
		if (weapon == null)
			weapon = new Weapon(Weapon.weaponType.Dagger);
		return weapon;
	}
	
	public boolean hasChaoticESD() {
		return (contains(41398)>0);
	}
	
	public boolean isEnchanted(int slot) {
		return (enchants[slot]!=null);
	}
	
	public boolean isSocketBonusActive(int slot) {
		Armor item = getItem(slot);
		if (item == null)
			return false;
		if (!item.hasSockets())
			return false;
		Gem[] gems = getGems(slot);
		SocketType socket;
		for (int index=0; index<=item.getMaxSocketIndex(); index++) {
			socket = item.getSocket(index);
			if (gems[index] == null || !gems[index].isMatch(socket))
				return false;
		}
		return true;
	}
	
	public void print() {
		System.out.println(this);
		for (int i=0; i<=18; i++) {
			if (items[i] != null)
				printItem(i);
			else
				System.out.format("Slot %02d: Empty%n", i);
		}
		System.out.println();
	}
	
	public void printItem(int slot) {
		Armor item = getItem(slot);
		if (item == null)
			return;
		System.out.println("Slot "+slot+": "+item);
		for (int index=0; index<=2; index++) {
			SocketType socket = item.getSocket(index);
			if (socket == null)
				continue;
			Gem gem = getGem(slot,index);
			if (gem == null)
				System.out.format("%15s: %s%n", socket.toString(), "Empty");
			else
				System.out.format("%15s: %s%n", socket.toString(), gem.toString());
		}
	}
	
	public void setEnchant(int slot, Enchant enchant) {
		Enchant oldenchant = enchants[slot];
		if (oldenchant != null)
			attr.sub(oldenchant.getAttributes());
		if (enchant != null)
			attr.add(enchant.getAttributes());
		enchants[slot] = enchant;
	}
	
	public void setGem(int slot, int index, Gem gem) {
		Armor item = getItem(slot);
		if (item ==  null)
			return;
		int max = item.getMaxSocketIndex();
		if (hasExtraSocket(slot))
			max++;
		if (index > max)
			return;
		// Remove attributes from socket bonus
		if (socketBonus[slot])
			attr.sub(item.getSocketBonus());
		Gem oldgem = getGem(slot, index);
		if (oldgem != null) {
			attr.sub(oldgem.getAttr());
			containsDec(oldgem);
			gems[slot][index] = null;
		}
		if (gem != null) {
			// Check if we can equip it
			if (containsInc(gem)) {
				attr.add(gem.getAttr());
				gems[slot][index] = gem;
			}
		}
		// Check if socket bonus is active
		socketBonus[slot] = isSocketBonusActive(slot);
		// if yes add bonus attributes again
		if (socketBonus[slot])
			attr.add(item.getSocketBonus());
	}
	
	public void setItem(int slot, Armor item) {
		Armor olditem = items[slot];
		Gem[] oldgems = getGems(slot).clone();
		if (olditem != null) {
			attr.sub(olditem.getAttr());
			for (int index=0; index<=2; index++)
				setGem(slot, index, null);
			if (olditem.getTier() != null)
				tierDec(olditem.getTier());
			containsDec(olditem);
			items[slot] = null;
		}
		if (item != null) {
			items[slot] = item;
			attr.add(item.getAttr());
			for (int index=0; index<=item.getMaxSocketIndex(); index++)
				setGem(slot, index, oldgems[index]);
			if (item.getTier() != null)
				tierInc(item.getTier());
			containsInc(item);
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private void tierDec(Armor.Tier t) {
		int c = tiers.get(t);
		if (c > 0) {
			c--;
			tiers.put(t, c);
		}
	}
	
	private void tierInc(Armor.Tier t) {
		int c = tiers.get(t);
		c++;
		tiers.put(t, c);
	}
	
	public static void add(Setup s) {
		if (s.id==0) {
			s.id = Setup.nextFreeId;
			Setup.nextFreeId++;
		}
		map.put(s.id, s);
	}
	
	public static void clear() {
		map.clear();
		nextFreeId = 1;
	}
	
	public static Setup find(int id) {
		if (map.containsKey(id))
			return map.get(id);
		return null;
	}
	
	public static ArrayList<Setup> getAll() {
		if (map != null)
			return new ArrayList<Setup>(map.values());
		return new ArrayList<Setup>();
	}
	
	public static void load() {
		load(null);
	}

	@SuppressWarnings("unchecked")
	public static void load(Application app) {
		map = new HashMap<Integer,Setup>();
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element root = doc.getRootElement();
		Element gearconfigs = root.getChild("gearconfigs");
		if (gearconfigs == null) {
			Setup s = new Setup("default");
			Setup.add(s);
			app.setSetup(s);
			return;
		}
		int defGear = Integer.valueOf(gearconfigs.getAttributeValue("default"));
		List<Element> l = gearconfigs.getChildren();
		Iterator<Element> li = l.iterator();
		while (li.hasNext()) {
			Element e = li.next();
			Setup s = new Setup(e);
			if (s.getId()>0) {
				map.put(s.getId(), s);
				if (app != null && s.getId()==defGear)
					app.setSetup(s);
			}
		}
	}

	public static void remove(Setup g) {
		map.remove(g.id);
	}

	public static void save(Application app) {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element elem = Persistency.getElement(doc, "gearconfigs");
		elem.removeContent();
		elem.setAttribute("default", Integer.toString(app.getSetup().getId()));
		Iterator<Setup> i = map.values().iterator();
		while (i.hasNext())
			saveSetup(elem, i.next());
		Persistency.saveXML(doc, Persistency.FileType.Settings);
	}

	@SuppressWarnings("unchecked")
	private static void saveSetup(Element root, Setup gear) {
		Element eSetup, eName, eItems, eItem, eGem, eEnchant, eEnchants;
		eSetup = new Element("gear");
		eSetup.setAttribute("id", gear.getId()+"");
		
		eName = new Element("name");
		eName.setText(gear.getName());
		eSetup.getChildren().add(eName);
		
		// TalentSpec
		eSetup.getChildren().add(gear.getTalents().toXML());
		
		// Glyphs
		eSetup.getChildren().add(gear.getGlyphs().toXML());
		
		// Race
		if (gear.getRace() != null && gear.getRace().getId() > 0) {
			eName = new Element("race");
			eName.setAttribute("id", gear.getRace().getId()+"");
			eSetup.getChildren().add(eName);
		}
		
		// Professions
		eName = new Element("professions");
		for (Profession p: Profession.values()) {
			if (!gear.hasProfession(p))
				continue;
			Element eProf = new Element("profession");
			eProf.setText(p.name());
			eName.getChildren().add(eProf);
		}
		eSetup.getChildren().add(eName);
		
		// Items
		eItems = new Element("items");
		for (int i=0; i<=18; i++) {
			if (gear.getItem(i)==null)
				continue;
			eItem = new Element("item");
			eItem.setAttribute("id", gear.getItem(i).getId()+"");
			eItem.setAttribute("slot", i+"");
			for (int j=0; j<=2; j++) {
				Gem gem = gear.getGem(i, j);
				if (gem != null) {
					eGem = new Element("gem");
					eGem.setAttribute("index",j+"");
					eGem.setAttribute("id", gem.getId()+"");
					eItem.getChildren().add(eGem);
				}
			}
			eItems.getChildren().add(eItem);
		}
		eSetup.getChildren().add(eItems);
		
		// Enchants
		eEnchants = new Element("enchants");
		for (int i=0; i<=18; i++) {
			if (gear.getEnchant(i)==null)
				continue;
			eEnchant = new Element("enchant");
			eEnchant.setAttribute("id", gear.getEnchant(i).getId()+"");
			eEnchant.setAttribute("slot", i+"");
			eEnchants.getChildren().add(eEnchant);
		}
		eSetup.getChildren().add(eEnchants);
		root.getChildren().add(eSetup);
		
		// save Buffs
		eName = new Element("buffs");
		for (Buff b: Buff.values()) {
			if (gear.buffs.get(b)) {
				Element elem2 = new Element("buff");
				elem2.setText(b.name());
				eName.getChildren().add(elem2);
			}
		}
		// save Consumables
		for (Consumable b: Consumable.values()) {
			if (gear.consumables.get(b)) {
				Element elem2 = new Element("consumable");
				elem2.setText(b.name());
				eName.getChildren().add(elem2);
			}
		}
		// save Debuffs
		for (Debuff b: Debuff.values()) {
			if (gear.debuffs.get(b)) {
				Element elem2 = new Element("debuff");
				elem2.setText(b.name());
				eName.getChildren().add(elem2);
			}
		}
		// save Other
		for (Other b: Other.values()) {
			if (gear.other.get(b)) {
				Element elem2 = new Element("other");
				elem2.setText(b.name());
				eName.getChildren().add(elem2);
			}
		}
		eSetup.getChildren().add(eName);
		// save Rotation
		eName = new Element("rotation");
		if (gear.useExpose)
			eName.getChildren().add(new Element("expose").setText("true"));
		if (gear.useRupture)
			eName.getChildren().add(new Element("rupture").setText("true"));
		if (gear.useTotT)
			eName.getChildren().add(new Element("tott").setText("true"));
		eSetup.getChildren().add(eName);
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}
	
	public void setProfession(Profession p, boolean b) {
		if (b)
			professions.add(p);
		else {
			// Remove Gems in sockets we dont have anymore
			if (p == Profession.Blacksmithing) {
				setGem(7, getItem(7).getMaxSocketIndex()+1, null);
				setGem(8, getItem(8).getMaxSocketIndex()+1, null);
			}
			professions.remove(p);
		}
	}
	
	public boolean hasProfession(Profession p) {
		return professions.contains(p);
	}

	public boolean hasExtraSocket(int slot) {
		if (slot == 9)
			return true;
		if ((slot == 7 || slot == 8) && hasProfession(Profession.Blacksmithing))
			return true;
		return false;
	}

	public EnumMap<Buff, Boolean> getBuffs() {
		return buffs;
	}

	public EnumMap<Consumable, Boolean> getConsumables() {
		return consumables;
	}

	public EnumMap<Debuff, Boolean> getDebuffs() {
		return debuffs;
	}
	
	public EnumMap<Other, Boolean> getOther() {
		return other;
	}

	public boolean isUseTotT() {
		return useTotT;
	}

	public void setUseTotT(boolean useTotT) {
		this.useTotT = useTotT;
	}

	public boolean isUseRupture() {
		return useRupture;
	}

	public void setUseRupture(boolean useRupture) {
		this.useRupture = useRupture;
	}

	public boolean isUseExpose() {
		return useExpose;
	}

	public void setUseExpose(boolean useExpose) {
		this.useExpose = useExpose;
	}

	public Talents getTalents() {
		return talents;
	}
	
	public void setTalents(Talents talents) {
		this.talents = talents;
	}
	
	public Glyphs getGlyphs() {
		return glyphs;
	}

}
