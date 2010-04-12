package iDPS.gui.sidepanel;

import iDPS.controllers.CycleController;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SidePanel extends JScrollPane implements ActionListener, PropertyChangeListener, ChangeListener {
	
	private CycleController cycleController;
	private MainFrame mainFrame;
	
	private JPanel defaultPanel;
	private JPanel buffPanel;
	private JPanel talentPanel;
	private JPanel glyphPanel;
	
	private JCheckBox boxTotT;
	private JCheckBox boxRupture;
	private JCheckBox boxExpose;
	private JSlider slideRupture;
	private JButton buffsButton;
	private JButton talentButton;
	private JButton glyphButton;
	private JButton doneBuffs;
	
	public SidePanel(MainFrame mainFrame, CycleController cycleController) {
		this.mainFrame = mainFrame;
		this.cycleController = cycleController;
		cycleController.addPropertyChangeListener(this);
		getVerticalScrollBar().setUnitIncrement(20);
		setPreferredSize(new Dimension(430,490));
		createDefaultPanel();
		createBuffPanel();
		createTalentPanel();
		createGlyphPanel();
		
		showDefaultPanel();
	}
	
	private void createDefaultPanel() {
		defaultPanel = new JPanel();
		defaultPanel.setLayout(new BoxLayout(defaultPanel, BoxLayout.PAGE_AXIS));
		defaultPanel.add(Box.createRigidArea(new Dimension(0,50)));
		defaultPanel.add(Box.createVerticalGlue());
		
		JPanel defaultPanel1 = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		//c.fill = GridBagConstraints.BOTH;
		
		boxTotT = new JCheckBox("Use TotT every CD");
		boxTotT.setFocusable(false);
		boxTotT.addActionListener(this);
		boxTotT.setSelected(cycleController.getUseTotT());
		c.gridx = 1; c.gridy = 1;
		defaultPanel1.add(boxTotT, c);
		
		boxExpose = new JCheckBox("Maintain Expose Armor");
		boxExpose.setFocusable(false);
		boxExpose.addActionListener(this);
		boxExpose.setSelected(cycleController.getUseExpose());
		c.gridx = 1; c.gridy = 2;
		defaultPanel1.add(boxExpose, c);
		
		boxRupture = new JCheckBox("Use Rupture");
		boxRupture.setFocusable(false);
		boxRupture.addActionListener(this);
		boxRupture.setSelected(cycleController.getUseRupture());
		c.gridx = 1; c.gridy = 3;
		defaultPanel1.add(boxRupture, c);
		
		c.gridx = 1; c.gridy = 4;
		defaultPanel1.add(Box.createRigidArea(new Dimension(100,15)), c);
		
		JLabel sliderLabel = new JLabel("Projected Rupture Uptime %");
		sliderLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		c.gridx = 1; c.gridy = 5; c.anchor = GridBagConstraints.CENTER;
		defaultPanel1.add(sliderLabel, c);
		
		int val = (int) (cycleController.getRuptureUptime()*100F);
		slideRupture = new JSlider(JSlider.HORIZONTAL, 0, 100, val);
		slideRupture.setEnabled(cycleController.getUseRupture());
		slideRupture.setFocusable(false);
		slideRupture.setSnapToTicks(true);
		slideRupture.addChangeListener(this);
		slideRupture.setMinorTickSpacing(5);
		slideRupture.setMajorTickSpacing(25);
		slideRupture.setPaintTicks(true);
		slideRupture.setPaintLabels(true);
		c.gridx = 1; c.gridy = 6;
		defaultPanel1.add(slideRupture, c);
		
		defaultPanel.add(defaultPanel1);
		defaultPanel.add(Box.createRigidArea(new Dimension(0,20)));
		
		JPanel defaultPanel2 = new JPanel(new GridBagLayout());
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		buffsButton = new JButton("Set Buffs/Debuffs");
		buffsButton.setFocusable(false);
		buffsButton.addActionListener(this);
		c.gridx = 1; c.gridy = 1;
		defaultPanel2.add(buffsButton, c);
		
		talentButton = new JButton("Set Talents");
		talentButton.setFocusable(false);
		talentButton.addActionListener(this);
		c.gridx = 1; c.gridy = 2;
		defaultPanel2.add(talentButton, c);
		
		glyphButton = new JButton("Set Glyphs");
		glyphButton.setFocusable(false);
		glyphButton.addActionListener(this);
		c.gridx = 1; c.gridy = 3;
		defaultPanel2.add(glyphButton, c);
		
		defaultPanel.add(defaultPanel2);
		defaultPanel.add(Box.createVerticalGlue());
		defaultPanel.add(Box.createRigidArea(new Dimension(0,50)));
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
			cycleController.setUseRupture(boxRupture.isSelected());
			mainFrame.showStats();
		} else if (e.getSource() == boxTotT) {
			cycleController.setUseTotT(boxTotT.isSelected());
			mainFrame.showStats();
		} else if (e.getSource() == boxExpose) {
			cycleController.setUseExpose(boxExpose.isSelected());
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
		if (evt.getSource() == cycleController) {
			if (evt.getPropertyName().equals("useRupture")) {
				boolean val = (Boolean) evt.getNewValue();
				boxRupture.setSelected(val);
				slideRupture.setEnabled(val);
			} else if (evt.getPropertyName().equals("ruptureUptime")) {
				int val = (int) (((Float) evt.getNewValue())*100);
				slideRupture.setValue(val);
			} else if (evt.getPropertyName().equals("useTotT"))
				boxTotT.setSelected((Boolean) evt.getNewValue());
			else if (evt.getPropertyName().equals("useExpose"))
				boxExpose.setSelected((Boolean) evt.getNewValue());
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == slideRupture) {
			JSlider source = (JSlider) e.getSource();
		    if (!source.getValueIsAdjusting()) {
		        float val = ((int) source.getValue())/100F;
		        cycleController.setRuptureUptime(val);
		        mainFrame.showStats();
		    }
		}
	}

}
