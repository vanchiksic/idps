package iDPS;


import iDPS.gear.Setup;
import iDPS.gear.Armor.Faction;


public class Player {
	
	private static Player instance;
	
	private Setup equipped;
	
	private Player() {		
		equipped = new Setup();
	}
	
	public void setSetup(Setup gear) {
		equipped = gear;
	}
	
	public Setup getSetup() {
		return equipped;
	}
	
	public Faction getFaction() {
		return equipped.getRace().getFaction();
	}

	public static Player getInstance() {
		if (instance == null)
			instance = new Player();
		return instance;
	}
	
	/*@SuppressWarnings("unchecked")
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
	}*/

}
