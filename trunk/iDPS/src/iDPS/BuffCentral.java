package iDPS;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BuffCentral {
	
	private final PropertyChangeSupport pcs;
	
	/** -25% armor **/
	private boolean armorMajor;
	/** Rogue maintinaing EA **/
	private boolean armorMaintain;
	/** -5% armor **/
	private boolean armorMinor;
	/** +3% crit **/
	private boolean critDebuff;
	/** +3% spell hit **/
	private boolean spellHitDebuff;
	/** +5% spell crit **/
	private boolean spellCritDebuff;
	/** +4% physical damage **/
	private boolean physicalDebuff;
	/** +13% spell damage **/
	private boolean spellDamageDebuff;
	
	/** Blessing of Kings **/
	private boolean kings;
	/** Blessing of Might, Battleshout **/
	private boolean attackpowerBuff;
	/** Imp. Blessing of Might, Imp. Battleshout **/
	private boolean impAttackpowerBuff;
	/** HotW, SoE Totem **/
	private boolean strengthAgilityBuff;
	/** Imp. SoE Totem **/
	private boolean impStrengthAgilityBuff;
	/** Mark of the Wild **/
	private boolean additiveStatBuff;
	/** Imp. Mark of the Wild **/
	private boolean impAdditiveStatBuff;
	/** +3% all damage **/
	private boolean damageBuff;
	/** 16% mele haste **/
	private boolean meleHasteBuff;
	/** 20% mele haste **/
	private boolean impMeleHasteBuff;
	/** 5% physical crit buff **/
	private boolean physicalCritBuff;
	/** 5% spell crit buff **/
	private boolean spellCritBuff;
	
	public BuffCentral() {
		pcs = new PropertyChangeSupport( this );
	}
	
	public void setKings(boolean newValue) {
		boolean oldValue = kings;
		kings = newValue;
		pcs.firePropertyChange("kings", oldValue, newValue);
	}
	
	public boolean isKings() {
		return kings;
	}

	public boolean isArmorMajor() {
		return armorMajor;
	}

	public void setArmorMajor(boolean newValue) {
		boolean oldValue = armorMajor;
		armorMajor = newValue;
		pcs.firePropertyChange("armorMajor", oldValue, newValue);
		if (!newValue)
			setArmorMaintain(false);
	}

	public boolean isArmorMaintain() {
		return armorMaintain;
	}

	public void setArmorMaintain(boolean newValue) {
		boolean oldValue = armorMaintain;
		armorMaintain = newValue;
		pcs.firePropertyChange("armorMaintain", oldValue, newValue);
	}

	public boolean isArmorMinor() {
		return armorMinor;
	}

	public void setArmorMinor(boolean newValue) {
		boolean oldValue = armorMinor;
		armorMinor = newValue;
		pcs.firePropertyChange("armorMinor", oldValue, newValue);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

}
