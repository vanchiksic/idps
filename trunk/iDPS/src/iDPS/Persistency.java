package iDPS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Persistency {
	
	public enum FileType { Settings, Items, Gems, Enchants, Realms };
	
	public static void createXML() {
		Element root;
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
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
		    outputter.output(document,out);
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
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
			case Realms:
				url = Persistency.class.getResource("/data/realms.xml");
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
	
	@SuppressWarnings("unchecked")
	public static Element getElement(Document doc, String name) {
		Element elem = doc.getRootElement().getChild(name);
		if (elem == null) {
			elem = new Element(name);
			doc.getRootElement().getChildren().add(elem);
		}
		return elem;
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
			case Realms:
				url = Persistency.class.getResource("/data/realms.xml");
				break;
		}
		return url;
	}

}
