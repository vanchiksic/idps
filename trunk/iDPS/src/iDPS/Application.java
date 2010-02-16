package iDPS;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import iDPS.gear.Enchant;
import iDPS.gear.Gem;
import iDPS.gear.Setup;

public class Application {
	
	private final PropertyChangeSupport pcs;
	private final BuffController buffController;
	private final FilterController filterController;
	
	private Setup setup;
	
	public Application() {
		pcs = new PropertyChangeSupport(this);
		// Create Buff Controller
		buffController = new BuffController(this);
		// Create Filter Controller
		filterController = new FilterController();
	}
	
	public void initialize() {
		Setup.load(this);
		System.out.println("Setups loaded.");
		
		// Load Filters
		filterController.load();
		System.out.println("Filters loaded.");
	}
	
	public BuffController getBuffController() {
		return buffController;
	}
	
	public FilterController getFilterController() {
		return filterController;
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

}
