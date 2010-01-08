package iDPS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Persistency {
	
	public enum FileType { Settings, Items, Gems, Enchants };
	
	@SuppressWarnings("unchecked")
	public static void createXML() {
		Element root, elem;
    XMLOutputter outputter = new XMLOutputter();
    File file;
    Document document;
		try {
			file = new File(System.getProperty("user.home")+"/iDPS.xml");
			if (file.exists()) {
        SAXBuilder builder = new SAXBuilder();
        document = builder.build(file);
        root = document.getRootElement();
			} else {
				root = new Element("idps");
				document = new Document(root);
			}
			boolean foundGear = false, foundTalents = false,
				foundRaces = false, foundProfessions = false;
			Iterator<Element> iter = root.getChildren().iterator();
			while (iter.hasNext()) {
				elem = iter.next();
				if (elem.getName().equals("gearconfigs"))
					foundGear = true;
				else if (elem.getName().equals("talentspecs"))
					foundTalents = true;
				else if (elem.getName().equals("races"))
					foundRaces = true;
				else if (elem.getName().equals("professions"))
					foundProfessions = true;
				else
					iter.remove();
			}
			if (foundGear && foundTalents && foundRaces && foundProfessions)
				return;
			if (!foundGear) {
				Element gearconfigs = new Element("gearconfigs");
				gearconfigs.setAttribute("default", "1");
				Element gear1 = new Element("gear");
				gear1.setAttribute("id", "1");
				gear1.getChildren().add(new Element("name").setText("default"));
				gearconfigs.getChildren().add(gear1);
				root.getChildren().add(gearconfigs);
			}
			if (!foundTalents) {
				Element talentspecs = new Element("talentspecs");
				talentspecs.setAttribute("default", "MutilateRS");
				root.getChildren().add(talentspecs);
			}
			if (!foundRaces) {
				Element races = new Element("races");
				races.setAttribute("default", "BloodElf");
				root.getChildren().add(races);
			}
			if (!foundProfessions) {
				Element professions = new Element("professions");
				root.getChildren().add(professions);
			}
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
	    outputter.output(document,out);
	    out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveXML(Document doc, FileType fileDesc) {
		URL url;
		File file = null;
		switch (fileDesc) {
			case Settings:
				file = new File(System.getProperty("user.home")+"/iDPS.xml");
				break;
			case Items:
				url = Persistency.class.getResource("/data/items.xml");
				file = new File(url.getFile());
				break;
			case Gems:
				url = Persistency.class.getResource("/data/gems.xml");
				file = new File(url.getFile());
				break;
			case Enchants:
				url = Persistency.class.getResource("/data/enchants.xml");
				file = new File(url.getFile());
				break;
		}
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		try {
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
			outputter.output(doc,out);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Document openXML(FileType fileDesc) {
    Document document = null;
    SAXBuilder builder = new SAXBuilder();
    try {
	    if (fileDesc == FileType.Settings) {
	    	File file = new File(System.getProperty("user.home")+"/iDPS.xml");
	    	document = builder.build(file);
	    } else
	    	document = builder.build(getURL(fileDesc));
    } catch (Exception e) {}
    return document;
	}
	
	private static URL getURL(FileType fileType) {
		URL url = null;
		switch (fileType) {
			case Items:
				url = Persistency.class.getResource("/data/items.xml");
				break;
			case Gems:
				url = Persistency.class.getResource("/data/gems.xml");
				break;
			case Enchants:
				url = Persistency.class.getResource("/data/enchants.xml");
				break;
		}
		return url;
	}

}
