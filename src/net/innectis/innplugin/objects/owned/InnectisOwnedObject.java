package net.innectis.innplugin.objects.owned;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 *
 * @author Lynxy
 */
public abstract class InnectisOwnedObject extends IdpWorldRegion {

    /** Status of the object. Is true when a value has been changed */
    private boolean updated = false;
    /** The ID of the given object (0 means no owner, as DB begins at 1) */
    private int id;
    /** The credentials of the owner of this object */
    private PlayerCredentials ownerCredentials;
    /** List of members of this object */
    private List<PlayerCredentials> members;
    /** List of operators of this object */
    private List<PlayerCredentials> operators;
    /** Long containing all the flags (set bitwise) */
    private long flags;

    /** Creates a new InnectisOwnedObject */
    protected InnectisOwnedObject(World world, Vector pos1, Vector pos2, int id, PlayerCredentials ownerCredentials, List<PlayerCredentials> members, List<PlayerCredentials> operators, long flags) {
        super(world, pos1, pos2);
        this.id = id;
        this.ownerCredentials = ownerCredentials;
        this.members = (members == null ? new ArrayList<PlayerCredentials>() : members);
        this.operators = (operators == null ? new ArrayList<PlayerCredentials>() : operators);
        this.flags = flags;
    }

    /**
     * Returns true if the object has been changed since loading
     * @return boolean
     */
    public boolean getUpdated() {
        return updated;
    }

    /**
     * Sets the updated status of the object
     * @param updated
     */
    protected void setUpdated(boolean updated) {
        this.updated = updated;
    }

    /**
     * Returns the ID of the object
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the owned object
     * @param id
     */
    protected void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the credentials of the owner of this object
     * @return
     */
    public PlayerCredentials getOwnerCredentials() {
        return ownerCredentials;
    }

    /**
     * Returns the owner of the owned object
     * @return
     */
    public String getOwner() {
        return ownerCredentials.getName();
    }

    /**
     * Sets the owner credentials of this owned object
     * @param ownerCredentials
     */
    public void setOwner(PlayerCredentials ownerCredentials) {
        this.ownerCredentials = ownerCredentials;
        updated = true;
    }

