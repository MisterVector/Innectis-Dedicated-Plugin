package net.innectis.innplugin.system.command.commands;

import net.innectis.innplugin.system.game.IdpGame;
import net.innectis.innplugin.system.game.IdpStartResult;
import net.innectis.innplugin.system.game.IdpGameType;
import net.innectis.innplugin.system.game.IdpRegionGame;
import net.innectis.innplugin.system.game.IdpGameManager;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.system.command.CommandMethod;
import net.innectis.innplugin.system.game.games.IdpCTF;
import net.innectis.innplugin.system.game.games.IdpDomination;
import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.TimerBroadcastTask;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.system.game.games.IdpEnderGolf;
import net.innectis.innplugin.system.game.games.IdpHungerGames;
import net.innectis.innplugin.system.game.games.IdpQuakeCraft;
import net.innectis.innplugin.system.game.games.IdpTron;
import net.innectis.innplugin.tasks.Task;
import net.innectis.innplugin.tasks.TaskManager;
import net.innectis.innplugin.util.DateUtil;
import net.innectis.innplugin.util.NotANumberException;
import net.innectis.innplugin.util.ParameterArguments;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 *
 * Commands that work with the game system.
 */
public final class GameCommands {

    private GameCommands() {
    }

    @CommandMethod(aliases = {"timer"},
    description = "This will start counting down for the given amount of time.",
    permission = Permission.command_game_timer,
    usage = "/timer <time> ",
    usage_Admin = "/timer <time> [-public] [-name <name>] [-list] [-delete <ID>]",
    serverCommand = true)
    public static void commandTimer(InnPlugin parent, IdpCommandSender sender, ParameterArguments args) {
        // Server is always public, else check for extra perm
        boolean isPublic = !sender.isPlayer() || (args.hasOption("public") && sender.hasPermission(Permission.command_game_timer_public));

        // Get the time in milliseconds
        long milliseconds = DateUtil.getTimeFormula(args.getString(0));

        // Max 2 hours
        if (milliseconds > 7200000) {
            sender.printError("That will take too long!");
            return;
        }

        if (args.hasOption("list")) {
            List<Long> timers = parent.getTimers();

            if (timers.isEmpty()) {
                sender.printError("There are no timers available!");
                return;
            }

            for (long taskId : timers) {
                Task task = parent.getTaskManager().getTask(taskId);
                TimerBroadcastTask realTask = (TimerBroadcastTask) task;
                long tillDone = realTask.getTargetTime() - System.currentTimeMillis();
                sender.printInfo("Timer #" + taskId + (realTask.getTimerText() != null ? " '" + realTask.getTimerText() + "' " : " ") + "time left: " + DateUtil.getTimeString(tillDone, true));
            }
        } else if (args.hasOption("delete")) {
            long taskId = 0;

            try {
                taskId = args.getInt("delete");
            } catch (NumberFormatException nfe) {
                sender.printError("Invalid timer ID entered!");
                return;
            }

            TaskManager tm = parent.getTaskManager();
            Task task = tm.getTask(taskId);

            // Task was not found, or is not a timer task
            if (task == null || !(task instanceof TimerBroadcastTask)) {
                sender.printError("This is not a valid timer ID!");
                return;
            }

            String timerName = ((TimerBroadcastTask) task).getTimerText();
            tm.removeTask(task);
            parent.removeTimer(taskId);

            sender.printInfo("Deleted timer #" + taskId + (timerName != null ? " '" + timerName + "'" : "") + "!");
        } else {
            // Add the 2 seconds it takes to add the timer.
            milliseconds += 2000;

            // Set the name of the sender if a private task.
            String sendername = null;
            if (!isPublic) {
                sendername = sender.getName();
            }

            String timerName = args.getString("name");

            // Get the task
            TimerBroadcastTask timer = new TimerBroadcastTask(parent, timerName, System.currentTimeMillis() + (milliseconds), sendername);
            long taskId = parent.getTaskManager().addTask(timer);
            timer.setId(taskId);
            parent.addTimer(taskId);

            // Print info
            sender.printInfo("Timer " + (timerName != null ? "'" + timerName + "' " : "") + "created!");
        }
    }

    @CommandMethod(aliases = {"score"},
    description = "Command to get the score of the current game.",
    permission = Permission.command_game_score,
    usage = "/score [-game <id>] [-player <playername>]",
    usage_Mod = "/score [-player <playername>]",
    serverCommand = true)
    public static void commandScore(InnPlugin parent, IdpCommandSender sender, ParameterArguments args) {
        if (args.hasOption("game", "g")) {
            int id = args.getIntDefaultTo(-1, "game", "g");

            if (id >= 0) {
                IdpGame game = IdpGameManager.getInstance().getGame(id);

                if (game != null) {
                    game.printScore(sender);
                    return;
                }
            }

            sender.printError("Unknown game ID!");
            return;
        }

        if (args.hasOption("player", "p")) {
            IdpPlayer player = args.getPlayer("player", "p");

            if (player != null) {
                IdpGame game = IdpGameManager.getInstance().getGame(player);

                if (game != null) {
                    game.printScore(sender);
                    return;
                } else {
                    sender.printError(player.getName() + " is not in a game!");
                }
            } else {
                sender.printError("Player not found!");
            }

            return;
        }

        if (sender.isPlayer()) {
            IdpGame game = IdpGameManager.getInstance().getGame((IdpPlayer) sender);

            if (game != null) {
                game.printScore(sender);
                return;
            }
        }

        sender.printError("You are not in a game!");
    }

