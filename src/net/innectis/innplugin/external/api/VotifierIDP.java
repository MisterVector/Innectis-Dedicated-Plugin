package net.innectis.innplugin.external.api;

import com.sk89q.util.StringUtil;
import com.vexsoftware.votifier.Votifier;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VoteListener;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.external.api.interfaces.IVotifierIDP;
import net.innectis.innplugin.external.LibraryInitalizationError;
import net.innectis.innplugin.external.VotifierService;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.handlers.datasource.FileHandler;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.util.ChatUtil;
import net.innectis.innplugin.util.PlayerUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.plugin.Plugin;

/**
 * API for votifier functionality
 *
 * @author Lynxy
 */
public class VotifierIDP implements VoteListener, IVotifierIDP {

    // The cooldown time between votes
    public static final long VOTE_COOLDOWN_TIME = (20 * 60 * 60 * 1000);
    private InnPlugin plugin;
    private Votifier votifierPlugin;
    private List<VotifierService> votifierServices = new ArrayList<VotifierService>();

    public VotifierIDP(InnPlugin plugin, Plugin bukkitPlugin) {
        this.plugin = plugin;
        this.votifierPlugin = (Votifier) bukkitPlugin;
    }

    /**
     * Returns the list of votifier services
     * @return
     */
    public List<VotifierService> getVotifierServices() {
        return votifierServices;
    }

