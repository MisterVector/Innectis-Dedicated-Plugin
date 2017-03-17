package net.innectis.innplugin.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.entity.*;

/**
 *
 * @author Hret
 *
 * Enum to keep track of the entityTypes.
 */
public enum IdpEntityType {

    // Enities
    ARROW(1, "Arrow", Arrow.class, Group.OBJECTS, null, true, "arrow"),
    BLAZE(2, "Blaze", Blaze.class, Group.AGGRESSIVE, EntityType.BLAZE, true, "blaze"),
    BAT(3, "Bat", Bat.class, Group.ANIMALS, EntityType.BAT, true, "bat"),
    BOAT(4, "Boat", Boat.class, Group.AGGRESSIVE, null, false, "boat"),
    CAVESPIDER(5, "Cave Spider", CaveSpider.class, Group.AGGRESSIVE, EntityType.CAVE_SPIDER, true, "cavespider", "undergroundspider"),
    CHICKEN(6, "Chicken", Chicken.class, Group.ANIMALS, EntityType.CHICKEN, true, "chicken", "chick"),
    COW(7, "Cow", Cow.class, Group.ANIMALS, EntityType.COW, true, "cow"),
    CREEPER(8, "Creeper", Creeper.class, Group.AGGRESSIVE, EntityType.CREEPER, true, "creeper"),
    EGG(9, "Egg", Egg.class, Group.OBJECTS, null, true, "egg", "chickenegg"),
    ENDERDRAGON(10, "Enderdragon", EnderDragon.class, Group.AGGRESSIVE, EntityType.ENDER_DRAGON, false, "enderdragon"),
    ENDERMAN(11, "Enderman", Enderman.class, Group.AGGRESSIVE, EntityType.ENDERMAN, true, "enderman"),
    ENDERPEARL(12, "Enderpearl", EnderPearl.class, Group.OBJECTS, null, false, "enderpearl", "pearl"),
    EXPERIENCEORB(13, "Experience orb", ExperienceOrb.class, Group.AGGRESSIVE, null, false, "experienceorb", "exporb"),
    EXPLOSIVE(14, "Explosive", Explosive.class, Group.OBJECTS, null, false, "explosive"),
    FISH(15, "Fish", FishHook.class, Group.ANIMALS, null, false, "fish"),
    FIREBALL(16, "Fireball", Fireball.class, Group.OBJECTS, null, true, "fireball"),
    GIANT(17, "Giant", Giant.class, Group.OBJECTS, EntityType.GIANT, true, "giant", "giantzombie"),
    GHAST(18, "Ghast", Ghast.class, Group.AGGRESSIVE, EntityType.GHAST, true, "ghast"),
    HORSE(19, "Horse", Horse.class, Group.COMPANIONS, EntityType.HORSE, true, "horse"),
    IRON_GOLEM(20, "IronGolem", IronGolem.class, Group.COMPANIONS, EntityType.IRON_GOLEM, true, "irongolem"),
    ITEM(21, "Item", Item.class, Group.OBJECTS, null, false, "item"),
    MAGMACUBE(22, "Magma cube", MagmaCube.class, Group.AGGRESSIVE, EntityType.MAGMA_CUBE, true, "magmacube"),
    MINECART(23, "Minecart", Minecart.class, Group.OBJECTS, null, false, "minecart", "cart"),
    MUSHROOMCOW(24, "Mooshroom", MushroomCow.class, Group.ANIMALS, EntityType.MUSHROOM_COW, true, "mooshroom", "mushroomcow", "mooshroomcow"),
    OCELOT(25, "Ocelot", Ocelot.class, Group.ANIMALS, EntityType.OCELOT, true, "ocelot"),
    PAINTING(26, "Painting", Painting.class, Group.OBJECTS, null, false, "painting"),
    PIG(27, "Pig", Pig.class, Group.ANIMALS, EntityType.PIG, true, "pig", "piglet"),
    PIGZOMBIE(28, "Pigzombie", PigZombie.class, Group.AGGRESSIVE, EntityType.PIG_ZOMBIE, true, "pigzombie", "zombiepig"),
    PLAYER(29, "Player", Player.class, Group.PLAYER, null, false, "player"),
    SHEEP(30, "Sheep", Sheep.class, Group.ANIMALS, EntityType.SHEEP, true, "sheep"),
    SILVERFISH(31, "Silverfish", Silverfish.class, Group.AGGRESSIVE, EntityType.SILVERFISH, true, "silverfish"),
    SKELETON(32, "Skeleton", Skeleton.class, Group.AGGRESSIVE, EntityType.SKELETON, true, "skeleton"),
    SLIME(33, "Slime", Slime.class, Group.AGGRESSIVE, EntityType.SLIME, true, "slime"),
    SMALLFIREBALL(34, "Small Fireball", SmallFireball.class, Group.OBJECTS, null, true, "smallfireball", "sfireball"),
    SNOWBALL(35, "Snowball", Snowball.class, Group.OBJECTS, null, true, "snowball"),
    SNOWMAN(36, "Snowman", Snowman.class, Group.COMPANIONS, EntityType.SNOWMAN, true, "snowman"),
    SPIDER(37, "Spider", Spider.class, Group.AGGRESSIVE, EntityType.SPIDER, true, "spider"),
    SQUID(38, "Squid", Squid.class, Group.ANIMALS, EntityType.SQUID, true, "squid", "lynxy"),
    TNTPRIMED(39, "TNT", TNTPrimed.class, Group.OBJECTS, null, false, "tnt", "tntprimed"),
    VILLAGER(40, "Villager", Villager.class, Group.ANIMALS, EntityType.VILLAGER, true, "villager"),
    WITCH(41, "Witch", Witch.class, Group.AGGRESSIVE, EntityType.WITCH, true, "witch"),
    WITHER(42, "Wither", Wither.class, Group.AGGRESSIVE, EntityType.WITHER, true, "wither"),
    WOLF(43, "Wolf", Wolf.class, Group.ANIMALS, EntityType.WOLF, true, "wolf"),
    ZOMBIE(44, "Zombie", Zombie.class, Group.AGGRESSIVE, EntityType.ZOMBIE, true, "zombie"),
    ENDERMITE(45, "Endermite", Endermite.class, Group.AGGRESSIVE, EntityType.ENDERMITE, true, "endermite"),
    GUARDIAN(46, "Guardian", Guardian.class, Group.AGGRESSIVE, EntityType.GUARDIAN, true, "guardian"),
    RABBIT(47, "Rabbit", Rabbit.class, Group.ANIMALS, EntityType.RABBIT, true, "rabbit"),
    ARMOR_STAND(48, "Armor Stand", ArmorStand.class, Group.OBJECTS, EntityType.ARMOR_STAND, false, "armorstand"),
    SHULKER(49, "Shulker", Shulker.class, Group.AGGRESSIVE, EntityType.SHULKER, true, "shulker"),
    SHULKER_BULLET(50, "Shulker Bullet", ShulkerBullet.class, Group.OBJECTS, EntityType.SHULKER_BULLET, true, "shulkerbullet"),
    DRAGON_FIREBALL(51, "Dragon Fireball", DragonFireball.class, Group.OBJECTS, EntityType.DRAGON_FIREBALL, true, "dragonfireball"),
    POLAR_BEAR(52, "Polar Bear", PolarBear.class, Group.ANIMALS, EntityType.POLAR_BEAR, true, "polarbear"),
    LLAMA(53, "Llama", Llama.class, Group.ANIMALS, EntityType.LLAMA, true, "llama"),
    LLAMA_SPIT(54, "LlamaSpit", LlamaSpit.class, Group.OBJECTS, EntityType.LLAMA_SPIT, false, "llamaspit"),
    EVOKER(55, "Evoker", Evoker.class, Group.AGGRESSIVE, EntityType.EVOKER, true, "evoker"),
    VINDICATOR(56, "Vindicator", Vindicator.class, Group.AGGRESSIVE, EntityType.VINDICATOR, true, "vindicator"),
    VEX(57, "Vex", Vex.class, Group.AGGRESSIVE, EntityType.VEX, true, "vex"),
    HUSK(58, "Husk", Husk.class, Group.AGGRESSIVE, EntityType.HUSK, true, "husk"),
    ZOMBIE_VILLAGER(59, "ZombieVillager", ZombieVillager.class, Group.AGGRESSIVE, EntityType.ZOMBIE_VILLAGER, true, "zombievillager"),
    ZOMBIE_HORSE(60, "ZombieHorse", ZombieHorse.class, Group.AGGRESSIVE, EntityType.ZOMBIE_HORSE, true, "zombiehorse"),
    SKELETON_HORSE(61, "SkeletonHorse", SkeletonHorse.class, Group.AGGRESSIVE, EntityType.SKELETON_HORSE, true, "skeletonhorse"),
    EVOKER_FANGS(62, "EvokerFangs", EvokerFangs.class, Group.OBJECTS, EntityType.EVOKER_FANGS, false, "evokerfangs"),
    ELDER_GUARDIAN(63, "ElderGuardian", ElderGuardian.class, Group.AGGRESSIVE, EntityType.ELDER_GUARDIAN, true, "elderguardian"),
    WITHER_SKELETON(64, "WitherSkeleton", WitherSkeleton.class, Group.AGGRESSIVE, EntityType.WITHER_SKELETON, true, "witherskeleton"),
    STRAY(65, "Stray", Stray.class, Group.AGGRESSIVE, EntityType.STRAY, true, "stray"),
    DONKEY(66, "Donkey", Donkey.class, Group.ANIMALS, EntityType.DONKEY, true, "donkey"),
    MULE(67, "Mule", Mule.class, Group.ANIMALS, EntityType.MULE, true, "mule"),
    AREA_EFFECT_CLOUD(68, "AreaEffectCloud", AreaEffectCloud.class, Group.OBJECTS, EntityType.AREA_EFFECT_CLOUD, false, "areaeffectcloud", "aec"),
    //
    // Entity Groups (start @ id 1000)
    CREATURE(1001, "Creature", Creature.class, Group.NONE, null, false, "creature"),
    ENTITY(1002, "Enitity", Entity.class, Group.NONE, null, false, "entity"),
    FLYING(1003, "Flying", Flying.class, Group.NONE, null, false, "flying"),
    MONSTER(1004, "Monster", Monster.class, Group.NONE, null, false, "monster"),
    NPCGROUP(1005, "NPC", NPC.class, Group.NONE, null, false, "npc"),
    PROJECTILE(1006, "Projectile", Projectile.class, Group.NONE, null, false, "projectile"),
    WATERMOB(1007, "Watermob", WaterMob.class, Group.NONE, null, false, "watermob"),
    VEHICLE(1008, "Vehicle", Vehicle.class, Group.NONE, null, false, "vehicle", "vehicles"),
    /** <b>Value to show that no value if found with a lookup. This value should never be used to spawn an Enity! </b>**/
    NONE(-1, "NONE", null, Group.NONE, null, false, "none");
    //
    /** Index for quick search (or saving). */
    private final int typeIndex;
    /** The name of the entity. */
    private final String name;
    /** Shows if the entity can be spawned. */
    private final boolean spawnable;
    /** Names that can be used to lookup the entity. */
    private final String[] lookupNames;
    /** The bukkit Class, which can be used to create a new instance. */
    private final Class bukkitClass;
    /** The bukkit Class, which can be used to create a new instance. */
    private final Group entityGroup;
    /** The bukkit Class, which can be used to create a new instance. */
    private final EntityType entityType;

