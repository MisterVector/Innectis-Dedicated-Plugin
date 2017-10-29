package net.innectis.innplugin.player;

import com.destroystokyo.paper.Title;
import net.innectis.innplugin.location.IdpRegion;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.location.IdpSpawnFinder;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.system.warps.IdpWarp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.system.economy.ValutaSinkManager;
import net.innectis.innplugin.external.api.interfaces.IWorldEditIDP;
import net.innectis.innplugin.external.LibraryType;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.OwnedPetHandler;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.handlers.WorldHandler;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.IdpException;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.ChatSoundSetting;
import net.innectis.innplugin.objects.DirectionType;
import net.innectis.innplugin.objects.EnchantmentType;
import net.innectis.innplugin.objects.OwnedPets;
import net.innectis.innplugin.objects.pojo.PlayerDeathItems;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.objects.ViewedPlayerInventoryData;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.items.IdpItem;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.listeners.idp.InnPlayerLotLeaveEvent;
import net.innectis.innplugin.objects.owned.FlagType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.channel.ChatChannelHandler;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.tasks.sync.TabListPlayerCountTask;
import net.innectis.innplugin.util.LocationUtil;
import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.util.ChatUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class IdpPlayer extends IdpCommandSender<CraftPlayer> {

    /** The amount of ticks the player can stay dead before kicking them for inactivity */
    private static final int INACTIVITY_KICKTICKS = 4;
    //
    private volatile Player player;
    private PlayerSession session = null;

    public IdpPlayer(InnPlugin plugin, Player player) {
        super(plugin, (CraftPlayer) player);
        this.player = player;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IdpPlayer)) {
            return false;
        }

        IdpPlayer player = (IdpPlayer) obj;

        return (player.getName().equalsIgnoreCase(getName()));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.player != null ? this.player.hashCode() : 0);
        return hash;
    }

    // <editor-fold defaultstate="collapsed" desc="Player Basic">
    /**
     * @inherit
     */
    @Override
    public CommandSenderType getType() {
        return CommandSenderType.PLAYER;
    }

    /**
     * Gets the unique ID of this player
     * @return
     */
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    /**
     * Returns the players name
     *
     * @return
     */
    public String getName() {
        return player.getName();
    }

    /**
     * @inherit
     */
    @Override
    public String getColoredName() {
        return getSession().getColoredName();
    }

    /**
     * Returns the player's name and prefix as 1 string
     *
     * @return
     */
    public String getPrefixAndName() {
        return getSession().getStringPrefixAndName();
    }

    /**
     * Returns the player's display name
     *
     * @return
     */
    public String getDisplayName() {
        return getSession().getDisplayName();
    }

    /**
     * Returns the player's color display name
     *
     * @return
     */
    public String getColoredDisplayName() {
        return getSession().getColoredDisplayName();
    }

    /**
     * Returns the player's display name and prefix as 1 string
     *
     * @return
     */
    public String getPrefixAndDisplayName() {
        return getSession().getStringPrefixAndDisplayName();
    }

    /**
     * Returns the players group
     *
     * @return
     */
    public PlayerGroup getGroup() {
        return getSession().getGroup();
    }

    /**
     * Retuns the player session
     *
     * @return
     */
    public PlayerSession getSession() {
        if (session == null) {
            session = PlayerSession.getSession(getUniqueId(), getName(), plugin);
        }

        return session;
    }

    /**
     * @inherit
     */
    public boolean hasPermission(Permission perm) {
        return getSession().hasPermission(perm);
    }

    public boolean hasFlagPermissions(FlagType type) {
        if (hasPermission(Permission.admin_setanyflag)) {
            return true;
        }
        return getGroup().equalsOrInherits(type.getRequiredGroup());
    }

    /**
     * Returns whether the player is online.
     *
     * @return
     */
    public boolean isOnline() {
        return (getHandle() != null && getHandle().isOnline());
    }

    public void login() {
        getSession().login();
    }

    /**
     * This method is called when a player logs out.
     */
    public void logout() {
        IdpPlayer player = this;
        PlayerSession session = getSession();

        if (session.hasLightsEnabled()) {
            PlayerEffect.NIGHT_VISION.removeSpecial(player);
            session.setLightsEnabled(false);
        }

        // Remove this from the cache, as it's no longer really needed unless requested
        TransactionHandler.removeTransactionObjectFromCache(player);

        if (ChatChannelHandler.isGlobalListener(player.getName())) {
            ChatChannelHandler.removeGlobalListener(player.getName());
        }

        OwnedPetHandler.teleportToPlayerHome(player);
        OwnedPetHandler.clearPets(player.getName());

        if (player.getWorld().getWorldType() == IdpWorldType.RESWORLD
                || player.getWorld().getWorldType() == IdpWorldType.DYNAMIC) {
            player.teleport(WarpHandler.getSpawn(player.getGroup()), TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
        }

        // Update all players with the player count for the TAB list
        plugin.getTaskManager().addTask(new TabListPlayerCountTask(plugin));

        // Save the inventoryType or print it in the serverlog when failed
        if (!saveInventory()) {
            InnPlugin.logError("Failed to store inventory of player " + getColoredName(), new IdpException()); // New throwable for error report!
        }

        getSession().logout();

        super.getPlugin().removeCachedPlayer(player);
    }

    /**
     * Respawn a player (his inventory will be set to the given world without
     * saving the old one and the status will be set on alive)
     */
    public void respawn() {
        getSession().setPlayerStatus(PlayerSession.PlayerStatus.ALIVE_PLAYER);
        setInventory(getWorld().getSettings().getInventoryType());
        getSession().resetPvPStateTime();
        getSession().resetDamageStateTime();
    }

    /**
     * Sends a header and footer to the tab list of this player
     */
    public void sendPlayersOnlineTabList(int numOnline) {
        TextComponent headerComponent = ChatUtil.createTextComponent(ChatColor.LIGHT_PURPLE, " ------- " + numOnline + " Player" + (numOnline != 1 ? "s" : "") + " Online ------- ");
        TextComponent footerComponent = ChatUtil.createTextComponent(ChatColor.YELLOW, "Innectis Minecraft Server");

        player.setPlayerListHeaderFooter(headerComponent, footerComponent);
    }
    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Help Methods">
    /**
     * Opens the inventory window for the given inventory.
     * @param inventory
     */
    public void openInventory(Inventory inventory) {
        getHandle().openInventory(inventory);
    }

    /**
     * Gets the display name this player will use in the player list
     * @return
     */
    public String getPlayerListName() {
        ChatColor color = getGroup().getPrefix().getTextColor();
        return color + getName();
    }

    /**
     * This method changes the players display name and sends the changes to the
     * players online.<br /> <b>This method uses packets to send
     * information.</b>. <br /> <b>This only changes the name above the player,
     * in the rest it will keep the old name!</b>
     *
     * @param name
     */
    public void updateClientDisplayName(String name) {
        this.player.setDisplayName(name);

        // Update all online clients
        for (IdpPlayer pl : plugin.getOnlinePlayers()) {
            // Do not send to yourself
            if (!pl.getName().equals(player.getName())) {
                // Remove old entity
                pl.getHandle().getHandle().playerConnection.sendPacket(new net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy(getHandle().getEntityId()));
                // Spawn new entity
                pl.getHandle().getHandle().playerConnection.sendPacket(new net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn(getHandle().getHandle()));
            }
        }
    }

    /**
     * Resets the food level to the maximum for this player
     */
    public void resetFoodLevel() {
        player.setFoodLevel(20);
    }
    //</editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Location">

    /**
     * Sends a fake block change to the player
     * @param loc
     * @param block
     */
    public void sendBlockChange(Block block) {
        sendBlockChange(block.getLocation(), block);
    }

    /**
     * Sends a fake block change to the player
     * @param loc
     * @param block
     */
    public void sendBlockChange(Location loc, Block block) {
        IdpMaterial mat = IdpMaterial.fromBlock(block);
        byte dat = BlockHandler.getBlockData(block);
        getHandle().sendBlockChange(loc, mat.getBukkitMaterial(), dat);
    }

    /**
     * Gets a clone of the player's location
     */
    public Location getLocation() {
        return player.getLocation();
    }

    /**
     * returns the yaw of the player
     *
     * @return
     */
    public float getYaw() {
        return player.getLocation().getYaw();
    }

    /**
     * Returns the pitch of the player
     *
     * @return
     */
    public float getPitch() {
        return player.getLocation().getPitch();
    }

    /**
     * Returns the world the player is in
     *
     * @return
     */
    public IdpWorld getWorld() {
        return IdpWorldFactory.getWorld(player.getWorld().getName());
    }

    /**
     * Gets the player's bed spawn location
     *
     * @return
     */
    public Location getBedSpawnLocation() {
        return getHandle().getBedSpawnLocation();
    }

    /**
     * Returns nearby entities
     *
     * @param range the range to search for
     * @param filter checks for certain entities
     * @param protectLots if true, will cause entities on lots to not be returned
     * @param allowTamed allows tamed animals to be included with the entities returned
     * @param leaveAmount determines how many entities to leave alone
     *
     * @return
     */
    public List<Entity> getNearbyEntities(double range, Class<? extends Entity> filter, boolean protectLots, boolean allowTamed, int leaveAmount) {
        List<Entity> nearbyEntities = player.getNearbyEntities(range, range, range);
        List<Entity> entities = new ArrayList<Entity>();

        for (Entity entity : nearbyEntities) {
            // Never process players
            if (entity instanceof Player) {
                continue;
            }

            InnectisLot lot = LotHandler.getLot(entity.getLocation());

            if (lot == null || !protectLots) {
                // Special case monster, since slimes and ghasts don't extend the monster class
                boolean isMonster = (filter == Monster.class && (entity instanceof Monster
                        || entity instanceof Ghast || entity instanceof Slime || entity instanceof Shulker));

                if (isMonster || filter.isAssignableFrom(entity.getClass())) {
                    boolean valid = false;

                    if (entity instanceof Tameable) {
                        Tameable tameable = (Tameable) entity;

                        if (!tameable.isTamed() || allowTamed) {
                            valid = true;
                        }
                    } else {
                        valid = true;
                    }

                    if (valid) {
                        if (leaveAmount > 0) {
                            leaveAmount--;
                            continue;
                        }

                        entities.add(entity);
                    }
                }
            }
        }

        return entities;
    }

    /**
     * Plays the specified chat sound from setting
     * @param setting
     */
    public void playChatSoundFromSetting(ChatSoundSetting setting) {
        handle.playSound(getLocation(), setting.getBukkitSound(), setting.getVolume(), setting.getPitch());
    }

    /**
     * Teleports the player to the target player
     * @param player
     * @param types
     * @return
     */
    public boolean teleport(IdpPlayer player, TeleportType... types) {
        // Check if the player is able to teleport first
        String restrictMsg = getRestrictionMessage();

        if (restrictMsg != null) {
            printError(restrictMsg);
            return false;
        }

        int flags = 0;

        if (types != null) {
            for (TeleportType type : types) {
                flags += type.getTeleportFlag();
            }
        }

        boolean ignoreRestriction = ((flags & TeleportType.IGNORE_RESTRICTION.getTeleportFlag()) != 0);
        Location loc = player.getLocation();
        float yaw = player.getYaw();

        BlockFace oppositeBlockFace = player.getFacingDirection().getOppositeFace();
        Location behindPlayerLocation = loc.getBlock().getRelative(oppositeBlockFace).getLocation();

        IdpSpawnFinder finder = new IdpSpawnFinder(behindPlayerLocation);
        Location targetLoc = finder.findClosestSpawn(false);
        restrictMsg = canTeleport(targetLoc, ignoreRestriction);

        IdpMaterial testMaterial = IdpMaterial.fromBlock(targetLoc.getBlock());

        // Check for no restrictions, a spawn finder height difference of less than 2
        // and make sure the destination is not lava or fire
        if (restrictMsg == null && finder.getHeightDifference() < 2
                && !(testMaterial.isLava() || testMaterial == IdpMaterial.FIRE)) {
            targetLoc.setYaw(yaw);
            targetLoc.setPitch(0);

            return teleport(targetLoc, true, true, flags);
        } else {
            loc.setYaw(yaw);
            loc.setPitch(0);

            flags |= TeleportType.RAW_COORDINATES.getTeleportFlag();

            return teleport(loc, false, true, flags);
        }
    }

    /**
     * Teleports to the specified location
     * @param location
     * @param types
     * @return
     */
    public boolean teleport(Location location, TeleportType... types) {
        int flags = 0;

        if (types != null) {
            for (TeleportType type : types) {
                flags += type.getTeleportFlag();
            }
        }

        return teleport(location, false, false, flags);
    }

    /**
     * Teleports the player to the specified location
     * @param location
     * @param flags
     * @return
     */
    private boolean teleport(Location location, boolean skipRestrictionCheck, boolean skipSpawnFinder, int flags) {
        // Check if location exists
        if (location == null) {
            printError("Teleport failed: no location!");
            return false;
        }

        // Check if world exists
        if (location.getWorld() == null) {
            printError("Teleport failed: unknown world!");
            return false;
        }

        String restrictMsg = null;

        if (!skipRestrictionCheck) {
            // Check if the player is restricted from teleporting
            restrictMsg = getRestrictionMessage();

            if (restrictMsg != null) {
                printError(restrictMsg);
                return false;
            }
        }

        boolean restrictIfNether = ((flags & TeleportType.RESTRICT_IF_NETHER.getTeleportFlag()) != 0);
        boolean ignoreRestriction = ((flags & TeleportType.IGNORE_RESTRICTION.getTeleportFlag()) != 0);
        boolean useSpawnFinder = ((flags & TeleportType.USE_SPAWN_FINDER.getTeleportFlag()) != 0);
        boolean pvpImmune = ((flags & TeleportType.PVP_IMMUNITY.getTeleportFlag()) != 0);
        boolean rawCoords = ((flags & TeleportType.RAW_COORDINATES.getTeleportFlag()) != 0);
        boolean restrictNoEscape = ((flags & TeleportType.RESTRICT_IF_NOESCAPE.getTeleportFlag()) != 0);
        boolean allowEndExempt = ((flags & TeleportType.ALLOW_END_EXEMPT.getTeleportFlag()) != 0);

        Location targetLoc = location;

        if (useSpawnFinder && !skipSpawnFinder) {
            // Find a location to put the player
            IdpSpawnFinder finder = new IdpSpawnFinder(location);
            targetLoc = finder.findClosestSpawn(false);
        }

        if (!skipRestrictionCheck) {
            // Check if there are any restrictions at the destination
            restrictMsg = canTeleport(targetLoc, ignoreRestriction);

            if (restrictMsg != null) {
                printError(restrictMsg);
                return false;
            }
        }

        InnectisLot targetLot = LotHandler.getLot(targetLoc, true);
        IdpWorld worldFrom = getWorld();
        IdpWorld worldTo = IdpWorldFactory.getWorld(targetLoc.getWorld().getName());

        // Check for world Switch
        if (!worldFrom.equals(worldTo)) {
            if (!WorldHandler.isSwitchAllowed(worldFrom, worldTo, this)) {
                printError("You are not allowed to teleport to that world!");
                return false;
            }
        }

        InnectisLot previousLot = LotHandler.getLot(getLocation());

        // Secondairy Listeners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_LOT_LEAVE)) {
            InnPlayerLotLeaveEvent idpevent = new InnPlayerLotLeaveEvent(this, targetLot, previousLot);
            plugin.getListenerManager().fireEvent(idpevent);

            if (idpevent.isCancelled()) {
                return false;
            }

            if (idpevent.shouldTerminate()) {
                return false;
            }
        }

        // Automatically promote guest to user if they teleport out of the promotion area
        if (getGroup() == PlayerGroup.GUEST) {
            IdpWorldRegion guestPromoteRegion = Configuration.getGuestPromoteRegion();

            if (guestPromoteRegion != null && guestPromoteRegion.contains(getLocation())) {
                getSession().setGroup(PlayerGroup.USER);
                print(ChatColor.AQUA, "You have completed the tutorial! You are now a " + ChatColor.YELLOW + "User", ".");

                for (IdpPlayer player : plugin.getOnlinePlayers()) {
                    if (!player.equals(this)) {
                        player.print(ChatColor.AQUA, getColoredDisplayName(), " has completed the tutorial!");
                    }
                }
            }
        }

        if (restrictNoEscape && previousLot != null && previousLot.isFlagSet(LotFlagType.NOESCAPE)) {
            printError("You cannot escape this lot that way!");
            return false;
        }

        // Don't allow the player to teleport out of the nether without sufficient funds
        if (worldFrom.getWorldType() == IdpWorldType.NETHER && restrictIfNether
                && !hasPermission(Permission.special_nether_tp_no_cost)) {
            int requiredCost = Configuration.WARP_OUT_NETHER_COST;
            TransactionObject transaction = TransactionHandler.getTransactionObject(this);
            int valutas = transaction.getValue(TransactionType.VALUTAS);

            if (valutas >= requiredCost) {
                transaction.subtractValue(requiredCost, TransactionType.VALUTAS);
                printInfo("You pay " + requiredCost + " valutas to warp from the nether!");
                ValutaSinkManager.addToSink(requiredCost);
            } else {
                printError("You do not have " + requiredCost + " valutas to teleport from the nether!");
                return false;
            }
        }

        // Don't allow into, out of, or within The End without an exception
        if ((worldFrom.getActingWorldType() == IdpWorldType.THE_END || worldTo.getActingWorldType() == IdpWorldType.THE_END)
                && !allowEndExempt && !hasPermission(Permission.special_the_end_exempt_teleport)) {
            printError("You cannot teleport to the destination.");
            return false;
        }

        // Dismount if player is in a vehicle
        Entity vehicle = player.getVehicle();

        if (vehicle != null) {
            player.leaveVehicle();
        }

        Entity passenger = player.getPassenger();

        // Eject any passengers before teleporting
        if (passenger != null) {
            player.eject();
        }

        if (!rawCoords) {
            targetLoc = LocationUtil.getCenterLocation(targetLoc);
        }

        // Keep reference to EXP
        int expBeforeTP = getPlayerExp();

        //Set their last teleprot location to their current location
        getSession().setLastTeleportLocation(getLocation());
        getSession().setLastTeleportTime(System.currentTimeMillis());

        OwnedPets pets = OwnedPetHandler.getPets(this.getName());

        InventoryType invTypeTo = worldTo.getSettings().getInventoryType();
        InventoryType invTypeFrom = worldFrom.getSettings().getInventoryType();

        // Check that pets can transport accross inventories, and only check main inventory types
        if (invTypeTo == invTypeFrom && invTypeTo == InventoryType.MAIN) {
            // Get any nearby tamed animals to keep track of
            List<LivingEntity> nearbyTamed = OwnedPetHandler.getNearbyPets(this, 50);

            if (nearbyTamed != null) {
                pets.addPets(nearbyTamed);
            }

            // Only do trait and kill if the player has any animals
            if (pets.petCount() > 0) {
                // Create traits of the pets
                pets.createPetTraits(false);

                // Kill the original pets
                pets.killPets(false);
            }
        } else {
            pets.clearPetTraits();
        }

        // Only fire a lot leave event if teleporting into a different lot
        if (previousLot != targetLot && previousLot != null) {
            previousLot.onLeave(this, targetLot, true);
        }

        player.teleport(targetLoc);

        // Only fire a lot enter event if teleporting into a different lot
        if (previousLot != targetLot && targetLot != null) {
            targetLot.onEnter(this, previousLot, true);
        }

        // Make sure to set the player's last lot here
        getSession().setLastLot(targetLot);

        ((CraftPlayer) player).getHandle().sleeping = false;

        // Teleport spectators to location.
        List<IdpPlayer> spectators = getSession().getSpectators();

        if (!spectators.isEmpty()) {
            for (IdpPlayer spectator : spectators) {
                spectator.getHandle().teleport(targetLoc);
            }
        }

        // Reset their jumped up state, so there is no possibility of problems later
        //getSession().setJumpedUp(false);

        // set player as immune
        if (pvpImmune) {
            getSession().setPvPImmuneTime(10);
        }

        boolean petsCanTeleport = (OwnedPetHandler.isWorldAllowed(worldTo) && pets.traitSize() > 0);

        // If conditions are met to spawn the owned animals
        if (petsCanTeleport) {
            pets.spawnPetsFromTraits(targetLoc);
            pets.clearPetTraits();

            // Remove any excessive animals the player may have
            int removedCount = pets.removeExcessPets();

            if (removedCount > 0) {
                printInfo("Removed " + removedCount + " pets (exceeded max total)");
            }
        }

        // Reset Exp
        setPlayerExp(expBeforeTP);

        return true;
    }

    /**
     * Teleports a player to a warp location
     *
     * @param warp
     *
     * @return
     */
    public boolean teleport(IdpWarp warp) {
        if (teleport(warp.getLocation(), TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY)) {
            if (warp.getComment() != null && !warp.getComment().isEmpty()) {
                for (String s : warp.getComment().split(";")) {
                    print(ChatColor.YELLOW, s);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the player is restricted from teleporting
     * @return
     */
    private String getRestrictionMessage() {
        // Block jailed player from teleporting
        if (getSession().isJailed()) {
            return "You are not allowed to teleport!";
        }

        // Block frozen player from teleporting
        if (getSession().isFrozen()) {
            return "You are not allowed to teleport!";
        }

        return null;
    }

    /**
     * Checks if the player can teleport to the target location
     * @param location
     * @param types
     * @return A message indicating why the player could not teleport
     */
    public String canTeleport(Location location, boolean ignoreRestriction) {
        // Block special teleports
        InnectisLot targetLot = LotHandler.getLot(location, true);

        if (targetLot != null && !ignoreRestriction) {
            if (!hasPermission(Permission.teleport_force)) {
                if (targetLot.isFlagSet(LotFlagType.NOENCHANTMENTS)) {
                    if (hasEnchantments()) {
                        return "Unable to teleport to here with enchantments!";
                    }
                }

                if (targetLot.isFlagSet(LotFlagType.SPLEEF)) {
                    return "You cannot teleport into Spleef!";
                }

                if ((targetLot.isFlagSet(LotFlagType.NOTELEPORT) && !targetLot.canPlayerManage(player.getName()))) {
                    return "You cannot teleport into that lot!";
                }
            }

            // If player is banned from lot
            if ((targetLot.isBanned(getName()) || (targetLot.isBanned("%")
                    && !getSession().isStaff()))
                    && !hasPermission(Permission.lot_ban_override)
                    && !targetLot.containsMember(getName())
                    && !targetLot.canPlayerManage(getName())) {
                return "You are banned from that lot!";
            }
        }

        IdpWorld worldTo = IdpWorldFactory.getWorld(location.getWorld().getName());
        int worldSize = worldTo.getSettings().getWorldSize();

        // Check if within world bounds.
        if (Math.abs(location.getBlockX()) > worldSize || Math.abs(location.getBlockZ()) > worldSize) {
            return "Teleporting would place you off the end of the map!";
        }

        return null;
    }

    /**
     * Gets the direction the player is looking at as a block face.
     * This uses the standard cardinal directions as a return value
     *
     * @return
     */
    public BlockFace getFacingDirection() {
        return getFacingDirection(DirectionType.CARDINAL);
    }

    /**
     * Gets the direction the player is looking at as a block face
     *
     * @param type
     * @return
     */
    public BlockFace getFacingDirection(DirectionType type) {
        double rot = getYaw();
        if (rot < 0) {
            rot += 360.0;
        }

        // Nort West, North North West, North, North North East, North East
        if (rot >= 135.0 && rot < 225.0) {
            if (type == DirectionType.CARDINAL) {
                return BlockFace.NORTH;
            } else {
                if (type == DirectionType.INTERCARDINAL) {
                    if (rot >= 157.5 && rot < 202.5) {
                        return BlockFace.NORTH;
                    } else {
                        if (rot < 157.5) {
                            return BlockFace.NORTH_WEST;
                        } else {
                            return BlockFace.NORTH_EAST;
                        }
                    }
                } else {
                    // FULL
                    if (rot >= 168.75 && rot < 191.25) {
                        return BlockFace.NORTH;
                    } else {
                        if (rot >= 157.5 && rot < 168.75) {
                            return BlockFace.NORTH_NORTH_WEST;
                        } else if (rot >= 191.25 && rot < 202.5) {
                            return BlockFace.NORTH_NORTH_EAST;
                        } else {
                            if (rot < 157.5) {
                                return BlockFace.NORTH_WEST;
                            } else {
                                return BlockFace.NORTH_EAST;
                            }
                        }
                    }
                }
            }
        // North East, East North East, East, East South East, South East
        } else if (rot >= 225.0 && rot < 315.0) {
            if (type == DirectionType.CARDINAL) {
                return BlockFace.EAST;
            } else {
                if (type == DirectionType.INTERCARDINAL) {
                    if (rot >= 247.5 && rot < 292.5) {
                        return BlockFace.EAST;
                    } else {
                        if (rot < 247.5) {
                            return BlockFace.NORTH_EAST;
                        } else {
                            return BlockFace.SOUTH_EAST;
                        }
                    }
                } else {
                    // FULL
                    if (rot >= 258.75 && rot < 281.25) {
                        return BlockFace.EAST;
                    } else if (rot >= 247.5 && rot < 258.75) {
                        return BlockFace.EAST_NORTH_EAST;
                    } else if (rot >= 281.25 && rot < 292.5) {
                        return BlockFace.EAST_SOUTH_EAST;
                    } else {
                        if (rot < 247.5) {
                            return BlockFace.NORTH_EAST;
                        } else {
                            return BlockFace.SOUTH_EAST;
                        }
                    }
                }
            }
        // South East, South South East, South, South South West, South West
        // South has a couple edge cases
        } else if ((rot >= 315.0 && rot < 360.0)
                || (rot >= 0.0 && rot < 45.0)) {
            if (type == DirectionType.CARDINAL) {
                return BlockFace.SOUTH;
            } else {
                if (type == DirectionType.INTERCARDINAL) {
                    if (rot >= 337.5 || rot < 22.5) {
                        return BlockFace.SOUTH;
                    } else {
                        if (rot >= 315.0 && rot < 337.5) {
                            return BlockFace.SOUTH_EAST;
                        } else {
                            return BlockFace.SOUTH_WEST;
                        }
                    }
                } else {
                    // FULL
                    if (rot >= 348.75 || rot < 11.25) {
                        return BlockFace.SOUTH;
                    } else if (rot >= 337.5 && rot < 348.75) {
                        return BlockFace.SOUTH_SOUTH_EAST;
                    } else if (rot >= 11.25 && rot < 22.5) {
                        return BlockFace.SOUTH_SOUTH_WEST;
                    } else {
                        if (rot >= 315.0 && rot < 337.5) {
                            return BlockFace.SOUTH_EAST;
                        } else {
                            return BlockFace.SOUTH_WEST;
                        }
                    }
                }
            }
        // South West, West South West, West, West North West, North West
        } else if (rot >= 45.0 && rot < 135.0) {
            if (type == DirectionType.CARDINAL) {
                return BlockFace.WEST;
            } else {
                if (type == DirectionType.INTERCARDINAL) {
                    if (rot >= 67.5 && rot < 112.5) {
                        return BlockFace.WEST;
                    } else {
                        if (rot < 67.5) {
                            return BlockFace.SOUTH_WEST;
                        } else {
                            return BlockFace.NORTH_WEST;
                        }
                    }
                } else {
                    // FULL
                    if (rot >= 78.75 && rot < 111.25) {
                        return BlockFace.WEST;
                    } else if (rot >= 67.5 && rot < 78.75) {
                        return BlockFace.WEST_SOUTH_WEST;
                    } else if (rot >= 111.25 && rot < 122.5) {
                        return BlockFace.WEST_NORTH_WEST;
                    } else {
                        if (rot < 67.5) {
                            return BlockFace.SOUTH_WEST;
                        } else {
                            return BlockFace.NORTH_WEST;
                        }
                    }
                }
            }
        } else {
            return null; // so IDE is happy
        }
    }

    /**
     * Returns the block the player is looking at
     *
     * @param range - the range to look
     * @return the block that the player is looking at or null if none
     */
    public Block getTargetBlock(int range) {
        return player.getTargetBlock((Set<Material>) null, range);
    }

    /**
     * Returns the block the player is looking at. This will ignore transparent
     * blocks;
     *
     * @param range - the range to look
     * @param Hashset - the blockid that are ignored
     * @return the block that the player is looking at or null if none
     */
    public Block getTargetBlockWithIgnored(HashSet<Material> ignored, int range) {
        return player.getTargetBlock(ignored, range);
    }

    /**
     * Returns the block the player is looking at, making sure that owned blocks
     * are not ignored
     *
     * @param player
     * @return
     */
    public Block getTargetOwnedBlock() {
        HashSet<Material> ignored = new HashSet<Material>();

        for (IdpMaterial mat : InnPlugin.getPlugin().getIgnoreBlocksLoS()) {
            if (mat != IdpMaterial.CHEST && mat != IdpMaterial.TRAPPED_CHEST
                    && mat != IdpMaterial.BOOKCASE && mat != IdpMaterial.IRON_DOOR_BLOCK
                    && mat != IdpMaterial.LAPIS_LAZULI_OREBLOCK && mat != IdpMaterial.IRON_TRAP_DOOR
                    && mat != IdpMaterial.LEVER) {
                ignored.add(mat.getBukkitMaterial());
            }
        }

        return getTargetBlockWithIgnored(ignored, 5);
    }

    /**
     * Checks if the player has the specified block in the specified line of
     * sight
     *
     * @param block
     * @param range
     * @return
     */
    public boolean hasLineOfSight(Block block, int range) {
        HashSet<Material> losBlocks = new HashSet<Material>();

        for (IdpMaterial mat : plugin.getIgnoreBlocksLoS()) {
            losBlocks.add(mat.getBukkitMaterial());
        }

        List<Block> los = getHandle().getLineOfSight(losBlocks, range);
        return los.contains(block);
    }

    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Entities">
    /**
     * Returns a list will all the players that are close to the player You can
     * set a single value for x, y, and z coordinates
     *
     * @return
     */
    public List<IdpPlayer> getNearByPlayers(double range) {
        return getNearbyPlayers(range, range, range);
    }

    /**
     * Returns a list will all the players that are close to the player You can
     * set a range for the x, y and z radius
     *
     * @return
     */
    public List<IdpPlayer> getNearbyPlayers(double x, double y, double z) {
        // Make a copy
        List<Entity> ents = new ArrayList<Entity>(getNearbyEntities(x, y, z));

        List<IdpPlayer> players = new ArrayList<IdpPlayer>();
        for (Entity e : ents) {
            try {
                if (e instanceof Player) {
                    players.add(plugin.getPlayer((Player) e));
                }
            } catch (NoSuchElementException nsee) {
                // Element was removed, ignore them.
            }
        }
        return players;
    }

    /**
     * Returns a list will all the entities that are close to the player You can
     * set a range for the x, y and z radius
     *
     * @return
     */
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        return player.getNearbyEntities(x, y, z);
    }
    //</editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Messaging">
    /**
     * @inherit
     */
    @Override
    public void printRaw(String message) {
        player.sendMessage(message);
    }

    /**
     * Prints a message as a text component
     * @param component
     */
    public void print(TextComponent component) {
        print(true, component);
    }

    /**
     * Prints a message with an option to use the IDP prefix
     * and a text component
     * @param usePrefix whether to apply the IDP message prefix
     * @param component
     */
    public void print(boolean usePrefix, TextComponent component) {
        print(ChatMessageType.CHAT, usePrefix, component);
    }

    /**
     * Prints a message based on the message type, and a text component
     * @param messageType
     * @param component
     */
    public void print(ChatMessageType messageType, TextComponent component) {
        print(messageType, true, component);
    }

    /**
     * Prints a message based on the message type, whether to use the IDP
     * prefix, and a text component
     * @param messageType
     * @param usePrefix whether to apply the IDP message prefix
     * @param component
     */
    public void print(ChatMessageType messageType, boolean usePrefix, TextComponent component) {
        if (usePrefix) {
            net.md_5.bungee.api.ChatColor tempColor = component.getColor();
            TextComponent tempComponent = component;

            component = new TextComponent(Configuration.MESSAGE_PREFIX);
            component.setColor(tempColor);
            component.addExtra(tempComponent);
        }

        player.sendMessage(messageType, component);
    }

    /**
     * Sets a title to the player
     * @param title
     * @param subtitle
     */
    public void sendTitle(String title, String subtitle) {
        player.sendTitle(new Title(title, subtitle, 10, 50, 10));
    }
    //</editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Inventory Handling">
    /**
     * Gets the inventoryType type of the player
     *
     * @return
     */
    public InventoryType getCurrentInventoryType() {
        return getSession().invType;
    }

    /**
     * Sets the current inventoryType type of the player. This does not update
     * the actual inventoryType contents.
     */
    private void setCurrentInventoryType(InventoryType type) {
        getSession().invType = type;
    }

    /**
     * Saves the player's inventoryType in the database
     *
     * @return true if succeded
     */
    public boolean saveInventory() {
        if (getSession().isPlayerAlive()) {
            return getInventory().store();
        } else {
            InnPlugin.logError(getName() + " is dead, so not storing inventory!");
        }

        return true;
    }

    /**
     * Returns the inventoryType of the player
     *
     * @return
     */
    public IdpPlayerInventory getInventory() {
        IdpPlayerInventory inventory = new IdpPlayerInventory(getUniqueId(), getName(), getCurrentInventoryType());
        inventory.setContents(player.getInventory().getStorageContents(), player.getInventory().getArmorContents(), player.getInventory().getItemInOffHand());
        inventory.setExperience(player.getExp());
        inventory.setLevel(player.getLevel());
        inventory.setHealth(player.getHealth());
        inventory.setHunger(player.getFoodLevel());
        inventory.setPotionEffects(new ArrayList<PotionEffect>(player.getActivePotionEffects()));
        return inventory;
    }

    /**
     * Sets the inventoryType of the player This does not save the old
     * inventoryType! use with caution!
     */
    @SuppressWarnings("deprecation")
    public void setInventory(IdpPlayerInventory inventory) {
        setCurrentInventoryType(inventory.getType());
        player.getInventory().setContents(inventory.getBukkitItems());
        player.getInventory().setArmorContents(inventory.getBukkitArmorItems());
        if (inventory.getExperience() >= 0) {
            player.setExp(inventory.getExperience());
        }
        if (inventory.getLevel() >= 0) {
            player.setLevel(inventory.getLevel());
        }
        for (PotionEffect effect : inventory.getPotionEffects()) {
            player.addPotionEffect(effect);
        }
        if (!getSession().isVisible()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 1), true);
        }

        // Make sure the stored inventory's health is above 0
        if (inventory.getHealth() > 0) {
            player.setHealth(inventory.getHealth());
        } else {
            // Restore the player's health if the inventory's health is 0 or less
            player.setHealth(player.getMaxHealth());
        }

        player.setFoodLevel(inventory.getHunger());
        player.updateInventory();
    }

    /**
     * Sets the inventoryType of the player to the given type The contents of
     * this type are loaded from the database. This does not save the old
     * inventoryType! use with caution!
     */
    @SuppressWarnings("deprecation")
    public void setInventory(InventoryType newType) {
        IdpItemStack handStack = getItemInMainHand();

        // Make sure the player does not have an item on
        // their cursor when the inventory is switched
        if (handStack != null) {
            getHandle().setItemOnCursor(null);
        }

        // Make sure the inventory is always closed when
        // switching inventories, to prevent possible abuse
        getHandle().closeInventory();

        clearInventory();
        clearPotionEffects();
        IdpPlayerInventory inventory = IdpPlayerInventory.load(getUniqueId(), getName(), newType, plugin);
        setCurrentInventoryType(inventory.getType());
        player.getInventory().setContents(inventory.getBukkitItems());
        player.getInventory().setArmorContents(inventory.getBukkitArmorItems());
        player.getInventory().setItemInOffHand(inventory.getBukkitOffHandItem()[0]);

        if (inventory.getExperience() >= 0) {
            player.setExp(inventory.getExperience());
        }
        if (inventory.getLevel() >= 0) {
            player.setLevel(inventory.getLevel());
        }

        for (PotionEffect effect : inventory.getPotionEffects()) {
            player.addPotionEffect(effect);
        }

        if (!getSession().isVisible()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 1), true);
        }

        inventory.setHealth(Math.min(inventory.getHealth(), player.getMaxHealth()));

        // Make sure the stored inventory's health is above 0
        if (inventory.getHealth() > 0) {
            player.setHealth(inventory.getHealth());
        } else {
            // Restore the player's health if the inventory's health is 0 or less
            player.setHealth(player.getMaxHealth());
        }

        player.setFoodLevel(inventory.getHunger());
        player.updateInventory();
    }

    /**
     * Switches the inventoryType of the player to an other type
     */
    public void switchInventory(InventoryType newType) throws InventorySwitchException {
        if (getCurrentInventoryType() != newType) {
            // Check if the player is dead.
            if (getSession().isPlayerAlive()) {
                if (plugin.isDebugEnabled()) {
                    plugin.logDebug("Switching inventory from " + getCurrentInventoryType() + " to " + newType);
                }
                if (!getInventory().store()) {
                    throw new InventorySwitchException("Cannot switch inventory!");
                }
            } else {
                InnPlugin.logInfo("Player " + getName() + " switching inventories while dead...");
            }
            clearInventory();
            clearPotionEffects();
            setInventory(newType);

            // Go through all online players and determine who is viewing someone's inventory
            // and if they are viewing the live inventory. Since the contents of their window
            // would change after the inventory switches, then the inventory type they're
            // viewing must also change, since the original inventory will save anyways
            for (IdpPlayer p : plugin.getOnlinePlayers()) {
                PlayerSession sess = p.getSession();
                ViewedPlayerInventoryData vpid = sess.getViewedPlayerInventoryData();

                if (vpid != null && vpid.isViewingLiveInventory()
                        && vpid.getPlayerId().equals(getUniqueId())) {
                    vpid.setViewedInventoryType(newType);
                }
            }
        }
    }

    /**
     * Clears the inventoryType of the player
     */
    public void clearInventory() {
        handle.getInventory().clear();
        handle.getInventory().setArmorContents(new org.bukkit.inventory.ItemStack[4]);
        handle.getInventory().setContents(new org.bukkit.inventory.ItemStack[36]);
    }

    /**
     * Removes all potion effects the player has.
     */
    public void clearPotionEffects() {
        for (PotionEffect effect : handle.getActivePotionEffects()) {
            handle.removePotionEffect(effect.getType());
        }
    }

    /**
     * Returns the amount of the given material
     *
     * @param materialId
     * @param data
     *
     * @return
     */
    public int getInventoryItemCount(IdpMaterial material) {
        IdpContainer cont = new IdpContainer(player.getInventory());
        return cont.countMaterial(material);
    }

    /**
     * Checks if the player has any enchantments on any items in their inventory
     *
     * @return
     */
    public boolean hasEnchantments() {
        IdpItemStack[] stack = getInventory().getArmorItems();

        for (int i = 0; i < stack.length; i++) {
            if (stack[i] != null) {
                ItemData data = stack[i].getItemdata();
                Map<EnchantmentType, Integer> enchantments = data.getEnchantments();

                if (enchantments.size() > 0) {
                    return true;
                }
            }
        }

        stack = getInventory().getItems();

        for (int i = 0; i < stack.length; i++) {
            if (stack[i] != null) {
                ItemData data = stack[i].getItemdata();
                Map<EnchantmentType, Integer> enchantments = data.getEnchantments();

                if (enchantments.size() > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Attempts to remove a player skull whose owner is
     * the specified owner passed to this method
     * @param owner
     * @return
     */
    public boolean removePlayerSkullByOwner(String owner) {
        IdpPlayerInventory inv = getInventory();
        IdpContainer container = new IdpContainer(inv.getItems(), 36);

        for (int i = 0; i < container.size(); i++) {
            IdpItemStack stack = container.getItemAt(i);
            IdpMaterial mat = stack.getMaterial();

            if (mat == IdpMaterial.PLAYER_SKULL) {
                ItemData data = stack.getItemdata();

                if (data.getMobheadName().equals(owner)) {
                    if (stack.getAmount() > 1) {
                        stack.setAmount(stack.getAmount() - 1);
                    } else {
                        stack = null;
                    }

                    container.setItemAt(i, stack);

                    inv.setItems(container.getItems());
                    inv.updateBukkitInventory();

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Removes a banner from the player that matches a base
     * color and patterns
     * @param banner
     * @return
     */
    public boolean removeBannerByPattern(Banner banner) {
        IdpPlayerInventory inv = getInventory();
        IdpContainer container = new IdpContainer(inv.getItems(), 36);

        for (int i = 0; i < container.size(); i++) {
            IdpItemStack stack = container.getItemAt(i);
            IdpMaterial mat = stack.getMaterial();

            if (mat.isBanner()) {
                ItemData itemdata = stack.getItemdata();

                if (itemdata.getBannerBaseColor().equals(banner.getBaseColor())
                        && itemdata.getBannerPatterns().equals(banner.getPatterns())) {
                    if (stack.getAmount() > 1) {
                        stack.setAmount(stack.getAmount() - 1);
                    } else {
                        stack = null;
                    }

                    container.setItemAt(i, stack);

                    inv.setItems(container.getItems());
                    inv.updateBukkitInventory();

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the player got the amount of the given item in their
     * inventory
     *
     * @param materialId
     * @param data
     * @param amount
     *
     * @return
     */
    public boolean hasItemInInventory(IdpMaterial material, int amount) {
        return (getInventoryItemCount(material) >= amount);
    }

    /**
     * Looks for the first itemstack in the inventory with the given material
     * and returns it. <b>Note: This will also remove the stack!</b>
     *
     * @param material
     * @return
     */
    public IdpItemStack getFirstItemstack(IdpMaterial material) {

        IdpPlayerInventory inv = getInventory();
        IdpContainer contents = new IdpContainer(inv.getItems(), 36);
        IdpItemStack stack = contents.removeFirstMaterialstack(new IdpItem(material));
        inv.setItems(contents.getItems());
        inv.updateBukkitInventory();

        return stack;
    }

    /**
     * @see InventoryHandler::removeItemFromInventory(Player player, int
     * materialId, int data, int amount)
     * @param material
     * @param data
     * @param amount
     *
     * @return true if all items where taken. <br/> When false, no changes will
     * be made to the inventory.
     */
    public boolean removeItemFromInventory(IdpMaterial material, int amount) {
        return removeItemFromInventory(new IdpItemStack(material, amount));
    }

    /**
     * Returns true if all items did exist in the inventoryType
     *
     * @param stack
     *
     * @return true if all items where taken.<br/> When false, no changes will
     * be made to the inventory.
     */
    public boolean removeItemFromInventory(IdpItemStack stack) {
        int amt = 0;

        IdpPlayerInventory inv = getInventory();
        IdpContainer contents = new IdpContainer(inv.getItems(), 36);
        amt = contents.removeMaterialFromStack(stack, stack.getAmount());

        if (amt > 0) {
            IdpContainer armorContents = new IdpContainer(inv.getArmorItems(), 4);
            amt = armorContents.removeMaterialFromStack(stack, amt);

            if (amt == 0) {
                inv.setArmorItems(armorContents.getItems());
            }
        }

        // All items where taken
        if (amt == 0) {
            // Only apply changed is all items where taken
            inv.setItems(contents.getItems());
            inv.updateBukkitInventory();
            return true;
        }
        return false;
    }

    /**
     * Add an item to the player's inventoryType
     *
     * @param itemstack
     *
     * @returns any items remaining
     */
    public int addItemToInventory(IdpItemStack stack) {
        return addItemToInventory(stack, false);
    }

    /**
     * Add an item to the player's inventoryType
     *
     * @param itemstack
     * @param denyOnPartial if the inventory accepts only part of the stack
     * then the inventory will not be affected
     *
     * @returns any items remaining
     */
    public int addItemToInventory(IdpItemStack stack, boolean denyOnPartial) {
        IdpPlayerInventory inv = getInventory();
        IdpContainer contents = new IdpContainer(inv.getItems(), 36);

        int remaining = contents.addMaterialToStack(stack);

        if (remaining > 0 && denyOnPartial) {
            // If we are not adding any amount given in the stack, then return
            // the whole amount, as none has been added
            return stack.getAmount();
        } else {
            inv.setItems(contents.getItems());
            inv.updateBukkitInventory();

            return remaining;
        }
    }

    /**
     * Add an item to the player's inventory
     *
     * @param material
     * @param data
     * @param amount
     *
     * @return amount left over
     */
    public int addItemToInventory(IdpMaterial material, int amount) {
        return addItemToInventory(new IdpItemStack(material, amount));
    }

    /**
     * Sets an item to the players iteminhand
     *
     * @param item
     */
    public void setItemInMainHand(IdpItemStack item) {
        player.getInventory().setItemInMainHand(item.toBukkitItemstack());
    }

    /**
     * Returns the item in hand as a itemstack
     *
     * @return
     */
    public IdpItemStack getItemInMainHand() {
        return IdpItemStack.fromBukkitItemStack(player.getInventory().getItemInMainHand());
    }

    /**
     * Returns the material in the player's hand
     *
     * @return
     */
    public IdpMaterial getMaterialInMainHand() {
        return (getItemInMainHand() == null ? IdpMaterial.AIR : getItemInMainHand().getMaterial());
    }


    /**
     * Returns the item in the player's off hand
     * @return
     */
    public IdpItemStack getItemInOffHand() {
        return IdpItemStack.fromBukkitItemStack(player.getInventory().getItemInOffHand());
    }

    /**
     * Returns the material in the player's off hand
     * @return
     */
    public IdpMaterial getMaterialInOffHand() {
        return getItemInOffHand().getMaterial();
    }

    /**
     * Sets the item in the player's off hand
     * @param item
     */
    public void setItemInOffHand(IdpItemStack item) {
        player.getInventory().setItemInOffHand(item.toBukkitItemstack());
    }

    public EquipmentSlot getNonEmptyHand() {
        IdpItemStack stack = getItemInMainHand();

        if (stack != null && stack.getMaterial() != IdpMaterial.AIR) {
            return EquipmentSlot.HAND;
        } else {
            stack = getItemInOffHand();

            if (stack != null && stack.getMaterial() != IdpMaterial.AIR) {
                return EquipmentSlot.OFF_HAND;
            }
        }

        return null;
    }

    /**
     * Gets the item in hand by the specified hand slot
     * @param slot
     * @return
     */
    public IdpItemStack getItemInHand(EquipmentSlot slot) {
        if (slot == EquipmentSlot.HAND) {
            return getItemInMainHand();
        } else {
            return getItemInOffHand();
        }
    }

    /**
     * Gets the material in hand by the specified hand slot
     * @param slot
     * @return
     */
    public IdpMaterial getMaterialInHand(EquipmentSlot slot) {
        if (slot == EquipmentSlot.HAND) {
            return getMaterialInMainHand();
        } else {
            return getMaterialInOffHand();
        }
    }

    /**
     * Checks which hand is carrying the specified material
     * @param mat
     * @return
     */
    public EquipmentSlot getHandSlotForMaterial(IdpMaterial mat) {
        IdpItemStack stack = getItemInMainHand();

        if (stack != null && stack.getMaterial() == mat) {
            return EquipmentSlot.HAND;
        } else {
            stack = getItemInOffHand();

            if (stack != null && stack.getMaterial() == mat) {
                return EquipmentSlot.OFF_HAND;
            }
        }

        return null;
    }

    /**
     * Sets the item in hand according to the slot
     * @param slot
     * @param stack
     */
    public void setItemInHand(EquipmentSlot slot, IdpItemStack stack) {
        if (slot == EquipmentSlot.HAND) {
            setItemInMainHand(stack);
        } else {
            setItemInOffHand(stack);
        }
    }

    /**
     * Returns the helmet
     *
     * @return
     */
    public IdpItemStack getHelmet() {
        if (getHandle().getInventory().getHelmet() != null) {
            return IdpItemStack.fromBukkitItemStack(getHandle().getInventory().getHelmet());
        } else {
            return IdpItemStack.EMPTY_ITEM;
        }
    }

    /**
     * Returns the chestplate
     *
     * @return
     */
    public IdpItemStack getChestplate() {
        if (getHandle().getInventory().getChestplate() != null) {
            return IdpItemStack.fromBukkitItemStack(getHandle().getInventory().getChestplate());
        } else {
            return IdpItemStack.EMPTY_ITEM;
        }
    }

    /**
     * Returns the leggins
     *
     * @return
     */
    public IdpItemStack getLeggings() {
        if (getHandle().getInventory().getLeggings() != null) {
            return IdpItemStack.fromBukkitItemStack(getHandle().getInventory().getLeggings());
        } else {
            return IdpItemStack.EMPTY_ITEM;
        }
    }

    /**
     * Returns the boots
     *
     * @return
     */
    public IdpItemStack getBoots() {
        if (getHandle().getInventory().getBoots() != null) {
            return IdpItemStack.fromBukkitItemStack(getHandle().getInventory().getBoots());
        } else {
            return IdpItemStack.EMPTY_ITEM;
        }
    }

    /**
     * Sets the item as helmet of the player
     *
     * @param item
     */
    public void setHelmet(IdpItemStack item) {
        getHandle().getInventory().setHelmet(item.toBukkitItemstack());
    }

    /**
     * Sets the item as chestplate of the player
     *
     * @param item
     */
    public void setChestplate(IdpItemStack item) {
        getHandle().getInventory().setChestplate(item.toBukkitItemstack());
    }

    /**
     * Sets the item as leggings of the player
     *
     * @param item
     */
    public void setLeggings(IdpItemStack item) {
        getHandle().getInventory().setLeggings(item.toBukkitItemstack());
    }

    /**
     * Sets the item as boots of the player
     *
     * @param item
     */
    public void setBoots(IdpItemStack item) {
        getHandle().getInventory().setBoots(item.toBukkitItemstack());
    }

    /**
     * Calculates the items the player would lose on death as well as
     * the items the player will keep
     * @param deathLot
     * @param randomizer
     * @param isAllDropWorld
     * @return the items to keep as well as the items that
     * will get dropped. Null if no drops will be given
     */
    public PlayerDeathItems calculateDeathItems(InnectisLot deathLot, Random randomizer, boolean isAllDropWorld) {
        List<IdpItemStack> keepItems = new ArrayList<IdpItemStack>();
        List<IdpItemStack> deathItems = new ArrayList<IdpItemStack>();

        for (IdpItemStack stack : getInventory().getItems()) {
            if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
                continue;
            }

            ItemData itemdata = stack.getItemdata();

            // Pass items with curse of vanishing
            if (itemdata.hasEnchantment(EnchantmentType.CURSE_OF_VANISHING)) {
                continue;
            }

            keepItems.add(stack);
        }

        // Conditions for not dropping death items
        if ((deathLot != null && deathLot.isFlagSet(LotFlagType.NODROPS))
                || hasPermission(Permission.entity_deathitems)) {
            return new PlayerDeathItems(keepItems.toArray(new IdpItemStack[36]));
        }

        int itemsToRemove = (int) Math.floor(keepItems.size() / 5); //20%

        if (isAllDropWorld) {
            itemsToRemove = keepItems.size(); // The full inventory!
        }

        // No items need to be removed
        if (itemsToRemove < 1) {
            return new PlayerDeathItems(keepItems.toArray(new IdpItemStack[36]));
        }

        int failedRemoveCount = 0;

        while (itemsToRemove > 0) {
            int randomIndex = randomizer.nextInt(keepItems.size());
            IdpItemStack tempItem = keepItems.get(randomIndex);
            IdpMaterial material = tempItem.getMaterial();

            if (failedRemoveCount < 20 && getSession().isInPvPState()
                    && (material == IdpMaterial.WOOD_SWORD || material == IdpMaterial.STONE_SWORD
                    || material == IdpMaterial.IRON_SWORD || material == IdpMaterial.GOLD_SWORD
                    || material == IdpMaterial.DIAMOND_SWORD)) {
                failedRemoveCount++;
            } else {
                keepItems.remove(randomIndex);
                deathItems.add(tempItem);
                itemsToRemove--;
            }
        }

        return new PlayerDeathItems(keepItems.toArray(new IdpItemStack[36]), deathItems.toArray(new IdpItemStack[36]));
    }

    /**
     * Refreshes the inventory to the player
     */
    @SuppressWarnings("deprecation")
    public void updateInventory() {
        getHandle().updateInventory();
    }
    //</editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="PVP">
    /**
     * Gets the player's level
     *
     * @return
     */
    public int getPlayerLevel() {
        return player.getLevel();
    }

    /**
     * Gets the player's experience
     *
     * @return
     */
    public int getPlayerExp() {
        return getHandle().getTotalExperience();
    }

    /**
     * sets the player's EXP
     *
     * @param exp - The new exp amount
     */
    public void setPlayerExp(int exp) {
        getHandle().setTotalExperience(exp);
    }

    /**
     * Deals damage to the player.
     *
     * @param damage
     */
    public void dealDamage(double damage) {
        dealDamage(damage, null);
    }

    /**
     * Deals damage to the player. Source can be null
     *
     * @param damage
     * @param source
     */
    public void dealDamage(double damage, Entity source) {
        if (source != null) {
            player.damage(damage, source);
        } else {
            player.damage(damage);
        }
    }

    /**
     * Sets the health of the player
     *
     * @param newHealth
     */
    public void setHealth(double newHealth) {
        player.setHealth(newHealth);
    }

    /**
     * Gets the health of the player
     *
     * @param newHealth
     */
    public double getHealth() {
        return player.getHealth();
    }

    /**
     * Sets the food level of the player
     *
     * @param level
     */
    public void setFoodLevel(int level) {
        player.setFoodLevel(level);
    }

    /**
     * Gets the food level of the player
     *
     * @return
     */
    public int getFoodLevel() {
        return player.getFoodLevel();
    }

    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="WE">

    // TODO: there has to be a better way to do this...
    private boolean noRegion = false;

    /**
     * returns the selected region
     *
     * @return
     */
    public IdpWorldRegion getRegion() {
        // Hacky code to get around the fact we can't nullify a region
        if (noRegion) {
            return null;
        }

        IWorldEditIDP worldEdit = (IWorldEditIDP) plugin.getExternalLibraryManager().getAPIObject(LibraryType.WORLDEDIT);

        IdpRegion region = worldEdit.getSelection(this);

        if (region == null) {
            return null;
        }

        return new IdpWorldRegion(getLocation().getWorld(), region);
    }

    /**
     * Sets the region the player has selected
     */
    public void setRegion(IdpWorldRegion region) {
        // Hacky code to get around the fact we can't nullify a region
        if (region == null) {
            noRegion = true;
            return;
        } else {
            noRegion = false;
        }

        IWorldEditIDP worldEdit = (IWorldEditIDP) plugin.getExternalLibraryManager().getAPIObject(LibraryType.WORLDEDIT);
        worldEdit.setSelection(this, region);
    }

    /**
     * Sets the location of the region or makes a new one if none is set!
     *
     * @param pos
     */
    public void setRegionLoc1(Vector pos) {
        IdpWorldRegion region = getRegion();

        if (region == null) {
            region = new IdpWorldRegion(getWorld().getHandle(), pos);
        } else {
            region.setPos1(pos);
        }

        setRegion(region);
        noRegion = false;
    }

    /**
     * Sets the location of the region or makes a new one if none is set!
     *
     * @param pos
     */
    public void setRegionLoc2(Vector pos) {
        IdpWorldRegion region = getRegion();

        if (region == null) {
            region = new IdpWorldRegion(getWorld().getHandle(), pos);
        } else {
            region.setPos2(pos);
        }

        setRegion(region);
        noRegion = false;
    }
    // </editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Spoofing">

    public boolean isOtherPlayerSpoofing(String name) {
        for (IdpPlayer p : plugin.getOnlinePlayers()) {
            if (this != p) {
                if (p.getDisplayName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }

        return false;
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Light Source">

    /**
     * Returns if the conditions are suitable for a player to
     * use their portable light
     *
     * @return
     */
    public boolean canUsePortableLight() {
        return canUsePortableLight(getItemInMainHand());
    }

    /**
     * Returns if the conditions are suitable for a player
     * @param heldItem
     * use their portable light
     *
     * @return
     */
    public boolean canUsePortableLight(IdpItemStack heldItem) {
        if (!getSession().hasLightsEnabled()) {
            return false;
        }

        // Allow lights regardless of light source items if player has permission
        if (hasPermission(Permission.special_lightsource_anywhere)) {
            return true;
        }

        // Don't allow lighting up blocks in event world
        if (getWorld().getActingWorldType() == IdpWorldType.EVENTWORLD) {
            return false;
        }

        // Check if the player has permission to use lights
        boolean mainConditionMet = hasPermission(Permission.special_lightsource_activate);

        // Main condition met, so let's check secondary condition
        if (mainConditionMet) {
            EquipmentSlot[] slots = null;

            // If the player switched to a new item in hand, check if the item can be
            // used to light up the world
            if (heldItem != null) {
                switch (heldItem.getMaterial()) {
                    case TORCH:
                    case LAVA_BUCKET:
                        return true;
                }

                slots = new EquipmentSlot[] {EquipmentSlot.OFF_HAND};
            } else {
                slots = new EquipmentSlot[] {EquipmentSlot.HAND, EquipmentSlot.OFF_HAND};
            }

            // Check all hands that apply here
            for (EquipmentSlot slot : slots) {
                IdpItemStack handStack = getItemInHand(slot);

                if (handStack != null) {
                    switch (handStack.getMaterial()) {
                        case TORCH:
                        case LAVA_BUCKET:
                            return true;
                    }
                }
            }

            IdpItemStack helmet = getHelmet();

            if (helmet != null && (helmet.getMaterial() == IdpMaterial.GLOWSTONE
                    || helmet.getMaterial() == IdpMaterial.JACK_O_LANTERN)) {
                return true;
            }
        }

        return false;
    }

    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Flying">
    /**
     * Check if the player has fly mode enabled. <br/> Note that this doesn't
     * check for creative mode.!
     *
     * @return either flymode
     */
    public boolean getAllowFlight() {
        return getHandle().getAllowFlight();
    }

    /**
     * Check if the player is flying.
     * <p/>
     * <b> This does not check cheats! </b>
     *
     * @return either flymode
     */
    public boolean isFlying() {
        return getHandle().isFlying();
    }

    /**
     * This method will mark if the player is able to fly (without creative
     * mode)
     *
     * @param allow
     */
    public void setAllowFlight(boolean allow) {
        setAllowFlight(allow, false);
    }

    /**
     * This method will mark if the player is able to fly (without creative
     * mode)
     *
     * @param allow
     * @param hover If this is true, the player will start to hover
     */
    public void setAllowFlight(boolean allow, boolean hover) {
        getHandle().setAllowFlight(allow);

        if (allow && hover) {
            getHandle().setFlying(true);
        }
    }
    //</editor-fold>
//
    //<editor-fold defaultstate="collapsed" desc="Enums">
    public enum TeleportType {
        // If teleporting to nether, allow teleport based on specific conditions
        RESTRICT_IF_NETHER(1),

        // Ignores any restrictions imposed by the destination
        IGNORE_RESTRICTION(2),

        // Attempts to find a proper location at the destination
        USE_SPAWN_FINDER(4),

        // Gives the player PvP immunity at the destination
        PVP_IMMUNITY(8),

        // Does not place the player at the center of a block
        RAW_COORDINATES(16),

        // If teleporting out of a lot with NoEscape flag, then disallow.
        RESTRICT_IF_NOESCAPE(32),

        // Allows teleporting out of the end without restriction
        ALLOW_END_EXEMPT(64);

        private final int teleportFlag;

        private TeleportType(int teleportFlag) {
            this.teleportFlag = teleportFlag;
        }

        public int getTeleportFlag() {
            return teleportFlag;
        }
    }
    //</editor-fold>
//
    //<editor-fold desc="Window metadata" defaultstate="collapsed">
    private int windowPage = 0;
    private int itemAcquisitionSize = 0;

    /**
     * Sets the window page of the player
     * @param page
     */
    public void setWindowPage(int page) {
        windowPage = page;
    }

    /**
     * Gets the window page of the player
     * @return
     */
    public int getWindowPage() {
        return windowPage;
    }

    /**
     * Sets the item acquisition size for windows
     * @param acquisitionSize
     */
    public void setItemAcquisitionSize(int acquisitionSize) {
        itemAcquisitionSize = acquisitionSize;
    }

    /**
     * Gets the item acquisition size for windows
     * @return
     */
    public int getItemAcquisitionSize() {
        return itemAcquisitionSize;
    }
    //</editor-fold>

}
