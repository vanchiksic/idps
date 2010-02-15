package iDPS;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;


public class BuffController {
	
	public enum Buff { attackPower, attackPowerImp, attackPowerMult,
		damage, meleHaste, meleHasteImp,
		physicalCrit, spellCrit, statsAdditive, statsAdditiveImp,
		statsMultiplicative, agilityStrength, agilityStrengthImp,
		food, foodAgi, foodArp, foodAtp, foodExp, foodHst }
	public enum Debuff { armorMajor, armorMajorMaintain, armorMinor,
		crit, physicalDamage, spellCrit, spellDamage, spellHit }
	
	private final PropertyChangeSupport pcs;
	private final EnumSet<Buff> foodBuffs = EnumSet.range(Buff.foodAgi, Buff.foodHst);
	
	private EnumMap<Buff,Boolean> buffs;
	private EnumMap<Debuff,Boolean> debuffs;
	
	public BuffController() {
		pcs = new PropertyChangeSupport(this);
		buffs = new EnumMap<Buff,Boolean>(Buff.class);
		for (Buff b: Buff.values())
			buffs.put(b, false);
		debuffs = new EnumMap<Debuff,Boolean>(Debuff.class);
		for (Debuff b: Debuff.values())
			debuffs.put(b, false);
		load();
	}
	
	public boolean hasBuff(Buff b) {
		return buffs.get(b);
	}
	
	public void setBuff(Buff b, boolean newValue) {
		boolean oldValue = buffs.get(b);
		buffs.put(b, newValue);
		save();
		pcs.firePropertyChange(b.name(), oldValue, newValue);
		if (!newValue) {
			switch (b) {
			case attackPower:
				setBuff(Buff.attackPowerImp, false);
				break;
			case meleHaste:
				setBuff(Buff.meleHasteImp, false);
				break;
			case statsAdditive:
				setBuff(Buff.statsAdditiveImp, false);
				break;
			case agilityStrength:
				setBuff(Buff.agilityStrengthImp, false);
				break;
			case food:
				setBuff(Buff.foodAgi, false);
				setBuff(Buff.foodArp, false);
				setBuff(Buff.foodAtp, false);
				setBuff(Buff.foodExp, false);
				setBuff(Buff.foodHst, false);
				break;
			}
		} else {
			switch (b) {
			case food:
				setBuff(Buff.foodAtp, true);
				break;
			}
			if (foodBuffs.contains(b)) {
				for (Buff fb: foodBuffs) {
					if (fb != b)
						setBuff(fb , false);
				}
			}
		}
	}
	
	public boolean hasDebuff(Debuff b) {
		return debuffs.get(b);
	}
	
	public void setDebuff(Debuff b, boolean newValue) {
		boolean oldValue = debuffs.get(b);
		debuffs.put(b, newValue);
		pcs.firePropertyChange(b.name(), oldValue, newValue);
		save();
		if (!newValue && b == Debuff.armorMajor)
			setDebuff(Debuff.armorMajorMaintain, false);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

	public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
	
	@SuppressWarnings("unchecked")
	public void save() {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element elem;
		// save Buffs
		elem = doc.getRootElement().getChild("buffs");
		elem.removeContent();
		for (Buff b: Buff.values()) {
			if (hasBuff(b)) {
				Element elem2 = new Element("buff");
				elem2.setText(b.name());
				elem.getChildren().add(elem2);
			}
		}
		// save Debuffs
		elem = doc.getRootElement().getChild("debuffs");
		elem.removeContent();
		for (Debuff b: Debuff.values()) {
			if (hasDebuff(b)) {
				Element elem2 = new Element("debuff");
				elem2.setText(b.name());
				elem.getChildren().add(elem2);
			}
		}
		Persistency.saveXML(doc, Persistency.FileType.Settings);
	}
	
	@SuppressWarnings("unchecked")
	public void load() {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element elem;
		// load Buffs
		elem = doc.getRootElement().getChild("buffs");
		if (elem.getChildren().size()>0) {
			for (Element e: (List<Element>) elem.getChildren()) {
				Buff b = Buff.valueOf(e.getText());
				buffs.put(b, true);
			}
		}
		// load Debuffs
		elem = doc.getRootElement().getChild("debuffs");
		if (elem.getChildren().size()>0) {
			for (Element e: (List<Element>) elem.getChildren()) {
				Debuff b = Debuff.valueOf(e.getText());
				debuffs.put(b, true);
			}
		}
	}

}
