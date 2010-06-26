package iDPS.gear;

import iDPS.Setup;
import iDPS.Talents;
import iDPS.gear.Armor.Faction;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


public class ItemParser {
	
	public ItemParser() {
	}

	private Armor importItem(int id) throws Exception {
		String urlString = "http://www.wowarmory.com/item-tooltip.xml?i=" + id;

		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1");
		conn.setRequestProperty("Cookie", "loginChecked=1");

		SAXBuilder builder = new SAXBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		Document document = builder.build(reader);
		Element root = document.getRootElement();
		Element armoryTooltip = root.getChild("itemTooltips").getChild("itemTooltip");

		if (armoryTooltip == null)
			throw new Exception("The xml = null :(");

		return Armor.createFromArmoryXML(armoryTooltip);
	}
	
	public static void main(String[] args) {
		ItemParser ip = new ItemParser();
		Talents.load();
		Setup.load();
		Armor.load();
		Iterator<Armor> iter = Armor.getAll().iterator();
		System.out.println(Armor.getAll().size()+" Items");
		try {
			while (iter.hasNext()) {
				Armor itemOrg = iter.next();
				if (itemOrg.getId()<=53000) {
					Armor.add(itemOrg);
					continue;
				}
				System.out.print("Item "+itemOrg.getId());
				Armor itemNew = ip.importItem(itemOrg.getId());
				if (itemOrg.getUniqueLimit() != 0) {
					itemNew.setUniqueLimit(itemOrg.getUniqueLimit());
					itemNew.setUniqueName(itemOrg.getUniqueName());
				}
				if (itemOrg.getIcon() != null)
					itemNew.setIcon(itemOrg.getIcon());
				if (itemOrg.getTag() != null)
					itemNew.setTag(itemOrg.getTag());
				if (itemOrg.getFaction() != Faction.Both)
					itemNew.setFaction(itemOrg.getFaction());
				if (itemOrg.getFilter().size() > 0)
					itemNew.setFilter(itemOrg.getFilter());
				System.out.println(" -> "+itemNew.getName());
				Armor.add(itemNew);
			}
			System.out.println("Saving...");
			Armor.save();
			System.out.println(Armor.getAll().size()+" Items saved.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
