package iDPS;

import iDPS.gear.Setup;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumMap;
import java.util.EnumSet;


public class BuffController implements PropertyChangeListener {
	
	public enum Buff { attackPower, attackPowerImp, attackPowerMult,
		damage, meleHaste, meleHasteImp, physicalCrit, spellCrit,
		statsAdditive, statsAdditiveImp, statsMultiplicative,
		agilityStrength, agilityStrengthImp, partyHit }
	public enum Consumable { flask,
		foodAgi, foodArp, foodAtp, foodExp, foodHit, foodHst }
	public enum Debuff { armorMajor, armorMajorMaintain, armorMinor,
		crit, physicalDamage, spellCrit, spellDamage, spellHit }
	public enum Other { bloodlust }
	
	private final PropertyChangeSupport pcs;
	private final EnumSet<Consumable> foodBuffs = EnumSet.range(Consumable.foodAgi, Consumable.foodHst);
	
	private EnumMap<Buff,Boolean> buffs;
	private EnumMap<Consumable,Boolean> consumables;
	private EnumMap<Debuff,Boolean> debuffs;
	private EnumMap<Other,Boolean> other;
	
	public BuffController(Application app) {
		pcs = new PropertyChangeSupport(this);
		app.addPropertyChangeListener(this);
	}
	
	public boolean hasBuff(Buff b) {
		return buffs.get(b);
	}
	
	public void setBuff(Buff b, boolean newValue) {
		boolean oldValue = buffs.get(b);
		buffs.put(b, newValue);
		pcs.firePropertyChange("buff_"+b.name(), oldValue, newValue);
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
		pcs.firePropertyChange("consumable_"+b.name(), oldValue, newValue);
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
		pcs.firePropertyChange("debuff_"+b.name(), oldValue, newValue);
		if (!newValue && b == Debuff.armorMajor)
			setDebuff(Debuff.armorMajorMaintain, false);
	}
	
	public boolean hasOther(Other b) {
		return other.get(b);
	}
	
	public void setOther(Other b, boolean newValue) {
		boolean oldValue = other.get(b);
		other.put(b, newValue);
		pcs.firePropertyChange("other_"+b.name(), oldValue, newValue);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

	public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof Application) {
			if (evt.getPropertyName() == "setup") {
				Setup setup = (Setup) evt.getNewValue();
				setSetupTo(setup);
			}
		}
	}
	
	private void setSetupTo(Setup setup) {
		buffs = setup.getBuffs();
		consumables = setup.getConsumables();
		debuffs = setup.getDebuffs();
		other = setup.getOther();
		
		pcs.firePropertyChange("buffs", null, buffs);
		pcs.firePropertyChange("consumables", null, consumables);
		pcs.firePropertyChange("debuffs", null, debuffs);
		pcs.firePropertyChange("other", null, other);
	}

}
