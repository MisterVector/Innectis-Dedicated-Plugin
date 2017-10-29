package net.innectis.innplugin.player;

import java.util.HashMap;
import org.bukkit.ChatColor;

/**
 *
 * @author Hret
 *
 * List with permissions that are in the IDP
 */
public enum Permission {

    // <editor-fold defaultstate="collapsed" desc="World">
    /** Allows the player to build in pixel world */
    world_build_pixelarea(1001, PlayerGroup.SUPER_VIP),
    /** Allows the player to build in the Nether */
    world_build_nether(1002, PlayerGroup.USER),
    /** Allows the player to build in the Aether */
    world_build_aether(1003, PlayerGroup.ADMIN),
    /** Allows the player to build outside the lots in the normal world */
    world_build_wilderness(1004, PlayerGroup.VIP),
    /** Allows the player to build everywhere, even in lots */
    world_build_unrestricted(1005, PlayerGroup.ADMIN),
    /** Makes the user able to use all commands in any world, even when they are disabled in that world */
    world_command_override(1006, PlayerGroup.ADMIN),
    /** Allows the player to build in The End */
    world_build_theend(1007, PlayerGroup.USER),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Build">
    build_noquota(2002, PlayerGroup.MODERATOR),
    build_block_fire(2007, PlayerGroup.ADMIN),
    build_lava_near_players(2008, PlayerGroup.MODERATOR),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Entity">
    // Next ID: 3023
    entity_controlotherpets(3001, PlayerGroup.MODERATOR),
    entity_sitanycreature(3002, PlayerGroup.GUEST),
    /** Prevents losing items on death */
    entity_deathitems(3003, PlayerGroup.MODERATOR),
    entity_canfeedanywhere(3004, PlayerGroup.ADMIN),
    entity_canshearanywhere(3005, PlayerGroup.ADMIN),
    entity_cankillanywhere(3006, PlayerGroup.ADMIN),
    entity_canleadanywhere(3021, PlayerGroup.ADMIN),
    entity_cannameanywhere(3022, PlayerGroup.ADMIN),
    /** Prevents losing items in backpack on death */
    entity_deathitemsbackpack(3007, PlayerGroup.MODERATOR),
    entity_catchentitiesoverride(3008, PlayerGroup.ADMIN),
    entity_transfercaughtentities(3009, PlayerGroup.GUEST),
    entity_sitplayer(3010, PlayerGroup.MODERATOR),
    entity_mount_manipulate_player(3012, PlayerGroup.MODERATOR),
    entity_recolourallsheeps(3013, PlayerGroup.ADMIN),
    entity_vehicle_damage_override(3014, PlayerGroup.ADMIN),
    entity_sitambient(3015, PlayerGroup.MODERATOR),
    entity_changeendermanblock(3016, PlayerGroup.ADMIN),
    entity_minecartsuckallitems(3017, PlayerGroup.ADMIN),
    entity_catchinfiniteentities(3018, PlayerGroup.ADMIN),
    entity_catchentitiesall(3019, PlayerGroup.ADMIN),
    entity_destroytamedanimal(3020, PlayerGroup.ADMIN),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Spoofing">
    spoofing_listspoofed(4001, PlayerGroup.ADMIN),
    spoofing_see_hidden(4002, PlayerGroup.MODERATOR),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Lot">
    lot_ignoreflag_itempickup(5001, PlayerGroup.ADMIN),
    lot_ignoreflag_itemdrop(5002, PlayerGroup.ADMIN),
    lot_ignoreflag_farm(5003, PlayerGroup.ADMIN),
    lot_sethome_override(5004, PlayerGroup.ADMIN),
    lot_command_override(5005, PlayerGroup.ADMIN),
    lot_changeanyspawn(5006, PlayerGroup.MODERATOR),
    lot_extendedinfo(5007, PlayerGroup.MODERATOR),
    lot_ban_override(5008, PlayerGroup.ADMIN),
    lot_memberlot_flagset(5009, PlayerGroup.MODERATOR),
    lot_ignoreflag_nomemberlogoutspawn(5010, PlayerGroup.MODERATOR),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Cheats">
    cheats_creativemode(6001, PlayerGroup.ADMIN),
    cheats_flymod(6002, PlayerGroup.ADMIN),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Special">
    // Next id is 7049
    special_builder_miningstick(7001, PlayerGroup.DIAMOND),
    special_blazerod_lightning(7009, PlayerGroup.ADMIN),
    special_sign_colour_lvl1(7002, PlayerGroup.VIP),
    special_sign_colour_lvl2(7003, PlayerGroup.SUPER_VIP),
    special_sign_colour_lvl3(7004, PlayerGroup.GOLDY),
    special_sign_colour_effects(7025, PlayerGroup.VIP),
    special_sign_colour_any(7030, PlayerGroup.ADMIN),
    special_edit_any_sign(7008, PlayerGroup.ADMIN),
    special_lightsource_activate(7006, PlayerGroup.SUPER_VIP),
    special_signcopy(7010, PlayerGroup.ADMIN),
    special_signedit(7011, PlayerGroup.ADMIN),
    special_feather_knockback(7012, PlayerGroup.ADMIN),
    special_chest_stashall(7013, PlayerGroup.ADMIN),
    special_stick_kickbatonall(7014, PlayerGroup.MODERATOR),
    special_ctf_createarenaanywhere(7016, PlayerGroup.ADMIN),
    special_external_commands(7017, PlayerGroup.ADMIN),
    special_enderchest_override(7018, PlayerGroup.ADMIN),
    special_usebackpack(7019, PlayerGroup.VIP),
    special_modifyban_otherowner(7020, PlayerGroup.RAINBOW_MODERATOR),
    special_lightsource_anywhere(7021, PlayerGroup.MODERATOR),
    special_enchantment_wearanywhere(7022, PlayerGroup.ADMIN),
    special_weapons_invuse(7023, PlayerGroup.SADMIN),
    special_waypoint_nodeduct(7024, PlayerGroup.ADMIN),
    special_chestshop_override(7027, PlayerGroup.ADMIN),
    special_chatroom_override(7028, PlayerGroup.MODERATOR),
    special_pixelworld_extraitems(7029, PlayerGroup.GOLDY),
    special_manipulate_any_inventory(7031, PlayerGroup.ADMIN),
    special_has_flight(7032, PlayerGroup.MODERATOR),
    special_backpack_override(7033, PlayerGroup.ADMIN),
    special_nether_tp_no_cost(7034, PlayerGroup.DIAMOND),
    special_respawn_lot(7035, PlayerGroup.SUPER_VIP),
    special_noflight_override(7036, PlayerGroup.MODERATOR),
    special_staffrequest_viewall(7037, PlayerGroup.MODERATOR),
    special_valutas_nodrop(7038, PlayerGroup.ADMIN),
    special_nether_tp_override(7039, PlayerGroup.ADMIN),
    special_valuta_sinklog_exempt(7041, PlayerGroup.ADMIN),
    special_damage_state_bypass(7042, PlayerGroup.ADMIN),
    special_ignore_creative_world_mode(7043, PlayerGroup.ADMIN),
    special_mobs_explodeall(7044, PlayerGroup.MODERATOR),
    special_view_private_tags(7045, PlayerGroup.MODERATOR),
    special_god_allow(7046, PlayerGroup.ADMIN),
    special_the_end_exempt_teleport(7047, PlayerGroup.MODERATOR),
    special_setshopitem_noconsume(7048, PlayerGroup.ADMIN),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Owned Blocks">
    owned_object_override(8001, PlayerGroup.ADMIN),
    owned_waypoint_setanywhere(8002, PlayerGroup.ADMIN),
    owned_waypoint_setall(8003, PlayerGroup.ADMIN),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Chat">
    chat_hearmuted(9001, PlayerGroup.MODERATOR),
    chat_usecolours(9002, PlayerGroup.MODERATOR),
    chat_exclaim(9003, PlayerGroup.MODERATOR),
    chat_ignoreuser(9004, PlayerGroup.GUEST),
    chat_unignoreuser(9005, PlayerGroup.GUEST),
    chat_ignoredusers(9006, PlayerGroup.GUEST),
    chat_ignore_overide(9007, PlayerGroup.ADMIN),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Admin">
    admin_blocklock(10001, PlayerGroup.ADMIN),
    admin_blockunlock(10002, PlayerGroup.ADMIN),
    admin_adminmessage(10003, PlayerGroup.ADMIN),
    admin_modmessage(10004, PlayerGroup.MODERATOR),
    admin_informationtool(10005, PlayerGroup.MODERATOR),
    admin_setanyflag(10006, PlayerGroup.ADMIN),
    admin_modifymultipleipbans(10007, PlayerGroup.ADMIN),
    admin_chestinformation(10008, PlayerGroup.MODERATOR),
    admin_blockinformation(10009, PlayerGroup.MODERATOR),
    admin_blockvalueinformation(10010, PlayerGroup.ADMIN),
    admin_serveroperator(10011, PlayerGroup.SADMIN),
    admin_serverrestart(10019, PlayerGroup.ADMIN),
    admin_tool(10014, PlayerGroup.MODERATOR),
    admin_tool_blocks(10015, PlayerGroup.MODERATOR),
    admin_tool_entities(10016, PlayerGroup.MODERATOR),
    admin_block_templock_override(10017, PlayerGroup.ADMIN),
    admin_linkanyswitch(10018, PlayerGroup.ADMIN),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Items">
    items_clock_teleport(11004, PlayerGroup.GUEST),
    items_clock_global_teleport(11005, PlayerGroup.MODERATOR),
    items_enderpearl_teleport_override(11006, PlayerGroup.ADMIN),
    items_clock_allow_managed_lots(11007, PlayerGroup.GOLDY),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="TinyWe">
    tinywe_selection_unlimited(12002, PlayerGroup.ADMIN),
    tinywe_selection_setpointsanywhere(12003, PlayerGroup.VIP),
    tinywe_override_useanywhere(12004, PlayerGroup.ADMIN),
    tinywe_override_noconsumption(12005, PlayerGroup.ADMIN),
    //tinywe_override_ignoreairblocks(1207, PlayerGroup.SUPER_VIP),
    tinywe_wand(12008, PlayerGroup.VIP),
    tinywe_override_useanyblock(12010, PlayerGroup.ADMIN),
    tinywe_returnwaterlavablocks(12011, PlayerGroup.SUPER_VIP),
    tinywe_override_biomeworld(12012, PlayerGroup.SADMIN),
    tinywe_place_water(12013, PlayerGroup.SUPER_VIP),
    tinywe_regen_anywhere(12014, PlayerGroup.ADMIN),
    tinywe_natural_world_use(12015, PlayerGroup.SUPER_VIP),
    tinywe_clipboard_creative_override(12016, PlayerGroup.ADMIN),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Warp">
    warp_show_private(13001, PlayerGroup.MODERATOR),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Teleport">
    teleport_instant(14001, PlayerGroup.MODERATOR),
    teleport_coords(14002, PlayerGroup.MODERATOR),
    teleport_tpr_cheaper(14003, PlayerGroup.SUPER_VIP),
    teleport_tpr_free(14005, PlayerGroup.GOLDY),
    teleport_force(14004, PlayerGroup.MODERATOR),
    teleport_invisible(14006, PlayerGroup.MODERATOR),
    teleport_unrestricted(14007, PlayerGroup.ADMIN),
    teleport_others_force(14008, PlayerGroup.ADMIN),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Commands">
    // next id is 15331
    command_shop_additem(15203, PlayerGroup.ADMIN),
    command_shop_removeitem(15204, PlayerGroup.ADMIN),
    command_shop_setamount(15206, PlayerGroup.ADMIN),
    command_shop_signshop(15258, PlayerGroup.VIP),
    command_shop_personalshop(15263, PlayerGroup.USER),
    command_staffrequest_special(15232, PlayerGroup.MODERATOR),
    command_admin_fillcontainer(15116, PlayerGroup.ADMIN),
    command_admin_modifybalance(15179, PlayerGroup.ADMIN),
    command_admin_cleanup(15003, PlayerGroup.SADMIN),
    command_admin_garbagecollection(15005, PlayerGroup.ADMIN),
    command_admin_lock(15006, PlayerGroup.ADMIN),
    command_admin_nightspeed(15007, PlayerGroup.ADMIN),
    command_admin_nightupdate(15008, PlayerGroup.ADMIN),
    command_admin_reloadfile(15009, PlayerGroup.SADMIN),
    command_admin_openchest(15010, PlayerGroup.ADMIN),
    command_admin_open_any_container(15151, PlayerGroup.ADMIN),
    command_admin_setgroup(15011, PlayerGroup.SADMIN),
    command_admin_idpsetvalue(15207, PlayerGroup.SADMIN),
    command_admin_unlock(15013, PlayerGroup.ADMIN),
    command_admin_invswitch(15015, PlayerGroup.ADMIN),
    command_admin_knockback(15161, PlayerGroup.ADMIN),
    command_admin_playerinfo(15164, PlayerGroup.ADMIN),
    command_admin_infotool(15198, PlayerGroup.MODERATOR),
    command_admin_openenderchest(15222, PlayerGroup.ADMIN),
    command_admin_perm(15224, PlayerGroup.SADMIN),
    command_admin_addworld(15227, PlayerGroup.SADMIN),
    command_admin_createpicture(15254, PlayerGroup.SADMIN),
    command_admin_delworld(15226, PlayerGroup.SADMIN),
    command_admin_vanish(15250, PlayerGroup.MODERATOR),
    command_admin_unloadworldchunks(15261, PlayerGroup.ADMIN),
    command_admin_utility(15277, PlayerGroup.ADMIN),
    command_admin_maintenance(15293, PlayerGroup.ADMIN),
    command_admin_setimage(15299, PlayerGroup.ADMIN),
    command_admin_spectate_kickall(15302, PlayerGroup.ADMIN),
    command_admin_setfloatingmessage(15303, PlayerGroup.ADMIN),
    command_admin_playerpasswords(15317, PlayerGroup.ADMIN),
    command_admin_setguestpromotearea(15328, PlayerGroup.ADMIN),
    command_information_cmdinfo(15016, PlayerGroup.GUEST),
    command_information_godlist(15017, PlayerGroup.ADMIN),
    command_information_help(15018, PlayerGroup.GUEST),
    command_information_id(15019, PlayerGroup.GUEST),
    command_information_version(15020, PlayerGroup.GUEST),
    command_information_version_extended(15247, PlayerGroup.SADMIN),
    command_information_plugins(15021, PlayerGroup.ADMIN),
    command_information_itemid(15233, PlayerGroup.GUEST),
    command_information_jaillist(15023, PlayerGroup.MODERATOR),
    command_information_ping(15665, PlayerGroup.GUEST),
    command_information_ping_other(15666, PlayerGroup.MODERATOR),
    command_information_serverinfo(15024, PlayerGroup.GUEST),
    command_information_serverinfo_extended(15193, PlayerGroup.ADMIN),
    command_information_statistics(15218, PlayerGroup.GUEST),
    command_information_statistics_extended(15219, PlayerGroup.MODERATOR),
    command_information_mutelist(15025, PlayerGroup.MODERATOR),
    command_information_points(15026, PlayerGroup.GUEST),
    command_information_who(15027, PlayerGroup.GUEST),
    command_information_lastseen(15028, PlayerGroup.GUEST),
    command_information_world(15029, PlayerGroup.GUEST),
    command_information_countitem(15270, PlayerGroup.GUEST),
    command_information_vtsink(15297, PlayerGroup.GUEST),
    command_information_commandlist(15309, PlayerGroup.GUEST),
    command_cheat_getwool(15030, PlayerGroup.ADMIN),
    command_cheat_equip(15031, PlayerGroup.ADMIN),
    command_cheat_enchant(15215, PlayerGroup.ADMIN),
    command_cheat_give(15032, PlayerGroup.ADMIN),
    command_cheat_god(15033, PlayerGroup.ADMIN),
    command_cheat_fly(15228, PlayerGroup.MODERATOR),
    command_cheat_fly_others(15229, PlayerGroup.ADMIN),
    command_cheat_heal(15034, PlayerGroup.GUEST),
    command_cheat_heal_free(15313, PlayerGroup.ADMIN),
    command_cheat_item(15035, PlayerGroup.MODERATOR),
    command_cheat_item_anywhere(15235, PlayerGroup.ADMIN),
    command_cheat_itemdata(15246, PlayerGroup.ADMIN),
    command_cheat_mcboost(15036, PlayerGroup.GUEST),
    command_cheat_spawnmob(15037, PlayerGroup.ADMIN),
    command_cheat_strike(15038, PlayerGroup.ADMIN),
    command_cheat_time(15039, PlayerGroup.ADMIN),
    command_cheat_weather(15040, PlayerGroup.MODERATOR),
    command_cheat_gamemode(15041, PlayerGroup.ADMIN),
    command_cheat_addeffect(15281, PlayerGroup.SADMIN),
    command_cheat_speed(15274, PlayerGroup.ADMIN),
    command_cheat_platform(15290, PlayerGroup.MODERATOR),
    command_cheat_platform_noconsume(15305, PlayerGroup.ADMIN),
    command_game_game(15242, PlayerGroup.MODERATOR),
    command_game_score(15244, PlayerGroup.USER),
    command_game_timer(15259, PlayerGroup.MODERATOR),
    command_game_timer_public(15260, PlayerGroup.MODERATOR),
    command_location_listhomes(15197, PlayerGroup.VIP),
    command_location_addwarp(15042, PlayerGroup.RAINBOW_MODERATOR),
    command_location_compass(15043, PlayerGroup.GUEST),
    command_location_delwarp(15044, PlayerGroup.RAINBOW_MODERATOR),
    command_location_forcewarp(15045, PlayerGroup.MODERATOR),
    command_location_home(15046, PlayerGroup.VIP),
    command_location_locate(15047, PlayerGroup.USER),
    command_location_sethome(15048, PlayerGroup.VIP),
    command_location_deletehome(15255, PlayerGroup.VIP),
    command_location_setspawn(15049, PlayerGroup.ADMIN),
    command_location_spawn(15050, PlayerGroup.GUEST),
    command_location_top(15266, PlayerGroup.MODERATOR),
    command_location_tpp(15051, PlayerGroup.MODERATOR),
    command_location_warp(15052, PlayerGroup.GUEST),
    command_location_addwaypoint(15053, PlayerGroup.USER),
    command_location_setwaypoint(15054, PlayerGroup.USER),
    command_location_teleport(15055, PlayerGroup.SUPER_VIP),
    command_location_teleportrequest(15056, PlayerGroup.USER),
    command_location_teleportall(15275, PlayerGroup.ADMIN),
    command_location_teleportback(15162, PlayerGroup.USER),
    command_location_setflag(15057, PlayerGroup.USER),
    command_location_removecaughtanimals(15147, PlayerGroup.USER),
    command_location_massaccess(15286, PlayerGroup.SUPER_VIP),
    command_location_massowner(15287, PlayerGroup.RAINBOW_MODERATOR),
    command_location_setendsign(15298, PlayerGroup.ADMIN),
    command_location_settag(15304, PlayerGroup.MODERATOR),
    command_location_settag_setowner(15306, PlayerGroup.ADMIN),
    command_lot_addlotarea(15058, PlayerGroup.SADMIN),
    command_lot_inspectmembers(15279, PlayerGroup.MODERATOR),
    command_lot_addlot(15059, PlayerGroup.MODERATOR),
    command_lot_getlot(15060, PlayerGroup.GUEST),
    command_lot_assignlot(15061, PlayerGroup.MODERATOR),
    command_lot_changelotspawn(15062, PlayerGroup.USER),
    command_lot_gotolot(15063, PlayerGroup.GUEST),
    command_lot_listlots(15064, PlayerGroup.MODERATOR),
    command_lot_mylots(15065, PlayerGroup.GUEST),
    command_lot_lotban(15067, PlayerGroup.GUEST),
    command_lot_lotkick(15150, PlayerGroup.GUEST),
    command_lot_lotcenter(15068, PlayerGroup.GUEST),
    command_lot_lotcenter_override(15322, PlayerGroup.MODERATOR),
    command_lot_lotinfo(15070, PlayerGroup.GUEST),
    command_lot_lotstack(15071, PlayerGroup.MODERATOR),
    command_lot_wholot(15201, PlayerGroup.GUEST),
    command_lot_lotunban(15072, PlayerGroup.GUEST),
    command_lot_mylot(15073, PlayerGroup.GUEST),
    command_lot_randomlot(15208, PlayerGroup.MODERATOR),
    command_lot_reloadlots(15074, PlayerGroup.ADMIN),
    command_lot_remlot(15075, PlayerGroup.MODERATOR),
    command_lot_resetlot(15076, PlayerGroup.MODERATOR),
    command_lot_savelots(15077, PlayerGroup.ADMIN),
    command_lot_select(15078, PlayerGroup.MODERATOR),
    command_lot_setleavemsg(15079, PlayerGroup.USER),
    command_lot_setlotnumber(15080, PlayerGroup.GUEST),
    command_lot_setlotnumber_any(15316, PlayerGroup.MODERATOR),
    command_location_setowner(15081, PlayerGroup.RAINBOW_MODERATOR),
    command_lot_setentermsg(15082, PlayerGroup.USER),
    command_lot_setlotname(15083, PlayerGroup.GUEST),
    command_lot_thislot(15084, PlayerGroup.GUEST),
    command_thislot_showhidden(15307, PlayerGroup.MODERATOR),
    command_lot_thatlot(15180, PlayerGroup.GUEST),
    command_thatlot_showhidden(15308, PlayerGroup.MODERATOR),
    command_lot_lotsafeadd(15288, PlayerGroup.USER),
    command_lot_lotsafedel(15289, PlayerGroup.USER),
    command_lot_borderlot(15177, PlayerGroup.MODERATOR),
    command_lot_lot(15202, PlayerGroup.ADMIN),
    command_lot_putlothere(15211, PlayerGroup.ADMIN),
    command_lot_chagelotheight(15225, PlayerGroup.RAINBOW_MODERATOR),
    command_lot_createctfarena(15291, PlayerGroup.MODERATOR),
    command_lot_respawnlot(15294, PlayerGroup.SUPER_VIP),
    command_lot_lotentities(15295, PlayerGroup.MODERATOR),
    command_lot_allowedlots(15300, PlayerGroup.GUEST),
    command_lot_allowedlotsall(15301, PlayerGroup.MODERATOR),
    command_lot_checkflag(15329, PlayerGroup.MODERATOR),
    command_lot_checkmemberbuild(15330, PlayerGroup.MODERATOR),
    command_misc_craft(15194, PlayerGroup.SUPER_VIP),
    command_misc_editsignwand(15188, PlayerGroup.GUEST),
    command_misc_signedit(15118, PlayerGroup.GUEST),
    command_misc_mail(15169, PlayerGroup.GUEST),
    command_misc_unsittamed(15182, PlayerGroup.GUEST),
    command_misc_bleach(15085, PlayerGroup.USER),
    command_misc_sortchest(15278, PlayerGroup.USER),
    command_misc_autoharvest(15280, PlayerGroup.SUPER_VIP),
    command_misc_findobject(15282, PlayerGroup.USER),
    command_misc_findobject_extended(15283, PlayerGroup.ADMIN),
    command_misc_present(15252, PlayerGroup.USER),
    command_misc_clearchat(15088, PlayerGroup.GUEST),
    command_misc_allow(15171, PlayerGroup.GUEST),
    command_misc_deny(15172, PlayerGroup.GUEST),
    command_misc_jump(15091, PlayerGroup.ADMIN),
    command_misc_kill(15092, PlayerGroup.ADMIN),
    command_misc_privatemessage(15093, PlayerGroup.GUEST),
    command_misc_played(15094, PlayerGroup.GUEST),
    command_misc_pvp(15095, PlayerGroup.GUEST),
    command_misc_resync(15214, PlayerGroup.ADMIN),
    command_misc_reply(15096, PlayerGroup.GUEST),
    command_misc_sit(15097, PlayerGroup.MODERATOR),
    command_misc_setbookcasetitle(15223, PlayerGroup.VIP),
    command_misc_setdata(15098, PlayerGroup.ADMIN),
    command_misc_setrank(15099, PlayerGroup.GOLDY),
    command_misc_setprefix(15100, PlayerGroup.GOLDY),
    command_misc_setprefix_allcharacters(15101, PlayerGroup.ADMIN),
    command_misc_resetprefix(15102, PlayerGroup.GOLDY),
    command_misc_reloadprefixes(15103, PlayerGroup.ADMIN),
    command_player_set_chatname(15104, PlayerGroup.USER),
    command_misc_set_helmet(15220, PlayerGroup.SUPER_VIP),
    command_misc_speak(15105, PlayerGroup.ADMIN),
    command_misc_test(15106, PlayerGroup.ADMIN),
    command_misc_custom(15276, PlayerGroup.ADMIN),
    command_misc_link(15272, PlayerGroup.USER),
    command_misc_fall(15262, PlayerGroup.ADMIN),
    command_misc_trash(15107, PlayerGroup.GUEST),
    command_misc_trash_clear(15315, PlayerGroup.MODERATOR),
    command_misc_boom(15109, PlayerGroup.ADMIN),
    command_misc_emote(15110, PlayerGroup.USER),
    command_misc_empty_bucket(15231, PlayerGroup.GUEST),
    command_misc_setart(15111, PlayerGroup.ADMIN),
    command_misc_save(15112, PlayerGroup.GUEST),
    command_misc_lights(15113, PlayerGroup.SUPER_VIP),
    command_misc_group_create(15173, PlayerGroup.GUEST),
    command_misc_group_delete(15174, PlayerGroup.GUEST),
    command_misc_group_member_add(15175, PlayerGroup.GUEST),
    command_misc_group_member_remove(15176, PlayerGroup.GUEST),
    command_misc_saveall(15114, PlayerGroup.ADMIN),
    command_misc_spike(15230, PlayerGroup.ADMIN),
    command_misc_chatchannel_join(15183, PlayerGroup.USER),
    command_misc_chatchannel_leave(15184, PlayerGroup.USER),
    command_misc_chatchannel_speak(15185, PlayerGroup.USER),
    command_misc_chatchannel_list(15186, PlayerGroup.USER),
    command_misc_chatchannel_joinall(15187, PlayerGroup.MODERATOR),
    command_misc_kicksit(15190, PlayerGroup.GUEST),
    command_misc_backpack(15216, PlayerGroup.VIP),
    command_misc_staffrequest(15014, PlayerGroup.GUEST),
    command_misc_endpot(15296, PlayerGroup.GUEST),
    command_player_settings(15213, PlayerGroup.GUEST),
    command_player_settings_other(15248, PlayerGroup.ADMIN),
    command_player_login(15249, PlayerGroup.GUEST),
    command_player_redeem(15257, PlayerGroup.GUEST),
    command_player_massdrop(15269, PlayerGroup.GUEST),
    command_player_channel(15271, PlayerGroup.GUEST),
    command_player_settimezone(15285, PlayerGroup.GUEST),
    command_player_referral(15310, PlayerGroup.USER),
    command_player_refferal_others(15311, PlayerGroup.SADMIN),
    command_player_sticksize(15312, PlayerGroup.DIAMOND),
    command_player_chatsounds(15314, PlayerGroup.GUEST),
    command_player_findrenamedplayer(15318, PlayerGroup.GUEST),
    command_player_findrenamedplayer_all(15319, PlayerGroup.MODERATOR),
    command_player_vote(15320, PlayerGroup.GUEST),
    command_player_miningstick(15321, PlayerGroup.DIAMOND),
    command_player_die(15323, PlayerGroup.GUEST),
    command_moderation_ban(15115, PlayerGroup.MODERATOR),
    command_moderation_filter(15195, PlayerGroup.MODERATOR),
    command_moderation_banned(15117, PlayerGroup.MODERATOR),
    command_moderation_whitelist(15167, PlayerGroup.ADMIN),
    command_moderation_clearinventory(15119, PlayerGroup.ADMIN),
    command_moderation_jail(15120, PlayerGroup.MODERATOR),
    command_moderation_kick(15121, PlayerGroup.MODERATOR),
    command_admin_listinventory(15122, PlayerGroup.ADMIN),
    command_moderation_modmsg(15123, PlayerGroup.MODERATOR),
    command_moderation_mute(15124, PlayerGroup.MODERATOR),
    command_moderation_removefrominventory(15125, PlayerGroup.ADMIN),
    command_moderation_unban(15126, PlayerGroup.MODERATOR),
    command_moderation_unjail(15127, PlayerGroup.MODERATOR),
    command_moderation_unmute(15128, PlayerGroup.MODERATOR),
    command_moderation_freeze(15148, PlayerGroup.MODERATOR),
    command_moderation_unfreeze(15149, PlayerGroup.MODERATOR),
    command_moderation_infract(15221, PlayerGroup.MODERATOR),
    command_moderation_lotlastedit(15284, PlayerGroup.MODERATOR),
    command_moderation_finditem(15004, PlayerGroup.MODERATOR),
    command_moderation_spectate(15256, PlayerGroup.MODERATOR),
    command_request_accept(15129, PlayerGroup.GUEST),
    command_request_reject(15130, PlayerGroup.GUEST),
    command_request_requestlist(15131, PlayerGroup.GUEST),
    command_request_request(15264, PlayerGroup.MODERATOR),
    command_shop_showpool(15152, PlayerGroup.ADMIN),
    command_shop_withdrawpool(15153, PlayerGroup.ADMIN),
    command_shop_depositpool(15154, PlayerGroup.ADMIN),
    command_shop_setpool(15155, PlayerGroup.ADMIN),
    command_shop_showbalance(15157, PlayerGroup.ADMIN),
    command_shop_shop(15158, PlayerGroup.GUEST),
    command_shop_getbalance(15205, PlayerGroup.GUEST),
    command_shop_getallbalances(15159, PlayerGroup.ADMIN),
    command_shop_sendmoney(15160, PlayerGroup.GUEST),
    command_shop_setshopchest(15132, PlayerGroup.ADMIN),
    command_shop_itemprice(15133, PlayerGroup.USER),
    command_shop_bank(15199, PlayerGroup.GUEST),
    command_shop_setshopitem(15325, PlayerGroup.GUEST),
    command_shop_chestshop(15326, PlayerGroup.GUEST),
    command_shop_chestshop_special(15327, PlayerGroup.MODERATOR),
    command_spoofing_login(15134, PlayerGroup.SADMIN),
    command_spoofing_logout(15135, PlayerGroup.SADMIN),
    command_spoofing_resetnames(15136, PlayerGroup.SADMIN),
    command_spoofing_setname(15137, PlayerGroup.SADMIN),
    command_spoofing_sudo(15209, PlayerGroup.SADMIN),
    command_tinywe_outline(15138, PlayerGroup.SUPER_VIP),
    command_tinywe_position(15139, PlayerGroup.VIP),
    command_tinywe_replace(15140, PlayerGroup.SUPER_VIP),
    command_tinywe_regen(15165, PlayerGroup.MODERATOR),
    command_tinywe_biome(15212, PlayerGroup.RAINBOW_MODERATOR),
    command_tinywe_count(15234, PlayerGroup.VIP),
    command_tinywe_size(15141, PlayerGroup.VIP),
    command_tinywe_resize(15166, PlayerGroup.SUPER_VIP),
    command_tinywe_set(15142, PlayerGroup.SUPER_VIP),
    command_tinywe_takeover(15143, PlayerGroup.MODERATOR),
    command_tinywe_walls(15144, PlayerGroup.SUPER_VIP),
    command_tinywe_wand(15145, PlayerGroup.VIP),
    command_tinywe_shift(15192, PlayerGroup.SUPER_VIP),
    command_tinywe_copy(15239, PlayerGroup.SUPER_VIP),
    command_tinywe_paste(15240, PlayerGroup.SUPER_VIP),
    command_tinywe_clipboard(15241, PlayerGroup.SUPER_VIP),
    command_tinywe_rotate(15292, PlayerGroup.SUPER_VIP),
    command_tinywe_sendselection(15324, PlayerGroup.MODERATOR),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Player Bonuses">
    bonus_no_restrictions(16000, PlayerGroup.ADMIN),
    //
    bonus_christmas_candle(16001, PlayerGroup.VIP),
    bonus_christmas_winterwonder(16002, PlayerGroup.VIP),
    //
    bonus_attack_pikachu(16003, PlayerGroup.ADMIN),
    //
    bonus_misc_fly(16004, PlayerGroup.ADMIN),
    bonus_misc_xray(16005, PlayerGroup.ADMIN),
    bonus_misc_flower(16007, PlayerGroup.VIP),
    bonus_misc_transmute(16008, PlayerGroup.VIP),
    //
    bonus_wand_create(16006, PlayerGroup.VIP),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="External Commands">
    // Note them as : 'external.<pluginname>.<permissionname>'
    // Subgroup Vanish (170xx) - next id is 17002
    external_vanishnopacket_wildcard(17001, PlayerGroup.SADMIN),
    // Subgroup mobdisguise  (171xx) - next id is 17103
    external_mobdisguise_mobs(17101, PlayerGroup.SADMIN),
    external_mobdisguise_players(17102, PlayerGroup.SADMIN),
    // Subgroup NoCheat  (172xx) - next id is 17207
    external_nocheat_spamfilter_override(17205, PlayerGroup.GUEST),
    external_nocheat_nomessagelimit(17206, PlayerGroup.GUEST),
    // Subgroup worldedit  (173xx) - next id is 17314
    external_worldedit_brush(17301, PlayerGroup.ADMIN),
    external_worldedit_clipboard(17302, PlayerGroup.ADMIN),
    external_worldedit_areamanipulation(17303, PlayerGroup.ADMIN),
    external_worldedit_storing(17304, PlayerGroup.ADMIN),
    external_worldedit_generation(17305, PlayerGroup.ADMIN),
    external_worldedit_admin(17306, PlayerGroup.SADMIN),
    external_worldedit_analysis(17307, PlayerGroup.ADMIN),
    external_worldedit_superpick(17308, PlayerGroup.ADMIN),
    external_worldedit_navigation(17309, PlayerGroup.ADMIN),
    external_worldedit_fixcommands(17310, PlayerGroup.ADMIN),
    external_worldedit_tools(17311, PlayerGroup.ADMIN),
    external_worldedit_selection(17312, PlayerGroup.ADMIN),
    external_worldedit_remove(17313, PlayerGroup.ADMIN),
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Game">
    game_start_all(18001, PlayerGroup.MODERATOR),
    game_start_ctf(18002, PlayerGroup.MODERATOR),
    game_start_dom(18004, PlayerGroup.MODERATOR),
    game_start_quake(18005, PlayerGroup.MODERATOR),
    game_start_tron(18006, PlayerGroup.MODERATOR),
    game_start_hungergames(18007, PlayerGroup.MODERATOR),
    game_start_endergolf(18008, PlayerGroup.MODERATOR),
    // ROOM FOR MORE GAMES!
    game_start_anywhere(18020, PlayerGroup.MODERATOR),
    game_override_end(18021, PlayerGroup.MODERATOR),
    game_override_add(18022, PlayerGroup.MODERATOR),
    game_override_parse(18023, PlayerGroup.MODERATOR),
    // </editor-fold>

