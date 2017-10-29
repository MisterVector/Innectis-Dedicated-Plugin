package net.innectis.innplugin.listeners.bukkit;

import com.destroystokyo.paper.event.entity.EntityZapEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.innectis.innplugin.system.economy.DroppedValutaOrb;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.OwnedPetHandler;
import net.innectis.innplugin.handlers.PvpHandler;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.EnchantmentType;
import net.innectis.innplugin.objects.EntityConstants;
import net.innectis.innplugin.objects.EntityTraits;
import net.innectis.innplugin.objects.OwnedPets;
import net.innectis.innplugin.objects.pojo.PlayerDeathItems;
import net.innectis.innplugin.objects.PortalDestinationResult;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.idp.InnCreatureSpawnEvent;
import net.innectis.innplugin.listeners.InnBukkitListener;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.listeners.idp.InnEntityDamageEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDeathEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerFoodLevelChangeEvent;
import net.innectis.innplugin.listeners.idp.InnProjectileHitEvent;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.data.IdpBlockData.SaveStrategy;
import net.innectis.innplugin.location.IdpDynamicWorldSettings;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.location.worldgenerators.MapType;
import net.innectis.innplugin.loggers.DeathDropLogger;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.loggers.StrangeErrorLogger;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.DoorHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.handlers.TrapdoorHandler;
import net.innectis.innplugin.objects.owned.handlers.WaypointHandler;
import net.innectis.innplugin.objects.owned.InnectisBookcase;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.InnectisSwitch;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.IdpPlayerInventory;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerBackpack;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerEffect;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.PlayerSession.PlayerStatus;
import net.innectis.innplugin.tasks.sync.PortalCooldownTask;
import net.innectis.innplugin.tasks.TaskManager;
import net.innectis.innplugin.util.LocationUtil;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

/**
 *
 * @author Hret
 *
 */
public class BukkitEntityListener implements InnBukkitListener {

    private Random randomizer;
    private DyeColor[] sheepdyecolours;
    private InnPlugin plugin;
    private IdpEntityDamageListener entityDamageListener;