    /**
     * Constructor for the IdpEntityType
     * @param typeIndex
     * @param name
     * @param lookupNames
     * @param bukkitClass
     * @param entityGroup
     */
    private IdpEntityType(int typeIndex, String name, Class bukkitClass, Group entityGroup, EntityType entityType, boolean spawnable, String... lookupNames) {
        this.typeIndex = typeIndex;
        this.name = name;
        this.bukkitClass = bukkitClass;
        this.entityGroup = entityGroup;
        this.lookupNames = lookupNames;
        this.spawnable = spawnable;
        this.entityType = entityType;
    }

    /**
     * The bukkit class that this entityType uses
     * @return
     */
    public Class getBukkitClass() {
        return bukkitClass;
    }

    /** Names which can be used to lookup the EntityType. */
    public String[] getLookupNames() {
        return lookupNames;
    }

    /** The group of the entity. */
    public Group getEntityGroup() {
        return entityGroup;
    }

    /** The name of the entity. */
    public String getName() {
        return name;
    }

    /** Returns the index of the entitytype, this can be used for a quick find. */
    public int getTypeIndex() {
        return typeIndex;
    }

    /** Returns if the entity can be spawned or not. */
    public boolean isSpawnable() {
        return spawnable;
    }

    /** Returns the creature type if there is any. <b>Can be null!</b>*/
    public EntityType getEntityType() {
        return entityType;
    }

