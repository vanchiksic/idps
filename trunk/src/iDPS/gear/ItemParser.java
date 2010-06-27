package iDPS.gear;

import iDPS.Setup;
import iDPS.Talents;
import iDPS.gear.Armor.Faction;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.htmlparser.*;
import org.htmlparser.filters.*;
import org.htmlparser.util.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


public class ItemParser {
	
	public ItemParser() {
	}

	private Armor importItem(int id) throws Exception {
		String urlString = "http://www.wowarmory.com/item-tooltip.xml?i="+id+"&r=Twilight%27s+Hammer&cn=Chack";

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

	private void loadSocketBonus(Armor armor) {
		try {
			URL url = new URL("http://www.wowhead.com/item=" + armor.getId());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			Pattern p = Pattern.compile(".*>Socket Bonus: \\+(\\d+) ([\\w\\s]+)<.*");
			String line = reader.readLine();
			while (line != null) {
				Matcher m = p.matcher(line);
				if (m.matches()) {
					if (m.group(2).equals("Agility"))
						armor.getSocketBonus().setAgi(Integer.parseInt(m.group(1)));
					else if (m.group(2).equals("Strength"))
						armor.getSocketBonus().setStr(Integer.parseInt(m.group(1)));
					else if (m.group(2).equals("Attack Power"))
						armor.getSocketBonus().setAtp(Integer.parseInt(m.group(1)));
					else if (m.group(2).equals("Hit Rating"))
						armor.getSocketBonus().setHit(Integer.parseInt(m.group(1)));
					else if (m.group(2).equals("Critical Strike Rating"))
						armor.getSocketBonus().setCri(Integer.parseInt(m.group(1)));
					else if (m.group(2).equals("Armor Penetration Rating"))
						armor.getSocketBonus().setArp(Integer.parseInt(m.group(1)));
					else if (m.group(2).equals("Haste Rating"))
						armor.getSocketBonus().setHst(Integer.parseInt(m.group(1)));
					else if (m.group(2).equals("Expertise Rating"))
						armor.getSocketBonus().setExp(Integer.parseInt(m.group(1)));
					else
						System.err.println("Unrecognized Socket Bonus: "+m.group(2));
					break;
				}
				line = reader.readLine();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void loadSocketBonusMMO(Armor armor) {
		try {
			Parser parser = new Parser("http://db.mmo-champion.com/i/" + armor.getId() + "/");
			NodeFilter nf = new HasAttributeFilter("class", "tti-socket_bonus");
			Node div = parser.extractAllNodesThatMatch(nf).elementAt(0);
			if (div == null)
				return;
			String bonus = div.getChildren().elementAt(1).getFirstChild().getText();
			Pattern p = Pattern.compile("^\\+(\\d+) ([\\w\\s]+)$");
			Matcher m = p.matcher(bonus);
			if (m.matches()) {
				if (m.group(2).equals("Agility"))
					armor.getSocketBonus().setAgi(Integer.parseInt(m.group(1)));
				else if (m.group(2).equals("Strength"))
					armor.getSocketBonus().setStr(Integer.parseInt(m.group(1)));
				else if (m.group(2).equals("Attack Power"))
					armor.getSocketBonus().setAtp(Integer.parseInt(m.group(1)));
				else if (m.group(2).equals("Hit Rating"))
					armor.getSocketBonus().setHit(Integer.parseInt(m.group(1)));
				else if (m.group(2).equals("Critical Strike Rating"))
					armor.getSocketBonus().setCri(Integer.parseInt(m.group(1)));
				else if (m.group(2).equals("Armor Penetration Rating"))
					armor.getSocketBonus().setArp(Integer.parseInt(m.group(1)));
				else if (m.group(2).equals("Haste Rating"))
					armor.getSocketBonus().setHst(Integer.parseInt(m.group(1)));
				else if (m.group(2).equals("Expertise Rating"))
					armor.getSocketBonus().setExp(Integer.parseInt(m.group(1)));
				else
					System.err.println("Unrecognized Socket Bonus: "+m.group(2));
			}
		} catch (ParserException ex) {
			ex.printStackTrace();
		}
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
				if (itemOrg.getId()<=53100) {
					Armor.add(itemOrg);
					continue;
				}
				System.out.print("Item "+itemOrg.getId());
				Armor itemNew = ip.importItem(itemOrg.getId());
				if (itemNew.hasSockets()) {
					ip.loadSocketBonus(itemNew);
				}
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
