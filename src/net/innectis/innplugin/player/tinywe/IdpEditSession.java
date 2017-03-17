package net.innectis.innplugin.player.tinywe;

import java.lang.ref.WeakReference;
import java.util.UUID;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.location.IdpDynamicWorldSettings;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.objects.owned.handlers.WaypointHandler;
import net.innectis.innplugin.objects.owned.InnectisWaypoint;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayerInventory;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerSettings;
import net.innectis.innplugin.player.tinywe.blockcounters.MaterialSelector;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.FlowerPot;
import org.bukkit.block.Skull;
import org.bukkit.material.MaterialData;

/**
 *
 * @author Hret
 *
 * Editsession keeping the information about edits with TinyWE. This can be
 * expanded to save preferences and other things.
 */
public class IdpEditSession {

    public enum SpecialItemHandleResult {
        // This item was not a special item
        NOT_SPECIAL,

        // This item is special but was not completely processed
        SPECIAL_NOT_HANDLED,

        // This item is special and does not need to be processed elsewhere
        SPECIAL_HANDLED;
    }

    private UUID playerId;
    private WeakReference<IdpPlayer> _player;

    /**
     * Create a new editsession
     *
     * @param playerId
     */
    public IdpEditSession(UUID playerId) {
        this.playerId = playerId;
        // load the player
        fetchPlayer();
    }

    // <editor-fold defaultstate="collapsed" desc="Basics">
    /**
     * Resets the _player variable
     *
     * @see IdpEditSession::getPlayer()
     */
    private void fetchPlayer() {
        IdpPlayer player = InnPlugin.getPlugin().getPlayer(playerId);
        _player = new WeakReference<IdpPlayer>(player);
    }

    /**
     * Gets the cached player or find the new object.<br/> Use this object, as
     * the internal field uses a weak reference to it.
     *
     * @return
     */
    protected IdpPlayer getPlayer() {
        if (_player.get() == null) {
            fetchPlayer();
        }
        return _player.get();
    }

