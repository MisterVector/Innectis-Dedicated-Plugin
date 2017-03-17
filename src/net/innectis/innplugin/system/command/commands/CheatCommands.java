package net.innectis.innplugin.system.command.commands;

import java.util.ArrayList;
import java.util.List;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.PagedCommandHandler;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.items.Bookinfo;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldSettings;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.objects.EnchantmentType;
import net.innectis.innplugin.objects.IdpEntityType;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.IdpPlayerInventory;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerEffect;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.PlayerSettings;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.specialitem.SpecialItem;
import net.innectis.innplugin.specialitem.SpecialItemManager;
import net.innectis.innplugin.specialitem.SpecialItemType;
import net.innectis.innplugin.util.LocationUtil;
import net.innectis.innplugin.util.LynxyArguments;
import net.innectis.innplugin.util.MagicValueUtil;
import net.innectis.innplugin.util.ParameterArguments;
import net.innectis.innplugin.util.SmartArguments;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class CheatCommands {

    @CommandMethod(aliases = {"platform"},
    description = "Creates a block of the specified type below you.",
    permission = Permission.command_cheat_platform,
    usage = "/platform <block>",
    serverCommand = false)
    public static boolean commandPlatform(IdpPlayer player, String[] args) {
        if (args.length != 1) {
            return false;
        }

        Location locBelow = player.getLocation().subtract(0, 1, 0);

        // Check building permissions.
        if (!BlockHandler.canBuildInArea(player, locBelow, BlockHandler.ACTION_BLOCK_PLACED, false)) {
            player.printError("You cannot create a platform there!");
            return true;
        }

        IdpMaterial mat = IdpMaterial.fromString(args[0]);

        if (mat == null) {
            player.printError("No valid material found.");
            return true;
        }

        if (!mat.isBlock()) {
            player.printError("Material is not a block.");
            return true;
        }

        // Prevent mods from spawning bedrock, etc.
        if (!player.getGroup().equalsOrInherits(mat.getRequiredGroupToPlace())) {
            player.printError("You do not have permission to place this block!");
            return true;
        }

        // If the player cannot spawn a platform, check their inventory
        // for the specified item
        if (!player.hasPermission(Permission.command_cheat_platform_noconsume)) {
            if (!player.removeItemFromInventory(mat, 1)) {
                player.printError("You do not have the item to do that!");
                return true;
            }
        }

        Block block = locBelow.getBlock();
        BlockHandler.setBlock(block, mat);

        player.printInfo("Set block below you to " + mat.getName().toLowerCase() + ".");

        return true;
    }

    @CommandMethod(aliases = {"effect"},
    description = "Adds an effect to a player.",
    permission = Permission.command_cheat_addeffect,
    usage = "/effect <username> [-clear] [<type> [-disable] [-t ticks] [-i intensity] [-special]]",
    serverCommand = true)
    public static boolean commandEffect(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        int checkSize = (args.hasOption("clear") ? 1 : 2);

        if (args.getActionSize() < checkSize) {
            return false;
        }

        IdpPlayer player = args.getPlayer(0);
        if (player == null) {
            sender.printError("Player not found");
            return true;
        }

        if (args.hasOption("clear")) {
            for (PotionEffect type : player.getHandle().getActivePotionEffects()) {
                player.getHandle().removePotionEffect(type.getType());
            }

            sender.printInfo("Removed all effects for " + player.getName() + "!");
        } else {
            PlayerEffect effect;
            try {
                int effectId = args.getInt(1);
                effect = PlayerEffect.findEffect(effectId);
            } catch (NumberFormatException ex) {
                effect = PlayerEffect.findEffect(args.getString(1));
            }

            if (effect != null) {
                boolean activate = !args.hasOption("disabled", "disable", "d");
                int ticks = 500;
                int intensity = 1;
                boolean special = args.hasOption("special", "s");

                if (args.hasArgument("ticks", "tick", "t")) {
                    try {
                        ticks = args.getInt("ticks", "tick", "t");
                    } catch (NumberFormatException ex) {
                        sender.printError("Invalid ticks!");
                        return true;
                    }
                }
                if (args.hasArgument("intensity", "int", "i")) {
                    try {
                        intensity = args.getInt("intensity", "int", "i");
                    } catch (NumberFormatException ex) {
                        sender.printError("Invalid intensity!");
                        return true;
                    }
                }

                if (!special) {
                    if (activate) {
                        effect.removeEffect(player); //remove first, then apply
                        effect.applyEffect(player, ticks, intensity);
                        sender.printInfo("Effect " + effect.name() + " applied to " + player.getColoredDisplayName(), " for " + ticks + " ticks (intensity " + intensity + ")!");
                    } else {
                        effect.removeEffect(player);
                        sender.printInfo("Effect " + effect.name() + " removed from " + player.getName() + "!");
                    }
                } else {
                    if (activate) {
                        effect.removeSpecial(player); //remove first, then apply
                        effect.applySpecial(player, ticks, intensity);
                        sender.printInfo("Special effect " + effect.name() + " applied to " + player.getColoredDisplayName(), " for " + ticks + " ticks (intensity " + intensity + ")!");
                    } else {
                        effect.removeSpecial(player);
                        sender.printInfo("Special effect " + effect.name() + " removed from " + player.getName() + "!");
                    }
                }
            } else {
                player.printError("No effect found! (1 - " + PlayerEffect.values().length + ")");
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"enchant"},
    description = "Enchants the item in the player's hand.",
    permission = Permission.command_cheat_enchant,
    usage = "/enchant <enchantment ID> [level] [-list]",
    serverCommand = false)
    public static boolean commandEnchant(IdpPlayer player, LynxyArguments args) {
        if (args.hasOption("list")) {
            player.print(ChatColor.GREEN, "List of enchantment types:");

            EquipmentSlot handSlot = player.getNonEmptyHand();
            IdpItemStack handStack = null;

            if (handSlot != null) {
                handStack = player.getItemInHand(handSlot);
            } else {
                handStack = player.getItemInMainHand();
            }

            boolean showUsableEnchants = (handStack != null);

            if (showUsableEnchants) {
                player.print(ChatColor.YELLOW, "Item in hand: " + handStack.getMaterial().getName());
                player.print(ChatColor.GREEN, "All usable enchantments will be shown in green.");
            }

            String list = "";
            int idx = 0;

            for (EnchantmentType et : EnchantmentType.values()) {
                if (et != EnchantmentType.NONE) {
                    int id = et.getId();
                    String name = et.getName();
                    String enchString = null;

                    if (showUsableEnchants) {
                        boolean canEnchant = et.canEnchantItem(handStack);

                        if (canEnchant) {
                            enchString = ChatColor.YELLOW + "(" + ChatColor.GREEN + id + ChatColor.YELLOW + ") ("
                                    + ChatColor.GREEN + name + ChatColor.YELLOW + ") ";
                        }
                    }

                    if (enchString == null) {
                        enchString = et.getId() + " (" + et.getName() + ") ";
                    }

                    list += enchString;
                    idx++;

                    if (idx % 2 == 0) {
                        player.print(ChatColor.YELLOW, list);
                        list = "";
                    }
                }
            }

            if (!list.isEmpty()) {
                player.print(ChatColor.YELLOW, list);
            }


            return true;
        } else {
            if (args.getActionSize() > 0) {
                EnchantmentType type;
                EquipmentSlot handSlot = player.getNonEmptyHand();

                if (handSlot == null) {
                    player.printError("Can't enchant without holding an item!");
                    return true;
                }

                IdpItemStack handStack = player.getItemInHand(handSlot);

                try {
                    int id = Integer.parseInt(args.getString(0));
                    type = EnchantmentType.fromId(id);
                } catch (NumberFormatException nfe) {
                    String name = args.getString(0);
                    type = EnchantmentType.fromArgument(name);
                }

                if (type == EnchantmentType.NONE) {
                    player.printError("That is an invalid enchantment.");
                    return true;
                }

                int level = 0;

                if (args.getActionSize() > 1) {
                    try {
                        level = Integer.parseInt(args.getString(1));
                    } catch (NumberFormatException nfe) {
                        player.printError("Level is not a number.");
                        return true;
                    }
                }

                if (!type.canEnchantItem(handStack)) {
                    player.printError("Unable to apply \"" + type.getName() + "\" to this " + handStack.getMaterial().getName() + ".");
                    return true;
                }

                ItemData data = handStack.getItemdata();

                if (level > 0) {
                    data.addEnchantment(type, level);
                    player.printInfo("Applied \"" + type.getName() + "\" (level " + level + ") to this " + handStack.getMaterial().getName() + ".");
                } else {
                    if (data.removeEnchantment(type)) {
                        player.printInfo("Removed the enchantment \"" + type.getName() + "\" from this " + handStack.getMaterial().getName() + ".");
                    } else {
                        player.printInfo("This " + handStack.getMaterial().getName() + " does not have the enchantment \"" + type.getName() + "\"");
                        return true;
                    }
                }
                player.setItemInHand(handSlot, handStack);
                player.getInventory().updateBukkitInventory();

                return true;
            }
        }

        return false;
    }

    @CommandMethod(aliases = {"equip"},
    description = "Equips the selected item in an armor slot (1 through 4).",
    permission = Permission.command_cheat_equip,
    usage = "/equip <slot> [username]",
    serverCommand = false)
    public static boolean commandEquip(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        try {
            IdpPlayer player = (IdpPlayer) sender;
            IdpPlayer target;
            if (args.length == 1) {
                target = player;
            } else if (args.length == 2) {
                target = parent.getPlayer(args[1], false);
                if (target == null) {
                    player.printError("Player not found!");
                    return true;
                }
            } else {
                return false;
            }

            EquipmentSlot handSlot = player.getNonEmptyHand();

            if (handSlot == null) {
                player.printError("You aren't holding an item!");
                return true;
            }

            IdpItemStack handStack = player.getItemInHand(handSlot);

            int armorSlot = 4 - Integer.parseInt(args[0]);
            int heldSlot = player.getHandle().getInventory().getHeldItemSlot();

            IdpItemStack[] armor = target.getInventory().getArmorItems();
            armor[armorSlot] = handStack;

            IdpPlayerInventory inv = target.getInventory();
            inv.setArmorItems(armor);
            inv.updateBukkitInventory();

            inv = player.getInventory();
            inv.setItemAt(heldSlot, null);
            inv.updateBukkitInventory();
            player.printInfo("Equipped " + handStack.getMaterial().getName() + " on armor slot " + armorSlot + " of " + target.getColoredName() + ChatColor.DARK_GREEN + "!");

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @CommandMethod(aliases = {"give", "g"},
    description = "Gives the specified player an item.",
    permission = Permission.command_cheat_give,
    usage = "/give <name> <itemid[:data]> [amount]",
    serverCommand = true)
    public static boolean commandGive(Server server, InnPlugin parent, IdpCommandSender sender, String[] args) {
        SmartArguments sa = new SmartArguments(args);

        if (sa.checkArgumentAmount(2, 3)) {
            IdpPlayer tarplayer = sa.getPlayer(0);

            IdpMaterial mat;
            try {
                mat = IdpMaterial.fromString(args[1]);
            } catch (NumberFormatException nfe) {
                sender.printError("Item not found: '" + args[1] + "'!");
                return true;
            }

            // Check if material is found
            if (mat == null) {
                sender.printError("Unknown material! " + sa.getString(1));
                return true;
            }

            int amount = 1;

            if (args.length == 3) {
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException nfe) {
                    sender.printError("Amount not formatted correctly.");
                    return true;
                }
            }

            if (amount > 5000) {
                sender.printError("Amount is too large! Max 5000");
                return true;
            }

            // Check if player is found
            if (tarplayer == null) {
                sender.printError("Cannot find player: " + sa.getString(0));
                return true;
            }

            // Check if player is allowed or if its the console.
            if (sender.isPlayer() && !mat.canPlayerPlaceMaterial((IdpPlayer) sender)) {
                sender.printError("You cannot spawn that item!");
                return true;
            }

            // Do not give certain items!
            if (!mat.isInventoryItem()) {
                sender.printError("You cannot give that item!");
                return true;
            }

            IdpItemStack stack = new IdpItemStack(mat, amount);
            int remaining = tarplayer.addItemToInventory(stack);

            if (remaining == amount) {
                sender.printError("Target player's inventory is full.");
            } else {
                int total = 0;

                if (remaining > 0) {
                    total = (amount - remaining);
                } else {
                    total = amount;
                }

                if (remaining > 0) {
                    sender.printInfo("Target received " + ChatColor.AQUA + total, " out of " + ChatColor.AQUA + amount, " items of "  + ChatColor.AQUA + mat.getName(), " (" + mat.getIdData() + ")");
                } else {
                    sender.printInfo("Gave " + tarplayer.getColoredDisplayName(), " " + ChatColor.AQUA + total, " items of " + ChatColor.AQUA + mat.getName(), " (" + mat.getIdData() + ")");
                }

                if (tarplayer != sender) {
                    tarplayer.printInfo("You were given " + ChatColor.AQUA + total, " items of " + ChatColor.AQUA + mat.getName(), " by " + sender.getColoredName(), " (" + mat.getIdData() + ")");
                }
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"god"},
    description = "Enables god mode for yourself or a targetted player.",
    permission = Permission.command_cheat_god,
    usage = "/god [username] [-list]",
    serverCommand = true)
    public static boolean commandGod(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        if (args.hasOption("list", "l")) {
            StringBuilder sb = new StringBuilder();
            for (PlayerSession session : PlayerSession.getSessions()) {
                if (session.hasGodmode()) {
                    sb.append(session.getColoredName()).append(" ");
                }
            }
            sender.printInfo("The following players have god mode:");
            sender.printInfo(sb.toString());
            return true;
        }

        if (args.getString(0) == null && sender.isPlayer()) {
            IdpPlayer player = (IdpPlayer) sender;

            player.getSession().setGodmode(!player.getSession().hasGodmode());

            if (!player.getSession().hasGodmode()) {
                player.printInfo("You're no longer invincible!");
            } else {
                player.printInfo("You're invincible!");
            }
            return true;
        } else {
            IdpPlayer tarPlayer = args.getPlayer(0);
            if (tarPlayer == null) {
                sender.printError("Player not found");
                return true;
            }
            tarPlayer.getSession().setGodmode(!tarPlayer.getSession().hasGodmode());

            if (!tarPlayer.getSession().hasGodmode()) {
                tarPlayer.printInfo("You're no longer invincible!");
                sender.printInfo(tarPlayer.getName() + " is no longer invincible!");
            } else {
                tarPlayer.printInfo("You're invincible!");
                sender.printInfo(tarPlayer.getName() + " is invincible!");
            }
            return true;
        }
    }

    @CommandMethod(aliases = {"fly"},
    description = "Allows you or a targetted player to fly.",
    permission = Permission.command_cheat_fly,
    usage = "/fly [username]",
    serverCommand = true)
    public static boolean commandFly(IdpCommandSender sender, ParameterArguments args) {
        IdpPlayer target = null;

        if (args.size() == 0) {
            if (!sender.isPlayer()) {
                sender.printError("Console must specify a player!");
                return true;
            } else {
                target = (IdpPlayer) sender;
            }
        } // Only allow this when sender got special perm!
        else if (sender.hasPermission(Permission.command_cheat_fly_others)) {
            target = args.getPlayer(0);
        }

        if (target == null || !target.isOnline()) {
            sender.printError("Target was not found!");
            return false;
        }

        boolean enable = false;

        if (args.hasOption("enable", "e")) {
            enable = true;
        } else if (args.hasOption("disable", "d")) {
            enable = false;
        } else {
            enable = !target.getAllowFlight();
        }

        target.setAllowFlight(enable);

        if (target.hasPermission(Permission.special_has_flight)) {
            target.getSession().setSetting(PlayerSettings.FLIGHT, enable);
        }

        if (target.getAllowFlight()) {
            if (sender == target) {
                sender.printInfo("You feel very light!");
            } else {
                sender.printInfo("You changed " + target.getColoredName(), " into a pegasus!");
                target.printInfo("You've been changed into a pegasus!");
            }
        } else {
            // Make sure the target is on the ground!
            Location location = target.getLocation();
            int highesty = location.getWorld().getHighestBlockYAt(location);
            if (location.getBlockY() > highesty) {
                location.setY(highesty);
            }

            target.teleport(location, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);

            if (sender == target) {
                sender.printInfo("You suddenly feel very heavy!");
            } else {
                sender.printInfo("You removed flight abilities from " + target.getColoredName());
                target.printInfo("You suddenly feel very heavy!");
            }
        }
        return true;
    }

    @CommandMethod(aliases = {"heal"},
    description = "Heal yourself at the cost of vote points.",
    permission = Permission.command_cheat_heal,
    usage = "/heal",
    usage_Admin = "/heal [username] [-lot]",
    serverCommand = true)
    public static boolean commandHeal(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        if (sender.hasPermission(Permission.command_cheat_heal_free)) {
            if (args.hasOption("lot")) {

                if (sender.isPlayer()) {

                    IdpPlayer player = (IdpPlayer) sender;
                    InnectisLot lot = LotHandler.getLot(player.getLocation());

                    if (lot != null) {
                        for (IdpPlayer pl : lot.getPlayersInsideRegion(0)) {
                            pl.setHealth(20);
                            pl.setFoodLevel(20);
                            pl.printInfo("You have been healed!");
                        }
                    } else {
                        player.printError("Lot not found!");
                    }

                } else {
                    sender.printError("You cannot use that argument as the console.");
                }

                return true;
            } else {
                if (args.getActionSize() > 0) {
                    String name = args.getString(0);
                    IdpPlayer player = parent.getPlayer(name);

                    if (player != null) {
                        player.setHealth(20);
                        player.setFoodLevel(20);
                        player.printInfo("You have been healed!");

                        if (!sender.getName().equalsIgnoreCase(player.getName())) {
                            sender.printInfo("You healed " + player.getName() + ".");
                        }
                    } else {
                        sender.printError("Player " + name + " not found.");
                    }
                } else {
                    if (sender.isPlayer()) {
                        IdpPlayer player = (IdpPlayer) sender;
                        player.setHealth(20);
                        player.setFoodLevel(20);
                        player.printInfo("You heal yourself!");
                    } else {
                        sender.printError("You cannot heal the console!");
                    }
                }
            }
        } else {
            IdpPlayer player = (IdpPlayer) sender;
            int pts = (player.getGroup().id + 1) / 2;

            if (player.getInventory().getType() != InventoryType.MAIN) {
                player.printError("You can only use this with your Main inventory.");
                return true;
            }

            TransactionObject transaction = TransactionHandler.getTransactionObject(player);
            int votepts = transaction.getValue(TransactionHandler.TransactionType.VOTE_POINTS);

            if (votepts >= pts) {
                if (pts > 0) {
                    votepts -= pts;
                    transaction.subtractValue(pts, TransactionHandler.TransactionType.VOTE_POINTS);
                }

                player.printInfo("Health and food restored for " + pts + " vote points! (" + votepts + " vote points remaining)");
                player.setHealth(20.0D);
                player.setFoodLevel(20);
            } else {
                player.printError("You don't have enough vote points to heal yourself.");
            }
        }

        return true;
    }

    @CommandMethod(aliases = {"item", "i"},
    description = "Gives yourself an item.",
    permission = Permission.command_cheat_item,
    usage = "/item <itemid[:data]> [amount]",
    serverCommand = false)
    public static boolean commandItem(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;

        if (!player.hasPermission(Permission.command_cheat_item_anywhere)) {
            IdpWorld world = player.getWorld();

            if (world.getActingWorldType() != IdpWorldType.CREATIVEWORLD) {
                player.printError("You may not use /item on this world!");
                return true;
            }
        }

        try {
            if (args.length == 1 || args.length == 2) {
                IdpMaterial mat = IdpMaterial.fromString(args[0]);

                if (mat == null) {
                    player.printError("Material not found.");
                    return true;
                }

                int amount = (args.length == 2) ? Integer.parseInt(args[1]) : mat.getMaxStackSize();

                if (amount > 5000) {
                    sender.printError("Amount is too large! Max 5000");
                    return true;
                }

                if (!mat.canPlayerPlaceMaterial(player)) {
                    player.printError("You cannot spawn that item!");
                    return true;
                }

                if (!mat.isInventoryItem()) {
                    player.printError("You cannot obtain this item!");
                    return true;
                }

                IdpItemStack stack = new IdpItemStack(mat, amount);
                int remaining = player.addItemToInventory(stack);

                if (remaining == amount) {
                    player.printError("Your inventory is full.");
                } else if (remaining > 0) {
                    player.printInfo("Received " + (amount - remaining) + " out of " + amount + " items of " + mat.getName() + " (" + mat.getIdData() + ")");
                } else {
                    player.print(ChatColor.AQUA, "You gave yourself " + amount + " items of " + mat.getName() + " (" + mat.getIdData() + ")");
                }
                return true;
            }
        } catch (NumberFormatException nfe) {
            player.printError("Unknown item id, amount, or data");
        }
        return false;
    }

    @CommandMethod(aliases = {"itemdata"},
    description = "Adds itemdata to an item in the player's hand.",
    permission = Permission.command_cheat_itemdata,
    usage = "/itemdata [-MobHead <name>] [-Name <name>] [-Title <title>] [-Writer <player>] [-Spell <spellid>] [-addlore, -al <lore>] [-color <color>] [-clearlore, -cl] [-type, -t <type>] [-removetype, -rt] [-listtypes, -lt [-page, -p <page>]]",
    serverCommand = false)
    public static boolean commandItemData(Server server, InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        // Set a mobhead to a player
        if (args.hasArgument("mobhead", "mh")) {
            String name = args.getString("mobhead", "mh");

            if (name == null) {
                player.printError("Name cannot be blank!");
                return true;
            }

            EquipmentSlot handSlot = player.getNonEmptyHand();

            if (handSlot == null) {
                player.printError("Cannot use this command without a mobhead!");
                return true;
            }

            IdpItemStack handStack = player.getItemInHand(handSlot);

            if (handStack.getMaterial() != IdpMaterial.PLAYER_SKULL) {
                player.printError("This can only be set on mobheads!");
                return true;
            }

            handStack.getItemdata().setMobheadName(name);

            player.printInfo("Mobhead name set!");
            player.setItemInHand(handSlot, handStack);

            return true;
        }

        // Clear lores
        if (args.hasOption("clearlore", "cl")) {
            EquipmentSlot handSlot = player.getNonEmptyHand();

            if (handSlot == null) {
                player.printError("You must have an item in hand first!");
                return true;
            }

            IdpItemStack item = player.getItemInHand(handSlot);
            item.getItemdata().setLore(null);
            player.printInfo("Lores cleared!");
            player.setItemInHand(handSlot, item);

            return true;
        }

        // Add lore (after clear so you can set one lore right away)
        if (args.hasArgument("addlore", "al")) {
            String lore = ChatColor.parseChatColor(args.getString("addlore", "al"));

            if (lore == null || lore.length() > 30) {
                player.printError("Invalid lore length!");
            } else {
                EquipmentSlot handSlot = player.getNonEmptyHand();

                if (handSlot == null) {
                    player.printError("You must have an item in hand.");
                    return true;
                }

                IdpItemStack item = player.getItemInHand(handSlot);
                item.getItemdata().addLore(lore);

                player.printInfo("Lore set!");
                player.setItemInHand(handSlot, item);
            }

            return true;
        }

        // Special item effects
        if (args.hasArgument("type", "t")) {
            SpecialItemType type = null;

            try {
                int typeId = Integer.parseInt(args.getString("type", "t"));
                type = SpecialItemType.fromId(typeId);

                if (type == null) {
                    player.printError("Special item type not found!");
                    return true;
                }
            } catch (NumberFormatException nfe) {
                player.printError("Special item type ID is not an integer.");
                return true;
            }

            if (!type.isSpawnable()) {
                player.printError("You cannot create this item with /itemdata!");
                return true;
            }

            EquipmentSlot handSlot = player.getNonEmptyHand();

            if (handSlot == null) {
                player.printError("You must have an item in hand.");
                return true;
            }

            IdpItemStack item = player.getItemInHand(handSlot);

            if (!SpecialItemManager.createSpecialItem(item, type)) {
                player.printError("Cannot apply \"" + type.getName() + "\" to the item in hand.");
                return true;
            }

            player.setItemInHand(handSlot, item);
            player.printInfo("Special item \"" + type.getName() + "\" set!");

            return true;
        }

        if (args.hasOption("listtypes", "lt")) {
            int page = 1;

            if (args.hasArgument("page", "p")) {
                try {
                    page = Integer.parseInt(args.getString("page", "p"));
                } catch (NumberFormatException nfe) {
                    player.printError("Page number is not a number.");
                    return true;
                }
            }

            EquipmentSlot handSlot = player.getNonEmptyHand();

            List<String> info = new ArrayList<String>();

            for (SpecialItemType type : SpecialItemType.values()) {
                int id = type.getId();
                String name = type.getName();
                SpecialItem si = type.getSpecialItem();
                ChatColor legendColor = null;

                if (!type.isSpawnable()) {
                    legendColor = ChatColor.YELLOW;
                } else if (handSlot == null || !si.canApplyTo(player.getItemInHand(handSlot))) {
                    legendColor = ChatColor.RED;
                } else {
                    legendColor = ChatColor.GREEN;
                }

                info.add(id + ". " + legendColor + name);
            }

            PagedCommandHandler ph = new PagedCommandHandler(page, info);

            if (ph.isValidPage()) {
                player.printInfo("Listing all special item types");

                if (handSlot == null) {
                    player.printInfo("Item in hand: " + ChatColor.RED + "nothing");
                } else {
                    IdpItemStack handStack = player.getItemInHand(handSlot);
                    player.printInfo("Item in hand: " + ChatColor.YELLOW + handStack.getMaterial().getName());
                }

                player.printInfo("");
                player.printInfo("Allowed on item: " + ChatColor.GREEN + "Yes "
                        + ChatColor.RED + "No " + ChatColor.YELLOW + "Unavailable");
                player.printInfo("Showing page " + page + " of " + ph.getMaxPage());
                player.printInfo("");

                for (String str : ph.getParsedInfo()) {
                    player.printInfo(str);
                }
            } else {
                player.printError(ph.getInvalidPageNumberString());
            }

            return true;

        }

        if (args.hasOption("removetype", "rt")) {
            EquipmentSlot handSlot = player.getNonEmptyHand();

            if (handSlot == null) {
                player.printError("You must have an item in hand.");
                return true;
            }

            IdpItemStack handStack = player.getItemInHand(handSlot);
            ItemData itemData = handStack.getItemdata();

            if (!itemData.hasSpecialItem()) {
                player.printError("This is not a special item!");
                return true;
            }

            itemData.clearSpecialItem();
            player.setItemInHand(handSlot, handStack);

            player.printInfo("Cleared special item!");

            return true;

        }

        // Item name
        if (args.hasArgument("name", "n")) {
            String name = ChatColor.parseChatColor(args.getString("name", "n"));

            if (StringUtil.stringIsNullOrEmpty(name) || name.length() > 30) {
                player.printError("Invalid name length!");
            } else {
                EquipmentSlot handSlot = player.getNonEmptyHand();

                if (handSlot == null) {
                    player.printError("You must have an item in hand.");
                    return true;
                }

                IdpItemStack item = player.getItemInHand(handSlot);
                item.getItemdata().setItemName(name);

                player.printInfo("Itemname set!");
                player.setItemInHand(handSlot, item);
            }

            return true;
        }

        // Armour colours
        if (args.hasArgument("color")) {
            String hex = args.getString("color");

            EquipmentSlot handSlot = player.getNonEmptyHand();

            if (handSlot == null) {
                player.printError("You must have an item in hand.");
                return true;
            }

            try {
                IdpItemStack item = player.getItemInHand(handSlot);

                switch (item.getMaterial()) {
                    case LEATHER_BOOTS:
                    case LEATHER_CHEST:
                    case LEATHER_HELMET:
                    case LEATHER_LEGGINGS:
                        item.getItemdata().setColor(hex);
                        player.printInfo("Itemcolour set!");
                        player.setItemInHand(handSlot, item);
                        break;

                    default:
                        player.printError("Cannot set colour to that item!");
                        break;
                }

            } catch (NumberFormatException nfe) {
                player.printError("The supplied value '" + hex + "' is not valid!");
            }

            return true;
        }

        // Book title.
        if (args.hasArgument("title", "t")) {
            String name = args.getString("title", "t");

            if (name == null || name.length() > 30) {
                player.printError("Invalid length!");
            } else {
                EquipmentSlot handSlot = player.getNonEmptyHand();

                if (handSlot == null) {
                    player.printError("You must have an item in hand.");
                    return true;
                }

                if (player.getMaterialInHand(handSlot) == IdpMaterial.WRITTEN_BOOK) {
                    IdpItemStack item = player.getItemInHand(handSlot);
                    Bookinfo info = item.getItemdata().getBookinfo();
                    info.setTitle(name);
                    item.getItemdata().setBookinfo(info);

                    player.printInfo("Book title set!");
                    player.setItemInHand(handSlot, item);
                } else {
                    player.printError("This can only be set on written books!");
                }
            }

            return true;
        }

        // The Write of a book
        if (args.hasArgument("writer", "w")) {
            String name = args.getString("writer", "w");

            if (name == null) {
                player.printError("Name cannot be blank!");
                return true;
            }

            EquipmentSlot handSlot = player.getNonEmptyHand();

            if (handSlot == null) {
                player.printError("You must have an item in hand.");
                return true;
            }

            IdpItemStack handStack = player.getItemInHand(handSlot);

            if (handStack.getMaterial() == IdpMaterial.WRITTEN_BOOK) {
                Bookinfo info = handStack.getItemdata().getBookinfo();
                info.setAuthor(name);
                handStack.getItemdata().setBookinfo(info);

                player.printInfo("Book writer set!");
                player.setItemInHand(handSlot, handStack);
            } else {
                player.printError("This can only be set on written books!");
            }

            return true;
        }

        return false;
    }

    @CommandMethod(aliases = {"spawnmob"},
    description = "Spawns a mob at the user's location, allowing various traits as well.",
    permission = Permission.command_cheat_spawnmob,
    usage = "/spawnmob <mob1[,mob2,...]> [amount] [minecartblock [-block, -b <material> [-invert]]] [-stacked] [-carry <material>] [-fly] [-slimesize, -ss <size>] [-kid, -baby] [-equip <head[,chest,torso,feet,hand]>] [-separate] [-power] [-head <player name>] [-rabbittype, -rt <type>] [-name, -n <name>] [-color (-c) <color>] [-noai] [-derp] [-jumpstrength, -js <strength>]",
    serverCommand = false)
    public static boolean commandSpawnMob(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, LynxyArguments args) {
        if (args.getActionSize() == 0) {
            return false;
        }

        IdpPlayer player = (IdpPlayer) sender;

        int amount = 1;

        if (args.getActionSize() > 1) {
            try {
                amount = Integer.parseInt(args.getString(1));

                if (amount < 1) {
                    player.printError("Amount cannot be less than 1.");
                    return true;
                }
            } catch (NumberFormatException ex) {
                player.printError("Amount not specified correctly.");
                return true;
            }
        }

        String spawnString = args.getString(0);

        if (spawnString.equalsIgnoreCase("minecartblock")) {
            IdpMaterial mat = IdpMaterial.AIR;

            if (args.hasArgument("block", "b")) {
                mat = IdpMaterial.fromString(args.getString("block", "b"));

                if (mat == null) {
                    player.printError("Invalid material specified!");
                    return true;
                }
            }

            int startHeight = 0;

            if (args.hasArgument("startheight", "sh")) {
                try {
                    startHeight = Integer.parseInt(args.getString("startheight", "sh"));
                } catch (NumberFormatException nfe) {
                    player.printError("Start height is not a number.");
                    return true;
                }
            }

            boolean invert = args.hasOption("invert");
            int heightIncrement = 15;

            if (invert) {
                startHeight = -startHeight;
                heightIncrement = -heightIncrement;
            }

            World bukkitWorld = player.getHandle().getWorld();
            Location minecartLocation = LocationUtil.getCenterLocation(player.getLocation());
            MaterialData md = new MaterialData(mat.getBukkitMaterial());
            int minecartHeight = startHeight + heightIncrement;

            for (int i = 0; i < amount; i++) {
                Minecart minecart = (Minecart) bukkitWorld.spawnEntity(minecartLocation, EntityType.MINECART);
                minecart.setDisplayBlock(md);
                minecart.setDisplayBlockOffset(minecartHeight);
                minecartHeight += heightIncrement;
            }

            player.printInfo("Created minecart wall " + amount + " blocks high.");
        } else {
            List<IdpEntityType> spawnTypes = IdpEntityType.lookupMultiple(args.getString(0), true);

            if (spawnTypes.isEmpty()) {
                player.printError("No available mobs to spawn.");
                return true;
            }

            Type rabbitType = null;

            if (args.hasArgument("rabbittype", "rt")) {
                try {
                    int typeNo = Integer.parseInt(args.getString("rabbittype", "rt"));

                    for (Type t : Type.values()) {
                        if (t.ordinal() == typeNo) {
                            rabbitType = t;
                            break;
                        }
                    }
                } catch (NumberFormatException nfe) {
                    String typeName = args.getString("rabbittype", "rt").toUpperCase();
                    rabbitType = Type.valueOf(typeName);
                }

                if (rabbitType == null) {
                    player.printError("That rabbit type doesn't exist!");
                    return true;
                }
            }

            double jumpStrength = -1;

            if (args.hasArgument("jumpstrength", "js")) {
                try {
                    jumpStrength = Double.parseDouble(args.getString("jumpstrength", "js"));

                    if (jumpStrength > 2.0D || jumpStrength < 0.0D) {
                        player.printError("Jump strength must be between 0.0 and 2.0.");
                        return true;
                    }
                } catch (NumberFormatException ex) {
                    player.printError("Jump strength is not a number.");
                    return true;
                }
            }

            int slimeSize = 0;

            if (args.hasArgument("slimesize", "ss")) {
                try {
                    slimeSize = Integer.parseInt(args.getString("slimesize", "ss"));

                    if (slimeSize < 1) {
                        player.printError("Slime size cannot be less than 1.");
                        return true;
                    }
                } catch (NumberFormatException nfe) {
                    player.printError("Slime size is not a number.");
                    return true;
                }
            }

            IdpMaterial carriedMaterial = null;

            if (args.hasArgument("carry")) {
                carriedMaterial = IdpMaterial.fromString(args.getString("carry"));

                // Set to air if invalid
                if (carriedMaterial == null) {
                    carriedMaterial = IdpMaterial.AIR;
                }
            }

            List<IdpMaterial> equipmentMaterials = new ArrayList<IdpMaterial>(5);
            boolean equipMaterials = false;

            if (args.hasArgument("equip")) {
                String[] equipString = args.getString("equip").split(",");
                equipMaterials = true;

                for (int i = 0; i < 5; i++) {
                    // Default to air
                    IdpMaterial mat = IdpMaterial.AIR;

                    // If equip string's length is great enough
                    if ((equipString.length - 1) >= i) {
                        mat = IdpMaterial.fromString(equipString[i]);

                        // If input gave bad material, set back to AIR
                        if (mat == null) {
                            mat = IdpMaterial.AIR;
                        }
                    }

                    equipmentMaterials.add(mat);
                }
            }

            String entityName = null;

            if (args.hasArgument("name", "n")) {
                entityName = args.getString("name", "n");
            }

            ChatColor color = null;

            if (args.hasArgument("color", "c")) {
                color = ChatColor.getByCodeOrString(args.getString("color", "c"));

                if (color == null) {
                    player.printError("Invalid color specified.");
                    return true;
                }
            }

            String headName = args.getString("head");
            boolean doStack = args.hasOption("stacked");
            boolean doFly = args.hasOption("fly");
            boolean isKid = args.hasOption("kid", "baby");
            boolean isPowered = args.hasOption("power");
            boolean isSeparate = args.hasOption("separate");
            boolean isDerp = args.hasOption("derp");

            Entity lastEntity = null;
            int idx = 0;
            int count = amount;

            while (count > 0) {
                IdpEntityType currentType = spawnTypes.get(idx);

                // Reset to 0 if index goes over
                if (++idx == spawnTypes.size()) {
                    idx = 0;

                    // If -separate is given, this allows you to stack multiple entities
                    // separately instead of all in a group
                    if (isSeparate) {
                        lastEntity = null;
                    }
                }

                Entity entity = player.getHandle().getWorld().spawnEntity(player.getHandle().getLocation(), currentType.getEntityType());

                if (entity instanceof Slime && slimeSize > 0) {
                    Slime slime = (Slime) entity;
                    slime.setSize(slimeSize);
                }

                if (entity instanceof Horse && jumpStrength > -1) {
                    Horse horse = (Horse) entity;
                    horse.setJumpStrength(jumpStrength);
                }

                if (entity instanceof Creeper && isPowered) {
                    Creeper creeper = (Creeper) entity;
                    creeper.setPowered(true);
                }

                if (entity instanceof Rabbit && rabbitType != null) {
                    Rabbit rabbit = (Rabbit) entity;
                    rabbit.setRabbitType(rabbitType);
                }

                if (isKid) {
                    makeBaby(entity);
                }

                if (entity instanceof Enderman && carriedMaterial != null) {
                    giveEndermanItem((Enderman) entity, carriedMaterial);
                }

                if (headName != null) {
                    setPlayerHead((LivingEntity) entity, headName);
                }

                if (equipMaterials) {
                    giveMobEquipment((LivingEntity) entity, equipmentMaterials);
                }

                if (entityName != null) {
                    entity.setCustomName(entityName);
                    entity.setCustomNameVisible(true);
                }

                if (entity instanceof LivingEntity && args.hasOption("noai")) {
                    ((LivingEntity) entity).setAI(false);
                }

                if (color != null && entity instanceof Sheep) {
                    Sheep sheep = (Sheep) entity;
                    sheep.setColor(DyeColor.getByColor(Color.SILVER));
                }

                if (isDerp && entity instanceof Snowman) {
                    Snowman showman = (Snowman) entity;
                    showman.setDerp(true);
                }

                if (doFly) {
                    flyMob(entity);
                } else if (doStack) {
                    if (lastEntity == null) {
                        lastEntity = entity;
                    } else {
                        lastEntity.setPassenger(entity);
                        lastEntity = entity;
                    }
                }

                count--;
            }

            StringBuilder sb = new StringBuilder();

            for (IdpEntityType type : spawnTypes) {
                if (sb.length() == 0) {
                    sb.append(type.getName());
                } else {
                    sb.append(", ").append(type.getName());
                }
            }

            player.printInfo("Spawned " + amount + " of " + sb.toString());
        }

        return true;

    }

    @CommandMethod(aliases = {"strike"},
    description = "Strikes lightning at nearby entities.",
    permission = Permission.command_cheat_strike,
    usage = "/strike <range/name> [mode]",
    serverCommand = false)
    public static boolean commandStrike(Server server, InnPlugin parent, IdpPlayer player, String[] args) {
        try {
            if (args.length == 1) {
                try {
                    int range = Integer.parseInt(args[0]);
                    if (range < 50) {
                        List<Entity> nearby = player.getNearbyEntities(range, range, range);
                        for (Entity ent : nearby) {
                            if (ent instanceof Player) {
                                player.getHandle().getWorld().strikeLightning(ent.getLocation());
                            }
                        }
                        player.printInfo("Lighting Stike!");
                    } else {
                        player.printError("Range is too big!");
                    }
                    return true;
                } catch (NumberFormatException ex) {
                    IdpPlayer target = parent.getPlayer(args[0], false);
                    if (target == null) {
                        player.printError("Player not found!");
                    } else {
                        target.getHandle().getWorld().strikeLightning(target.getLocation());
                    }
                    return true;
                }
            }
            if (args.length == 2) {
                int range = Integer.parseInt(args[0]);
                if (range <= 60) {
                    if (args[1].equalsIgnoreCase("mob")) {
                        List<Entity> nearby = player.getNearbyEntities(range, range, range);
                        for (Entity ent : nearby) {
                            if (!(ent instanceof Player) && !(ent instanceof Item)) {
                                player.getHandle().getWorld().strikeLightning(ent.getLocation());
                            }
                        }
                        player.printInfo("Lighting Stike!");

                    } else if (args[1].equalsIgnoreCase("all")) {
                        List<Entity> nearby = player.getNearbyEntities(range, range, range);
                        for (Entity ent : nearby) {
                            player.getHandle().getWorld().strikeLightning(ent.getLocation());
                        }
                        player.printInfo("Lighting Stike!");
                    }
                } else {
                    player.printError("Range too big!");
                }
                return true;
            }

        } catch (NumberFormatException nfe) {
            player.printError("Unable to strike player with lightning!");
        }
        return false;
    }

    @CommandMethod(aliases = {"time"},
    description = "Sets the time, either server, targetted world, or a player's time.",
    permission = Permission.command_cheat_time,
    usage = "/time [day/night/time] [-world, -w <world or local>] [-player, -p username] [-relative]",
    serverCommand = true)
    public static boolean commandTime(InnPlugin parent, IdpCommandSender sender, ParameterArguments args) {
        String worldName = args.getString("world", "w");
        IdpWorld targetWorld = null;

        if (worldName != null && sender.isPlayer()) {
            if (worldName.equalsIgnoreCase("local")) {
                IdpPlayer player = parent.getPlayer(sender.getName());
                targetWorld = player.getWorld();
            } else {
                targetWorld = IdpWorldFactory.getWorld(worldName);
            }

            if (targetWorld == null) {
                sender.printError("Invalid target world.");
                return true;
            }
        }

        IdpPlayer targetPlayer = (args.hasOption("player", "p") ? parent.getPlayer(args.getString("player", "p")) : null);

        if (targetPlayer != null && targetWorld != null) {
            sender.printError("Cannot set time for both a player and world!");
            return true;
        }

        long timeOfDay;
        String timeStr;
        try {
            if (StringUtil.matches(args.getString(0), null, "day", "d")) {
                timeOfDay = 500;
                timeStr = "day";
            } else if (StringUtil.matches(args.getString(0), "night", "n")) {
                timeOfDay = 14000;
                timeStr = "night";
            } else if (StringUtil.matches(args.getString(0), "set")) {
                timeOfDay = args.size() > 1 ? Long.parseLong(args.getString(1)) : 0l;
                timeStr = String.valueOf(timeOfDay);
            } else {
                timeOfDay = Long.parseLong(args.getString(0));
                timeStr = String.valueOf(timeOfDay);
            }
        } catch (NumberFormatException nfe) {
            sender.printError("Unknown time value!");
            return true;
        }

        if (targetPlayer == null) {
            if (targetWorld == null) {
                for (World w : parent.getServer().getWorlds()) {
                    IdpWorld world = IdpWorldFactory.getWorld(w.getName());
                    IdpWorldSettings settings = world.getSettings();

                    // Skip worlds that do not have a daylight cycle
                    if (settings != null && !settings.hasDaylightCycle()) {
                        continue;
                    }

                    world.getHandle().setTime(timeOfDay);
                }
            } else {
                targetWorld.getHandle().setTime(timeOfDay);
            }
        } else {
            targetPlayer.getHandle().setPlayerTime(timeOfDay, args.hasOption("relative", "r"));
        }

        sender.printInfo("Time set to " + timeStr
                + (targetWorld == null ? "" : " in world " + targetWorld.getName())
                + (targetPlayer == null ? "" : " for player " + targetPlayer.getName()) + ".");

        return true;
    }

    @CommandMethod(aliases = {"weather"},
    description = "Sets the weather for either the server or a targetted world.",
    permission = Permission.command_cheat_weather,
    usage = "/weather [storm/thunder/rain/thunderstorm/sun] [-world <worldname>] [-player, -p <player>]",
    serverCommand = true)
    public static boolean commandWeather(InnPlugin parent, Server server, IdpCommandSender<? extends CommandSender> sender, ParameterArguments pargs) {
        String worldName = pargs.getString("world", "w");

        // Check for single world
        World world = null;

        if (worldName != null) {
            world = server.getWorld(worldName);

            if (world == null) {
                sender.printInfo("Cannot find world ('" + worldName + "')!.");
                return true;
            }
        }

        String playerName = pargs.getString("player", "p");
        IdpPlayer player = null;

        if (playerName != null) {
            player = parent.getPlayer(playerName);

            if (player == null) {
                sender.printError("That player is not online.");
                return true;
            }
        }

        boolean makeThunder = false;
        boolean makeStorm = false;

        // Check type of action
        if (pargs.actionMatches(0, "thunderstorm")) {
            makeThunder = true;
            makeStorm = true;
        } else if (pargs.actionMatches(0, "thunder")) {
            makeThunder = true;
        } else if (pargs.actionMatches(0, "storm", "rain")) {
            makeStorm = true;
        }

        // Set storm/thunder status!
        if (world == null) {
            if (player != null) {
                player.getHandle().setPlayerWeather(makeStorm || makeThunder ? WeatherType.DOWNFALL : WeatherType.CLEAR);
                sender.printInfo("Weather changed for player " + player.getColoredName(), "!");
            } else {
                for (World w : InnPlugin.getPlugin().getServer().getWorlds()) {
                    IdpWorldSettings settings = IdpWorldFactory.getWorld(w.getName()).getSettings();

                    if (settings != null && !settings.hasWeather()) {
                        continue;
                    }

                    w.setStorm(makeStorm);
                    w.setThundering(makeThunder);
                }

                sender.printInfo("Weather changed for all worlds!");
            }
        } else {
            world.setStorm(makeStorm);
            world.setThundering(makeThunder);

            sender.printInfo("Weather changed for world " + world.getName() + "!");
        }

        return true;
    }

    @CommandMethod(aliases = {"gamemode", "gm"},
    description = "Sets your game mode.",
    permission = Permission.command_cheat_gamemode,
    usage = "/gamemode <mode>",
    serverCommand = false)
    public static boolean commandGameMode(Server server, InnPlugin parent, IdpPlayer player, String[] args) {
        if (args.length == 1) {
            GameMode setMode = null;

            if (args[0].equalsIgnoreCase("0")) {
                setMode = GameMode.SURVIVAL;
            } else if (args[0].equalsIgnoreCase("1")) {
                setMode = GameMode.CREATIVE;
            } else if (args[0].equalsIgnoreCase("2")) {
                setMode = GameMode.ADVENTURE;
            } else if (args[0].equalsIgnoreCase("3")) {
                setMode = GameMode.SPECTATOR;
            } else {
                player.printRaw("Unknown game mode!");
                return true;
            }

            GameMode previousMode = player.getHandle().getGameMode();

            // Can't set same game mode!
            if (previousMode == setMode) {
                player.printError("You are already on this game mode!");
                return true;
            }

            player.getHandle().setGameMode(setMode);
            player.printRaw("Game mode set to " + setMode.name().toLowerCase() + ".");

            // Switch fly back on if previous game mode was creative and flight is enabled
            if (previousMode == GameMode.CREATIVE && player.getSession().hasFlightMode()) {
                Block standingBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                player.setAllowFlight(true, !IdpMaterial.fromBlock(standingBlock).isSolid());
            }

            return true;
        }

        return false;
    }

    private static void setEnchantment(IdpItemStack item) {
        switch (item.getMaterial()) {
            case IRON_SWORD:
            case DIAMOND_SWORD:
            case WOOD_SWORD:
            case GOLD_SWORD:
                item.getItemdata().addEnchantment(EnchantmentType.LOOTING, 10);
                break;
            case DIAMOND_HELMET:
            case DIAMOND_CHEST:
            case DIAMOND_BOOTS:
            case DIAMOND_LEGGINS:

            case GOLD_HELMET:
            case GOLD_CHEST:
            case GOLD_BOOTS:
            case GOLD_LEGGINGS:

            case IRON_HELMET:
            case IRON_CHEST:
            case IRON_BOOTS:
            case IRON_LEGGINGS:

            case LEATHER_HELMET:
            case LEATHER_CHEST:
            case LEATHER_BOOTS:
            case LEATHER_LEGGINGS:

            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHEST:
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_LEGGINGS:
                item.getItemdata().addEnchantment(EnchantmentType.PROTECTION_PROJECTILE, 10);
                item.getItemdata().addEnchantment(EnchantmentType.PROTECTION_ENVIRONMENTAL, 10);
                item.getItemdata().addEnchantment(EnchantmentType.PROTECTION_EXPLOSIONS, 10);
                item.getItemdata().addEnchantment(EnchantmentType.PROTECTION_FALL, 10);
                item.getItemdata().addEnchantment(EnchantmentType.PROTECTION_FIRE, 10);
                item.getItemdata().addEnchantment(EnchantmentType.PROTECTION_RESPIRATION, 10);
                break;

        }
    }

    private static void setPlayerHead(LivingEntity entity, String playerName) {
        ItemStack stack = MagicValueUtil.materialAmountToItemStack(IdpMaterial.PLAYER_SKULL, 1);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(playerName);
        stack.setItemMeta(meta);
        entity.getEquipment().setHelmet(stack);
    }

    private static void flyMob(Entity entity) {
        Location loc = entity.getLocation();
        World world = loc.getWorld();
        Bat bat = (Bat) world.spawnEntity(loc, EntityType.BAT);
        PotionEffect pe = new PotionEffect(PotionEffectType.INVISIBILITY, 50000, 0, true);
        bat.addPotionEffect(pe);
        bat.setPassenger(entity);
    }

    private static void makeBaby(Entity entity) {
        if (entity instanceof Ageable) {
            Ageable ageable = (Ageable) entity;
            ageable.setBaby();
        } else if (entity instanceof Zombie) {
            Zombie zombie = (Zombie) entity;
            zombie.setBaby(true);
        }
    }

    private static void giveEndermanItem(Enderman enderman, IdpMaterial carryMaterial) {
        MaterialData mdata = new MaterialData(carryMaterial.getBukkitMaterial());
        enderman.setCarriedMaterial(mdata);
    }

    private static void giveMobEquipment(LivingEntity entity, List<IdpMaterial> equipment) {
        EntityEquipment entityEquipment = entity.getEquipment();

        for (int i = 0; i < 5; i++) {
            IdpMaterial mat = equipment.get(i);

            if (mat != IdpMaterial.AIR) {
                ItemStack bukkitStack = new IdpItemStack(mat, 1).toBukkitItemstack();

                switch (i) {
                    case 0:
                        entityEquipment.setHelmet(bukkitStack);
                        break;
                    case 1:
                        entityEquipment.setChestplate(bukkitStack);
                        break;
                    case 2:
                        entityEquipment.setLeggings(bukkitStack);
                        break;
                    case 3:
                        entityEquipment.setBoots(bukkitStack);
                        break;
                    case 4:
                        entityEquipment.setItemInMainHand(bukkitStack);
                        break;
                }
            }
        }
    }

}
