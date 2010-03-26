package iDPS.controllers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import iDPS.Application;
import iDPS.controllers.BuffController.Debuff;

public class CycleController implements PropertyChangeListener {
	
	private final Application app;
	private final PropertyChangeSupport pcs;
		
	public CycleController(Application app) {
		this.app = app;
		pcs = new PropertyChangeSupport(this);
		app.addPropertyChangeListener(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

	public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == app) {
			if (evt.getPropertyName() == "setup") {
				pcs.firePropertyChange("useRupture", null, getUseRupture());
				pcs.firePropertyChange("ruptureUptime", null, getRuptureUptime());
				pcs.firePropertyChange("useTotT", null, getUseTotT());
				pcs.firePropertyChange("useExpose", null, getUseExpose());
			}
		}
	}
	
	public boolean getUseRupture() {
		if (app.getSetup() == null)
			return false;
		return app.getSetup().isUseRupture();
	}
	
	public void setUseRupture(boolean newValue) {
		if (app.getSetup() == null)
			return;
		boolean oldValue = app.getSetup().isUseRupture();
		app.getSetup().setUseRupture(newValue);
		pcs.firePropertyChange("useRupture", oldValue, newValue);
	}
	
	public float getRuptureUptime() {
		if (app.getSetup() == null)
			return 0;
		return app.getSetup().getRuptureUptime();
	}
	
	public void setRuptureUptime(float newValue) {
		if (app.getSetup() == null)
			return;
		float oldValue = app.getSetup().getRuptureUptime();
		app.getSetup().setRuptureUptime(newValue);
		pcs.firePropertyChange("ruptureUptime", oldValue, newValue);
		
		if (newValue == 0)
			setUseRupture(false);
	}
	
	public boolean getUseTotT() {
		if (app.getSetup() == null)
			return false;
		return app.getSetup().isUseTotT();
	}
	
	public void setUseTotT(boolean newValue) {
		if (app.getSetup() == null)
			return;
		boolean oldValue = app.getSetup().isUseTotT();
		app.getSetup().setUseTotT(newValue);
		pcs.firePropertyChange("useTotT", oldValue, newValue);
	}
	
	public boolean getUseExpose() {
		if (app.getSetup() == null)
			return false;
		return app.getSetup().isUseExpose();
	}
	
	public void setUseExpose(boolean newValue) {
		if (app.getSetup() == null)
			return;
		boolean oldValue = app.getSetup().isUseExpose();
		app.getSetup().setUseExpose(newValue);
		pcs.firePropertyChange("useExpose", oldValue, newValue);
		
		if (newValue) {
			app.getBuffController().setDebuff(Debuff.armorMajor, newValue);
		}
	}

}
