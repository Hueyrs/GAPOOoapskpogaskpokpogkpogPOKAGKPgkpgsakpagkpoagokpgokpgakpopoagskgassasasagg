package org.rs2server.rs2.model.minigame.impl;

import java.util.Random;

import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.GroundItem;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.NPCDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.tickable.Tickable;

public class FightCaves extends AbstractMinigame {

    private final Player player;

    private int height;

    private int wave = 1;

    private int remaining = 1;

    private final Random random = new Random();

    public FightCaves(Player player) {
        this.player = player;
        // this.height = player.getIndex() * 4;
    }

    public static void enterCaves(Player player) {
        if (player.getMinigame() != null) {
            return;
        }
        System.out.println("lulwut");
        DialogueManager.openDialogue(player, 254);
        FightCaves caves = new FightCaves(player);
        player.setMinigame(caves);
        caves.start();
    }

    @Override
    public void end() {
        player.setMinigame(null);
        player.setTeleportTarget(Location.create(2348, 5168, 0));
        player.setAttribute("temporaryHeight", 0);
        Item tokkul = TOKKUL;
        tokkul.setCount((int) (139.68 * (wave + 32/* ? */)));
        if (player.getInventory().hasRoomFor(tokkul)) {
            player.getInventory().add(tokkul);
        } else {
            World.getWorld().createGroundItem(
                    new GroundItem(player.getName(), tokkul, player.getLocation()), player);
        }
        if (player.getInventory().hasRoomFor(FIRE_CAPE)) {
            player.getInventory().add(FIRE_CAPE);
        } else {
            World.getWorld().createGroundItem(
                    new GroundItem(player.getName(), tokkul, player.getLocation()), player);
        }
    }

    @Override
    public void quit(Player player) {
        player.setMinigame(null);
        player.setTeleportTarget(Location.create(2348, 5168, 0));
        player.setAttribute("temporaryHeight", null);
        Item tokkul = TOKKUL;
        tokkul.setCount((int) (139.68 * (wave + 32/* ? */)));
        if (player.getInventory().hasRoomFor(tokkul)) {
            player.getInventory().add(tokkul);
        } else {
            World.getWorld().createGroundItem(
                    new GroundItem(player.getName(), tokkul, player.getLocation()), player);
        }
    }

    @Override
    public Boundary getBoundary() {
        return new Boundary("Fight Caves", Location.create(MIN_X, MIN_Y, height), Location.create(
                MAX_X, MAX_Y, height));
    }

    @Override
    public ItemSafety getItemSafety() {
        return ItemSafety.SAFE;
    }

    @Override
    public String getName() {
        return "Fight Caves";
    }

    public void spawnWave() {
        try {
            if (wave < MAX_WAVE) {
                int amount = 0;
                for (int i = 0; i < WAVES[wave].length; i++) {
                    NPCDefinition def = NPCDefinition.forId(WAVES[wave][i]);
                    Location spawn = (Location) SPAWNS[random.nextInt(SPAWNS.length - 1)];

                    spawn.setZ(height);
                    Location min = Location.create(MIN_X, MIN_Y, height);
                    Location max = Location.create(MAX_X, MAX_Y, height);
                    NPC monster = new NPC(def, spawn, min, max);
                    amount++;
                    World.getWorld().register(monster);
                    monster.getCombatState().startAttacking(player,
                            player.getSettings().isAutoRetaliating());
                }
                remaining = amount;
            } else if (wave == WAVES.length - 1) {
                // spawnjad
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        player.setAttribute("temporaryHeight", this.height);
        player.setTeleportTarget(Location.create(2412, 5117, this.height));
        World.getWorld().submit(new Tickable(2) {
            @Override
            public void execute() {
                spawnWave();
                this.stop();
            }
        });
    }

    @Override
    public boolean deathHook(Player player) {
        player.setMinigame(null);
        player.setTeleportTarget(Location.create(2348, 5168, 0));
        player.setAttribute("temporaryHeight", 0);
        Item tokkul = TOKKUL;
        tokkul.setCount((int) (139.68 * (wave + 1/* ? */)));
        if (player.getInventory().hasRoomFor(tokkul))
            player.getInventory().add(tokkul);
        else
            World.getWorld().createGroundItem(
                    new GroundItem(player.getName(), tokkul, player.getLocation()), player);
        return true;
    }

    @Override
    public void killHook(final Player player, final Mob victim) {
        remaining--;
        if (remaining < 1) {
            wave++;
            if (wave < MAX_WAVE) {
                World.getWorld().submit(new Tickable(2) {
                    @Override
                    public void execute() {
                        this.stop();
                        spawnWave();
                        World.getWorld().unregister((NPC) victim);
                    }
                });
            } else
                end();
        }
    }

    private static final Item TOKKUL = new Item(6529);
    private static final Item FIRE_CAPE = new Item(6570);

    private static final int MIN_X = 2368, MIN_Y = 5056, MAX_X = 2431, MAX_Y = 5120;

    private static final int[][] WAVES = new int[][] { { 1 }, // empty wave 30
            { 7, 7 }, // wave 31
            { 7, 8 }, { 2, 3, 2 }, { 1, 2, 2 }, {}, {}, {}, {}, {}, {}, {}, {}, {}, {} };

    private static final Location[] SPAWNS = new Location[] { Location.create(2419, 5083),
            Location.create(2379, 5107), Location.create(2402, 5088) };

    private static final int MAX_WAVE = WAVES.length - 1;

}
