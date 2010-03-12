package iDPS;

import iDPS.Talents.Talent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class TalentReader {
	private ArmoryInfo armoryInfo;
	private int group;
	private Element talents = new Element("talents");
	
	public TalentReader(ArmoryInfo armoryInfo, int group) {
		this.armoryInfo = armoryInfo;
		this.group = group;
	}
	
	public Element read() {
		connectTalentInfo();
		return talents;
	}
	
	private void connectTalentInfo() {
			try {
				String urlString = "http://";
				urlString += armoryInfo.getRegion().toLowerCase();
				urlString += ".wowarmory.com/character-talents.xml?group=" + group;
				urlString += "&r=" + URLEncoder.encode(armoryInfo.getRealm().toLowerCase(),"utf-8");
				urlString += "&cn=" + URLEncoder.encode(armoryInfo.getCharacter().toLowerCase(), "utf-8");

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
				Element talentArmory = root.getChild("characterInfo").getChild("talents");
				
				for (int i = 0; i < talentArmory.getChildren("talentGroup").size(); i++) {
					Element talentGroup = (Element) talentArmory.getChildren("talentGroup").get(i);
					if (talentGroup.getAttributeValue("active") != null && talentGroup.getAttributeValue("active").equals("1")) {
						if (talentGroup.getAttributeValue("prim").equals("Assassination"))
							addToElement(talents, "ModelType", "Mutilate");
						else if (talentGroup.getAttributeValue("prim").equals("Combat"))
							addToElement(talents, "ModelType", "Combat");
						else
							addToElement(talents, "ModelType", "SubHemo");
						
						String talentStrip = talentGroup.getChild("talentSpec").getAttributeValue("value");
						processTalentStrip(talentStrip);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
	}
	
	private void processTalentStrip(String talentStrip) {
		for (int i = 0; i < talentStrip.length()-1; i++) {
			int value = Integer.parseInt(talentStrip.substring(i, i+1));
			if (value > 0) {
				Talent t = Talents.getTalentPosition().get(i);
				if (t != null) {
					addToElement(talents, t.getIdentifier(), String.valueOf(value));
				}
			}
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
}
