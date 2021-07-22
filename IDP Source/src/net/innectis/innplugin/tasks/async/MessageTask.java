package net.innectis.innplugin.tasks.async;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.PlayerSettings;
import net.innectis.innplugin.tasks.DefaultTaskDelays;
import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.util.ChatUtil;
import net.innectis.innplugin.util.ColorUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * This displays the vip, reszone and vote message.
 * Both VIP & Reszone messages switch every 7 min, making both messages appear every 14 apart.
 * The Vote message displays every 42 minutes.
 */
public class MessageTask extends RepeatingTask {

    private static final long NO_MESSAGE_ONLINETIME = (2 * 24 * 60 * 60 * 1000); // 2 days
    private boolean nextVipMessage;
    private int timerCount;

    public MessageTask() {
        super(RunBehaviour.ASYNC, DefaultTaskDelays.Message);
        nextVipMessage = true;
        timerCount = 0;
    }

    @Override
    public void run() {
        nextVipMessage = !nextVipMessage;
        timerCount++;

        if (nextVipMessage) {
            for (IdpPlayer player : InnPlugin.getPlugin().getOnlinePlayers()) {

                if (player.getSession().getTotalOnlineTime() < NO_MESSAGE_ONLINETIME) {
                    if (player.getGroup() == PlayerGroup.USER || player.getGroup() == PlayerGroup.GUEST) {
                        player.print(ChatColor.YELLOW, "Got some spare cash?");
                        player.print(ChatColor.YELLOW, "Donate $5 to Innectis and get VIP!");

                        TextComponent text = ChatUtil.createTextComponent(ChatColor.RED, "If you haven't done so already, click ");
                        text.addExtra(ChatUtil.createHTMLLink("here", "http://www.tinyurl.com/innectisrules"));
                        text.addExtra(ChatUtil.createTextComponent(ChatColor.RED, " to view our rules."));
                        player.print(text);
                    }
                }
            }
        } else {
            IdpWorld world = IdpWorldFactory.getWorld(IdpWorldType.RESWORLD);

            // World may not have been loaded at all, so check this
            if (world != null) {
                for (IdpPlayer player : world.getPlayers()) {
                    player.print(ChatColor.DARK_RED, "Reminder: " + ChatColor.YELLOW + "Reszone is reset every once in awhile!");
                    player.print(ChatColor.YELLOW, "Do not build your home here, you will lose it!");
                }
            }

        }

        if (timerCount == 1) {
            for (IdpPlayer player : InnPlugin.getPlugin().getOnlinePlayers()) {
                if (player.getSession().hasSetting(PlayerSettings.TIPS)) {
                    TextComponent text = ChatUtil.createTextComponent(ChatColor.YELLOW, "Did you know we have a forum? Click ");
                    text.addExtra(ChatUtil.createHTMLLink("here", "https://archives.codespeak.org/innectis/forum/"));
                    text.addExtra(ChatUtil.createTextComponent(ChatColor.YELLOW, " to check it out!"));

                    player.print(text);
                }
            }
        }

        if (timerCount > 5) {
            timerCount = 0;

            for (IdpPlayer player : InnPlugin.getPlugin().getOnlinePlayers()) {
                PlayerSession session = player.getSession();
                Timestamp lastVoteTimestamp = session.getLastVoteTimestamp();

                // More than 20 hours since last vote, then display message.
                if (lastVoteTimestamp != null && (System.currentTimeMillis() - lastVoteTimestamp.getTime()) < (20 * 60 * 60 * 1000)) {
                    continue;
                }

                if (session.hasSetting(PlayerSettings.TIPS)) {
                    player.print(ChatColor.DARK_RED, "You have not voted within the past 24 hours!");

                    TextComponent text = ChatUtil.createTextComponent(ChatColor.YELLOW, "If you would like to vote for Innectis, click ");
                    text.addExtra(ChatUtil.createCommandLink("here", "/vote"));
                    text.addExtra(ChatUtil.createTextComponent(ChatColor.YELLOW, "."));
                    player.print(text);
                }
            }
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

}
