package net.innectis.innplugin.player.request;

import java.util.UUID;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 *
 * @author Hret
 *
 * A request that is send to a player.
 * It can be accepted, rejected or timed out.
 * The timeout will be done automaticly.
 */
public abstract class Request {

    /** The id of the request */
    protected final Long requestid;
    /** The timeout how long this request should stay active */
    protected final Long timeouttime;
    /** The plugin */
    protected final InnPlugin plugin;
    /** The ID of the player */
    protected final UUID playerId;
    /** The name of the player */
    protected final String player;
    /** The player that made the request */
    protected final String requester;
    /** The ID of the requester */
    protected final UUID requesterId;
    /** The ID of the linked delayed task */
    private long taskid;

    /**
     * @param currentTime in miliseconds
     * @param timeout in miliseconds
     */
    public Request(InnPlugin plugin, IdpPlayer player, IdpPlayer requester, Long currentTime, Long timeout) {
        this.timeouttime = currentTime + timeout;
        this.plugin = plugin;
        this.player = player.getName();
        this.playerId = player.getUniqueId();
        this.requester = requester.getName();
        this.requesterId = requester.getUniqueId();
        this.requestid = generateRequestId();
        startTimeoutTask(timeout);
    }

    /**
     * Gets the player who is the target of the request
     * @return
     */
    public IdpPlayer getPlayer() {
        return plugin.getPlayer(player, true);
    }

    /**
     * Gets the ID of the player
     * @return
     */
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Gets the name of the player who issued the request (uncoloured)
     * @return
     */
    public IdpPlayer getRequester() {
        return plugin.getPlayer(requester, true);
    }

    /**
     * Gets the ID of the requester
     * @return
     */
    public UUID getRequesterId() {
        return requesterId;
    }

    /**
     * Starts a task to timeout the request.
     */
    private void startTimeoutTask(Long timeout) {
        taskid = plugin.getTaskManager().addTask(new RequestTask(timeout, plugin, playerId, requestid));
    }

    /** Returns the request id */
    public final Long getRequestid() {
        return requestid;
    }

    /**
     * Checks if this request is timed out
     * @return
     */
    public boolean isTimedOut(Long currentTime) {
        return currentTime - timeouttime > 0;
    }

    /**
     * Make this request timeout
     */
    public final void timeout() {
        getPlayer().getSession().removeRequest(requestid);
        onTimeout();
    }

    /**
     * Accept this request
     */
    public final void accept() {
        getPlayer().getSession().removeRequest(requestid);
        plugin.getTaskManager().removeTask(taskid);
        if (getRequester() == null) {
            return;
        }
        onAccept();
    }

    /**
     * Reject this request
     */
    public final void reject() {
        getPlayer().getSession().removeRequest(requestid);
        plugin.getTaskManager().removeTask(taskid);
        onReject();
    }

    /**
     * This event is called when the request is rejected by the player
     */
    protected abstract void onReject();

    /**
     * This event is called when the request is timed out
     */
    protected abstract void onTimeout();

    /**
     * This event is called when the request is accepted
     */
    protected abstract void onAccept();

    /**
     * A description about the request
     * Like: "Teleport request from Notch"
     * @return
     */
    public abstract String getDescription();

    /**
     * Makes an unique requestid
     * @return
     */
    private long generateRequestId() {
        // In theory this could create double values, only its highly unlikly
        // It would take about 14 hours for the same id to come again.
        // Also the chance it will be called on the exact same time is extrenly low.
        return Long.valueOf(String.valueOf(System.currentTimeMillis()).substring(5));
    }

}

class RequestTask extends LimitedTask {
    private long requestid;
    private InnPlugin plugin;
    private UUID playerId;

    public RequestTask(long delay, InnPlugin plugin, UUID playerId, long requestid) {
        super(RunBehaviour.ASYNC, delay, 1);
        this.playerId = playerId;
        this.plugin = plugin;
        this.requestid = requestid;
    }

    @Override
    public String getName() {
        return "Request task for #" + requestid;
    }

    public void run() {
        if (PlayerSession.hasSession(playerId)) {
            PlayerSession session = PlayerSession.getSession_(playerId);
            Request req = session.removeRequest(requestid);

            if (req != null) {
                req.onTimeout();
            }
        }
    }

}