package iDPS.gui.sidepanel;

import iDPS.Application;
import iDPS.gui.MainFrame;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class SidePanel extends JScrollPane implements ActionListener, PropertyChangeListener {
	
	private MainFrame mainFrame;
	private JPanel defaultPanel;
	private JPanel buffPanel;
	private JPanel talentPanel;
	private JPanel glyphPanel;
	
	private JCheckBox boxTotT;
	private JCheckBox boxRupture;
	private JCheckBox boxExpose;
	private JButton buffsButton;
	private JButton talentButton;
	private JButton glyphButton;
	private JButton doneBuffs;
	
	public SidePanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		mainFrame.getApp().addPropertyChangeListener(this);
		getVerticalScrollBar().setUnitIncrement(20);
		setPreferredSize(new Dimension(430,490));
		createDefaultPanel();
		createBuffPanel();
		createTalentPanel();
		createGlyphPanel();
		
		showDefaultPanel();
	}
	
	private void createDefaultPanel() {
		defaultPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		
		c.gridwidth = 1; c.gridheight = 3; c.weightx = 1;		
		c.gridx = 0; c.gridy = 1;
		defaultPanel.add(Box.createGlue(), c);
		c.gridx = 4; c.gridy = 1;
		defaultPanel.add(Box.createGlue(), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0; c.gridheight = 1; c.gridwidth = 3;
		
		boxTotT = new JCheckBox("Use TotT every CD");
		boxTotT.setFocusable(false);
		boxTotT.addActionListener(this);
		boxTotT.setSelected(mainFrame.getApp().getUseTotT());
		c.gridx = 1; c.gridy = 1;
		defaultPanel.add(boxTotT, c);
		
		boxRupture = new JCheckBox("Use Rupture");
		boxRupture.setFocusable(false);
		boxRupture.addActionListener(this);
		boxRupture.setSelected(mainFrame.getApp().getUseRupture());
		c.gridx = 1; c.gridy = 2;
		defaultPanel.add(boxRupture, c);
		
		boxExpose = new JCheckBox("Maintain Expose Armor");
		boxExpose.setFocusable(false);
		boxExpose.addActionListener(this);
		boxExpose.setSelected(mainFrame.getApp().getUseExpose());
		c.gridx = 1; c.gridy = 3;
		defaultPanel.add(boxExpose, c);
		
		c.gridx = 1; c.gridy = 4;
		defaultPanel.add(Box.createRigidArea(new Dimension(100,20)), c);
		
		c.gridwidth = 1; c.gridheight = 2; c.weightx = 0.001;
		c.gridx = 1; c.gridy = 5;
		defaultPanel.add(Box.createGlue(), c);
		c.gridx = 3; c.gridy = 5;
		defaultPanel.add(Box.createGlue(), c);
		
		c.weightx = 0; c.gridwidth = 1; c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		buffsButton = new JButton("Set Buffs/Debuffs");
		buffsButton.setFocusable(false);
		buffsButton.addActionListener(this);
		c.gridx = 2; c.gridy = 5;
		defaultPanel.add(buffsButton, c);
		
		talentButton = new JButton("Set Talents");
		talentButton.setFocusable(false);
		talentButton.addActionListener(this);
		talentButton.setSize(buffsButton.getPreferredSize());
		c.gridx = 2; c.gridy = 6;
		defaultPanel.add(talentButton, c);
		
		glyphButton = new JButton("Set Glyphs");
		glyphButton.setFocusable(false);
		glyphButton.addActionListener(this);
		glyphButton.setSize(buffsButton.getPreferredSize());
		c.gridx = 2; c.gridy = 7;
		defaultPanel.add(glyphButton, c);
	}
	
	private void createBuffPanel() {
		buffPanel = new JPanel();
		buffPanel.setLayout(new BoxLayout(buffPanel, BoxLayout.PAGE_AXIS));
		
		JTabbedPane tp = new JTabbedPane();
		JPanel bp = new BuffPanelBuffs(mainFrame, mainFrame.getApp().getBuffController());
		JPanel cp = new BuffPanelConsumables(mainFrame, mainFrame.getApp().getBuffController());
		JPanel dp = new BuffPanelDebuffs(mainFrame, mainFrame.getApp().getBuffController());
		JPanel op = new BuffPanelOther(mainFrame, mainFrame.getApp().getBuffController());
		tp.addTab("Buffs", bp);
		tp.addTab("Consumables", cp);
		tp.addTab("Debuffs", dp);
		tp.addTab("Other", op);
		buffPanel.add(tp);
		
		doneBuffs = new JButton("done");
		doneBuffs.addActionListener(this);
		doneBuffs.setAlignmentX(CENTER_ALIGNMENT);
		buffPanel.add(doneBuffs);
	}
	
	private void createTalentPanel() {
		talentPanel = new TalentPanel(mainFrame);
	}
	
	private void createGlyphPanel() {
		glyphPanel = new GlyphPanel(mainFrame);
	}
	
	public void showDefaultPanel() {
		setViewportView(defaultPanel);
	}
	
	public void showBuffPanel() {
		setViewportView(buffPanel);
	}
	
	public void showTalentPanel() {
		setViewportView(talentPanel);
	}
	
	public void showGlyphPanel() {
		setViewportView(glyphPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == boxRupture) {
			mainFrame.getApp().setUseRupture(boxRupture.isSelected());
			mainFrame.showStats();
		} else if (e.getSource() == boxTotT) {
			mainFrame.getApp().setUseTotT(boxTotT.isSelected());
			mainFrame.showStats();
		} else if (e.getSource() == boxExpose) {
			mainFrame.getApp().setUseExpose(boxExpose.isSelected());
			mainFrame.showStats();
		} else if (e.getSource() == buffsButton) {
			showBuffPanel();
		} else if (e.getSource() == talentButton) {
			showTalentPanel();
		} else if (e.getSource() == glyphButton) {
			showGlyphPanel();
		} else if (e.getSource() == doneBuffs) {
			showDefaultPanel();
		}
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof Application) {
			if (evt.getPropertyName().equals("useRupture"))
				boxRupture.setSelected((Boolean) evt.getNewValue());
			if (evt.getPropertyName().equals("useTotT"))
				boxTotT.setSelected((Boolean) evt.getNewValue());
			if (evt.getPropertyName().equals("useExpose"))
				boxExpose.setSelected((Boolean) evt.getNewValue());
		}
	}

}
