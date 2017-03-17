package net.innectis.innplugin.holiday;

import net.innectis.innplugin.tasks.RepeatingTask;
import net.innectis.innplugin.tasks.RunBehaviour;

/**
 *
 * @author Hret
 *
 * Task to check for holidays modules that need to be loaded/unloaded
 */
class HolidayTask extends RepeatingTask {

    private final HolidayModuleSelector selector;

    public HolidayTask(HolidayModuleSelector selector, long delay) {
        super(RunBehaviour.ASYNC, delay);
        this.selector = selector;
    }

    public String getName() {
        return "Holidays checker.";
    }

    public void run() {
        selector.lookupModule();
    }

}
