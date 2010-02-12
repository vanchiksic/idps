package iDPS.gui;


import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;


public class OSXAdapter {
	
	static void installAdapter() {
		// if not on a Mac, don't load the EAWT application
		final String os = System.getProperty("os.name");
		if (!os.startsWith("Mac OS X")) return;
		
		final Application macApp = Application.getApplication();
		
		// link the About action enabled state to the EAWT About menu
		macApp.setEnabledAboutMenu(false);
		
		// link the Preferences action enabled state to the EAWT Preferences menu
		macApp.setEnabledPreferencesMenu(false);
		
		// link the EAWT actions to Swing actions
		macApp.addApplicationListener(new ApplicationAdapter() {
			public void handleOpenFile(final ApplicationEvent e) {
				System.out.println("Open File");
			}
			
			public void handleQuit(final ApplicationEvent e) {
				System.exit(0);
			}
			
			public void handleAbout(final ApplicationEvent e) {
				System.out.println("About");
			}
			
			public void handlePreferences(final ApplicationEvent e) {
				System.out.println("Preferences");
			}
		});
	}
}
