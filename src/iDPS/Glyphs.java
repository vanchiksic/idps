package iDPS;

import java.util.EnumMap;
import java.util.List;

import org.jdom.Element;

public class Glyphs implements Cloneable {
	
	public enum Glyph { Mut, HfB, SS, SnD, KS, Rup, AR, Evi, EA, BF, SD }
	
	private EnumMap<Glyph,Boolean> glyphs;

	public Glyphs() {
		glyphs = new EnumMap<Glyph,Boolean>(Glyph.class);
		for (Glyph g: Glyph.values())
			glyphs.put(g, false);
	}
	
	@SuppressWarnings("unchecked")
	public Glyphs(Element root) {
		this();
		for (Element elem: (List<Element>) root.getChildren()) {
			if (!elem.getText().equals("true"))
				continue;
			Glyph g = Glyph.valueOf(elem.getName());
			glyphs.put(g, true);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Element toXML() {
		Element elem = new Element("glyphs");
		for (Glyph g: Glyph.values()) {
			if (!glyphs.get(g))
				continue;
			Element sub = new Element(g.name());
			sub.setText("true");
			elem.getChildren().add(sub);
		}
		return elem;
	}
	
	public boolean has(Glyph g) {
		return glyphs.get(g);
	}
	
	public void set(Glyph g, boolean has) {
		glyphs.put(g, has);
	}
	
	public Glyphs clone() {
		Glyphs clone = new Glyphs();
		clone.glyphs = glyphs.clone();
		return clone;
	}
	
}
