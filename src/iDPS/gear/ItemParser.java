package iDPS.gear;

import iDPS.Attributes;
import iDPS.Setup;
import iDPS.Talents;
import iDPS.gear.Armor.Faction;
import iDPS.gear.Armor.SlotType;
import iDPS.gear.Armor.SocketType;
import iDPS.gear.Weapon.WeaponType;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.htmlparser.*;
import org.htmlparser.filters.*;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


public class ItemParser {
	
	public ItemParser() {
	}

	public Armor loadItem(int id) {
			 try {
					Parser parser = new Parser("http://db.mmo-champion.com/i/"+id+"/");

					NodeFilter nf = new HasParentFilter(new HasAttributeFilter("id", "sitenav"));
					NodeList nl = parser.extractAllNodesThatMatch(nf);
					SimpleNodeIterator iter = nl.elements();
					boolean isWeapon = false;
					while (iter.hasMoreNodes()) {
							Node n = iter.nextNode().getFirstChild();

							if (n != null && n.getText().equals("Weapon")) {
									isWeapon = true;
									break;
							}
					}
					Armor item;
					if (isWeapon)
							item = new Weapon(id);
					else
							item = new Armor(id);
					//if (id >= 0 && id <= 0 && item.getFaction() != Faction.Alliance)
					//      item.setFaction(Faction.Horde);
					fetchAttributes(parser, item);
					fetchNameIcon(parser, item);
					fetchTag(parser, item);
					return item;
			 } catch (ParserException e) {
					e.printStackTrace();
					return null;
			}
	}