    /** Nobody got access to this permission. */
    NONE(-1, PlayerGroup.NONE);
    /** The default group for this permission, it can be given custom to users with an other rank.
     * For everyone use PlayerGroup.Guest
     */
    public final PlayerGroup group;
    public final int id;

    private Permission(int id, PlayerGroup group) {
        this.group = group;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public PlayerGroup getGroup() {
        return group;
    }

    public String getRestrictMessage() {
        if (this == NONE || group == PlayerGroup.NONE) {
            return ChatColor.AQUA + "(None)";
        }
        return ChatColor.AQUA + "(" + group.name + "+)";
    }
    /** Permissions map for quick lookup */
    private static final HashMap<Integer, Permission> perms = new HashMap<Integer, Permission>();

    /**
     * Finds the permission with the given id, or if not found Permission.NONE
     * @param id
     * @return The permission or Permission.NONE
     */
    public static Permission getPermission(int id) {
        if (perms.containsKey(id)) {
            return perms.get(id);
        }
        return NONE;
    }

    static {
        for (Permission p : values()) {
            if (perms.containsKey(p.id)) {
                System.out.println("Double Permission found! " + p.name() + " & " + perms.get(p.id).name());
                perms.remove(p.id);
            } else {
                perms.put(p.id, p);
            }
        }
    }

    public static void main(String args[]) {
    }

}
