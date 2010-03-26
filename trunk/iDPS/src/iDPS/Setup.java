package iDPS;

import iDPS.controllers.BuffController.Buff;
import iDPS.controllers.BuffController.Consumable;
import iDPS.controllers.BuffController.Debuff;
import iDPS.controllers.BuffController.Other;
import iDPS.gear.Gear;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;


public class Setup implements Cloneable, Comparable<Setup> {
	
	public enum Profession { Alchemy, Blacksmithing, Enchanting, Engineering, Inscription,
		Jewelcrafting, Leatherworking, Skinning, Tailoring };
	
	private static HashMap<Integer,Setup> map = null;
	private static int nextFreeId = 1;
	
	private int id;
	private String name;
	private Gear gear;
	private Talents talents;
	private Glyphs glyphs;
	private Race race;
	private boolean useTotT;
	private boolean useRupture;
	private float ruptureUptime;
	private boolean useExpose;
	private EnumSet<Profession> professions;
	private EnumMap<Buff,Boolean> buffs;
	private EnumMap<Consumable,Boolean> consumables;
	private EnumMap<Debuff,Boolean> debuffs;
	private EnumMap<Other,Boolean> other;
	
	public Setup() {
		gear = new Gear(this);
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
			} else if (s.equals("gear")) {
				gear = new Gear(this, eGear);
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
					else if (e.getName().equals("ruptureUptime"))
						ruptureUptime = Float.parseFloat(e.getText());
					else if (e.getName().equals("tott"))
						useTotT = Boolean.parseBoolean(e.getText());
					else if (e.getName().equals("expose"))
						useExpose = Boolean.parseBoolean(e.getText());
				}
			}
		}
	}
	
	public Setup(String name) {
		this();
		this.id = Setup.nextFreeId;
		Setup.nextFreeId++;
		this.name = name;
	}
	
	public Setup clone() {
		Setup clone = new Setup();
		clone.id = id;
		clone.name = name;
		clone.gear = gear.clone(clone);
		clone.talents = talents.clone();
		clone.glyphs = glyphs.clone();
		clone.race = race;
		clone.professions = professions.clone();
		clone.buffs = buffs.clone();
		clone.consumables = consumables.clone();
		clone.debuffs = debuffs.clone();
		clone.other = other.clone();
		clone.useRupture = useRupture;
		clone.ruptureUptime = ruptureUptime;
		clone.useTotT = useTotT;
		clone.useExpose = useExpose;
		return clone;
	}
	
	@SuppressWarnings("unchecked")
	private Element toXML() {
		Element eSetup = new Element("setup");
		eSetup.setAttribute("id", id+"");
		
		// Name
		Element eSub = new Element("name");
		eSub.setText(name);
		eSetup.getChildren().add(eSub);
		
		// Gear
		eSetup.getChildren().add(gear.toXML());
		
		// TalentSpec
		eSetup.getChildren().add(talents.toXML());
		
		// Glyphs
		eSetup.getChildren().add(glyphs.toXML());
		
		// Race
		if (race != null) {
			eSub = new Element("race");
			eSub.setAttribute("id", race.getId()+"");
			eSetup.getChildren().add(eSub);
		}
		
		// Professions
		eSub = new Element("professions");
		for (Profession p: Profession.values()) {
			if (!hasProfession(p))
				continue;
			Element eProf = new Element("profession");
			eProf.setText(p.name());
			eSub.getChildren().add(eProf);
		}
		eSetup.getChildren().add(eSub);
		
		// save Buffs
		eSub = new Element("buffs");
		for (Buff b: Buff.values()) {
			if (buffs.get(b)) {
				Element eBuff = new Element("buff");
				eBuff.setText(b.name());
				eSub.getChildren().add(eBuff);
			}
		}
		// save Consumables
		for (Consumable b: Consumable.values()) {
			if (consumables.get(b)) {
				Element elem2 = new Element("consumable");
				elem2.setText(b.name());
				eSub.getChildren().add(elem2);
			}
		}
		// save Debuffs
		for (Debuff b: Debuff.values()) {
			if (debuffs.get(b)) {
				Element elem2 = new Element("debuff");
				elem2.setText(b.name());
				eSub.getChildren().add(elem2);
			}
		}
		// save Other
		for (Other b: Other.values()) {
			if (other.get(b)) {
				Element elem2 = new Element("other");
				elem2.setText(b.name());
				eSub.getChildren().add(elem2);
			}
		}
		eSetup.getChildren().add(eSub);
		
		// save Rotation
		eSub = new Element("rotation");
		if (useExpose)
			eSub.getChildren().add(new Element("expose").setText("true"));
		if (useRupture)
			eSub.getChildren().add(new Element("rupture").setText("true"));
		if (useTotT)
			eSub.getChildren().add(new Element("tott").setText("true"));
		eSub.getChildren().add(new Element("ruptureUptime").setText(String.valueOf(ruptureUptime)));
		eSetup.getChildren().add(eSub);
		
		return eSetup;
	}
	
	public void clearId() {
		id = 0;
	}
	
	public int compareTo(Setup o) {
		return name.compareTo(o.name);
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
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
		Element setups = root.getChild("setups");
		if (setups == null) {
			Setup s = new Setup("default");
			Setup.add(s);
			app.setSetup(s);
			return;
		}
		int defGear = Integer.valueOf(setups.getAttributeValue("default"));
		List<Element> l = setups.getChildren();
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

	@SuppressWarnings("unchecked")
	public static void save(Application app) {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element elem = Persistency.getElement(doc, "setups");
		elem.removeContent();
		elem.setAttribute("default", Integer.toString(app.getSetup().getId()));
		for (Setup setup: map.values())
			elem.getChildren().add(setup.toXML());
		Persistency.saveXML(doc, Persistency.FileType.Settings);
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
				gear.setGem(7, gear.getItem(7).getMaxSocketIndex()+1, null);
				gear.setGem(8, gear.getItem(8).getMaxSocketIndex()+1, null);
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

	public Gear getGear() {
		return gear;
	}

	public void setGlyphs(Glyphs glyphs) {
		this.glyphs = glyphs;
	}

	public float getRuptureUptime() {
		return ruptureUptime;
	}

	public void setRuptureUptime(float ruptureUptime) {
		this.ruptureUptime = ruptureUptime;
	}

}
