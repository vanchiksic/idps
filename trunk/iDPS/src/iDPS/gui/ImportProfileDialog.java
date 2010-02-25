package iDPS.gui;

import iDPS.Persistency;
import iDPS.gear.Armor;
import iDPS.gear.Enchant;
import iDPS.gear.Gem;
import iDPS.gear.Setup;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
        
        String regions[] = { "EU", "US", "CN", "KR", "TW" };
        mRegions = new JComboBox(regions);
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
        String region = (String)mRegions.getSelectedItem();
        if (region == "EU") {
            String realms[] = {
                "Азурегос", "Вечная Песня", "Галакронд", "Гордунни", "Гром", "Дракономор", "Король-лич", "Пиратская бухта", "Подземье", "Разувий", "Свежеватель Душ", "Седогрив", "Страж смерти", "Термоштепсель", "Ткач Смерти", "Ясеневый лес", "Aegwynn", "Aerie Peak", "Agamaggan", "Aggramar", "Ahn'Qiraj", "Al'Akir", "Alexstrasza", "Alleria", "Alonsus", "Aman'Thul", "Ambossar", "Anachronos", "Anetheron", "Antonidas", "Anub'arak", "Arak-arahm", "Arathi", "Arathor", "Archimonde", "Area 52", "Arena Tournament 1", "Arena Tournament 2", "Arena Tournament 3", "Arena Tournament 4", "Arena Tournament 5", "Arena Tournament 6", "Argent Dawn", "Arthas", "Arygos", "Aszune", "Auchindoun", "Azjol-Nerub", "Azshara", "Azuremyst", "Baelgun", "Balnazzar", "Blackhand", "Blackmoore", "Blackrock", "Blade's Edge", "Bladefist", "Bloodfeather", "Bloodhoof", "Bloodscalp", "Blutkessel", "Boulderfist", "Bronze Dragonflight", "Bronzebeard", "Burning Blade", "Burning Legion", "Burning Steppes", "C'Thun", "Chamber of Aspects", "Chants éternels", "Cho'gall", "Chromaggus", "Colinas Pardas", "Confrérie du Thorium", "Conseil des Ombres", "Crushridge", "Culte de la Rive noire", "Daggerspine", "Dalaran", "Dalvengyr", "Darkmoon Faire", "Darksorrow", "Darkspear", "Das Konsortium", "Das Syndikat", "Deathwing", "Defias Brotherhood", "Dentarg", "Der abyssische Rat", "Der Mithrilorden", "Der Rat von Dalaran", "Destromath", "Dethecus", "Die Aldor", "Die Arguswacht", "Die ewige Wacht", "Die Nachtwache", "Die Silberne Hand", "Die Todeskrallen", "Doomhammer", "Draenor", "Dragonblight", "Dragonmaw", "Drak'thul", "Drek'Thar", "Dun Modr", "Dun Morogh", "Dunemaul", "Durotan", "Earthen Ring", "Echsenkessel", "Eitrigg", "Eldre'Thalas", "Elune", "Emerald Dream", "Emeriss", "Eonar", "Eredar", "Executus", "Exodar", "Férocité", "Festung der Stürme", "Forscherliga", "Frostmane", "Frostmourne", "Frostwhisper", "Frostwolf", "Garona", "Garrosh", "Genjuros", "Ghostlands", "Gilneas", "Gorgonnash", "Grim Batol", "Gul'dan", "Hakkar", "Haomarush", "Hellfire", "Hellscream", "Hyjal", "Illidan", "Jaedenar", "Kael'thas", "Karazhan", "Kargath", "Kazzak", "Kel'Thuzad", "Khadgar", "Khaz Modan", "Khaz'goroth", "Kil'jaeden", "Kilrogg", "Kirin Tor", "Kor'gall", "Krag'jin", "Krasus", "Kul Tiras", "Kult der Verdammten", "La Croisade écarlate", "Laughing Skull", "Les Clairvoyants", "Les Sentinelles", "Lightbringer", "Lightning's Blade", "Lordaeron", "Los Errantes", "Lothar", "Madmortem", "Magtheridon", "Mal'Ganis", "Malfurion", "Malorne", "Malygos", "Mannoroth", "Marécage de Zangar", "Mazrigos", "Medivh", "Minahonda", "Molten Core", "Moonglade", "Mug'thol", "Némésis", "Nagrand", "Nathrezim", "Naxxramas", "Nazjatar", "Nefarian", "Neptulon", "Ner'zhul", "Nera'thor", "Nethersturm", "Nordrassil", "Norgannon", "Nozdormu", "Onyxia", "Outland", "Perenolde", "Proudmoore", "Quel'Thalas", "Ragnaros", "Rajaxx", "Rashgarroth", "Ravencrest", "Ravenholdt", "Représailles", "Rexxar", "Runetotem", "Sanguino", "Sargeras", "Saurfang", "Scarshield Legion", "Sen'jin", "Shadowmoon", "Shadowsong", "Shattered Halls", "Shattered Hand", "Shattrath", "Shen'dralar", "Silvermoon", "Sinstralis", "Skullcrusher", "Spinebreaker", "Sporeggar", "Steamwheedle Cartel", "Stonemaul", "Stormrage", "Stormreaver", "Stormscale", "Sunstrider", "Suramar", "Sylvanas", "Taerar", "Talnivarr", "Tarren Mill", "Teldrassil", "Temple noir", "Terenas", "Terokkar", "Terrordar", "The Maelstrom", "The Sha'tar", "The Venture Co", "Theradras", "Thrall", "Throk'Feroth", "Thunderhorn", "Tichondrius", "Tirion", "Todeswache", "Trollbane", "Turalyon", "Twilight's Hammer", "Twisting Nether", "Tyrande", "Uldaman", "Ulduar", "Uldum", "Un'Goro", "Varimathras", "Vashj", "Vek'lor", "Vek'nilash", "Vol'jin", "Warsong", "Wildhammer", "Wrathbringer", "Xavius", "Ysera", "Ysondre", "Zenedar", "Zirkel des Cenarius", "Zul'jin", "Zuluhed"
            };
            for (String realm : realms) {
                mRealms.addItem(realm);
            }
        }
        else if (region == "US") {
            String realms[] = {
                "Aegwynn", "Aerie Peak", "Agamaggan", "Aggramar", "Akama", "Alexstrasza", "Alleria", "Altar of Storms", "Alterac Mountains", "Aman'Thul", "Andorhal", "Anetheron", "Antonidas", "Anub'arak", "Anvilmar", "Arathor", "Archimonde", "Area 52", "Arena Tournament 1", "Arena Tournament 2", "Arena Tournament 3", "Arena Tournament 4", "Argent Dawn", "Arthas", "Arygos", "Auchindoun", "Azgalor", "Azjol-Nerub", "Azshara", "Azuremyst", "Baelgun", "Balnazzar", "Barthilas", "Black Dragonflight", "Blackhand", "Blackrock", "Blackwater Raiders", "Blackwing Lair", "Blade's Edge", "Bladefist", "Bleeding Hollow", "Blood Furnace", "Bloodhoof", "Bloodscalp", "Bonechewer", "Borean Tundra", "Boulderfist", "Bronzebeard", "Burning Blade", "Burning Legion", "Caelestrasz", "Cairne", "Cenarion Circle", "Cenarius", "Cho'gall", "Chromaggus", "Coilfang", "Crushridge", "Daggerspine", "Dalaran", "Dalvengyr", "Dark Iron", "Darkspear", "Darrowmere", "Dath'Remar", "Dawnbringer", "Deathwing", "Demon Soul", "Dentarg", "Destromath", "Dethecus", "Detheroc", "Doomhammer", "Draenor", "Dragonblight", "Dragonmaw", "Drak'Tharon", "Drak'thul", "Draka", "Drakkari", "Dreadmaul", "Drenden", "Dunemaul", "Durotan", "Duskwood", "Earthen Ring", "Echo Isles", "Eitrigg", "Eldre'Thalas", "Elune", "Emerald Dream", "Eonar", "Eredar", "Executus", "Exodar", "Farstriders", "Feathermoon", "Fenris", "Firetree", "Fizzcrank", "Frostmane", "Frostmourne", "Frostwolf", "Galakrond", "Garithos", "Garona", "Garrosh", "Ghostlands", "Gilneas", "Gnomeregan", "Gorefiend", "Gorgonnash", "Greymane", "Grizzly Hills", "Gul'dan", "Gundrak", "Gurubashi", "Hakkar", "Haomarush", "Hellscream", "Hydraxis", "Hyjal", "Icecrown", "Illidan", "Jaedenar", "Jubei'Thos", "Kael'thas", "Kalecgos", "Kargath", "Kel'Thuzad", "Khadgar", "Khaz Modan", "Khaz'goroth", "Kil'jaeden", "Kilrogg", "Kirin Tor", "Korgath", "Korialstrasz", "Kul Tiras", "Laughing Skull", "Lethon", "Lightbringer", "Lightning's Blade", "Lightninghoof", "Llane", "Lothar", "Madoran", "Maelstrom", "Magtheridon", "Maiev", "Mal'Ganis", "Malfurion", "Malorne", "Malygos", "Mannoroth", "Medivh", "Misha", "Mok'Nathal", "Moon Guard", "Moonrunner", "Mug'thol", "Muradin", "Nagrand", "Nathrezim", "Nazgrel", "Nazjatar", "Ner'zhul", "Nesingwary", "Nordrassil", "Norgannon", "Onyxia", "Perenolde", "Proudmoore", "Quel'dorei", "Quel'Thalas", "Ragnaros", "Ravencrest", "Ravenholdt", "Rexxar", "Rivendare", "Runetotem", "Sargeras", "Saurfang", "Scarlet Crusade", "Scilla", "Sen'jin", "Sentinels", "Shadow Council", "Shadowmoon", "Shadowsong", "Shandris", "Shattered Halls", "Shattered Hand", "Shu'halo", "Silver Hand", "Silvermoon", "Sisters of Elune", "Skullcrusher", "Skywall", "Smolderthorn", "Spinebreaker", "Spirestone", "Staghelm", "Steamwheedle Cartel", "Stonemaul", "Stormrage", "Stormreaver", "Stormscale", "Suramar", "Tanaris", "Terenas", "Terokkar", "Thaurissan", "The Forgotten Coast", "The Scryers", "The Underbog", "The Venture Co", "Thorium Brotherhood", "Thrall", "Thunderhorn", "Thunderlord", "Tichondrius", "Tortheldrin", "Trollbane", "Turalyon", "Twisting Nether", "Uldaman", "Uldum", "Undermine", "Ursin", "Uther", "Vashj", "Vek'nilash", "Velen", "Warsong", "Whisperwind", "Wildhammer", "Windrunner", "Winterhoof", "Wyrmrest Accord", "Ysera", "Ysondre", "Zangarmarsh", "Zul'jin", "Zuluhed"
            };
            for (String realm : realms) {
                mRealms.addItem(realm);
            }
        }
        else if (region == "CN") {
            String realms[] = {
                "Abyssal Maw", "Aerie Peak", "Aessina", "Agamaggan", "Aggramar", "Ahn'Qiraj", "Akama", "Al'Akir", "Al'ar", "Alexstrasza", "Alleria", "Alonsus", "Altar of Storms", "Alterac Mountains", "Anachronos", "Andorhal", "Anetheron", "Antonidas", "Anub'arak", "Anvilmar", "Arathi", "Arathor", "Archaedas", "Archimonde", "Argus", "Arthas", "Arygos", "Ashenvale", "Astranaar", "Aszune", "Aviana", "Azgalor", "Azjol-Nerub", "Azshara", "Azuregos", "Baelgun", "Balnazzar", "Barthilas", "Black Dragonflight", "Blackhand", "Blackmoore", "Blackrock", "Blackwing Lair", "Bladefist", "Blanchard", "Bleeding Hollow", "Blood Furnace", "Bloodfeather", "Bloodhoof", "Bloodmaul", "Bloodscalp", "Blue Dragonflight", "Booty Bay", "Boulderfist", "Brann", "Bronze Dragonflight", "Bronzebeard", "Burning Blade", "Burning Legion", "Burning Steppes", "C'Thun", "Cassandra", "Cenarius", "Chillwind Point", "Cho'gall", "Chromaggus", "Claw of the Shadowmancer", "Crushridge", "Crystalpine Stinger", "Daggerspine", "Dalvengyr", "Danath Trollbane", "Dark Iron", "Dark Phantom", "Dark Portal", "Darkspear", "Darrowmere", "Dath'Remar", "Death's Door", "Deathforge", "Deathwing", "Deepfury", "Deephome", "Demon Fall Canyon", "Demon Soul", "Demonslayer", "Dentarg", "Destromath", "Detheroc", "Direwing", "Doomhammer", "Dor'Danil", "Draenor", "Dragonblight", "Dragonmaw", "Drakkari", "Dreadmaul", "Dreadmist Peak", "Dream Bough", "Dreamwalker", "Dun Modr", "Dunemaul", "Durotan", "Duskwood", "Dustbelcher", "Dustwind Gulch", "Echo Isles", "Echo Ridge", "Eldre'Thalas", "Emerald Dream", "Emeriss", "Eonar", "Eranikus", "Eredar", "Executus", "Explorer's League", "Falathim", "Feathermoon", "Fel Rock", "Fenris", "Feralas", "Fire Plume Ridge", "Firegut", "Firelands", "Firetree", "Fist of the Titans", "Flame Crest", "Force of Elemental", "Fray Island", "Freewind", "Frostmane", "Frostmourne", "Frostreaver Crown", "Frostwhisper", "Frostwolf", "Gadgetzan", "Galardell", "Garithos", "Garona", "Gazlowe", "Gnomeregan", "Gold Road", "Gordunni", "Gorefiend", "Gorgonnash", "Greymane", "Grim Batol", "Grimtotem", "Guardian Blade", "Gul'dan", "Gurubashi", "Hakkar", "Hectae", "Hellscream", "Holy Chanter", "Hydraxis", "Hyjal", "Icecrown", "Illidan", "Immol'thar", "Ironaya", "Isillien", "Itharius", "Jaedenar", "Jammal'an", "Jin'do", "Kael'thas", "Kaleidoscope Star", "Karazhan", "Kargath", "Kel'Thuzad", "Khadgar", "Khardros", "Khaz'goroth", "Kil'jaeden", "Kilrogg", "Kul Tiras", "Kurdran", "Laughing Skull", "Lethon", "Lich King", "Lightning's Blade", "Lord Kazzak", "Lordaeron", "Lothar", "Lushwater Oasis", "Madoran", "Maelstrom", "Magatha", "Magtheridon", "Maiev Shadowsong", "Mal'Ganis", "Malfurion", "Malygos", "Mannoroth", "Medivh", "Menethil", "Midnight Scythe", "Modgud", "Mograine", "Molten Core", "Moonglade", "Mosh'Ogg", "Mossflayer", "Mug'thol", "Murmur", "Naxxramas", "Nazjatar", "Nefarian", "Nekros", "Neltharion", "Neptulon", "Ner'zhul", "Nighthaven", "Northrend", "Nozdormu", "Onyxia", "Ossirian", "Outland", "Pandaren", "Perenolde", "Poison-tipped Bone Spear", "Prestor", "Proudmoore", "Quel'Thalas", "Ragnaros", "Rajaxx", "Rangers", "Ravencrest", "Ravenholdt", "Razorwind Canyon", "Red Cloud Mesa", "Red Dragonflight", "Rend", "Rexxar", "Rhonin", "Ring of Trials", "Rivendare", "River Pride", "Runetotem", "Sapphiron", "Sargeras", "Scarlet Crusade", "Scholomance", "Searinox", "Sen'jin", "Sethekk", "Shadow Council", "Shadowfang Keep", "Shadowmoon", "Shattered Halls", "Shattered Hand", "Shrine of the Dormant Flame", "Silver Hand", "Silvermoon", "Silverpine Forest", "Skullcrusher", "Skywall", "Smolderthorn", "Soulflayer", "Spirestone", "Splinter Fist", "Staghelm", "Stonetalon Peak", "Storm Eye", "Stormrage", "Stormreaver", "Stormscale", "Stranglethorn", "Stratholme", "Stromgarde Keep", "Sun Rock Retreat", "Sundown Marsh", "Sunstrider", "Sutarn", "Swiftwind", "Sylvanas", "Taerar", "Tanaris", "Tarren Mill", "Temple of Elune", "Thaurissan", "The Forgotten Coast", "The Golden Plains", "The Great Sea", "The Master's Glaive", "The Underbog", "The Veiled Sea", "Theradras", "Theramore", "Therazane", "Thermaplugg", "Thoradin", "Thousand Needles", "Thrall", "Thunder Axe Fortress", "Thunder Bluff", "Thunderaan", "Thunderhorn", "Thunderlord", "Tirisfal Glades", "Tortheldrin", "Turalyon", "Twisting Nether", "Tyr's Hand", "Tyrande", "Uldaman", "Uldum", "Un'Goro", "Vaelastrasz", "Valley of Kings", "VanCleef", "Varimathras", "Vek'lor", "Vek'nilash", "Vilebranch", "Vol'jin", "Warsong", "Well of Eternity", "Whispering Shore", "Whisperwind", "Whitemane", "Windrunner", "Windshear Crag", "Wing of the Whelping", "Winterspring", "World Tree", "Xavian", "Ysera", "Ysondre", "Zalazane", "Zealot Blade", "Zul'jin", "Zuluhed"
            };
            for (String realm : realms) {
                mRealms.addItem(realm);
            }
        }
        else if (region == "KR") {
            String realms[] = {
                "아레나 토너먼트 1", "아레나 토너먼트 2", "아레나 토너먼트 3", "Aegwynn", "Al'ar", "Alexstrasza", "Alleria", "Arena Tournament 1", "Arena Tournament 2", "Arena Tournament 3", "Azshara", "Blackmoore", "Burning Legion", "Cenarius", "Dalaran", "Deathwing", "Durotan", "Elune", "Eonar", "Eye of the Storm", "Garona", "Gul'dan", "Hellscream", "Hyjal", "Karazhan", "Kargath", "Kul Tiras", "Llane", "Malfurion", "Malygos", "Medivh", "Norgannon", "Ragnaros", "Rexxar", "Sartharion", "Stormrage", "Tirion", "Uther", "Wildhammer", "Windrunner", "Zul'jin"
            };
            for (String realm : realms) {
                mRealms.addItem(realm);
            }
        }
        else if (region == "TW") {
            String realms[] = {
                "Altar of Storms", "Arena Tournament 1", "Arena Tournament 2", "Arena Tournament 3", "Arthas", "Arygos", "Balnazzar", "Black Dragonflight", "Bleeding Hollow", "Chillwind Point", "Crystalpine Stinger", "Deathwing", "Demon Fall Canyon", "Demon Soul", "Dragonmaw", "Dreadmist Peak", "Frenzyheart", "Frostmane", "Gnomeregan", "Hellscream", "Howling Fjord", "Icecrown", "Kel'Thuzad", "Light's Hope", "Menethil", "Nesingwary", "Nightsong", "Onyxia", "Quel'dorei", "Sartharion", "Shadowmoon", "Silverwing Hold", "Skywall", "Spirestone", "Stormscale", "Strand of the Ancients", "Sundown Marsh", "Warsong", "Whisperwind", "World Tree", "Wrathbringer", "Zealot Blade"
            };
            for (String realm : realms) {
                mRealms.addItem(realm);
            }
        }
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
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1");
            connection.setRequestProperty("Cookie", "loginChecked=1");
            System.out.println(connection.getResponseMessage()); 
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(reader);
            Element root = document.getRootElement();
            Element characterInfo = root.getChild("characterInfo");
            if (characterInfo.getChild("character").getAttributeValue("class").compareTo("Rogue") != 0) {
                throw new Exception("Dear sir, I don't believe you are a Rogue at all!\r\n\"There's an old saying in Tennessee — I know it's in Texas, probably in Tennessee — that says, fool me once, shame on — shame on you. Fool me — you can't get fooled again.\"");
            }
            Element characterTab = characterInfo.getChild("characterTab");
            Element items = characterTab.getChild("items");

            int chackifyBlizzardItemSlotId[] = { 0, 1, 2, 5, 4, 9, 10, 11, 7, 8, 12, 13, 14, 15, 3, 16, 17, 18, 6 };
            
            ListIterator<Element> elementIter = items.getChildren().listIterator();
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

