package iDPS.gui.menu;

import iDPS.Race;
import iDPS.controllers.RaceController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

public class MenuRaces extends JMenu implements PropertyChangeListener {

	private RaceController controller;
	private HashMap<Race,ItemSelectRace> boxes;

	public MenuRaces(RaceController controller) {
		super("Race");
		this.controller = controller;
		controller.addPropertyChangeListener(this);
		
		ArrayList<Race> races = Race.getAll();
		ButtonGroup gRaces = new ButtonGroup();
		boxes = new HashMap<Race,ItemSelectRace>();
		for (int i=0; i<races.size(); i++) {
			ItemSelectRace isr = new ItemSelectRace(races.get(i));
			gRaces.add(isr);
			add(isr);
			boxes.put(races.get(i), isr);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == controller) {
			if (evt.getPropertyName().equals("race")) {
				Race r = (Race) evt.getNewValue();
				boxes.get(r).setSelected(true);
			}
		}
	}

	private class ItemSelectRace extends JCheckBoxMenuItem implements ActionListener {

		private Race race;

		public ItemSelectRace(Race race) {
			super(race.getName());
			this.race = race;
			setSelected(controller.getRace() == race);
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			controller.setRace(race);
		}

	}

}
