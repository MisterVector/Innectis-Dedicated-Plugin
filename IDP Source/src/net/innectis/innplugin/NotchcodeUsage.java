package net.innectis.innplugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Hret
 * <p />
 * This annotation marks the usage of decompiled MC code.
 * Also known as Notchcode.
 * <p />
 * Due to it being decompiled and obfrusticated it might change
 * after an MC update. It is therefor important to mark the usage of these
 * files.
 * <p />
 * Some methods that hasn't been renamed by bukkit might also benifit from a
 * version number which says when it was last checked (and confirmed working).
 */
@Retention(RetentionPolicy.SOURCE)
public @interface NotchcodeUsage {

    /**
     * This variable should be used to check if the code is based upon reflection.
     * As this isn't checked by the compiler this might cause errors.
     */
    boolean usesReflection() default false;

    /**
     * The version when this was last checked and confirmed it worked.
     * Like '<b>1.3.1</b>'.
     * @return
     */
    String mcversion() default "";
    
}
