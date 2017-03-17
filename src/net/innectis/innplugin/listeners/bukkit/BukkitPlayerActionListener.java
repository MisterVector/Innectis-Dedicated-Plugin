package net.innectis.innplugin.listeners.bukkit;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.innectis.innplugin.system.bans.Ban;
import net.innectis.innplugin.system.bans.BanHandler;
import net.innectis.innplugin.system.bans.BanState;
import net.innectis.innplugin.system.bans.IPBanGroup;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.system.economy.DroppedValutaOrb;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.OwnedPetHandler;
import net.innectis.innplugin.handlers.StaffMessageHandler;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.handlers.WorldHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.EntityConstants;
import net.innectis.innplugin.objects.IPLogger;
import net.innectis.innplugin.objects.OwnedPets;
import net.innectis.innplugin.objects.pojo.PlayerDeathItems;
import net.innectis.innplugin.objects.SpoofObject;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.InnBukkitListener;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.listeners.idp.InnPlayerChatEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerDropItemEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerLotLeaveEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerMoveEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerPickupItemEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerPostRespawnEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerQuitEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerRespawnEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerTeleportEvent;
import net.innectis.innplugin.location.IdpSpawnFinder;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.location.worldgenerators.MapType;
import net.innectis.innplugin.loggers.ChatLogger;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.system.mail.MailHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.externalpermissions.ExternalPermissionHandler;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.IdpPlayerInventory;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerEffect;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.renames.PlayerRenameHandler;
import net.innectis.innplugin.player.PlayerSecurity;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.PlayerSession.PlayerStatus;
import net.innectis.innplugin.player.PlayerSettings;
import net.innectis.innplugin.player.channel.ChatChannelHandler;
import net.innectis.innplugin.player.tools.miningstick.MiningStickData;
import net.innectis.innplugin.specialitem.SpecialItemType;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.tasks.sync.TabListPlayerCountTask;
import net.innectis.innplugin.util.ChatUtil;
import net.innectis.innplugin.util.ColorUtil;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.PlayerUtil;
import net.innectis.innplugin.util.StringUtil;
import net.innectis.papyrus.event.player.PlayerArmorStandCreateEvent;
import net.innectis.papyrus.event.player.PlayerPostRespawnEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionType;

/**
 *
 * @author Hret
 *
 */
public class BukkitPlayerActionListener implements InnBukkitListener {

    public static final int LOCAL_CHAT_RADIUS = 30;
    private InnPlugin plugin;
    public IdpChatListener idpChatListener;

