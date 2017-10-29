package net.innectis.innplugin.holiday;

/**
 *
 * @author Hret
 *
 * Interface for the manager that looks if an holiday has started.
 */
interface HolidayModuleSelector {

    /**
     * This method will check if a new module should be loaded or an old one unloaded.
     */
    void lookupModule();

}
