package iDPS.controllers;

import iDPS.Application;
import iDPS.Setup.Profession;
import iDPS.gear.Enchant;
import iDPS.gear.Gem;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ProfessionController implements PropertyChangeListener {
	
	private final Application app;
	private final PropertyChangeSupport pcs;
	
	public ProfessionController(Application app) {
		this.app = app;
		pcs = new PropertyChangeSupport(this);
		app.addPropertyChangeListener(this);
	}
	
	public void setProfession(Profession p, boolean newValue) {
		boolean oldValue = hasProfession(p);
		app.getSetup().setProfession(p, newValue);
		pcs.firePropertyChange(p.name(), oldValue, newValue);
		
		switch (p) {
		case Blacksmithing:
			app.getMainFrame().showGear();
			break;
		case Enchanting:
		case Engineering:
		case Inscription:
		case Leatherworking:
		case Tailoring:
			Enchant.limit();
			break;
		case Jewelcrafting:
			Gem.limit();
			break;
		case Alchemy:
		case Skinning:
			app.getMainFrame().showStats();
			break;
	}
	}
	
	public boolean hasProfession(Profession p) {
		return app.getSetup().hasProfession(p);
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
				for (Profession p: Profession.values())
					pcs.firePropertyChange(p.name(), null, app.getSetup().hasProfession(p));
		}
	}

}
