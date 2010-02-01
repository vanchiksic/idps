package iDPS;

import java.util.EnumMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import iDPS.gear.Gear;
import iDPS.gear.Armor.Faction;


public class Player {
	
	public enum Stat { Agi, Str, Atp, Arp, Cri, Exp, Hit, Hst };
	public enum Profession { Alchemy, Blacksmithing, Enchanting, Engineering, Inscription,
		Jewelcrafting, Leatherworking, Skinning, Tailoring };
	private static Player instance;
	
	private Race race;
	private Gear equipped;
	private EnumMap<Profession,Boolean> professions;
	
	private Player() {		
		equipped = new Gear();
		professions = new EnumMap<Profession, Boolean>(Profession.class);
	}
	
	public void setSetup(Gear gear) {
		equipped = gear;
	}
	
	public Gear getSetup() {
		return equipped;
	}
	
	public Faction getFaction() {
		return race.getFaction();
	}

	public static Player getInstance() {
		if (instance == null)
			instance = new Player();
		return instance;
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}
	
	public Attributes getAttr() {
		Attributes attr = new Attributes();
		if (race != null)
			attr.add(race.getAttr());
		if (hasProfession(Profession.Skinning))
			attr.incCri(40F);
		return attr;
	}
	
	public void setProfession(Profession p, boolean b) {
		professions.put(p, b);
	}
	
	public boolean hasProfession(Profession p) {
		if (professions.containsKey(p))
			return professions.get(p);
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void saveProfessions() {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element profs = doc.getRootElement().getChild("professions");
		profs.removeContent();
		for (Profession p: Profession.values()) {
			if (Player.getInstance().hasProfession(p)) {
				Element prof = new Element("profession");
				prof.setText(p.name());
				profs.getChildren().add(prof);
			}
		}
		Persistency.saveXML(doc, Persistency.FileType.Settings);
	}
	
	@SuppressWarnings("unchecked")
	public void loadProfessions() {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element profs = doc.getRootElement().getChild("professions");
		for (Element e: (List<Element>) profs.getChildren()) {
			Profession p = Profession.valueOf(e.getText());
			setProfession(p, true);
		}
	}

}
