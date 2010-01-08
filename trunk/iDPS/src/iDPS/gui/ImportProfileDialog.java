package iDPS.gui;

import iDPS.*;
import iDPS.gear.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;
import javax.swing.*;

final class ImportProfileDialog extends JDialog implements ActionListener {
    
    private JComboBox mRegions;
    private JLabel mRegionsLabel;
    
    private JComboBox mRealms;
    private JLabel mRealmsLabel;
    
    private JTextField mCharacterName;
    private JLabel mCharacterNameLabel;
    
    private JButton mImportButton;
    
    ImportProfileDialog() {
        super(MainFrame.getInstance(), "Import Profile", true);
        
        GridLayout layout = new GridLayout(0, 2);
        setLayout(layout);
        
        mRegionsLabel = new JLabel("Region:");
        add(mRegionsLabel);
        
        String regions[] = { "EU", "US", "CN", "KR", "TW" };
        mRegions = new JComboBox(regions);
        mRegions.addActionListener(this);
        add(mRegions);
        
        mRealmsLabel = new JLabel("Realm:");
        add(mRealmsLabel);
        
        mRealms = new JComboBox();
        mRealms.addActionListener(this);
        add(mRealms);
        
        mCharacterNameLabel = new JLabel("Character:");
        add(mCharacterNameLabel);
        
        mCharacterName = new JTextField();
        add(mCharacterName);
        
        mImportButton = new JButton("Import");
        mImportButton.addActionListener(this);
        add(mImportButton);
        
        updateRealms();
        
        pack();
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
                "Азурегос", "Вечная Песня", "Гордунни", "Гром", "Дракономор", "Король-лич", "Пиратская бухта", "Подземье", "Разувий", "Свежеватель Душ", "Седогрив", "Страж смерти", "Термоштепсель", "Ткач Смерти", "Ясеневый лес", "Aegwynn", "Aerie Peak", "Agamaggan", "Aggramar", "Ahn'Qiraj", "Al'Akir", "Alexstrasza", "Alleria", "Alonsus", "Aman'Thul", "Ambossar", "Anachronos", "Anetheron", "Antonidas", "Anub'arak", "Arak-arahm", "Arathi", "Arathor", "Archimonde", "Area 52", "Arena Tournament 1", "Arena Tournament 2", "Arena Tournament 3", "Arena Tournament 4", "Arena Tournament 5", "Arena Tournament 6", "Argent Dawn", "Arthas", "Arygos", "Aszune", "Auchindoun", "Azjol-Nerub", "Azshara", "Azuremyst", "Baelgun", "Balnazzar", "Blackhand", "Blackmoore", "Blackrock", "Blade's Edge", "Bladefist", "Bloodfeather", "Bloodhoof", "Bloodscalp", "Blutkessel", "Boulderfist", "Bronze Dragonflight", "Bronzebeard", "Burning Blade", "Burning Legion", "Burning Steppes", "C'Thun", "Chamber of Aspects", "Chants éternels", "Cho'gall", "Chromaggus", "Colinas Pardas", "Confrérie du Thorium", "Conseil des Ombres", "Crushridge", "Culte de la Rive noire", "Daggerspine", "Dalaran", "Dalvengyr", "Darkmoon Faire", "Darksorrow", "Darkspear", "Das Konsortium", "Das Syndikat", "Deathwing", "Defias Brotherhood", "Dentarg", "Der abyssische Rat", "Der Mithrilorden", "Der Rat von Dalaran", "Destromath", "Dethecus", "Die Aldor", "Die Arguswacht", "Die ewige Wacht", "Die Nachtwache", "Die Silberne Hand", "Die Todeskrallen", "Doomhammer", "Draenor", "Dragonblight", "Dragonmaw", "Drak'thul", "Drek'Thar", "Dun Modr", "Dun Morogh", "Dunemaul", "Durotan", "Earthen Ring", "Echsenkessel", "Eitrigg", "Eldre'Thalas", "Elune", "Emerald Dream", "Emeriss", "Eonar", "Eredar", "Executus", "Exodar", "Férocité", "Festung der Stürme", "Forscherliga", "Frostmane", "Frostmourne", "Frostwhisper", "Frostwolf", "Garona", "Garrosh", "Genjuros", "Ghostlands", "Gilneas", "Gorgonnash", "Grim Batol", "Gul'dan", "Hakkar", "Haomarush", "Hellfire", "Hellscream", "Hyjal", "Illidan", "Jaedenar", "Kael'thas", "Karazhan", "Kargath", "Kazzak", "Kel'Thuzad", "Khadgar", "Khaz Modan", "Khaz'goroth", "Kil'jaeden", "Kilrogg", "Kirin Tor", "Kor'gall", "Krag'jin", "Krasus", "Kul Tiras", "Kult der Verdammten", "La Croisade écarlate", "Laughing Skull", "Les Clairvoyants", "Les Sentinelles", "Lightbringer", "Lightning's Blade", "Lordaeron", "Los Errantes", "Lothar", "Madmortem", "Magtheridon", "Mal'Ganis", "Malfurion", "Malorne", "Malygos", "Mannoroth", "Marécage de Zangar", "Mazrigos", "Medivh", "Minahonda", "Molten Core", "Moonglade", "Mug'thol", "Némésis", "Nagrand", "Nathrezim", "Naxxramas", "Nazjatar", "Nefarian", "Neptulon", "Ner'zhul", "Nera'thor", "Nethersturm", "Nordrassil", "Norgannon", "Nozdormu", "Onyxia", "Outland", "Perenolde", "Proudmoore", "Quel'Thalas", "Ragnaros", "Rajaxx", "Rashgarroth", "Ravencrest", "Ravenholdt", "Représailles", "Rexxar", "Runetotem", "Sanguino", "Sargeras", "Saurfang", "Scarshield Legion", "Sen'jin", "Shadowmoon", "Shadowsong", "Shattered Halls", "Shattered Hand", "Shattrath", "Shen'dralar", "Silvermoon", "Sinstralis", "Skullcrusher", "Spinebreaker", "Sporeggar", "Steamwheedle Cartel", "Stonemaul", "Stormrage", "Stormreaver", "Stormscale", "Sunstrider", "Suramar", "Sylvanas", "Taerar", "Talnivarr", "Tarren Mill", "Teldrassil", "Temple noir", "Terenas", "Terokkar", "Terrordar", "The Maelstrom", "The Sha'tar", "The Venture Co", "Theradras", "Thrall", "Throk'Feroth", "Thunderhorn", "Tichondrius", "Tirion", "Todeswache", "Trollbane", "Turalyon", "Twilight's Hammer", "Twisting Nether", "Tyrande", "Uldaman", "Ulduar", "Uldum", "Un'Goro", "Varimathras", "Vashj", "Vek'lor", "Vek'nilash", "Vol'jin", "Warsong", "Wildhammer", "Wrathbringer", "Xavius", "Ysera", "Ysondre", "Zenedar", "Zirkel des Cenarius", "Zul'jin", "Zuluhed"
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
    }
    
