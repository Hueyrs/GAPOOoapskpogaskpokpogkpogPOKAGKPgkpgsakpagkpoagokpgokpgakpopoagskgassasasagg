package org.rs2server.rs2.model.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.rs2server.rs2.action.impl.HarvestingAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;

public class Woodcutting extends HarvestingAction {

    /**
     * The random number generator.
     */
    private final Random random = new Random();

    /**
     * The tree we are cutting down.
     */
    private final GameObject object;

    /**
     * The hatchet we are using.
     */
    private Hatchet hatchet;

    /**
     * The tree we are cutting down.
     */
    private final Tree tree;

    public Woodcutting(Mob mob, GameObject object) {
        super(mob);
        this.object = object;
        tree = Tree.forId(object.getId());
    }

    /**
     * Represents types of axe hatchets.
     * @author Michael (Scu11)
     */
    public static enum Hatchet {

        /**
         * Dragon axe.
         */
        DRAGON(6739, 61, Animation.create(2846)),

        /**
         * Rune axe.
         */
        RUNE(1359, 41, Animation.create(867)),

        /**
         * Adamant axe.
         */
        ADAMANT(1357, 31, Animation.create(869)),

        /**
         * Mithril axe.
         */
        MITHRIL(1355, 21, Animation.create(871)),

        /**
         * Black axe.
         */
        BLACK(1361, 6, Animation.create(873)),

        /**
         * Steel axe.
         */
        STEEL(1353, 6, Animation.create(875)),

        /**
         * Iron axe.
         */
        IRON(1349, 1, Animation.create(877)),

        /**
         * Bronze axe.
         */
        BRONZE(1351, 1, Animation.create(879));

        /**
         * The item id of this hatchet.
         */
        private int id;

        /**
         * The level required to use this hatchet.
         */
        private int level;

        /**
         * The animation performed when using this hatchet.
         */
        private Animation animation;

        /**
         * A list of hatchets.
         */
        private static List<Hatchet> hatchets = new ArrayList<Hatchet>();

        /**
         * Gets the list of hatchets.
         * @return The list of hatchets.
         */
        public static List<Hatchet> getHatchets() {
            return hatchets;
        }

        /**
         * Populates the hatchet map.
         */
        static {
            for (Hatchet hatchet : Hatchet.values()) {
                hatchets.add(hatchet);
            }
        }

        private Hatchet(int id, int level, Animation animation) {
            this.id = id;
            this.level = level;
            this.animation = animation;
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @return the level
         */
        public int getRequiredLevel() {
            return level;
        }

        /**
         * @return the animation
         */
        public Animation getAnimation() {
            return animation;
        }
    }

    /**
     * Represents types of tree.
     * @author Michael
     */
    public static enum Tree {

        /**
         * Normal tree.
         */
        NORMAL(1511, 1, 50, 50, 1, new int[] { 1276, 1277, 1278, 1279, 1280, 1282, 1283, 1284,
                1285, 1286, 1287, 1288, 1289, 1290, 1291, 1301, 1303, 1304, 1305, 1318, 1319, 1315,
                1316, 1330, 1331, 1332, 1333, 1383, 1384, 2409, 2447, 2448, 3033, 3034, 3035, 3036,
                3879, 3881, 3883, 3893, 3885, 3886, 3887, 3888, 3892, 3889, 3890, 3891, 3928, 3967,
                3968, 4048, 4049, 4050, 4051, 4052, 4053, 4054, 4060, 5004, 5005, 5045, 5902, 5903,
                5904, 8973, 8974, 10041, 10081, 10082, 10664, 11112, 11510, 12559, 12560, 12732,
                12895, 12896, 13412, 13411, 13419, 13843, 13844, 13845, 13847, 13848, 13849, 13850,
                14308, 14309, 14513, 14514, 14515, 14521, 14564, 14565, 14566, 14593, 14594, 14595,
                14600, 14635, 14636, 14637, 14642, 14664, 14665, 14666, 14693, 14694, 14695, 14696,
                14701, 14738, 14796, 14797, 14798, 14799, 14800, 14801, 14802, 14803, 14804, 14805,
                14806, 14807, 15489, 15776, 15777, 16264, 16265, 19165, 19166, 19167, 23381, }),

        /**
         * Oak tree.
         */
        OAK(1521, 15, 75, 22, 2, new int[] { 1281, 3037, 8462, 8463, 8464, 8465, 8466, 8467, 10083,
                13413, 13420 }),

        /**
         * Willow tree.
         */
        WILLOW(1519, 30, 135, 22, 4, new int[] { 1308, 5551, 5552, 5553, 8481, 8482, 8483, 8484,
                8485, 8486, 8487, 8488, 8496, 8497, 8498, 8499, 8500, 8501, 13414, 13421 }),

        /**
         * Maple tree.
         */
        MAPLE(1517, 45, 200, 100, 5, new int[] { 1307, 4674, 8435, 8436, 8437, 8438, 8439, 8440,
                8441, 8442, 8443, 8444, 8454, 8455, 8456, 8457, 8458, 8459, 8460, 8461, 13415,
                13423 }),

        /**
         * Yew tree.
         */
        YEW(1515, 60, 350, 160, 7, new int[] { 1309, 8503, 8504, 8505, 8506, 8507, 8508, 8509,
                8510, 8511, 8512, 8513, 13416, 13422 }),

