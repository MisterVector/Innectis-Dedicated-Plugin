package net.innectis.innplugin.objects;

import net.innectis.innplugin.player.PlayerGroup;
import org.bukkit.Sound;

/**
 * An enum listing all chat events and their sounds
 *
 * @author AlphaBlend
 */
public enum ChatSoundSetting {

    GLOBAL_AND_EMOTE(1, "Global and Emote", PlayerGroup.GUEST, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f, "globalandemote", "ge"),
    CHANNEL_CHAT(2, "Channel Chat", PlayerGroup.GUEST, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f, "channelchat", "cc"),
    WHISPER_CHAT(3, "Whisper Chat", PlayerGroup.GUEST, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f, "whisperchat", "wc"),
    LOT_AND_LOCAL_CHAT(4, "Lot and Local Chat", PlayerGroup.GUEST, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2f, "lotlocalchat", "llc"),
    STAFF_CHAT(5, "Staff Chat", PlayerGroup.MODERATOR, Sound.ENTITY_PLAYER_BREATH, 1f, 0.8f, "staffchat", "sc");

    private final long chatBit;
    private final String description;
    private final PlayerGroup minGroup;
    private final Sound bukkitSound;
    private final float volume;
    private final float pitch;
    private String[] names = null;

    private ChatSoundSetting(long chatBit, String description, PlayerGroup minGroup, Sound bukkitSound, float volume, float pitch, String... names) {
        this.chatBit = (long) Math.pow(2L, chatBit - 1);
        this.description = description;
        this.minGroup = minGroup;
        this.bukkitSound = bukkitSound;
        this.volume = volume;
        this.pitch = pitch;
        this.names = names;
    }

    /**
     * Gets the chat bit used with this chat sound setting
     * @return
     */
    public long getChatBit() {
        return chatBit;
    }

    /**
     * Gets the description of this chat group
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the minimum group required to use this chat sound group
     * @return
     */
    public PlayerGroup getMinGroup() {
        return minGroup;
    }

    /**
     * Returns the bukkit sound for this chat sound setting
     * @return
     */
    public Sound getBukkitSound() {
        return bukkitSound;
    }

    /**
     * Returns the volume of the sound for this chat sound setting
     * @return
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Returns the pitch of the sound for this chat sound setting
     * @return
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Gets all the names that describe this chat sound group
     * @return
     */
    public String[] getNames() {
        return names;
    }

    /**
     * Gets a chat sound group from the specified name
     * @param name
     * @return
     */
    public static ChatSoundSetting byName(String name) {
        for (ChatSoundSetting csg : values()) {
            for (String groupName : csg.getNames()) {
                if (groupName.equalsIgnoreCase(name)) {
                    return csg;
                }
            }
        }

        return null;
    }

}
