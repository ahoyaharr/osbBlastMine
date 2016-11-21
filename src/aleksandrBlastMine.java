import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.api.util.ExperienceTracker;
import org.osbot.rs07.api.util.GraphicUtilities;
import org.osbot.rs07.input.mouse.InventorySlotDestination;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

@ScriptManifest(name = "aleksandrBlastMine", author = "Solzhenitsyn", version = 0.0, info = "OSBot Template", logo = "")
public class aleksandrBlastMine extends Script {

    private Timer timer;
    private Cursor m;
    private aleksandrMethods a;
    private String status = "Initializing script...";

    private ExperienceTracker xp;

    private List<String> inventory = Arrays.asList("Chisel", "Tinderbox", "Dynamite");

    public void onStart() {
        a = new aleksandrMethods(this);
        m = new Cursor(this);
        timer = new Timer(System.currentTimeMillis());
        xp = getExperienceTracker();
        xp.start(Skill.MINING);
        xp.start(Skill.FIREMAKING);
    }

    private String parse(long millis) {
        String time = "n/a";
        if (millis > 0) {
            int seconds = (int) (millis / 1000) % 60;
            int minutes = (int) ((millis / (1000 * 60)) % 60);
            int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
            time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return time;
    }

    public void onPaint(Graphics2D g) { // NOT DONE
        int msHour = 3600000;
        m.draw(g);
        g.setColor(new Color(255, 255, 255, 255));
        g.setFont(new Font("Tahoma", Font.PLAIN, 14));
        g.drawString("Currently " + status, 12, 48);
        g.drawString("Timer: " + timer.parse(timer.getElapsed()), 12, 62);
        g.drawString("Ores Mined: " + getFiremakingXp() / 50 + " (" + (int) (xp.getGainedXPPerHour(Skill.FIREMAKING) / 50) + ")", 12, 74);

        g.setFont(new Font("Tahoma", Font.BOLD, 14));
        g.drawString("aleksandrBlastMine", 12, 34);

        /*
        g.setColor(new Color(55, 255, 0, 80));
        if (currentWall != null && currentWall.exists() && currentWall.isVisible()) {
            if (GraphicUtilities.getScreenCoordinates(bot, currentWall.getGridX(), currentWall.getGridY(), currentWall.getZ(), currentWall.getHeight())[0] != -1) {
                GraphicUtilities.drawModel(bot, g, currentWall.getGridX(), currentWall.getGridY(), currentWall.getZ(), currentWall.getModel());
            }
        }
        if (currLoot != null && currLoot.exists() && currLoot.isVisible()) {
            if (GraphicUtilities.getScreenCoordinates(bot, currLoot.getGridX(), currLoot.getGridY(), currLoot.getZ(), currLoot.getHeight())[0] != -1) {
                GraphicUtilities.drawModel(bot, g, currLoot.getGridX(), currLoot.getGridY(), currLoot.getZ(), currLoot.getModel());
            }
        }
        if (chest != null && chest.exists() && chest.isVisible()) {
            if (GraphicUtilities.getScreenCoordinates(bot, chest.getGridX(), chest.getGridY(), chest.getZ(), chest.getHeight())[0] != -1) {
                GraphicUtilities.drawModel(bot, g, chest.getGridX(), chest.getGridY(), chest.getZ(), chest.getModel());
            }
        }
        */
    }

    // 79 Unexcavated -> 81 Excavated -> 83 Unlit Dynamite -> 85 Lit dynamite (or +1 to each)
    private final int type1 = 28579;
    private final int type2 = 28580;
    private int[] wallID = {type2, type1, type1, type2, type2, type1};
    private int[] phase = {0, 2, 4, 6};
    private String loot = "Blasted ore";
    private final List<Position> wallPos = new LinkedList<>(Arrays.asList(
            new Position(1502, 3876, 0), // Wall 1
            new Position(1503, 3876, 0), // 2
            new Position(1508, 3868, 0), // 3
            new Position(1508, 3871, 0), // 4
            new Position(1508, 3867, 0), // 5
            new Position(1508, 3870, 0)  // 6
    ));
    private final List<Position> lootPos = new LinkedList<>(Arrays.asList(
            new Position(1507, 3871, 0), // Loot 1
            new Position(1507, 3868, 0), // 2
            new Position(1507, 3867, 0), // 3
            new Position(1507, 3870, 0), // 4
            new Position(1503, 3875, 0), // 5
            new Position(1502, 3875, 0)  // 6
    ));
    private final List<String> blastedOre = new LinkedList<>(Arrays.asList(
            "Blasted ore"
    ));

    private boolean supplied() {
        for (String s : inventory) {
            if (!getInventory().contains(s)) {
                return false;
            }
        }
        return true;
    }


    private int getMiningXp() {
        return xp.getGainedXP(Skill.MINING);
    }

    private int getFiremakingXp() {
        return xp.getGainedXP(Skill.FIREMAKING);
    }

    private int getUnnotedDynamite() {
        int k = 0;
        for (Item o : getInventory().getItems()) {
            if (o != null && !o.isNote() && o.getName().equals(dynamite)) {
                k++;
            }
        }
        return k;
    }

    public void excavateWall(Integer i) {
        log("Excavating wall " + i);
        storage = getMiningXp();
        a.deselectItem();
        while (!myPlayer().isMoving()) {
            if (a.getRS2Object(wallPos.get(i), wallID[i]) != null && (!myPlayer().isMoving() || !myPlayer().isAnimating())) {
                currentWall = a.getRS2Object(wallPos.get(i), wallID[i]);
                a.clickPointByTooltip(a.getPointOfRS2Object(currentWall), excavate);
            }
            try {
                sleep(random(100, 125));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new cSleep(() -> storage < getMiningXp(), 10000).sleep();
    }

    public void dynamiteWall(Integer i) {
        log("Placing dynamite on wall " + i);
        a.deselectItem();
        a.openTab(Tab.INVENTORY);
        while (a.getRS2Object(wallPos.get(i), wallID[i] + phase[2]) == null) {
            if (getInventory().contains(dynamite)) {
                currentWall = a.getRS2Object(wallPos.get(i), wallID[i] + phase[1]);
                itemLoc = a.getPointOfInventoryItemContainingName(dynamite);
                if (a.useItem(dynamite)) {
                    a.clickPointByTooltip(a.getPointOfRS2Object(currentWall), currentWall.getName());
                    new cSleep(() -> a.getRS2Object(wallPos.get(i), wallID[i] + phase[2]) != null, 2500).sleep();
                }
            }
        }
    }

    public void lightDynamite(Integer i) {
        log("Lighting dynamite on wall " + i);
        a.deselectItem();
        storage = getFiremakingXp();
        while (getFiremakingXp() == storage) {
            if (a.getRS2Object(wallPos.get(i), wallID[i] + phase[2]) != null) {
                currentWall = a.getRS2Object(wallPos.get(i), wallID[i] + phase[2]);
                a.clickObject(currentWall);
            }
            try {
                sleep(random(100, 150));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new cSleep(() -> storage < getFiremakingXp(), 1500).sleep();
    }


    private enum State {
        mine, deposit, bank, lootFailsafe, runFailsafe;
    }

    private double half = 0.5;

    private State getState() {
        if (getSettings().getRunEnergy() < 5 || !getSettings().isRunning()) {
            log("Run failsafe");
            return State.runFailsafe;
        } else if (a.getLootableItem(blastedOre, 10) != null) {
            log("Looting failsafe");
            return State.lootFailsafe;
        } else if (getInventory().getAmount("Blasted ore") > 0) {
            log("Depositing ores");
            return State.deposit;
        } else if (a.isHpLow(half) || getUnnotedDynamite() < 6 || !supplied() || !a.hasPotion("Stamina")) {
            log("Banking");
            return State.bank;
        } else {
            log("Mining");
            return State.mine;
        }
    }

    GroundItem currLoot = null;
    RS2Object currentWall = null;
    RS2Object chest = null;
    String excavate = "Excavate";
    String dynamite = "Dynamite";


    int sackId = 28592;
    int chestId = 28595;
    Position sackLoc = new Position(1497, 3871, 0);
    Position terminalPos = wallPos.get(1).translate(0, -2);
    Position bankChest = new Position(1501, 3856, 0);
    Position bankSpot = bankChest.translate(0, 1);
    private int storage;
    private Point itemLoc;

    public int onLoop() throws InterruptedException {
        switch (getState()) {
            case runFailsafe:
                a.drinkPotion("Stamina");
                a.enableRun();
                new cSleep(() -> getSettings().isRunning(), 1000).sleep();
                break;
            case lootFailsafe:
                while (a.getLootableItem(blastedOre, 10) != null) {
                    if (!myPlayer().isMoving()) {
                        currLoot = a.getLootableItem(blastedOre, 10);
                        itemLoc = currLoot.getPoint();
                        if (itemLoc != null) {
                            a.rightClickByTooltip(itemLoc, "Take", currLoot);
                        }
                    }
                }
                break;
            case mine:
                // Walk to mining spot
                if (myPosition().distance(terminalPos) > 5) {
                    a.walkToPosition(terminalPos);
                    new cSleep(() -> myPosition().distance(terminalPos) < 2, 5000).sleep();
                }

                // Dragon pickaxe spec
                if (getCombat().getSpecialPercentage() == 100) {
                    a.activateSpecial();
                }

                // Excavate Spot 2
                a.setCameraPosition(67, 340, 0.0);
                storage = getMiningXp();
                if (a.getRS2Object(wallPos.get(1), wallID[1]) != null) {
                    if (!myPlayer().isAnimating()) {
                        currentWall = a.getRS2Object(wallPos.get(1), wallID[1]);
                        Point translated = a.getPointOfRS2Object(currentWall);
                        translated.translate(5, -5);
                        a.clickPointByTooltip(translated, excavate);
                    }
                }
                new cSleep(() -> storage < getMiningXp(), 500).sleep();
                sleep(random(50, 100));

                // Dynamite 2
                if (a.getRS2Object(wallPos.get(1), wallID[1] + phase[1]) != null) {
                    dynamiteWall(1);

                    // Excavate 1
                    excavateWall(0);

                    // Dynamite 1
                    a.deselectItem();
                    dynamiteWall(0);

                    // Light 1
                    lightDynamite(0);
                    a.setCameraPosition(67, 285, 0.0);

                    // Light 2
                    lightDynamite(1);

                    // Excavate 3
                    excavateWall(2);

                    // Dynamite 3
                    dynamiteWall(2);

                    // Light 3
                    lightDynamite(2);

                    // Excavate 4
                    excavateWall(3);

                    // Dynamite 4
                    dynamiteWall(3);

                    // Light 4
                    lightDynamite(3);

                    // Excavate 5
                    excavateWall(4);

                    // Dynamite 5
                    dynamiteWall(4);

                    // Light 5
                    lightDynamite(4);

                    // Excavate 6
                    excavateWall(5);

                    // Dynamite 6
                    dynamiteWall(5);

                    // Light 6
                    lightDynamite(5);

                    // Looting
                    for (int i = 0; i < lootPos.size(); i++) {
                        log("Looting ore# " + i + 1);
                        final int j = i;
                        new cSleep(() -> a.getGroundItemAtLoc(lootPos.get(j), "Blasted ore") != null, 2500).sleep();
                        currLoot = a.getGroundItemAtLoc(lootPos.get(i), "Blasted ore");
                        while (currLoot != null && currLoot.exists()) {
                            itemLoc = currLoot.getPoint();
                            if (itemLoc != null) {
                                a.rightClickByTooltip(itemLoc, "Take", currLoot);
                            }
                            new cSleep(() -> a.getPointOfGroundItem("Blasted ore", lootPos.get(j)) == null, 3500).sleep();
                            if (i == 2) {
                                new cSleep(() -> a.getPointOfGroundItem("Blasted ore", lootPos.get(j + 1)) != null, 3500).sleep();
                                a.setCameraPosition(67, 340, 0.0);
                                sleep(100);
                            }
                        }
                    }
                }
                break;
            case deposit:
                if (getSettings().getRunEnergy() < 50) {
                    a.drinkPotion("Stamina");
                }
                RS2Object depositSack = a.getRS2Object(sackLoc, sackId);
                if (depositSack != null) {
                    a.walkToPosition(sackLoc.translate(0, 1));
                    new cSleep(() -> depositSack.isVisible(), 7500).sleep();
                    sleep(250);
                    currentWall = a.getRS2Object(sackLoc, sackId);
                    a.rightClickByTooltip(a.getPointOfRS2Object(currentWall), "Deposit", currentWall);
                }
                new cSleep(() -> !getInventory().contains("Blasted ore") && !myPlayer().isAnimating(), 1500).sleep();
                sleep(1250);
                if (!getInventory().contains("Blasted ore")) {
                    if (getUnnotedDynamite() >= 6) {
                        a.walkToPosition(terminalPos);
                        sleep(random(200, 250));
                        new cSleep(() -> myPosition().distance(terminalPos) < 3, 10000).sleep();
                    }
                }
                break;
            case bank:
                if (getWidgets().getWidgetContainingText("don't have any more inventory") != null) {
                    a.clickWidget(getWidgets().get(229, 1));
                    break;
                }
                String food = "Tuna";
                Integer heal = 10;
                if (getSettings().getRunEnergy() < 50) {
                    a.drinkPotion("Stamina");
                }
                a.walkToPosition(bankSpot);
                chest = a.getRS2Object(bankChest, chestId);
                if (chest != null && !chest.isVisible()) {
                    new cSleep(() -> chest.isVisible(), 20000).sleep();
                    sleep(random(300,500));
                    if (chest != null && !chest.isVisible()) {
                        getCamera().toEntity(chest);
                    }
                }
                if (!a.hasPotion("Stamina") || a.isHpLow(half) || !supplied()) {
                    log("trying to open chest");
                    chest = a.getRS2Object(bankChest, chestId);
                    Point chestLoc = a.getPointOfRS2Object(chest);
                    a.rightClickByTooltip(chestLoc, "Use", chest);
                    new cSleep(() -> getBank().isOpen(), 2500).sleep();
                    if (a.isHpLow(half)) {
                        if (getBank().isOpen()) {
                            a.depositAll();
                            a.moveMouse(a.getPointOfBankSlotItem(getBank().getItem(food)));
                            new cSleep(() -> getInventory().isEmpty(), 2500).sleep();
                            getBank().withdraw(food, (getSkills().getStatic(Skill.HITPOINTS) - getSkills().getDynamic(Skill.HITPOINTS)) / heal);
                            sleep(25);
                        }
                    }
                    if (!a.hasPotion("Stamina") || !supplied()) {
                        if (getBank().isOpen()) {
                            sleep(25);
                            if (getInventory().contains("Vial")) {
                                getBank().deposit("Vial", 1);
                                sleep(25);
                            }
                            if (!a.hasPotion("Stamina")) {
                                String potionName = a.getPotionNameFromBank("Stamina");
                                Point potLoc = a.getPointOfBankSlotItem(getBank().getItem(potionName));
                                a.clickPointByTooltip(potLoc, potionName);
                            }
                            if (!supplied()) {
                                for (String s : inventory) {
                                    sleep(25);
                                    if (!s.equals(dynamite)) {
                                        a.clickPointByTooltip(a.getPointOfBankSlotItem(getBank().getItem(s)), s);
                                    } else {
                                        a.clickWidget(getWidgets().get(12, 23), "Note");
                                        a.moveMouse(a.getPointOfInventoryItemContainingName(s));
                                        getBank().withdrawAll(s);
                                    }
                                }
                            }
                        }
                    }
                    new cSleep(() -> a.hasPotion("Stamina"), 1000).sleep();
                }
                getBank().close();
                while (getInventory().contains(food)) {
                    a.openTab(Tab.INVENTORY);
                    a.clickPointByTooltip(a.getPointOfInventoryItemContainingName(food), food);
                    sleep(random(100, 250));
                }
                if (getUnnotedDynamite() < 24 && !a.isHpLow(half) && a.hasPotion("Stamina")) {
                    if (a.useItem(dynamite, true)) {
                        chest = a.getRS2Object(bankChest, chestId);
                        Point chestLoc = a.getPointOfRS2Object(chest);
                        a.rightClickByTooltip(chestLoc, "Use", chest);
                        new cSleep(() -> getWidgets().get(219, 0, 1) != null, 5000).sleep();
                        sleep(25);
                        a.continueDialogue();
                        sleep(25);
                        a.walkToPosition(terminalPos);
                        new cSleep(() -> getInventory().isFull(), 2000).sleep();
                    }
                }
                if (a.hasPotion("Stamina") && getUnnotedDynamite() >= 24 && !myPlayer().isMoving()) {
                    a.walkToPosition(terminalPos);
                }
                break;
        }
        return 25;
    }
}

