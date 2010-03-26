package iDPS.gui;

import iDPS.Persistency;
import iDPS.armory.ImportWorker;
import iDPS.armory.Realm;
import iDPS.armory.Realm.Region;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker.StateValue;

import org.jdom.Document;
import org.jdom.Element;

public final class ImportProfileDialog extends JDialog implements ActionListener, PropertyChangeListener {

	private JComboBox mRegions;
	private JComboBox mRealms;
	private JTextField mCharacterName;
	private JProgressBar progressBar;

	private JButton mImportButton;
	private ImportWorker importWorker;

	ImportProfileDialog(MainFrame mainFrame) {
		super(mainFrame, "Import Profile", true);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;  c.weighty = 0;
		c.insets = new Insets(5, 5, 0, 5);

		c.gridx = 0; c.gridy = 0;
		add(new JLabel("Region:"), c);

		mRegions = new JComboBox();
		for (Region r: Region.values())
			mRegions.addItem(r.name());
		mRegions.addActionListener(this);
		c.gridx = 1; c.gridy = 0;
		add(mRegions, c);

		c.gridx = 0; c.gridy = 1;
		add(new JLabel("Realm:"), c);

		mRealms = new JComboBox();
		mRealms.addActionListener(this);
		c.gridx = 1; c.gridy = 1;
		add(mRealms, c);

		c.gridx = 0; c.gridy = 2;
		add(new JLabel("Character:"), c);

		mCharacterName = new JTextField(15);
		mCharacterName.setPreferredSize(mRealms.getPreferredSize());
		c.gridx = 1; c.gridy = 2;
		add(mCharacterName, c);

		mImportButton = new JButton("Import");
		mImportButton.addActionListener(this);
		c.gridx = 0; c.gridy = 3; c.gridwidth = 3;
		c.fill = GridBagConstraints.NONE; c.insets = new Insets(5, 5, 5, 5);
		add(mImportButton, c);

		progressBar = new JProgressBar(0,100);
		c.gridx = 0; c.gridy = 4; c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL; c.insets = new Insets(0,0,0,0);
		add(progressBar, c);

		updateRealms();
		pack();

		// put it in the center
		int x = mainFrame.getLocation().x + mainFrame.getSize().width/2 - getSize().width/2;
		int y = mainFrame.getLocation().y + mainFrame.getSize().height/2 - getSize().height/2;
		setLocation(new Point(x,y));

		setResizable(false);

		load();
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == mRegions) {
			updateRealms();
		} else if (event.getSource() == mImportButton) {
			if (importWorker != null)
				return;
			String character = mCharacterName.getText();
			Realm realm = (Realm) mRealms.getSelectedItem();
			importWorker = new ImportWorker(character, realm);
			setProcessing(true);
			importWorker.addPropertyChangeListener(this);
			importWorker.execute();
			save();
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == importWorker) {
			if ("progress".equals(evt.getPropertyName())) {
				progressBar.setIndeterminate(false);
				progressBar.setValue((Integer) evt.getNewValue());
			} else if ("state".equals(evt.getPropertyName())) {
				if (evt.getNewValue() == StateValue.DONE) {
					setProcessing(false);
					setVisible(false);
					importWorker = null;
				}
			}
		}
	}

	public void setProcessing(boolean b) {
		progressBar.setValue(0);
		progressBar.setIndeterminate(b);
		mImportButton.setEnabled(!b);
	}

	private void updateRealms() {
		mRealms.removeAllItems();
		Region region = Region.valueOf((String) mRegions.getSelectedItem());
		List<Realm> realms = Realm.getRealms(region);
		for (Realm r: realms)
			mRealms.addItem(r);
	}

	@SuppressWarnings("unchecked")
	private void save() {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);

		Element elem = Persistency.getElement(doc, "import");
		elem.removeContent();
		Element sub;

		sub = new Element("region");
		sub.setText(mRegions.getSelectedItem().toString());
		elem.getChildren().add(sub);

		sub = new Element("realm");
		sub.setText(mRealms.getSelectedItem().toString());
		elem.getChildren().add(sub);

		sub = new Element("character");
		sub.setText(mCharacterName.getText());
		elem.getChildren().add(sub);

		Persistency.saveXML(doc, Persistency.FileType.Settings);
	}

	@SuppressWarnings("unchecked")
	private void load() {
		Document doc = Persistency.openXML(Persistency.FileType.Settings);
		Element elem = doc.getRootElement().getChild("import");
		if (elem == null)
			return;
		for (Element e: (List<Element>) elem.getChildren()) {
			String s = e.getName();
			// Region
			if (s.equals("region")) {
				for (int i=0; i<mRegions.getModel().getSize(); i++) {
					String tmp = (String) mRegions.getModel().getElementAt(i);
					if (tmp.equals(e.getText())) {
						mRegions.setSelectedIndex(i);
						break;
					}
				}
			}
			// Realms
			if (s.equals("realm")) {
				for (int i=0; i<mRealms.getModel().getSize(); i++) {
					Realm realm = (Realm) mRealms.getModel().getElementAt(i);
					if (realm.getName().equals(e.getText())) {
						mRealms.setSelectedIndex(i);
						break;
					}
				}
			}
			// Name
			if (s.equals("character")) {
				mCharacterName.setText(e.getText());
			}
		}
	}

}

