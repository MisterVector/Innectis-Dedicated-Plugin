package net.innectis.innplugin.holiday;

import java.util.Random;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.idp.InnCreatureSpawnEvent;

/**
 * @author Nosliw
 *
 * Module for april fools!
 */
class AprilFoolsModule extends HolidayModule {

    // For random mob heads.
    private static final String[] STAFF_NAMES = new String[]{"Hret", "AlphaBlend", "The_Lynxy", "Nosliw"};
    private Random rand;

    public AprilFoolsModule() {
        super(HolidayType.APRIL_FOOLS);
    }

    @Override
    public void onCreatureSpawn(InnCreatureSpawnEvent event) {
        event.getHandle().getEquipment().setHelmet(getMobHead().toBukkitItemstack());
        event.getHandle().getEquipment().setHelmetDropChance(0);
    }

    /**
     * Returns a random staff mob head.
     * @param name
     * @return
     */
    private IdpItemStack getMobHead() {
        rand = new Random();
        IdpItemStack stack = new IdpItemStack(IdpMaterial.PLAYER_SKULL, 1);
        stack.getItemdata().setMobheadName(STAFF_NAMES[rand.nextInt(STAFF_NAMES.length)]);
        return stack;
    }

}