    //<editor-fold defaultstate="collapsed" desc="Object members">
    /**
     * Checks if the target string can be allowed to this object.
     * @param check
     * @return
     */
    public boolean isValidPlayer(String check) {
        // Allow Wildcard and @ characters
        if (check.equals("%") || check.equals("@")) {
            return true;
        }

        String checkName = null;

        if (check.charAt(0) == '!') {
            checkName = check.substring(1);
        } else {
            checkName = check;
        }

        PlayerCredentials credentials = PlayerCredentialsManager.getByName(checkName);

        // Check if the player is valid.
        if (credentials != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Clears the members and operators list for this owned object
     */
    public void clearMembersAndOperators() {
        members.clear();
        operators.clear();
        setUpdated(true);
    }

    /**
     * Clears the flags for this owned object
     */
    public void clearFlags() {
        flags = 0;
        setUpdated(true);
    }

    /**
     * Clears the traits of this owned object
     */
    public void clearTraits() {
        clearMembersAndOperators();
        clearFlags();
    }

    /**
     * Returns a List of each member.
     * <br /><b>This list can't be modified!</b>
     */
    public List<PlayerCredentials> getMembers() {
        return Collections.unmodifiableList(members);
    }

    /**
     * Sets the members of this owned object
     * @param members
     */
    public void setMembers(List<PlayerCredentials> members) {
        this.members = members;
    }

    /**
     * Returns a List of each operator.
     * <br /><b>This list can't be modified!</b>
     */
    public List<PlayerCredentials> getOperators() {
        return Collections.unmodifiableList(operators);
    }

    /**
     * Sets the operators of this owned object
     * @param operators
     */
    public void setOperators(List<PlayerCredentials> operators) {
        this.operators = operators;
    }

    /**
     * Returns a colorized String of all the members seperated by commas. If there are no members, returns "none"
     */
    public String getMembersString(ChatColor userColor, ChatColor opColor, ChatColor customGroupColor, ChatColor groupColor) {
        StringBuilder sb = new StringBuilder();
        String groupName;

        // First print operators!
        for (PlayerCredentials pc : operators) {
            String name = pc.getName();

            if (userColor != null) {
                sb.append(opColor);
            }
            sb.append(name).append(", ");
        }

        // Print members
        for (PlayerCredentials pc : members) {
            String name = pc.getName();

            if (userColor != null) {
                sb.append(userColor);
            }
            sb.append(name).append(", ");
        }

        if (sb.length() == 0) {
            return "none";
        }
        return sb.substring(0, sb.length() - 2);
    }

    /**
     * Adds the credentials of the given player as member of this object.
     * If the given name is already a member or operator, this method will return false;
     * @param credentials
     */
    public boolean addMember(PlayerCredentials credentials) {
        if (containsMember(credentials.getName()) || containsOperator(credentials.getName())) {
            return false;
        }

        members.add(credentials);
        updated = true;
        return true;
    }

    /**
     * Removes the specified player from this owned object.
     * If member didn't exist in first place, returns false
     * @param playerName
     */
    public boolean removeMember(String playerName) {
        boolean removed = false;

        for (Iterator<PlayerCredentials> it = members.iterator(); it.hasNext();) {
            PlayerCredentials pc = it.next();

            if (pc.getName().equalsIgnoreCase(playerName)) {
                it.remove();
                removed = true;
                break;
            }

        }

        if (removed) {
            updated = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds the credentials of the specified player as an operator
     * if this is a member already added, it will be removed from
     * the member list and added to the operator list.
     * @param credentials
     */
    public boolean addOperator(PlayerCredentials credentials) {
        if (containsOperator(credentials.getName())) {
            return false;
        }

        // Remove from members, if given name is a member
        if (containsMember(credentials.getName())) {
            members.remove(credentials);
        }

        operators.add(credentials);
        updated = true;
        return true;
    }

    /**
     * Removes the given player from this object.
     * If operator didn't exist in first place, returns false
     * @param playerName
     */
    public boolean removeOperator(String playerName) {
        boolean removed = false;

        for (Iterator<PlayerCredentials> it = operators.iterator(); it.hasNext();) {
            PlayerCredentials pc = it.next();

            if (pc.getName().equalsIgnoreCase(playerName)) {
                it.remove();
                removed = true;
                break;
            }
        }

        if (removed) {
            updated = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the player is a valid member
     * @param playerName
     * @return
     */
    public boolean containsMember(String playerName) {
        for (PlayerCredentials pc : members) {
            if (pc.getName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the player is a valid operator
     * @param playerName
     * @return
     */
    public boolean containsOperator(String playerName) {
        for (PlayerCredentials pc : operators) {
            if (pc.getName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }
    //</editor-fold>

    /**
     * Returns true if the player represented by the ID is the owner
     * or member, the object has % as a special member, the player is
     * an operator, or whose player group is part of this lot
     * on this object
     * @param playerName
     */
    public boolean canPlayerAccess(String playerName) {
        if (ownerCredentials.getName().equalsIgnoreCase(playerName)) {
            return true;
        }

        if (containsMember("%") || containsMember(playerName) || containsOperator(playerName)) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the player player can manage this object
     * @param playerName
     * @return
     */
    public boolean canPlayerManage(String playerName) {
        return ownerCredentials.getName().equalsIgnoreCase(playerName) || containsOperator(playerName);
    }

    /**
     * Returns true if the player represented by the ID is the owner,
     * contains operator, a member or is part of a member group on
     * this object.
     * <p/>
     * This ignores the generic lotmember '%'
     * @param playerName
     */
    public boolean canPlayerAccessIgnoreGeneric(String playerName) {
        if (ownerCredentials.getName().equalsIgnoreCase(playerName) || containsMember(playerName) || containsOperator(playerName)) {
            return true;
        }

        return false;
    }

    //<editor-fold defaultstate="collapsed" desc="Object Flags">
    /**
     * Returns the class of the flagenum
     * @return
     */
    protected abstract Class<? extends FlagType> getEnumClass();

    /**
     * Returns if this owned object has any flags that can be set
     * @return
     */
    public boolean hasFlags() {
        return (getEnumClass() != null);
    }

    /**
     * Returns the possible flagtypes on this owned object.
     * If there are no flags an empty array will be returned.
     * @return Array with flagtypes
     */
    public FlagType[] getFlagTypes() {
        Class<? extends FlagType> flagenum = getEnumClass();
        // Return 'none' when no class given
        if (flagenum == null) {
            return new FlagType[0];
        }

        // Extra check if the class is an ENUM
        if (!flagenum.isEnum()) {
            throw new ClassFormatError("The given class is not an enum!");
        }

        return flagenum.getEnumConstants();
    }

    /**
     * Constructs a string containing all the flags on the given lot.
     *
     * Given a class that extends this one, returns a colored list of all flags available to that class, separated by a space. If there are no flags, returns "none".
     * If this object contains the flag itself (i.e. not through inheritance) it will be colored with 'thisObjectColor'. If both colors are null, will not color the text at all.
     * @param cls
     * @param defaultColor
     * @param thisObjectColor
     */
    public String getFlagsString(ChatColor inheritedColor, ChatColor thisObjectColor) {
        Class<? extends FlagType> flagenum = getEnumClass();
        // Return 'none' when no class given
        if (flagenum == null) {
            return "none";
        }

        // Extra check if the class is an ENUM
        if (!flagenum.isEnum()) {
            throw new ClassFormatError("The given class is not an enum!");
        }

        StringBuilder sb = new StringBuilder();
        for (FlagType type : flagenum.getEnumConstants()) {
            if (isFlagSet(type)) {
                if (isFlagSetNoInheritance(type)) {
                    //it is set on this very object
                    if (thisObjectColor != null) {
                        sb.append(thisObjectColor);
                    }
                    sb.append(type.getFlagName()).append(" ");
                } else {
                    //must be set through inheritance
                    if (inheritedColor != null) {
                        sb.append(inheritedColor);
                    }
                    sb.append(type.getFlagName()).append(" ");
                }
            }

        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        }
        return "none";
    }

    /**
     * Returns the flagtype from the given string
     *
     * Given a class that extends this one, returns a FlagType that matches the flag name passed in. If no matches, returns null
     * @param cls
     * @param flagStr
     */
    public FlagType getFlagsFromString(String flagStr) {
        Class<? extends FlagType> flagenum = getEnumClass();
        // Return 'none' when no class given
        if (flagenum == null) {
            return null;
        }

        if (!flagenum.isEnum()) {
            throw new ClassFormatError("The given class is not an enum!");
        }

        for (FlagType type : flagenum.getEnumConstants()) {
            if (flagStr.equalsIgnoreCase(type.getFlagName())) {
                return type;
            }
        }
        return null;
    }

    /**
     * Returns true if this object has a flag bit set
     * @param flag
     */
    public boolean isFlagSet(FlagType flag) {
        return isFlagSetNoInheritance(flag);
    }

    /**
     * Returns true if this object has a flag bit set, without looking at parent objects (if overridden class supports it)
     * @param flag
     */
    public boolean isFlagSetNoInheritance(FlagType flag) {
        return ((flags & flag.getFlagBit()) == flag.getFlagBit());
    }

    /**
     * Returns all flags set on this object
     */
    public long getFlags() {
        return flags;
    }

    /**
     * Sets the flags on this object. Note: this does not turn a flag on or off! Use setFlag() for that
     */
    public void setFlags(long flags) {
        flags = flags;
        updated = true;
    }

    /**
     * Turns a flag on or off
     * @param flag
     * @param disable
     */
    public void setFlag(FlagType flag, boolean disable) {
        setFlag(flag.getFlagBit(), disable);
    }

    /**
     * Turns a flag on or off
     * @param flag
     * @param disable
     */
    public void setFlag(long flag, boolean disable) {
        if (disable) {
            flags &= ~flag;
        } else {
            flags |= flag;
        }
        updated = true;
    }
    //</editor-fold>

    @Override
    public void setPos1(Vector point) {
        super.setPos1(point);
        updated = true;
    }

    @Override
    public void setPos2(Vector point) {
        super.setPos2(point);
        updated = true;
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        updated = true;
    }

    public abstract boolean save();

    public abstract boolean isAtLocation(Location location);

    public abstract OwnedObjectType getType();

}
