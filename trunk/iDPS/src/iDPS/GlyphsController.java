package iDPS;

import iDPS.Glyphs.Glyph;
import iDPS.gear.Setup;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GlyphsController implements PropertyChangeListener {
	
	private final Application app;
	private final PropertyChangeSupport pcs;
	private Glyphs glyphs;
	
	public GlyphsController(Application app) {
		this.app = app;
		pcs = new PropertyChangeSupport(this);
		app.addPropertyChangeListener(this);
	}
	
	public boolean hasGlyph(Glyph g) {
		return glyphs.has(g);
	}
	
	public void setGlyph(Glyph g, boolean newValue) {
		boolean oldValue = glyphs.has(g);
		glyphs.set(g, newValue);
		pcs.firePropertyChange(g.name(), oldValue, newValue);
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
				glyphs = setup.getGlyphs();
				for (Glyph g: Glyph.values())
					pcs.firePropertyChange(g.name(), null, hasGlyph(g));
			}
		}
	}

}
