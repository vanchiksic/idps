package iDPS;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;

import org.jdom.Element;

public class Attributes {
	
	public enum Type { ATP, AGI, STR, ARP, CRI, EXP, HIT, HST }
	private EnumMap<Type,Float> values;
	private boolean finalized;
	
	public Attributes() {
		finalized = false;
		values = new EnumMap<Type,Float>(Type.class);
	}
	
	public Attributes(Type t, float value) {
		this();
		values.put(t, value);
	}
	
	public Attributes(Attributes copy) {
		this();
		if (copy == null)
			return;
		values = copy.values.clone();
	}
	
	@SuppressWarnings("unchecked")
	public Attributes(Element elem) {
		this();
		Iterator<Element> iter = elem.getChildren().iterator();
		while (iter.hasNext()) {
			Element sub = iter.next();
			try {
				Type t = Type.valueOf(sub.getName().toUpperCase(java.util.Locale.ENGLISH));
				set(t, Float.parseFloat(sub.getText()));
			} catch (IllegalArgumentException e) {
				System.err.println("Cannot map Attribute "+sub.getName()+".");
			}
		}
	}

	/**
	 * Finalizes the Attributes.
	 * ATP, AGI and STR will be added up to the final ATP value.
	 * Can only be called once, and blocks this Attribute object from Attribute changes.
	 */
	public void finalizeStats() {
		if (finalized)
			return;
		float agi = get(Type.AGI);
		float str = get(Type.STR);
		float atp = get(Type.ATP);
		atp = agi + str + atp;
		set(Type.ATP, atp);
		finalized = true;
	}
	
	/**
	 * Applys the a Stat multiplier.
	 * Can only be called on unfinalized Attributes instances.
	 */
	public void applyStatMult(Type t, float mult) {
		if (finalized)
			return;
		set(t, get(t)*mult);
	}
	
	/**
	 * Applys an ATP multiplier.
	 * Used for Buffs like Unleashed Rage and Savage Combat.
	 * Can only be called on finalized Attributes instances.
	 * @param mult Multiplier to apply to the ATP value
	 */
	public void applyAtpMult(float mult) {
		if (!finalized)
			return;
		if (values.containsKey(Type.ATP))
			values.put(Type.ATP, values.get(Type.ATP)*mult);
	}
	
	/**
	 * Sets the specified Attribute Type to the new value
	 * @param t Attribute Type
	 * @param value new Value
	 */
	public void set(Type t, float value) {
		if (finalized)
			return;
		values.put(t, value);
	}
	
	public void inc(Type t, float value) {
		if (finalized)
			return;
		set(t, get(t)+value);
	}
	
	public float get(Type t) {
		if (values.containsKey(t))
			return values.get(t);
		return 0;
	}
	
	public void clear() {
		values.clear();
	}
	
	public float getAgi() {
		return get(Type.AGI);
	}
	
	public float getArp() {
		return get(Type.ARP);
	}

	public float getAtp() {
		return get(Type.ATP);
	}

	public float getCri() {
		return get(Type.CRI);
	}

	public float getExp() {
		return get(Type.EXP);
	}

	public float getHit() {
		return get(Type.HIT);
	}

	public float getHst() {
		return get(Type.HST);
	}

	public float getStr() {
		return get(Type.STR);
	}
	
	public void incAgi(float value) {
		inc(Type.AGI, value);
	}

	public void incArp(float value) {
		inc(Type.ARP, value);
	}

	public void incAtp(float value) {
		inc(Type.ATP, value);
	}

	public void incCri(float value) {
		inc(Type.CRI, value);
	}

	public void incExp(float value) {
		inc(Type.EXP, value);
	}

	public void incHit(float value) {
		inc(Type.HIT, value);
	}

	public void incHst(float value) {
		inc(Type.HST, value);
	}

	public void incStr(float value) {
		inc(Type.STR, value);
	}

	public void setAgi(float value) {
		set(Type.AGI, value);
	}

	public void setArp(float value) {
		set(Type.ARP, value);
	}

	public void setAtp(float value) {
		set(Type.ATP, value);
	}

	public void setCri(float value) {
		set(Type.CRI, value);
	}

	public void setExp(float value) {
		set(Type.EXP, value);
	}

	public void setHit(float value) {
		set(Type.HIT, value);
	}

	public void setHst(float value) {
		set(Type.HST, value);
	}

