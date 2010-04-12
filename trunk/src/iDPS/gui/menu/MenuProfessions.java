package iDPS.gui.menu;

import iDPS.Setup.Profession;
import iDPS.controllers.ProfessionController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumMap;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

public class MenuProfessions extends JMenu implements PropertyChangeListener {

	private ProfessionController controller;
	private EnumMap<Profession, ItemSelectProfession> boxes;

	public MenuProfessions(ProfessionController controller) {
		super("Professions");
		this.controller = controller;
		controller.addPropertyChangeListener(this);

		boxes = new EnumMap<Profession,ItemSelectProfession>(Profession.class);
		for (Profession p: Profession.values()) {
			ItemSelectProfession isp = new ItemSelectProfession(p);
			add(isp);
			boxes.put(p, isp);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == controller) {
			try {
				Profession p = Profession.valueOf(evt.getPropertyName());
				boolean newValue = (Boolean) evt.getNewValue();
				boxes.get(p).setSelected(newValue);
			} catch (IllegalArgumentException e) {};
		}
	}

	private class ItemSelectProfession extends JCheckBoxMenuItem implements ActionListener {

		private Profession profession;

		public ItemSelectProfession(Profession profession) {
			super(profession.name());
			this.profession = profession;
			setSelected(controller.hasProfession(profession));
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			controller.setProfession(profession, this.isSelected());
		}

	}

}
