package iDPS.gui;

import iDPS.BuffCentral;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSeparator;


public class BuffPanel extends JPanel implements ActionListener, PropertyChangeListener {
	
	BuffCentral central;
	
	JCheckBox cbArmorMajor;
	JCheckBox cbArmorMaintain;
	JCheckBox cbArmorMinor;
	JCheckBox cbKings;
	
	public BuffPanel(BuffCentral buffCentral) {
		super(new GridBagLayout());
	
		central = buffCentral;
		central.addPropertyChangeListener(this);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		cbArmorMajor = createCheckbox("Major Armor Debuff", central.isArmorMajor());
		c.gridx = 0; c.gridy = 0; c.insets = new Insets(0,0,0,0);
		add(cbArmorMajor, c);
		
		cbArmorMaintain = createCheckbox("Maintaining Expose Armor", central.isArmorMaintain());
		cbArmorMaintain.setEnabled(central.isArmorMajor());
		c.gridx = 0; c.gridy = 1; c.insets = new Insets(0,20,0,0);
		add(cbArmorMaintain, c);
		
		cbArmorMinor = createCheckbox("Minor Armor Debuff", central.isArmorMinor());
		c.gridx = 0; c.gridy = 2; c.insets = new Insets(0,0,0,0);
		add(cbArmorMinor, c);
		
		c.gridx = 0; c.gridy = 3; c.insets = new Insets(0,0,0,0);
		add(new JSeparator(), c);
		
		cbKings = createCheckbox("Blessing of Kings", central.isKings());
		c.gridx = 0; c.gridy = 4; c.insets = new Insets(0,0,0,0);
		add(cbKings, c);
		
	}
	
	private JCheckBox createCheckbox(String name, boolean selected) {
		JCheckBox cb = new JCheckBox(name);
		cb.setSelected(selected);
		cb.addActionListener(this);
		cb.setFocusable(false);
		return cb;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("armorMajor")) {
			boolean b = central.isArmorMajor();
			cbArmorMajor.setSelected(b);
			cbArmorMaintain.setEnabled(b);
		} else if (evt.getPropertyName().equals("armorMaintain")) {
			cbArmorMaintain.setSelected(central.isArmorMaintain());
		} else if (evt.getPropertyName().equals("kings")) {
			cbArmorMinor.setSelected(central.isArmorMinor());
		} else if (evt.getPropertyName().equals("kings")) {
			cbKings.setSelected(central.isKings());
		}
	}

	public void actionPerformed(ActionEvent e) {
		JCheckBox cb = (JCheckBox) e.getSource();
		if (cb == cbArmorMajor)
			central.setArmorMajor(cb.isSelected());
		else if (cb == cbArmorMaintain)
			central.setArmorMaintain(cb.isSelected());
		else if (cb == cbArmorMinor)
			central.setArmorMinor(cb.isSelected());
		else if (cb == cbKings)
			central.setKings(cb.isSelected());
	}

}
