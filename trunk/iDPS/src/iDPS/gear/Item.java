package iDPS.gear;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.jdom.CDATA;
import org.jdom.Element;

import iDPS.Attributes;
import iDPS.controllers.FilterController.Filter;

public class Item implements Comparable<Item>, Rateable {
		
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
	
	public void loadFromArmoryXML(Element armoryTooltip) {
		id = Integer.parseInt(armoryTooltip.getChildText("id"));
		lvl	= Integer.parseInt(armoryTooltip.getChildText("itemLevel"));
		name = armoryTooltip.getChildText("name");
		icon = armoryTooltip.getChildText("icon");
		attr.loadFromArmoryXML(armoryTooltip);
	}
	
	@SuppressWarnings("unchecked")
	public Element toXML() {
		Element eSub, eItem = new Element("item");
		eItem.setAttribute("id", String.valueOf(id));
		
		eSub = new Element("name");
		eSub.addContent(new CDATA(name));
		eItem.getChildren().add(eSub);
		
		eSub = attr.toXML(null);
		eItem.getChildren().add(eSub);
		
		eSub = new Element("lvl");
		eSub.setText(String.valueOf(lvl));
		eItem.getChildren().add(eSub);
		
		if (uniqueLimit>0) {
			eSub = new Element("unique");
			eSub.setAttribute("max", String.valueOf(uniqueLimit));
			eSub.setText(uniqueName);
			eItem.getChildren().add(eSub);
		}

		if (filter.size() > 0) {
			eSub = new Element("filters");
			Element eSub2;
			for (Filter f: filter) {
				eSub2 = new Element("filter");
				eSub2.setText(f.name());
				eSub.getChildren().add(eSub2);
			}
			eItem.getChildren().add(eSub);
		}
		eSub = new Element("icon");
		eSub.setText(icon);
		eItem.getChildren().add(eSub);
		
		return eItem;
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
}
