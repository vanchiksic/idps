package iDPS.gui;

import iDPS.Attributes;
import iDPS.model.Calculations;
import iDPS.model.Modifiers;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class CenterPanel extends JPanel {
	
	private GridBagConstraints c;
	private MainFrame mainFrame;
	private JTextField[] fields;
	
	public CenterPanel(MainFrame mainFrame) {
		super(new GridBagLayout());
		this.mainFrame = mainFrame;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		fields = new JTextField[20];
		
		addLabel("DPS", 0, 0);
		fields[0]  = createField(1,0, 4);
		
		addLabel("AGI", 0, 2);
		addLabel("ATP", 0, 3);
		addLabel("HIT", 0, 4);
		addLabel("CRI", 0, 5);
		addLabel("HST", 0, 6);
		addLabel("ARP", 0, 7);
		addLabel("EXP", 0, 8);
		fields[1]  = createField(1,2, 4);
		fields[2]  = createField(1,3, 4);
		fields[3]  = createField(1,4, 4);
		fields[4]  = createField(1,5, 4);
		fields[5]  = createField(1,6, 4);
		fields[6]  = createField(1,7, 4);
		fields[7]  = createField(1,8, 4);

		addLabel("Mis%", 3, 2);
		addLabel("Dod%", 3, 3);
		addLabel("Cri%", 3, 4);
		addLabel("Cap%", 3, 5);
		fields[8]  = createField(4, 2, 4);
		fields[9]  = createField(4, 3, 4);
		fields[10] = createField(4, 4, 4);
		fields[11] = createField(4, 5, 4);
		
		addLabel("Mis%", 3, 7);
		addLabel("Cri%", 3, 8);
		fields[12] = createField(4, 7, 4);
		fields[13] = createField(4, 8, 4);
		
		addLabel("AGI", 0, 10);
		fields[14] = createField(1, 10, 4);
		addLabel("HIT", 3, 10);
		fields[15] = createField(4, 10, 4);
		addLabel("CRI", 0, 11);
		fields[16] = createField(1, 11, 4);
		addLabel("HST", 3, 11);
		fields[17] = createField(4, 11, 4);
		addLabel("EXP", 0, 12);
		fields[18] = createField(1, 12, 4);
		addLabel("ARP", 3, 12);
		fields[19] = createField(4, 12, 4);
		
		JLabel label;
		c.insets = new Insets(10,0,0,0);
		
		c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
		label = new JLabel("Gear Stats");
		label.setHorizontalAlignment(JLabel.CENTER);
		add(label, c);
		
		c.gridx = 3; c.gridy = 1; c.gridwidth = 2;
		label = new JLabel("Melee");
		label.setHorizontalAlignment(JLabel.CENTER);
		add(label, c);
		
		c.insets = new Insets(4,0,0,0);
		c.gridx = 3; c.gridy = 6; c.gridwidth = 2;
		label = new JLabel("Spell");
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.BOTTOM);
		add(label, c);
		
		c.gridx = 2; c.gridy = 1; c.gridheight = 4;
		JSeparator sep = new JSeparator();
		sep.setOrientation(SwingConstants.VERTICAL);
		add(sep, c);
		
		c.insets = new Insets(10,0,0,0);
		c.gridx = 0; c.gridy = 9; c.gridheight = 1; c.gridwidth = 5;
		label = new JLabel("EP Values");
		label.setHorizontalAlignment(JLabel.CENTER);
		add(label, c);
	}
	

	
	private JTextField createField(int gridx, int gridy, int cols) {
		JTextField field;
		field = new JTextField();
		field.setEditable(false);
		field.setColumns(cols);
		field.setHorizontalAlignment(JTextField.RIGHT);
		Font f = field.getFont().deriveFont(12F);
		field.setFont(f);
		c.gridx = gridx; c.gridy = gridy; c.insets = new Insets(0,1,0,10);
		add(field, c);
		return field;
	}
	
	private JLabel addLabel(String str, int gridx, int gridy) {
		JLabel label = new JLabel(str);
		Font f = label.getFont().deriveFont(12F);
		label.setFont(f);
		label.setHorizontalAlignment(JLabel.RIGHT);
		c.gridx = gridx; c.gridy = gridy; c.insets = new Insets(1,1,1,5);
		add(label, c);
		return label;
	}
	
	public void showStats() {
		Calculations calcs = Calculations.createInstance();
		calcs.calculate(mainFrame.getSetup());
		Modifiers mod = calcs.getModifiers();
		if (mod == null)
			return;
		
		fields[0].setText(String.format("%.0f", calcs.getTotalDPS()));
		Attributes attr = mainFrame.getSetup().getAttributes();
		float atp = attr.getAtp();
		atp += attr.getAgi() + attr.getStr();
		fields[1].setText(String.format("%.0f", attr.getAgi()));
		fields[2].setText(String.format("%.0f", atp));
		fields[3].setText(String.format("%.0f", attr.getHit()));
		fields[4].setText(String.format("%.0f", attr.getCri()));
		fields[5].setText(String.format("%.0f", attr.getHst()));
		fields[6].setText(String.format("%.0f", attr.getArp()));
		if (mod.getArpExceeded()==2)
			fields[6].setForeground(Color.RED);
		else if (mod.getArpExceeded()==1)
			fields[6].setForeground(new Color(255,150,0));
		else
			fields[6].setForeground(Color.BLACK);
		fields[7].setText(String.format("%.0f", attr.getExp()));
		fields[8].setText(String.format("%.2f", mod.getHtMH().getMiss()*100));
		if (mod.getHtMH().getHitExceeded()==2)
			fields[8].setForeground(Color.RED);
		else
			fields[8].setForeground(Color.BLACK);
		fields[9].setText(String.format("%.2f", mod.getHtMH().getDodge()*100));
		fields[10].setText(String.format("%.2f", mod.getHtMH().getCrit0()*100));
		if (mod.getHtMH().getCritExceeded()==2) {
			fields[10].setForeground(Color.RED);
			fields[10].setToolTipText(String.format("Cap permanently exceeded by %.2f%%", mod.getHtMH().getCritPermOverCap()*100));
		} else if (mod.getHtMH().getCritExceeded()==1) {
			fields[10].setForeground(new Color(255,150,0));
			fields[10].setToolTipText(String.format("Cap temporarily exceeded by %.2f%%", mod.getHtMH().getCritProcOverCap()*100));
		} else {
			fields[10].setForeground(Color.BLACK);
			fields[10].setToolTipText(null);
		}
		fields[11].setText(String.format("%.2f", mod.getHtMH().getCritCap()*100));
		fields[12].setText(String.format("%.2f", mod.getSpellMissPercent()));
		fields[13].setText(String.format("%.2f", mod.getSpellCritPercent()));
		
		calcs.calcEP();
		fields[14].setText(String.format("%.2f", calcs.getEpAGI()));
		fields[15].setText(String.format("%.2f", calcs.getEpHIT()));
		fields[16].setText(String.format("%.2f", calcs.getEpCRI()));
		fields[17].setText(String.format("%.2f", calcs.getEpHST()));
		fields[18].setText(String.format("%.2f", calcs.getEpEXP()));
		fields[19].setText(String.format("%.2f", calcs.getEpARP()));
	}

}
