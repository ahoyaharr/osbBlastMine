import org.osbot.rs07.api.Prayer;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Character;
import org.osbot.rs07.api.model.*;
import org.osbot.rs07.api.ui.*;
import org.osbot.rs07.input.mouse.*;
import org.osbot.rs07.script.Script;

import java.awt.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import static java.awt.AWTEvent.INVOCATION_EVENT_MASK;
import static java.awt.event.MouseEvent.MOUSE_MOVED;
import static org.osbot.rs07.script.MethodProvider.random;

public class aleksandrMethods {

    protected Script m;

    aleksandrMethods() {
    }

    aleksandrMethods(Script m) {
        this.m = m;
    }

    /// Get Operations ///
    public GroundItem getGroundItemAtLoc(Position pos, String name) {
        for (GroundItem a : m.getGroundItems().get(pos.getX(), pos.getY())) {
            if (a.getName().contains(name) && a.getPosition().equals(pos)) {
                return a;
            }
        }
        return null;
    }

    public Item getItem(String s, boolean noted) {
        for (Item o : m.getInventory().getItems()) {
            if (o != null && o.getName().contains(s) && o.isNote() == noted) {
                m.log(o.getName());
                return o;
            }
        }
        m.log("Failed to find: " + s);
        return null;
    }

    public Item getItem(String s) {
        return getItem(s, false);
    }

    public Item getItem(Integer i, boolean noted) {
        for (Item o : m.getInventory().getItems()) {
            if (o != null && o.getId() == i && o.isNote() == noted) {
                return o;
            }
        }
        return null;
    }

    public Item getItem(Integer i) {
        return getItem(i, false);
    }

    public RS2Object getRS2Object(Position objectPosition, int id) {
        for (RS2Object a : m.getObjects().get(objectPosition.getX(), objectPosition.getY())) {
            if (a.getId() == id && a.getPosition().equals(objectPosition)) {
                return a;
            }
        }
        return null;
    }

    public RS2Object getRS2Object(Position objectPosition, String name) {
        for (RS2Object a : m.getObjects().get(objectPosition.getX(), objectPosition.getY())) {
            if (a.getName().equals(name) && a.getPosition().equals(objectPosition)) {
                return a;
            }
        }
        return null;
    }

    public GroundItem getLootableItem(List<String> loot, int distanceMaximum) {
        List<GroundItem> floorItems = m.getGroundItems().getAll();
        for (GroundItem o : floorItems) {
            if (loot.contains(o.getName()) && m.getMap().canReach(o) && m.myPosition().distance(o) < distanceMaximum) {
                return o;
            }
        }
        return null;
    }

    public NPC getBestTarget(String name, int distance) {
        List<NPC> attackables = m.getNpcs().getAll();
        NPC bestTarget = null;
        for (NPC o : attackables) {
            if (o.getName().equals(name) && o.getHealthPercent() > 75 && o.getPosition().distance(m.myPosition()) < distance) {
                if (bestTarget == null) {
                    bestTarget = o;
                } else if (m.myPosition().distance(bestTarget.getPosition()) > m.myPosition().distance(o.getPosition())
                        && o.getHealthPercent() > 75 && o.getPosition().distance(m.myPosition()) < distance) {
                    bestTarget = o;
                }
            }
        }
        return bestTarget;
    } // Combat

    public Character getBestTargetAsCharacter(String name, int distance) {
        List<NPC> attackables = m.getNpcs().getAll();
        NPC bestTarget = null;
        for (NPC o : attackables) {
            if (o.getName().equals(name) && o.getHealthPercent() > 75 && o.getPosition().distance(m.myPosition()) < distance) {
                if (bestTarget == null) {
                    bestTarget = o;
                } else if (m.myPosition().distance(bestTarget.getPosition()) > m.myPosition().distance(o.getPosition())
                        && o.getHealthPercent() > 75 && o.getPosition().distance(m.myPosition()) < distance) {
                    bestTarget = o;
                }
            }
        }
        return bestTarget;
    } // Combat

