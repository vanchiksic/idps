package iDPS.gear;

import iDPS.Attributes;
import iDPS.gear.Item.Faction;
import iDPS.gear.Item.SlotType;
import iDPS.gear.Socket.SocketType;
import iDPS.gear.Weapon.weaponType;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.*;
import org.htmlparser.filters.*;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.*;


public class ItemParser {
	
	public ItemParser() {
	}
	
	public Item loadItem(int id) {
		 try {
			Parser parser = new Parser("http://db.mmo-champion.com/i/"+id+"/");
			
			NodeFilter nf = new HasParentFilter(new HasAttributeFilter("id", "sitenav"));
			NodeList nl = parser.extractAllNodesThatMatch(nf);
			SimpleNodeIterator iter = nl.elements();
			boolean isWeapon = false;
			while (iter.hasMoreNodes()) {
				Node n = iter.nextNode().getFirstChild();
				
				if (n != null && n.getText().equals("Weapons")) {
					isWeapon = true;
					break;
				}
			}
			Item item;
			if (isWeapon)
				item = new Weapon(id);
			else
				item = new Item(id);
			//if (id >= 0 && id <= 0 && item.getFaction() != Faction.Alliance)
			//	item.setFaction(Faction.Horde);
			fetchAttributes(parser, item);
			fetchNameIcon(parser, item);
			fetchTag(parser, item);
			return item;
		 } catch (ParserException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void fetchAttributes(Parser parser, Item item) throws ParserException {
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
			w.setType(weaponType.valueOf(type));
			
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
		Attributes attr = item.getAttributes();
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
		if (slot.equals("Thrown") || slot.equals("Gun") || slot.equals("Bow") || slot.equals("Crossbow"))
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
		Socket[] sockets = new Socket[3];
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
				sockets[i] = new Socket(item, i, SocketType.Meta);
			else if (s.equals("Red Socket"))
				sockets[i] = new Socket(item, i, SocketType.Red);
			else if (s.equals("Yellow Socket"))
				sockets[i] = new Socket(item, i, SocketType.Yellow);
			else if (s.equals("Blue Socket"))
				sockets[i] = new Socket(item, i, SocketType.Blue);
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
	
	private void fetchNameIcon(Parser parser, Item item) throws ParserException {
		parser.reset();
		NodeFilter nf = new AndFilter(
					new TagNameFilter("img"),
					new HasAttributeFilter("src", "icon")
					);
		TagNode n = (TagNode) parser.extractAllNodesThatMatch(nf).elementAt(0);
		item.setIcon(n.getAttribute("alt"));
		item.setName(Translate.decode(n.getParent().getNextSibling().getText().trim()));
	}
	
	private void fetchTag(Parser parser, Item item) throws ParserException {
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
	
	public static void main(String[] args) {
		ItemParser ip = new ItemParser();
		//Race.load();
		Item.load();
		Iterator<Item> iter = Item.getAll().iterator();
		System.out.println(Item.getAll().size());
		while (iter.hasNext()) {
			Item itemOrg = iter.next();
			Item itemNew = ip.loadItem(itemOrg.getId());
			if (itemOrg.getTag() != null)
				itemNew.setTag(itemOrg.getTag());
			if (itemOrg.getFaction() != Faction.Both)
				itemNew.setFaction(itemOrg.getFaction());
			Item.add(itemNew);
			System.out.println("Done "+itemNew.getName());
		}
		Item.save();
	}

}
