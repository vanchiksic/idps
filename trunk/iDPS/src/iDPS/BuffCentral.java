package iDPS;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumMap;


public class BuffCentral {
	
	public enum Buff { attackPower, attackPowerImp, damage, meleHaste, meleHasteImp,
		physicalCrit, spellCrit, statsAdditive, statsAdditiveImp,
		statsMultiplicative, agilityStrength, agilityStrengthImp };
	public enum Debuff { armorMajor, armorMajorMaintain, armorMinor, crit, physicalDamage,
		spellCrit, spellDamage, spellHit }
	
	private final PropertyChangeSupport pcs;
	
	private EnumMap<Buff,Boolean> buffs;
	private EnumMap<Debuff,Boolean> debuffs;
	
	public BuffCentral() {
		pcs = new PropertyChangeSupport( this );
		buffs = new EnumMap<Buff,Boolean>(Buff.class);
		for (Buff b: Buff.values())
			buffs.put(b, false);
		debuffs = new EnumMap<Debuff,Boolean>(Debuff.class);
		for (Debuff b: Debuff.values())
			debuffs.put(b, false);
	}
	
	public boolean hasBuff(Buff b) {
		return buffs.get(b);
	}
	
	public void setBuff(Buff b, boolean newValue) {
		boolean oldValue = buffs.get(b);
		buffs.put(b, newValue);
		pcs.firePropertyChange(b.name(), oldValue, newValue);
	}
	
	public boolean hasDebuff(Debuff b) {
		return debuffs.get(b);
	}
	
	public void setDebuff(Debuff b, boolean newValue) {
		boolean oldValue = debuffs.get(b);
		debuffs.put(b, newValue);
		pcs.firePropertyChange(b.name(), oldValue, newValue);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

	public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

}
