package net.innectis.innplugin.system.command.commands;

import java.util.List;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.request.GenericRequest;
import net.innectis.innplugin.player.request.Request;
import net.innectis.innplugin.util.ChatUtil;
import net.innectis.innplugin.util.ParameterArguments;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Hret
 */
public final class RequestCommands {

    @CommandMethod(aliases = {"accept", "a"},
    description = "Accepts an incoming request.",
    permission = Permission.command_request_accept,
    usage = "/accept [id]",
    serverCommand = false,
    disabledWorlds = {IdpWorldType.NETHER})
    public static boolean commandAccept(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;

        // Invalid args
        if (args.length > 1) {
            return false;
        }


        List<Request> requests = player.getSession().getRequests();
        if (requests.isEmpty()) {
            player.printError("You have no pending requests!");
            return true;
        }
        int id = 0;
        if (args.length == 1) {
            try {
                id = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                player.printError("Unknown number: " + args[0] + "!");
                return true;
            }
        }
        if (id >= requests.size()) {
            player.printError("You don't have a request with ID " + id + "!");
            return true;
        }
        Request req = requests.get(id);
        req.accept();

        player.printInfo("You have accepted the " + req.getDescription());
        return true;
    }

    @CommandMethod(aliases = {"reject"},
    description = "Rejects an incomming request",
    permission = Permission.command_request_reject,
    usage = "/reject [id]",
    serverCommand = false)
    public static boolean commandReject(Server server, InnPlugin parent, IdpCommandSender<? extends CommandSender> sender, String[] args) {
        IdpPlayer player = (IdpPlayer) sender;

        // Invalid args
        if (args.length > 1) {
            return false;
        }

        List<Request> requests = player.getSession().getRequests();
        if (requests.isEmpty()) {
            player.printError("You have no pending requests!");
            return true;
        }
        int id = 0;
        if (args.length == 1) {
            try {
                id = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                player.printError("Unknown number: " + args[0] + "!");
                return true;
            }
        }
        if (id >= requests.size()) {
            player.printError("You don't have an incoming request with ID " + id + "!");
            return true;
        }
        Request req = requests.get(id);
        req.reject();

        player.printInfo("You have rejected the " + req.getDescription());
        return true;
    }

    @CommandMethod(aliases = {"requestlist", "requests", "rq"},
    description = "Lists all incomming requests.",
    permission = Permission.command_request_requestlist,
    usage = "/requestlist",
    serverCommand = false)
    public static void commandRequests(IdpPlayer player) {
        List<Request> requests = player.getSession().getRequests();
        player.printInfo("You have " + requests.size() + " pending request" + (requests.size() != 1 ? "s!" : "!"));
        int i = 0;
        for (Request req : requests) {
            player.print(ChatColor.DARK_AQUA, (i < 10 ? i + " " : i) + ChatColor.AQUA.toString() + " - " + req.getDescription());
            i++;
        }
    }

    @CommandMethod(aliases = {"request"},
    description = "Makes a request.",
    permission = Permission.command_request_request,
    usage = "/request <player> <message>",
    serverCommand = false)
    public static boolean commandRequest(InnPlugin plugin, IdpPlayer player, ParameterArguments args) {
        if (args.size() != 2) {
            return false;
        }

        IdpPlayer target = args.getPlayer(0);

        if (target == null) {
            player.printError("Player not found!");
            return true;
        }

        Request req = new GenericRequest(plugin, target, player, System.currentTimeMillis(), 60000l);
        target.getSession().addRequest(req);
        target.print(ChatColor.AQUA, player.getColoredDisplayName(), " wants to " + args.getString(1));
        target.print(ChatColor.AQUA, "Please type /accept or /reject within 30 seconds.");

        TextComponent text = ChatUtil.createTextComponent(ChatColor.AQUA, "You can also click ");
        text.addExtra(ChatUtil.createCommandLink("here", "/accept"));
        text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to accept or "));
        text.addExtra(ChatUtil.createCommandLink("here", "/reject"));
        text.addExtra(ChatUtil.createTextComponent(ChatColor.AQUA, " to reject."));
        target.print(text);

        return true;
    }

}
