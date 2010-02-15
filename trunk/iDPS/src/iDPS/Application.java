package iDPS;

import iDPS.gear.Enchant;
import iDPS.gear.Gem;
import iDPS.gear.Setup;

public class Application {
	
	private final BuffController buffController;
	private final FilterController filterController;
	
	private Setup setup;
	
	public Application() {
		// Create Buff Controller
		buffController = new BuffController();
		// Create Filter Controller
		filterController = new FilterController();
	}
	
	public void initialize() {
		// Load Buffs
		buffController.load();
		System.out.println("Buffs/Debuffs loaded.");
		
		// Load Filters
		filterController.load();
		System.out.println("Filters loaded.");
		
		Setup.load(this);
		System.out.println("Setups loaded.");
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
	
	public void setSetup(Setup setup) {
		this.setup = setup;
		// Limit Gems and Enchants to our Professions
		Gem.limit();
		Enchant.limit();
	}
	
	public void saveAllSetups() {
		Setup.save(this);
	}
	
	public void exit() {
		System.out.println("Bye!");
		buffController.save();
		filterController.save();
		System.exit(0);
	}

}
