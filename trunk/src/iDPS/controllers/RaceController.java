package iDPS.controllers;

import iDPS.Application;
import iDPS.Race;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RaceController implements PropertyChangeListener {
	
	private final Application app;
	private final PropertyChangeSupport pcs;
	
	public RaceController(Application app) {
		this.app = app;
		pcs = new PropertyChangeSupport(this);
		app.addPropertyChangeListener(this);
	}
	
	public void setRace(Race newValue) {
		Race oldValue = getRace();
		app.getSetup().setRace(newValue);
		pcs.firePropertyChange("race", oldValue, newValue);
		
		app.getMainFrame().showStats();
	}
	
	public Race getRace() {
		return app.getSetup().getRace();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

	public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == app) {
			if (evt.getPropertyName() == "setup")
				pcs.firePropertyChange("race", null, app.getSetup().getRace());
		}
	}

}
