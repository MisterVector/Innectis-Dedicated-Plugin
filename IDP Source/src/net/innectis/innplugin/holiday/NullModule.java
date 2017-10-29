package net.innectis.innplugin.holiday;

/**
 *
 * @author Hret
 *
 * Module that does nothing.
 */
class NullModule extends HolidayModule {

    public NullModule() {
        super(HolidayType.NULL);
    }

    @Override
    public boolean isValid() {
        return false;
    }

}
