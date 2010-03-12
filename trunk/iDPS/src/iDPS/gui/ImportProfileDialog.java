package iDPS.gui;

import iDPS.Persistency;
import iDPS.Talents;
import iDPS.armory.ArmoryInfo;
import iDPS.armory.ItemFinder;
import iDPS.armory.Realm;
import iDPS.armory.TalentReader;
import iDPS.armory.Realm.Region;
import iDPS.gear.Armor;
import iDPS.gear.Enchant;
import iDPS.gear.Gem;
import iDPS.gear.Setup;
import iDPS.gear.Weapon;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

final class ImportProfileDialog extends JDialog implements ActionListener {
    
	private MainFrame mainFrame;
	
    private JComboBox mRegions;
    private JComboBox mRealms;
    private JTextField mCharacterName;
    
    private JButton mImportButton;
    
    ImportProfileDialog(MainFrame mainFrame) {
        super(mainFrame, "Import Profile", true);
        this.mainFrame = mainFrame;
        
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
        
        updateRealms();
        pack();
        
		// put it in the center
		int x = mainFrame.getLocation().x + mainFrame.getSize().width/2 - getSize().width/2;
		int y = mainFrame.getLocation().y + mainFrame.getSize().height/2 - getSize().height/2;
		setLocation(new Point(x,y));
		
		load();
    }
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == mRegions) {
            updateRealms();
        } else if (event.getSource() == mImportButton) {
            importCharacter();
        }
    }
    
    private void updateRealms() {
        mRealms.removeAllItems();
        Region region = Region.valueOf((String) mRegions.getSelectedItem());
        List<Realm> realms = Realm.getRealms(region);
        for (Realm r: realms)
        	mRealms.addItem(r.getName());
    }
    
    @SuppressWarnings("unchecked")
	private void importCharacter() {
        String region = (String)mRegions.getSelectedItem();
        String realm = (String)mRealms.getSelectedItem();
        String character = mCharacterName.getText();
        
        Setup gear = mainFrame.getApp().getSetup().clone();
        gear.reset();
        
        try {
            String urlString = "http://";
            urlString += region.toLowerCase();
            urlString += ".wowarmory.com/character-sheet.xml?r=";
            urlString += URLEncoder.encode(realm.toLowerCase(), "utf-8");
            urlString += "&n=";
            urlString += URLEncoder.encode(character.toLowerCase(), "utf-8");
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1");
            conn.setRequestProperty("Cookie", "loginChecked=1");
            conn.connect();
            System.out.println(conn.getResponseMessage()); 
            //BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(conn.getInputStream());
            Element root = document.getRootElement();
            Element characterInfo = root.getChild("characterInfo");
            if (characterInfo.getChild("character").getAttributeValue("class").compareTo("Rogue") != 0) {
                throw new Exception("Dear sir, I don't believe you are a Rogue at all!\r\n\"There's an old saying in Tennessee — I know it's in Texas, probably in Tennessee — that says, fool me once, shame on — shame on you. Fool me — you can't get fooled again.\"");
            }
            
            Element talents = new TalentReader(new ArmoryInfo(region, realm, character), 1).read();
            gear.setTalents(new Talents(talents));
            
            Element characterTab = characterInfo.getChild("characterTab");
            Element items = characterTab.getChild("items");

            int chackifyBlizzardItemSlotId[] = { 0, 1, 2, 5, 4, 9, 10, 11, 7, 8, 12, 13, 14, 15, 3, 16, 17, 18, 6 };
            
            ListIterator<Element> elementIter = items.getChildren().listIterator();
            List<Element> newItemList = new ArrayList<Element>();
            while (elementIter.hasNext()) {
                Element element = elementIter.next();
                int id = Integer.parseInt(element.getAttributeValue("id"));
                int rawSlot = Integer.parseInt(element.getAttributeValue("slot"));
                if (rawSlot < 0 || rawSlot >= chackifyBlizzardItemSlotId.length) {
                    break;
                }
                int slot = chackifyBlizzardItemSlotId[rawSlot];
                Vector<Integer> gemIds = new Vector<Integer>(3);
                gemIds.add(Integer.parseInt(element.getAttributeValue("gem0Id")));
                gemIds.add(Integer.parseInt(element.getAttributeValue("gem1Id")));
                gemIds.add(Integer.parseInt(element.getAttributeValue("gem2Id")));
                int enchantId = Integer.parseInt(element.getAttributeValue("permanentenchant"));
                
                Armor item = Armor.find(id);

                if (item == null) {
                	//System.out.println("ID nao encontrado: " + id);
                	Element newItem = new ItemFinder(id, rawSlot, slot, new ArmoryInfo(region, realm, character)).find();
                	if (newItem == null)
                		continue;
                	
                	newItemList.add(newItem);
                	String newItemSlot = newItem.getChildText("slot");
                	if (newItemSlot != null && (newItemSlot.equals("MainHand") || newItemSlot.equals("OneHand") || newItemSlot.equals("OffHand")))
        				item = new Weapon(newItem);
        			else
        				item = new Armor(newItem);
                	
                	Armor.add(item);
                }

                if (item != null) {
                    gear.setItem(slot, item);
                    
                    Enchant enchant = Enchant.find(enchantId);
                    if (enchant != null) {
                        gear.setEnchant(slot, enchant);
                    }
                    
                    ListIterator<Integer> gemIter = gemIds.listIterator();
                    int socketIndex = 0;
                    while (gemIter.hasNext()) {
                        Gem gem = Gem.find(gemIter.next());
                        if (gem != null) {
                            gear.setGem(slot, socketIndex, gem);
                        }
                        socketIndex++;
                    }
                }
            }
            
            //save unrecognized items to settings XML
            if (!newItemList.isEmpty()) {
    			Document doc = Persistency.openXML(Persistency.FileType.Settings);
    			Element settingItems = doc.getRootElement().getChild("items");
    			if (settingItems == null) {
    				settingItems = new Element("items");
    				doc.getRootElement().getChildren().add(settingItems);
    			}

	            for (int i = 0; i < newItemList.size(); i++) {
	            	settingItems.getChildren().add(newItemList.get(i));
				}
				
				Persistency.saveXML(doc, Persistency.FileType.Settings);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Looks like there was a minor setback when importing the character:\r\n"+e.getLocalizedMessage(), "Import failed", JOptionPane.ERROR_MESSAGE); 
            return;
        }
        mainFrame.getApp().setSetup(gear);
        mainFrame.showGear();
        Setup.add(gear);
        mainFrame.getMyMenuBar().createGearMenu();
        setVisible(false);
        save();
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
					String tmp = (String) mRealms.getModel().getElementAt(i);
					if (tmp.equals(e.getText())) {
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

