package iDPS.gui;

import iDPS.Player;
import iDPS.Race;
import iDPS.Talents;
import iDPS.Player.Profession;
import iDPS.gear.Enchant;
import iDPS.gear.Gear;
import iDPS.gear.Gem;
import iDPS.gear.Item;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

public class MenuBar extends JMenuBar implements ActionListener {
	
	private JMenu mGear, mArmory;
	private JMenuItem iGearNew, iGearRename, iGearDup, iGearSave, iGearDel, iImportArmory;
	
	private JMenu mTalents, mRaces, mProfessions;
	
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
	}
	
	public void createGearMenu() {
		mGear.removeAll();
		
		iGearNew = new JMenuItem("New");
		iGearNew.addActionListener(this);
		mGear.add(iGearNew);
		
		iGearDup = new JMenuItem("Duplicate");
		iGearDup.addActionListener(this);
		mGear.add(iGearDup);
		
		iGearDel = new JMenuItem("Delete");
		iGearDel.addActionListener(this);
		mGear.add(iGearDel);
		
		iGearRename = new JMenuItem("Rename");
		iGearRename.addActionListener(this);
		mGear.add(iGearRename);
		
		iGearSave = new JMenuItem("Save");
		iGearSave.addActionListener(this);
		mGear.add(iGearSave);
		
		mGear.addSeparator();
		
		ArrayList<Gear> setups = Gear.getAll();
		Collections.sort(setups);
		Iterator<Gear> iter = setups.iterator();
		ItemSelectGear iGear;
		ButtonGroup group = new ButtonGroup();
		while (iter.hasNext()) {
			Gear setup = iter.next();
			iGear = new ItemSelectGear(setup);
			iGear.setSelected(setup == Player.getInstance().getEquipped());
			group.add(iGear);
			mGear.add(iGear);
		}
		iGearDel.setEnabled(Gear.getAll().size()>1);
	}
	
	private void createTalentsMenu() {
		mTalents.removeAll();
		
		Collection<Talents> tSpecs = Talents.getAll();
		Iterator<Talents> iter = tSpecs.iterator();
		ItemSelectTalent iTalent;
		ButtonGroup group = new ButtonGroup();
		while (iter.hasNext()) {
			Talents t = iter.next();
			iTalent = new ItemSelectTalent(t);
			iTalent.setSelected(t == Player.getInstance().getTalents());
			group.add(iTalent);
			mTalents.add(iTalent);
		}
	}
	
	private void createRacesMenu() {
		mRaces.removeAll();
		
		Collection<Race> races = Race.getAll();
		ItemSelectRace iRace;
		ButtonGroup group = new ButtonGroup();
		for (Race r: races) {
			iRace = new ItemSelectRace(r);
			iRace.setSelected(r == Player.getInstance().getRace());
			group.add(iRace);
			mRaces.add(iRace);
		}
	}
	
	private void createProfessionsMenu() {
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
	
	private void selectGearSetup(Gear setup) {
		Player.getInstance().equipGear(setup);
		MainFrame.getInstance().showGear();
	}
	
	private void selectTalents(Talents talents) {
		Player.getInstance().setTalents(talents);
		MainFrame.getInstance().showStats();
		Talents.save();
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
		else if (e.getSource() == iGearDup) {
			Gear g = Player.getInstance().getEquipped().clone();
			g.clearId();
			g.setName(Player.getInstance().getEquipped().getName()+" Copy");
			Gear.add(g);
			createGearMenu();
			revalidate();
		}
		else if (e.getSource() == iGearDel) {
			int really = JOptionPane.showConfirmDialog(
					MainFrame.getInstance(),
					null,
					"Delete Gear Configuration '"+Player.getInstance().getEquipped().getName()+"'",
					JOptionPane.YES_NO_OPTION);
			if (really == JOptionPane.OK_OPTION) {
				Gear.remove(Player.getInstance().getEquipped());
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
          Player.getInstance().getEquipped().getName());
			if (s == null || s.trim().isEmpty())
				return;
			Player.getInstance().getEquipped().setName(s.trim());
			createGearMenu();
			revalidate();
		}
		else if (e.getSource() == iImportArmory) {
		    ImportProfileDialog dialog = new ImportProfileDialog();
		    dialog.setVisible(true);
		    }
	}
	
	public class ItemSelectGear extends JRadioButtonMenuItem implements ActionListener {
		
		private Gear setup;
		
		public ItemSelectGear(Gear setup) {
			super(setup.getName());
			this.setup = setup;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			selectGearSetup(setup);
		}
		
	}
	
	public class ItemSelectTalent extends JRadioButtonMenuItem implements ActionListener {
		
		private Talents talents;
		
		public ItemSelectTalent(Talents talents) {
			super(talents.getName());
			this.talents = talents;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			selectTalents(talents);
		}
		
	}
	
	public class ItemSelectRace extends JRadioButtonMenuItem implements ActionListener {
		
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
	
	public class ItemSelectProfession extends JRadioButtonMenuItem implements ActionListener {
		
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
				Gear gear = Player.getInstance().getEquipped();
				if (!isSelected()) {
					gear.setGem(7, gear.getItem(7).getMaxSocketIndex(), null);
					gear.setGem(8, gear.getItem(8).getMaxSocketIndex(), null);
				}
				Item.setBlacksmith(isSelected());
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

}