    /**
     * The EntityTypeGroup
     */
    public enum Group {

        /** Group that contains animals (chicken,pig..). */
        ANIMALS,
        /** Group that contains companions (cat, wolf, snowman). */
        COMPANIONS,
        /** Group that contains entities that are hostile towards players (zombie, ghast..). */
        AGGRESSIVE,
        /** Group that contains neutral entities (player..). */
        PLAYER,
        /** Objects (arrow, tnt..). */
        OBJECTS,
        /** Entity superclass (animals, creatures, mobs..). */
        NONE
    }

    /* -----------------------------------------
     * STATICS
     * -----------------------------------------
     */
    /** Lookup map for bukkit class objects */
    private static final HashMap<Class, IdpEntityType> bukkitClassLookup;

    static {
        bukkitClassLookup = new HashMap<Class, IdpEntityType>();

        for (IdpEntityType type : values()) {
            bukkitClassLookup.put(type.getBukkitClass(), type);
        }
    }

    /**
     * Checks if this entity is living
     * @return
     */
    public boolean isLiving() {
        return LivingEntity.class.isAssignableFrom(bukkitClass);
    }

    /**
     * Looks up the entitytype with the given name.
     * @param name
     * @return NONE if none found
     */
    public static IdpEntityType lookup(String name) {
        for (IdpEntityType type : values()) {
            for (String lookupstr : type.getLookupNames()) {
                if (lookupstr.equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }
        return NONE;
    }

    /**
     * Looks for the name that looks the most like the given name. <br />
     * This also checks the normal lookup method.
     * @param name
     * @param onlyLiving if true, will only include living entities
     * @return NONE if none found
     */
    public static List<IdpEntityType> lookupMultiple(String name, boolean onlyLiving) {
        List<IdpEntityType> returnTypes = new ArrayList<IdpEntityType>();
        String[] names = name.split(",");

        for (String n : names) {
            IdpEntityType entityType = lookup(n);

            if (entityType != IdpEntityType.NONE) {
                if (!onlyLiving || entityType.isLiving()) {
                    if (!returnTypes.contains(entityType)) {
                        returnTypes.add(entityType);
                    }
                }
            }
        }

        return returnTypes;
    }

    /**
     * Looks up the entity from the given typeindex
     * @param index
     * @return
     */
    public static IdpEntityType lookup(int index) {
        for (IdpEntityType type : values()) {
            if (type.getTypeIndex() == index) {
                return type;
            }
        }
        return NONE;
    }

    /**
     * Looks up the entitytype with the given bukkitclass.
     * @param bukkitclass
     * @return
     */
    public static IdpEntityType lookup(Class<? extends Entity> bukkitclass) {
        IdpEntityType returntype = bukkitClassLookup.get(bukkitclass);
        if (returntype == null) {
            returntype = NONE;
        }

//        for (IdpEntityType EntityType : values()) {
//            if (EntityType == bukkitclass){
//                // If entity value return
//                if (EntityType.getEntityGroup() != IdpEntityType.Group.NONE)
//                    return EntityType;
//                else // group entity object, keep looking for a entity group
//                    returntype = EntityType;
//            }
//        }

        // Return the EntityType or group when nno entity value is found
        return returntype;
    }

    /**
     * Looks up the entitytype with a reference to an object.
     * @param bukkitclass
     * @return The type or NONE
     */
    public static IdpEntityType lookup(Object obj) {
        IdpEntityType returntype = NONE;

        for (IdpEntityType EntityType : values()) {
            if (EntityType.getBukkitClass().isInstance(obj)){
                // If entity value return
                if (EntityType.getEntityGroup() != IdpEntityType.Group.NONE) {
                    return EntityType;
                } else {
                    // group entity object, keep looking for a entity group
                    returntype = EntityType;
                }
            }
        }

        // Return the EntityType or group when nno entity value is found
        return returntype;
    }

}