    private void importCharacter() {
        Player player = Player.getInstance();
        
        String region = (String)mRegions.getSelectedItem();
        String realm = (String)mRealms.getSelectedItem();
        String character = mCharacterName.getText();
        
        try {
            String urlString = "http://";
            urlString += region.toLowerCase();
            urlString += ".wowarmory.com/character-sheet.xml?r=";
            urlString += URLEncoder.encode(realm.toLowerCase(), "utf-8");
            urlString += "&n=";
            urlString += URLEncoder.encode(character.toLowerCase(), "utf-8");
            
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(reader);
            Element root = document.getRootElement();
            Element characterInfo = root.getChild("characterInfo");
            Element characterTab = characterInfo.getChild("characterTab");
            Element items = characterTab.getChild("items");

            int chackifyBlizzardItemSlotId[] = { 0, 1, 2, 5, 4, 9, 10, 11, 7, 8, 12, 13, 14, 15, 3, 16, 17, 18, 6 };
            
            ListIterator<Element> elementIter = items.getChildren().listIterator();
            while (elementIter.hasNext()) {
                Element element = elementIter.next();
                int id = Integer.parseInt(element.getAttributeValue("id"));
                int rawSlot = Integer.parseInt(element.getAttributeValue("slot"));
                int slot = chackifyBlizzardItemSlotId[rawSlot];
                Vector<Integer> gemIds = new Vector<Integer>(3);
                gemIds.add(Integer.parseInt(element.getAttributeValue("gem0Id")));
                gemIds.add(Integer.parseInt(element.getAttributeValue("gem1Id")));
                gemIds.add(Integer.parseInt(element.getAttributeValue("gem2Id")));
                int enchantId = Integer.parseInt(element.getAttributeValue("permanentenchant"));
                
                Item item = Item.find(id);
                if (item != null) {
                    player.getEquipped().setItem(slot, item);
                    
                    Enchant enchant = Enchant.find(enchantId);
                    if (enchant != null) {
                        player.getEquipped().setEnchant(slot, enchant);
                    }
                    
                    ListIterator<Integer> gemIter = gemIds.listIterator();
                    int socketIndex = 0;
                    while (gemIter.hasNext()) {
                        Gem gem = Gem.find(gemIter.next());
                        if (gem != null) {
                            iDPS.gear.Socket socket = item.getSocket(socketIndex);
                            if (socket != null) {
                                player.getEquipped().setGem(slot, socketIndex, gem);
                            }
                        }
                        socketIndex++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.getInstance(), "HELP ME! OH GOD, THE BEARS, THEY ARE VISCIOUS! THEY ARE RAPING THE ARMORY!"); 
            return;
        }

        MainFrame.getInstance().showGear(player.getEquipped());
        dispose();
    }
    
}