    public int getSlotOfNotedItem() {
        for (int i = 0; i < 28; i++) {
            if (m.getInventory().getItemInSlot(i) != null && m.getInventory().getItemInSlot(i).isNote()) {
                return i;
            }
        }
        return -1;
    }


    /// Point Operations ///
    public Point getPointOfRectangle(Rectangle bounds) {
        if (bounds != null) {
            Point center = bounds.getLocation();
            center.translate((int) bounds.getWidth() / 2, (int) bounds.getHeight() / 2);
            return center;
        }
        return null;
    }

    public Rectangle getRectangleOfInventoryItem(String s) {
        return InventorySlotDestination.getSlot(m.getInventory().getSlotForNameThatContains(s)).getBounds();
    }

    public Point getPointOfInventoryItem(Item item, boolean includedNotedItem) {
        if (includedNotedItem) {
            return getPointOfRectangle(InventorySlotDestination.getSlot(m.inventory.getSlot(item)).getBounds());
        } else {
            for (Item o : m.getInventory().getItems()) {
                if (o != null && o.getName().equals(item.getName()) && !o.isNote()) {
                    return getPointOfRectangle(InventorySlotDestination.getSlot(m.inventory.getSlot(o)).getBounds());
                }
            }
        }
        return null;
    }

    public Point getPointOfInventoryItem(Item item) {
        return getPointOfInventoryItem(item, false);
    }

    public Rectangle getBankSlotRectangle(Item item) {
        return new BankSlotDestination(m.bot, m.bank.getSlot(item)).getBoundingBox();
    }

    public Point getPointOfBankSlotItem(Item item) {
        return getPointOfRectangle(new BankSlotDestination(m.bot, m.bank.getSlot(item)).getBoundingBox());
    }

    public Point getPointOfInventoryItemContainingName(String s, boolean includeNoted) {
        for (Item o : m.getInventory().getItems()) {
            if (o != null && o.getName().contains(s) && o.isNote() == includeNoted) {
                return getPointOfInventoryItem(o);
            }
        }
        return null;
    }

    public Point getPointOfInventoryItemContainingName(String s) {
        return getPointOfInventoryItemContainingName(s, false);
    }

    public Point getPointOfInventoryItemID(int i) {
        for (Item o : m.getInventory().getItems()) {
            if (o != null) {
                if (o.getId() == i && !o.isNote()) {
                    return getPointOfInventoryItem(o);
                }
            }
        }
        return null;
    }

    public Point getPointOfNextNotedItem() {
        for (Item o : m.getInventory().getItems()) {
            if (o != null && o.isNote()) {
                return getPointOfInventoryItem(o, true);
            }
        }
        return null;
    }

    public Point getPointOfGroundItem(String a, Position p) {
        if (a != null) {
            for (GroundItem o : m.getGroundItems().getAll()) {
                if (a.equals(o.getName()) && p.equals(o.getPosition()) && m.getMap().canReach(o)) {
                    return o.getPoint();
                }
            }
        }
        return null;
    }

    public Point getPointOfRS2Object(RS2Object a) {
        if (a != null) {
            Rectangle bounds = a.getModel().getBoundingBox(a.getGridX(), a.getGridY(), a.getZ());
            Point center = bounds.getLocation();
            center.translate((int) bounds.getWidth() / 2, (int) bounds.getHeight() / 2);
            return center;
        }
        return null;
    }

    public Point getPointOfNPC(NPC a) {
        if (a != null) {
            Rectangle bounds = a.getModel().getBoundingBox(a.getGridX(), a.getGridY(), a.getZ());
            Point center = bounds.getLocation();
            center.translate((int) bounds.getWidth() / 2, (int) bounds.getHeight() / 2);
            return center;
        }
        return null;
    }