    @CommandMethod(aliases = {"game"},
    description = "A command to control games.",
    permission = Permission.command_game_game,
    usage = "/game [-listTypes (-lt)] [-listGames (-lg)] [-start <mode> [-limit (-l) <amount>]] [-addPlayer <player>] [-parse [parse]]",
    usage_Mod = "/game [-listTypes (-lt)] [-listGames (-lg)] [-start <mode> [-limit (-l) <amount>]] [-end <id>] [-addPlayer <player> <-id <id>>] [-parse [parse] <-id <id>>]",
    serverCommand = true)
    public static boolean commandGame(InnPlugin parent, IdpCommandSender sender, ParameterArguments args) {
        if (args.hasOption("listgames", "lg")) {
            Map<Integer, IdpGame> games = IdpGameManager.getInstance().getGames();

            if (games.isEmpty()) {
                sender.printError("There are no games in progress.");
                return true;
            }

            int idx = 1;
            for (IdpGame game : games.values()) {
                int gameId = game.getId();

                if (game instanceof IdpRegionGame) {
                    IdpWorldRegion region = ((IdpRegionGame) game).getRegion();

                    if (region instanceof InnectisLot) {
                        int lotId = ((InnectisLot) region).getId();
                        sender.printInfo(idx++ + ". " + game.getGameType() + " (game #" + game.getId() + ") in lot #" + lotId);
                    } else {
                        Vector pos1 = region.getPos1();
                        String worldName = region.getWorld().getName();
                        String blockString = pos1.getBlockX() + ", " + pos1.getBlockY() + ", " + pos1.getBlockZ();

                        sender.printInfo(idx++ + ". " + game.getGameType() + " (game #" + gameId + ") at " + blockString + " in " + worldName);
                    }
                } else {
                    sender.printInfo(idx++ + ". " + game.getGameType() + " (game #" + gameId + ")");
                }
            }

            return true;
        }

        if (args.hasOption("listtypes", "lt")) {
            IdpGameType[] types = IdpGameType.values();

            sender.printInfo("Possible game modes:");
            for (IdpGameType type : types) {
                // Only types that can be triggerd.
                if (type.getTriggers().length > 0) {
                    sender.printInfo(" - ", type.getName(), " (", type.getTriggers()[0], ")");
                }
            }

            return true;
        }

        if (!StringUtil.stringIsNullOrEmpty(args.getString("start"))) {
            if (!sender.isPlayer()) {
                sender.printError("Console may not start games!");
                return true;
            }

            IdpPlayer player = (IdpPlayer) sender;
            IdpGameManager gameManager = IdpGameManager.getInstance();

            if (gameManager.isInGame(player)) {
                player.printError("You are already in a game!");
                return true;
            }

            int winLimit = 0;

            if (args.hasOption("limit", "l")) {
                try {
                    winLimit = args.getInt("limit", "l");

                    if (winLimit < 1) {
                        sender.printError("Win limit cannot be less than 1.");
                        return true;
                    }
                } catch (NotANumberException nan) {
                    sender.printError("Win limit is not an integer.");
                    return true;
                }
            }

            IdpGameType type = IdpGameType.fromTrigger(args.getString("start"));

            if (type == null) {
                sender.printError("Unknown game type!");
                return true;
            }

            if (!player.hasPermission(type.getPermission())) {
                sender.printError("You do not have permission to run this game!");
                return true;
            }

            // Check if there is a lot and if the given player is the owner, a member or operator
            InnectisLot lot = LotHandler.getLot(player.getLocation());

            if (lot == null) {
                sender.printError("This game must be played on a lot.");
                return true;
            }


            if (!lot.canPlayerManage(player.getName()) && !lot.containsMember(player.getDisplayName())
                    && !player.hasPermission(Permission.game_start_anywhere)) {
                sender.printError("You cannot start a game here!");
                return true;
            }

            IdpGame game;
            switch (type) {
                case CTF:
                    game = new IdpCTF(LotHandler.getLot(player.getLocation()), player.getName(), (winLimit > 0 ? winLimit : 50));
                    break;
                case DOMINATION:
                    game = new IdpDomination(LotHandler.getLot(player.getLocation()).getParentTop(), player.getName(), winLimit);
                    break;
                case QUAKECRAFT:
                    game = new IdpQuakeCraft(LotHandler.getLot(player.getLocation()).getParentTop(), player.getName(), (winLimit == 0 ? 25 : winLimit));
                    break;
                case TRON:
                    game = new IdpTron(LotHandler.getLot(player.getLocation()).getParentTop(), player.getName());
                    break;
                case HUNGERGAMES:
                    game = new IdpHungerGames(LotHandler.getLot(player.getLocation()).getParentTop(), player.getName());
                    break;
                case ENDERGOLF:
                    game = new IdpEnderGolf(LotHandler.getLot(player.getLocation()).getParentTop(), player.getName(), (winLimit == 0 ? 25 : winLimit));
                    break;
                default:
                    sender.printError("That game is not supported yet.");
                    return true;
            }

            IdpStartResult result = game.canStart();

            if (result.canStart()) {
                gameManager.addGame(game);

                game.initializeGame();
                game.startGame();
            } else {
                player.printError("Cannot start game!");
                for (String err : result.getErrorMessage()) {
                    player.printError(err);
                }
            }

            InnPlugin.logDebug("Game started");
            return true;
        }

        if (args.hasOption("end")) {
            int id = args.getIntDefaultTo(-1, "end");

            boolean override = !sender.isPlayer() || ((IdpPlayer) sender).hasPermission(Permission.game_override_end);

            IdpGame game = null;
            if (id >= 0 && override) {
                game = IdpGameManager.getInstance().getGame(id);
            } else if (sender.isPlayer()) {
                game = IdpGameManager.getInstance().getGame((IdpPlayer) sender);
            }

            if (game != null) {

                if (sender.isPlayer()) {
                    // Only host or staff player
                    if (sender.getName().equalsIgnoreCase(game.getGameHost()) || override) {
                        game.endGame();
                    } else {
                        sender.printError("You can't stop this game!");
                    }
                } else {
                    game.endGame();
                }
            }

            InnPlugin.logDebug("Game ended");
            return true;
        }

        if (args.getPlayer("addPlayer") != null) {
            IdpPlayer target = args.getPlayer("addPlayer");
            int id = args.getIntDefaultTo(-1, "id");

            if (target == null) {
                sender.printError("Target player not found.");
                return true;
            }

            boolean override = !sender.isPlayer() || ((IdpPlayer) sender).hasPermission(Permission.game_override_add);

            IdpGame game = null;
            if (id >= 0 && override) {
                game = IdpGameManager.getInstance().getGame(id);
            } else if (sender.isPlayer()) {
                game = IdpGameManager.getInstance().getGame((IdpPlayer) sender);
            }

            if (game != null) {
                if (sender.isPlayer()) {
                    // Only host or staff player
                    if (sender.getName().equalsIgnoreCase(game.getGameHost()) || override) {
                        if (game.addPlayer(target)) {
                            sender.printInfo("You added " + target.getName() + " to the game!");
                        } else {
                            sender.printError("Unable to add " + target.getName() + " to the game!");
                        }
                    } else {
                        sender.printError("You can't manage this game!");
                    }
                } else {
                    if (game.addPlayer(target)) {
                        sender.printInfo("You added " + target.getName() + " to the game!");
                    } else {
                        sender.printError("Unable to add " + target.getName() + " to the game!");
                    }
                }
                InnPlugin.logDebug(target.getName() + " was added to the game.");
                return true;
            }
            return false;
        }

        if (args.getString("parse") != null) {
            String parse = args.getString("parse");
            int id = args.getIntDefaultTo(-1, "id");

            boolean override = !sender.isPlayer() || ((IdpPlayer) sender).hasPermission(Permission.game_override_parse);

            IdpGame game = null;
            if (id >= 0 && override) {
                game = IdpGameManager.getInstance().getGame(id);
            } else if (sender.isPlayer()) {
                game = IdpGameManager.getInstance().getGame((IdpPlayer) sender);
            }

            if (game != null) {
                if (sender.isPlayer()) {
                    // Only host or staff player
                    if (sender.getName().equalsIgnoreCase(game.getGameHost()) || override) {
                        game.parse(sender, parse);
                    } else {
                        sender.printError("You can't manage this game!");
                    }
                } else {
                    game.parse(sender, parse);
                }
                InnPlugin.logDebug("Parsed to Game #" + game.getId() + ": " + parse);
                return true;
            }
            return false;
        }

        if (args.hasOption("parse")) {
            int id = args.getIntDefaultTo(-1, "id");

            IdpGame game = null;
            if (id >= 0) {
                game = IdpGameManager.getInstance().getGame(id);
            } else if (sender.isPlayer()) {
                game = IdpGameManager.getInstance().getGame((IdpPlayer) sender);
            }

            if (game != null) {
                game.printParse(sender);
                return true;
            }

            return false;
        }

        return false;
    }

}
