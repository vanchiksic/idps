package iDPS;

import iDPS.Talents.Talent;
import iDPS.model.Calculations.ModelType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TalentsController implements PropertyChangeListener {
	
	private final Application app;
	private final PropertyChangeSupport pcs;
	
	public TalentsController(Application app) {
		this.app = app;
		pcs = new PropertyChangeSupport(this);
		app.addPropertyChangeListener(this);
	}
	
	public int getTalentPoints(Talent t) {
		return app.getSetup().getTalents().getTalentPoints(t);
	}
	
	public void setTalentPoints(Talent t, int newValue) {
		int oldValue = app.getSetup().getTalents().getTalentPoints(t);
		app.getSetup().getTalents().setTalentPoints(t, newValue);
		pcs.firePropertyChange(t.getIdentifier(), oldValue, newValue);
	}
	
	public ModelType getModel() {
		return app.getSetup().getTalents().getModel();
	}
	
	public void setModel(ModelType mt) {
		ModelType oldValue = app.getSetup().getTalents().getModel();
		app.getSetup().getTalents().setModel(mt);
		pcs.firePropertyChange("modelType", oldValue, mt);
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
				pcs.firePropertyChange("modelType", null, app.getSetup().getTalents().getModel());
				for (Talent t: Talents.getTalents())
					pcs.firePropertyChange(t.getIdentifier(), null, getTalentPoints(t));
			}
		}
	}

}
