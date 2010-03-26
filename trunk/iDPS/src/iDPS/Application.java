package iDPS;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import iDPS.controllers.BuffController;
import iDPS.controllers.CycleController;
import iDPS.controllers.FilterController;
import iDPS.controllers.GlyphsController;
import iDPS.controllers.ProfessionController;
import iDPS.controllers.RaceController;
import iDPS.controllers.TalentsController;
import iDPS.gear.Enchant;
import iDPS.gear.Gem;
import iDPS.gui.MainFrame;

public class Application {
	
	private MainFrame mainFrame;
	
	private final PropertyChangeSupport pcs;
	private final CycleController cycleController;
	private final BuffController buffController;
	private final FilterController filterController;
	private final TalentsController talentsController;
	private final GlyphsController glyphsController;
	private final ProfessionController professionsController;
	private final RaceController raceController;
	
	private Setup setup;
	
	public Application() {
		pcs = new PropertyChangeSupport(this);
		// Create Cycle Controller
		cycleController = new CycleController(this);
		// Create Buff Controller
		buffController = new BuffController(this);
		// Create Filter Controller
		filterController = new FilterController();
		// Create Talents Controller
		talentsController = new TalentsController(this);
		// Create Glyphs Controller
		glyphsController = new GlyphsController(this);
		// Create Professions Controller
		professionsController = new ProfessionController(this);
		// Create Race Controller
		raceController = new RaceController(this);
	}
	
	public void initialize() {
		Talents.load();
		System.out.println("Talents generated.");
		
		Setup.load(this);
		System.out.println("Setups loaded.");
		
		// Load Filters
		filterController.load();
		System.out.println("Filters loaded.");
		
		// Start GUI
		mainFrame = new MainFrame(this);
		mainFrame.getMyMenuBar().createGearMenu();
		
		mainFrame.showGear();
		mainFrame.getMyMenuBar().checkSetup(getSetup());
	}
	
	public BuffController getBuffController() {
		return buffController;
	}
	
	public FilterController getFilterController() {
		return filterController;
	}
	
	public TalentsController getTalentsController() {
		return talentsController;
	}
	
	public GlyphsController getGlyphsController() {
		return glyphsController;
	}
	
	public ProfessionController getProfessionController() {
		return professionsController;
	}
	
	public RaceController getRaceController() {
		return raceController;
	}
	
	public CycleController getCycleController() {
		return cycleController;
	}

	public ProfessionController getProfessionsController() {
		return professionsController;
	}
	
	public Setup getSetup() {
		return setup;
	}
	
	public void setSetup(Setup newSetup) {
		Setup oldSetup = this.setup;
		this.setup = newSetup;
		pcs.firePropertyChange("setup", oldSetup, newSetup);
		// Limit Gems and Enchants to our Professions
		Gem.limit();
		Enchant.limit();
	}
	
	public void saveAllSetups() {
		Setup.save(this);
	}
	
	public void exit() {
		System.out.println("Bye!");
		filterController.save();
		System.exit(0);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

	public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

	public MainFrame getMainFrame() {
		return mainFrame;
	}

}