    /**
     * playermove playerrespawn playerquit playerjoin playerchat
     *
     * @param instance
     */
    public BukkitPlayerActionListener(InnPlugin instance) {
        plugin = instance;
        idpChatListener = new IdpChatListener(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        IdpItemStack stack = IdpItemStack.fromBukkitItemStack(event.getItem());

        if (stack.getMaterial() == IdpMaterial.POTIONS) {
            ItemStack bukkitStack = stack.toBukkitItemstack();
            PotionMeta meta = (PotionMeta) bukkitStack.getItemMeta();

            // Don't allow night vision to be consumed when your portable light is enabled
            if (meta.getBasePotionData().getType() == PotionType.NIGHT_VISION
                    && player.getSession().hasLightsEnabled()) {
                player.printError("Your portable light is enabled. Cannot consume potion!");
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        PlayerSession session = player.getSession();
        IdpItemStack heldItem = player.getInventory().getItemAt(event.getNewSlot());

        // Check if the player can use their portable light
        if (player.canUsePortableLight(heldItem)) {
            PlayerEffect.NIGHT_VISION.applySpecial(player, 9000000, 1);
        } else {
            // Only remove the lights if the player has lights enabled
            if (session.hasLightsEnabled()) {
                PlayerEffect.NIGHT_VISION.removeSpecial(player);
            }
        }

        IdpMaterial mat = heldItem.getMaterial();

        // Alert player about their mining stick size
        // if it is greater than 1
        if (mat == IdpMaterial.STICK && player.hasPermission(Permission.special_builder_miningstick)) {
            MiningStickData miningStick = session.getMiningStickData();
            int miningStickSize = miningStick.getSize();

            if (miningStickSize > 1) {
                player.printInfo("Your mining stick size is: " + miningStickSize);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());

        // If not logged in
        if (!player.getSession().isLoggedIn()) {
            player.printError("You are not logged in!");
            event.setCancelled(true);
            return;
        }

        if (player.getInventory().getType() == InventoryType.NONE) { //prevent item dupe
            event.setCancelled(true);
            return;
        }

        // Secondairy Listeners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_DROP_ITEM)) {
            InnPlayerDropItemEvent idpevent = new InnPlayerDropItemEvent(player, event.getItemDrop());
            plugin.getListenerManager().fireEvent(idpevent);
            if (idpevent.isCancelled()) {
                event.setCancelled(true);
            }
            if (idpevent.shouldTerminate()) {
                return;
            }
        }

        // Don't allow dropping items in a spleef lot
        InnectisLot lot = LotHandler.getLot(player.getLocation(), true);
        if (lot != null && lot.isFlagSet(LotFlagType.SPLEEF)) {
            player.printError("You may not drop items in a spleef lot!");
            event.setCancelled(true);
            return;
        }

        event.getItemDrop().setMetadata(EntityConstants.METAKEY_DROPPED_ITEM, new FixedMetadataValue(plugin, true));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        IdpMaterial material = IdpMaterial.fromItemStack(event.getItem().getItemStack());

        // If not logged in
        if (!player.getSession().isLoggedIn()) {
            player.printError("You are not logged in!");
            event.setCancelled(true);
            return;
        }

        // If player is not alive, it cannot pick anything up
        if (player.getSession().getPlayerStatus() != PlayerStatus.ALIVE_PLAYER) {
            event.setCancelled(true);
            return;
        }

        // Secondairy Listners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_PICKUP_ITEM)) {
            InnPlayerPickupItemEvent idpevent = new InnPlayerPickupItemEvent(player, event.getItem(), event.getRemaining());
            plugin.getListenerManager().fireEvent(idpevent);
            if (idpevent.isCancelled()) {
                event.setCancelled(true);
            }
            if (idpevent.shouldTerminate()) {
                return;
            }
        }

        if (!material.canPlayerPlaceMaterial(player)) {
            event.setCancelled(true);
            return;
        }

        if (!player.getSession().canPickUpItems()) {
            event.setCancelled(true);
            return;
        }

        if (!player.getSession().isVisible()) {
            event.setCancelled(true);
            return;
        }

        InnectisLot playerLot = LotHandler.getLot(player.getLocation());
        InnectisLot itemLot = LotHandler.getLot(event.getItem().getLocation());

        //Don't let players pick up items cross-lot
        if ((itemLot == null && playerLot != null)
                || (itemLot != null && playerLot == null)
                || !((itemLot == null && playerLot == null)
                || itemLot == playerLot
                || (itemLot.getParentNotHidden() != null && itemLot.getParentNotHidden() == playerLot)
                || (playerLot.getParentNotHidden() != null && itemLot == playerLot.getParentNotHidden()))) {
            event.setCancelled(true);
            return;
        }

        //Check for 'thrown' items, prevent picking up if item wasn't thrown (unless flag is set)
        // Ignore checks if this is a pixel build area
        if (!event.getItem().hasMetadata(EntityConstants.METAKEY_DROPPED_ITEM)
                && (itemLot == null || !itemLot.isFlagSet(LotFlagType.PIXELBUILD))) {
            //only lot members can pick up
            if (itemLot != null && !itemLot.isFlagSet(LotFlagType.ITEMPICKUP)
                    && !playerLot.canPlayerAccess(player.getName())
                    && !player.hasPermission(Permission.lot_ignoreflag_itempickup)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        Entity source = event.getSource();

        if (source instanceof ExperienceOrb) {
            if (source.hasMetadata(EntityConstants.METAKEY_DROPPED_VALUTAS)) {
                // Don't actually increase experience when gaining valutas
                event.setAmount(0);

                DroppedValutaOrb vorb = (DroppedValutaOrb) source.getMetadata(EntityConstants.METAKEY_DROPPED_VALUTAS).get(0).value();
                String coloredPlayerName = vorb.getColoredPlayerName();
                int value = vorb.getAmount();

                TransactionObject transaction = TransactionHandler.getTransactionObject(player);
                transaction.addValue(value, TransactionType.VALUTAS);
                player.printInfo("Picked up " + ChatColor.AQUA + value, " valuta" + (value != 1 ? "s" : "") + " dropped by " + coloredPlayerName, "!");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        InnectisLot lot = LotHandler.getLot(event.getRespawnLocation());

        player.respawn();

        // Make sure to initialize the lot
        if (lot != null) {
            lot.onEnter(player, null, true);
        }

        // Secondairy Listners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_POST_RESPAWN)) {
            InnPlayerPostRespawnEvent idpevent = new InnPlayerPostRespawnEvent(player);
            plugin.getListenerManager().fireEvent(idpevent);
            if (idpevent.shouldTerminate()) {
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location locFrom = event.getFrom();
        Location locTo = event.getTo();

        IdpPlayer player = new IdpPlayer(plugin, event.getPlayer());
        PlayerSession session = player.getSession();

        // Don't allow movment whilst dead.
        if (session.getPlayerStatus() == PlayerStatus.DEAD_PLAYER) {
            event.setCancelled(true);
            InnPlugin.logError("Player trying to move while dead!");
            return;
        }

        // If not logged in
        if (!session.isLoggedIn()) {
            player.printError("You are not logged in!");
            locFrom.setPitch(locTo.getPitch());
            locFrom.setYaw(locTo.getYaw());
            event.setTo(locFrom);
            return;
        }

        session.addActionPerMinute();

        // MUST BE FIRST! Never under any circumstance can ANYONE go outside boundaries
        int maxMapSize = player.getWorld().getSettings().getWorldSize();

        if (Math.abs(locTo.getBlockX()) > maxMapSize || Math.abs(locTo.getBlockZ()) > maxMapSize) {
            player.printError("You have reached the end of the map!");

            Entity entity = event.getPlayer();

            // If this entity is sitting in a vehicle, leave it
            if (entity.getVehicle() != null) {
                entity.leaveVehicle();
            }

            // If this entity has a passenger, eject it
            if (entity.getPassenger() != null) {
                entity.eject();
            }

            Location lastLocation = session.getLastLocation();

            if (Math.abs(lastLocation.getBlockX()) > maxMapSize || Math.abs(lastLocation.getBlockZ()) > maxMapSize) {
                event.setTo(WarpHandler.getSpawn(player.getGroup()));
            } else {
                event.setTo(lastLocation);
            }

            return;
        }

        //dont let frozen players move
        if (player.getSession().isFrozen()) {
            locFrom.setPitch(locTo.getPitch());
            locFrom.setYaw(locTo.getYaw());
            event.setTo(locFrom);
            return;
        }

        IdpWorld world = IdpWorldFactory.getWorld(locTo.getWorld().getName());

        if (world.getSettings().getMaptype() == MapType.NETHER && locTo.getBlockY() >= 128) {
            player.printError("You were teleported to the spawn because you got stuck on top of the Nether!");
            event.setTo(WarpHandler.getSpawn(player.getGroup()));
            return;
        }

        // Check if the user has moved enough to handle it...
        if (locFrom.getBlockX() == locTo.getBlockX()
                && locFrom.getBlockZ() == locTo.getBlockZ()
                && locFrom.getBlockY() == locTo.getBlockY()) {
            //havent moved much... so dont even update the last location
            return;
        }

        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_MOVE)) {
            InnPlayerMoveEvent event2 = new InnPlayerMoveEvent(player, locFrom, locTo);
            plugin.getListenerManager().fireEvent(event2);

            if (event2.isCancelled()) {
                event.setCancelled(true);
            }

            if (event2.shouldTerminate()) {
                return;
            }

        }

        // Skip this if flymode or creative
        if (!player.getAllowFlight() && player.getHandle().getGameMode() != GameMode.CREATIVE) {
            // Very simple (not fully functional) flymod watcher
            if ((locTo.getBlockY() - locFrom.getBlockY()) > 2
                    && (locTo.getBlockY() - locTo.getWorld().getHighestBlockYAt(locTo)) > 3) {
                if (!player.hasPermission(Permission.cheats_flymod)
                        && !player.getSession().isJumped()) {
                    plugin.sendAdminMessage("fly " + player.getName(), player.getName() + " might be using a flymod!");
                    InnPlugin.logMessage(player.getColoredName() + ChatColor.YELLOW + " might be using a flymod!");
                }
            }
        }

        InnectisLot lotFrom = player.getSession().getLastLot();
        InnectisLot lotTo = LotHandler.getLot(locTo, true);

        // Check for if user is banned from a lot. Only check new lots entered if
        // player is banned
        if (lotTo != null && lotTo != lotFrom && !lotTo.canPlayerManage(player.getName()) && !lotTo.containsMember(player.getName())) {
            boolean banned = false;

            if (!player.hasPermission(Permission.lot_ban_override)) {
                banned = ((lotTo.isBanned(player.getName()) || lotTo.isBanned("%"))
                        && !lotTo.containsSafelist(player.getName())) && !player.getSession().isStaff();
            }

            if (banned) {
                player.printError("You are banned from this lot!");

                Player bukkitPlayer = player.getHandle();

                // Leave vehicle if player is in one
                if (bukkitPlayer.isInsideVehicle()) {
                    bukkitPlayer.leaveVehicle();
                }

                // Eject passenger if player has one
                if (bukkitPlayer.getPassenger() != null) {
                    bukkitPlayer.eject();
                }

                boolean useSpawnTeleport = false;
                Location targetLocation = session.getLastLocation();
                if (targetLocation == null || LotHandler.getLot(targetLocation, true) == lotTo) {
                    targetLocation = WarpHandler.getSpawn(player.getGroup());
                    useSpawnTeleport = true;
                }

                if (useSpawnTeleport) {
                    player.teleport(targetLocation, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
                    event.setCancelled(true);
                } else {
                    // If gliding, land the player on the ground
                    if (bukkitPlayer.isGliding()) {
                        bukkitPlayer.setGliding(false);
                        targetLocation.setY(world.getHandle().getHighestBlockYAt(targetLocation) + 1);
                    }

                    event.setTo(targetLocation);
                }

                return;
            }
        }

        if (lotTo != null) {
            // Check for blindness flag
            if (lotTo.isFlagSet(LotFlagType.BLINDNESS)
                    && player.getSession().getLastBlindnessEffect() < System.currentTimeMillis() - 120000) {
                session.setLastBlindnessEffect(System.currentTimeMillis());
                PlayerEffect.BLINDNESS.applySpecial(player, 20000, 10);
            }
            // Check for blindness flag
            if (lotTo.isFlagSet(LotFlagType.NOJUMP)
                    && player.getSession().getLastJumpEffect() < System.currentTimeMillis() - 120000) {
                session.setLastJumpEffect(System.currentTimeMillis());
                PlayerEffect.JUMP_BOOST.applySpecial(player, 20000, 190);
            }

            if (lotTo.isFlagSet(LotFlagType.BOUNCE)) {
                IdpMaterial mat = IdpMaterial.fromBlock(locTo.getBlock());

                if (mat == IdpMaterial.BED_BLOCK
                        || IdpMaterial.fromBlock(locTo.getBlock().getRelative(BlockFace.DOWN)) == IdpMaterial.BED_BLOCK) {
                    PlayerEffect.JUMP_BOOST.applyEffect(player, 20, 2);
                }
            }
        }

        // Auto Harvest and plant blocks if tractor is on.
        if (session.isTractorRunning()) {
            Block cropBlock = player.getLocation().getBlock();
            Block farmBlock = cropBlock.getRelative(BlockFace.DOWN);
            IdpMaterial farmMaterial = IdpMaterial.fromBlock(farmBlock);

            // Check if farm block is tilled soil or soul sand
            if (farmMaterial == IdpMaterial.FARMLAND || farmMaterial == IdpMaterial.SOUL_SAND) {
                if (BlockHandler.canBuildInArea(player, player.getLocation(), BlockHandler.ACTION_BLOCK_PLACED, false)) {
                    IdpMaterial cropMaterial = IdpMaterial.fromBlock(cropBlock);
                    boolean allowedCrop = (cropMaterial == IdpMaterial.WHEAT_BLOCK || cropMaterial == IdpMaterial.CARROT_BLOCK
                            || cropMaterial == IdpMaterial.POTATO_BLOCK || cropMaterial == IdpMaterial.NETHER_WART
                            || cropMaterial == IdpMaterial.BEETROOT_BLOCK);

                    // The player may harvest this crop
                    if (allowedCrop) {
                        byte dat = BlockHandler.getBlockData(cropBlock);
                        byte checkData = 0;
                        switch (cropMaterial) {
                            case BEETROOT_BLOCK:
                            case NETHER_WART:
                                checkData = 3;
                                break;
                            default:
                                checkData = 7;
                        }

                        // Check if crop is fully grown
                        if (dat == checkData) {
                            IdpMaterial seedMaterial = null;

                            // Determine the seed material that creates this crop
                            switch (cropMaterial) {
                                case WHEAT_BLOCK:
                                    seedMaterial = IdpMaterial.SEEDS;
                                    break;
                                case CARROT_BLOCK:
                                    seedMaterial = IdpMaterial.CARROT;
                                    break;
                                case POTATO_BLOCK:
                                    seedMaterial = IdpMaterial.POTATO;
                                    break;
                                case NETHER_WART:
                                    seedMaterial = IdpMaterial.NETHER_WART_ITEM;
                                    break;
                                case BEETROOT_BLOCK:
                                    seedMaterial = IdpMaterial.BEETROOT_SEEDS;
                                    break;
                            }

                            // Break this crop naturally
                            cropBlock.breakNaturally();

                            // Reset the crop if player has a valid seed, or set it to air
                            if (player.removeItemFromInventory(new IdpItemStack(seedMaterial, 1))) {
                                BlockHandler.setBlock(cropBlock, cropMaterial);
                            }
                        }
                    }
                }
            }
        }

        EquipmentSlot handSlot = player.getNonEmptyHand();

        if (handSlot != null && player.getItemInHand(handSlot).getItemdata().getSpecialItem() == SpecialItemType.MAGIC_FLOWER) {
            Random random = new Random();
            if (random.nextBoolean()) {
                final Block block = locFrom.getBlock();

                if (IdpMaterial.fromBlock(block) == IdpMaterial.AIR
                        && IdpMaterial.fromBlock(block.getRelative(BlockFace.DOWN)) == IdpMaterial.GRASS) {
                    List<IdpMaterial> possibleMaterials = new ArrayList<IdpMaterial>(
                            Arrays.asList(IdpMaterial.DANDELION, IdpMaterial.POPPY,
                                          IdpMaterial.BLUE_ORCHID, IdpMaterial.ALLIUM,
                                          IdpMaterial.AZURE_BLUET, IdpMaterial.RED_TULIP,
                                          IdpMaterial.ORANGE_TULIP, IdpMaterial.WHITE_TULIP,
                                          IdpMaterial.PINK_TULIP)
                            );

                    IdpMaterial randomMaterial = possibleMaterials.get(random.nextInt(possibleMaterials.size()));

                    BlockHandler.setBlock(block, randomMaterial);
                    final IdpBlockData blockData = BlockHandler.getIdpBlockData(block.getLocation());
                    blockData.setVirtualBlockStatus(true);

                    plugin.getTaskManager().addTask(new LimitedTask(RunBehaviour.SYNCED, 3000, 1) {
                        public void run() {
                            if (blockData.isVirtualBlock()) {
                                BlockHandler.setBlock(block, IdpMaterial.AIR);
                                blockData.setVirtualBlockStatus(false);
                            }
                        }
                    });
                }
            }
        }

        //Custom lot enter/leave events
        if (lotFrom != null && lotFrom != lotTo) {
            // Secondairy Listeners
            if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_LOT_LEAVE)) {
                InnPlayerLotLeaveEvent idpevent = new InnPlayerLotLeaveEvent(player, lotTo, lotFrom);
                plugin.getListenerManager().fireEvent(idpevent);
                if (idpevent.isCancelled()) {
                    event.setCancelled(true);
                }

                if (idpevent.shouldTerminate()) {
                    return;
                }
            }

            if (!lotFrom.onLeave(player, lotTo, true)) {
                event.setCancelled(true);

                return;
            }
        }

        if (lotTo != null && lotFrom != lotTo) {
            if (!lotTo.onEnter(player, lotFrom, true)) {
                event.setCancelled(true);

                return;
            }
        }

        //update the users GOOD position - MUST BE NEAR LAST IN METHOD, AND ONLY IF THEY'VE MOVED A BLOCK
        session.setLastLocation(locTo);
        session.setLastLot(lotTo);

        // Make sure the player can use their portable light (
        if (player.canUsePortableLight()) {
            PlayerEffect.NIGHT_VISION.applySpecial(player, 9000000, 1);
        } else {
            // Only remove if their lights are enabled, otherwise this could
            // interfere with normal night vision potions
            if (session.hasLightsEnabled()) {
                PlayerEffect.NIGHT_VISION.removeSpecial(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        Location loc = event.getTo();
        InnectisLot lot = LotHandler.getLot(loc);

        // Secondairy Listeners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_TELEPORT)) {
            InnPlayerTeleportEvent idpevent = new InnPlayerTeleportEvent(player, event.getCause(), loc, event.getFrom());
            plugin.getListenerManager().fireEvent(idpevent);
            if (idpevent.isCancelled()) {
                event.setCancelled(true);
            }
            if (idpevent.shouldTerminate()) {
                return;
            }
        }

        TeleportCause cause = event.getCause();

        // If trying to spectate someone while you're already spectating
        // someone else then cancel altogether
        if (cause == TeleportCause.SPECTATE) {
            PlayerSession session = player.getSession();

            if (session.isSpectating()) {
                event.setCancelled(true);

                IdpPlayer spectateTarget = session.getSpectatorTarget();
                player.printInfo("You are no longer spectating " + spectateTarget.getName());

                session.spectateTarget(null);
                return;
            }
        // Don't let chorus fruit teleport the player to a bad location
        } else if (cause == TeleportCause.CHORUS_FRUIT) {
            if (lot != null && !lot.canPlayerAccess(player.getName())
                    && !player.hasPermission(Permission.world_build_unrestricted)) {
                player.printError("Chorus fruit attempted to teleport you to a bad location.");
                event.setCancelled(true);
                return;
            }
        }

        IdpWorld world = IdpWorldFactory.getWorld(loc.getWorld().getName());
        // Unrestrict reszone
        if (world.getActingWorldType() == IdpWorldType.RESWORLD
                && (lot == null || !lot.isFlagSet(LotFlagType.NOTELEPORT))) {
            return;
        }

        // Don't allow ender pearls to be thrown in NoPearls lots
        if (event.getCause() == TeleportCause.ENDER_PEARL) {
            boolean canTeleport = lot == null
                            || !lot.isFlagSet(LotFlagType.NOPEARLS)
                            || lot.canPlayerManage(player.getName())
                            || player.hasPermission(Permission.items_enderpearl_teleport_override);

            if (!canTeleport) {
                player.printError("You may not teleport via ender pearl in this lot.");
                player.addItemToInventory(IdpMaterial.ENDER_PEARL, 1);
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        IdpPlayer player = new IdpPlayer(plugin, event.getPlayer());
        PlayerSession session = player.getSession();

        Location respawnLocation = null;

        if (player.hasPermission(Permission.special_respawn_lot)) {
            int id = session.getRespawnLotPersonalId();

            // Make sure the player has a respawn lot set
            if (id > 0) {
                InnectisLot respawnLot = LotHandler.getLot(player.getName(), id);

                // This might not be valid, so let's check
                if (respawnLot != null) {
                    IdpSpawnFinder finder = new IdpSpawnFinder(respawnLot.getSpawn());
                    respawnLocation = finder.findClosestSpawn(false);
                } else {
                    player.printError("Your respawn lot was not found for personal ID " + id + "!");
                    session.deleteRespawnLotPersonalId();
                }
            }
        }

        if (respawnLocation == null) {
            respawnLocation = WarpHandler.getSpawn(player.getGroup());
        }

        // Secondairy Listeners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_RESPAWN)) {
            InnPlayerRespawnEvent idpevent = new InnPlayerRespawnEvent(player, respawnLocation);
            plugin.getListenerManager().fireEvent(idpevent);

            if (idpevent.getRespawnLocation() != null) {
                respawnLocation = idpevent.getRespawnLocation();
            }

            if (idpevent.shouldTerminate()) {
                event.setRespawnLocation(respawnLocation);
                InnPlugin.logInfo(player.getColoredName(), " respawned!");
                return;
            }
        }

        if (player.getSession().isJailed()) {
            respawnLocation = WarpHandler.getJail().getLocation();
        }

        event.setRespawnLocation(respawnLocation);
        InnPlugin.logInfo(player.getColoredName(), " respawned!");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        PlayerSession session = player.getSession();

        if (session.isSpectating()) {
            IdpPlayer spectating = session.getSpectatorTarget();
            player.printInfo("You are no longer spectating " + spectating.getColoredDisplayName(), "!");

            session.spectateTarget(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String ipAddress = event.getAddress().getHostAddress();
        UUID playerId = event.getUniqueId();
        String playerName = event.getName();
        PlayerGroup group = PlayerGroup.getGroupOfPlayerById(playerId);
        String coloredName = group.getPrefix().getTextColor() + playerName;
        IdpPlayer testPlayer = null;

        PlayerCredentials testCredentials = PlayerCredentialsManager.getByUniqueId(playerId);

        // This player renamed themselves, so let's update their credentials
        // If they are new, don't do anything, as they have no credentials yet
        if (testCredentials != null && !playerName.equalsIgnoreCase(testCredentials.getName())) {
            String oldName = testCredentials.getName();
            String oldColoredName = group.getPrefix().getTextColor() + oldName;

            plugin.broadCastMessage(ChatColor.DARK_AQUA, "NOTICE: " + oldColoredName
                    + ChatColor.DARK_AQUA + " has renamed themselves to " + coloredName
                    + ChatColor.DARK_AQUA + ".");

            testCredentials.setName(playerName);
            testCredentials.update();

            PlayerRenameHandler.logRenamedPlayer(playerId, new Timestamp(System.currentTimeMillis()), oldName, playerName);
        }

        if ((testPlayer = plugin.getPlayer(playerId)) != null) { //player is online!
            // If IP is the same, kick right away. Else kick if AFK
            if (testPlayer.getSession().isAFK() || testPlayer.getHandle().getAddress().getAddress().getHostAddress().equals(ipAddress)) {
                testPlayer.getHandle().kickPlayer("You logged in from another location!");
                try {
                    // Get the playerfile and try to delete it
                    File playerfile = new File(IdpWorldFactory.getWorld(IdpWorldType.INNECTIS).getHandle().getWorldFolder() + File.separator + "playerdata" + File.separator + playerId.toString() + ".dat");
                    if (playerfile.exists()) {
                        InnPlugin.logError("Deleting playerfile of '" + testPlayer.getName() + "' due to double login.");
                        playerfile.delete();
                    }
                } catch (Exception ex) {
                    InnPlugin.logError("Cannot delete playerfile of " + testPlayer.getName(), ex);
                }
            } else {
                testPlayer.printInfo("Another user tried logging on to your account.");
                testPlayer.printInfo("They were rejected because you are currently logged on.");
            }
            //Disconnect connecting player - failure to do so will cause their inventory to WIPE!
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You are currently logged on!");
            return;
        }

        // Skip login check if the player is new (has no credentials)
        boolean skipLoginCheck = (testCredentials == null);

        // Only check if valid credentials (player might be new)
        if (!skipLoginCheck && !PlayerSecurity.canLogin(playerId, playerName)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "You entered your password wrong too many times...");
            return;
        }

        if (!group.equalsOrInherits(PlayerGroup.MODERATOR)
                && Configuration.isInMaintenanceMode()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "The server is in maintenance mode.");
            return;
        }

        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        IPLogger.logConnection(playerId, playerName, ipAddress, stamp);

        if (BanHandler.isWhitelisted(playerId)) {
            InnPlugin.logInfo("Ban whitelisted player " + coloredName + ChatColor.GREEN + " has joined!");
            return;
        }

        Ban ban = BanHandler.getBan(playerId);

        if (ban != null) {
            BanState state = ban.getBanState();
            boolean isIPBan = (state == BanState.IPBAN_INDEFINITE
                        || state == BanState.IPBAN_TIMED);

            switch (ban.getType()) {
                case BANNED_IP:
                case BANNED:
                    String banString = (isIPBan ? "IP banned " : "Banned ") + (ban.isIndefiniteBan() ? "indefinitely" : "for " + ban.getUnbanTimeString(true));
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, banString + "! See innectis.net!");
                    break;
                case BANNED_JOINBAN:
                case BANNED_JOINBAN_IP:
                    long durationTicks = ban.getDurationTicks();
                    ban.setNewBanStartTime(durationTicks);
                    ban.setJoinBan(false);
                    ban.save();

                    for (IdpPlayer p : plugin.getOnlinePlayers()) {
                        p.printError(coloredName, " was " + (isIPBan ? "ip" : "") + "banned by joinban for " + ban.getUnbanTimeString(true) + "!");
                    }

                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, (isIPBan ? "IP banned " : "Banned ") + "by joinban for " + ban.getUnbanTimeString(true) + "! See innectis.net!");
                    break;
                case EXPIRED:
                    BanHandler.removeBan(ban);

                    for (IdpPlayer p : InnPlugin.getPlugin().getOnlinePlayers()) {
                        p.printError(coloredName + ChatColor.RED + "'s ban has automatically expired.");
                    }
            }
        } else {
            if (BanHandler.hasIPBan(ipAddress)) {
                IPBanGroup ipGroup = BanHandler.getIPBanGroup(ipAddress);
                StringBuilder sb = new StringBuilder();

                for (PlayerCredentials pc : ipGroup.getPlayers()) {
                    String coloredPlayerName = PlayerUtil.getColoredName(pc);
                    sb.append(coloredPlayerName).append(ChatColor.YELLOW).append(", ");
                }

                String finalString = playerName + " shares an IP with the following IPBanned players: " + sb.toString().substring(0, sb.length() - 2);

                for (IdpPlayer p : plugin.getOnlineStaff(false)) {
                    p.printError("NOTICE: " + finalString);
                }

                InnPlugin.logInfo(ChatColor.RED + "NOTICE: " + finalString);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player bukkitPlayer = event.getPlayer();
        bukkitPlayer.getInventory().clear(); //clear it as we manage our own!

        //ANTI-CHEAT MESSAGES
        bukkitPlayer.sendMessage("§3 §9 §2 §0 §0 §2"); //xray
        bukkitPlayer.sendMessage("§3 §9 §2 §0 §0 §1"); //fly
        bukkitPlayer.sendMessage("§3 §9 §2 §0 §0 §3"); //radar
        bukkitPlayer.sendMessage("§f §f §2 §0 §4 §8"); //z-cheat
        bukkitPlayer.sendMessage("§f §f §4 §0 §9 §6"); //noclip
        bukkitPlayer.sendMessage("§f §f §1 §0 §2 §4"); //fly
        bukkitPlayer.sendMessage("§0§0§1§e§f"); //minimap, caves
        bukkitPlayer.sendMessage("§0§0§2§3§4§5§6§7§e§f"); //minimap, radar
        bukkitPlayer.sendMessage("§0§0§1§f§e"); //ore detector
        bukkitPlayer.sendMessage("§0§0§2§f§e"); //cave mapping
        bukkitPlayer.sendMessage("§0§0§3§4§5§6§7§8§f§e"); //player radar
        bukkitPlayer.sendMessage("§0§1§0§1§2§f§f"); //"smart" climbing
        bukkitPlayer.sendMessage("§0§1§3§4§f§f"); //"smart" swimming
        bukkitPlayer.sendMessage("§0§1§5§f§f"); //"smart" crawling
        bukkitPlayer.sendMessage("§0§1§6§f§f"); //"smart" sliding
        bukkitPlayer.sendMessage("§0§1§8§9§a§b§f§f"); //"smart" jumping
        bukkitPlayer.sendMessage("§0§1§7§f§f"); //"smart" alternative fly

        IdpPlayer player = new IdpPlayer(plugin, bukkitPlayer);
        plugin.addCachedPlayer(player);

        // Indicates a location the player might be required to teleport to
        Location relocateLocation = null;

        PlayerSession session = player.getSession();

        // Make sure their realm name is the same as what bukkit sees
        // (when a player renames themselves, sometimes this isn't set properly)
        if (!bukkitPlayer.getName().equalsIgnoreCase(session.getRealName())) {
            session.setFixedPlayerName(bukkitPlayer.getName());
        }

        player.login();

        //PREVENT CREATIVE MODE
        if (!player.hasPermission(Permission.cheats_creativemode)) {
            // Only force survival mode if player's world is not creative world
            if (player.getWorld().getWorldType() != IdpWorldType.CREATIVEWORLD) {
                bukkitPlayer.setGameMode(GameMode.SURVIVAL);
            }
        }

        if (player.getWorld().getWorldType() == IdpWorldType.RESWORLD) {
            // Players cannot stay in the resource zone, as they might get stuck otherwise
            relocateLocation = WarpHandler.getSpawn(player.getGroup());
        }

        ChatColor colour = ChatColor.WHITE;

        if (session.isNewPlayer()) {
            player.print(colour, "Welcome to Innectis!");
            player.print(colour, "We are running the " + ChatColor.DARK_AQUA + Configuration.PLUGIN_NAME
                    ," version " + Configuration.PLUGIN_VERSION, "!");
            player.print(colour, "You can type ", ChatColor.AQUA + "/help", " to get some help!");
            player.print(colour, "To get your own lot, type " + ChatColor.AQUA + "/getlot");
            player.print(colour, "To gather resources, type " + ChatColor.AQUA + "/warp reszone");

            TextComponent text = ChatUtil.createTextComponent(ChatColor.RED, "Click ");
            text.addExtra(ChatUtil.createHTMLLink("here", "http://www.tinyurl.com/innectisrules"));
            text.addExtra(ChatUtil.createTextComponent(ChatColor.RED, " to read our rules."));
            player.print(text);

            relocateLocation = WarpHandler.getSpawn(PlayerGroup.GUEST);
        } else {
            player.printInfo(colour + "Welcome back to Innectis!");
            player.printInfo(colour + "You were last seen on " + DateUtil.formatString(session.getLastLogin(), DateUtil.FORMAT_FULL_DATE));

            if (LotHandler.getLotCount(player.getName()) > 0) {
                player.printInfo(colour + "Type " + ChatColor.AQUA + "/mylot" + colour + " to visit your lot.");
            } else {
                player.printInfo(colour + "You dont have a lot yet!");
                player.printInfo(colour + "To get your own lot, type " + ChatColor.AQUA + "/getlot");
            }

            player.printInfo(colour + "To gather resources, type " + ChatColor.AQUA + "/warp reszone");
        }

        player.printInfo(colour + "Good luck, and Happy Mining!");

        if (session.isStaff()) {
            ChatChannelHandler.addGlobalListener(bukkitPlayer.getName());
            player.printInfo(colour + "You have joined all chat channels.");
        }

        if (session.isSpoofing()) {
            String getDisplayName = player.getDisplayName();
            player.getHandle().setDisplayName(getDisplayName);
            player.getHandle().setPlayerListName(getDisplayName);
        }

        if (bukkitPlayer.isDead()) {
            session.setPlayerStatus(PlayerStatus.DEAD_PLAYER);
        } else {
            if (session.isNewPlayer()) {
                player.setInventory(player.getWorld().getSettings().getInventoryType());

                // Give items to new players when they join
                player.addItemToInventory(IdpMaterial.IRON_AXE, 1);
                player.addItemToInventory(IdpMaterial.IRON_PICKAXE, 1);
                player.addItemToInventory(IdpMaterial.STONE_SWORD, 1);
                player.addItemToInventory(IdpMaterial.COOKED_CHICKEN, 4);

                session.setPlayerStatus(PlayerStatus.ALIVE_PLAYER);
            } else {
                player.respawn();
            }
        }

        if (player.getSession().isPersonalPvpEnabled()) {
            player.printInfo(ChatColor.RED + "Your personal PvP setting is enabled!");
        }

        // reset the player's external permissions
        ExternalPermissionHandler.resetPlayerPermissions(player);

        // Load the player's chat sound settings
        session.loadChatSoundSettings();

        // Set the player's tab list name to include their rank color
        // if their name is less than 16 characters
        if (player.getName().length() < 16) {
            bukkitPlayer.setPlayerListName(player.getPlayerListName());
        }

        // Only add nearby tamed animals if the player's world is main world
        if (player.getWorld().getSettings().getInventoryType() == InventoryType.MAIN) {
            List<LivingEntity> nearbyTamed = OwnedPetHandler.getNearbyPets(player, 50);

            if (nearbyTamed != null) {
                OwnedPets pets = OwnedPetHandler.getPets(player.getName());
                pets.addPets(nearbyTamed);
            }
        }

        boolean doFly = false;

        // Since flying is not remembered on login, apply flight if the
        // player has flight mode enabled
        if (session.hasFlightMode()) {
            doFly = true;
        } else {
            IdpWorld world = player.getWorld();

            if (world.getSettings().isFlightDefault()) {
                // Leave creative alone
                if (bukkitPlayer.getGameMode() != GameMode.CREATIVE) {
                    doFly = true;
                }
            }
        }

        // Apply flight if we're able to
        if (doFly) {
            Location locBelow = player.getLocation().subtract(0, 1, 0);
            IdpMaterial mat = IdpMaterial.fromBlock(locBelow.getBlock());

            // If they are in the air (no solid block below) let them hover
            boolean hover = !mat.isSolid();

            player.setAllowFlight(true, hover);
        }

        if (!session.hasQueriedMail()) {
            MailHandler.loadPlayerMailToSession(player);
            session.setQueriedMail(true);
        }

        int mailcount = session.countMail(true);

        if (mailcount > 0) {
            player.printInfo("You have " + ChatColor.AQUA + mailcount, " mail messages in your inbox!");
            player.printInfo("Type " + ChatColor.AQUA + "/mail -list", " to view them!");
        }

        boolean restricted = !player.hasPermission(Permission.special_staffrequest_viewall);
        int count = StaffMessageHandler.countUnreadStaffMessages(restricted ? player.getName() : null);

        if (count > 0) {
            if (restricted) {
                player.print(ChatColor.YELLOW, "You have " + count + " pending staff requests. View with /staffrequest -list");
            } else {
                player.printInfo("There are " + ChatColor.AQUA + count, " new staff requests! View with /staffrequest -list");
            }
        }

        // Lets sort out hidden players.
        WorldHandler.reloadHidden();

        if (!session.isVisible()) {
            player.print(ChatColor.AQUA, "You are currently invisible!");
            plugin.broadCastStaffMessageExcept(player.getName(), ChatColor.DARK_GREEN + "The player '" + ChatColor.AQUA + player.getColoredDisplayName() + ChatColor.DARK_GREEN + "' is currently invisible.", true);
        }

        // Load their personal lot ID for the respawn lot
        session.loadRespawnLotPersonalId();
        int id = session.getRespawnLotPersonalId();

        // Make sure they have a respawn lot. If not, warn them, then delete the personal ID
        if (id > 0) {
            InnectisLot testLot = LotHandler.getLot(player.getName(), id);

            if (testLot == null) {
                player.printError("Your respawn lot was not found for personal ID " + id + "!");
                session.deleteRespawnLotPersonalId();
            }
        }


        // Check if the player needs to login
        if (PlayerSecurity.hasPassword(player)) {
            session.setPlayerLoggedin(false);
        }

        if (player.getDisplayName().length() > 0) { //only broadcast if their name isnt blank!
            plugin.getServer().broadcastMessage(player.getColoredDisplayName() + ChatColor.YELLOW + " has joined the server" + (session.isNewPlayer() ? " for the first time!" : "."));
        }

        event.setJoinMessage(null);

        // Load player channel data
        session.loadPersonalChannelNumbers();
        session.joinAllChatChannels();

        InnectisLot lot = LotHandler.getLot(player.getLocation(), true);

        if (lot != null) {
            // Simulate lot enter, even if the player may not end at this destination
            lot.onEnter(player, null, true);
        }

        if (session.isJailed()) {
            player.printError("You are currently jailed.");
            relocateLocation = WarpHandler.getJail().getLocation();
        }

        // Update all players with the player count for the TAB list
        plugin.getTaskManager().addTask(new TabListPlayerCountTask(plugin));

        // If the player's location needs to be changed, teleport them to the
        // relocated location
        if (relocateLocation != null) {
            player.teleport(relocateLocation, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
        } else {
            // If player spawns in a lot they're banned from, teleport to spawn
            if (lot != null && lot.isBanned(player.getName())
                    && !lot.containsMember(player.getName())
                    && !lot.canPlayerManage(player.getName())
                    && !player.hasPermission(Permission.lot_ban_override)) {
                player.printError("You spawned in a lot you were banned from. Teleporting to saftey!");
                player.teleport(WarpHandler.getSpawn(player.getGroup()), TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
            }
        }

        //AT THIS POINT IT IS ASSUMED USER WILL NOT BE TELEPORTED ANY MORE
        //DO NOT PUT CODE UNDER THIS LINE THAT TELEPORTS/MOVES THE USER
        session.setLastLocation(player.getLocation());

        // Make sure to set the player's last lot here if logging into a lot
        if (lot != null) {
            session.setLastLot(lot);
        }

        // Do this as task to make sure its done last.
        // (Ugly way around a MC issue)
        //
        // It's listed as a task to prevent a packet mixup
        // During testing these packages would be received
        // and handled before the chat packets that where send from message above.
        // Causing the message to disappear..
        plugin.getTaskManager().addTask(new CheckPlayerTask(player));

        // New players don't need to be informed of latest server updates.
        if (session.isNewPlayer()) {
            session.updateLastPlayedVersion();
        }

        // Lets inform them of any new changes.
        if (player.hasPermission(Permission.command_player_referral) && session.getReferType() == 0) {
            player.printRaw("");
            player.print(ChatColor.AQUA, " -------- [" + ChatColor.RED + "REFERRAL", "] --------");
            player.print(ChatColor.AQUA, " -- How did you find us? We would love to know!");
            player.print(ChatColor.AQUA, " -- Type " + ChatColor.YELLOW + "/refer -refer", " to tell us!");
            player.print(ChatColor.AQUA, " -------- [" + ChatColor.RED + "REFERALL", "] --------");
            player.printRaw("");
        }

        // Lets inform them of any new changes.
        if (session.hasLastPlayedOnOlderVersion()) {
            String versionString = Configuration.PLUGIN_VERSION;

            player.print(ChatColor.AQUA, "*************** IDP UPDATE ***************");
            player.print(ChatColor.AQUA, "Your last played version: " + ChatColor.WHITE + session.getLatestVersion());
            player.print(ChatColor.AQUA, "Server's current version: " + ChatColor.WHITE + versionString);

            TextComponent text = ChatUtil.createTextComponent(ChatColor.AQUA, "Click ");
            text.addExtra(ChatUtil.createHTMLLink("here", "http://files.innectis.net/changelog/index.htm#" + versionString));
            text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to read the changelog."));
            player.print(text);

            text = ChatUtil.createTextComponent(ChatColor.AQUA, "Or click ");
            text.addExtra(ChatUtil.createHTMLLink("here", "http://forums.innectis.net/index.php/board,37.0.html"));
            text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to leave feedback."));
            player.print(text);

            player.print(ChatColor.AQUA, "*************** IDP UPDATE ***************");

            session.updateLastPlayedVersion();
        }

        // Join event for holidays!
        plugin.getHolidaymanager().onPlayerJoin(player);

        // Finally, set their last login time
        session.setLastLogin();
    }

    /**
     * Simple task that will check the security status of a player after 5ms.
     */
    private class CheckPlayerTask extends LimitedTask {
        private IdpPlayer player;

        public CheckPlayerTask(IdpPlayer player) {
            super(RunBehaviour.ASYNC, 5, 1);
            this.player = player;
        }

        public void run() {
            PlayerSecurity.checkPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return; //something else canceled it, eg NoCheat, so lets not process
        }
        String[] args = event.getMessage().substring(1).split(" ");
        ArrayList<String> args2 = new ArrayList<String>(args.length - 1);
        if (args.length > 1) {
            // Copy the arguments and filter out the empty ones
            for (int i = 1; i < args.length; i++) {
                if (!StringUtil.stringIsNullOrEmpty(args[i])) {
                    args2.add(args[i]);
                }
            }
        }

        if (plugin.onCommand(event.getPlayer(), null, args[0], args2.toArray(new String[args2.size()]))) {
            event.setCancelled(true); //cancel only if we handled it
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerArmorStandCreate(PlayerArmorStandCreateEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        Location loc = player.getLocation();

        if (!BlockHandler.canBuildInArea(player, loc, BlockHandler.ACTION_BLOCK_PLACED, false)) {
            player.printError("You cannot place an armor stand here!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        Location loc = player.getLocation();
        InnectisLot lot = LotHandler.getLot(loc, true);

        if (lot != null && !lot.canPlayerAccess(player.getName())
                && !player.hasPermission(Permission.world_build_unrestricted)) {
            event.setCancelled(true);

            player.printError("This is not your armor stand!");
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onTabComplete(TabCompleteEvent event) {
        String msg = event.getBuffer();

        // Only tab-complete commands
        if (!msg.startsWith("/")) {
            return;
        }

        CommandSender sender = event.getSender();

        // Only handle tab-completion for players
        if (sender instanceof Player) {
            IdpPlayer player = plugin.getPlayer(sender.getName());

            // Don't filter commands if player is staff
            if (player.getSession().isStaff()) {
                return;
            }

            List<String> completions = plugin.commandManager.onTabComplete(player, msg.substring(1));

            // Check for any completions, as this list may be empty
            if (completions.size() > 0) {
                event.setCompletions(completions);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        try {
            if (event.isCancelled()) {
                return; //something else canceled it, eg NoCheat, so lets not process
            }
            //ignore messages spit out of modloader/we cui
            if (event.getMessage().equalsIgnoreCase("u00a74u00a75u00a73u00a74v|1")) {
                event.setCancelled(true);
                return;
            }

            IdpPlayer player = new IdpPlayer(plugin, event.getPlayer());
            PlayerSession session = player.getSession();

            // If not logged in, not allowed to talk.
            if (!session.isLoggedIn()) {
                player.printError("You are not logged in!");
                event.setCancelled(true);
                return;
            }

            // Check if the player has a chatinjector
            if (session.hasInjector()) {
                session.getChatInjector().onChat(player, event.getMessage());
                event.setCancelled(true);
                return;
            }

            if (session.isSpoofing()) {
                SpoofObject spoof = session.getSpoofObject();

                // Don't allow chat if hidden
                if (spoof.isHidden()) {
                    player.printError("Unable to speak, you are hidden!");
                    event.setCancelled(true);
                    return;
                }
            }

            //add 10 actions for chat
            for (int i = 0; i < 10; i++) {
                session.addActionPerMinute();
            }

            String message = event.getMessage();
            //get rid of excessive spaces
            while (message.contains("     ")) {
                message = message.replace("     ", "  ");
            }
            message = message.replaceAll("(?i)\u00A7[0-F]", ""); // Strip any colors
            ChatColor msgColor = ChatColor.WHITE;

            if (message.startsWith("!!") && player.hasPermission(Permission.chat_exclaim)) {
                message = message.substring(2);

                // Resulting substring will be null if base message is !!
                if (message.isEmpty()) {
                    player.printError("What do you want to say?");
                    event.setCancelled(true);
                    return;
                }

                msgColor = ChatColor.RED;
            }

            if (player.hasPermission(Permission.chat_usecolours)) {
                message = ChatColor.parseChatColor(message);
            }

            if (session.getSpiked() != 0) {
                message = message.replace("s", "sh");
                message = message.replace("S", "Sh");
                message = message.replace(".", "");
                message = message.replace("?", "");
                message = message.replace("!", "");
                message += "?!";

                if (message.length() > 5) {
                    int random = (new Random().nextInt(message.length()));
                    message = message.substring(0, random) + " *hick* " + message.substring(random);
                }
            }

            long muteTicks = session.getRemainingMuteTicks();

            // Secondairy listeners
            if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_CHAT)) {
                InnPlayerChatEvent idpevent = new InnPlayerChatEvent(player, message, msgColor);
                plugin.getListenerManager().fireEvent(idpevent);

                if (idpevent.isCancelled()) {
                    event.setCancelled(true);
                }

                if (idpevent.shouldTerminate()) {
                    // Still log it, if not overridden
                    if (!idpevent.isCancelled()) {
                        ChatLogger chatLogger = (ChatLogger) LogType.getLoggerFromType(LogType.CHAT);
                        chatLogger.logChat(player.getName(), "(UNKNOWN) " + idpevent.getChatMessage().getUncensoredMessage());
                        InnPlugin.logMessage(player.getPrefixAndDisplayName() + msgColor + idpevent.getChatMessage().getUncensoredMessage());
                    }
                    event.setMessage(idpevent.getMessage());
                    return;
                }
            }

            if (muteTicks != 0) {
                idpChatListener.playerMutedChat(player, message, msgColor);
            } else {
                boolean mainChat = !(message.startsWith("@") || message.startsWith("##"));
                boolean lotChat = false;

                if (!mainChat) {
                    int findLength = (message.startsWith("##") ? 2 : 1);

                    if (message.length() == findLength) {
                        player.printError("What do you want to say?");
                        event.setCancelled(true);
                        return;
                    }

                    if (findLength == 2) {
                        lotChat = true;
                    }

                    message = message.substring(findLength);
                }

                // Ceck if the chat has been inverted
                if (session.hasSetting(PlayerSettings.INVERT_LOCALCHAT)) {
                    // Allow lot chat regardless of invert local setting
                    if (!lotChat) {
                        mainChat = !mainChat;
                    }
                }

                if (mainChat) {
                    idpChatListener.playerGlobalChat(player, message, msgColor);
                } else {
                    if (lotChat) {
                        InnectisLot lot = LotHandler.getLot(player.getLocation());

                        if (lot != null) {
                            msgColor = ChatColor.YELLOW;
                            idpChatListener.playerLotChat(player, lot, message, msgColor);
                        } else {
                            player.printError("No lot found. Cannot use lot chat!");
                        }
                    } else {
                        msgColor = ChatColor.GRAY;
                        idpChatListener.playerLocalChat(player, message, msgColor, false);
                    }
                }
            }

            event.setCancelled(true);
        } catch (Exception ex) {
            // Always catch uncaught exceptions as it can cause crashes
            // Fixes bug #720
            InnPlugin.logError("Uncaught exception in chatlistener! ", ex);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {
        final IdpPlayer player = plugin.getPlayer(event.getPlayer());
        final IdpMaterial material = player.getMaterialInHand(EquipmentSlot.HAND);

        // Lets automatically replace this tool, straight after the item breaks!
        // It will replace it with a tool of the same type. e.g. Stone Shovel -> Iron Shovel
        // This way, it still plays the item break effect ;)
        this.plugin.getTaskManager().addTask(new LimitedTask(RunBehaviour.SYNCED, 1, 1) {
            @Override
            public void run() {
                IdpItemStack newTool = player.getFirstItemstack(material);
                if (newTool == null) {
                    for (IdpMaterial toolMaterial : material.getOtherToolTypes()) {
                        newTool = player.getFirstItemstack(toolMaterial);
                        if (newTool != null) {
                            break;
                        }
                    }
                }
                if (newTool != null) {
                    player.setItemInHand(EquipmentSlot.HAND, newTool);
                    player.updateInventory();
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBeacon(BeaconEffectEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());

        InnectisLot beaconlot = LotHandler.getLot(event.getBlock().getLocation(), true);
        InnectisLot playerlot = LotHandler.getLot(player.getLocation(), true);

        // Both in wild, ignore
        if (beaconlot == null && playerlot == null) {
            return;
        }

        // One is inside a lot, cancel
        if (beaconlot == null || playerlot == null) {
            event.setCancelled(true);
            return;
        }

        // Check for same lot or within the beaconlot
        if (beaconlot != playerlot && !beaconlot.isAtLocation(player.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        event.setQuitMessage(onPlayerLeave(player));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerKick(PlayerKickEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        event.setLeaveMessage(onPlayerLeave(player));
    }

    public String onPlayerLeave(IdpPlayer player) {
        boolean hidden = false;
        String quitmessage = null;
        InnectisLot lot = LotHandler.getLot(player.getLocation(), true);

        if (player.getHandle().getVehicle() != null) {
            // If the player is in a vehicle while logging out, make sure to eject
            // them if they are in a lot they do not own or operate, to prevent taking a vehicle
            if (lot != null && !(lot.canPlayerManage(player.getName())
                    || player.hasPermission(Permission.world_build_unrestricted))) {
                player.getHandle().leaveVehicle();
            }
        }

        if (lot != null && lot.isFlagSet(LotFlagType.NOMEMBERLOGOUTSPAWN)
                && !lot.canPlayerAccessIgnoreGeneric(player.getName())
                && !player.hasPermission(Permission.lot_ignoreflag_nomemberlogoutspawn)) {
            player.teleport(WarpHandler.getSpawn(), TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
        }

        // Make sure the region is removed when the player leaves
        // (a dupe bug may occur otherwise)
        player.setRegion(null);

        PlayerSession session = player.getSession();

        // Restore original hunger if logging out of a NoHunger lot
        if (lot != null && lot.isFlagSet(LotFlagType.NOHUNGER)) {
            double oldHealthLevel = session.getOldHealthLevel();
            int oldFoodLevel = session.getOldFoodLevel();

            player.setHealth(oldHealthLevel);
            player.setFoodLevel(oldFoodLevel);
        }

        // If their flight mode is enabled, check if they're allowed
        if (session.hasFlightMode() && !player.hasPermission(Permission.special_noflight_override)) {
            session.setFlightMode(false);
        }

        if (session.isSpoofing()) {
            SpoofObject spoof = session.getSpoofObject();
            hidden = spoof.isHidden();
        }

        if (session.isInPvPState() && player.getHealth() < 18) {
            player.dealDamage(1000);
        }

        // If player has god mode without permission, remove it
        if (session.hasGodmode() && !player.hasPermission(Permission.special_god_allow)) {
            session.setGodmode(false);
        }

        if (session.isSpectating()) {
            IdpPlayer spectating = session.getSpectatorTarget();
            player.printInfo("You are no longer spectating " + spectating.getColoredDisplayName(), "!");

            session.spectateTarget(null);
        }

        session.kickSpectators(player.getName() + " left the server.");

        // Spoofed hidden players don't show logout message
        if (!hidden) {
            quitmessage = player.getColoredDisplayName() + ChatColor.YELLOW + " left the server.";
        }

        // Apply standard death logic if player logs out while in The End
        // but do not treat it as an all drop world
        if (player.getWorld().getSettings().getMaptype() == MapType.THE_END) {
            PlayerDeathItems playerDeathItems = player.calculateDeathItems(lot, new Random(), false);

            if (playerDeathItems.hasItemsToDrop()) {
                IdpPlayerInventory inventory = player.getInventory();
                Location loc = player.getLocation();
                World world = loc.getWorld();

                for (IdpItemStack stack : playerDeathItems.getDroppedItems()) {
                    if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
                        continue;
                    }

                    world.dropItem(loc, stack.toBukkitItemstack());
                }

                inventory.setItems(playerDeathItems.getItemsToKeep());
                inventory.updateBukkitInventory();
            }
        }

        // Check for secondairy listeners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_QUIT)) {
            InnPlayerQuitEvent idpevent = new InnPlayerQuitEvent(player, quitmessage);
            plugin.getListenerManager().fireEvent(idpevent);

            if (!StringUtil.stringIsNullOrEmpty(idpevent.getQuitMessage())) {
                quitmessage = idpevent.getQuitMessage();
            }
        }

        player.logout();

        return quitmessage;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());

        // Allow change in creative world
        if (player.getWorld().getWorldType() == IdpWorldType.CREATIVEWORLD) {
            return;
        }

        if (event.getNewGameMode() == GameMode.CREATIVE && !player.hasPermission(Permission.cheats_creativemode)) {
            plugin.sendAdminMessage(null, event.getPlayer().getName() + " tried to enter Creative mode!");
            player.printError("You are not authorized to do that!");
            InnPlugin.logError(event.getPlayer().getName() + " tried to enter Creative mode!");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
        Entity entity = event.getEntity();
        IdpPlayer player = plugin.getPlayer(event.getPlayer());

        if (entity instanceof Tameable) {
            Tameable tameable = (Tameable) entity;
            AnimalTamer owner = tameable.getOwner();

            if (owner != null && !owner.getName().equalsIgnoreCase(player.getName())
                    && !player.hasPermission(Permission.entity_canleadanywhere)) {
                player.printError("You cannot leash this tamed " + entity.getType() + "!");
                event.setCancelled(true);
                return;
            }
        }

        InnectisLot lot = LotHandler.getLot(entity.getLocation());

        if (lot != null && lot.isFlagSet(LotFlagType.FARM)
                && !lot.canPlayerManage(player.getName())
                && !player.hasPermission(Permission.entity_canleadanywhere)) {
            player.printError("Cannot leash entities here!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerUnleashEntity(PlayerUnleashEntityEvent event) {
        Entity entity = event.getEntity();
        IdpPlayer player = plugin.getPlayer(event.getPlayer());

        if (entity instanceof Tameable) {
            Tameable tameable = (Tameable) entity;
            AnimalTamer owner = tameable.getOwner();

            if (owner != null && !owner.getName().equalsIgnoreCase(player.getName())
                    && !player.hasPermission(Permission.entity_canleadanywhere)) {
                player.printError("You cannot unleash this tamed " + entity.getType() + "!");
                event.setCancelled(true);
                return;
            }
        }

        InnectisLot lot = LotHandler.getLot(entity.getLocation());

        if (lot != null && lot.isFlagSet(LotFlagType.FARM)
                && !lot.canPlayerManage(player.getName())
                && !player.hasPermission(Permission.entity_canleadanywhere)) {
            player.printError("Cannot unleash entities here!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        WorldHandler.onWorldSwitch(new IdpWorld(event.getFrom()), new IdpWorld(event.getPlayer().getWorld()), plugin.getPlayer(event.getPlayer()), plugin);
    }

}