	public void setStr(float value) {
		set(Type.STR, value);
	}
	
	public Attributes clone() {
		return new Attributes(this);
	}
	
	public String getToolTip() {
		String s = "<p>";
		if (getAgi() > 0)
			s += String.format("+%.0f Agility<br/>", getAgi());
		if (getStr() > 0)
			s += String.format("+%.0f Strength<br/>", getStr());
		s += "</p><p style=\"color:#00FF00;\">";
		if (getAtp() > 0)
			s += String.format("Increases attack power by %.0f<br/>", getAtp());
		if (getArp() > 0)
			s += String.format("Increases armor penetration rating by %.0f<br/>", getArp());
		if (getCri() > 0)
			s += String.format("Increases critical strike rating by %.0f<br/>", getCri());
		if (getExp() > 0)
			s += String.format("Increases expertise rating by %.0f<br/>", getExp());
		if (getHit() > 0)
			s += String.format("Increases hit rating by %.0f<br/>", getHit());
		if (getHst() > 0)
			s += String.format("Increases haste rating by %.0f<br/>", getHst());
		s += "</p>";
		return s;
	}
	
	public String getMinToolTip() {
		String s = "<p>";
		if (getAgi() > 0)
			s += String.format("+%.0f Agility<br/>", getAgi());
		if (getStr() > 0)
			s += String.format("+%.0f Strength<br/>", getStr());
		if (getAtp() > 0)
			s += String.format("+%.0f Attack Power<br/>", getAtp());
		if (getArp() > 0)
			s += String.format("+%.0f Armor Penetration<br/>", getArp());
		if (getCri() > 0)
			s += String.format("+%.0f Crit<br/>", getCri());
		if (getExp() > 0)
			s += String.format("+%.0f Expertise<br/>", getExp());
		if (getHit() > 0)
			s += String.format("+%.0f Hit<br/>", getHit());
		if (getHst() > 0)
			s += String.format("+%.0f Haste<br/>", getHst());
		s += "</p>";
		return s;
	}
	
	public String toString() {
		ArrayList<String> s = new ArrayList<String>();
		if (getAgi() > 0)
			s.add(String.format("%.0f Agi", getAgi()));
		if (getStr() > 0)
			s.add(String.format("%.0f Str", getStr()));
		if (getAtp() > 0)
			s.add(String.format("%.0f Atp", getAtp()));
		if (getArp() > 0)
			s.add(String.format("%.0f Arp", getArp()));
		if (getCri() > 0)
			s.add(String.format("%.0f Cri", getCri()));
		if (getExp() > 0)
			s.add(String.format("%.0f Exp", getExp()));
		if (getHit() > 0)
			s.add(String.format("%.0f Hit", getHit()));
		if (getHst() > 0)
			s.add(String.format("%.0f Hst", getHst()));
		
		return join(s, " / ");
	}
	
	 private String join(AbstractCollection<String> s, String delimiter) {
	     StringBuffer buffer = new StringBuffer();
	     Iterator<String> iter = s.iterator();
	     if (iter.hasNext()) {
	         buffer.append(iter.next());
	         while (iter.hasNext()) {
	             buffer.append(delimiter);
	             buffer.append(iter.next());
	         }
	     }
	     return buffer.toString();
	 }
	
	public void add(Attributes inc) {
		if (finalized)
			return;
		for (Type t: Type.values()) {
			inc(t, inc.get(t));
		}
	}
	
	public void sub(Attributes sub) {
		if (finalized)
			return;
		for (Type t: Type.values()) {
			inc(t, -sub.get(t));
		}
	}
	
	@SuppressWarnings("unchecked")
	public Element toXML(String name) {
		if (name == null)
			name = "attributes";
		Element ele, sub;
		ele = new Element(name);
		for (Type t: Type.values()) {
			if (get(t)>0) {
				sub = new Element(t.name().toLowerCase());
				sub.setText(String.format("%.0f",get(t)));
				ele.getChildren().add(sub);
			}
		}
		return ele;
	}

	public static Attributes add(Attributes... attribs) {
		Attributes a = new Attributes();
		for (Attributes at: attribs)
			a.add(at);
		return a;
	}
	
	public static Attributes add(ArrayList<Attributes> attr) {
		Attributes a = new Attributes();
		for (Attributes at: attr)
			a.add(at);
		return a;
	}

}
