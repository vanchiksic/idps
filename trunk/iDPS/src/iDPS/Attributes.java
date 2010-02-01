package iDPS;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;

import org.jdom.Element;

public class Attributes {
		
	private float atp, agi, str, arp, cri, exp, hit, hst;
	
	public Attributes() {
		atp = 0;
		agi = 0;
		str = 0;
		arp = 0;
		cri = 0;
		exp = 0;
		hit = 0;
		hst = 0;
	}
	
	public Attributes(Attributes copy) {
		if (copy == null)
			return;
		atp = copy.atp;
		agi = copy.agi;
		str = copy.str;
		arp = copy.arp;
		cri = copy.cri;
		exp = copy.exp;
		hit = copy.hit;
		hst = copy.hst;
	}
	
	@SuppressWarnings("unchecked")
	public Attributes(Element elem) {
		this();
		Iterator<Element> iter = elem.getChildren().iterator();
		while (iter.hasNext()) {
			Element sub = iter.next();
			String s = sub.getName();
			if (s.equals("agi"))
				agi = Float.parseFloat(sub.getText());
			else if (s.equals("arp"))
				arp = Float.parseFloat(sub.getText());
			else if (s.equals("atp"))
				atp = Float.parseFloat(sub.getText());
			else if (s.equals("cri"))
				cri = Float.parseFloat(sub.getText());
			else if (s.equals("exp"))
				exp = Float.parseFloat(sub.getText());
			else if (s.equals("hit"))
				hit = Float.parseFloat(sub.getText());
			else if (s.equals("hst"))
				hst = Float.parseFloat(sub.getText());
			else if (s.equals("str"))
				str = Float.parseFloat(sub.getText());
		}
	}
	
	public void clear() {
		atp = 0;
		agi = 0;
		str = 0;
		arp = 0;
		cri = 0;
		exp = 0;
		hit = 0;
		hst = 0;
	}
	
	public float getAgi() {
		return agi;
	}
	
	public float getArp() {
		return arp;
	}

	public float getAtp() {
		return atp;
	}

	public float getCri() {
		return cri;
	}

	public float getExp() {
		return exp;
	}

	public float getHit() {
		return hit;
	}

	public float getHst() {
		return hst;
	}

	public float getStr() {
		return str;
	}
	
	public void incAgi(float agi) {
		this.agi += agi;
	}

	public void incArp(float arp) {
		this.arp += arp;
	}

	public void incAtp(float atp) {
		this.atp += atp;
	}

	public void incCri(float cri) {
		this.cri += cri;
	}

	public void incExp(float exp) {
		this.exp += exp;
	}

	public void incHit(float hit) {
		this.hit += hit;
	}

	public void incHst(float hst) {
		this.hst += hst;
	}

	public void incStr(float str) {
		this.str += str;
	}

	public void setAgi(float agi) {
		this.agi = agi;
	}

	public void setArp(float arp) {
		this.arp = arp;
	}

	public void setAtp(float atp) {
		this.atp = atp;
	}

	public void setCri(float cri) {
		this.cri = cri;
	}

	public void setExp(float exp) {
		this.exp = exp;
	}

	public void setHit(float hit) {
		this.hit = hit;
	}

	public void setHst(float hst) {
		this.hst = hst;
	}

	public void setStr(float str) {
		this.str = str;
	}
	
	public boolean isInfluenceMods() {
		return (agi>0||arp>0||cri>0||exp>0||hit>0||hst>0);
	}
	
	public Attributes clone() {
		return new Attributes(this);
	}
	
	public String getToolTip() {
		String s = "<p>";
		if (agi > 0)
			s += String.format("+%.0f Agility<br/>", agi);
		if (str > 0)
			s += String.format("+%.0f Strength<br/>", str);
		s += "</p><p style=\"color:#00FF00;\">";
		if (atp > 0)
			s += String.format("Equip: Increases attack power by %.0f<br/>", atp);
		if (arp > 0)
			s += String.format("Equip: Increases armor penetration rating by %.0f<br/>", arp);
		if (cri > 0)
			s += String.format("Equip: Increases critical strike rating by %.0f<br/>", cri);
		if (exp > 0)
			s += String.format("Equip: Increases expertise rating by %.0f<br/>", exp);
		if (hit > 0)
			s += String.format("Equip: Increases hit rating by %.0f<br/>", hit);
		if (hst > 0)
			s += String.format("Equip: Increases haste rating by %.0f<br/>", hst);
		s += "</p>";
		return s;
	}
	
	public String getMinToolTip() {
		String s = "<p>";
		if (agi > 0)
			s += String.format("+%.0f Agility<br/>", agi);
		if (str > 0)
			s += String.format("+%.0f Strength<br/>", str);
		if (atp > 0)
			s += String.format("+%.0f Attack Power<br/>", atp);
		if (arp > 0)
			s += String.format("+%.0f Armor Pentration<br/>", arp);
		if (cri > 0)
			s += String.format("+%.0f Crit<br/>", cri);
		if (exp > 0)
			s += String.format("+%.0f Expertise<br/>", exp);
		if (hit > 0)
			s += String.format("+%.0f Hit<br/>", hit);
		if (hst > 0)
			s += String.format("+%.0f Haste<br/>", hst);
		s += "</p>";
		return s;
	}
	
	public String toString() {
		ArrayList<String> s = new ArrayList<String>();
		if (agi > 0)
			s.add(String.format("%.0f Agi", agi));
		if (str > 0)
			s.add(String.format("%.0f Str", str));
		if (atp > 0)
			s.add(String.format("%.0f Atp", atp));
		if (arp > 0)
			s.add(String.format("%.0f Arp", arp));
		if (cri > 0)
			s.add(String.format("%.0f Cri", cri));
		if (exp > 0)
			s.add(String.format("%.0f Exp", exp));
		if (hit > 0)
			s.add(String.format("%.0f Hit", hit));
		if (hst > 0)
			s.add(String.format("%.0f Hst", hst));
		
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
		agi += inc.agi;
		arp += inc.arp;
		atp += inc.atp;
		cri += inc.cri;
		exp += inc.exp;
		hit += inc.hit;
		hst += inc.hst;
		str += inc.str;
	}
	
	public void sub(Attributes sub) {
		agi -= sub.agi;
		arp -= sub.arp;
		atp -= sub.atp;
		cri -= sub.cri;
		exp -= sub.exp;
		hit -= sub.hit;
		hst -= sub.hst;
		str -= sub.str;
	}
	
	@SuppressWarnings("unchecked")
	public Element toXML(String name) {
		if (name == null)
			name = "attributes";
		Element ele, sub;
		ele = new Element(name);
		if (agi>0) {
			sub = new Element("agi");
			sub.setText(String.format("%.0f",agi));
			ele.getChildren().add(sub);
		} if (atp>0) {
			sub = new Element("atp");
			sub.setText(String.format("%.0f",atp));
			ele.getChildren().add(sub);
		} if (hit>0) {
			sub = new Element("hit");
			sub.setText(String.format("%.0f",hit));
			ele.getChildren().add(sub);
		} if (cri>0) {
			sub = new Element("cri");
			sub.setText(String.format("%.0f",cri));
			ele.getChildren().add(sub);
		} if (hst>0) {
			sub = new Element("hst");
			sub.setText(String.format("%.0f",hst));
			ele.getChildren().add(sub);
		} if (arp>0) {
			sub = new Element("arp");
			sub.setText(String.format("%.0f",arp));
			ele.getChildren().add(sub);
		} if (exp>0) {
			sub = new Element("exp");
			sub.setText(String.format("%.0f",exp));
			ele.getChildren().add(sub);
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
