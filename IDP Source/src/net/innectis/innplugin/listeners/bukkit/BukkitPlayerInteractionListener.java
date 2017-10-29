package net.innectis.innplugin.listeners.bukkit;

import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.CTFHandler;
import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.CreateCTFArenaObj;
import net.innectis.innplugin.objects.EntityTraits;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.idp.InnInteractEntityEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerInteractEvent;
import net.innectis.innplugin.listeners.InnBukkitListener;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerSettings;
import net.innectis.innplugin.player.tools.Tool;
import net.innectis.innplugin.tasks.sync.EntityDestroyFancyTask;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.*;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 */
public class BukkitPlayerInteractionListener implements InnBukkitListener {

    private static InnPlugin plugin;
    private IdpPlayerInteractListener idpListener;

    public BukkitPlayerInteractionListener(InnPlugin instance) {
        plugin = instance;
        idpListener = new IdpPlayerInteractListener(instance);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Location loc = event.getBlockClicked().getLocation();
        IdpPlayer player = plugin.getPlayer(event.getPlayer());

        // If not logged in
        if (!player.getSession().isLoggedIn()) {
            player.printError("You are not logged in!");
            event.setCancelled(true);
            return;
        }

        if (event.getItemStack().getType() == Material.MILK_BUCKET) {
            return;
        }

        String errMsg = BlockHandler.canBreakBlock(player, loc, false);
        if (errMsg != null) {
            Block block = event.getBlockClicked();
            player.sendBlockChange(block);
            player.printError(errMsg);
            player.getInventory().updateBukkitInventory();
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        Block block = event.getBlockClicked();

        // If not logged in
        if (!player.getSession().isLoggedIn()) {
            player.printError("You are not logged in!");
            event.setCancelled(true);
            return;
        }

        // Unrestrict reszone
        if (player.getWorld().getActingWorldType() == IdpWorldType.RESWORLD) {
            InnectisLot lot = LotHandler.getLot(player.getLocation());

            // Return if there's no lot here
            if (lot == null) {
                return;
            }
        }

        String errMsg = BlockHandler.canPlaceBlock(player, event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(),
                IdpMaterial.fromBukkitMaterial(event.getBucket()), false);
        if (errMsg == null) {
            if (event.getBucket() == Material.LAVA_BUCKET) {
                //do not allow lava placement if other users around
                if (!player.hasPermission(Permission.build_lava_near_players)) {
                    for (Player p : block.getWorld().getPlayers()) {
                        if (p != player.getHandle()) {
                            if (p.getLocation().distance(block.getLocation()) <= 3) {
                                player.printError("You cannot place lava with users nearby!");
                                event.setCancelled(true);
                                player.getInventory().updateBukkitInventory();
                                return;
                            }
                        }
                    }
                }
            }
        } else {
            player.sendBlockChange(block);
            player.printError(errMsg);
            event.setCancelled(true);
            player.getInventory().updateBukkitInventory();
            return;
        }
    }

    /**
     * When the playerFootballPush interacts with an item
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        EquipmentSlot handSlot = event.getHand();
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        IdpItemStack handStack = IdpItemStack.fromBukkitItemStack(event.getItem());
        IdpMaterial handMat = handStack.getMaterial();
        Block block = event.getClickedBlock();

        InnPlayerInteractEvent idpEvent = new InnPlayerInteractEvent(
                plugin.getPlayer(event.getPlayer()), handSlot, block, handStack, event.getBlockFace(), event.getAction());

        // <-- End Collect stuff

        // If not logged in
        if (!player.getSession().isLoggedIn()) {
            player.printError("You are not logged in!");
            event.setCancelled(true);
            return;
        }

        // Check for secondairy listeners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_INTERACT)) {
            plugin.getListenerManager().fireEvent(idpEvent);
            if (idpEvent.isCancelled()) {
                event.setCancelled(true);
            }
            if (idpEvent.shouldTerminate()) {
                return;
            }
        }

        // Check for tools (DO NOT PUT IN BLOCK STATEMENT!)
        Tool tool = Tool.getTool(idpEvent.getPlayer(), handMat, block);
        if (tool != null && tool.isAllowed()) {
            if (tool.doAction(event.getAction())) {
                event.setCancelled(true);
                return;
            }
        }

        if (block != null) {
            if (player.getSession().checkBlockUse(block, false) && !player.getSession().isStaff()) {
                InnPlugin.getPlugin().broadCastStaffMessage(ChatColor.DARK_GREEN + "[IDP] " + player.getName() + " may be using x-ray!", false);
                InnPlugin.logInfo(player.getName() + " may be using x-ray.");
            }
        }


        // Check if the player is using a special item, then
        // handle it if so
        if (idpEvent.getItem() != null) {
                        if (plugin.getSpecialItemManager().onInteract(player, handSlot, idpEvent.getItem(), idpEvent.getAction(), idpEvent.getBlock())) {
                event.setCancelled(true);
                return;
            }
        }

        CreateCTFArenaObj cobj = CTFHandler.getCreateGameMode(player.getName());

        if (cobj != null && block != null) {
            InnectisLot lot = LotHandler.getLot(block.getLocation());

            if (CTFHandler.isCTFArena(lot)) {
                player.printError("This is already a CTF arena.");
            } else {
                CTFHandler.onCTFAreaCreation(idpEvent, cobj);
            }

            event.setCancelled(true);
            return;
        }

        //<editor-fold defaultstate="collapsed" desc="Check item usage BEFORE LOS check">

        // Before LOS check
        switch (handMat) {
            // Player is attempting to use a potion
            case POTIONS:
            case SPLASH_POTION:
            case LINGERING_POTION: {
                if (idpListener.playerPotionUse(idpEvent)) {
                    event.setCancelled(true);
                    return;
                }
                break;
            }
            // Interacting with a sign
            case SIGN: {
                if (idpListener.playerInteractWithSign(idpEvent)) {
                    event.setCancelled(true);
                    return;
                }
                break;
            }
        }

        //</editor-fold>

        if (block != null) {
            IdpMaterial mat = IdpMaterial.fromBlock(block);
            boolean isInteractable = mat.isInteractable();
            InnectisLot lot = idpEvent.getBlockLot();

            // Only do basic interaction checks with right-click
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                //Line of Sight check
                //VERY IMPORTANT, MUST BE FIRST!
                if (isInteractable) {
                    if (!player.hasLineOfSight(block, 5)) {
                        event.setCancelled(true); //do not allow interaction of a block out of LoS
                        InnPlugin.logMessage(idpEvent.getPlayer().getColoredName() + ChatColor.YELLOW + " interacted with a " + mat.getName() + " not in their line of sight!");

                        if (player.getSession().hasSetting(PlayerSettings.LOS_MESSAGE)) {
                            player.printError("That " + mat.getName() + " is not in your line of sight!");
                        }

                        return;
                    }
                }

                //VERY IMPORTANT, MUST BE SECOND!
                //This is a general interaction check to see whether or not the playerFootballPush is
                //permitted to place/interact with the block
                Block interactBlock = block;
                boolean allowInteract = !isInteractable;
                boolean sneaking = idpEvent.getPlayer().getHandle().isSneaking();

                // If we're right clicking, see if the target block is interactable.
                // If they're crouching, or if it is NOT interactable, set the target block to the relative block
                if (sneaking || !isInteractable) {
                    interactBlock = block.getRelative(event.getBlockFace());
                }

                IdpMaterial interactMaterial = IdpMaterial.fromBlock(interactBlock);

                //always allowed blocks - these must ALWAYS be allowed to be interacted with
                switch (interactMaterial) {
                    case NOTE_BLOCK: //note, dont worry tuning check is handled below
                    case BED_BLOCK:
                    case CHEST:
                    case TRAPPED_CHEST:
                    case WORKBENCH:
                    case WALL_SIGN: // Must be excluded for interactive signs
                    case IRON_TRAP_DOOR: // Iron trap door is an owned object. Access check done later
                    case IRON_DOOR_BLOCK:
                    case CAKE:
                        allowInteract = true;
                        break;
                    case OAK_DOOR_BLOCK:
                    case BIRCH_DOOR_BLOCK:
                    case SPRUCE_DOOR_BLOCK:
                    case JUNGLE_DOOR_BLOCK:
                    case ACACIA_DOOR_BLOCK:
                    case DARK_OAK_DOOR_BLOCK:
                    case OAK_FENCE_GATE:
                    case BIRCH_FENCE_GATE:
                    case SPRUCE_FENCE_GATE:
                    case JUNGLE_FENCE_GATE:
                    case ACACIA_FENCE_GATE:
                    case DARK_OAK_FENCE_GATE:
                    case TRAP_DOOR:
                        if (lot != null && lot.isFlagSet(LotFlagType.LOCKOUTPUT)
                                && !lot.canPlayerManage(player.getName())
                                && !lot.containsMember(player.getName())
                                && !lot.containsMember("%")
                                && !player.hasPermission(Permission.world_build_unrestricted)) {
                            player.printError("This is not the " + interactMaterial.getName() + " you're looking for!");
                            event.setCancelled(true);
                            return;
                        } else {
                            allowInteract = true;
                        }

                        break;
                    case STONE_BUTTON:
                    case LEVER:
                    case WOOD_BUTTON:
                        if (lot != null && lot.isFlagSet(LotFlagType.LOCKINPUT)
                                && !lot.canPlayerManage(player.getName())
                                && !lot.containsMember(player.getName())
                                && !lot.containsMember("%")
                                && !player.hasPermission(Permission.world_build_unrestricted)) {
                            player.printError("This is not the " + interactMaterial.getName() + " you're looking for!");
                            event.setCancelled(true);
                            return;
                        } else {
                            allowInteract = true;
                        }
                        break;
                    case BREWING_STAND:
                    case ANVIL:
                    case ENCHANTMENT_TABLE:
                        // This condition will always allow use of this block
                        boolean canAlwaysUse = ((lot != null && lot.getOwner().equalsIgnoreCase(player.getName()))
                                                    || player.hasPermission(Permission.world_build_unrestricted));

                        if (!canAlwaysUse) {
                            if (interactMaterial == IdpMaterial.ANVIL || interactMaterial == IdpMaterial.ENCHANTMENT_TABLE) {
                                int craftCount = player.getHandle().getStatistic(Statistic.CRAFT_ITEM, interactMaterial.getBukkitMaterial());

                                if (craftCount == 0) {
                                    player.printError("You must craft at least one " + interactMaterial.getName() + ".");
                                    event.setCancelled(true);
                                    return;
                                }
                            } else {
                                // Check for lot access if trying to use brewing stand
                                if (lot != null && !lot.canPlayerAccess(player.getName())) {
                                    player.printError("You don't have permission to use this brewing stand.");
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        }

                        allowInteract = true;
                }

                if (!allowInteract) {
                    // If we can't interact from the list above, then see if they can build with
                    // default IDP build permissions
                    String errMsg = BlockHandler.canPlaceBlock(idpEvent.getPlayer(), interactBlock.getLocation(), handMat, false);
                    if (errMsg != null) {
                        event.setCancelled(true);
                        idpEvent.getPlayer().sendBlockChange(interactBlock);

                        //client thinks it placed water/lava/milk, but this is wrong!
                        if (event.getItem() != null && !event.getItem().getType().isBlock()) {
                            idpEvent.getPlayer().getInventory().updateBukkitInventory();
                        }

                        if (isInteractable && !sneaking) {
                            idpEvent.getPlayer().printError("You cannot use this " + mat.getName() + "!");
                        } else {
                            idpEvent.getPlayer().printError(errMsg);
                        }

                        return;
                    }
                }
            }

            //VERY IMPORTANT, MUST BE THIRD!
            //Container checks (chests, dispensers, furnaces, brewing stands, etc) (NO ENDER CHESTS!)
            if (block.getState() instanceof InventoryHolder && idpEvent.getBlockMaterial() != IdpMaterial.ENDER_CHEST) {
                if (idpListener.playerContainerUse(idpEvent)) {
                    event.setCancelled(true);
                    return;
                }
            }

            // Prevent fire from being extinguished by anyone but lot members or owner
            if (IdpMaterial.fromBlock(block.getRelative(idpEvent.getBlockFace())) == IdpMaterial.FIRE) {
                if (lot != null && !lot.canPlayerAccess(player.getName())
                        && !player.hasPermission(Permission.build_block_fire)) {
                    event.setCancelled(true);
                    return;
                }
            }

            //<editor-fold defaultstate="collapsed" desc="Check item usage AFTER LOS check">
            // Check item usage
            switch (handMat) {
                // Using flint and steel
                case FLINT_AND_TINDER: {
                    if (idpListener.playerFlintAndSteelUse(idpEvent)) {
                        return;
                    }
                    break;
                }

                case FISHING_ROD: {
                    if (idpListener.playerFishrodUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case BONEMEAL: {
                    if (idpListener.playerBonemealUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case FIRE_CHARGE: {
                    if (idpListener.playerFireChargeUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case FIREWORK_ROCKET: {
                    if (idpListener.playerFireworkUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case OAK_BOAT:
                case SPRUCE_BOAT:
                case BIRCH_BOAT:
                case JUNGLE_BOAT:
                case ACACIA_BOAT:
                case DARK_OAK_BOAT:
                case MINECART:
                case POWERED_MINECART:
                case STORAGE_MINECART:
                case TNT_MINECART:
                case HOPPER_MINECART: {
                    if (idpListener.playerVehiclePlace(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }

                    break;
                }

                case SPAWN_EGG:
                    if (idpListener.spawnEggUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Check block usage">

            // Check block usage
            switch (idpEvent.getBlockMaterial()) {
                case REDSTONE_COMPARATOR_ON:
                case REDSTONE_COMPARATOR_OFF:
                case REDSTONE_REPEATER_ON:
                case REDSTONE_REPEATER_OFF: {
                    if (idpListener.playerDiodeUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case WALL_SIGN: {
                    if (idpListener.playerWallSignUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case JUKEBOX: {
                    if (idpListener.playerJukeboxUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case NOTE_BLOCK: {
                    if (idpListener.playerNoteBlockUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case FARMLAND: {
                    if (idpListener.playerSoilTramp(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case ENDER_CHEST: {
                    if (idpListener.playerEnderChestUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case BOOKCASE: {
                    if (idpListener.playerBookcaseUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case CHEST:
                case TRAPPED_CHEST: {
                    if (idpListener.playerChestUse(idpEvent)) {
                        event.setCancelled(idpEvent.isCancelled());
                        return;
                    }
                    break;
                }

                case OAK_DOOR_BLOCK:
                case SPRUCE_DOOR_BLOCK:
                case BIRCH_DOOR_BLOCK:
                case JUNGLE_DOOR_BLOCK:
                case ACACIA_DOOR_BLOCK:
                case DARK_OAK_DOOR_BLOCK:
                case IRON_DOOR_BLOCK: {
                    if (idpListener.playerDoorUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case LAPIS_LAZULI_OREBLOCK: {
                    if (!player.getHandle().isSneaking()) {
                        if (idpListener.playerWaypointUse(idpEvent)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                    break;
                }

                case IRON_TRAP_DOOR: {
                    if (idpListener.playerTrapdoorUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case LEVER: {
                    if (idpListener.playerLeverUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case FIRE: {
                    if (idpListener.playerFireExtinguish(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case IRON_BLOCK:
                case GOLD_BLOCK:
                case DIAMOND_BLOCK:
                case EMERALD_BLOCK: {
                    if (idpListener.playerFootballPush(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                    break;
                }

                case FLOWERPOT_BLOCK: {
                    if (idpListener.playerFlowerPotUse(idpEvent)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            //</editor-fold>
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        EquipmentSlot handSlot = event.getHand();
        Entity entity = event.getRightClicked();
        IdpPlayer player = plugin.getPlayer(event.getPlayer());

        // If not logged in
        if (!player.getSession().isLoggedIn()) {
            player.printError("You are not logged in!");
            event.setCancelled(true);
            return;
        }

        // Check for secondairy listeners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_INTERACT_ENTITY)) {
            InnInteractEntityEvent idpEvent = new InnInteractEntityEvent(plugin.getPlayer(event.getPlayer()), handSlot, entity);
            plugin.getListenerManager().fireEvent(idpEvent);
            if (idpEvent.isCancelled()) {
                event.setCancelled(true);
            }
            if (idpEvent.shouldTerminate()) {
                return;
            }
        }

        // Don't process armor stands here
        if (entity instanceof ArmorStand) {
            return;
        }

        IdpItemStack handStack = player.getItemInHand(handSlot);
        IdpMaterial mat = handStack.getMaterial();

        // Itemeffects
        if (mat != IdpMaterial.AIR) {
            // Only allow item effects to work on living entities
            if (entity instanceof LivingEntity) {
                // Check if the effect was handled.
                if (plugin.getSpecialItemManager().onInteractEntity(player, handSlot, player.getItemInHand(handSlot), (LivingEntity) entity)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // Check if player has rights to put an item inside an item frame, rotate it, etc.
        if (entity instanceof ItemFrame) {
            String errorMsg = BlockHandler.canPlaceBlock(player, entity.getLocation(), IdpMaterial.ITEM_FRAME, false);
            if (errorMsg != null) {
                player.printError(errorMsg);
                event.setCancelled(true);
                return;
            }
        }

        // Catching mobs by interacting with them with a fishing rod
        if (mat == IdpMaterial.FISHING_ROD
                && (entity instanceof Animals || entity instanceof Bat
                || entity instanceof Villager || entity instanceof Squid
                || entity instanceof Snowman || entity instanceof IronGolem
                || player.hasPermission(Permission.entity_catchentitiesall))) {
            InnectisLot lot = LotHandler.getLot(entity.getLocation());

            if (lot == null || lot.canPlayerManage(player.getName())
                    || player.hasPermission(Permission.entity_catchentitiesoverride)) {
                if (player.getWorld().getSettings().getInventoryType() != InventoryType.MAIN
                        && !player.hasPermission(Permission.entity_catchentitiesoverride)) {
                    player.printError("You cannot catch entities here!");
                    return;
                }

                String typeName = "thing";

                if (entity.getType() != EntityType.UNKNOWN) {
                    typeName = entity.getType().toString().toLowerCase();
                }

                if (entity instanceof Tameable) {
                    Tameable tameable = (Tameable) entity;

                    if (tameable.getOwner() != null && !tameable.getOwner().getName().equalsIgnoreCase(player.getName())
                            && !player.hasPermission(Permission.entity_catchentitiesoverride)) {
                        player.printError("This " + typeName + " is tamed. Unable to catch!");
                        event.setCancelled(true);
                        return;
                    }
                }

                EntityTraits traits = EntityTraits.getEntityTraits(entity);

                int result = player.getSession().addCaughtEntityTraits(traits);

                switch (result) {
                    case 2:
                        player.printError("You have already caught this " + typeName + "!");
                        break;
                    case 1:
                        boolean isTamed = (entity instanceof Tameable && ((Tameable) entity).getOwner() != null
                                && ((Tameable) entity).getOwner().getName().equalsIgnoreCase(player.getName()));

                        entity.remove();
                        player.printInfo("Caught a " + (isTamed ? "tamed " : "") + typeName + " for transporting!");
                        break;
                    case 0:
                        player.printError("Can't catch more entities. Drop existing ones off somewhere!");
                        break;
                }

                event.setCancelled(true);
                return;
            }
        }

        // Flint and steel can be used to kill entities in an interesting way
        // without giving any drops
        if (mat == IdpMaterial.FLINT_AND_TINDER) {
            if (((entity instanceof Animals || player.hasPermission(Permission.special_mobs_explodeall))
                    && (!(entity instanceof Tameable) || !((Tameable) entity).isTamed()))
                    && !(entity instanceof Player)) {
                InnectisLot lot = LotHandler.getLot(entity.getLocation());

                // Only allow normally on a lot or off a lot for anyone with permission
                if (player.hasPermission(Permission.world_build_unrestricted)
                        || (lot != null && lot.canPlayerManage(player.getName()))) {
                    Vector velocity = entity.getVelocity();
                    velocity.setY(1.1);
                    entity.setVelocity(velocity);

                    if (player.getHandle().getGameMode() != GameMode.CREATIVE) {
                        int data = handStack.getData();
                        data = data + 1;
                        handStack.setData(data);
                        player.setItemInHand(handSlot, handStack);
                    }

                    plugin.getTaskManager().addTask(new EntityDestroyFancyTask(entity));

                    event.setCancelled(true);
                    return;
                }
            }
        }

        // Kick baton for moderators - kicks any user not an owner of the specified lot to spawn
        if (mat == IdpMaterial.STICK && entity instanceof Player) {
            IdpPlayer kickPlayer = plugin.getPlayer((Player) entity);
            InnectisLot lot = LotHandler.getLot(kickPlayer.getLocation());

            if (lot != null && !kickPlayer.getSession().isStaff()
                    && !lot.canPlayerManage(kickPlayer.getName())
                    && (lot.canPlayerManage(player.getName())
                    || player.hasPermission(Permission.special_stick_kickbatonall))) {
                kickPlayer.teleport(WarpHandler.getSpawn(kickPlayer.getGroup()), TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
                player.printInfo("Kicked " + kickPlayer.getName() + " to spawn!");
                return;
            }
        }

        Location clickLocation = entity.getLocation();

        // Saddle lets players sit on entities
        if (mat == IdpMaterial.SADDLE) {
            if (!player.hasPermission(Permission.entity_sitanycreature)) {
                player.printError("You may not sit on this creature! " + Permission.entity_sitanycreature.getRestrictMessage());
                return;
            }

            InnectisLot lot = LotHandler.getLot(clickLocation, true);

            if (lot != null && lot.isFlagSet(LotFlagType.NOSIT)) {
                player.printError("You cannot sit on creatures here!");
                event.setCancelled(true);
            } else {
                Entity playerPassenger = player.getHandle().getPassenger();

                // Prevent riding an entity already sitting on you
                if (playerPassenger != null && playerPassenger.getUniqueId().equals(entity.getUniqueId())) {
                    player.printError("Cannot sit on an entity sitting on you!");
                    event.setCancelled(true);
                    return;
                }

                Entity passenger = entity.getPassenger();

                if (passenger != null) {
                    // Dismount if the player is riding on this entity
                    if (passenger.getUniqueId().equals(player.getHandle().getUniqueId())) {
                        player.getHandle().leaveVehicle();
                        String name = "thing";

                        if (entity.getType() != EntityType.UNKNOWN) {
                            name = entity.toString().toLowerCase();
                        }

                        player.printInfo("You get off this " + name + "!");
                    } else {
                        boolean isPlayer = (entity instanceof Player);

                        player.printError("This " + (isPlayer ? "player" : "entity") + " already has a passenger!");
                    }

                    return;
                }

                if (entity instanceof Player) {
                    IdpPlayer targplayer = plugin.getPlayer((Player) entity);

                    if (!targplayer.getSession().canSitSaddle()
                            && !player.hasPermission(Permission.entity_sitplayer)) {
                        player.printError("Player has saddle sitting disabled!");
                        return;
                    }

                    entity.setPassenger(player.getHandle());
                    player.printInfo("You sit on " + targplayer.getColoredDisplayName(), ".");
                } else {
                    if (entity instanceof Creature
                            || (entity instanceof Ambient && player.hasPermission(Permission.entity_sitambient))) {
                        entity.setPassenger(player.getHandle());
                        String name = "thing";

                        if (entity.getType() != EntityType.UNKNOWN) {
                            name = entity.getType().toString().toLowerCase();
                        }

                        player.printInfo("You get on this " + name + "!");
                    } else {
                        player.printError("You can't sit on this!");
                        return;
                    }
                }
            }

            return;
        }

        if (mat != null && mat.isBlock() && mat != IdpMaterial.AIR && entity instanceof Enderman
                && player.hasPermission(Permission.entity_changeendermanblock)) {
            Enderman enderman = (Enderman) entity;
            MaterialData mdata = enderman.getCarriedMaterial();
            IdpMaterial carriedMaterial = IdpMaterial.fromMaterialData(mdata);

            // Only give the enderman the item if they are not already carrying it
            if (carriedMaterial != mat) {
                enderman.setCarriedMaterial(new MaterialData(mat.getBukkitMaterial()));
                player.printInfo("This enderman is now carrying " + mat.getName().toLowerCase() + "!");
            }
        }

        if (entity instanceof Animals) {
            switch (mat) {
                case CARROT:
                case SEEDS:
                case WHEAT: {
                    if (!player.hasPermission(Permission.entity_canfeedanywhere)) {
                        InnectisLot lot = LotHandler.getLot(clickLocation, true);
                        boolean canFeedAnimals = (lot == null || !lot.isFlagSet(LotFlagType.FARM) ? true : lot.canPlayerAccess(player.getName()));

                        if (!canFeedAnimals) {
                            player.printError("You cannot feed animals here!");
                            event.setCancelled(true);
                            return;
                        }
                    }
                    break;
                }
                case NAME_TAG: {
                    if (!player.hasPermission(Permission.entity_cannameanywhere)) {
                        InnectisLot lot = LotHandler.getLot(clickLocation, true);
                        boolean canName = lot == null || lot.canPlayerAccess(player.getName())
                                    || player.hasPermission(Permission.world_build_unrestricted);

                        if (!canName) {
                            player.printError("You cannot name animals here!");
                            event.setCancelled(true);
                            return;
                        }

                        if (entity instanceof Tameable) {
                            Tameable tameable = (Tameable) entity;

                            if (tameable.isTamed() && tameable.getOwner() != null && !tameable.getOwner().getName().equalsIgnoreCase(player.getName())
                                    && !player.hasPermission(Permission.world_build_unrestricted)) {
                                EntityType type = entity.getType();

                                String typeName = "thing";

                                if (type != null) {
                                    typeName = type.toString().toLowerCase();
                                }

                                player.printError("You may not rename this tamed " + typeName + "!");
                                event.setCancelled(true);

                                return;
                            }
                        }
                    }
                    break;
                }
                case LEAD: {
                    if (!player.hasPermission(Permission.entity_canleadanywhere)) {
                        InnectisLot lot = LotHandler.getLot(clickLocation, true);
                        boolean canLead = (lot == null || !lot.isFlagSet(LotFlagType.FARM) ? true : lot.canPlayerAccess(player.getName()));

                        if (!canLead) {
                            player.printError("You cannot leash animals here!");
                            event.setCancelled(true);
                            return;
                        }

                        if (entity instanceof Tameable) {
                            Tameable tameable = (Tameable) entity;

                            if (tameable.isTamed() && tameable.getOwner() != null && !tameable.getOwner().getName().equalsIgnoreCase(player.getName())) {
                                EntityType type = entity.getType();

                                String typeName = "thing";

                                if (type != null) {
                                    typeName = type.toString().toLowerCase();
                                }

                                player.printError("You may not leash this tamed " + typeName + "!");
                                return;
                            }
                        }
                    }
                    break;
                }
                case SHEARS: {
                    if ((entity instanceof Sheep || entity instanceof MushroomCow)
                            && !player.hasPermission(Permission.entity_canshearanywhere)) {
                        InnectisLot lot = LotHandler.getLot(clickLocation, true);
                        boolean canShearAnimals = (lot == null || !lot.isFlagSet(LotFlagType.FARM) ? true : lot.canPlayerAccess(player.getName()));

                        if (!canShearAnimals) {
                            player.printError("You cannot shear animals here!");
                            event.setCancelled(true);
                            return;
                        }
                    }
                    break;
                }

                case INK_SAC:
                case DYE_RED:
                case DYE_GREEN:
                case COCOA_BEANS:
                case LAPIS_LAZULI:
                case DYE_PURPLE:
                case DYE_CYAN:
                case DYE_LIGHTGRAY:
                case DYE_GRAY:
                case DYE_PINK:
                case DYE_LIME:
                case DYE_YELLOW:
                case DYE_LIGHTBLUE:
                case DYE_MAGENTA:
                case DYE_ORANGE:
                case BONEMEAL: {
                    InnectisLot lot = LotHandler.getLot(entity.getLocation(), true);

                    // Check lot for farm flag
                    if (lot != null && lot.isFlagSet(LotFlagType.FARM)
                            && !(lot.canPlayerAccess(player.getName()) || player.hasPermission(Permission.entity_recolourallsheeps))
                            && entity instanceof Sheep) {

                        player.printError("Go and brand your own sheep!");
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        if (entity instanceof Vehicle) {
            InnectisLot lot = LotHandler.getLot(clickLocation, true);
            if (lot != null && lot.isFlagSet(LotFlagType.NOSIT)) {
                player.printError("You cannot sit in vehicles here!");
                event.setCancelled(true);
                return;
            }
        }

        if (entity instanceof Tameable) {
            Tameable tameable = (Tameable) entity;
            EntityType type = entity.getType();
            String typeName = "thing";

            if (type != null) {
                typeName = type.toString().toLowerCase();
            }

            String ownername = null;

            if (tameable.isTamed()) {
                if (tameable.getOwner() == null) {
                    player.printInfo("There is no owner of this " + typeName + ", or it is unknown!");
                } else {
                    if (mat == IdpMaterial.SADDLE
                            && !(tameable.getOwner().getName().equalsIgnoreCase(player.getName())
                            || player.hasPermission(Permission.entity_sitanycreature))) {
                        player.printError("Cannot ride this tamed " + typeName + ". You do not own it.");
                        event.setCancelled(true);
                        return;
                    }

                    if (tameable.getOwner() instanceof Player) {
                        Player owner = ((Player) tameable.getOwner());
                        ownername = owner.getName();
                    } else if (tameable.getOwner() instanceof CraftOfflinePlayer) {
                        CraftOfflinePlayer owner = ((CraftOfflinePlayer) tameable.getOwner());
                        ownername = owner.getName();
                    } else {
                        player.printError("Cannot find owner of this " + typeName + "!");
                    }

                    // Do not say anything if the player owns this tamed animal
                    if (ownername != null && !ownername.equalsIgnoreCase(player.getName())) {
                        // Don't echo owner if sneaking here with a horse
                        if (entity instanceof Horse && player.getHandle().isSneaking()) {
                            return;
                        }

                        player.printInfo("The owner of this " + typeName + " is: " + ownername);
                    }
                }
            }

            // Check for different materials
            switch (mat) {
                case INK_SAC:
                case DYE_RED:
                case DYE_GREEN:
                case COCOA_BEANS:
                case LAPIS_LAZULI:
                case DYE_PURPLE:
                case DYE_CYAN:
                case DYE_LIGHTGRAY:
                case DYE_GRAY:
                case DYE_PINK:
                case DYE_LIME:
                case DYE_YELLOW:
                case DYE_LIGHTBLUE:
                case DYE_MAGENTA:
                case DYE_ORANGE:
                case BONEMEAL: {
                    // Only for wolves that are tamed
                    if (entity.getType() == EntityType.WOLF && tameable.isTamed()) {
                        // Check if owner
                        if (ownername == null || !ownername.equalsIgnoreCase(player.getName())) {
                            player.printInfo("This is not your wolf!");
                            event.setCancelled(true);
                            return;
                        }
                    }
                    break;
                }

                case WOOD_SWORD: {
                    if (!tameable.isTamed()) {
                        break;
                    }

                    AnimalTamer tamer = tameable.getOwner();

                    // No owner, so do nothing
                    if (tamer == null || tamer.getName() == null) {
                        break;
                    }

                    // This is the player's tamed animal, so do nothing
                    if (tamer.getName().equalsIgnoreCase(player.getName())) {
                        break;
                    }

                    // No permission, so do nothing
                    if (!player.hasPermission(Permission.entity_controlotherpets)) {
                        break;
                    }

                    CraftTameableAnimal tamed = (CraftTameableAnimal) entity;
                    LivingEntity target = tamed.getTarget();

                    if (target != null && target.equals(player.getHandle())) {
                        tamed.setTarget(null);
                        tamed.setSitting(true);

                        player.printInfo("You command this " + typeName + " to sit still!");
                    } else {
                        // We can't do this if the animal is sitting
                        if (tamed.isSitting()) {
                            tamed.setSitting(false);
                        }

                        tamed.setTarget(player.getHandle());

                        player.printInfo("You command this " + typeName + " to follow you!");
                    }

                    break;
                }
            }
        }
    }

}
