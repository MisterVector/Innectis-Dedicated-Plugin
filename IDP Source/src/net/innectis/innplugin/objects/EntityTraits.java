package net.innectis.innplugin.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftTameableAnimal;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

/**
 *
 * @author AlphaBlend
 *
 * Holds traits of an Entity
 */
public class EntityTraits<EntityType extends Entity> {

    // Standard entity traits
    private int ticksLived = 0;
    private int fireTicks = 0;
    private org.bukkit.entity.EntityType type;
    private UUID uniqueId; // Used for tracking only - not applied

    protected EntityTraits(EntityType ent) {
        ticksLived = ent.getTicksLived();
        fireTicks = ent.getFireTicks();
        type = ent.getType();
        uniqueId = ent.getUniqueId();
    }

    /**
     * This will apply the given traits to the entity.
     * @param ent
     * The Entity to supply the traits to.
     */
    public void applyTraits(EntityType ent) {
        ent.setTicksLived(Math.max(ticksLived, 1));
        ent.setFireTicks(fireTicks);
    }

    /**
     * Returns the type of entity
     * @return
     */
    public org.bukkit.entity.EntityType getType() {
        return type;
    }

    /**
     * Returns the unique ID of the entity represented by these traits
     * @return
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    /**
     * This method will find the right entitytraits for the given entity.
     * @param ent
     * @return EntityTraits for the given entity.
     */
    public static EntityTraits getEntityTraits(Entity ent) {
        if (ent instanceof LivingEntity) {
            if (ent instanceof Villager) {
                return new VillagerTraits((Villager) ent);
            }

            if (ent instanceof Animals) {
                if (ent instanceof Horse) {
                    return new HorseTraits((Horse) ent);
                }

                if (ent instanceof Llama) {
                    return new LlamaTraits((Llama) ent);
                }

                if (ent instanceof ChestedHorse) {
                    return new ChestedHorseTraits((ChestedHorse) ent);
                }

                if (ent instanceof AbstractHorse) {
                    return new AbstractHorseTraits((AbstractHorse) ent);
                }

                if (ent instanceof Sheep) {
                    return new SheepTraits((Sheep) ent);
                }

                if (ent instanceof Pig) {
                    return new PigTraits((Pig) ent);
                }

                if (ent instanceof Ocelot) {
                    return new OcelotTraits((Ocelot) ent);
                }

                if (ent instanceof Wolf) {
                    return new WolfTraits((Wolf) ent);
                }

                if (ent instanceof Rabbit) {
                    return new RabbitTraits((Rabbit) ent);
                }

                return new AnimalTraits((Animals) ent);
            }

            return new LivingEntityTraits((LivingEntity) ent);
        }

        return new EntityTraits<Entity>(ent);
    }
}

class LivingEntityTraits<Living extends LivingEntity> extends EntityTraits<Living> {

    private double health;
    private double maxHealth;
    private String customName;
    private boolean customNameVisible;
    private ItemStack[] armourContents;
    private ItemStack heldItem;

    /**
     * @inherit
     */
    protected LivingEntityTraits(Living ent) {
        super(ent);
        health = ent.getHealth();
        maxHealth = ent.getMaxHealth();
        customName = ent.getCustomName();
        customNameVisible = ent.isCustomNameVisible();
        armourContents = ent.getEquipment().getArmorContents();
        heldItem = ent.getEquipment().getItemInMainHand();
    }

    /**
     * @inherit
     */
    @Override
    public void applyTraits(Living ent) {
        super.applyTraits(ent);
        ent.setMaxHealth(maxHealth);
        ent.setHealth(health);
        ent.setCustomName(customName);
        ent.setCustomNameVisible(customNameVisible);
        ent.getEquipment().setArmorContents(armourContents);
        ent.getEquipment().setItemInMainHand(heldItem);
    }
}

class AnimalTraits<Animal extends Animals> extends LivingEntityTraits<Animal> {

    private int age;
    private boolean adult;

    // For Tameable type
    private boolean tamed;
    private AnimalTamer owner;

    // For CraftTameableType
    private boolean sitting;

    /**
     * @inherit
     */
    protected AnimalTraits(Animal ent) {
        super(ent);
        age = ent.getAge();
        adult = ent.isAdult();

        // Since we can't have TameableTraits, let's test for it here
        if (ent instanceof Tameable) {
            Tameable tameable = (Tameable) ent;
            owner = tameable.getOwner();
            tamed = tameable.isTamed();
        }

        // This is required for isSitting()
        if (ent instanceof CraftTameableAnimal) {
            CraftTameableAnimal tameable = (CraftTameableAnimal) ent;
            sitting = tameable.isSitting();
        }
    }

    /**
     * @inherit
     */
    @Override
    public void applyTraits(Animal ent) {
        super.applyTraits(ent);
        ent.setAge(age);

        if (adult) {
            ent.setAdult();
        } else {
            ent.setBaby();
        }

        // Since we can't have TameableTraits, let's apply it here
        if (ent instanceof Tameable) {
            Tameable tameable = (Tameable) ent;
            tameable.setTamed(tamed);
            tameable.setOwner(owner);
        }

        if (ent instanceof CraftTameableAnimal) {
            CraftTameableAnimal tameable = (CraftTameableAnimal) ent;
            tameable.setSitting(sitting);
        }
    }
}

