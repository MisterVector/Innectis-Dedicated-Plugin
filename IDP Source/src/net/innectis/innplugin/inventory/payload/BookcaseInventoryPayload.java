package net.innectis.innplugin.inventory.payload;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.objects.owned.InnectisBookcase;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.World;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

/**
 * An implementation of inventory payload
 * that deals with bookcase inventories
 *
 * @author AlphaBlend
 */
public class BookcaseInventoryPayload extends InventoryPayload {

    private InnectisBookcase bookcase;

    public BookcaseInventoryPayload(InnectisBookcase bookcase) {
        super();

        this.bookcase = bookcase;
    }

    @Override
    public void onInventoryClose(IdpPlayer player, InventoryCloseEvent event) {
        ItemStack[] stack = event.getInventory().getContents();
        IdpItemStack[] contents = new IdpItemStack[stack.length];

        for (int i = 0; i < stack.length; i++) {
            IdpItemStack item = IdpItemStack.fromBukkitItemStack(stack[i]);

            // Only allow books!
            switch (item.getMaterial()) {

                // Allow book items
                case WRITTEN_BOOK:
                case BOOK:
                case BOOK_AND_QUILL:
                case ENCHANTED_BOOK: {
                    contents[i] = item;
                    break;
                }

                // Do nothing with air
                case AIR: {
                    break;
                }

                // Drop it
                default: {
                    World world = bookcase.getBookcase().getWorld();
                    world.dropItemNaturally(bookcase.getBookcase().getLocation(), stack[i]);
                    break;
                }
            }
        }

        bookcase.setItems(contents);

        if (!bookcase.save()) {
            InnPlugin.logError("Cannot store books!");
        }
    }

}
