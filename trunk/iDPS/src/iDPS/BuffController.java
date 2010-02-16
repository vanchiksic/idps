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
		statsMultiplicative, agilityStrength, agilityStrengthImp }
	public enum Consumable { flask,
		foodAgi, foodArp, foodAtp, foodExp, foodHit, foodHst }
	public enum Debuff { armorMajor, armorMajorMaintain, armorMinor,
		crit, physicalDamage, spellCrit, spellDamage, spellHit }
	
	private final PropertyChangeSupport pcs;
	private final EnumSet<Consumable> foodBuffs = EnumSet.range(Consumable.foodAgi, Consumable.foodHst);
	
	private EnumMap<Buff,Boolean> buffs;
	private EnumMap<Consumable,Boolean> consumables;
	private EnumMap<Debuff,Boolean> debuffs;
	
	public BuffController() {
		pcs = new PropertyChangeSupport(this);
		buffs = new EnumMap<Buff,Boolean>(Buff.class);
		for (Buff b: Buff.values())
			buffs.put(b, false);
		consumables = new EnumMap<Consumable,Boolean>(Consumable.class);
		for (Consumable b: Consumable.values())
			consumables.put(b, false);
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
			}
		}
	}
	
	public boolean hasConsumable(Consumable c) {
		return consumables.get(c);
	}
	
	public void setConsumable(Consumable b, boolean newValue) {
		boolean oldValue = consumables.get(b);
		consumables.put(b, newValue);
		pcs.firePropertyChange(b.name(), oldValue, newValue);
		if (newValue) {
			if (foodBuffs.contains(b)) {
				for (Consumable fb: foodBuffs) {
					if (fb != b)
						setConsumable(fb , false);
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
		// save Consumables
		elem = doc.getRootElement().getChild("consumables");
		elem.removeContent();
		for (Consumable b: Consumable.values()) {
			if (hasConsumable(b)) {
				Element elem2 = new Element("consumable");
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
				try {
					Buff b = Buff.valueOf(e.getText());
					buffs.put(b, true);
				} catch (IllegalArgumentException ex) { }
			}
		}
		// load Consumables
		elem = doc.getRootElement().getChild("consumables");
		if (elem.getChildren().size()>0) {
			for (Element e: (List<Element>) elem.getChildren()) {
				try {
					Consumable b = Consumable.valueOf(e.getText());
					consumables.put(b, true);
				} catch (IllegalArgumentException ex) { }
			}
		}
		// load Debuffs
		elem = doc.getRootElement().getChild("debuffs");
		if (elem.getChildren().size()>0) {
			for (Element e: (List<Element>) elem.getChildren()) {
				try {
					Debuff b = Debuff.valueOf(e.getText());
					debuffs.put(b, true);
				} catch (IllegalArgumentException ex) { }
			}
		}
	}

}
