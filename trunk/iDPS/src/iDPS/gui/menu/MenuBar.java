package iDPS.gui.menu;

import iDPS.Setup;
import iDPS.gui.MainFrame;
import iDPS.gui.OSXAdapter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

public class MenuBar extends JMenuBar implements ActionListener {
	
	private MainFrame mainFrame;
	
	private JMenu mSetup;
	private ItemSelectGear[] iSetups;
	private JMenuItem iGearNew, iGearRename, iGearDup, iGearSave, iGearDel, iImportArmory;
	
	private JMenu mRaces;
	private JMenu mProfessions;
	private JMenu mFilter;
		
	public MenuBar(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		
		boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));
        if (MAC_OS_X) {
            try {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setQuitHandler(mainFrame.getApp(), mainFrame.getApp().getClass().getDeclaredMethod("exit", (Class[]) null));
                //OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[])null));
                //OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[])null));
                //OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadImageFile", new Class[] { String.class }));
            } catch (Exception e) {
                System.err.println("Error while loading the OSXAdapter:");
                e.printStackTrace();
            }
        }
		
		mSetup = new JMenu("Setups");
		createGearMenu();
		add(mSetup);
		
		mRaces = new MenuRaces(mainFrame.getApp().getRaceController());
		add(mRaces);
		
		mProfessions = new MenuProfessions(mainFrame.getApp().getProfessionController());
		add(mProfessions);
		
		mFilter = new MenuFilter(mainFrame.getApp().getFilterController());
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
		
		iGearDup = new JMenuItem("Duplicate");
		iGearDup.addActionListener(this);
		mSetup.add(iGearDup);
		
	  iImportArmory = new JMenuItem("Armory...");
	  iImportArmory.addActionListener(this);
	  mSetup.add(iImportArmory);
		
		mSetup.addSeparator();
		
		iGearSave = new JMenuItem("Save all");
		iGearSave.addActionListener(this);
		mSetup.add(iGearSave);
		
		mSetup.addSeparator();
		
		ArrayList<Setup> setups = Setup.getAll();
		Setup curSetup = mainFrame.getApp().getSetup();
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
	
	public void checkSetup(Setup setup) {
		for (ItemSelectGear iSetup: iSetups)
			iSetup.setSelected(iSetup.getSetup() == setup);
	}
	
	private void selectGearSetup(Setup setup) {
		mainFrame.getApp().setSetup(setup);
		mainFrame.showGear();
		checkSetup(setup);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == iGearSave)
			mainFrame.getApp().saveAllSetups();
		else if (e.getSource() == iGearDup) {
			Setup g = mainFrame.getApp().getSetup().clone();
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
					"Delete Gear Configuration '"+mainFrame.getApp().getSetup().getName()+"'",
					JOptionPane.YES_NO_OPTION);
			if (really == JOptionPane.OK_OPTION) {
				Setup.remove(mainFrame.getApp().getSetup());
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
					mainFrame.getApp().getSetup().getName());
			if (s == null || s.trim().isEmpty())
				return;
			mainFrame.getApp().getSetup().setName(s.trim());
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

}
