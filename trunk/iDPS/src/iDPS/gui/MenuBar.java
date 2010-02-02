package iDPS.gui;

import iDPS.Race;
import iDPS.Talents;
import iDPS.gear.Enchant;
import iDPS.gear.Item;
import iDPS.gear.Setup;
import iDPS.gear.Gem;
import iDPS.gear.Armor;
import iDPS.gear.Item.Filter;
import iDPS.gear.Setup.Profession;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

public class MenuBar extends JMenuBar implements ActionListener {
	
	private MainFrame mainFrame;
	
	private JMenu mSetup;
	private ItemSelectGear[] iSetups;
	private JMenuItem iGearNew, iGearRename, iGearSaveAs, iGearSave, iGearDel, iImportArmory;
	
	private JMenu mTalents;
	private ItemSelectTalent[] iTalents;
	private ButtonGroup gTalents;
	
	private JMenu mRaces;
	private ItemSelectRace[] iRaces;
	private ButtonGroup gRaces;
	
	private JMenu mProfessions;
	private ItemSelectProfession[] iProfessions;
	
	private JMenu mFilter;
	
	private EnumSet<Filter> checkedFilters;
	
	public MenuBar(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		checkedFilters = EnumSet.allOf(Filter.class);
		
		mSetup = new JMenu("Setups");
		createGearMenu();
		add(mSetup);
		
		mTalents = new JMenu("Talent Specs");
		createTalentsMenu();
		add(mTalents);
		
		mRaces = new JMenu("Race");
		createRacesMenu();
		add(mRaces);
		
		mProfessions = new JMenu("Professions");
		createProfessionsMenu();
		add(mProfessions);
		
		mFilter = new JMenu("Loot Filter");
		createFilterMenu();
		add(mFilter);
	}
	
	public void createGearMenu() {
		mSetup.removeAll();
		
		iGearNew = new JMenuItem("New");
		iGearNew.addActionListener(this);
		mSetup.add(iGearNew);
		
		iGearDel = new JMenuItem("Delete");
		iGearDel.addActionListener(this);
		mSetup.add(iGearDel);
		
		iGearRename = new JMenuItem("Rename");
		iGearRename.addActionListener(this);
		mSetup.add(iGearRename);
		
		iGearSave = new JMenuItem("Save");
		iGearSave.addActionListener(this);
		mSetup.add(iGearSave);
		
		iGearSaveAs = new JMenuItem("Save As...");
		iGearSaveAs.addActionListener(this);
		mSetup.add(iGearSaveAs);
		
	    iImportArmory = new JMenuItem("Armory...");
	    iImportArmory.addActionListener(this);
	    mSetup.add(iImportArmory);
		
		mSetup.addSeparator();
		
		ArrayList<Setup> setups = Setup.getAll();
		Setup curSetup = mainFrame.getSetup();
		Collections.sort(setups);
		iSetups = new ItemSelectGear[setups.size()];
		ButtonGroup group = new ButtonGroup();
		for (int i=0; i<setups.size(); i++) {
			iSetups[i] = new ItemSelectGear(setups.get(i));
			if (curSetup == setups.get(i))
				iSetups[i].setSelected(true);
			group.add(iSetups[i]);
			mSetup.add(iSetups[i]);
		}
		
		iGearDel.setEnabled(Setup.getAll().size()>1);
	}
	
	public void createTalentsMenu() {
		mTalents.removeAll();
		
		ArrayList<Talents> tSpecs = Talents.getAll();
		iTalents = new ItemSelectTalent[tSpecs.size()];
		gTalents = new ButtonGroup();
		for (int i=0; i<tSpecs.size(); i++) {
			iTalents[i] = new ItemSelectTalent(tSpecs.get(i));
			gTalents.add(iTalents[i]);
			mTalents.add(iTalents[i]);
		}
	}
	
	public void createRacesMenu() {
		mRaces.removeAll();
		
		ArrayList<Race> races = Race.getAll();
		iRaces = new ItemSelectRace[races.size()];
		gRaces = new ButtonGroup();
		for (int i=0; i<races.size(); i++) {
			iRaces[i] = new ItemSelectRace(races.get(i));
			gRaces.add(iRaces[i]);
			mRaces.add(iRaces[i]);
		}
	}
	
	public void createProfessionsMenu() {
		mProfessions.removeAll();
		
		iProfessions = new ItemSelectProfession[Profession.values().length];
		for (int i=0; i<Profession.values().length; i++) {
			iProfessions[i] = new ItemSelectProfession(Profession.values()[i]);
			mProfessions.add(iProfessions[i]);
		}
	}
	
	public void createFilterMenu() {
		mFilter.removeAll();
		
		ItemSelectFilter iFilter;
		for (Filter f: Filter.values()) {
			iFilter = new ItemSelectFilter(f.name(), f);
			iFilter.setSelected(checkedFilters.contains(f));
			mFilter.add(iFilter);
		}
	}
	
	public void setFilter(Filter f, boolean b) {
		if (b)
			checkedFilters.add(f);
		else
			checkedFilters.remove(f);
	}
	
