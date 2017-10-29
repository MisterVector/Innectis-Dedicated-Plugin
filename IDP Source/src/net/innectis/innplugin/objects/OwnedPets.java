package net.innectis.innplugin.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.innectis.innplugin.Configuration;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftTameableAnimal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;

/**
 * Object to keep track of an owner's pets
 *
 * @author AlphaBlend
 *
 */
public class OwnedPets {

    private List<LivingEntity> pets = new ArrayList<LivingEntity>();
    private List<EntityTraits> petTraits = new ArrayList<EntityTraits>();

    public OwnedPets() {
    }

    /**
     * Adds a pet to the list
     * @param pet
     */
    public void addPet(LivingEntity pet) {
        pets.add(pet);
    }

    /**
     * Removes a pet from the list
     * @param idx
     * @return
     */
    public LivingEntity removePet(int idx) {
        return pets.remove(idx);
    }

    /**
     * Removes a pet using its entity object
     * @param pet
     */
    public void removePet(LivingEntity pet) {
        for (Iterator<LivingEntity> it = pets.iterator(); it.hasNext();) {
            LivingEntity e = it.next();

            if (e.getUniqueId().equals(pet.getUniqueId())) {
                it.remove();
                return;
            }

        }
    }

    /**
     * Gets a list of pets
     * @return
     */
    public List<LivingEntity> getPets() {
        return pets;
    }

    /**
     * Checks if the specified entity is a pet
     * @param ent
     * @return
     */
    public boolean isPetTamed(LivingEntity checkPet) {
        for (LivingEntity pet : pets) {
            if (pet.getUniqueId().equals(checkPet.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds the specified list of entities to the pet list, first
     * checking to see if they aren't already added
     * @param tamedAnimals
     * @return A list of living entities that were added
     */
    public void addPets(List<LivingEntity> petsToAdd) {
        for (LivingEntity pet : petsToAdd) {
            if (!isPetTamed(pet)) {
                pets.add(pet);
            }
        }
    }

    /**
     * Removes any excess pets
     * @return
     */
    public int removeExcessPets() {
        int maxCount = Configuration.MAX_PETS;
        int excess = pets.size() - maxCount;
        int removed = 0;

        while (excess > 0) {
            LivingEntity ent = pets.remove(pets.size() - 1);

            Tameable tameable = (Tameable) ent;
            tameable.setTamed(false);

            excess--;
            removed++;
        }

        return removed;
    }

    /**
     * Returns the amount of pets
     * @return
     */
    public int petCount() {
        return pets.size();
    }

    /**
     * Gets the size of the pet traits
     * @return
     */
    public int traitSize() {
        return petTraits.size();
    }

    /**
     * This will kill the pets and removes their reference for all unsat
     * pets, or sitting as an option
     * @param allowSitting
     */
    public void killPets(boolean allowSitting) {
        for (Iterator<LivingEntity> it = pets.iterator(); it.hasNext();) {
            LivingEntity pet = it.next();
            // Do not allow leashed mobs
            if (!pet.isLeashed()) {
                boolean sitting = (pet instanceof CraftTameableAnimal && ((CraftTameableAnimal) pet).isSitting());

                if (!sitting || allowSitting) {
                    pet.remove();
                    it.remove();
                }
            }
        }
    }

    /**
     * Creates a list of EntityTraits for the pets that are unsat, or
     * sitting as an option
     * @param allowSitting
     */
    public void createPetTraits(boolean allowSitting) {
        for (Iterator<LivingEntity> it = pets.iterator(); it.hasNext();) {
            LivingEntity pet = it.next();
            // Make sure this is a valid entity
            if (pet.isValid()) {
                if (pet.getHealth() > 0.0D) {
                    // Do not allow leashed mobs
                    if (!pet.isLeashed()) {
                        boolean sitting = (pet instanceof CraftTameableAnimal && ((CraftTameableAnimal) pet).isSitting());

                        if (!sitting || allowSitting) {
                            EntityTraits traits = EntityTraits.getEntityTraits(pet);
                            petTraits.add(traits);
                        }
                    }
                } else {
                    // Entity is in processi of dying, so remove reference
                    it.remove();
                }
            } else {
                // Remove dead reference to this entity
                it.remove();
            }
        }
    }

    /**
     * Returns the list of pet traits
     * @return
     */
    public List<EntityTraits> getPetTraits() {
        return petTraits;
    }

    /**
     * Removes the pet traits
     */
    public void clearPetTraits() {
        petTraits.clear();
    }

    /**
     * Spawns pets from the pet traits and adds them to the pet list
     * @param loc
     */
    public void spawnPetsFromTraits(Location loc) {
        for (Iterator<EntityTraits> it = petTraits.iterator(); it.hasNext();) {
            EntityTraits traits = it.next();
            LivingEntity pet = (LivingEntity) loc.getWorld().spawnEntity(loc, traits.getType());
            traits.applyTraits(pet);

            pets.add(pet);
       }
    }

}
