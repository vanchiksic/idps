package iDPS.gui.sidepanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;

import iDPS.Talents;
import iDPS.TalentsController;
import iDPS.Talents.Talent;
import iDPS.Talents.Tree;
import iDPS.gui.MainFrame;
import iDPS.model.Calculations.ModelType;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class TalentPanel extends JPanel implements ActionListener, PropertyChangeListener {
	
	private final MainFrame mainFrame;
	private final TalentsController tc;
	private HashMap<String,TalentField> fields;
	private JComboBox modelSelect;
	private JButton done;
	
	public TalentPanel(MainFrame mainFrame) {
		super(new GridBagLayout());
		this.mainFrame = mainFrame;
		tc = mainFrame.getApp().getTalentsController();
		
		GridBagConstraints c1 = new GridBagConstraints();
		
		c1.gridx = 0; c1.gridy = 1;
		c1.weightx = 1;
		add(Box.createGlue(), c1);
		
		JLabel label = new JLabel("Model Type: ");
		c1.gridx = 1; c1.gridy = 1;
		c1.weightx = 0;
		add(label, c1);
		
		String[] options = {"Combat", "Mutilate"};
		modelSelect = new JComboBox(options);
		modelSelect.setFocusable(false);
		modelSelect.setAlignmentX(CENTER_ALIGNMENT);
		if (tc.getModel() == ModelType.Mutilate)
			modelSelect.setSelectedIndex(1);
		else
			modelSelect.setSelectedIndex(0);
		modelSelect.addActionListener(this);
		c1.gridx = 2; c1.gridy = 1;
		add(modelSelect, c1);
		
		c1.gridx = 3; c1.gridy = 1;
		c1.weightx = 1;
		add(Box.createGlue(), c1);
		
		c1.gridx = 0; c1.gridy = 2;
		c1.weightx = 0; c1.gridwidth = 4;
		add(Box.createRigidArea(new Dimension(200,20)), c1);
		
		fields = new HashMap<String,TalentField>();
		JTabbedPane tp = new JTabbedPane();
		Talents talents2 = mainFrame.getApp().getSetup().getTalents();
		Insets iLabel = new Insets(0,0,0,10);
		Insets iField = new Insets(0,0,0,20);
		for (Tree tree: Tree.values()) {
			JPanel tab = new JPanel(new GridBagLayout());
			GridBagConstraints c2 = new GridBagConstraints();

			Collection<Talents.Talent> talents = talents2.getTalents(tree);
			
			int i = 0; boolean right = false;
			for (Talent t: talents) {
				int x = 0;
				if (right)
					x = 2;
				label = new JLabel(t.getName());
				c2.gridx = x; c2.gridy = i; c2.insets = iLabel;
				c2.anchor = GridBagConstraints.EAST;
				tab.add(label, c2);
				TalentField field = new TalentField(t);
				fields.put(t.getIdentifier(), field);
				c2.gridx = x+1; c2.gridy = i; c2.insets = iField;
				c2.anchor = GridBagConstraints.WEST;
				tab.add(field, c2);
				if (right) {
					right = false;
					i++;
				} else
					right = true;
			}
			c2.anchor = GridBagConstraints.CENTER; c2.gridwidth = 4;
			c2.gridx = 0; c2.gridy = i+1; c2.insets = new Insets(50,0,0,0);
			tp.addTab(tree.name(), tab);
		}
		c1.gridx = 0; c1.gridy = 3;
		add(tp, c1);
		
		done = new JButton("done");
		done.addActionListener(this);
		c1.gridx = 1; c1.gridy = 5;
		c1.gridwidth = 2;
		add(done, c1);
		
		tc.addPropertyChangeListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == modelSelect) {
			ModelType mt = ModelType.valueOf((String)modelSelect.getSelectedItem());
			tc.setModel(mt);
			mainFrame.showStats();
		} else if (e.getSource() == done) {
			mainFrame.getSideScroll().showDefaultPanel();
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof TalentsController) {
			if (evt.getPropertyName().equals("modelType")) {
				modelSelect.setSelectedItem(evt.getNewValue());
			}
			if (fields.containsKey(evt.getPropertyName()))
				fields.get(evt.getPropertyName()).setValue((Integer) evt.getNewValue());
		}
	}
	
	private class TalentField extends JTextField implements FocusListener {
		
		private boolean armed;
		private Talent talent;
		
		public TalentField(Talent talent) {
			super(1);
			this.talent = talent;
			setHorizontalAlignment(CENTER);
			setDocument(new TalentFieldDocument());
			addFocusListener(this);
			setValue(tc.getTalentPoints(talent));
		}
		
		private synchronized void setValue(int i) {
			armed = false;
			setText(String.valueOf(i));
			armed = true;
		}

		public void focusGained(FocusEvent e) {
			setSelectionStart(0);
			setSelectionEnd(1);
		}

		public void focusLost(FocusEvent e) {
			if (getText().length()==0)
				setText("0");
		}
		
		private class TalentFieldDocument extends PlainDocument {

			public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {
				if (string == null) {
					return;
				} else {
					int length = getLength() + string.length();
					if (length > 1)
						return;
					try {
						int i = Integer.parseInt(string);
						if (i>=0 && i<=talent.getMaxPoints()) {
							super.insertString(offset, string, attributes);
							setSelectionStart(0);
							setSelectionEnd(1);
							if (armed) {
								tc.setTalentPoints(talent, i);
								mainFrame.showStats();
							}
						} else
							Toolkit.getDefaultToolkit().beep();
					} catch (NumberFormatException exception) {
						Toolkit.getDefaultToolkit().beep();
					}
				}
			}
		}
		
	}

}