    @Override
    public void initialize() throws LibraryInitalizationError {
        try {
            List<String> data = FileHandler.getData(Configuration.FILE_VOTIFIERSERVICES);

            for (String line : data) {
                String[] services = line.split(" ");

                if (services.length >= 3) {
                    String serviceName = services[0];
                    String serviceURL = services[1];
                    String serviceTitle = StringUtil.joinString(services, " ", 2);
                    VotifierService service = new VotifierService(serviceName, serviceURL, serviceTitle);

                    votifierServices.add(service);
                } else {
                    InnPlugin.logError("Vote service malformed for service: " + services[0]);
                }
            }

            this.votifierPlugin.getListeners().add(this);
        } catch (FileNotFoundException ex) {
            throw new LibraryInitalizationError("VotifierServices.txt not found!", ex);
        }
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    /**
     * Handles a vor that has been made.
     * @param vote
     */
    @Override
    public void voteMade(Vote vote) {
        String name = vote.getUsername().trim();

        // Filter out test names
        if (name.equalsIgnoreCase("test notification") || name.equalsIgnoreCase("anonymous")) {
            return;
        }

        PlayerCredentials credentials = PlayerCredentialsManager.getByName(name);

        // Don't allow non-existant players to vote
        if (credentials == null) {
            return;
        }

        // Check if service is valid
        if (!isValidService(vote)) {
            InnPlugin.logError("Unauthorized vote server \"" + vote.getServiceName() + "\" tried to send a vote!");
            return;
        }

        IdpPlayer player = plugin.getPlayer(credentials.getUniqueId());
        Timestamp voteTimestamp = new Timestamp(System.currentTimeMillis());
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT timestamp FROM vote_log WHERE player_id = ? AND service = ?");
            statement.setString(1, credentials.getUniqueId().toString());
            statement.setString(2, vote.getServiceName());
            result = statement.executeQuery();

            if (result.next()) {
                Timestamp stamp = result.getTimestamp("timestamp");
                long diff = (System.currentTimeMillis() - stamp.getTime());

                // Less than 20 hours since last vote from this service (allow for slight variation)
                if (diff < VOTE_COOLDOWN_TIME) {
                    InnPlugin.logError("Illegitimate vote received from \"" + vote.getServiceName() + "\" for voter " + name + "(" + vote.getAddress() + ")");

                    if (player != null) {
                        player.printError("Your vote was received, but you have not");
                        player.printError("been credited any points because you have");
                        player.printError("already voted too many times today! Sorry");
                    }

                    return;
                } else {
                    addPlayerVote(credentials.getUniqueId(), vote, voteTimestamp, true);
                }
            } else {
                addPlayerVote(credentials.getUniqueId(), vote, voteTimestamp, false);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("SQLException VotifierListener::voteMade(1) - ", ex.getMessage());
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        if (player != null) {
            player.getSession().setLastVoteTimestamp(voteTimestamp);
        }

        String coloredName = PlayerUtil.getColoredName(name);

        InnPlugin.logInfo("Vote received from \"" + vote.getServiceName() + "\" by voter " + coloredName, "(" + vote.getAddress() + ")");

        TransactionObject to = TransactionHandler.getTransactionObject(credentials.getUniqueId(), credentials.getName());
        to.addValue(1, TransactionHandler.TransactionType.VOTE_POINTS);

        if (player != null && player.isOnline()) {
            int votePoints = to.getValue(TransactionType.VOTE_POINTS);
            player.printInfo("You have been credited " + ChatColor.AQUA + "1", " vote point! (" + ChatColor.AQUA + votePoints, " points total)");
        }

        VotifierService service = getService(vote.getServiceName());

        TextComponent text = ChatUtil.createTextComponent(ChatColor.GREEN, coloredName, " voted for Innectis on ");
        text.addExtra(ChatUtil.createHTMLLink(vote.getServiceName(), service.getURL()));
        text.addExtra(ChatUtil.createTextComponent(ChatColor.GREEN, ". Thank you!"));

        for (IdpPlayer p : plugin.getOnlinePlayers()) {
            p.print(text);

            TextComponent extraText = ChatUtil.createTextComponent(ChatColor.GREEN, "To list all vote sites click ");
            extraText.addExtra(ChatUtil.createCommandLink("here", "/vote"));
            extraText.addExtra(ChatUtil.createTextComponent(ChatColor.GREEN, " or type /vote."));

            p.print(extraText);
        }
    }

    /**
     * Returns the amount of time that has elapsed since the player
     * last voted from the specified service. The value is 0 if the
     * player had not voted for that service
     * @param player
     * @param service
     * @return
     */
    public long getLastVoteTimeFromService(IdpPlayer player, String service) {
        PreparedStatement statement = null;
        ResultSet set = null;
        long remain = 0;

        try {
            statement = DBManager.prepareStatement("SELECT timestamp FROM vote_log WHERE player_id = ? AND service = ?;");
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, service);
            set = statement.executeQuery();

            if (set.next()) {
                long now = System.currentTimeMillis();
                long then = set.getTimestamp("timestamp").getTime();
                remain = (now - then);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to get vote time for " + player.getName() + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
            DBManager.closeResultSet(set);
        }

        return remain;
    }

    /**
     * Gets a votifier service from the specified URL
     * @param serviceName
     * @return
     */
    private VotifierService getService(String serviceName) {
        for (VotifierService votifierService : votifierServices) {
            if (votifierService.getName().equalsIgnoreCase(serviceName)) {
                return votifierService;
            }
        }

        return null;
    }

    private static void addPlayerVote(UUID playerId, Vote vote, Timestamp timestamp, boolean update) {
        PreparedStatement statement = null;

        try {
            if (update) {
                statement = DBManager.prepareStatement("UPDATE vote_log SET timestamp = ? WHERE player_id = ? AND service = ?");
                statement.setTimestamp(1, timestamp);
                statement.setString(2, playerId.toString());
                statement.setString(3, vote.getServiceName());
                statement.executeUpdate();
            } else {
                statement = DBManager.prepareStatement("INSERT INTO vote_log (player_id, ip, service, timestamp) VALUES (?, ?, ?, ?)");
                statement.setString(1, playerId.toString());
                statement.setString(2, vote.getAddress());
                statement.setString(3, vote.getServiceName());
                statement.setTimestamp(4, timestamp);
                statement.execute();
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save vote! " + ex.getMessage());
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Checks if the given service is allowed to send in votes
     * @param vote
     * @return
     */
    private boolean isValidService(Vote vote) {
        String voteServiceName = vote.getServiceName();

        for (VotifierService service : votifierServices) {
            if (voteServiceName.equalsIgnoreCase(service.getName())) {
                return true;
            }
        }

        return false;
    }

}