    public BukkitEntityListener(InnPlugin instance) {
        plugin = instance;
        sheepdyecolours = DyeColor.values();
        randomizer = new Random();
        entityDamageListener = new IdpEntityDamageListener(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityInteract(EntityInteractEvent event) {
        IdpMaterial mat = IdpMaterial.fromBlock(event.getBlock());

        // Don't allow mobs to trample blocks.
        if (mat == IdpMaterial.FARMLAND) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityZap(EntityZapEvent event) {
        Entity entity = event.getEntity();
        Location loc = entity.getLocation();
        InnectisLot lot = LotHandler.getLot(loc);

        if (lot != null && lot.isFlagSet(LotFlagType.FARM)) {
            event.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPotionSplashEvent(PotionSplashEvent event) {
        ThrownPotion thrown = event.getEntity();

        boolean harmful = false;

        for (PotionEffect effect : event.getPotion().getEffects()) {
            PotionEffectType type = effect.getType();

            if (type.equals(PotionEffectType.SLOW)
                    || type.equals(PotionEffectType.INCREASE_DAMAGE)
                    || type.equals(PotionEffectType.HARM)
                    || type.equals(PotionEffectType.POISON)
                    || type.equals(PotionEffectType.CONFUSION)
                    || type.equals(PotionEffectType.BLINDNESS)
                    || type.equals(PotionEffectType.HUNGER)
                    || type.equals(PotionEffectType.WEAKNESS)
                    || type.equals(PotionEffectType.WITHER)
                    || type.equals(PotionEffectType.INVISIBILITY)) {
                harmful = true;
                break;
            }
        }

        if (!harmful) {
            return;
        }

        Collection<LivingEntity> affectedEntities = event.getAffectedEntities();
        List<LivingEntity> removeEntities = new ArrayList<LivingEntity>();

        for (LivingEntity entity : affectedEntities) {
            if (entity instanceof Player) {
                if (thrown.getShooter() instanceof Player) {
                    IdpPlayer damagerPlayer = plugin.getPlayer((Player) thrown.getShooter());
                    IdpPlayer affectedPlayer = plugin.getPlayer((Player) entity);

                    if (!PvpHandler.playerCanHit(damagerPlayer, affectedPlayer, true, true)) {
                        removeEntities.add(entity);
                    } else {
                        affectedPlayer.getSession().setPvPStateTime();
                        damagerPlayer.getSession().setPvPStateTime();
                    }
                }
            } else if (entity instanceof Animals || entity instanceof Villager
                    || entity instanceof Snowman || entity instanceof IronGolem
                    || entity instanceof Squid || entity instanceof Bat) {
                InnectisLot lot = LotHandler.getLot(entity.getLocation(), true);

                if (lot != null && lot.isFlagSet(LotFlagType.FARM)) {
                    removeEntities.add(entity);
                }
            }
        }

        for (LivingEntity entity : removeEntities) {
            event.setIntensity(entity, 0);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityPortalEvent(EntityPortalEvent event) {
        // Cancel here, we'll take care of this
        event.setCancelled(true);

        Entity ent = event.getEntity();

        if (ent instanceof Item || ent instanceof Monster) {
            Location location = ent.getLocation();
            InnectisLot lot = LotHandler.getLot(location, true);

            ent.remove();

            // Do not strike lightning if the entity is on a NoLightning lot
            if (lot != null && lot.isFlagSet(LotFlagType.NOLIGHTNING)) {
                return;
            }

            location.getWorld().strikeLightning(location);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPortalEvent(PlayerPortalEvent event) {
        TeleportCause cause = event.getCause();

        // Teleport already handled by a plugin, so we don't need
        // to handle this here
        if (cause == TeleportCause.PLUGIN) {
            return;
        }

        // No special handling required for end gateways
        if (cause == TeleportCause.END_GATEWAY) {
            return;
        }

        // Cancel here, we'll take care of this. Do not cancel before
        // TeleportCause.PLUGIN reasons or they won't work at all
        event.setCancelled(true);

        IdpPlayer player = plugin.getPlayer(event.getPlayer());

        // Handle end portals differently
        if (cause == TeleportCause.END_PORTAL) {
            Location loc = player.getLocation();
            Block blockBelow = loc.getBlock().getRelative(BlockFace.DOWN);
            BlockState state = blockBelow.getState();

            if (state instanceof Sign) {
                PortalDestinationResult result = LocationUtil.getPortalDestination(player, (Sign) state);

                if (result != null) {
                    if (player.teleport(result.getLocation(), TeleportType.PVP_IMMUNITY)) {
                        return;
                    }
                }
            }

            // TP back when in the end
            if (player.getWorld().getSettings().getMaptype() == MapType.THE_END) {
                player.teleport(WarpHandler.getSpawn(player.getGroup()), TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY, TeleportType.ALLOW_END_EXEMPT);
                return;
            } else {
                IdpWorldType type = player.getWorld().getActingWorldType();
                Location spawnLocation = null;
                PlayerGroup playerGroup = player.getGroup();

                // Only allow the end portal to function in the resource zone
                if (type == IdpWorldType.RESWORLD) {
                    IdpWorld endWorld = IdpWorldFactory.getWorld(IdpWorldType.THE_END);
                    spawnLocation = endWorld.getHandle().getSpawnLocation();

                    Location playerLocation = player.getLocation();

                    // Check for glitch...
                    // @todo remove when the glitch is confirmed fixed
                    if (playerLocation.getBlockX() == spawnLocation.getBlockX()
                            && playerLocation.getBlockZ() == spawnLocation.getBlockZ()) {
                        player.printError("A glitch is preventing you from teleporting to The End...");
                        spawnLocation = WarpHandler.getSpawn(playerGroup);
                    }
                } else {
                    spawnLocation = WarpHandler.getSpawn(playerGroup);
                }

                player.teleport(spawnLocation, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY, TeleportType.ALLOW_END_EXEMPT);
            }

            return;
        }

        // Dont let portals work in the end
        if (player.getWorld().getSettings().getMaptype() == MapType.THE_END) {
            return;
        }

        PortalDestinationResult result = LocationUtil.getPlayerPortalLocation(player);

        if (result != null) {
            TaskManager taskManager = plugin.getTaskManager();
            PlayerSession session = player.getSession();
            long portalCooldownTaskId = session.getPortalCooldownTaskId();

            // If a cooldown task already exists, cancel it
            if (portalCooldownTaskId > -1) {
                taskManager.removeTask(portalCooldownTaskId);
            }

            int portalTime = (player.getHandle().getGameMode() == GameMode.CREATIVE ? 4000 : 0);
            //long portalTime = (result.isInstant() ? 0 : Configuration.PORTAL_TELEPORT_TIME);
            long taskId = taskManager.addTask(new PortalCooldownTask(player, result.getLocation(), portalTime));

            session.setPortalCooldownTaskId(taskId);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Entity hitEntity = event.getHitEntity();
        Block hitBlock = event.getHitBlock();

        if (plugin.getListenerManager().hasListeners(InnEventType.PROJECTILE_HIT)) {
            InnProjectileHitEvent event2 = new InnProjectileHitEvent(projectile, hitEntity, hitBlock);
            plugin.getListenerManager().fireEvent(event2);

            if (event2.shouldTerminate()) {
                return;
            }
        }

        // If entity has custom metadata, remove it
        if (projectile.hasMetadata(EntityConstants.METAKEY_CUSTOM_ARROW)) {
            projectile.remove();
            return;
        }

        // Do nothing beyond here if the projectile is not a fish hook
        if (!(projectile instanceof FishHook)) {
            return;
        }

        ProjectileSource projectileSource = projectile.getShooter();

        // A player did not shoot this projectile so ignore
        if (!(projectileSource instanceof Player)) {
            return;
        }

        // Projectile did not hit an entity so cancel
        if (hitEntity == null) {
            return;
        }

        IdpPlayer player = plugin.getPlayer((Player) projectileSource);

        // Do nothing if entity is not living, a player or an armor stand
        if (!(hitEntity instanceof LivingEntity) || hitEntity instanceof Player
                || hitEntity instanceof ArmorStand) {
            return;
        }

        InnectisLot lot = LotHandler.getLot(hitEntity.getLocation());

        // Player uses fishing rod to capture entities
        if (hitEntity instanceof Animals || hitEntity instanceof Bat
                || hitEntity instanceof Villager || hitEntity instanceof Squid
                || hitEntity instanceof Snowman || hitEntity instanceof IronGolem
                || player.hasPermission(Permission.entity_catchentitiesall)) {
            if (player.getWorld().getSettings().getInventoryType() != InventoryType.MAIN
                    && !player.hasPermission(Permission.entity_catchentitiesoverride)) {
                player.printError("You cannot catch entities here!");
                return;
            }

            if (hitEntity.getType() == EntityType.UNKNOWN) {
                player.printError("Cannot catch this. Notify an admin!");
                return;
            }

            String entityName = hitEntity.getType().toString().toLowerCase();

            if (hitEntity instanceof Tameable) {
                Tameable tameable = (Tameable) hitEntity;

                if (tameable.getOwner() != null && !tameable.getOwner().getName().equalsIgnoreCase(player.getName())
                        && !player.hasPermission(Permission.entity_catchentitiesoverride)) {
                    player.printError("This " + entityName + " is tamed. Unable to catch!");
                    return;
                }
            }

            if (lot == null || lot.canPlayerManage(player.getName())
                    || player.hasPermission(Permission.entity_catchentitiesoverride)) {
                EntityTraits traits = EntityTraits.getEntityTraits(hitEntity);
                int result = player.getSession().addCaughtEntityTraits(traits);

                switch (result) {
                    case 2:
                        player.printError("You have already caught this " + entityName + "!");
                        hitEntity.remove();
                        break;
                    case 1:
                        boolean isTamed = (hitEntity instanceof Tameable && ((Tameable) hitEntity).getOwner() != null
                                && ((Tameable) hitEntity).getOwner().getName().equalsIgnoreCase(player.getName()));

                        hitEntity.remove();
                        player.printInfo("Caught a " + (isTamed ? "tamed " : "") + entityName + " for transporting!");
                        break;
                    case 0:
                        player.printError("Can't catch more entities. Drop existing ones off somewhere!");
                        break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityTarget(EntityTargetEvent event) {
        Entity targEntity = event.getTarget();

        if (targEntity instanceof Player) {
            IdpPlayer player = plugin.getPlayer((Player) targEntity);
            Entity entity = event.getEntity();

            if (player != null) {
                InnectisLot lot = LotHandler.getLot(player.getLocation(), true);

                if (lot != null && lot.isFlagSet(LotFlagType.NOTARGET)) {
                    event.setCancelled(true);
                    return;
                }
            // Sometimes player is null, so let's log why
            } else {
                StrangeErrorLogger serrorLogger = (StrangeErrorLogger) LogType.getLoggerFromType(LogType.STRANGE_ERRORS);
                String logMsg = "Returned null player in EntityTargetEvent! Reason: " + event.getReason()
                        + " Source: " + entity.getType() + " target: " + targEntity.getType();

                if (targEntity instanceof Player) {
                    Player bukkitPlayer = (Player) targEntity;
                    logMsg += " (name: " + bukkitPlayer.getName() + ")";
                }

                serrorLogger.log(logMsg);

                // Let it through or this event will loop
                return;
            }

            if (entity instanceof Animals) {
                // Tamed animals cannot target players, exclusing their owner
                if (entity instanceof Tameable) {
                    Tameable tameable = (Tameable) entity;

                    if (tameable.isTamed() && tameable.getOwner() != null && !tameable.getOwner().getName().equalsIgnoreCase(player.getName())) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            if (targEntity instanceof Player) {
                player = plugin.getPlayer((Player) targEntity);

                if (player.getSession().hasGodmode()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (targEntity instanceof Player) {
            IdpPlayer player = plugin.getPlayer((Player) targEntity);

            if (player.getSession().hasGodmode()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // @todo: Remove when ugly mojang hack to support armor stands is gone
        if (event.getEntityType() == EntityType.ARMOR_STAND) {
            return;
        }

        Location loc = event.getEntity().getLocation();
        InnectisLot lot = LotHandler.getLot(loc, true);
        IdpWorld world = IdpWorldFactory.getWorld(loc.getWorld().getName());

        // Check for secondairy listeners
        if (plugin.getListenerManager().hasListeners(InnEventType.ENTITY_SPAWN)) {
            InnCreatureSpawnEvent idpevent = new InnCreatureSpawnEvent(event.getEntity(), event.getSpawnReason(), loc);
            plugin.getListenerManager().fireEvent(idpevent);
            if (idpevent.isCancelled()) {
                event.setCancelled(true);
            }

            if (idpevent.shouldTerminate()) {
                return;
            }
        }

        SpawnReason reason = event.getSpawnReason();

        // Override all restrictions for custom entity spawns
        if (reason == SpawnReason.CUSTOM) {
            return;
        }

        // No mobs spawn in NoMobs lot
        if (lot != null && lot.isFlagSet(LotFlagType.NOMOBS)) {
            event.setCancelled(true);
            return;
        }

        // Prevent spawners from spawning in certain cases
        if (reason == SpawnReason.SPAWNER) {
            Block block = event.getLocation().getBlock();

            // Scan for the mob spawner block
            Block block2 = BlockHandler.getBlockNearby(block.getLocation(), IdpMaterial.MOB_SPAWNER, 6);

            if (block2 != null) {
                IdpMaterial downMaterial = IdpMaterial.fromBlock(block2.getRelative(BlockFace.DOWN));
                boolean noSpawnCondition = (downMaterial == IdpMaterial.BEDROCK);

                // no bedrock below spawner? Let's check all around the block
                if (!noSpawnCondition) {
                    int count = 0;

                    for (BlockFace face : BlockHandler.getAllSideFaces()) {
                        IdpMaterial mat = IdpMaterial.fromBlock(block2.getRelative(face));

                        if (mat == IdpMaterial.TORCH) {
                            count++;
                        }
                    }

                    // If torch on all sides, no spawn condition is met
                    if (count == 4) {
                        noSpawnCondition = true;
                    }
                }

                if (noSpawnCondition) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // Look for world specific
        switch (world.getActingWorldType()) {
            // Required since the Aether is a normal world, for now...
            case AETHER: {
                switch (event.getSpawnReason()) {
                    case SPAWNER_EGG:
                    case BUILD_IRONGOLEM:
                    case BUILD_SNOWMAN:
                    case EGG:
                    case BREEDING:
                        return;
                    default:
                        event.setCancelled(true);
                        return;
                }
            }
        }

        if (event.getEntityType() == EntityType.SHEEP) {
            Sheep sheep = (Sheep) event.getEntity();
            sheep.setColor(sheepdyecolours[randomizer.nextInt(sheepdyecolours.length)]);
        }

        if (lot != null && lot.isFlagSet(LotFlagType.NOMONSTERS)) {
            switch (event.getEntityType()) {
                case BLAZE:
                case CAVE_SPIDER:
                case CREEPER:
                case ENDERMAN:
                case ENDER_DRAGON:
                case GHAST:
                case GIANT:
                case MAGMA_CUBE:
                case PIG_ZOMBIE:
                case SILVERFISH:
                case SKELETON:
                case SPIDER:
                case SLIME:
                case ZOMBIE:
                case WITHER_SKULL:
                case WITHER:
                case WITCH: {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() == null) {
            //an entity didn't cause it (bed?) so dont allow breaks
            event.blockList().clear();
            return;
        }

        IdpWorld world = IdpWorldFactory.getWorld(event.getLocation().getWorld().getName());

        for (Iterator<Block> it = event.blockList().iterator(); it.hasNext();) {
            Block block = it.next();
            IdpMaterial mat = IdpMaterial.fromBlock(block);
            Location loc = block.getLocation();

            if (((mat == IdpMaterial.CHEST || mat == IdpMaterial.TRAPPED_CHEST) && ChestHandler.getChest(loc) != null)
                    || (mat == IdpMaterial.IRON_TRAP_DOOR && TrapdoorHandler.getTrapdoor(loc) != null)
                    || (mat == IdpMaterial.BOOKCASE && InnectisBookcase.getBookcase(loc) != null)
                    || (mat == IdpMaterial.LAPIS_LAZULI_OREBLOCK && WaypointHandler.getWaypoint(loc) != null)
                    || (mat == IdpMaterial.IRON_DOOR_BLOCK && DoorHandler.getDoor(loc) != null)
                    || (mat == IdpMaterial.LEVER && InnectisSwitch.getSwitch(loc) != null)) {
                it.remove(); //these can never be removed by explosions
            } else {
                InnectisLot lot = LotHandler.getLot(loc, true);

                boolean allowExplosion = (lot == null ? world.getActingWorldType() == IdpWorldType.RESWORLD : lot.isFlagSet(LotFlagType.DESTRUCTION));

                if (!allowExplosion) {
                    IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

                    if (blockData.isVirtualBlock()) {
                        // Set to air, so it doesn't drop anything
                        BlockHandler.setBlock(block, IdpMaterial.AIR);

                        blockData.setSaveStrategy(SaveStrategy.EAGER);
                        blockData.clear();
                    } else {
                        it.remove();
                    }
                } else {
                    // Criteria for explosions passed
                    // Only set virtual blocks to air, allow other blocks to drop
                    // TNT is unaffected, since it doesn't drop anything if exploded
                    if (mat != IdpMaterial.TNT) {
                        IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

                        // Clear IDP block data if this is a virtual block
                        if (blockData.isVirtualBlock()) {
                            blockData.setSaveStrategy(SaveStrategy.EAGER);
                            blockData.clear();
                            BlockHandler.setBlock(block, IdpMaterial.AIR);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Location loc = event.getEntity().getLocation();
        IdpWorld world = IdpWorldFactory.getWorld(loc.getWorld().getName());
        InnectisLot lot = LotHandler.getLot(loc, true);

        IdpPlayer player = plugin.getPlayer(event.getEntity());
        long lastDeath = player.getSession().getLastDeath();

        // Check if player hasn't died twice...
        if (lastDeath > 0 && lastDeath >= (System.currentTimeMillis() - 300)) {
            InnPlugin.logInfo("Player " + player.getName() + " died twice...");
            return;
        }

        // Update latest death
        player.getSession().setLastDeath(System.currentTimeMillis());
        InnPlugin.logInfo("Player " + player.getName() + " died...");

        // Remove the player's lights upon death
        if (player.getSession().hasLightsEnabled()) {
            PlayerEffect.NIGHT_VISION.removeSpecial(player);
            player.getSession().setLightsEnabled(false);
        }

        // Clear last teleport location and lot.
        player.getSession().setLastLocation(null);
        player.getSession().setLastLot(null);

        String deathMsg = event.getDeathMessage();
        if (player.getSession().getDeathMessage() != null) {
            deathMsg = player.getSession().getDeathMessage();
            player.getSession().setDeathMessage(null);
        }

        // Check for secondairy listeners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_DEATH)) {
            InnPlayerDeathEvent idpevent = new InnPlayerDeathEvent(player, event.getDrops(), deathMsg);

            plugin.getListenerManager().fireEvent(idpevent);

            if (idpevent.getShowDeathMessage()) {
                deathMsg = idpevent.getDeathMessage();
            } else {
                deathMsg = "";
            }

            event.getDrops().clear();
            for (ItemStack stack : idpevent.getDrops()) {
                player.getLocation().getWorld().dropItem(player.getLocation(), stack);
            }

            if (idpevent.shouldTerminate()) {
                player.getSession().setPlayerStatus(PlayerStatus.DEAD_PLAYER);
                event.setDeathMessage(deathMsg);
                return;
            }
        }

        player.getSession().spectatorMessage(ChatColor.DARK_GREEN, player.getName() + " has died.");

        // Remove the message from bukkit, we are handling it ourselves
        event.setDeathMessage("");

        // Show death message to players who have the setting on
        if (!StringUtil.stringIsNullOrEmpty(deathMsg)) {
            for (IdpPlayer p : plugin.getOnlinePlayers()) {
                if (p.getSession().getDeathMsgStatus()) {
                    p.printRaw(deathMsg);
                }
            }
        }

        boolean isEnderWorld = world.getSettings().getMaptype() == MapType.THE_END;

        // If a player dies with a backpack full of items, those items are dropped
        // Moderators and up don't drop items

        // The items in the backpack are not dropped if:
        // 1) They are mod+
        // 2) They're on a NoDrops lot
        if (player.hasPermission(Permission.special_usebackpack)) {
            // Skip this in the enderworld (backpack is not usable there)
            if (!isEnderWorld) {
                // Check if the backpack should be dropped
                if (!player.hasPermission(Permission.entity_deathitemsbackpack)
                        && (lot == null || !lot.isFlagSet(LotFlagType.NODROPS))
                        && player.getInventory().getType() == InventoryType.MAIN) {
                    PlayerBackpack backpack = player.getSession().getBackpack();

                    if (!backpack.isEmpty()) {
                        PlayerGroup group = player.getGroup();

                        double dropPercent = backpack.getDropPercent(group);
                        int dropAmount = backpack.getDropAmount(group);

                        if (dropAmount > 0) {
                            for (int i = 0; i < backpack.size(); i++) {
                                IdpItemStack stack = backpack.getItemAt(i);

                                if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
                                    continue;
                                }

                                world.getHandle().dropItem(loc, stack.toBukkitItemstack());
                                backpack.setItemAt(i, null);
                                dropAmount--;

                                if (dropAmount == 0) {
                                    break;
                                }
                            }

                            backpack.save();

                            player.printError("You drop " + (int) (dropPercent * 100) + "% of the contents of your backpack!");
                        }
                    }
                }
            }
        }

        // check for PvP
        if (player.getHandle().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent cause = (EntityDamageByEntityEvent) player.getHandle().getLastDamageCause();
            if (cause.getDamager() instanceof Player) {
                IdpPlayer attacker = plugin.getPlayer((Player) cause.getDamager());
                PvpHandler.playerKilledPlayer(attacker, player);
            } else if (cause.getDamager() instanceof Projectile && ((Projectile) cause.getDamager()).getShooter() instanceof Player) {
                IdpPlayer attacker = plugin.getPlayer((Player) ((Projectile) cause.getDamager()).getShooter());

                // Make sure the player did not kill themselves with their own projectile
                if (!player.getName().equalsIgnoreCase(attacker.getName())) {
                    PvpHandler.playerKilledPlayer(attacker, player);
                }
            }
        }

        // Clear the default drops, we'll handle this
        event.getDrops().clear();

        // Check if the world is a world where all items are dropped
        boolean isAllDropWorld = isEnderWorld;

        PlayerDeathItems playerDeathItems = player.calculateDeathItems(lot, randomizer, isAllDropWorld);

        if (playerDeathItems.hasItemsToDrop()) {
            for (IdpItemStack stack : playerDeathItems.getDroppedItems()) {
                if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
                    continue;
                }

                event.getDrops().add(stack.toBukkitItemstack());
            }

            // Check for specific messages
            if (isEnderWorld) {
                // No death messages in The End!
            } else {
                int deathItemsCount = playerDeathItems.getDroppedItemsCount();

                player.printError("As you die, you manage to hold on to 85% of your items.");
                player.printError("The remaining " + deathItemsCount + (deathItemsCount == 1 ? " item" : " items") + " can be found where you died.");
            }
        } else {
            // Only output this if the player has any inventory items
            if (playerDeathItems.hasItemsToKeep()) {
                player.printError("As you die, you manage to hold on to 100% of your items.");
            }
        }

        if (!event.getDrops().isEmpty()) {
            // Construct dropped message
            StringBuilder dropMessage = new StringBuilder(200);

            ItemStack dropstack;
            for (int i = 0; i < event.getDrops().size(); i++) {
                dropstack = event.getDrops().get(i);
                if (i != 0) {
                    dropMessage.append(", ");
                }
                dropMessage.append(dropstack.getAmount()).append("x ").append(dropstack.getType().name());
            }

            DeathDropLogger ddlogger = (DeathDropLogger) LogType.getLoggerFromType(LogType.DEATH_DROPS);
            ddlogger.log(player, dropMessage.toString());

            plugin.getConsole().print(ChatColor.GREEN, player.getName() + " dropped: " + dropMessage);
        }

        // Do a series of checks to see if the player can drop valutas, starting if
        // their inventory is the main inventory
        if (player.getInventory().getType() == InventoryType.MAIN
                && (lot == null || !lot.isFlagSet(LotFlagType.NODROPS))
                && !player.hasPermission(Permission.special_valutas_nodrop)) {
            boolean hasPvP = player.getSession().isPvpEnabled();
            boolean lotPvP = (lot != null && lot.isFlagSet(LotFlagType.PVP));
            boolean dropValutas = false;

            if (lotPvP) {
                // Drop valutas in a pvp lot only if the player has their setting on
                dropValutas = hasPvP;
            } else {
                dropValutas = true;
            }

            if (dropValutas) {
                TransactionObject transaction = TransactionHandler.getTransactionObject(player);
                int valutas = transaction.getValue(TransactionType.VALUTAS);

                if (valutas > 0) {
                    int droppedValutas = valutas / 2;

                    if (droppedValutas > 0) {
                        transaction.subtractValue(droppedValutas, TransactionType.VALUTAS);
                        //int totalDropped = droppedValutas;

                        // due to some weird bug, the previous code could result in players picking up more orbs than they
                        // dropped but the problem was not due to the code but some issue with minecraft itself
                        ExperienceOrb orb = (ExperienceOrb) world.getHandle().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
                        orb.setExperience(1); // Set to 1 so it shows up in PlayerExpChangeEvent
                        DroppedValutaOrb vorb = new DroppedValutaOrb(player.getColoredDisplayName(), droppedValutas);
                        orb.setMetadata(EntityConstants.METAKEY_DROPPED_VALUTAS, new FixedMetadataValue(plugin, vorb));

                        /*while (droppedValutas > 0) {
                            int dropSize = getValutaDropAmount(droppedValutas);

                            ExperienceOrb orb = (ExperienceOrb) world.getHandle().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
                            orb.setExperience(0);
                            DroppedValutaOrb vorb = new DroppedValutaOrb(player.getColoredDisplayName(), orb, dropSize, System.currentTimeMillis());
                            orb.setMetadata(EntityConstants.METAKEY_DROPPED_VALUTAS, new FixedMetadataValue(plugin, vorb));
                            droppedValutas -= dropSize;
                        }*/

                        player.printInfo("You have dropped " + ChatColor.AQUA + droppedValutas, " valuta" + (droppedValutas != 1 ? "s" : "") + "!");
                        //player.printInfo("You have dropped " + ChatColor.AQUA + totalDropped, " valuta" + (totalDropped != 1 ? "s" : "") + "!");
                    }
                }
            }
        }

        // Set inventory and save
        try {
            IdpPlayerInventory inv = new IdpPlayerInventory(player.getUniqueId(), player.getName(), player.getInventory().getType(), playerDeathItems.getItemsToKeep(),
                    IdpItemStack.fromBukkitItemStack(player.getInventory().getBukkitArmorItems()),
                    IdpItemStack.fromBukkitItemStack(player.getInventory().getBukkitOffHandItem()),
                    0, 0, -1, 20);
            if (!inv.store()) {
                InnPlugin.logError("Inventory store returned false when played died: " + player.getName());
            }
        } catch (Exception ex) {
            InnPlugin.logError("Exception storing inventory when played died: " + player.getName(), ex);
        }

        // If Hardcore is set, lets ban the player
        if (lot != null && lot.isFlagSet(LotFlagType.HARDCORE)) {
            PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName(), true);
            lot.banUser(credentials);
        }

        // Update player status
        player.getSession().setPlayerStatus(PlayerStatus.DEAD_PLAYER);

        //since we have to keep track of Item (not ItemStack) we have to drop the items ourselves
        //do not pass anything back to CraftBukkit as it will drop it regardless of cancelled state
        for (Iterator<ItemStack> it = event.getDrops().iterator(); it.hasNext();) {
            ItemStack item = it.next();
            Item item2 = event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), item);
            item2.setMetadata(EntityConstants.METAKEY_DROPPED_ITEM, new FixedMetadataValue(plugin, true));
            it.remove();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event) {
        // Player death already handled above
        if (event instanceof PlayerDeathEvent) {
            return;
        }

        Location loc = event.getEntity().getLocation();
        InnectisLot lot = LotHandler.getLot(loc, true);
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

        if (damageEvent instanceof EntityDamageByEntityEvent) {
            Entity killed = event.getEntity();
            EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) killed.getLastDamageCause();
            Entity killer = event2.getDamager();

            boolean isPlayer = (killer instanceof Player);
            boolean isPlayerProjectile = false;

            if (!isPlayer) {
                isPlayerProjectile = (killer instanceof Projectile && ((Projectile) killer).getShooter() instanceof Player);
            }

            // Player gets valutas for killing mobs in designated areas
            // Do not reward valuta if the entitiy was on the SpawnerEntity list!
            if (isPlayer || isPlayerProjectile) {
                IdpPlayer idpKiller = plugin.getPlayer((isPlayer ? (Player) killer : (Player) ((Projectile) killer).getShooter()));
                EquipmentSlot handSlot = idpKiller.getNonEmptyHand();

                // Gold sword has a 1:115 chance of having a monster drop a golden apple
                if (handSlot != null && idpKiller.getMaterialInHand(handSlot) == IdpMaterial.GOLD_SWORD
                        && event.getEntity() instanceof Monster) {
                    Random random = new Random(System.currentTimeMillis());
                    int next = random.nextInt(114);

                    String typeName = "thing";

                    if (killed.getType() != EntityType.UNKNOWN) {
                        typeName = killed.getType().getName().toLowerCase();
                    }

                    if (next == 30) {
                        idpKiller.printInfo("What luck! This " + typeName + " has given you a golden apple!");
                        event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE, 1));
                    }
                }
            }
        }

        if (lot != null && lot.isFlagSet(LotFlagType.NOMOBLOOT)) {
            event.setDroppedExp(0);
            event.getDrops().clear();
        }

        // Remove the tamed animal from the player's list
        if (event.getEntity() instanceof Tameable) {
            Tameable tamed = (Tameable) event.getEntity();

            if (tamed.isTamed() && tamed.getOwner() != null) {
                String owner = tamed.getOwner().getName();
                OwnedPets pets = OwnedPetHandler.getPets(owner);
                pets.removePet(event.getEntity());
            }
        }

        //since we have to keep track of Item (not ItemStack) we have to drop the items ourselves
        //do not pass anything back to CraftBukkit as it will drop it regardless of cancelled state
        for (Iterator<ItemStack> it = event.getDrops().iterator(); it.hasNext();) {
            ItemStack item = it.next();
            Item item2 = event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), item);
            item2.setMetadata(EntityConstants.METAKEY_DROPPED_ITEM, new FixedMetadataValue(plugin, true));
            it.remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            IdpPlayer player = plugin.getPlayer((Player) entity);
            PlayerSession session = player.getSession();
            IdpWorldType worldType = player.getWorld().getActingWorldType();

             if (worldType == IdpWorldType.AETHER && event.getCause() == DamageCause.VOID) {
                event.setCancelled(true);

                player.getHandle().setFallDistance(0);
                player.teleport(WarpHandler.getSpawn(player.getGroup()));

                return;
            }

            // Stop damage if not logged in
            if (!session.isLoggedIn()) {
                event.setCancelled(true);
                return;
            }
        }

        InnEntityDamageEvent idpDamageEvent = new InnEntityDamageEvent(event);
        IdpPlayer damagePlayer = null;
        EquipmentSlot handSlot = null;
        IdpItemStack heldItem = IdpItemStack.EMPTY_ITEM;

        switch (idpDamageEvent.getDamageType()) {
            case ENTITY_DAMAGE:
                if (entityDamageListener.onEntityDamage(idpDamageEvent)) {
                    event.setCancelled(true);
                    return;
                }

                break;
            case ENTITY_DAMAGE_BY_ENTITY:
                if (entityDamageListener.onEntityDamageByEntity(idpDamageEvent)) {
                    event.setCancelled(true);
                    return;
                }

                break;
            case ENTITY_DAMAGE_BY_PROJECTILE:
                if (entityDamageListener.onEntityDamageByProjectile(idpDamageEvent)) {
                    event.setCancelled(true);
                    return;
                }

                break;
            case ENTITY_DAMAGE_BY_PLAYER:
                if (entityDamageListener.onEntityDamageByPlayer(idpDamageEvent)) {
                    event.setCancelled(true);
                    return;
                }
                break;
            case PLAYER_DAMAGE:
                damagePlayer = idpDamageEvent.getPlayer();
                handSlot = damagePlayer.getNonEmptyHand();
                heldItem = IdpItemStack.EMPTY_ITEM;

                if (handSlot != null) {
                    heldItem = damagePlayer.getItemInHand(handSlot);
                }

                // Check for special items
                if (plugin.getSpecialItemManager().onDamageEntity(damagePlayer, handSlot, heldItem, idpDamageEvent)) {
                    if (idpDamageEvent.getDamage() == Integer.MIN_VALUE) {
                        event.setCancelled(true);
                    }
                    return;
                }

                if (entityDamageListener.onPlayerDamage(idpDamageEvent)) {
                    event.setCancelled(true);
                    return;
                }

                break;
            case PLAYER_DAMAGE_BY_ENTITY:
                damagePlayer = idpDamageEvent.getPlayer();
                handSlot = damagePlayer.getNonEmptyHand();
                heldItem = IdpItemStack.EMPTY_ITEM;

                if (handSlot != null) {
                    heldItem = damagePlayer.getItemInHand(handSlot);
                }

                // Check for special items
                if (plugin.getSpecialItemManager().onDamageEntity(damagePlayer, handSlot, heldItem, idpDamageEvent)) {
                    if (idpDamageEvent.getDamage() == Integer.MIN_VALUE) {
                        event.setCancelled(true);
                    }
                    return;
                }

                if (entityDamageListener.onPlayerDamageByEntity(idpDamageEvent)) {
                    event.setCancelled(true);
                    return;
                }

                break;
            case PLAYER_DAMAGE_BY_PROJECTILE:
                damagePlayer = idpDamageEvent.getPlayer();
                handSlot = damagePlayer.getNonEmptyHand();
                heldItem = IdpItemStack.EMPTY_ITEM;

                if (handSlot != null) {
                    heldItem = damagePlayer.getItemInHand(handSlot);
                }

                // Check itemeffects
                if (plugin.getSpecialItemManager().onDamageEntity(damagePlayer, handSlot, heldItem, idpDamageEvent)) {
                    if (idpDamageEvent.getDamage() == Integer.MIN_VALUE) {
                        event.setCancelled(true);
                    }
                    return;
                }

                if (entityDamageListener.onPlayerDamageByProjectile(idpDamageEvent)) {
                    event.setCancelled(true);
                    return;
                }

                break;
            case PLAYER_DAMAGE_BY_PLAYER:
                damagePlayer = idpDamageEvent.getPlayer();
                handSlot = damagePlayer.getNonEmptyHand();
                heldItem = IdpItemStack.EMPTY_ITEM;

                if (handSlot != null) {
                    heldItem = damagePlayer.getItemInHand(handSlot);
                }

                // Check itemeffects
                if (plugin.getSpecialItemManager().onDamageEntity(damagePlayer, handSlot, heldItem, idpDamageEvent)) {
                    if (idpDamageEvent.getDamage() == Integer.MIN_VALUE) {
                        event.setCancelled(true);
                    }
                    return;
                }

                if (entityDamageListener.onPlayerDamageByPlayer(idpDamageEvent)) {
                    event.setCancelled(true);
                    return;
                }

                break;
        }

        if (entity instanceof Player && event.getCause() != DamageCause.FALL) {
            // Mark the player as being in the damage state
            IdpPlayer player = plugin.getPlayer((Player) entity);
            player.getSession().setDamageStateTime();
        }

        // If the damage was modified, be sure to set it here!
        event.setDamage(idpDamageEvent.getDamage());

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityTame(EntityTameEvent event) {
        LivingEntity entity = event.getEntity();
        IdpPlayer player = plugin.getPlayer((Player) event.getOwner());

        String typeName = "thing";

        if (entity.getType() != EntityType.UNKNOWN) {
            typeName = entity.getType().toString().toLowerCase();
        }

        if (OwnedPetHandler.tameAnimal(player, entity)) {
            player.printInfo("This " + typeName + " is yours!");
        } else {
            player.printError("You have too many of this " + typeName + ".");
            event.setCancelled(true);

            IdpItemStack itemToReturn = null;

            if (entity instanceof Wolf) {
                itemToReturn = new IdpItemStack(IdpMaterial.BONE, 1);
            } else if (entity instanceof Ocelot) {
                itemToReturn = new IdpItemStack(IdpMaterial.RAW_FISH, 1);
            }

            // Only return if there is an item to return
            if (itemToReturn != null) {
                EquipmentSlot handSlot = player.getNonEmptyHand();
                IdpItemStack handStack = null;
                int maxStack = 0;
                int amt = 0;

                if (handSlot != null) {
                    handStack = player.getItemInHand(handSlot);

                    if (handStack.getMaterial() == itemToReturn.getMaterial()) {
                        amt = handStack.getAmount();
                        maxStack = itemToReturn.getMaterial().getMaxStackSize();
                    }
                }

                if (handStack != null && amt > 0 && amt < maxStack) {
                    int handIndex = player.getHandle().getInventory().getHeldItemSlot();
                    IdpPlayerInventory inv = player.getInventory();
                    handStack.setAmount(handStack.getAmount() + 1);
                    inv.setItemAt(handIndex, handStack);
                    inv.updateBukkitInventory();
                } else {
                    player.addItemToInventory(itemToReturn);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        Entity ent = event.getEntity();

        //dont allow entities to heal if their health is 0!
        if (ent instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) ent;
            if (entity.getHealth() <= 0) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemSpawn(ItemSpawnEvent event) {
        IdpItemStack itemstack = IdpItemStack.fromBukkitItemStack(event.getEntity().getItemStack());
        // Dont allow mobspawners to spawn
        if (itemstack.getMaterial() == IdpMaterial.MOB_SPAWNER) {
            event.setCancelled(true);
        }

        Location location = event.getLocation();
        IdpWorld world = IdpWorldFactory.getWorld(location.getWorld().getName());

        // Check dynamic world settings
        if (world.getWorldType() == IdpWorldType.DYNAMIC) {
            if (!((IdpDynamicWorldSettings) world.getSettings()).isBuildable()) {
                event.setCancelled(true);
                return;
            }
        }

        // Make sure that no item spawns while on a spleef lot
        InnectisLot lot = LotHandler.getLot(location, true);
        if (lot != null && lot.isFlagSet(LotFlagType.SPLEEF)) {
            event.setCancelled(true);
            return;
        }

//        if (location.getBlock().getIdpBlockData().isVirtualBlock()) {
//            event.setCancelled(true);
//            return;
//        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityBowShoot(EntityShootBowEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            IdpItemStack itemstack = IdpItemStack.fromBukkitItemStack(event.getBow());
            IdpPlayer player = plugin.getPlayer((Player) event.getEntity());

            // Mark arrows shot by either creative mode players, or players with
            // infinity bows as special arrows
            boolean isInfiniteBow = (player.getHandle().getGameMode() == GameMode.CREATIVE);

            if (!isInfiniteBow) {
                // Mark deletion of projectile if it was shot from an infinity bow
                for (EnchantmentType type : itemstack.getItemdata().getEnchantments().keySet()) {
                    if (type == EnchantmentType.ARROW_INFINITE) {
                        isInfiniteBow = true;
                        break;
                    }
                }
            }

            // Tag projectiles shot from infinity bow or from custom inventory
            if (isInfiniteBow) {
                event.getProjectile().setMetadata(EntityConstants.METAKEY_CUSTOM_ARROW, new FixedMetadataValue(plugin, true));
            }

            plugin.getSpecialItemManager().onBowShoot(player, EquipmentSlot.HAND, itemstack, event.getProjectile());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        InnectisLot lot = LotHandler.getLot(location, true);

        // Do not allow entities to interact with locked blocks..
        if (BlockHandler.getIdpBlockData(location).isUnbreakable()) {
            event.setCancelled(true);
            return;
        }

        // Dont allow virtual or unbreakable blocks to be changed
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        if (mat != IdpMaterial.AIR) {
            IdpBlockData blockdata = BlockHandler.getIdpBlockData(location);

            if (blockdata.isVirtualBlock() || blockdata.isUnbreakable()) {
                event.setCancelled(true);
                return;
            }
        }

        IdpWorld world = IdpWorldFactory.getWorld(location.getWorld().getName());
        boolean allowBlockChange = (lot == null ? world.getActingWorldType() == IdpWorldType.RESWORLD : lot.isFlagSet(LotFlagType.DESTRUCTION));

        switch (event.getEntityType()) {
            case FALLING_BLOCK:
                FallingBlock fblock = (FallingBlock) event.getEntity();

                // Check if not just spawned
                if (fblock.getTicksLived() > 1) {
                    // Reset the block to make sure it keeps the data values
                    Block resetBlock = world.getHandle().getBlockAt(fblock.getLocation());
                    BlockHandler.resetBlock(resetBlock, fblock);
                }

                return;
            case SHEEP:
                return; //allow sheep to do their thing
            case SILVERFISH:
                if (allowBlockChange) {
                    return;
                }

                event.setCancelled(true);
                break;
            case ENDERMAN:
            case ZOMBIE:
            case VILLAGER:
                // enderman, zombies and villagers  may manipulate blocks
                // outside lots or on destructive lots
                if (lot == null || lot.isFlagSet(LotFlagType.DESTRUCTION)) {
                    break;
                }

                event.setCancelled(true);
                break;
            case BOAT:
                boolean canDestroy = (lot == null || lot.isFlagSet(LotFlagType.DESTRUCTION));

                // If default destroy condition is not met, check if the
                // passenger is a player that has access to a lot
                if (!canDestroy) {
                    Boat boat = (Boat) event.getEntity();
                    Entity entity = boat.getPassenger();

                    if (entity instanceof Player) {
                        IdpPlayer player = plugin.getPlayer((Player) entity);

                        canDestroy = ((lot != null && lot.canPlayerAccess(player.getName()))
                                || player.hasPermission(Permission.world_build_unrestricted));
                    }
                }

                if (canDestroy) {
                    return;
                }

                event.setCancelled(true);
                break;
            default:
                // don't let any other entities change blocks anywhere unless
                // in the reszone or on a destruction lot
                if (!allowBlockChange) {
                    event.setCancelled(true);
                    return;
                }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Entity ent = event.getEntity();

        //dont allow entities to heal if their health is 0!
        if (ent instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) ent;
            if (entity.getHealth() <= 0) {
                event.setCancelled(true);
                return;
            }
        }

        if (ent.getType() == EntityType.PLAYER) {
            IdpPlayer player = plugin.getPlayer((Player) ent);

            if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_FOOD_LEVEL_CHANGE)) {
                InnPlayerFoodLevelChangeEvent event2 = new InnPlayerFoodLevelChangeEvent(player, event.getFoodLevel());
                plugin.getListenerManager().fireEvent(event2);

                if (event2.isCancelled()) {
                    event.setCancelled(true);
                }

                if (event2.shouldTerminate()) {
                    return;
                }
            }

            IdpWorldType worldType = player.getWorld().getWorldType();

            InnectisLot lot = LotHandler.getLot(player.getLocation(), true);
            boolean preventHunger = (lot != null && lot.isFlagSet(LotFlagType.NOHUNGER)
                    && !lot.isFlagSet(LotFlagType.HUNGER));

            // Check extra settings for dynamic worlds
            if (worldType == IdpWorldType.DYNAMIC) {
                IdpDynamicWorldSettings settings = (IdpDynamicWorldSettings) player.getWorld().getSettings();
                if (!settings.hasHunger()) {
                    player.resetFoodLevel();
                    event.setCancelled(true);
                    return;
                }
            } else {
                // Prevent hunger in event world unless set with the Hunger flag
                if (worldType == IdpWorldType.EVENTWORLD) {
                    if (!preventHunger) {
                        preventHunger = (lot == null);
                    }
                }
            }

            // Prevent hunger if conditions are met
            if (preventHunger) {
                // If food is not full, then set to full
                if (player.getFoodLevel() < 20) {
                    player.setFoodLevel(20);
                }

                event.setCancelled(true);
                return;
            }

            // Goldy+ can turn hunger off
            if (player.getGroup().equalsOrInherits(PlayerGroup.GOLDY)
                    && !player.getSession().canPlayerStarve()) {
                player.resetFoodLevel();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        //IdpWorldVector loc = IdpWorldVector.fromBukkitLocation(event.getVehicle().getLocation());
        //InnectisLot lot = LotHandler.getLot(loc);

        if (event.getEntity() instanceof Player) {
            IdpPlayer player = plugin.getPlayer((Player) event.getEntity());

            if (!player.getSession().isVisible()) {
                event.setCancelled(true);
                return;
            }
        }
    }


    /**
     * Gets the drop size of valutas based on the value given
     * @param valutas
     * @return
     */
    private int getValutaDropAmount(int valutas) {
        int dropSize = 0;

        if (valutas >= 100000000) {
            dropSize = 25000000;
        } else if (valutas >= 25000000) {
            dropSize = 6500000;
        } else if (valutas >= 5000000) {
            dropSize = 1250000;
        } else if (valutas >= 1000000) {
            dropSize = 250000;
        } else if (valutas >= 500000) {
            dropSize = 125000;
        } else if (valutas >= 250000) {
            dropSize = 62500;
        } else if (valutas >= 2500) {
            dropSize = 2500;
        } else {
            dropSize = valutas;
        }

        return dropSize;
    }

}
