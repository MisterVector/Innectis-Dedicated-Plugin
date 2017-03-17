package net.innectis.innplugin.system.game;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hret
 */
public class IdpStartResult {

    private boolean canStart;
    private List<String> errors;

    public IdpStartResult(boolean canStart) {
        this.canStart = canStart;
        this.errors = new ArrayList<String>(10);
    }

    public IdpStartResult(boolean canStart, String errorMessage) {
        this(canStart);
        errors.add(errorMessage);
    }

    /**
     * @return the canStart
     */
    public boolean canStart() {
        return canStart;
    }

    /**
     * @param canStart the canStart to set
     */
    public void setCanStart(boolean canStart) {
        this.canStart = canStart;
    }

    /**
     * Returns the list of errors
     */
    public List<String> getErrorMessage() {
        return errors;
    }

    /**
     * Adds an error to the result
     * @param error the error to add
     * @param canStart sets if the game can still be started
     */
    public void addError(String error, boolean canStart) {
        this.errors.add(error);
        this.canStart &= canStart;
    }

    /**
     * Removes all errors
     */
    public void clearErrors() {
        this.errors = new ArrayList<String>(10);
    }

}
