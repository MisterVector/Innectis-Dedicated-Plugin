

CREATE DATABASE `innectis_db`;

/** User craft needs: 'select, insert, update, delete, execute, trigger and show view' for this DB **/

rename table `otakucraft`.`auto_responses` to `innectis_db`.`auto_responses`; 
rename table `otakucraft`.`ban_whitelist` to `innectis_db`.`ban_whitelist`; 
rename table `otakucraft`.`banned_ip_players` to `innectis_db`.`banned_ip_players`; 
rename table `otakucraft`.`banned_players` to `innectis_db`.`banned_players`; 
rename table `otakucraft`.`block_breaks` to `innectis_db`.`block_breaks`; 
rename table `otakucraft`.`block_locks` to `innectis_db`.`block_locks`; 
rename table `otakucraft`.`block_log` to `innectis_db`.`block_log`; 
rename table `otakucraft`.`block_quota` to `innectis_db`.`block_quota`; 
rename table `otakucraft`.`block_quota_log` to `innectis_db`.`block_quota_log`; 
rename table `otakucraft`.`bookcase` to `innectis_db`.`bookcase`; 
rename table `otakucraft`.`bookcase_members` to `innectis_db`.`bookcase_members`; 
rename table `otakucraft`.`chestlog` to `innectis_db`.`chestlog`; 
rename table `otakucraft`.`chests` to `innectis_db`.`chests`; 
rename table `otakucraft`.`chests_members` to `innectis_db`.`chests_members`; 
rename table `otakucraft`.`chunk_data` to `innectis_db`.`chunk_data`; 
rename table `otakucraft`.`chunks` to `innectis_db`.`chunks`; 
rename table `otakucraft`.`configvalues` to `innectis_db`.`configvalues`; 
rename table `otakucraft`.`contentbag` to `innectis_db`.`contentbag`; 
rename table `otakucraft`.`contentbag_items` to `innectis_db`.`contentbag_items`; 
rename table `otakucraft`.`converted_inventories` to `innectis_db`.`converted_inventories`; 
rename table `otakucraft`.`deaths` to `innectis_db`.`deaths`; 
rename table `otakucraft`.`doors` to `innectis_db`.`doors`; 
rename table `otakucraft`.`doors_members` to `innectis_db`.`doors_members`; 
rename table `otakucraft`.`enderchests` to `innectis_db`.`enderchests`; 
rename table `otakucraft`.`fort` to `innectis_db`.`fort`; 

rename table `otakucraft`.`fort_members` to `innectis_db`.`zold_fort_members`; 

rename table `otakucraft`.`geolite_blocks` to `innectis_db`.`geolite_blocks`; 
rename table `otakucraft`.`geolite_country` to `innectis_db`.`geolite_country`; 
rename table `otakucraft`.`geolite_location` to `innectis_db`.`geolite_location`; 
rename table `otakucraft`.`held_items` to `innectis_db`.`held_items`; 
rename table `otakucraft`.`homes` to `innectis_db`.`homes`; 
rename table `otakucraft`.`infractions` to `innectis_db`.`infractions`; 
rename table `otakucraft`.`ip_log` to `innectis_db`.`ip_log`; 
rename table `otakucraft`.`item` to `innectis_db`.`item`; 
rename table `otakucraft`.`lot_banned` to `innectis_db`.`lot_banned`; 
rename table `otakucraft`.`lot_members` to `innectis_db`.`lot_members`; 
rename table `otakucraft`.`lot_messages` to `innectis_db`.`lot_messages`; 
rename table `otakucraft`.`lot_names` to `innectis_db`.`lot_names`; 
rename table `otakucraft`.`lots` to `innectis_db`.`lots`; 

rename table `otakucraft`.`lots_OLD2012-03-20` to `innectis_db`.`zold_lots_OLD2012-03-20`; 

rename table `otakucraft`.`member_groups` to `innectis_db`.`member_groups`; 
rename table `otakucraft`.`member_users` to `innectis_db`.`member_users`; 
rename table `otakucraft`.`player_channels` to `innectis_db`.`player_channels`; 
rename table `otakucraft`.`player_inventory` to `innectis_db`.`player_inventory`; 
rename table `otakucraft`.`player_permission` to `innectis_db`.`player_permission`; 
rename table `otakucraft`.`playermail` to `innectis_db`.`playermail`; 
rename table `otakucraft`.`players` to `innectis_db`.`players`; 
rename table `otakucraft`.`prefix` to `innectis_db`.`prefix`; 
rename table `otakucraft`.`pvp_leavers` to `innectis_db`.`pvp_leavers`; 
rename table `otakucraft`.`pvp_opt` to `innectis_db`.`pvp_opt`; 
rename table `otakucraft`.`referral_forum_cache` to `innectis_db`.`referral_forum_cache`; 
rename table `otakucraft`.`referral_list` to `innectis_db`.`referral_list`; 
rename table `otakucraft`.`saved_inventory` to `innectis_db`.`saved_inventory`; 
rename table `otakucraft`.`shop_items` to `innectis_db`.`shop_items`; 
rename table `otakucraft`.`staff_requests` to `innectis_db`.`staff_requests`; 

rename table `otakucraft`.`stored_inventory` to `innectis_db`.`zold_stored_inventory`; 
rename table `otakucraft`.`tmp` to `innectis_db`.`zold_tmp`; 

rename table `otakucraft`.`version` to `innectis_db`.`version`; 
rename table `otakucraft`.`vote_log` to `innectis_db`.`vote_log`; 
rename table `otakucraft`.`warps` to `innectis_db`.`warps`; 
rename table `otakucraft`.`waypoints` to `innectis_db`.`waypoints`; 
rename table `otakucraft`.`waypoints_members` to `innectis_db`.`waypoints_members`; 


INSERT INTO `innectis_db`.version (name,version) VALUES ('database', 109) ON DUPLICATE KEY UPDATE version=109;


-- --------------------------------------------------------------------------------
-- Routine DDL
-- Note: comments before and after the routine body will not be stored by the server
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE FUNCTION `innectis_db`.`getChunkId`(locx int(11), locy int(11), locz int(11), world varchar(50)) RETURNS bigint(11)
    READS SQL DATA
    DETERMINISTIC
BEGIN
    declare id bigint(11);
    set id = -999;
    
    SELECT c.chunkid into id FROM chunks as c 
    WHERE c.world = world AND c.locx = locx AND c.locz = locz AND c.locy = locy LIMIT 1;
    
    IF id = -999 THEN
        INSERT INTO chunks (`locx`,`locy`,`locz`,`world`)
        VALUES (locx,locy,locz,world);
        
        SELECT LAST_INSERT_ID() INTO id;
    END IF;
    
    return id;
END
