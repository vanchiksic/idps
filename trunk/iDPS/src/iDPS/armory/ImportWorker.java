package iDPS.armory;

import iDPS.Application;
import iDPS.Glyphs;
import iDPS.Launcher;
import iDPS.Race;
import iDPS.Setup;
import iDPS.Talents;
import iDPS.Glyphs.Glyph;
import iDPS.Setup.Profession;
import iDPS.Talents.Talent;
import iDPS.gear.Armor;
import iDPS.gear.Enchant;
import iDPS.gear.Gem;
import iDPS.model.Calculations.ModelType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class ImportWorker extends SwingWorker<Setup,Void> {

	private String charName;
	private Realm realm;
	private Setup setup;

	private ArrayList<Integer> importItemIds;

	public ImportWorker(String charName, Realm realm) {
		super();
		this.charName = charName;
		this.realm = realm;

		importItemIds = new ArrayList<Integer>();

		setup = Launcher.getApp().getSetup().clone();
	}

	@Override
	protected Setup doInBackground() {
		try {
			importCharacter();
			if (importItemIds.size()>0) {
				System.out.println(importItemIds);
				double progress = (1D/(importItemIds.size()+2D))*100D;
				setProgress((int) Math.floor(progress));
				Thread.sleep(500);
				importItems();
				// importItems always ends with a sleep 500
				importCharacter();
			} else {
				setProgress(50);
			}
			Thread.sleep(500);
			importTalents();
			setProgress(100);
			Thread.sleep(100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return setup;
	}

	public void done() {
		Application app = Launcher.getApp();
		try {
			Setup setup = get();
			Setup.add(setup);
			app.setSetup(setup);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		app.getMainFrame().showGear();
	}

	@SuppressWarnings("unchecked")
	private void importCharacter() throws Exception {
		String urlString = "http://";
		urlString += realm.getRegion().name().toLowerCase();
		urlString += ".wowarmory.com/character-sheet.xml?r=";
		urlString += URLEncoder.encode(realm.getName(), "utf-8");
		urlString += "&n=";
		urlString += URLEncoder.encode(charName, "utf-8");

		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1");
		conn.setRequestProperty("Cookie", "loginChecked=1");

		//System.out.println("Armory URL: " + urlString);
		//System.out.println("Armory Response: " + conn.getResponseMessage());

		SAXBuilder builder = new SAXBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		Document document = builder.build(reader);
		Element root = document.getRootElement();

		Element characterInfo = root.getChild("characterInfo");
		Element eCharacter = characterInfo.getChild("character");
		if (!eCharacter.getAttributeValue("class").equals("Rogue"))
			throw new Exception("Dear sir, I don't believe you are a Rogue at all!\r\n\"There's an old saying in Tennessee — I know it's in Texas, probably in Tennessee — that says, fool me once, shame on — shame on you. Fool me — you can't get fooled again.\"");
		int raceId = Integer.valueOf(eCharacter.getAttributeValue("raceId"));
		setup.setRace(Race.find(raceId));

		Element characterTab = characterInfo.getChild("characterTab");
		
		
		// Professions
		for (Profession p: Profession.values())
			setup.setProfession(p, false);
		Element eProfs = characterTab.getChild("professions");
		for (Element eProf: (Collection<Element>) eProfs.getChildren()) {
			String name = eProf.getAttributeValue("name");
			try {
				Profession p = Profession.valueOf(name);
				setup.setProfession(p, true);
			} catch (IllegalArgumentException e) {
				System.err.println("Unknown Profession: "+name);
			}
		}


		int blizzItemSlotIds[] = { 0, 1, 2, 5, 4, 9, 10, 11, 7, 8, 12, 13, 14, 15, 3, 16, 17, 18, 6 };

		Element eItems = characterTab.getChild("items");
		// Looping through all equipped items
		for (Element eItem: (Collection<Element>) eItems.getChildren()) {
			int id = Integer.parseInt(eItem.getAttributeValue("id"));
			int rawSlot = Integer.parseInt(eItem.getAttributeValue("slot"));
			if (rawSlot < 0 || rawSlot == 3 || rawSlot == 18 || rawSlot >= blizzItemSlotIds.length)
				continue;
			int slot = blizzItemSlotIds[rawSlot];
			Vector<Integer> gemIds = new Vector<Integer>(3);
			gemIds.add(Integer.parseInt(eItem.getAttributeValue("gem0Id")));
			gemIds.add(Integer.parseInt(eItem.getAttributeValue("gem1Id")));
			gemIds.add(Integer.parseInt(eItem.getAttributeValue("gem2Id")));
			int enchantId = Integer.parseInt(eItem.getAttributeValue("permanentenchant"));

			Armor item = Armor.find(id);
			if (item == null)
				importItemIds.add(id);

			setup.getGear().setItem(slot, item);
			if (item != null) {
				Enchant enchant = Enchant.find(enchantId);
				setup.getGear().setEnchant(slot, enchant);
				ListIterator<Integer> gemIter = gemIds.listIterator();
				int socketIndex = 0;
				while (gemIter.hasNext()) {
					Gem gem = Gem.find(gemIter.next());
					setup.getGear().setGem(slot, socketIndex, gem);
					socketIndex++;
				}
			}
		}

		// Glyphs
		setup.setGlyphs(new Glyphs());
		Element eGlyphs = characterTab.getChild("glyphs");
		// Looping through all glyphs
		for (Element eGlyph: (Collection<Element>) eGlyphs.getChildren()) {
			int id = Integer.parseInt(eGlyph.getAttributeValue("id"));
			switch (id) {
			case 391:
				setup.getGlyphs().set(Glyph.AR, true);
				break;
			case 394:
				setup.getGlyphs().set(Glyph.BF, true);
				break;
			case 398:
				setup.getGlyphs().set(Glyph.Evi, true);
				break;
			case 399:
				setup.getGlyphs().set(Glyph.EA, true);
				break;
			case 406:
				setup.getGlyphs().set(Glyph.Rup, true);
				break;
			case 409:
				setup.getGlyphs().set(Glyph.SS, true);
				break;
			case 410:
				// SnD
				break;
			case 714:
				setup.getGlyphs().set(Glyph.HfB, true);
				break;
			case 715:
				setup.getGlyphs().set(Glyph.KS, true);
				break;
			case 716:
				setup.getGlyphs().set(Glyph.SD, true);
				break;
			case 732:
				// TotT
				break;
			case 733:
				setup.getGlyphs().set(Glyph.Mut, true);
				break;
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void importTalents() throws Exception {
		setup.setTalents(new Talents());

		String urlString = "http://";
		urlString += realm.getRegion().name().toLowerCase();
		urlString += ".wowarmory.com/character-talents.xml?r=";
		urlString += URLEncoder.encode(realm.getName(), "utf-8");
		urlString += "&n=";
		urlString += URLEncoder.encode(charName, "utf-8");

		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1");
		conn.setRequestProperty("Cookie", "loginChecked=1");

		//System.out.println("Armory URL: " + urlString);
		//System.out.println("Armory Response: " + conn.getResponseMessage());

		SAXBuilder builder = new SAXBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		Document document = builder.build(reader);
		Element root = document.getRootElement();
		Element talentElement = root.getChild("characterInfo").getChild("talents");

		Talents talents = setup.getTalents();

		for (Element talentGroup: (Collection<Element>) talentElement.getChildren("talentGroup")) {
			if (talentGroup.getAttributeValue("active") != null && talentGroup.getAttributeValue("active").equals("1")) {
				if (talentGroup.getAttributeValue("prim").equals("Assassination"))
					talents.setModel(ModelType.Mutilate);
				else if (talentGroup.getAttributeValue("prim").equals("Subtetly"))
					talents.setModel(ModelType.SubHemo);
				else
					talents.setModel(ModelType.Combat);

				String talentStrip = talentGroup.getChild("talentSpec").getAttributeValue("value");
				for (int i = 0; i < talentStrip.length()-1; i++) {
					int value = Integer.parseInt(talentStrip.substring(i, i+1));
					if (value > 0) {
						Talent t = Talents.getTalentPosition().get(i);
						if (t != null)
							talents.setTalentPoints(t, value);
					}
				}
			}
		}
	}

	private void importItems() throws Exception {
		int count = 0;
		for (int itemId: importItemIds) {
			Armor item = importItem(itemId);
			Armor.add(item);
			count++;
			double progress = ((count+1D)/(importItemIds.size()+2D))*100D;
			setProgress((int) Math.floor(progress));
			Thread.sleep(500);
		}
	}

	private Armor importItem(int id) throws Exception {
		String urlString = "http://";
		urlString += realm.getRegion().name().toLowerCase();
		urlString += ".wowarmory.com/item-tooltip.xml?i=" + id;
		urlString += "&r=" + URLEncoder.encode(realm.getName(), "utf-8");
		urlString += "&cn=" + URLEncoder.encode(charName, "utf-8");

		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1");
		conn.setRequestProperty("Cookie", "loginChecked=1");

		//System.out.println("Armory URL: " + urlString);
		//System.out.println("Armory Response: " + conn.getResponseMessage());

		SAXBuilder builder = new SAXBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		Document document = builder.build(reader);
		Element root = document.getRootElement();
		Element armoryTooltip = root.getChild("itemTooltips").getChild("itemTooltip");

		if (armoryTooltip == null)
			throw new Exception("The xml = null :(");

		return Armor.createFromArmoryXML(armoryTooltip);
	}

}
