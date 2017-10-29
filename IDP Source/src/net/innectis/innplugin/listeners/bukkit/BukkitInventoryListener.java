package net.innectis.innplugin.listeners.bukkit;

import net.innectis.innplugin.objects.EnderChestContents;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.EnderChestContents.EnderContentsType;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.inventory.IdpInventoryDataHolder;
import net.innectis.innplugin.inventory.payload.InventoryAction;
import net.innectis.innplugin.inventory.payload.InventoryPayload;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.listeners.idp.InnInventoryClickEvent;
import net.innectis.innplugin.listeners.InnBukkitListener;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.handlers.OwnedEntityHandler;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.OwnedEntity;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.system.signs.MaterialSign;
import net.innectis.innplugin.system.signs.SignValidator;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Hopper;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Lynxy
 */
public final class BukkitInventoryListener implements InnBukkitListener {

    private final InnPlugin plugin;

    public BukkitInventoryListener(InnPlugin instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
        InventoryHolder sourceHolder = event.getSource().getHolder();
        InventoryHolder destinationHolder = event.getDestination().getHolder();

        // Check if a hopper minecart is sucking items from a chest or double chest
        if (sourceHolder instanceof Chest || sourceHolder instanceof DoubleChest) {
            // Hopper sucking items out of a chest. This is fine as there was a build check
            // when placing the hopper, so this transaction is valid
            if ((destinationHolder instanceof Hopper)) {
                return;
            }

            // An unknown item sucked an item out of a chest. This should not happen
            // but in case it does, let's catch it
            if (!(destinationHolder instanceof HopperMinecart)) {
                InnPlugin.logError("Unknown destination entity/block attempted to suck items from chest: " + destinationHolder.getInventory().getType());
                event.setCancelled(true);
                return;
            }

            HopperMinecart hopperMinecart = (HopperMinecart) destinationHolder;
            OwnedEntity ownedEntity = OwnedEntityHandler.getOwnedEntity(hopperMinecart.getUniqueId());

            // Not linked to an owner, so cancel the event
            if (ownedEntity == null) {
                event.setCancelled(true);
                return;
            }

            Location chestLoc;
            if (sourceHolder instanceof DoubleChest) {
                DoubleChest dchest = (DoubleChest) sourceHolder;
                chestLoc = dchest.getLocation();
            } else {
                Chest chest = (Chest) sourceHolder;
                chestLoc = chest.getLocation();
            }

            InnectisLot chestLot = LotHandler.getLot(chestLoc, true);
            InnectisChest innChest = ChestHandler.getChest(chestLoc);
            boolean valid;

            // If a chest is found, check if this transaction is valid
            if (innChest != null) {
                valid = innChest.getOwner().equalsIgnoreCase(ownedEntity.getOwner());
            } else {
                // No protected chest, so this transaction is fine if there is no lot that
                // the chest is on
                valid = (chestLot == null);
            }

            // If the owner of this hopper minecart is not the same as the owner
            // of the chest, or there is no protected chest, let's check to
            // see if they have global permission
            if (!valid) {
                PlayerSession session;
                boolean tempSession = false;

                IdpPlayer player = plugin.getPlayer(ownedEntity.getOwner());

                if (player != null) {
                    session = player.getSession();
                } else {
                    PlayerCredentials credentials = ownedEntity.getOwnerCredentials();

                    session = PlayerSession.getSession(credentials.getUniqueId(), credentials.getName(), plugin, true);
                    tempSession = true;
                }

                valid = session.hasPermission(Permission.entity_minecartsuckallitems);

                if (tempSession) {
                    session.destroy();
                }

                // The owner of this hopper minecart does not have global permissions to
                // suck items out of this chest, so we cancel instead
                if (!valid) {
                    event.setCancelled(true);
                }
            }
        } else {
            Location sourceLocation = getSourceLocation(sourceHolder);
            Location destinationLocation = getSourceLocation(destinationHolder);

            // If for some reason the location couldn't be determined, we cancel
            if (sourceLocation == null || destinationLocation == null) {
                plugin.logError("Something went wrong... Source holder: " + sourceHolder.getInventory().getType()
                        + " destination holder: " + destinationHolder.getInventory().getType().name() + " " + sourceLocation + " " + destinationLocation);
                event.setCancelled(true);
                return;
            }

            // Special case minecarts (this could be a hopper minecart or a storage minecart)
            // make sure they can only be loaded or unloaded with items if the owner of them
            // owns the lot (if any)
            if (sourceHolder instanceof Minecart || destinationHolder instanceof Minecart) {
                OwnedEntity ownedEntity;

                if (sourceHolder instanceof Minecart) {
                    ownedEntity = OwnedEntityHandler.getOwnedEntity(((Minecart) sourceHolder).getUniqueId());
                } else {
                    ownedEntity = OwnedEntityHandler.getOwnedEntity(((Minecart) destinationHolder).getUniqueId());
                }

                // No owner linked to this hopper minecart, so cancel here
                if (ownedEntity == null) {
//                    // We cannot handle this event, throw it out to prevent looping...
//                    IdpItemStack itemstack = IdpItemStack.fromBukkitItemStack(event.getItem());
//                    sourceLocation.getWorld().dropItem(sourceLocation, itemstack);
//                    event.setItem(new ItemStack(0, 1)); // Decrease by 1 (IDP Behaviour)
                    event.setCancelled(true);
                    return;
                }

                // This transaction is valid if there is no lot, or the owner of the lot
                // owns this hopper minecart making the transaction
                InnectisLot minecartLot = LotHandler.getLot(destinationLocation, true);
                boolean valid = (minecartLot == null || minecartLot.getOwner().equalsIgnoreCase(ownedEntity.getOwner()));

                if (!valid) {
                    PlayerSession session;
                    boolean tempSession = false;
                    IdpPlayer testPlayer = plugin.getPlayer(ownedEntity.getOwner());

                    if (testPlayer != null) {
                        session = testPlayer.getSession();
                    } else {
                        PlayerCredentials credentials = ownedEntity.getOwnerCredentials();
                        session = PlayerSession.getSession(credentials.getUniqueId(), credentials.getName(), plugin, true);
                        tempSession = true;
                    }

                    valid = session.hasPermission(Permission.entity_minecartsuckallitems);

                    if (tempSession) {
                        session.destroy();
                    }

                    // If the owner of this hopper minecart does not have the permission, cancel
                    if (!valid) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        InventoryHolder pickupHolder = event.getInventory().getHolder();
        Location location = null;

        if (pickupHolder instanceof Hopper) {
            Hopper hopper = (Hopper) pickupHolder;
            location = hopper.getLocation();
        } else if (pickupHolder instanceof HopperMinecart) {
            HopperMinecart hopperMinecart = (HopperMinecart) pickupHolder;
            location = hopperMinecart.getLocation();
        }

        if (location == null) {
            InnPlugin.logError("Something other than a hopper / hopper minecart picked this item up! Type: " + pickupHolder.getInventory().getType());
            event.setCancelled(true);
        }
    }

    /**
     * This event gets called when somebody clicks in the inventory
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) {
        IdpPlayer player = plugin.getPlayer((Player) event.getWhoClicked());

        // If not logged in, not allowed to talk.
        if (!player.getSession().isLoggedIn()) {
            player.printError("You are not logged in!");
            event.setCancelled(true);
            return;
        }

        // Secondairy listeners
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_INVENTORY_CLICK)) {
            InnInventoryClickEvent idpevent = new InnInventoryClickEvent(player, event);
            plugin.getListenerManager().fireEvent(idpevent);

            if (idpevent.isCancelled()) {
                event.setCancelled(true);
            }

            event.setCurrentItem(idpevent.getCurrentItem());
            event.setResult(idpevent.getResult());

            if (idpevent.shouldTerminate()) {
                return;
            }
        }

        InventoryPayload payload = getPayload(event.getInventory());

        // Handle the payload if it is found
        if (payload != null) {
            payload.onInventoryClick(player, event);

            if (payload.hasAction(InventoryAction.CLOSE_PLAYER_INVENTORY_LATER)) {
                closeInventoryLater(player);
            }

            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inv = event.getInventory();
        IdpPlayer player = plugin.getPlayer(event.getPlayer().getName());

        if (inv instanceof HorseInventory) {
            InventoryHolder holder = inv.getHolder();

            if (holder instanceof Tameable) {
                Tameable tameable = (Tameable) holder;

                if (tameable.getOwner() != null && !tameable.getOwner().getName().equalsIgnoreCase(player.getName())
                        && !player.hasPermission(Permission.special_manipulate_any_inventory)) {
                    // We just need the type of entity
                    Entity entity = (Entity) tameable;

                    player.printError("You cannot view the inventory of this " + entity.getType().name().toLowerCase() + ".");
                    event.setCancelled(true);

                    return;
                }
            }
        }

        PlayerSession session = player.getSession();

        // If an ender chest is being viewed, make sure to populate the inventory
        // being opened with the ender chest contents
        if (session.isViewingEnderChest()) {
            // In case the vanilla ender chest has items in it, clear it
            inv.clear();

            UUID playerId = session.getEnderchestOwnerId();
            PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(playerId);
            EnderContentsType type = session.getEnderchestType();

            EnderChestContents contents = EnderChestContents.getContents(credentials, type);
            IdpItemStack[] items = contents.getItems();

            for (int i = 0; i < items.length; i++) {
                if (items[i] != null) {
                    inv.setItem(i, items[i].toBukkitItemstack());
                }
            }

            return;
        }

        InventoryPayload payload = getPayload(inv);

        // Handle the payload if it is found
        if (payload != null) {
            payload.onInventoryOpen(player, event);

            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClose(InventoryCloseEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer().getName());
        PlayerSession session = player.getSession();
        InventoryPayload payload = getPayload(event.getInventory());

        // Handle the payload if it is found
        if (payload != null) {
            payload.onInventoryClose(player, event);

            return;
        }

        // Check if its a enderchest
        if (session.isViewingEnderChest()) {
            session.setViewingEnderChest(false);

            UUID ownerId = session.getEnderchestOwnerId();
            EnderContentsType type = session.getEnderchestType();

            session.setEnderchestOwnerId(null);
            session.setEnderchestType(null);

            IdpItemStack[] items = new IdpItemStack[IdpInventory.DEFAULT_CHEST_SIZE];
            ItemStack[] invitems = event.getInventory().getContents();

            // Convert
            for (int i = 0; i < items.length; i++) {
                if (invitems[i] != null) {
                    items[i] = IdpItemStack.fromBukkitItemStack(invitems[i]);
                }
            }

            // Get the chestobject
            PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(ownerId);
            EnderChestContents contents = EnderChestContents.getContents(credentials, type);

            if (contents != null) {
                // Set the new items and update
                contents.setItems(items);
                contents.save();
            }

            return;
        }

        // Check if the chest has a shop sign above, and if this is a double-chest
        // then check the other half too and support multiple signs for the
        // same matereial as well
        List<Sign> signs = new ArrayList<Sign>();
        IdpContainer container = null;
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof Chest) {
            Chest chest = (Chest) holder;
            Block chestBlock = chest.getBlock();

            if (chestBlock != null && chestBlock.getRelative(BlockFace.UP).getState() instanceof Sign) {
                container = new IdpContainer(chest.getInventory());
                signs.add((Sign) chestBlock.getRelative(BlockFace.UP).getState());
            }
        } else if (holder instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) event.getInventory().getHolder();
            Chest chest = (Chest) doubleChest.getLeftSide();
            Block leftChestBlock = chest.getBlock();

            if (leftChestBlock != null && leftChestBlock.getRelative(BlockFace.UP).getState() instanceof Sign) {
                container = new IdpContainer(doubleChest.getInventory());
                signs.add((Sign) leftChestBlock.getRelative(BlockFace.UP).getState());
            }

            chest = (Chest) doubleChest.getRightSide();
            Block rightChestBlock = chest.getBlock();

            if (rightChestBlock != null && rightChestBlock.getRelative(BlockFace.UP).getState() instanceof Sign) {
                container = new IdpContainer(doubleChest.getInventory());
                signs.add((Sign) rightChestBlock.getRelative(BlockFace.UP).getState());
            }
        }

        // If any signs found, check if they can be updated
        if (signs.size() > 0) {
            for (Sign sign : signs) {
                MaterialSign testMaterialSign = SignValidator.getMaterialSign(sign.getLines());

                if (testMaterialSign != null) {
                    switch (testMaterialSign.getType()) {
                        case CHEST_SHOP:
                            int count = container.countMaterial(testMaterialSign.getMaterial());
                            sign.setLine(3, "Count: " + count);
                            sign.update();
                            break;
                    }
                }
            }
        }
    }

    /**
     * Gets the custom payload of an inventory
     *
     * @param inv
     * @return
     */
    private static InventoryPayload getPayload(Inventory inv) {
        // CraftInventory check is currently not needed, but it might be needed later on
        // if (inv instanceof CraftInventory) {
        CraftInventory craftinv = (CraftInventory) inv;

        if (craftinv.getInventory() instanceof IdpInventoryDataHolder) {
            return ((IdpInventoryDataHolder) craftinv.getInventory()).getPayload();
        }
        // }
        return null;
    }

    /**
     * This method will try to get the location of the given inventory holder
     * @param inventoryHolder
     * @return
     */
    private Location getSourceLocation(InventoryHolder inventoryHolder) {
        if (inventoryHolder instanceof BlockState) {
            BlockState state = (BlockState) inventoryHolder;
            return state.getLocation();
        }
        if (inventoryHolder instanceof DoubleChest) {
            DoubleChest state = (DoubleChest) inventoryHolder;
            return state.getLocation();
        } else if (inventoryHolder instanceof HopperMinecart) {
            HopperMinecart hopperMinecart = (HopperMinecart) inventoryHolder;
            return hopperMinecart.getLocation();
        } else if (inventoryHolder instanceof StorageMinecart) {
            StorageMinecart storageCart = (StorageMinecart) inventoryHolder;
            return storageCart.getLocation();
        }

        return null;
    }

    /**
     * Closes the inventory at a later time
     * @param player
     */
    private void closeInventoryLater(final IdpPlayer player) {
        plugin.getTaskManager().addTask(new LimitedTask(RunBehaviour.SYNCED, 50L, 1) {
            @Override
            public void run() {
                player.getHandle().closeInventory();
            }
        });
    }

}