	private void fetchAttributes(Parser parser, Armor item) throws ParserException {
			NodeFilter nf;
			Node root, n;
			NodeList nl;
			parser.reset();
			nf = new HasAttributeFilter("id", "dview-tooltip");
			root = parser.extractAllNodesThatMatch(nf).elementAt(0);

			if (item instanceof Weapon) {
					Weapon w = (Weapon) item;
					nf = new HasParentFilter(new HasAttributeFilter("class", "tti-subclass"));
					n = root.getChildren().extractAllNodesThatMatch(nf, true).elementAt(0);
					String type = n.getFirstChild().getText();
					if (type.equals("Fist Weapon"))
							type = "Fist";
					w.setType(WeaponType.valueOf(type));

					nf = new HasParentFilter(new HasAttributeFilter("class", "tti-speed"));
					n = root.getChildren().extractAllNodesThatMatch(nf, true).elementAt(0);
					Pattern p = Pattern.compile("^Speed ([\\d\\.]+)$");
					Matcher m = p.matcher(n.getText());
					if (m.matches())
							w.setSpeed(Float.parseFloat(m.group(1)));

					nf = new HasParentFilter(new HasAttributeFilter("class", "tti-dps"));
					n = root.getChildren().extractAllNodesThatMatch(nf, true).elementAt(0);
					p = Pattern.compile("\\(([\\d\\.]+) damage per second\\)");
					m = p.matcher(n.getText());
					if (m.matches())
							w.setDps(Float.parseFloat(m.group(1)));
			}

			nf = new HasParentFilter(new HasAttributeFilter("class", "tti-stat"));
			nl = root.getChildren().extractAllNodesThatMatch(nf, true);
			SimpleNodeIterator iter = nl.elements();
			Pattern p1, p2;
			p1 = Pattern.compile("^\\+(\\d+) (\\w+)$");
			p2 = Pattern.compile("^Equip: (?:Improves|Increases) (?:your )?([\\w\\s]+) by (\\d+).$");
			Matcher m1, m2;
			Attributes attr = item.getAttr();
			attr.clear();
			while (iter.hasMoreNodes()) {
					n = iter.nextNode();
					m1 = p1.matcher(n.getText());
					m2 = p2.matcher(n.getText());
					if (m1.matches()) {
							if (m1.group(2).equals("Agility"))
									attr.setAgi(Integer.parseInt(m1.group(1)));
					}
					else if (m2.matches()) {
							if (m2.group(1).equals("attack power"))
									attr.setAtp(Integer.parseInt(m2.group(2)));
							else if (m2.group(1).equals("armor penetration rating"))
									attr.setArp(Integer.parseInt(m2.group(2)));
							else if (m2.group(1).equals("critical strike rating"))
									attr.setCri(Integer.parseInt(m2.group(2)));
							else if (m2.group(1).equals("expertise rating"))
									attr.setExp(Integer.parseInt(m2.group(2)));
							else if (m2.group(1).equals("haste rating"))
									attr.setHst(Integer.parseInt(m2.group(2)));
							else if (m2.group(1).equals("hit rating"))
									attr.setHit(Integer.parseInt(m2.group(2)));
					}
			}
			nf = new HasParentFilter(new HasAttributeFilter("class", "tti-slot"));
			String slot = root.getChildren().extractAllNodesThatMatch(nf, true).elementAt(0).getText();
			if (slot.equals("One-Hand"))
					slot = "OneHand";
			else if (slot.equals("Main Hand"))
					slot = "MainHand";
			else if (slot.equals("Off-Hand"))
					slot = "OffHand";
			else if (slot.equals("Thrown") || slot.equals("Gun") || slot.equals("Bow") || slot.equals("Crossbow"))
					slot = "Ranged";
			item.setSlot(SlotType.valueOf(slot));
			nf = new HasParentFilter(new HasAttributeFilter("class", "tti-level"));
			n = root.getChildren().extractAllNodesThatMatch(nf, true).elementAt(0);
			p1 = Pattern.compile("^Item Level (\\d+)$");
			m1 = p1.matcher(n.getText());
			if (m1.matches())
					item.setLvl(Integer.parseInt(m1.group(1)));

			nf = new HasParentFilter(new HasAttributeFilter("class", "tti-sockets"));
			nl = root.getChildren().extractAllNodesThatMatch(nf, true);
			ArrayList<SocketType> sockets = new ArrayList<SocketType>();
			attr = item.getSocketBonus();
			attr.clear();
			iter = nl.elements();
			int i = 0;
			while (iter.hasMoreNodes()) {
					n = iter.nextNode();
					String s = n.getFirstChild().getText().trim();
					if (n.getFirstChild().getFirstChild() != null)
							s = n.getFirstChild().getFirstChild().getText().trim();
					//System.out.println(s);
					if (s.equals("Meta Socket"))
							sockets.add(SocketType.Meta);
					else if (s.equals("Red Socket"))
							sockets.add(SocketType.Red);
					else if (s.equals("Yellow Socket"))
							sockets.add(SocketType.Yellow);
					else if (s.equals("Blue Socket"))
							sockets.add(SocketType.Blue);
					else if (s.equals("Socket Bonus:")) {
							String bonus = n.getChildren().elementAt(1).getFirstChild().getText();
							p1 = Pattern.compile("^\\+(\\d+) ([\\w\\s]+)$");
							m1 = p1.matcher(bonus);
							if (m1.matches()) {
									if (m1.group(2).equals("Agility"))
											attr.setAgi(Integer.parseInt(m1.group(1)));
									else if (m1.group(2).equals("Strength"))
											attr.setStr(Integer.parseInt(m1.group(1)));
									else if (m1.group(2).equals("Attack Power"))
											attr.setAtp(Integer.parseInt(m1.group(1)));
									else if (m1.group(2).equals("Hit Rating"))
											attr.setHit(Integer.parseInt(m1.group(1)));
									else if (m1.group(2).equals("Critical Strike Rating"))
											attr.setCri(Integer.parseInt(m1.group(1)));
									else if (m1.group(2).equals("Armor Penetration Rating"))
											attr.setArp(Integer.parseInt(m1.group(1)));
									else if (m1.group(2).equals("Haste Rating"))
											attr.setHst(Integer.parseInt(m1.group(1)));
									else if (m1.group(2).equals("Expertise Rating"))
											attr.setExp(Integer.parseInt(m1.group(1)));
									else
											System.err.println("Unrecognized Socket Bonus: "+m1.group(2));
							}
					} else {
							System.err.println(s);
					}
					i++;
			}
			item.setSockets(sockets);
	}

	private void fetchNameIcon(Parser parser, Armor item) throws ParserException {
			parser.reset();
			NodeFilter nf = new AndFilter(
									new TagNameFilter("a"),
									new HasParentFilter(new TagNameFilter("h1"))
									);
			TagNode n = (TagNode) parser.extractAllNodesThatMatch(nf).elementAt(0);
			String url = n.getAttribute("href");
			item.setIcon(url.substring(12));
			item.setName(Translate.decode(n.getParent().getLastChild().getText().trim()));
	}

	private void fetchTag(Parser parser, Armor item) throws ParserException {
			parser.reset();
			NodeFilter nf = new HasParentFilter(new AndFilter(
									new TagNameFilter("a"),
									new HasParentFilter(new AndFilter(
													new TagNameFilter("li"),
													new HasParentFilter(new HasAttributeFilter("id","taglist")))
											)
									));
			Node n = parser.extractAllNodesThatMatch(nf).elementAt(0);
			if (n != null)
					item.setTag(n.getText());
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
				if (itemOrg.getId()<=0) {
					Armor.add(itemOrg);
					continue;
				}
				System.out.print("Item "+itemOrg.getId());
				Armor itemNew = ip.importItem(itemOrg.getId());
				ip.loadSocketBonus(itemNew);
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
