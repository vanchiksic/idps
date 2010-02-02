package iDPS.gear;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import iDPS.Attributes;
import iDPS.Persistency;
import iDPS.gui.MainFrame;
import iDPS.gui.MenuBar;

public class Item implements Comparable<Item>, Rateable {
	
	public enum Filter { heroics, naxx25, uld10n, uld10h, uld25n, uld25h, toc10n, toc10h, toc25n, toc25h, icc10n, icc10h, icc25n, icc25h }
	
	private String icon;
	private int id;
	private int lvl;
	private String name;
	private Attributes attr;
	private float comparedDPS;
	
	private EnumSet<Filter> filter;
	
	private String uniqueName;
	private int uniqueLimit;
	
	@SuppressWarnings("unchecked")
	public Item(Element element) {
		this();
		id = (Integer.parseInt(element.getAttributeValue("id")));

		List<Element> childs = element.getChildren();
		Iterator<Element> i = childs.iterator();
		while (i.hasNext()) {
			Element e = i.next();
			String s = e.getName();
			if (s.equals("name"))
				name = e.getText();
			else if (s.equals("icon"))
				icon = e.getText();
			else if (s.equals("filters")) {
				List<Element> childs2 = e.getChildren();
				Iterator<Element> i2 = childs2.iterator();
				while (i2.hasNext()) {
					Element e2 = i2.next();
					filter.add(Filter.valueOf(e2.getText()));
				}
			}
			else if (s.equals("attributes"))
				attr = new Attributes(e);
			else if (s.equals("lvl"))
				lvl = Integer.parseInt(e.getText());
			else if (s.equals("unique")) {
				uniqueName = e.getText();
				uniqueLimit = Integer.parseInt(e.getAttributeValue("max"));
			}
		}
	}
	
	public Item() {
		icon = null;
		attr = new Attributes();
		filter = EnumSet.noneOf(Filter.class);
		comparedDPS = 0;
	}
			
	public String getToolTip() {
		String s = "<html><body style=\"padding:4px;background-color:#070c20;color:white;font-family:Verdana,sans-serif;font-size:8px;\"><p style=\"font-weight:bold;font-size:8px;margin:0 0 6px 0;\">"+getName()+"</p>";
		s += attr.getMinToolTip();
		s += "</body></html>";
		return s;
	}
	
	public float getComparedDPS() {
		return comparedDPS;
	}
	
	protected void setComparedDPS(float comparedDPS) {
		this.comparedDPS = comparedDPS;
	}
	
	public String toString() {
		return name+" ("+id+")";
	}

	public String getIcon() {
		return icon;
	}

	protected void setIcon(String icon) {
		this.icon = icon;
	}

	public int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}

	public int getLvl() {
		return lvl;
	}

	protected void setLvl(int lvl) {
		this.lvl = lvl;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public Attributes getAttr() {
		return attr;
	}

	protected void setAttr(Attributes attr) {
		this.attr = attr;
	}
	
	public int compareTo(Item o) {
		if (comparedDPS > o.comparedDPS)
			return -1;
		else if (comparedDPS < o.comparedDPS)
			return 1;
		return 0;
	}
	
	public String getUniqueName() {
		return uniqueName;
	}

	public int getUniqueLimit() {
		return uniqueLimit;
	}

	protected void setUniqueName(String unique_name) {
		this.uniqueName = unique_name;
	}

	protected void setUniqueLimit(int unique_limit) {
		this.uniqueLimit = unique_limit;
	}

	public EnumSet<Filter> getFilter() {
		return filter;
	}
	
	public void setFilter(EnumSet<Filter> filter) {
		this.filter = filter;
	}
	
	@SuppressWarnings("unchecked")
	public static void saveFilter() {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element filters = doc.getRootElement().getChild("filters");
		filters.removeContent();
		MenuBar mb = MainFrame.getInstance().getMyMenuBar();
		for (Filter f: Filter.values()) {
			if (mb.isOneFilterChecked(EnumSet.of(f))) {
				Element filter = new Element("filter");
				filter.setText(f.name());
				filters.getChildren().add(filter);
			}
		}
		Persistency.saveXML(doc, Persistency.FileType.Settings);
	}
	
	@SuppressWarnings("unchecked")
	public static void loadFilter() {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element profs = doc.getRootElement().getChild("filters");
		MenuBar mb = MainFrame.getInstance().getMyMenuBar();
		for (Element e: (List<Element>) profs.getChildren()) {
			Filter f = Filter.valueOf(e.getText());
			mb.checkFilter(f);
		}
	}

}
