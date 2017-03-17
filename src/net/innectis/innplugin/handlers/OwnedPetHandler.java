package net.innectis.innplugin.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.objects.EntityTraits;
import net.innectis.innplugin.objects.OwnedPets;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;

/**
 *
 * @author AlphaBlend
 */
public final class OwnedPetHandler {

    private static HashMap<String, OwnedPets> playerPets = new HashMap<String, OwnedPets>();

    private OwnedPetHandler() {
    }

    /**
     * Returns all pets by the player
     *
     * @param owner
     * @return
     */
    public static OwnedPets getPets(String owner) {
        OwnedPets pets = playerPets.get(owner);

        if (pets == null) {
            pets = new OwnedPets();
            playerPets.put(owner, pets);
        }

        return pets;
    }

    /**
     * Clears all pets of the player
     * @param owner
     */
    public static void clearPets(String owner) {
        if (playerPets.containsKey(owner)) {
            playerPets.remove(owner);
        }
    }

    /**
     * Checks to see if an animal is successfully tamed
     *
     * @param player
     * @param ent
     * @return
     */
    public static boolean tameAnimal(IdpPlayer player, LivingEntity ent) {
        OwnedPets pets = getPets(player.getName());

        if (pets.petCount() >= Configuration.MAX_PETS) {
            return false;
        }

        pets.addPet(ent);
        return true;
    }

    /**
     * Gets any nearby pets of the player
     *
     * @param player
     * @param range
     * @return
     */
    public static List<LivingEntity> getNearbyPets(IdpPlayer player, int range) {
        List<Entity> entities = player.getHandle().getNearbyEntities(range, range, range);
        List<LivingEntity> pets = new ArrayList<LivingEntity>();

        for (Entity e : entities) {
            if (e instanceof LivingEntity) {
                // TODO: Remove when armor stands are no longer living
                if (e instanceof ArmorStand) {
                    continue;
                }

                LivingEntity le = (LivingEntity) e;

                //if (le instanceof Tameable && !oa.isAnimalTamedByPlayer(le)) {
                if (le instanceof Tameable) {
                    Tameable tameable = (Tameable) le;

                    if (tameable.isTamed() && tameable.getOwner() != null && tameable.getOwner().getName() != null
                            && tameable.getOwner().getName().equalsIgnoreCase(player.getName())) {
                        pets.add(le);
                    }
                }
            }
        }

        return pets;
    }

    /**
     * Teleports all of the player's pets to their first lot, if it exists
     * @param player
     */
    public static void teleportToPlayerHome(IdpPlayer player) {
        InnectisLot lot = LotHandler.getLot(player.getName(), 1);

        if (lot != null) {
            OwnedPets oa = getPets(player.getName());

            if (oa.traitSize() > 0) {
                List<EntityTraits> entityTraits = oa.getPetTraits();

                Location loc = lot.getSpawn();
                World bukkitWorld = loc.getWorld();

                // Load chunk if it's not loaded
                if (!bukkitWorld.isChunkLoaded(loc.getChunk())) {
                    bukkitWorld.loadChunk(loc.getChunk());
                }

                // Spawn a mob based on the traits, and spawn to player's first lot
                for (EntityTraits traits : entityTraits) {
                    LivingEntity ent = (LivingEntity) bukkitWorld.spawnEntity(loc, traits.getType());
                    traits.applyTraits(ent);
                }
            }
        }
    }

    /**
     * Checks if the world is adequate for pet teleporting
     * @param world
     * @return
     */
    public static boolean isWorldAllowed(IdpWorld world) {
        switch (world.getActingWorldType()) {
            case EVENTWORLD:
            case NETHER:
                return false;
        }

        return true;
    }
    
}