    public Point getPointOfCharacter(org.osbot.rs07.api.model.Character a) {
        if (a != null) {
            Rectangle bounds = a.getModel().getBoundingBox(a.getGridX(), a.getGridY(), a.getZ());
            Point center = bounds.getLocation();
            center.translate((int) bounds.getWidth() / 2, (int) bounds.getHeight() / 2);
            return center;
        }
        return null;
    }


    /// Mouse Operations ///
    public boolean useItem(String itemName, boolean includeNotedItems) {
        Item i = getItem(itemName, includeNotedItems);
        if (i != null) {
            Point loc = getPointOfInventoryItem(i, includeNotedItems);
            m.log(loc == null);
            clickPointByTooltip(loc, "Use " + i.getName());
            new cSleep(() -> m.getInventory().isItemSelected(), 250).sleep();
        }
        return m.getInventory().isItemSelected();
    }

    public boolean useItem(String itemName) {
        return useItem(itemName, false);
    }

    public void moveMouse(Point center) {
        if (center != null) {
            m.bot.getMouseEventHandler().generateBotMouseEvent(MOUSE_MOVED, System.currentTimeMillis(), 0, (int) center.getX(), (int) center.getY(), 0, false, 0, true);
        }
    }

    public void clickPoint(Point a) {
        if (a != null) {
            moveMouse(a);
            m.execute(new ClickMouseEvent(new PointDestination(m.getBot(), (int) a.getX(), (int) a.getY())));
        }
    }

    public boolean rightClickPoint(Point a) {
        if (a != null) {
            moveMouse(a);
            m.execute(new ClickMouseEvent(new PointDestination(m.getBot(), (int) a.getX(), (int) a.getY()), true));
        }
        return m.getMenuAPI().isOpen();
    }

