package net.innectis.innplugin.system.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.player.Permission;

/**
 * @author Hret
 *
 * This annotation is used to mark a method inside a class as an method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CommandMethod {

    /**
     * A list of aliases for the command. The first alias is the most
     * important -- it is the main name of the command. (The method name
     * is never used for anything).
     */
    String[] aliases();

    /**
     * Description what this command does.
     */
    String description();

    /**
     * Short description how to use it.
     */
    String usage();

    /** Usage info for GUEST rank, if null the usage default will be used */
    String usage_Guest() default "";

    /** Usage info for User rank, if null the Guest desc (or lower) will be used */
    String usage_User() default "";

    /** Usage info for VIP rank, if null the User desc (or lower) will be used */
    String usage_VIP() default "";

    /** Usage info for Super_VIP rank, if null the VIP desc (or lower) will be used */
    String usage_Super_VIP() default "";

    /** Usage info for Goldy rank, if null the Super_VIP desc (or lower) will be used */
    String usage_Goldy() default "";

    /** Usage info for Mod rank, if null the Goldy desc (or lower) will be used */
    String usage_Mod() default "";

    /** Usage info for Rainbow_Mod rank, if null the Mod desc (or lower) will be used */
    String usage_Rainbow_Mod() default "";

    /** Usage info for Admin rank, if null the Rainbow_Mod desc (or lower) will be used */
    String usage_Admin() default "";

    /** Usage info for SADMIN rank, if null the ADMIN desc (or lower) will be used */
    String usage_SAdmin() default "";

    /**
     * The permission node the user has to have
     */
    Permission permission();

    /**
     * The worlds where this command is disabled
     */
    IdpWorldType[] disabledWorlds() default IdpWorldType.NONE;

    /**
     * True if this command can also be used by the server
     */
    boolean serverCommand();

    /**
     * if true, players who dont have access will see this command as not existing
     */
    boolean hiddenCommand() default false;

    /**
     * If true this command will not be used in the generation of the
     * command documentations
     */
    boolean hideinlists() default false;

    /**
     * If true this command can be used before the player is logged in.
     */
    boolean preLoginCommand() default false;

    /**
     * If true the logging of this command hides the arguments given.
     * <b>Use this only on commands which contain private information!</b>
     */
    boolean obfusticateLogging() default false;

}
