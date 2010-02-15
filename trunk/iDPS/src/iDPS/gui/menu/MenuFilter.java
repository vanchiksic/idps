package iDPS.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumMap;

import iDPS.FilterController;
import iDPS.FilterController.Filter;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

public class MenuFilter extends JMenu implements PropertyChangeListener {
	
	private FilterController controller;
	private EnumMap<Filter,ItemSelectFilter> boxes;
	
	public MenuFilter(FilterController controller) {
		super("Loot Filter");
		this.controller = controller;
		controller.addPropertyChangeListener(this);
		
		boxes = new EnumMap<Filter,ItemSelectFilter>(Filter.class);
		ItemSelectFilter iFilter;
		for (Filter f: Filter.values()) {
			iFilter = new ItemSelectFilter(f.name(), f);
			boxes.put(f, iFilter);
			add(iFilter);
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		Filter f = Filter.valueOf(evt.getPropertyName());
		Boolean b = (Boolean) evt.getNewValue();
		boxes.get(f).setSelected(b);
	}
	
	private class ItemSelectFilter extends JCheckBoxMenuItem implements ActionListener {
		
		private Filter filter;
		
		public ItemSelectFilter(String name, Filter filter) {
			super(name);
			this.filter = filter;
			setSelected(controller.hasFilter(filter));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			controller.setFilter(filter, isSelected());
		}
		
	}
}