    public boolean clickPointByTooltip(Point a, String tooltip) {
        if (a == null || tooltip == null) {
            return false;
        }
        try {
            int i = 0;
            while (!m.getMenuAPI().getTooltip().contains(tooltip)) {
                moveMouse(a);
                m.sleep(25);
                if (i++ > 100) {
                    return false;
                }
            }
            clickPoint(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean rightClickByTooltip(Point a, String tooltip, Entity entity) {
        m.log(tooltip + " " + entity.getName());
        if (a == null || tooltip == null) {
            return false;
        }
        try {
            int i = 0;
            while (!m.getMenuAPI().getTooltip().contains(entity.getName())) {
                moveMouse(a);
                m.sleep(10);
                a.translate(random(-1, 1), random(-1, 1));
                if (i++ > 5) {
                    break;
                }
            }
            if (rightClickPoint(a)) {
                m.sleep(25);
                String[] nameAsArray = {entity.getName()};
                String[] actionAsArray = {tooltip};
                int index = m.getMenuAPI().getMenuIndex(nameAsArray, actionAsArray);
                if (index < 0) {
                    rightClickPoint(a);
                    nameAsArray[0] = entity.getName();
                    actionAsArray[0] = tooltip;
                    index = m.getMenuAPI().getMenuIndex(nameAsArray, actionAsArray);
                }
                clickPoint(getPointOfRectangle(m.getMenuAPI().getOptionRectangle(index)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean rightClickByTooltip(Point a, String tooltip, String name) {
        if (a == null || tooltip == null) {
            return false;
        }
        try {
            int i = 0;
            while (!m.getMenuAPI().getTooltip().contains(tooltip)) {
                moveMouse(a);
                m.sleep(25);
                if (i++ > 100) {
                    return false;
                }
            }
            if (rightClickPoint(a)) {
                String[] nameAsArray = {name};
                String[] actionAsArray = {tooltip};
                clickPoint(getPointOfRectangle(m.getMenuAPI().getOptionRectangle(m.getMenuAPI().getMenuIndex(nameAsArray, actionAsArray))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clickObject(RS2Object a) {
        if (a != null) {
            Point center = getPointOfRS2Object(a);
            moveMouse(center);
            m.execute(new ClickMouseEvent(new PointDestination(m.getBot(), (int) center.getX(), (int) center.getY())));
        }
    }

    public boolean continueDialogue() {
        if (m.getWidgets().get(219, 0, 1) != null) {
            clickWidget(m.getWidgets().get(219, 0, 1), "Continue");
            return true;
        }
        return false;
    }

    public boolean depositAll() {
        if (m.getBank().isOpen()) {
            clickWidget(m.getWidgets().get(12, 30), "Deposit inventory");
            return true;
        }
        return false;
    }

    public boolean clickWidget(RS2Widget wid) {
        if (wid != null && wid.isVisible() && wid.getBounds() != null) {
            clickPoint(getPointOfRectangle(wid.getBounds()));
            return true;
        }
        return false;
    }

    public boolean clickWidget(RS2Widget wid, String tooltip) {
        if (wid != null && wid.isVisible() && wid.getBounds() != null) {
            clickPointByTooltip(getPointOfRectangle(wid.getBounds()), tooltip);
            return true;
        }
        return false;
    }

    private void clickFirstItem(Item item) {
        clickPoint(InventorySlotDestination.getSlot(m.inventory.getSlot(item)).getLocation());
    }

    private void clickNextNotedItem() {
        clickPoint(InventorySlotDestination.getSlot(getSlotOfNotedItem()).getLocation());
    }


    // Inventory
    public void deselectItem() {
        /*
        while (m.getInventory().isItemSelected()) {
            clickWidget(m.getWidgets().get(548, 55));
        }*/
    }

    /// Camera Operations ///
    public void setCameraPosition(int pitch, int yaw, double modifier) {
        m.getCamera().moveYaw(random((int) (yaw * (1 - modifier)), (int) (yaw * (1 + modifier))));
        m.getCamera().movePitch(random((int) (pitch * (1 - modifier)), (int) (yaw * (1 + modifier))));
    }

    private void lookAtObject(RS2Object o) {
        if (o != null && !o.isVisible()) {
            m.getCamera().toEntity(o);
        }
    }

    private void setCameraNorth() {
        if (m.getWidgets().get(548, 8) != null) {
            clickWidget(m.getWidgets().get(548, 8));
        }
    }


    /// Movement ///
    public void walkToPosition(Position pos) {
        if (!pos.isOnMiniMap(m.getBot())) {
            m.getWalking().webWalk(pos);
        }
        clickMinimapTile(pos);
    }

    public void clickMinimapTile(Position pos) {
        if (pos.isOnMiniMap(m.getBot())) {
            clickPoint(new MiniMapTileDestination(m.getBot(), pos).getExactPoint());
            m.getMouse().click(new MiniMapTileDestination(m.getBot(), pos));
        }
    }

    // Status Check
    public boolean isHpLow(double threshold) {
        return m.getSkills().getDynamic(Skill.HITPOINTS) < m.getSkills().getStatic(Skill.HITPOINTS) * threshold;
    }

    public boolean isPrayerLow(double threshhold) {
        return m.getSkills().getDynamic(Skill.PRAYER) > threshhold;
    }

    public boolean isPrayerLow() {
        return isPrayerLow(0.0);
    }

    public boolean shouldPlayerRepot(Skill skill, double threshold) {
        return m.getSkills().getDynamic(skill) < (m.getSkills().getStatic(skill) + (int) (m.getSkills().getStatic(skill) * threshold));
    }

    // Prayer
    public class prayData {
        prayData(Integer widgetChild, Integer configValue) {
            this.configValue = configValue;
            this.widgetChild = widgetChild;
        }

        public int configValue;
        public int widgetChild;
    }


    public void disableQuickPrayer(int prayToggledOn) {
        RS2Widget prayer = m.getWidgets().get(160, 19);
        if (m.getConfigs().get(375) == prayToggledOn) {
            clickWidget(prayer);
        }
    }

    public void enableQuickPrayer(int prayToggledOff) {
        RS2Widget prayer = m.getWidgets().get(160, 19);
        if (m.getConfigs().get(375) == prayToggledOff) {
            clickWidget(prayer);
        }
    }

    public void deactivateAllPrayers() {
        final Integer widgetRoot = 271;
        final Integer configRoot = 83;
        Hashtable<PrayerButton, prayData> uiId = new Hashtable<>();
        uiId.put(PrayerButton.STEEL_SKIN, new prayData(13, Integer.parseInt("200", 16)));
        uiId.put(PrayerButton.ULTIMATE_STRENGTH, new prayData(14, Integer.parseInt("400", 16)));
        uiId.put(PrayerButton.INCREDIBLE_REFLEXES, new prayData(15, Integer.parseInt("800", 16)));
        uiId.put(PrayerButton.PROTECT_FROM_MAGIC, new prayData(16, Integer.parseInt("1000", 16)));
        uiId.put(PrayerButton.PROTECT_FROM_MISSILES, new prayData(17, Integer.parseInt("2000", 16)));
        uiId.put(PrayerButton.PROTECT_FROM_MELEE, new prayData(18, Integer.parseInt("4000", 16)));
        uiId.put(PrayerButton.EAGLE_EYE, new prayData(26, Integer.parseInt("400000", 16)));
        uiId.put(PrayerButton.MYSTIC_MIGHT, new prayData(27, Integer.parseInt("800000", 16)));

        if (openTab(Tab.PRAYER)) {
            for (PrayerButton p : PrayerButton.values()) {
                if (m.getPrayer().isActivated(p)) {
                    clickWidget(m.getWidgets().get(widgetRoot, uiId.get(p).widgetChild));
                    try {
                        m.sleep(25);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean enablePrayer(PrayerButton p) {
        final Integer widgetRoot = 271;
        final Integer configRoot = 83;
        Hashtable<PrayerButton, prayData> uiId = new Hashtable<>();
        uiId.put(PrayerButton.STEEL_SKIN, new prayData(13, Integer.parseInt("200", 16)));
        uiId.put(PrayerButton.ULTIMATE_STRENGTH, new prayData(14, Integer.parseInt("400", 16)));
        uiId.put(PrayerButton.INCREDIBLE_REFLEXES, new prayData(15, Integer.parseInt("800", 16)));
        uiId.put(PrayerButton.PROTECT_FROM_MAGIC, new prayData(16, Integer.parseInt("1000", 16)));
        uiId.put(PrayerButton.PROTECT_FROM_MISSILES, new prayData(17, Integer.parseInt("2000", 16)));
        uiId.put(PrayerButton.PROTECT_FROM_MELEE, new prayData(18, Integer.parseInt("4000", 16)));
        uiId.put(PrayerButton.EAGLE_EYE, new prayData(26, Integer.parseInt("400000", 16)));
        uiId.put(PrayerButton.MYSTIC_MIGHT, new prayData(27, Integer.parseInt("800000", 16)));

        if (openTab(Tab.PRAYER)) {
            clickWidget(m.getWidgets().get(widgetRoot, uiId.get(p).widgetChild));
            return true;
        }

        return false;
    }


    // Gear swap
    public void switchGear(List gear) {
        if (openTab(Tab.INVENTORY)) {
            for (Object s : gear) {
                Item o = null;
                if (gear.get(0) instanceof String) {
                    o = getItem((String) s);
                } else if (gear.get(0) instanceof Integer) {
                    o = getItem((Integer) s);
                }
                if (o != null) {
                    m.log(o.getActions()[0] + " " + o.getName());
                    clickPointByTooltip(getPointOfInventoryItem(o), o.getName());
                }
            }
        }
    }

    // Toggle run
    public boolean enableRun() {
        if (!m.getSettings().isRunning()) {
            clickWidget(m.getWidgets().get(160, 24));
            return true;
        }
        return false;
    }

    // Tab management
    public boolean openTab(Tab tab) {
        if (tab == null) {
            return false;
        }
        final Integer parentWidgetId = 548;
        Hashtable<Tab, Integer> uiId = new Hashtable<>();
        uiId.put(Tab.CLANCHAT, 35);
        uiId.put(Tab.FRIENDS, 36);
        uiId.put(Tab.IGNORES, 37);
        uiId.put(Tab.LOGOUT, 38);
        uiId.put(Tab.SETTINGS, 39);
        uiId.put(Tab.EMOTES, 40);
        uiId.put(Tab.MUSIC, 41);
        uiId.put(Tab.ATTACK, 52);
        uiId.put(Tab.SKILLS, 53);
        uiId.put(Tab.QUEST, 54);
        uiId.put(Tab.INVENTORY, 55);
        uiId.put(Tab.EQUIPMENT, 56);
        uiId.put(Tab.PRAYER, 57);
        uiId.put(Tab.MAGIC, 58);

        deselectItem();
        if (!m.getTabs().getOpen().equals(tab)) {
            clickWidget(m.getWidgets().get(parentWidgetId, uiId.get(tab)));
            new cSleep(() -> m.getTabs().getOpen().equals(tab), 1000).sleep();
        }

        return m.getTabs().getOpen().equals(tab);
    }


    // Eating
    public boolean eat(List<String> food, Double threshold) {
        openTab(Tab.INVENTORY);
        new cSleep(() -> m.getTabs().getOpen().equals(Tab.INVENTORY), 1000).sleep();
        if (m.getTabs().getOpen().equals(Tab.INVENTORY) && isHpLow(threshold)) {
            for (String s : food) {
                if (getItem(s) != null) {
                    Item o = getItem(s);
                    clickPointByTooltip(getPointOfInventoryItem(o), o.getActions()[0]);
                    return true;
                }
            }
        }
        return false;
    }

    // Potions
    // Overload: Type = "Stamina",
    // Literal = false ==> "Stamina potion". Literal = true ==> "Stamina"
    public List<String> makePotionName(String type, boolean literal) {
        List<String> names = new LinkedList<>();
        for (int i = 1; i <= 4; i++) {
            if (literal) {
                names.add(type + "(" + i + ")");
            } else {
                names.add(type + " potion(" + i + ")");
            }
        }
        return names;
    }

    public boolean drinkPotion(String type, boolean literal) {
        if (openTab(Tab.INVENTORY)) {
            /*List<String> pots = new LinkedList<>();
            if (!literal) {
                pots.add(type + " potion(1)");
                pots.add(type + " potion(2)");
                pots.add(type + " potion(3)");
                pots.add(type + " potion(4)");
            } else {
                pots.add(type + "(1)");
                pots.add(type + "(2)");
                pots.add(type + "(3)");
                pots.add(type + "(4)");
            }*/
            for (String s : makePotionName(type, literal)) {
                if (getItem(s) != null) {
                    Item o = getItem(s);
                    clickPointByTooltip(getPointOfInventoryItem(o), o.getActions()[0]);
                    return true;
                }
            }
        }
        return false;
    }

    public void drinkPotion(String type) {
        drinkPotion(type, false);
    } // Overload

    public boolean hasPotion(String type, boolean literal) {
        if (openTab(Tab.INVENTORY)) {
            for (String s : makePotionName(type, literal)) {
                if (m.getInventory().contains(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasPotion(String type) {
        return hasPotion(type, false);
    } // Overload


    public String getPotionNameFromBank(String type, boolean literal) {
        if (m.getBank().isOpen()) {
            for (String s : makePotionName(type, literal)) {
                if (m.getBank().contains(s)) {
                    moveMouse(getPointOfRectangle(getBankSlotRectangle(m.getBank().getItem(s))));
                    return s;
                }
            }
        }
        return null;
    }

    public String getPotionNameFromBank(String type) {
        return getPotionNameFromBank(type, false);
    } // Overload

    // Magic
    private boolean castSpell(Spells.NormalSpells spell) {
        final Integer parentWidgetId = 218;
        final Integer invalidOffset = 50;

        Hashtable<Spells.NormalSpells, Integer> uiId = new Hashtable<>();

        // Enchants
        uiId.put(Spells.NormalSpells.LVL_1_ENCHANT, 6);
        uiId.put(Spells.NormalSpells.LVL_2_ENCHANT, 17);
        uiId.put(Spells.NormalSpells.LVL_3_ENCHANT, 29);
        uiId.put(Spells.NormalSpells.LVL_4_ENCHANT, 37);
        uiId.put(Spells.NormalSpells.LVL_5_ENCHANT, 52);
        uiId.put(Spells.NormalSpells.LVL_6_ENCHANT, 64);

        // Curses
        uiId.put(Spells.NormalSpells.CONFUSE, 3);
        uiId.put(Spells.NormalSpells.WEAKEN, 8);
        uiId.put(Spells.NormalSpells.CURSE, 12);
        uiId.put(Spells.NormalSpells.VULNERABILITY, 51);
        uiId.put(Spells.NormalSpells.ENFEEBLE, 55);
        uiId.put(Spells.NormalSpells.STUN, 59);

        // Teleports
        uiId.put(Spells.NormalSpells.HOME_TELEPORT, 1);
        uiId.put(Spells.NormalSpells.VARROCK_TELEPORT, 16);
        uiId.put(Spells.NormalSpells.LUMBRIDGE_TELEPORT, 19);
        uiId.put(Spells.NormalSpells.FALADOR_TELEPORT, 22);
        uiId.put(Spells.NormalSpells.HOUSE_TELEPORT, 24);
        uiId.put(Spells.NormalSpells.CAMELOT_TELEPORT, 27);
        uiId.put(Spells.NormalSpells.ARDOUGNE_TELEPORT, 33);
        uiId.put(Spells.NormalSpells.WATCHTOWER_TELEPORT, 38);
        uiId.put(Spells.NormalSpells.TROLLHEIM_TELEPORT, 45);
        uiId.put(Spells.NormalSpells.TELEPORT_TO_APE_ATOLL, 48);
        uiId.put(Spells.NormalSpells.TELEPORT_TO_KOUREND, 53);

        // Alchemy
        uiId.put(Spells.NormalSpells.LOW_LEVEL_ALCHEMY, 14);
        uiId.put(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY, 35);

        try {
            openTab(Tab.MAGIC);
            new cSleep(() -> m.getTabs().getOpen().equals(Tab.MAGIC), 1000).sleep();
            if (m.getTabs().getOpen().equals(Tab.MAGIC)) {
                clickWidget(m.getWidgets().get(parentWidgetId, uiId.get(spell)));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Combat
    boolean activateSpecial() {
        final int root = 593;
        final int child = 31;
        if (openTab(Tab.ATTACK)) {
            clickWidget(m.getWidgets().get(root, child));
            return true;
        }
        return false;
    }

    // Utilities
    public List<NPC> filterNPCList(List<String> names) {
        List<NPC> filtered = new LinkedList<>();
        for (NPC o : m.getNpcs().getAll()) {
            for (String s : names) {
                if (o.getName().equals(s)) {
                    filtered.add(o);
                    break;
                }
            }
        }
        return filtered;
    }

    public String[] listToString(List<String> list) {
        String[] conversion = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            conversion[i] = list.get(i);
        }
        return conversion;
    }

    public boolean readyToReset(Timer timer, int time) {
        return timer.getElapsed() > time;
    }

    public Position getFocalPointOfArea(Area area) {
        int averageX, averageY, size;
        averageX = averageY = 0;
        size = area.getPositions().size();
        for (Position p : area.getPositions()) {
            averageX += p.getX();
            averageY += p.getY();
        }
        return new Position(averageX / size, averageY / size, 0);
    }

    public String getNameOfPlayerOnOurPosition() {
        List<Player> players = m.getPlayers().getAll();
        for (Player o : players) {
            if (o.getPosition().equals(m.myPosition()) && !o.getName().equals(m.myPlayer().getName())) {
                return o.getName();
            }
        }
        return null;
    }

}