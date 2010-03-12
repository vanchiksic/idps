package iDPS;

import iDPS.gear.Armor.SlotType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class ItemFinder {
	private int id;
	private int slot;
	private int rawSlot;
	private ArmoryInfo armoryInfo;
	private Element item = new Element("item");

	SlotType[] slotMap = { SlotType.Head, SlotType.Neck, SlotType.Shoulder,
			SlotType.Back, SlotType.Chest, null, null, SlotType.Wrist,
			SlotType.Hands, SlotType.Waist, SlotType.Legs, SlotType.Feet,
			SlotType.Finger, SlotType.Finger, SlotType.Trinket,
			SlotType.Trinket, SlotType.MainHand, SlotType.OffHand,
			SlotType.Ranged, };

	public ItemFinder(int id, int rawSlot, int slot, ArmoryInfo armoryInfo) {
		this.id = id;
		this.slot = slot;
		this.rawSlot = rawSlot;
		this.armoryInfo = armoryInfo;
		item.setAttribute("id", String.valueOf(id));
	}

	public Element find() {
		if (slotMap[slot] == null)
			return null;

		connectItemInfo(id);
		return item;
	}

	@SuppressWarnings("unchecked")
	private void connectItemInfo(int id) {
		try {
			String urlString = "http://";
			urlString += armoryInfo.getRegion().toLowerCase();
			urlString += ".wowarmory.com/item-tooltip.xml?i=" + id;
			urlString += "&r=" + URLEncoder.encode(armoryInfo.getRealm().toLowerCase(),"utf-8");
			urlString += "&cn=" + URLEncoder.encode(armoryInfo.getCharacter().toLowerCase(), "utf-8");
			urlString += "&s=" + this.rawSlot;

			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.addRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1");
			connection.setRequestProperty("Cookie", "loginChecked=1");
			
			//System.out.println("Armory Response:" + connection.getResponseMessage());
			//System.out.println("Armory URL:" + urlString);
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(reader);
			Element root = document.getRootElement();
			Element itemTooltip = root.getChild("itemTooltips").getChild("itemTooltip");

			//sets the name and slot
			addToElement(item, "name", itemTooltip.getChildText("name"));
			String slotName = slotMap[slot].name();
			addToElement(item, "slot", slotName);

			// if it is a weapon
			if (slotName != null && (slotName.equals("MainHand") || slotName.equals("OneHand") || slotName.equals("OffHand"))) {
				String type = itemTooltip.getChild("equipData").getChildText("subclassName");

				//armory brings 'FistWeapon'
				if (type.contains("Fist"))
					type = "Fist";
				
				addToElement(item, "type", type);
				Element damage = itemTooltip.getChild("damageData");
				if (damage != null) {
					addToElement(item, "speed", damage.getChildText("speed"));
					addToElement(item, "dps", damage.getChildText("dps"));

				}
			}

			//sets attributes and sockets
			setAttributes(itemTooltip);
			setSockets(itemTooltip);

			//sets level and icon
			addToElement(item, "lvl", itemTooltip.getChildText("itemLevel"));
			addToElement(item, "icon", itemTooltip.getChildText("icon"));

			
			//Document doc = Persistency.openXML(Persistency.FileType.Items);
			//doc.getRootElement().getChildren().add(item);
			//Persistency.saveXML(doc, Persistency.FileType.Items);
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@SuppressWarnings("unchecked")
	private void addToElement(Element elem, String name, String value) {
		if (value == null || value.trim().isEmpty())
			return;

		Element e = new Element(name);
		e.setText(value);
		elem.getChildren().add(e);
	}

	@SuppressWarnings("unchecked")
	private void addToElement(Element elem, String name, String value, Attribute attribute) {
		if (value == null || value.trim().isEmpty())
			return;

		Element e = new Element(name);
		e.setAttribute(attribute);
		e.setText(value);
		elem.getChildren().add(e);
	}

	@SuppressWarnings("unchecked")
	private void setSockets(Element itemTooltip) {
		Element socketData = itemTooltip.getChild("socketData");
		if (socketData != null && socketData.getChildren().size() > 0) {
			String[] sockets = new String[socketData.getChildren().size()];
			for (int i = 0; i < socketData.getChildren().size(); i++) {
				Element e = (Element) socketData.getChildren().get(i);
				if (e.getName().trim().equals("socket"))
					sockets[i] = e.getAttributeValue("color");
			}
			
			Element sock = new Element("sockets");
			for (int i = 0; i < sockets.length; i++) {
				addToElement(sock, "socket", sockets[i], new Attribute("index", String.valueOf(i)));
			}
			
			setSocketBonus(socketData, sock);
			item.getChildren().add(sock);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setSocketBonus(Element socketData, Element socket) {
		Element bonus = new Element("bonus");
		String bonusStr = socketData.getChildText("socketMatchEnchant");
		String bonusValue = bonusStr.substring(1, bonusStr.indexOf(" "));

		if (bonusStr.contains("Agility"))
			addToElement(bonus, "agi", bonusValue);
		if (bonusStr.contains("Attack Power"))
			addToElement(bonus, "atp", bonusValue);
		if (bonusStr.contains("Penetration"))
			addToElement(bonus, "arp", bonusValue);
		if (bonusStr.contains("Haste"))
			addToElement(bonus, "hst", bonusValue);
		if (bonusStr.contains("Critical"))
			addToElement(bonus, "cri", bonusValue);
		
		socket.getChildren().add(bonus);
	}
	
	@SuppressWarnings("unchecked")
	private void setAttributes(Element itemTooltip) {
		Element attr = new Element("attributes");
		addToElement(attr, "agi", itemTooltip.getChildText("bonusAgility"));
		addToElement(attr, "cri", itemTooltip.getChildText("bonusCritRating"));
		addToElement(attr, "hst", itemTooltip.getChildText("bonusHasteRating"));
		addToElement(attr, "atp", itemTooltip.getChildText("bonusAttackPower"));
		addToElement(attr, "hit", itemTooltip.getChildText("bonusHitRating"));
		addToElement(attr, "exp", itemTooltip.getChildText("bonusExpertiseRating"));

		item.getChildren().add(attr);
	}
}
