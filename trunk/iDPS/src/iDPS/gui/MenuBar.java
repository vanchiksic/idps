package iDPS.gui;

import iDPS.Player;
import iDPS.Race;
import iDPS.Talents;
import iDPS.Player.Profession;
import iDPS.gear.Enchant;
import iDPS.gear.Gear;
import iDPS.gear.Gem;
import iDPS.gear.Armor;
import iDPS.gear.Item.Filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

public class MenuBar extends JMenuBar implements ActionListener {
	
	private JMenu mGear;
	private ItemSelectGear[] iSetups;
	
	private JMenu mArmory;
	private JMenu mFilter;
	private JMenuItem iGearNew, iGearRename, iGearSaveAs, iGearSave, iGearDel, iImportArmory;
	
	private JMenu mTalents;
	private ItemSelectTalent[] iTalents;
	
	private JMenu mRaces;
	private ItemSelectRace[] iRaces;
	
	private JMenu mProfessions;
	
	private EnumMap<Filter,Boolean> checkedFilters;
	
	public MenuBar() {
		mGear = new JMenu("GearConfigs");
		createGearMenu();
		add(mGear);
		
		mTalents = new JMenu("TalentSpecs");
		createTalentsMenu();
		add(mTalents);
		
		mRaces = new JMenu("Race");
		createRacesMenu();
		add(mRaces);
		
		mProfessions = new JMenu("Professions");
		createProfessionsMenu();
		add(mProfessions);
		
		mArmory = new JMenu("Armory");
		createArmoryMenu();
		add(mArmory);
		
		mFilter = new JMenu("Filter");
		createFilterMenu();
		add(mFilter);
	}
	
	public void createGearMenu() {
		mGear.removeAll();
		
		iGearNew = new JMenuItem("New");
		iGearNew.addActionListener(this);
		mGear.add(iGearNew);
		
		iGearDel = new JMenuItem("Delete");
		iGearDel.addActionListener(this);
		mGear.add(iGearDel);
		
		iGearRename = new JMenuItem("Rename");
		iGearRename.addActionListener(this);
		mGear.add(iGearRename);
		
		iGearSave = new JMenuItem("Save");
		iGearSave.addActionListener(this);
		mGear.add(iGearSave);
		
		iGearSaveAs = new JMenuItem("Save As...");
		iGearSaveAs.addActionListener(this);
		mGear.add(iGearSaveAs);
		
		mGear.addSeparator();
		
		ArrayList<Gear> setups = Gear.getAll();
		Collections.sort(setups);
		iSetups = new ItemSelectGear[setups.size()];
		ButtonGroup group = new ButtonGroup();
		for (int i=0; i<setups.size(); i++) {
			iSetups[i] = new ItemSelectGear(setups.get(i));
			group.add(iSetups[i]);
			mGear.add(iSetups[i]);
		}
		
		iGearDel.setEnabled(Gear.getAll().size()>1);
	}
	
	public void createTalentsMenu() {
		mTalents.removeAll();
		
		ArrayList<Talents> tSpecs = Talents.getAll();
		iTalents = new ItemSelectTalent[tSpecs.size()];
		ButtonGroup group = new ButtonGroup();
		for (int i=0; i<tSpecs.size(); i++) {
			iTalents[i] = new ItemSelectTalent(tSpecs.get(i));
			group.add(iTalents[i]);
			mTalents.add(iTalents[i]);
		}
	}
	
	public void createRacesMenu() {
		mRaces.removeAll();
		
		ArrayList<Race> races = Race.getAll();
		iRaces = new ItemSelectRace[races.size()];
		ButtonGroup group = new ButtonGroup();
		for (int i=0; i<races.size(); i++) {
			iRaces[i] = new ItemSelectRace(races.get(i));
			group.add(iRaces[i]);
			mTalents.add(iRaces[i]);
		}
	}
	
	public void createProfessionsMenu() {
		mProfessions.removeAll();
		
		ItemSelectProfession iProf;
		for (Profession p: Profession.values()) {
			iProf = new ItemSelectProfession(p);
			iProf.setSelected(Player.getInstance().hasProfession(p));
			mProfessions.add(iProf);
		}
	}
	
	private void createArmoryMenu() {
	    mArmory.removeAll();
	    
	    iImportArmory = new JMenuItem("Import");
	    iImportArmory.addActionListener(this);
	    mArmory.add(iImportArmory);	    
	}
	
	private void createFilterMenu() {
		checkedFilters = new EnumMap<Filter,Boolean>(Filter.class);
		mFilter.removeAll();
		ItemSelectFilter iFilter;
		for (Filter f: Filter.values()) {
			iFilter = new ItemSelectFilter(f.name(), f);
			iFilter.setSelected(true);
			checkedFilters.put(f, true);
			mFilter.add(iFilter);
		}
	}
	
	public boolean isSelected(Filter f) {
		if (checkedFilters.containsKey(f))
			return checkedFilters.get(f);
		return false;
	}
	