class VillagerTraits extends LivingEntityTraits<Villager> {

    private Profession profession;
    private List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
    private boolean adult;

    /**
     * @inherit
     */
    protected VillagerTraits(Villager ent) {
        super(ent);
        profession = ent.getProfession();
        adult = ent.isAdult();
        recipes = ent.getRecipes();
    }

    /**
     * @inherit
     */
    @Override
    public void applyTraits(Villager ent) {
        super.applyTraits(ent);
        ent.setProfession(profession);

        if (adult) {
            ent.setAdult();
        } else {
            ent.setBaby();
        }

        ent.setRecipes(recipes);
    }
}

class AbstractHorseTraits<AH extends AbstractHorse> extends AnimalTraits<AH> {

    private int domestic;
    private int maxDomestic;
    private double jumpStrength;
    private Inventory inventory;

    public AbstractHorseTraits(AH ent) {
        super(ent);

        domestic = ent.getDomestication();
        jumpStrength = ent.getJumpStrength();
        maxDomestic = ent.getMaxDomestication();
        inventory = ent.getInventory();
    }

    @Override
    public void applyTraits(AH ent) {
        super.applyTraits(ent);

        ent.setDomestication(domestic);
        ent.setJumpStrength(jumpStrength);
        ent.setMaxDomestication(maxDomestic);
        //ent.getInventory().setArmor(inventory.getArmor());
        ent.getInventory().setContents(inventory.getContents());
        //ent.getInventory().setSaddle(inventory.getSaddle());
    }
}

class HorseTraits extends AbstractHorseTraits<Horse> {

    private Color color;
    private Style style;

    /**
     * @inherit
     */
    protected HorseTraits(Horse ent) {
        super(ent);

        color = ent.getColor();
        style = ent.getStyle();
    }

    /**
     * @inherit
     */
    @Override
    public void applyTraits(Horse ent) {
        super.applyTraits(ent);
        ent.setColor(color);
        ent.setStyle(style);
    }
}

class ChestedHorseTraits<CH extends ChestedHorse> extends AbstractHorseTraits<CH> {

    private boolean isCarryingChest;

    public ChestedHorseTraits(CH chestedHorse) {
        super(chestedHorse);

        isCarryingChest = chestedHorse.isCarryingChest();
    }

    @Override
    public void applyTraits(CH ent) {
        super.applyTraits(ent);

        ent.setCarryingChest(isCarryingChest);
    }
}

class LlamaTraits extends ChestedHorseTraits<Llama> {

    private org.bukkit.entity.Llama.Color color;

    public LlamaTraits(Llama llama) {
        super(llama);
        this.color = llama.getColor();
    }

    @Override
    public void applyTraits(Llama llama) {
        llama.setColor(color);
    }

}

class RabbitTraits extends AnimalTraits<Rabbit> {

    private Rabbit.Type rabbitType;

    public RabbitTraits(Rabbit rabbit) {
        super(rabbit);
        this.rabbitType = rabbit.getRabbitType();
    }

    @Override
    public void applyTraits(Rabbit rabbit) {
        super.applyTraits(rabbit);
        rabbit.setRabbitType(rabbitType);
    }
}

class PigTraits extends AnimalTraits<Pig> {

    private boolean saddle;

    public PigTraits(Pig ent) {
        super(ent);

        saddle = ent.hasSaddle();
    }

    @Override
    public void applyTraits(Pig ent) {
        super.applyTraits(ent);

        ent.setSaddle(saddle);
    }
}

class SheepTraits extends AnimalTraits<Sheep> {

    private DyeColor color;
    private boolean isSheared;

    /**
     * @inherit
     */
    protected SheepTraits(Sheep ent) {
        super(ent);
        color = ent.getColor();
        isSheared = ent.isSheared();
    }

    /**
     * @inherit
     */
    @Override
    public void applyTraits(Sheep ent) {
        super.applyTraits(ent);
        ent.setColor(color);
        ent.setSheared(isSheared);
    }
}

class OcelotTraits extends AnimalTraits<Ocelot> {

    private Type catType;

    /**
     * @inherit
     */
    protected OcelotTraits(Ocelot ent) {
        super(ent);
        catType = ent.getCatType();
    }

    /**
     * @inherit
     */
    @Override
    public void applyTraits(Ocelot ent) {
        super.applyTraits(ent);
        ent.setCatType(catType);
    }
}

class WolfTraits extends AnimalTraits<Wolf> {

    DyeColor collarColor;

    /**
     * @inherit
     */
    protected WolfTraits(Wolf ent) {
        super(ent);
        collarColor = ent.getCollarColor();
    }

    /**
     * @inherit
     */
    @Override
    public void applyTraits(Wolf ent) {
        super.applyTraits(ent);
        ent.setCollarColor(collarColor);
    }

}
