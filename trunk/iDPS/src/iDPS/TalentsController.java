package iDPS;

import iDPS.Talents.Talent;
import iDPS.gear.Setup;
import iDPS.model.Calculations.ModelType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TalentsController implements PropertyChangeListener {
	
	private final Application app;
	private final PropertyChangeSupport pcs;
	private Talents talents;
	
	public TalentsController(Application app) {
		this.app = app;
		pcs = new PropertyChangeSupport(this);
		app.addPropertyChangeListener(this);
	}
	
	public int getTalentPoints(Talent t) {
		return talents.getTalentPoints(t);
	}
	
	public void setTalentPoints(Talent t, int newValue) {
		int oldValue = talents.getTalentPoints(t);
		talents.setTalentPoints(t, newValue);
		pcs.firePropertyChange(t.getIdentifier(), oldValue, newValue);
	}
	
	public ModelType getModel() {
		return talents.getModel();
	}
	
	public void setModel(ModelType mt) {
		ModelType oldValue = talents.getModel();
		talents.setModel(mt);
		pcs.firePropertyChange("modelType", oldValue.name(), mt.name());
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
				Setup setup = (Setup) evt.getNewValue();
				talents = setup.getTalents();
				pcs.firePropertyChange("modelType", null, talents.getModel().name());
				for (Talent t: talents.getTalents())
					pcs.firePropertyChange(t.getIdentifier(), null, getTalentPoints(t));
			}
		}
	}

}