	public void checkSetup(Gear setup) {
		for (ItemSelectGear iSetup: iSetups)
			iSetup.setSelected(iSetup.getSetup() == setup);
		for (ItemSelectTalent iTalent: iTalents)
			iTalent.setSelected(iTalent.getTalents() == setup.getTalents());
	}
	
	private void selectGearSetup(Gear setup) {
		Player.getInstance().setSetup(setup);
		MainFrame.getInstance().showGear();
		checkSetup(setup);
	}
	
	private void selectTalents(Talents talents) {
		Player.getInstance().getSetup().setTalents(talents);
		MainFrame.getInstance().showStats();
	}
	
	private void selectRace(Race race) {
		Player p = Player.getInstance();
		p.setRace(race);
		Race.save();
		MainFrame.getInstance().showStats();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == iGearSave)
			Gear.save();
		else if (e.getSource() == iGearSaveAs) {
			Gear g = new Gear(Player.getInstance().getSetup());
			String s = (String) JOptionPane.showInputDialog(
          MainFrame.getInstance(),
          null, "enter Name", JOptionPane.PLAIN_MESSAGE,
          null, null, g.getName()+" Copy");
			if (s == null || s.trim().isEmpty())
				return;
			g.setName(s);
		}
		else if (e.getSource() == iGearDel) {
			int really = JOptionPane.showConfirmDialog(
					MainFrame.getInstance(),
					null,
					"Delete Gear Configuration '"+Player.getInstance().getSetup().getName()+"'",
					JOptionPane.YES_NO_OPTION);
			if (really == JOptionPane.OK_OPTION) {
				Gear.remove(Player.getInstance().getSetup());
				createGearMenu();
				revalidate();
				ArrayList<Gear> gears = Gear.getAll();
				Collections.sort(gears);
				selectGearSetup(gears.get(0));
			}
		}
		else if (e.getSource() == iGearNew) {
			String s = (String) JOptionPane.showInputDialog(
          MainFrame.getInstance(),
          null,
          "enter Name",
          JOptionPane.PLAIN_MESSAGE);
			if (s == null || s.trim().isEmpty())
				return;
			Gear g = new Gear(s.trim());
			Gear.add(g);
			createGearMenu();
			revalidate();
		}
		else if (e.getSource() == iGearRename) {
			String s = (String) JOptionPane.showInputDialog(
          MainFrame.getInstance(),
          null,
          "enter new name",
          JOptionPane.PLAIN_MESSAGE,
          null,
          null,
          Player.getInstance().getSetup().getName());
			if (s == null || s.trim().isEmpty())
				return;
			Player.getInstance().getSetup().setName(s.trim());
			createGearMenu();
			revalidate();
		}
		else if (e.getSource() == iImportArmory) {
		    ImportProfileDialog dialog = new ImportProfileDialog();
		    dialog.setVisible(true);
		    }
	}
	
	private class ItemSelectGear extends JRadioButtonMenuItem implements ActionListener {
		
		private Gear setup;
		
		public ItemSelectGear(Gear setup) {
			super(setup.getName());
			this.setup = setup;
			addActionListener(this);
		}
		
		public Gear getSetup() {
			return setup;
		}

		public void actionPerformed(ActionEvent e) {
			selectGearSetup(setup);
		}
		
	}
	
	private class ItemSelectTalent extends JRadioButtonMenuItem implements ActionListener {
		
		private Talents talents;
		
		public ItemSelectTalent(Talents talents) {
			super(talents.getName());
			this.talents = talents;
			addActionListener(this);
		}
		
		public Talents getTalents() {
			return talents;
		}

		public void actionPerformed(ActionEvent e) {
			selectTalents(talents);
		}
		
	}
	
	private class ItemSelectRace extends JRadioButtonMenuItem implements ActionListener {
		
		private Race race;
		
		public ItemSelectRace(Race race) {
			super(race.getName());
			this.race = race;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			selectRace(race);
		}
		
	}
	
	private class ItemSelectProfession extends JRadioButtonMenuItem implements ActionListener {
		
		private Profession profession;
		
		public ItemSelectProfession(Profession profession) {
			super(profession.name());
			this.profession = profession;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			Player.getInstance().setProfession(profession, this.isSelected());
			Player.getInstance().saveProfessions();
			switch (profession) {
			case Blacksmithing:
				Gear gear = Player.getInstance().getSetup();
				if (!isSelected()) {
					gear.setGem(7, gear.getItem(7).getMaxSocketIndex(), null);
					gear.setGem(8, gear.getItem(8).getMaxSocketIndex(), null);
				}
				Armor.setBlacksmith(isSelected());
				if (isSelected()) {
					gear.setGem(7, gear.getItem(7).getMaxSocketIndex(), null);
					gear.setGem(8, gear.getItem(8).getMaxSocketIndex(), null);
				}
				MainFrame.getInstance().showGear();
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
				MainFrame.getInstance().showStats();
				break;
			}
		}
		
	}
	
	private class ItemSelectFilter extends JRadioButtonMenuItem implements ActionListener {
		
		private Filter filter;
		
		public ItemSelectFilter(String name, Filter filter) {
			super(name);
			this.filter = filter;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			checkedFilters.put(filter, isSelected());
			Armor.limit();
		}
		
	}

}