    /**
     * Checks if all of the materials in the materialsector can be used
     *
     * @param selector
     * @return
     */
    public boolean checkMaterials(MaterialSelector selector) {
        for (IdpMaterial mat : selector.getMaterials()) {
            if (!canPlaceMaterial(mat)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cehecks if it should take and return items from the inventory of the
     * player
     *
     * @return
     */
    public boolean useInventory() {
        // Never use inventory for creative world
        if (getPlayer().getWorld().getActingWorldType() == IdpWorldType.CREATIVEWORLD) {
            return false;
        }

        if (getPlayer().hasPermission(Permission.tinywe_override_noconsumption)) {
            return getPlayer().getSession().hasSetting(PlayerSettings.TWE_INVUSEAGE);
        }
        return true;
    }

    //</editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Selection Size">
    /**
     * *
     * The max size of the selection (x * y * z)
     *
     * @return
     */
    public int getMaxSelectionSize() {

        // Check for unlimited permission
        if (getPlayer().hasPermission(Permission.tinywe_selection_unlimited)) {
            return Integer.MAX_VALUE; // basicly unlimited
        }
        int size = getMaxBlockChanges();
        return (int) (size * Configuration.TWE_MAX_CHANGESELECTION_MULTIPLYER);
    }

    /**
     * *
     * The max amount of blocks that the player is allowed to edit
     *
     * @return
     */
    public int getMaxBlockChanges() {
        // Check for unlimited permission
        if (getPlayer().hasPermission(Permission.tinywe_selection_unlimited)) {
            return Integer.MAX_VALUE; // basicly unlimited
        }
        switch (getPlayer().getGroup()) {
            case MODERATOR:
                return Configuration.TWE_MAX_BLOCKCHANGES_MODERATOR;
            case GOLDY:
                return Configuration.TWE_MAX_BLOCKCHANGES_GOLDY;
            case SUPER_VIP:
                return Configuration.TWE_MAX_BLOCKCHANGES_SVIP;
            case DIAMOND:
                return Configuration.TWE_MAX_BLOCKCHANGES_DIAMOND;

            default:
                return 0;
        }
    }

    // </editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Items">
    /**
     * Log functions for the block that is changed.<br/> Currently its only
     * appending the quota. But other logging methode may be added later
     *
     * @param block
     */
    public void placedBlock(Block block) {
        IdpPlayer player = getPlayer();
        IdpWorld world = player.getWorld();

        // Only track block quotas in main inventory worlds
        if (world.getSettings().getInventoryType() == InventoryType.MAIN) {
            // If passing quota boundary, notify Staff.
            boolean exceededQuota = BlockHandler.isExceedingQuota(player, block);
            BlockHandler.appendQuota(player, block);

            if (!exceededQuota && BlockHandler.isExceedingQuota(player, block)) {
                IdpMaterial mat = IdpMaterial.fromBlock(block);
                InnPlugin.getPlugin().broadCastStaffMessage(ChatColor.DARK_GREEN + "[IDP] " + player.getName() + " is now exceeding their quota for " + mat.getName() + ".", false);
                InnPlugin.logInfo(player.getName() + " is now exceeding their quota for " + mat.getName() + ".");
            }
        }
    }
    //</editor-fold>
//
    // <editor-fold defaultstate="collapsed" desc="Change place and overwrite permission checkers">
    /**
     * Checks if the block can be overwritten by TinyWE
     * @param block
     * @return
     */
    public boolean canOverwriteBlock(Block block) {
        // Perm still overrides world settings!
        if (getPlayer().hasPermission(Permission.tinywe_override_useanywhere)) {
             return true;
        }

        IdpWorld world = IdpWorldFactory.getWorld(block.getWorld().getName());

        // Check for worldsettings
        switch (world.getWorldType()) {
            case DYNAMIC:
                IdpDynamicWorldSettings settings = (IdpDynamicWorldSettings) world.getSettings();
                if (!settings.isWorldeditAllowed()) {
                    return false;
                }
                break;
            case RESWORLD:
                // Do not allow blocks below Y coordinate 34 in the reszone
                if (block.getY() < 34) {
                    return false;
                }

                break;
            case NONE:
                return false;
        }

        IdpMaterial mat = IdpMaterial.fromBlock(block);

        // Don't allow waypoints
        if (mat == IdpMaterial.LAPIS_LAZULI_OREBLOCK) {
            InnectisWaypoint waypoint = WaypointHandler.getWaypoint(block.getLocation());

            if (waypoint != null) {
                return false;
            }
        }

        // Disallow these. It would be bad to allow them!
        switch (mat) {
            case BEDROCK:
            case BARRIER:
            case STRUCTURE_VOID:
            case STRUCTURE_BLOCK_LOAD:
            case STRUCTURE_BLOCK_SAVE:
            case STRUCTURE_BLOCK_DATA:
            case STRUCTURE_BLOCK_CORNER:
            case BED_BLOCK:
            case MOB_SPAWNER:
            case PISTON_MOVING_PIECE:
            case PISTON_EXTENSION:
            case CHEST:
            case TRAPPED_CHEST:
            case HOPPER:
            case IRON_DOOR_BLOCK:
            case OAK_DOOR_BLOCK:
            case BIRCH_DOOR_BLOCK:
            case SPRUCE_DOOR_BLOCK:
            case JUNGLE_DOOR_BLOCK:
            case ACACIA_DOOR_BLOCK:
            case DARK_OAK_DOOR_BLOCK:
            case PORTAL:
            case END_PORTAL:
            case END_GATEWAY:
            case SUGAR_CANE:
                return false;
        }

        // Disallow all shulker boxes
        if (mat.isTypeOf(IdpMaterial.WHITE_SHULKER_BOX)) {
            return false;
        }

        // Check blockhandler aswell
        if (BlockHandler.canBreakBlock(getPlayer(), block.getLocation(), true) != null) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the block can be processed by TinyWE
     *
     * @param block
     * @return
     */
    public boolean canProcessBlock(Block block) {
        // Perm still overrides world settings!
        if (getPlayer().hasPermission(Permission.tinywe_override_useanywhere)) {
            return true;
        }

        IdpWorld world = IdpWorldFactory.getWorld(block.getWorld().getName());

        // Check for worldsettings
        switch (world.getWorldType()) {
            case DYNAMIC:
                IdpDynamicWorldSettings settings = (IdpDynamicWorldSettings) world.getSettings();
                if (!settings.isWorldeditAllowed()) {
                    return false;
        }
                break;

            case NONE:
                return false;
        }

        if (BlockHandler.getIdpBlockData(block.getLocation()).isVirtualBlock()) {
            return false;
        }

        IdpMaterial mat = IdpMaterial.fromBlock(block);

        // Disallow ores, with the exception of coal and iron
        if (mat.isOre()) {
            return (mat == IdpMaterial.COAL_ORE || mat == IdpMaterial.IRON_ORE);
        }

        // Disallow these. It would be bad to allow them!
        // These only include blocks that aren't part of the
        // global deny list (see canOverwriteBlock() logic)
        switch (mat) {
            case WATER:
            case LAVA:
            case GLASS:
            case GLASS_PANE:
            case WEB:
            case GLASS_PANE_WHITE:
            case GLASS_PANE_ORANGE:
            case GLASS_PANE_MAGENTA:
            case GLASS_PANE_LIGHT_BLUE:
            case GLASS_PANE_YELLOW:
            case GLASS_PANE_LIME:
            case GLASS_PANE_PINK:
            case GLASS_PANE_GRAY:
            case GLASS_PANE_LIGHT_GRAY:
            case GLASS_PANE_CYAN:
            case GLASS_PANE_PURPLE:
            case GLASS_PANE_BLUE:
            case GLASS_PANE_BROWN:
            case GLASS_PANE_GREEN:
            case GLASS_PANE_RED:
            case GLASS_PANE_BLACK:
            case GLASS_STAINED_WHITE:
            case GLASS_STAINED_ORANGE:
            case GLASS_STAINED_MAGENTA:
            case GLASS_STAINED_LIGHT_BLUE:
            case GLASS_STAINED_YELLOW:
            case GLASS_STAINED_LIME:
            case GLASS_STAINED_PINK:
            case GLASS_STAINED_GRAY:
            case GLASS_STAINED_LIGHT_GRAY:
            case GLASS_STAINED_CYAN:
            case GLASS_STAINED_PURPLE:
            case GLASS_STAINED_BLUE:
            case GLASS_STAINED_BROWN:
            case GLASS_STAINED_GREEN:
            case GLASS_STAINED_RED:
            case GLASS_STAINED_BLACK:
            case TALL_GRASS:
            case SHRUBS:
            case FIRE:
            case FARMLAND:
            case SNOW_LAYER:
            case CAKE:
            case MONSTER_BLOCK_STONE:
            case MONSTER_BLOCK_COBBLE:
            case MONSTER_BLOCK_BRICK:
            case MONSTER_BLOCK_MOSSYBRICK:
            case MONSTER_BLOCK_CRACKSTONE:
            case MONSTER_BLOCK_CHISELBRICK:
            case BROWN_MUSHROOM_BLOCK:
            case RED_MUSHROOM_BLOCK:
            case END_PORTAL_FRAME:
            case DRAGON_EGG:
            case ENDER_CHEST:
            case TRIPWIRE_HOOK:
            case TRIPWIRE:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case ORANGE_TULIP:
            case WHITE_TULIP:
            case PINK_TULIP:
            case OXEYE_DAISY:
            case SUNFLOWER:
            case LILAC:
            case DOUBLE_TALLGRASS:
            case LARGE_FERN:
            case ROSE_BUSH:
            case PEONY:
            case ICE:
            case PACKED_ICE:
            case FROSTED_ICE:
                return false;

            case STATIONARY_WATER:
            case STATIONARY_LAVA:
                return getPlayer().hasPermission(Permission.tinywe_returnwaterlavablocks);

        }

        return true;
    }

    /**
     * Checks if the users is allowed to use this block to set in a region.
     *
     * @param mat
     * @return
     */
    public boolean canPlaceMaterial(IdpMaterial mat) {
        // Only allow blocks
        if (!mat.isBlock()) {
            return false;
        }

        // Check special permission
        if (getPlayer().hasPermission(Permission.tinywe_override_useanyblock)) {
            return true;
        }

        switch (mat) {
            case OAK_SAPLING:
            case SPRUCE_SAPLING:
            case BIRCH_SAPLING:
            case JUNGLE_SAPLING:
            case ACACIA_SAPLING:
            case DARK_OAK_SAPLING:
            case BEDROCK:
            case BARRIER:
            case LAVA:
            case STATIONARY_LAVA:
            case BED_BLOCK:
            case POWERED_RAIL:
            case DETECTOR_RAIL:
            case WEB:
            case TALL_GRASS:
            case SHRUBS:
            case PISTON_EXTENSION:
            case PISTON_MOVING_PIECE:
            case DANDELION:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case ORANGE_TULIP:
            case WHITE_TULIP:
            case PINK_TULIP:
            case OXEYE_DAISY:
            case SUNFLOWER:
            case LILAC:
            case ROSE_BUSH:
            case PEONY:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case TNT:
            case TORCH:
            case FIRE:
            case MOB_SPAWNER:
            case CHEST:
            case TRAPPED_CHEST:
            case HOPPER:
            case REDSTONE_WIRE:
            case SEEDS:
            case SIGN:
            case OAK_DOOR_BLOCK:
            case BIRCH_DOOR_BLOCK:
            case SPRUCE_DOOR_BLOCK:
            case JUNGLE_DOOR_BLOCK:
            case ACACIA_DOOR_BLOCK:
            case DARK_OAK_DOOR_BLOCK:
            case LADDER:
            case RAILS:
            case SIGN_POST:
            case LEVER:
            case IRON_DOOR_BLOCK:
            case STONE_PRESSURE_PLATE:
            case WOODEN_PRESSURE_PLATE:
            case LIGHT_PRESSURE_PLATE:
            case HEAVY_PRESSURE_PLATE:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
            case STONE_BUTTON:
            case WOOD_BUTTON:
            case SNOW_LAYER:
            case CACTUS:
            case SUGAR_CANE:
            case PORTAL:
            case CAKE:
            case REDSTONE_REPEATER_OFF:
            case REDSTONE_REPEATER_ON:
            case REDSTONE_COMPARATOR_ON:
            case REDSTONE_COMPARATOR_OFF:
            case TRAP_DOOR:
            case IRON_TRAP_DOOR:
            case MONSTER_BLOCK_STONE:
            case MONSTER_BLOCK_COBBLE:
            case MONSTER_BLOCK_BRICK:
            case MONSTER_BLOCK_MOSSYBRICK:
            case MONSTER_BLOCK_CRACKSTONE:
            case MONSTER_BLOCK_CHISELBRICK:
            case BROWN_MUSHROOM_BLOCK:
            case RED_MUSHROOM_BLOCK:
            case PUMPKIN_STEM:
            case MELON_STEM:
            case VINES:
            case MYCELIUM:
            case LILY_PAD:
            case NETHER_WART:
            case ENCHANTMENT_TABLE:
            case BREWING_STAND:
            case CAULDRON_BLOCK:
            case END_PORTAL:
            case END_PORTAL_FRAME:
            case END_STONE:
            case DRAGON_EGG:
                return false;

            // check special materials
            case LOCKED:
            case UN_LOCKED:
            case VIRTUAL:
            case NON_VIRTUAL:
                return false;

            // Check water perms
            case WATER:
            case STATIONARY_WATER:
                return getPlayer().hasPermission(Permission.tinywe_place_water);
        }

        return true;
    }
    // </editor-fold>

    /**
     * Checks if the material is special
     * @param mat
     * @return
     */
    public boolean isSpecialMaterial(IdpMaterial mat) {
        switch (mat) {
            case STATIONARY_LAVA:
            case STATIONARY_WATER:
            case SKULL_BLOCK:
            case FLOWERPOT_BLOCK:
                return true;
        }

        return false;
    }

    /**
     * Attempts to handle a block in a special manner
     * @param mat
     * @return true if the special item was given to the
     * player, false otherwise
     */
    public SpecialItemHandleResult handleSpecialBlock(Block block) {
        IdpPlayer player = getPlayer();
        IdpMaterial mat = IdpMaterial.fromBlock(block);
        IdpItemStack stack = null;
        int remain = 0;

        switch (mat) {
            case STATIONARY_LAVA:
                if (player.removeItemFromInventory(IdpMaterial.BUCKET, 1)) {
                    player.addItemToInventory(IdpMaterial.LAVA_BUCKET, 1);
                }

                return SpecialItemHandleResult.SPECIAL_HANDLED;
            case STATIONARY_WATER:
                if (player.removeItemFromInventory(IdpMaterial.BUCKET, 1)) {
                    player.addItemToInventory(IdpMaterial.WATER_BUCKET, 1);
                }

                return SpecialItemHandleResult.SPECIAL_HANDLED;
            case SKULL_BLOCK:
                Skull skull = (Skull) block.getState();
                SkullType type = skull.getSkullType();

                if (skull.getSkullType() == SkullType.PLAYER) {
                    OfflinePlayer owner = skull.getOwningPlayer();
                    stack = new IdpItemStack(IdpMaterial.PLAYER_SKULL, 1);

                    if (owner != null) {
                        ItemData data = stack.getItemdata();
                        data.setMobheadName(owner.getName());
                    }
                } else {
                    stack = new IdpItemStack(IdpMaterial.PLAYER_SKULL, 1, type.ordinal());
                }

                remain = player.addItemToInventory(stack);

                if (remain > 0) {
                    return SpecialItemHandleResult.SPECIAL_NOT_HANDLED;
                } else {
                    return SpecialItemHandleResult.SPECIAL_HANDLED;
                }
            case FLOWERPOT_BLOCK:
                stack = new IdpItemStack(IdpMaterial.FLOWER_POT, 1);

                IdpPlayerInventory playerInv = player.getInventory();
                IdpContainer container = new IdpContainer(playerInv);
                container.addMaterial(playerInv.getOffHandItem());

                FlowerPot flowerpot = (FlowerPot) block.getState();
                boolean updateInventory = false;

                remain = container.addMaterialToStack(stack);

                if (remain == 0) {
                    MaterialData contents = flowerpot.getContents();

                    if (contents != null) {
                        IdpItemStack contentStack = IdpItemStack.fromBukkitItemStack(contents.toItemStack());

                        // @todo: hack to fix item stack returning with amount 0, remove when fixed in bukkit
                        contentStack.setAmount(1);

                        remain = container.addMaterialToStack(contentStack);

                        if (remain == 0) {
                            updateInventory = true;
                        }
                    } else {
                        updateInventory = true;
                    }
                }

                if (updateInventory) {
                    playerInv.setItems(container.getNonArmorItems());
                    playerInv.setOffHandItem(container.getItemAt(36)); // temporary till off-hand supports inventories of size 37
                    playerInv.updateBukkitInventory();

                    // Make sure to remove the contents of the flower pot as
                    // they will drop otherwise
                    flowerpot.setContents(null);
                    flowerpot.update();

                    return SpecialItemHandleResult.SPECIAL_HANDLED;
                } else {
                    return SpecialItemHandleResult.SPECIAL_NOT_HANDLED;
                }
        }

        return SpecialItemHandleResult.NOT_SPECIAL;
    }

    /**
     * Gets a return item stack from the given material
     * This does not handle skull blocks, that is taken
     * care of in the handleSpecialItem() method
     *
     * @param state The state of a block, of which a
     * material is determined with its respective amount
     * @return
     */
    public IdpItemStack getReturnStack(BlockState state) {
        if (state instanceof Skull) {
            Skull skull = (Skull) state;
            IdpMaterial skullMaterial = null;

            switch (skull.getSkullType()) {
                case PLAYER:
                    skullMaterial = IdpMaterial.PLAYER_SKULL;
                    break;
                case CREEPER:
                    skullMaterial = IdpMaterial.CREEPER_SKULL;
                    break;
                case WITHER:
                    skullMaterial = IdpMaterial.WITHER_SKELETON_SKULL;
                    break;
                case ZOMBIE:
                    skullMaterial = IdpMaterial.ZOMBIE_SKULL;
                    break;
                case DRAGON:
                    skullMaterial = IdpMaterial.DRAGON_SKULL;
                    break;
            }

            IdpItemStack returnStack = new IdpItemStack(skullMaterial, 1);

            if (skull.getSkullType() == SkullType.PLAYER) {
                ItemData itemdata = returnStack.getItemdata();
                OfflinePlayer owner = skull.getOwningPlayer();

                if (owner != null) {
                    itemdata.setMobheadName(owner.getName());
                }
            }

            return returnStack;
        } else if (state instanceof Banner) {
            Banner banner = (Banner) state;
            IdpMaterial bannerMaterial = IdpMaterial.fromID(IdpMaterial.BANNER_BLACK.getId(), banner.getBaseColor().getDyeData());
            IdpItemStack returnStack = new IdpItemStack(bannerMaterial, 1);

            ItemData itemdata = returnStack.getItemdata();
            itemdata.setBannerBaseColor(banner.getBaseColor());
            itemdata.setBannerPatterns(banner.getPatterns());

            return returnStack;
        } else {
            Block block = state.getBlock();
            IdpMaterial mat = IdpMaterial.fromFilteredBlock(block);
            byte dat = BlockHandler.getBlockData(block);

            switch (mat) {
                case AIR:
                    return null;
            }

            IdpMaterial convertMaterial = mat;
            int amount = 1;

            // @todo: return proper item amount on fully grown crops?
            switch (mat) {
                case DOUBLE_STONE_SLAB:
                    convertMaterial = IdpMaterial.STONE_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_SANDSTONE_SLAB:
                    convertMaterial = IdpMaterial.SANDSTONE_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_WOOD_SLAB:
                    convertMaterial = IdpMaterial.WOOD_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_COBBLE_SLAB:
                    convertMaterial = IdpMaterial.COBBLE_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_BRICK_SLAB:
                    convertMaterial = IdpMaterial.BRICK_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_STONE_BRICK_SLAB:
                    convertMaterial = IdpMaterial.STONE_BRICK_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_NETHER_BRICK_SLAB:
                    convertMaterial = IdpMaterial.NETHER_BRICK_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_OAK_WOOD_SLAB:
                    convertMaterial = IdpMaterial.OAK_WOOD_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_BIRCH_WOOD_SLAB:
                    convertMaterial = IdpMaterial.BIRCH_WOOD_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_SPRUCE_WOOD_SLAB:
                    convertMaterial = IdpMaterial.SPRUCE_WOOD_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_JUNGLE_WOOD_SLAB:
                    convertMaterial = IdpMaterial.JUNGLE_WOOD_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_ACACIA_WOOD_SLAB:
                    convertMaterial = IdpMaterial.ACACIA_WOOD_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_DARKOAK_WOOD_SLAB:
                    convertMaterial = IdpMaterial.DARK_OAK_WOOD_SLAB;
                    amount = 2;
                    break;
                case DBL_RED_SANDSTONE_SLAB:
                case SMTH_DBL_RED_SAND_SLAB:
                    convertMaterial = IdpMaterial.RED_SANDSTONE_SLAB;
                    amount = 2;
                    break;
                case DOUBLE_PURPUR_SLAB:
                    convertMaterial = IdpMaterial.PURPUR_SLAB;
                    amount = 2;
                    break;
                case UP_STONE_SLAB:
                    convertMaterial = IdpMaterial.STONE_SLAB;
                    break;
                case UP_SANDSTONE_SLAB:
                    convertMaterial = IdpMaterial.SANDSTONE_SLAB;
                    break;
                case UP_COBBLE_SLAB:
                    convertMaterial = IdpMaterial.COBBLE_SLAB;
                    break;
                case UP_BRICK_SLAB:
                    convertMaterial = IdpMaterial.BRICK_SLAB;
                    break;
                case UP_STONE_BRICK_SLAB:
                    convertMaterial = IdpMaterial.STONE_BRICK_SLAB;
                    break;
                case UP_NETHER_BRICK_SLAB:
                    convertMaterial = IdpMaterial.NETHER_BRICK_SLAB;
                    break;
                case UP_QUARTZ_SLAB:
                    convertMaterial = IdpMaterial.QUARTZ_SLAB;
                    break;
                case UP_BIRCH_WOOD_SLAB:
                    convertMaterial = IdpMaterial.BIRCH_WOOD_SLAB;
                    break;
                case UP_JUNGLE_WOOD_SLAB:
                    convertMaterial = IdpMaterial.JUNGLE_WOOD_SLAB;
                    break;
                case UP_SPRUCE_WOOD_SLAB:
                    convertMaterial = IdpMaterial.SPRUCE_WOOD_SLAB;
                    break;
                case UP_OAK_WOOD_SLAB:
                    convertMaterial = IdpMaterial.OAK_WOOD_SLAB;
                    break;
                case UP_ACACIA_WOOD_SLAB:
                    convertMaterial = IdpMaterial.ACACIA_WOOD_SLAB;
                    break;
                case UP_DARK_OAK_WOOD_SLAB:
                    convertMaterial = IdpMaterial.DARK_OAK_WOOD_SLAB;
                    break;
                case UP_RED_SANDSTONE_SLAB:
                    convertMaterial = IdpMaterial.RED_SANDSTONE_SLAB;
                    break;
                case UP_PURPUR_SLAB:
                    convertMaterial = IdpMaterial.PURPUR_SLAB;
                    break;

                // ALLOW SIDE LOG WORLD EDITTING!
                case SIDE_OAK_LOG_EAST:
                case SIDE_OAK_LOG_NORTH:
                case BARK_OAK_LOG:
                    convertMaterial = IdpMaterial.OAK_LOG;
                    break;
                case SIDE_SPRUCE_LOG_EAST:
                case SIDE_SPRUCE_LOG_NORTH:
                case BARK_SPRUCE_LOG:
                    convertMaterial = IdpMaterial.SPRUCE_LOG;
                    break;
                case SIDE_BIRCH_LOG_EAST:
                case SIDE_BIRCH_LOG_NORTH:
                case BARK_BIRCH_LOG:
                    convertMaterial = IdpMaterial.BIRCH_LOG;
                    break;
                case SIDE_JUNGLE_LOG_EAST:
                case SIDE_JUNGLE_LOG_NORTH:
                case BARK_JUNGLE_LOG:
                    convertMaterial = IdpMaterial.JUNGLE_LOG;
                    break;
                case SIDE_ACACIA_LOG_EAST:
                case SIDE_ACACIA_LOG_NORTH:
                case BARK_ACACIA_LOG:
                    convertMaterial = IdpMaterial.ACACIA_LOG;
                    break;
                case SIDE_DARK_OAK_LOG_EAST:
                case SIDE_DARK_OAK_LOG_NORTH:
                case BARK_DARK_OAK_LOG:
                    convertMaterial = IdpMaterial.DARK_OAK_LOG;
                    break;

                case TRIPWIRE:
                    convertMaterial = IdpMaterial.STRING;
                    break;
                case WHEAT_BLOCK:
                    convertMaterial = (dat == 7 ? IdpMaterial.WHEAT : IdpMaterial.SEEDS);
                    break;
                case INVERTED_DAY_DETECTOR:
                    convertMaterial = IdpMaterial.DAYLIGHT_DETECTOR;
                    break;
                case FARMLAND:
                    convertMaterial = IdpMaterial.DIRT;
                    break;
                case SIGN_POST:
                case WALL_SIGN:
                    convertMaterial = IdpMaterial.SIGN;
                    break;
                case REDSTONE_WIRE:
                    convertMaterial = IdpMaterial.REDSTONE_DUST;
                    break;
                case NETHER_WART:
                    convertMaterial = IdpMaterial.NETHER_WART_ITEM;
                    break;
                case SUGAR_CANE:
                    convertMaterial = IdpMaterial.SUGAR_CANE_ITEM;
                    break;
                case CAKE:
                    convertMaterial = IdpMaterial.CAKE_ITEM;
                    break;
                case BEETROOT_BLOCK:
                    convertMaterial = (dat == 3 ? IdpMaterial.BEETROOT : IdpMaterial.BEETROOT_SEEDS);
                    break;
                case BED_BLOCK:
                    convertMaterial = IdpMaterial.BED_ITEM;
                    break;
                case REDSTONE_LAMP_ON:
                    convertMaterial = IdpMaterial.REDSTONE_LAMP_OFF;
                    break;
                case REDSTONE_REPEATER_ON:
                case REDSTONE_REPEATER_OFF:
                    convertMaterial = IdpMaterial.REDSTONE_REPEATER;
                    break;
                case REDSTONE_COMPARATOR_ON:
                case REDSTONE_COMPARATOR_OFF:
                    convertMaterial = IdpMaterial.REDSTONE_COMPARATOR;
                    break;
                case PUMPKIN_STEM:
                    convertMaterial = IdpMaterial.PUMPKIN_SEEDS;
                    break;
                case MELON_STEM:
                    convertMaterial = IdpMaterial.MELON_SEEDS;
                    break;
                case BREWING_STAND:
                    convertMaterial = IdpMaterial.BREWING_STAND_ITEM;
                    break;
                case CAULDRON_BLOCK:
                    convertMaterial = IdpMaterial.CAULDRON_ITEM;
                    break;
                case CARROT_BLOCK:
                    convertMaterial = IdpMaterial.CARROT;
                    break;
                case POTATO_BLOCK:
                    convertMaterial = IdpMaterial.POTATO;
                    break;
                case COCOA_PLANT:
                    convertMaterial = IdpMaterial.COCOA_BEANS;
                    break;
                case FLOWERPOT_BLOCK:
                    convertMaterial = IdpMaterial.FLOWER_POT;
                    break;
                case STONE:
                    convertMaterial = IdpMaterial.COBBLESTONE;
                    break;
                case COARSE_DIRT:
                case GRASS:
                case MYCELIUM:
                case PODZOL:
                    convertMaterial = IdpMaterial.DIRT;
                    break;
                case EMERALD_ORE:
                    convertMaterial = IdpMaterial.EMERALD;
                    break;
                case DIAMOND_ORE:
                    convertMaterial = IdpMaterial.DIAMOND;
                    break;
                case COAL_ORE:
                    convertMaterial = IdpMaterial.COAL;
                    break;
            }

            if (!convertMaterial.isInventoryItem()) {
                InnPlugin.logError("Material not converted to inventory item: " + convertMaterial);
                return null;
            }

            return new IdpItemStack(convertMaterial, amount);
        }
    }

    /**
     * Checks if the player has the specified item in their inventory
     * @param mat
     * @return
     */
    public boolean hasItemInInventory(IdpMaterial mat) {
        IdpItemStack stack = convertMaterialToItemStack(mat);
        return getPlayer().hasItemInInventory(stack.getMaterial(), stack.getAmount());
    }

    /**
     * Converts and removes the specified material from the
     * player's inventory
     * @param state the state of a block, of which a material
     * is derived from, or a player skull
     * @return
     */
    public boolean removeItemFromPlayer(BlockState state) {
        if (state instanceof Skull) {
            Skull skull = (Skull) state;
            SkullType type = skull.getSkullType();

            // If player head, check for player name
            if (type == SkullType.PLAYER) {
                OfflinePlayer ownerPlayer = skull.getOwningPlayer();
                String owner = (ownerPlayer != null ? ownerPlayer.getName() : "");

                return getPlayer().removePlayerSkullByOwner(owner);
            } else {
                IdpMaterial headType = null;

                switch (type) {
                    case SKELETON:
                        headType = IdpMaterial.SKELETON_SKULL;
                        break;
                    case WITHER:
                        headType = IdpMaterial.WITHER_SKELETON_SKULL;
                        break;
                    case ZOMBIE:
                        headType = IdpMaterial.ZOMBIE_SKULL;
                        break;
                    case CREEPER:
                        headType = IdpMaterial.CREEPER_SKULL;
                        break;
                    case DRAGON:
                        headType = IdpMaterial.DRAGON_SKULL;
                        break;
                }

                IdpItemStack stack = new IdpItemStack(headType, 1);
                return getPlayer().removeItemFromInventory(stack);
            }
        } else if (state instanceof Banner) {
            Banner banner = (Banner) state;
            return getPlayer().removeBannerByPattern(banner);
        }

        IdpMaterial mat = IdpMaterial.fromFilteredBlock(state.getBlock());
        return removeItemFromPlayer(mat);
    }

    /**
     * Converts and removes the specified material from the
     * player's inventory
     * @param mat
     * @return
     */
    public boolean removeItemFromPlayer(IdpMaterial mat) {
        IdpItemStack stack = convertMaterialToItemStack(mat);
        return getPlayer().removeItemFromInventory(stack);
    }

    /**
     * Adds the specified itemstack to the player
     * @param stack
     * @return the amount of items remaining, if any
     */
    public int addItemToPlayer(IdpItemStack stack) {
        return getPlayer().addItemToInventory(stack, true);
    }

    /**
     * Converts the specified material into the appropriate item stack
     * @param mat
     * @return
     */
    private IdpItemStack convertMaterialToItemStack(IdpMaterial mat) {
        int amount = 1;
        IdpMaterial convertMaterial = mat;

        switch (mat) {
            case DOUBLE_STONE_SLAB:
                convertMaterial = IdpMaterial.STONE_SLAB;
                amount = 2;
                break;
            case DOUBLE_SANDSTONE_SLAB:
                convertMaterial = IdpMaterial.SANDSTONE_SLAB;
                amount = 2;
                break;
            case DOUBLE_WOOD_SLAB:
                convertMaterial = IdpMaterial.WOOD_SLAB;
                amount = 2;
                break;
            case DOUBLE_COBBLE_SLAB:
                convertMaterial = IdpMaterial.COBBLE_SLAB;
                amount = 2;
                break;
            case DOUBLE_BRICK_SLAB:
                convertMaterial = IdpMaterial.BRICK_SLAB;
                amount = 2;
                break;
            case DOUBLE_STONE_BRICK_SLAB:
                convertMaterial = IdpMaterial.STONE_BRICK_SLAB;
                amount = 2;
                break;
            case DOUBLE_NETHER_BRICK_SLAB:
                convertMaterial = IdpMaterial.NETHER_BRICK_SLAB;
                amount = 2;
                break;
            case DOUBLE_OAK_WOOD_SLAB:
                convertMaterial = IdpMaterial.OAK_WOOD_SLAB;
                amount = 2;
                break;
            case DOUBLE_BIRCH_WOOD_SLAB:
                convertMaterial = IdpMaterial.BIRCH_WOOD_SLAB;
                amount = 2;
                break;
            case DOUBLE_SPRUCE_WOOD_SLAB:
                convertMaterial = IdpMaterial.SPRUCE_WOOD_SLAB;
                amount = 2;
                break;
            case DOUBLE_JUNGLE_WOOD_SLAB:
                convertMaterial = IdpMaterial.JUNGLE_WOOD_SLAB;
                amount = 2;
                break;
            case DOUBLE_ACACIA_WOOD_SLAB:
                convertMaterial = IdpMaterial.ACACIA_WOOD_SLAB;
                amount = 2;
                break;
            case DOUBLE_DARKOAK_WOOD_SLAB:
                convertMaterial = IdpMaterial.DARK_OAK_WOOD_SLAB;
                amount = 2;
                break;
            case DBL_RED_SANDSTONE_SLAB:
            case SMTH_DBL_RED_SAND_SLAB:
                convertMaterial = IdpMaterial.RED_SANDSTONE_SLAB;
                amount = 2;
                break;
            case DOUBLE_PURPUR_SLAB:
                convertMaterial = IdpMaterial.PURPUR_SLAB;
                amount = 2;
                break;
            case UP_STONE_SLAB:
                convertMaterial = IdpMaterial.STONE_SLAB;
                break;
            case UP_SANDSTONE_SLAB:
                convertMaterial = IdpMaterial.SANDSTONE_SLAB;
                break;
            case UP_COBBLE_SLAB:
                convertMaterial = IdpMaterial.COBBLE_SLAB;
                break;
            case UP_BRICK_SLAB:
                convertMaterial = IdpMaterial.BRICK_SLAB;
                break;
            case UP_STONE_BRICK_SLAB:
                convertMaterial = IdpMaterial.STONE_BRICK_SLAB;
                break;
            case UP_NETHER_BRICK_SLAB:
                convertMaterial = IdpMaterial.NETHER_BRICK_SLAB;
                break;
            case UP_QUARTZ_SLAB:
                convertMaterial = IdpMaterial.QUARTZ_SLAB;
                break;
            case UP_OAK_WOOD_SLAB:
                convertMaterial = IdpMaterial.OAK_WOOD_SLAB;
                break;
            case UP_BIRCH_WOOD_SLAB:
                convertMaterial = IdpMaterial.BIRCH_WOOD_SLAB;
                break;
            case UP_JUNGLE_WOOD_SLAB:
                convertMaterial = IdpMaterial.JUNGLE_WOOD_SLAB;
                break;
            case UP_ACACIA_WOOD_SLAB:
                convertMaterial = IdpMaterial.ACACIA_WOOD_SLAB;
                break;
            case UP_DARK_OAK_WOOD_SLAB:
                convertMaterial = IdpMaterial.DARK_OAK_WOOD_SLAB;
                break;
            case UP_RED_SANDSTONE_SLAB:
                convertMaterial = IdpMaterial.RED_SANDSTONE_SLAB;
                break;
            case UP_PURPUR_SLAB:
                convertMaterial = IdpMaterial.PURPUR_SLAB;
                break;
        }

        return new IdpItemStack(convertMaterial, amount);
    }

    /**
     * Convert materials that cannot be found in a
     * player's inventory to the proper ones a
     * player has
     * @param mat
     * @return
     */
    public IdpMaterial getInventoryMaterial(IdpMaterial mat) {
        switch (mat) {
            // Convert upwards slabs to their normal equivalent
            case UP_STONE_SLAB:
                return IdpMaterial.STONE_SLAB;
            case UP_SANDSTONE_SLAB:
                return IdpMaterial.SANDSTONE_SLAB;
            case UP_WOOD_SLAB:
                return IdpMaterial.WOOD_SLAB;
            case UP_COBBLE_SLAB:
                return IdpMaterial.COBBLE_SLAB;
            case UP_BRICK_SLAB:
                return IdpMaterial.BRICK_SLAB;
            case UP_STONE_BRICK_SLAB:
                return IdpMaterial.STONE_BRICK_SLAB;
            case UP_NETHER_BRICK_SLAB:
                return IdpMaterial.NETHER_BRICK_SLAB;
            case UP_OAK_WOOD_SLAB:
                return IdpMaterial.OAK_WOOD_SLAB;
            case UP_SPRUCE_WOOD_SLAB:
                return IdpMaterial.SPRUCE_WOOD_SLAB;
            case UP_BIRCH_WOOD_SLAB:
                return IdpMaterial.BIRCH_WOOD_SLAB;
            case UP_JUNGLE_WOOD_SLAB:
                return IdpMaterial.JUNGLE_WOOD_SLAB;
            case UP_ACACIA_WOOD_SLAB:
                return IdpMaterial.ACACIA_WOOD_SLAB;
            case UP_DARK_OAK_WOOD_SLAB:
                return IdpMaterial.DARK_OAK_WOOD_SLAB;
            case UP_RED_SANDSTONE_SLAB:
                return IdpMaterial.RED_SANDSTONE_SLAB;
            case UP_PURPUR_SLAB:
                return IdpMaterial.PURPUR_SLAB;

            // Convert normal water and lava to their bucket equivalents
            case STATIONARY_WATER:
            case WATER:
                return IdpMaterial.WATER_BUCKET;
            case STATIONARY_LAVA:
            case LAVA:
                return IdpMaterial.LAVA_BUCKET;
        }

        return mat;
    }

}
