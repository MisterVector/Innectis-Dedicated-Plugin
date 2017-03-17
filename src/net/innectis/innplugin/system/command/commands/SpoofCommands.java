package net.innectis.innplugin.system.command.commands;

import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.SpoofObject;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.util.LynxyArguments;
import net.innectis.innplugin.util.PlayerUtil;
import net.innectis.innplugin.util.SmartArguments;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class SpoofCommands {

    @CommandMethod(aliases = {"slogin"},
    description = "Spoofs a fake login message.",
    permission = Permission.command_spoofing_login,
    usage = "/slogin <username>",
    serverCommand = true,
    hiddenCommand = true)
    public static boolean commandLogin(InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String playerName = args[0];
        String coloredSpoofName = null;
        PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

        // Modify group color by this player's group
        if (credentials != null) {
            coloredSpoofName = PlayerUtil.getColoredName(credentials);
        } else {
            // Default to guest color
            net.innectis.innplugin.player.chat.ChatColor color = net.innectis.innplugin.player.chat.ChatColor.YELLOW;
            coloredSpoofName = color + playerName;
        }

        parent.broadCastMessage(coloredSpoofName + ChatColor.YELLOW + " joined the server.");
        return true;
    }

    @CommandMethod(aliases = {"slogout"},
    description = "Spoofs a fake logout message.",
    permission = Permission.command_spoofing_logout,
    usage = "/slogout <username>",
    serverCommand = true,
    hiddenCommand = true)
    public static boolean commandLogout(InnPlugin parent, IdpCommandSender sender, String[] args) {
        if (args.length < 1) {
            return false;
        }

        String playerName = args[0];
        String coloredSpoofName = null;
        PlayerCredentials credentials = PlayerCredentialsManager.getByName(playerName);

        // Modify group color by this player's group
        if (credentials != null) {
            coloredSpoofName = PlayerUtil.getColoredName(credentials);
        } else {
            // Default to guest color
            net.innectis.innplugin.player.chat.ChatColor color = net.innectis.innplugin.player.chat.ChatColor.YELLOW;
            coloredSpoofName = color + playerName;
        }

        parent.broadCastMessage(coloredSpoofName + ChatColor.YELLOW + " left the server.");
        return true;
    }

    @CommandMethod(aliases = {"sresetnames"},
    description = "Resets all spoofed names to the real ones.",
    permission = Permission.command_spoofing_resetnames,
    usage = "/sresetnames",
    serverCommand = true,
    hiddenCommand = true)
    public static boolean commandResetNames(InnPlugin parent, IdpCommandSender sender, String[] args) {
        for (Player p : parent.getServer().getOnlinePlayers()) {
            p.setDisplayName(p.getName());
            IdpPlayer idpPlayer = parent.getPlayer(p);
            idpPlayer.getSession().clearSpoofObject();
        }
        sender.printInfo("All names reset to original name!");
        return true;
    }

    @CommandMethod(aliases = {"ssetname"},
    description = "Spoofs the username of the player that uses this.",
    permission = Permission.command_spoofing_setname,
    usage = "/setname [name] [hide, -h [-logout]] [-unhide, -show [-login [name]]] [-reset, -r]",
    serverCommand = false,
    hiddenCommand = true)
    public static boolean commandSetName(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        String name = player.getName();
        PlayerGroup group = player.getGroup();

        if (args.hasOption("reset", "r")) {
            player.printInfo("Your name has been reset.");
        } else if (args.hasOption("hide", "h")) {
            SpoofObject obj = player.getSession().getSpoofObject();

            if (obj == null || !obj.isHidden()) {
                player.printInfo("You have been hidden!");
                name = "";

                if (args.hasOption("logout", "l")) {
                    parent.broadCastMessage(player.getColoredDisplayName() + ChatColor.YELLOW + " left the server.");
                }
            } else {
                player.printError("You are already hidden.");
                return true;
            }
        } else if (args.hasOption("unhide", "show")) {
            boolean spoofAnother = false;
            String coloredSpoofName = "";

            if (args.hasOption("login", "l")) {
                coloredSpoofName = player.getColoredName();
            } else if (args.hasArgument("login", "l")) {
                name = args.getString("login", "l");

                if (name.length() > 15) {
                    player.printInfo("Name must be 15 characters or less!");
                    return true;
                }

                spoofAnother = true;
                IdpPlayer target = parent.getPlayer(name);

                if (target != null) {
                    name = target.getName();
                }

                PlayerCredentials credentials = PlayerCredentialsManager.getByName(name);

                if (credentials != null) {
                    coloredSpoofName = PlayerUtil.getColoredName(credentials);
                } else {
                    group = PlayerGroup.GUEST;
                    coloredSpoofName = PlayerGroup.GUEST.getPrefix().getTextColor() + name;
                }
            }

            parent.broadCastMessage(coloredSpoofName + ChatColor.YELLOW + " has joined the server.");
            player.printInfo("You have reappeared as " + (spoofAnother ? name : "yourself") + "!");
        } else if (args.getActionSize() > 0) {
            IdpPlayer target = parent.getPlayer(args.getString(0), true);
            name = (target != null ? target.getName() : args.getString(0));

            if (!name.equalsIgnoreCase(player.getName())) {
                if (parent.getPlayer(name, true) != null) {
                    player.printError("That player is online. Can't change name!");
                    return true;
                }

                if (name.length() > 15) {
                    player.printInfo("Name must be 15 characters or less!");
                    return true;
                }

                PlayerCredentials credentials = PlayerCredentialsManager.getByName(name);

                if (credentials != null) {
                    group = PlayerGroup.getGroupOfPlayerById(credentials.getUniqueId());
                } else {
                    group = PlayerGroup.GUEST;
                }

                player.printInfo("Your name has been changed to: " + name);
            }
        } else {
            player.printInfo("Your name has been reset.");
        }

        if (!name.equalsIgnoreCase(player.getName())) {
            if (player.isOtherPlayerSpoofing(name)) {
                player.printError("Another user is spoofing that name.");
                return true;
            }
        }

        if (name.equalsIgnoreCase(player.getName())) {
            player.getSession().clearSpoofObject();
        } else {
            player.getSession().setSpoofName(name, group);
        }

        Player bukkitPlayer = player.getHandle();

        bukkitPlayer.setDisplayName(name);
        bukkitPlayer.setPlayerListName(name);
        player.getSession().setPrefix(2, group.getPrefix());
        player.updateClientDisplayName(name);

        return true;
    }

    @CommandMethod(aliases = {"sudo"},
    description = "Spoofs a fake message from a player.",
    permission = Permission.command_spoofing_sudo,
    usage = "/sudo <username> <type> <message>",
    serverCommand = true,
    hiddenCommand = true,
    hideinlists = true)
    public static boolean commandSudo(InnPlugin parent, IdpCommandSender sender, SmartArguments args) {
        if (args.size() >= 3) {
            IdpPlayer player = args.getPlayer(0);
            if (player != null) {
                if (args.getString(1).equalsIgnoreCase("say")) {
                    parent.getListenerManager().getChatListener().playerGlobalChat(player, args.getJoinedStrings(2), ChatColor.WHITE);
                } else if (args.getString(1).equalsIgnoreCase("local")) {
                    parent.getListenerManager().getChatListener().playerLocalChat(player, args.getJoinedStrings(2), ChatColor.GRAY, true);
                } else if (args.getString(1).equalsIgnoreCase("me")) {
                    parent.getListenerManager().getChatListener().playerEmote(player, args.getJoinedStrings(2));
                } else {
                    sender.printError("Invalid sudo type! (say/local/me)");
                }
            } else {
                sender.printError("Player " + args.getString(0) + " not found!");
            }
            return true;
        }
        return false;
    }

    @CommandMethod(aliases = {"vanish", "v"},
    description = "Toggles the player's visibility.",
    permission = Permission.command_admin_vanish,
    usage = "/vanish [-status]",
    serverCommand = false,
    hiddenCommand = true)
    public static boolean commandVanish(InnPlugin parent, IdpPlayer player, LynxyArguments args) {
        if (args.getOptionSize() > 0) {
            if (args.hasOption("status")) {
                if (player.getSession().isVisible()) {
                    player.printInfo("You are currently: " + ChatColor.AQUA + "Visible" + ChatColor.GREEN + "!");
                } else {
                    player.printInfo("You are currently: " + ChatColor.AQUA + "Invisible" + ChatColor.GREEN + "!");
                }
                return true;
            }
        }

        boolean toggle = !player.getSession().isVisible();
        player.getSession().setPlayerVisible(toggle);

        if (toggle) {
            player.printInfo("You have returned!");
            player.getHandle().removePotionEffect(PotionEffectType.INVISIBILITY);
        } else {
            player.printInfo("You have vanished off the face of the earth!");
            player.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 1), true);
        }

        return true;
    }

}
