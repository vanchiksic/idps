package iDPS;

import iDPS.gear.Armor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumSet;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

public class FilterController {
	
	public enum Filter { heroics, naxx25,
		uld10n, uld10h, uld25n, uld25h,
		toc10n, toc10h, toc25n, toc25h,
		icc10n, icc10h, icc25n, icc25h }
	
	private final PropertyChangeSupport pcs;
	private EnumSet<Filter> checkedFilters;
	
	public FilterController() {
		pcs = new PropertyChangeSupport(this);
		checkedFilters = EnumSet.allOf(Filter.class);
	}
	
	public void setFilter(Filter f, boolean newValue) {
		boolean oldValue = checkedFilters.contains(f);
		if (newValue)
			checkedFilters.add(f);
		else
			checkedFilters.remove(f);
		pcs.firePropertyChange(f.name(), oldValue, newValue);
		Armor.limit();
	}
		
	public boolean hasFilter(Filter f) {
		return checkedFilters.contains(f);
	}
	
	public boolean isOneFilterChecked(EnumSet<Filter> fs) {
		for (Filter f: fs) {
			if (checkedFilters.contains(f))
				return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void save() {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element filters = doc.getRootElement().getChild("filters");
		filters.removeContent();
		for (Filter f: Filter.values()) {
			if (checkedFilters.contains(f)) {
				Element filter = new Element("filter");
				filter.setText(f.name());
				filters.getChildren().add(filter);
			}
		}
		Persistency.saveXML(doc, Persistency.FileType.Settings);
	}
	
	@SuppressWarnings("unchecked")
	public void load() {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element elem = doc.getRootElement().getChild("filters");
		if (elem.getChildren().size()>0) {
			checkedFilters = EnumSet.noneOf(Filter.class);
			for (Element e: (List<Element>) elem.getChildren()) {
				Filter f = Filter.valueOf(e.getText());
				checkedFilters.add(f);
			}
			for (Filter f: Filter.values())
				pcs.firePropertyChange(f.name(), null, checkedFilters.contains(f));
		}
		Armor.limit();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

	public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

}