        /**
         * Magic tree.
         */
        MAGIC(1513, 75, 500, 310, 9, new int[] { 1306, 8396, 8397, 8398, 8399, 8400, 8401, 8402,
                8403, 8404, 8405, 8406, 8407, 8408, 8409, 13417, 13424 }),

        /**
         * Fremennik Trials Swaying tree
         */
        SWAYING_TREE(3692, 40, 10, 22, 4, new int[] { 4142 }),
        /**
         * Mahogany tree.
         */
        MAHOGANY(6332, 50, 250, 22, 4, new int[] { 9034 }),

        /**
         * Teak tree.
         */
        TEAK(6333, 35, 170, 22, 4, new int[] { 9036 }),

        /**
         * Achey tree.
         */
        ACHEY(2862, 1, 50, 22, 4, new int[] { 2023 }),

        /**
         * Dramen tree
         */
        DRAMEN(771, 36, 0, 22, 4, new int[] { 1292 });

        /**
         * The object ids of this tree.
         */
        private int[] objects;

        /**
         * The level required to cut this tree down.
         */
        private int level;

        /**
         * The log rewarded for each cut of the tree.
         */
        private int log;

        /**
         * The time it takes for this tree to respawn.
         */
        private int respawnTimer;

        /**
         * The amount of logs this tree contains.
         */
        private int logCount;

        /**
         * The experience granted for cutting a log.
         */
        private double experience;

        /**
         * A map of object ids to trees.
         */
        private static Map<Integer, Tree> trees = new HashMap<Integer, Tree>();

        /**
         * Gets a tree by an object id.
         * @param object
         *            The object id.
         * @return The tree, or <code>null</code> if the object is not a tree.
         */
        public static Tree forId(int object) {
            return trees.get(object);
        }

        static {
            for (Tree tree : Tree.values()) {
                for (int object : tree.objects) {
                    trees.put(object, tree);
                }
            }
        }

        /**
         * Creates the tree.
         * @param log
         *            The log id.
         * @param level
         *            The required level.
         * @param experience
         *            The experience per log.
         * @param objects
         *            The object ids.
         */
        private Tree(int log, int level, double experience, int respawnTimer, int logCount,
                int[] objects) {
            this.objects = objects;
            this.level = level;
            this.experience = experience;
            this.respawnTimer = respawnTimer;
            this.logCount = logCount;
            this.log = log;
        }

        /**
         * Gets the log id.
         * @return The log id.
         */
        public int getLogId() {
            return log;
        }

        /**
         * Gets the object ids.
         * @return The object ids.
         */
        public int[] getObjectIds() {
            return objects;
        }

        /**
         * Gets the required level.
         * @return The required level.
         */
        public int getRequiredLevel() {
            return level;
        }

        /**
         * Gets the experience.
         * @return The experience.
         */
        public double getExperience() {
            return experience;
        }

        /**
         * @return the respawnTimer
         */
        public int getRespawnTimer() {
            return respawnTimer;
        }

        /**
         * @return the logCount
         */
        public int getLogCount() {
            return logCount;
        }
    }

    @Override
    public Animation getAnimation() {
        return hatchet.getAnimation();
    }

    @Override
    public int getCycleCount() {
        int skill = getMob().getSkills().getLevel(getSkill());
        int level = tree.getRequiredLevel();
        int modifier = hatchet.getRequiredLevel();
        int randomAmt = random.nextInt(3);
        double cycleCount = 1;
        cycleCount = Math.ceil((level * 50 - skill * 10) / modifier * 0.25 - randomAmt * 4);
        if (cycleCount < 1) {
            cycleCount = 1;
        }
        return (int) cycleCount;
    }

    @Override
    public double getExperience() {
        return tree.getExperience();
    }

    @Override
    public GameObject getGameObject() {
        return object;
    }

    @Override
    public int getGameObjectMaxHealth() {
        return tree.getLogCount();
    }

    @Override
    public String getHarvestStartedMessage() {
        return "You swing your axe at the tree.";
    }

    @Override
    public String getLevelTooLowMessage() {
        return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of "
                + tree.getRequiredLevel() + " to cut this tree.";
    }

    @Override
    public int getObjectRespawnTimer() {
        return tree.getRespawnTimer();
    }

    @Override
    public GameObject getReplacementObject() {
        return new GameObject(getGameObject().getLocation(), 1342, 10, 0, false);
    }

    @Override
    public int getRequiredLevel() {
        return tree.getRequiredLevel();
    }

    @Override
    public Item getReward() {
        return new Item(tree.getLogId(), 1);
    }

    @Override
    public int getSkill() {
        return Skills.WOODCUTTING;
    }

    @Override
    public String getSuccessfulHarvestMessage() {
        return "You get some " + getReward().getDefinition().getName().toLowerCase() + ".";
    }

    @Override
    public boolean canHarvest() {
        for (Hatchet hatchet : Hatchet.values()) {
            if ((getMob().getInventory().contains(hatchet.getId()) || getMob().getEquipment()
                    .contains(hatchet.getId()))
                    && getMob().getSkills().getLevelForExperience(getSkill()) >= hatchet
                            .getRequiredLevel()) {
                this.hatchet = hatchet;
                break;
            }
        }
        if (hatchet == null) {
            getMob().getActionSender().sendMessage("You do not have an axe that you can use.");
            return false;
        }
        return true;
    }

    @Override
    public String getInventoryFullMessage() {
        return "Your inventory is too full to hold any more "
                + getReward().getDefinition().getName().toLowerCase() + ".";
    }

}