	public boolean isOneFilterChecked(EnumSet<Filter> fs) {
		for (Filter f: fs) {
			if (checkedFilters.contains(f))
				return true;
		}
		return false;
	}
	
	public void checkSetup(Setup setup) {
		for (ItemSelectGear iSetup: iSetups)
			iSetup.setSelected(iSetup.getSetup() == setup);
		
		gTalents.clearSelection();
		for (ItemSelectTalent iTalent: iTalents)
			gTalents.setSelected(iTalent.getModel(), (iTalent.getTalents() == setup.getTalents()));
		
		gRaces.clearSelection();
		for (ItemSelectRace iRace: iRaces)
			gRaces.setSelected(iRace.getModel(), (iRace.getRace() == setup.getRace()));
		
		for (ItemSelectProfession iProfession: iProfessions)
			iProfession.setSelected(setup.hasProfession(iProfession.getProfession()));
	}
	
	private void selectGearSetup(Setup setup) {
		mainFrame.setSetup(setup);
		mainFrame.showGear();
		checkSetup(setup);
	}
	
	private void selectTalents(Talents talents) {
		mainFrame.getSetup().setTalents(talents);
		mainFrame.showStats();
	}
	
	private void selectRace(Race race) {
		mainFrame.getSetup().setRace(race);
		mainFrame.showStats();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == iGearSave)
			Setup.save();
		else if (e.getSource() == iGearSaveAs) {
			Setup g = new Setup(mainFrame.getSetup());
			g.clearId();
			String s = (String) JOptionPane.showInputDialog(
					mainFrame,
					null, "enter Name", JOptionPane.PLAIN_MESSAGE,
					null, null, g.getName()+" Copy");
			if (s == null || s.trim().isEmpty())
				return;
			g.setName(s);
			Setup.add(g);
			createGearMenu();
		}
		else if (e.getSource() == iGearDel) {
			int really = JOptionPane.showConfirmDialog(
					mainFrame,
					null,
					"Delete Gear Configuration '"+mainFrame.getSetup().getName()+"'",
					JOptionPane.YES_NO_OPTION);
			if (really == JOptionPane.OK_OPTION) {
				Setup.remove(mainFrame.getSetup());
				createGearMenu();
				revalidate();
				ArrayList<Setup> gears = Setup.getAll();
				Collections.sort(gears);
				selectGearSetup(gears.get(0));
			}
		}
		else if (e.getSource() == iGearNew) {
			String s = (String) JOptionPane.showInputDialog(
					mainFrame,
					null,
					"enter Name",
					JOptionPane.PLAIN_MESSAGE);
			if (s == null || s.trim().isEmpty())
				return;
			Setup g = new Setup(s.trim());
			Setup.add(g);
			createGearMenu();
			revalidate();
		}
		else if (e.getSource() == iGearRename) {
			String s = (String) JOptionPane.showInputDialog(
					mainFrame,
					null,
					"enter new name",
					JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					mainFrame.getSetup().getName());
			if (s == null || s.trim().isEmpty())
				return;
			mainFrame.getSetup().setName(s.trim());
			createGearMenu();
			revalidate();
		}
		else if (e.getSource() == iImportArmory) {
			mainFrame.createAndShowImportFrame();
		}
	}
	
	private class ItemSelectGear extends JRadioButtonMenuItem implements ActionListener {
		
		private Setup setup;
		
		public ItemSelectGear(Setup setup) {
			super(setup.getName());
			this.setup = setup;
			addActionListener(this);
		}
		
		public Setup getSetup() {
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
		
		public Race getRace() {
			return race;
		}

		public void actionPerformed(ActionEvent e) {
			selectRace(race);
		}
		
	}
	
	private class ItemSelectProfession extends JCheckBoxMenuItem implements ActionListener {
		
		private Profession profession;
		
		public ItemSelectProfession(Profession profession) {
			super(profession.name());
			this.profession = profession;
			addActionListener(this);
		}
		
		public Profession getProfession() {
			return profession;
		}

		public void actionPerformed(ActionEvent e) {
			Setup setup = mainFrame.getSetup();
			setup.setProfession(profession, isSelected());
			
			switch (profession) {
				case Blacksmithing:
					if (!isSelected()) {
						setup.setGem(7, setup.getItem(7).getMaxSocketIndex(), null);
						setup.setGem(8, setup.getItem(8).getMaxSocketIndex(), null);
					}
					Armor.setBlacksmith(isSelected());
					if (isSelected()) {
						setup.setGem(7, setup.getItem(7).getMaxSocketIndex(), null);
						setup.setGem(8, setup.getItem(8).getMaxSocketIndex(), null);
					}
					mainFrame.showGear();
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
					mainFrame.showStats();
					break;
			}
		}
		
	}
	
	private class ItemSelectFilter extends JCheckBoxMenuItem implements ActionListener {
		
		private Filter filter;
		
		public ItemSelectFilter(String name, Filter filter) {
			super(name);
			this.filter = filter;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			setFilter(filter, isSelected());
			Armor.limit();
			Item.saveFilter();
		}
		
	}

}
