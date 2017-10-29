package net.innectis.innplugin.system.game.games.domination;

public enum PlayerClassType {

    NONE(0, "None", PlayerClass.class),
    WARRIOR(1, "Warrior", WarriorClass.class),
    ARCHER(2, "Archer", ArcherClass.class),
    NINJA(3, "Ninja", NinjaClass.class),
    MAGE(4, "Mage", MageClass.class),
    MEDIC(5, "Medic", MedicClass.class);
    private final int id;
    private final String name;
    private final Class<? extends PlayerClass> handle;

    PlayerClassType(int id, String name, Class<? extends PlayerClass> handle) {
        this.id = id;
        this.name = name;
        this.handle = handle;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Class<? extends PlayerClass> getHandle() {
        return handle;
    }

    public static PlayerClassType lookup(int i) {
        for (PlayerClassType type : PlayerClassType.values()) {
            if (type.getId() == i) {
                return type;
            }
        }
        return NONE;
    }

    public static PlayerClassType lookup(String name) {
        for (PlayerClassType type : PlayerClassType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return NONE;
    }
    
}
