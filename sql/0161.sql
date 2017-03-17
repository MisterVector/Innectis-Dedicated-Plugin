ALTER TABLE `ban_whitelist` 
	DROP COLUMN `name`, COMMENT='';

ALTER TABLE `banned_ip_players` 
	CHANGE `banned_by_player_id` `banned_by_player_id` varchar(45)  COLLATE latin1_swedish_ci NULL after `player_id_list`, 
	CHANGE `banned_time` `banned_time` timestamp   NULL after `banned_by_player_id`, 
	CHANGE `duration_ticks` `duration_ticks` bigint(20)   NULL after `banned_time`, 
	CHANGE `joinban` `joinban` tinyint(1)   NULL after `duration_ticks`, 
	CHANGE `expired` `expired` bigint(20)   NOT NULL DEFAULT '0' after `joinban`, 
	DROP COLUMN `userlist`, 
	DROP COLUMN `banned_by`, COMMENT='';

ALTER TABLE `banned_players` 
	CHANGE `banned_by_player_id` `banned_by_player_id` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `player_id`, 
	CHANGE `banned_time` `banned_time` timestamp   NULL after `banned_by_player_id`, 
	CHANGE `duration_ticks` `duration_ticks` bigint(20)   NOT NULL DEFAULT '0' after `banned_time`, 
	CHANGE `joinban` `joinban` tinyint(1)   NULL DEFAULT '0' after `duration_ticks`, 
	CHANGE `expired` `expired` bigint(20)   NOT NULL DEFAULT '0' after `joinban`, 
	DROP COLUMN `username`, 
	DROP COLUMN `banned_by`, COMMENT='';

ALTER TABLE `block_breaks` 
	CHANGE `blockid` `blockid` int(11)   NOT NULL after `player_id`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `blockid`, 
	CHANGE `x` `x` int(11)   NOT NULL after `world`, 
	CHANGE `y` `y` int(11)   NOT NULL after `x`, 
	CHANGE `z` `z` int(11)   NOT NULL after `y`, 
	DROP COLUMN `username`, 
	DROP KEY `username`, COMMENT='';

ALTER TABLE `block_locks` 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `player_id`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	DROP COLUMN `username`, COMMENT='';

ALTER TABLE `block_log` 
	CHANGE `locx` `locx` int(11)   NOT NULL after `player_id`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `world` `world` varchar(45)  COLLATE utf8_general_ci NOT NULL after `locz`, 
	CHANGE `Id` `Id` int(11)   NOT NULL after `world`, 
	CHANGE `Data` `Data` int(11)   NOT NULL after `Id`, 
	CHANGE `DateTime` `DateTime` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP after `Data`, 
	CHANGE `ActionType` `ActionType` int(11)   NOT NULL after `DateTime`, 
	DROP COLUMN `name`, 
	DROP KEY `INX_Username`, COMMENT='';

ALTER TABLE `block_quota` 
	CHANGE `blockid` `blockid` int(11)   NOT NULL after `player_id`, 
	CHANGE `maxblocks` `maxblocks` int(11)   NOT NULL after `blockid`, 
	CHANGE `timespan` `timespan` int(11)   NOT NULL after `maxblocks`, 
	DROP COLUMN `username`, COMMENT='';

ALTER TABLE `block_quota_log` 
	CHANGE `blockid` `blockid` int(11)   NOT NULL after `player_id`, 
	DROP COLUMN `username`, 
	DROP KEY `idx`, add KEY `idx`(`time`,`blockid`), COMMENT='';

ALTER TABLE `bookcase` 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner_id`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `flags` `flags` bigint(11)   NOT NULL DEFAULT '0' after `locz`, 
	DROP COLUMN `owner`, 
	DROP KEY `INX_OWNER`, ENGINE=MyISAM, COMMENT='';

ALTER TABLE `bookcase_members` 
	CHANGE `isop` `isop` tinyint(1)   NOT NULL DEFAULT '0' after `player_id`, 
	DROP COLUMN `username`, ENGINE=MyISAM, COMMENT='';

ALTER TABLE `channel_bans` 
	DROP COLUMN `username`, COMMENT='';

ALTER TABLE `channel_members` 
	CHANGE `personalnum` `personalnum` int(11)   NOT NULL after `player_id`, 
	CHANGE `membergroup` `membergroup` int(11)   NOT NULL after `personalnum`, 
	DROP COLUMN `username`, COMMENT='';

ALTER TABLE `chestlog` 
	CHANGE `date` `date` timestamp   NULL DEFAULT CURRENT_TIMESTAMP after `player_id`, 
	DROP COLUMN `username`, 
	DROP KEY `name`, COMMENT='';

ALTER TABLE `chests` 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner_id`, 
	CHANGE `locx1` `locx1` int(11)   NOT NULL after `world`, 
	CHANGE `locy1` `locy1` int(11)   NOT NULL after `locx1`, 
	CHANGE `locz1` `locz1` int(11)   NOT NULL after `locy1`, 
	CHANGE `locx2` `locx2` int(11)   NOT NULL after `locz1`, 
	CHANGE `locy2` `locy2` int(11)   NOT NULL after `locx2`, 
	CHANGE `locz2` `locz2` int(11)   NOT NULL after `locy2`, 
	CHANGE `flags` `flags` bigint(11)   NOT NULL after `locz2`, 
	DROP COLUMN `owner`, COMMENT='';

ALTER TABLE `chests_members` 
	CHANGE `isop` `isop` tinyint(1)   NOT NULL DEFAULT '0' after `player_id`, 
	DROP COLUMN `username`, COMMENT='';

ALTER TABLE `configvalues` ENGINE=MyISAM, COMMENT='';

ALTER TABLE `doors` 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner_id`, 
	CHANGE `locx1` `locx1` int(11)   NOT NULL after `world`, 
	CHANGE `locy1` `locy1` int(11)   NOT NULL after `locx1`, 
	CHANGE `locz1` `locz1` int(11)   NOT NULL after `locy1`, 
	CHANGE `locx2` `locx2` int(11)   NOT NULL after `locz1`, 
	CHANGE `locy2` `locy2` int(11)   NOT NULL after `locx2`, 
	CHANGE `locz2` `locz2` int(11)   NOT NULL after `locy2`, 
	CHANGE `flags` `flags` int(11)   NOT NULL after `locz2`, 
	DROP COLUMN `owner`, COMMENT='';

ALTER TABLE `doors_members` 
	CHANGE `isop` `isop` tinyint(1)   NOT NULL DEFAULT '0' after `player_id`, 
	DROP COLUMN `username`, COMMENT='';

ALTER TABLE `enderchests` 
	CHANGE `typeid` `typeid` int(11)   NOT NULL after `player_id`, 
	CHANGE `bagid` `bagid` bigint(20)   NULL after `typeid`, 
	DROP COLUMN `username`, 
	DROP KEY `INDX_USERNAME_TYPEID`, add KEY `INDX_USERNAME_TYPEID`(`typeid`), ENGINE=MyISAM, COMMENT='';

ALTER TABLE `homes` 
	CHANGE `homeid` `homeid` int(11)   NOT NULL after `player_id`, 
	CHANGE `homename` `homename` varchar(60)  COLLATE latin1_swedish_ci NULL after `homeid`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `homename`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `yaw` `yaw` int(11)   NOT NULL after `locz`, 
	DROP COLUMN `username`, 
	DROP KEY `coords`, add KEY `coords`(`world`,`locx`,`locy`,`locz`), COMMENT='';

ALTER TABLE `infractions` ENGINE=MyISAM, COMMENT='';

ALTER TABLE `ip_log` 
	CHANGE `ip` `ip` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `player_id`, 
	CHANGE `logtime` `logtime` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP after `ip`, 
	CHANGE `logouttime` `logouttime` timestamp   NULL after `logtime`, 
	DROP COLUMN `name`, 
	ADD KEY `ip`(`ip`), 
	ADD KEY `ipplayer`(`player_id`,`ip`), 
	DROP KEY `name`, 
	ADD KEY `player_id`(`player_id`), COMMENT='';

ALTER TABLE `lot_banned` 
	CHANGE `timeout` `timeout` bigint(20)   NOT NULL after `player_id`, 
	DROP COLUMN `username`, 
	DROP KEY `primairy`, add UNIQUE KEY `primairy`(`lotid`,`player_id`), COMMENT='';

ALTER TABLE `lot_members` 
	CHANGE `isop` `isop` tinyint(1)   NOT NULL DEFAULT '0' after `player_id`, 
	DROP COLUMN `username`, 
	DROP KEY `primairy`, add UNIQUE KEY `primairy`(`lotid`,`player_id`), COMMENT='';

ALTER TABLE `lot_respawns` 
	CHANGE `player_id` `player_id` varchar(60)  COLLATE utf8_general_ci NULL first, 
	CHANGE `personalid` `personalid` int(11)   NULL after `player_id`, 
	DROP COLUMN `name`, 
	DROP KEY `PRIMARY`, COMMENT='';

ALTER TABLE `lot_safelist` 
	DROP COLUMN `username`, COMMENT='';

ALTER TABLE `lots` 
	CHANGE `lotnr` `lotnr` int(11)   NOT NULL after `owner_id`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `lotnr`, 
	CHANGE `lotname` `lotname` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `world`, 
	CHANGE `x1` `x1` int(11)   NOT NULL after `lotname`, 
	CHANGE `y1` `y1` int(11)   NOT NULL DEFAULT '-1' after `x1`, 
	CHANGE `z1` `z1` int(11)   NOT NULL after `y1`, 
	CHANGE `x2` `x2` int(11)   NOT NULL after `z1`, 
	CHANGE `y2` `y2` int(11)   NOT NULL DEFAULT '-1' after `x2`, 
	CHANGE `z2` `z2` int(11)   NOT NULL after `y2`, 
	CHANGE `sx` `sx` int(11)   NOT NULL after `z2`, 
	CHANGE `sy` `sy` int(11)   NOT NULL after `sx`, 
	CHANGE `sz` `sz` int(11)   NOT NULL after `sy`, 
	CHANGE `yaw` `yaw` int(11)   NOT NULL DEFAULT '0' after `sz`, 
	CHANGE `flags` `flags` bigint(11)   NOT NULL DEFAULT '0' after `yaw`, 
	CHANGE `creator_id` `creator_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `flags`, 
	CHANGE `creator` `creator` varchar(60)  COLLATE latin1_swedish_ci NULL after `creator_id`, 
	CHANGE `enter_msg` `enter_msg` varchar(200)  COLLATE latin1_swedish_ci NULL after `creator`, 
	CHANGE `exit_msg` `exit_msg` varchar(200)  COLLATE latin1_swedish_ci NULL after `enter_msg`, 
	CHANGE `last_owner_edit` `last_owner_edit` bigint(20)   NOT NULL after `exit_msg`, 
	CHANGE `last_member_edit` `last_member_edit` varchar(45)  COLLATE latin1_swedish_ci NULL after `last_owner_edit`, 
	CHANGE `warp_count` `warp_count` int(11)   NOT NULL DEFAULT '0' after `last_member_edit`, 
	CHANGE `hidden` `hidden` tinyint(1)   NOT NULL DEFAULT '0' after `warp_count`, 
	CHANGE `deleted` `deleted` tinyint(1)   NOT NULL DEFAULT '0' after `hidden`, 
	DROP COLUMN `owner`, 
	DROP KEY `lotnumbers`, add KEY `lotnumbers`(`lotnr`), 
	DROP KEY `owner`, COMMENT='';

ALTER TABLE `lots_backup` 
	CHANGE `flags` `flags` bigint(11)   NOT NULL DEFAULT '0' after `yaw`, 
	ADD COLUMN `enter_msg` varchar(60)  COLLATE latin1_swedish_ci NULL after `creator`, 
	ADD COLUMN `exit_msg` varchar(60)  COLLATE latin1_swedish_ci NULL after `enter_msg`, 
	ADD COLUMN `last_owner_edit` bigint(20)   NOT NULL after `exit_msg`, 
	ADD COLUMN `last_member_edit` bigint(20)   NULL after `last_owner_edit`, 
	ADD COLUMN `warp_count` int(11)   NOT NULL DEFAULT '0' after `last_member_edit`, 
	CHANGE `hidden` `hidden` tinyint(1)   NOT NULL DEFAULT '0' after `warp_count`, 
	CHANGE `deleted` `deleted` tinyint(1)   NOT NULL DEFAULT '0' after `hidden`, 
	DROP COLUMN `lastedit`, COMMENT='';

ALTER TABLE `member_groups` 
	CHANGE `groupname` `groupname` varchar(50)  COLLATE latin1_swedish_ci NOT NULL after `player_id`, 
	DROP COLUMN `username`, 
	DROP KEY `usergroup`, add UNIQUE KEY `usergroup`(`groupname`,`player_id`), COMMENT='';

ALTER TABLE `member_users` 
	CHANGE `groupid` `groupid` int(11)   NULL first, 
	CHANGE `player_id` `player_id` varchar(60)  COLLATE latin1_swedish_ci NULL after `groupid`, 
	DROP COLUMN `username`, 
	DROP KEY `PRIMARY`, COMMENT='';

ALTER TABLE `owned_entities` 
	CHANGE `entityid` `entityid` int(11)   NULL after `owner_id`, 
	CHANGE `mostsigbits` `mostsigbits` bigint(20)   NULL after `entityid`, 
	CHANGE `leastsigbits` `leastsigbits` bigint(20)   NULL after `mostsigbits`, 
	DROP COLUMN `owner`, COMMENT='';


ALTER TABLE `player_failedlogin` 
	CHANGE `ip` `ip` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `player_id`, 
	CHANGE `logdate` `logdate` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP after `ip`, 
	DROP COLUMN `username`, ENGINE=MyISAM, COMMENT='';

ALTER TABLE `player_infracts` 
	CHANGE `intensity` `intensity` int(11)   NOT NULL after `player_id`, 
	CHANGE `dateGMT` `dateGMT` bigint(20)   NOT NULL after `intensity`, 
	CHANGE `summary` `summary` varchar(100)  COLLATE utf8_general_ci NOT NULL after `dateGMT`, 
	CHANGE `details` `details` text  COLLATE utf8_general_ci NULL after `summary`, 
	CHANGE `staff_id` `staff_id` varchar(60)  COLLATE utf8_general_ci NOT NULL after `details`, 
	CHANGE `revoker_id` `revoker_id` varchar(60)  COLLATE utf8_general_ci NULL after `staff_id`, 
	CHANGE `revokeDateGMT` `revokeDateGMT` bigint(20)   NULL after `revoker_id`, 
	DROP COLUMN `name`, 
	DROP COLUMN `staff`, 
	DROP COLUMN `revoker`, 
	DROP KEY `INDEX_SFF`, 
	DROP KEY `INDEX_USR`, COMMENT='', DEFAULT CHARSET='latin1';

ALTER TABLE `player_inventory` 
	CHANGE `inventoryid` `inventoryid` double   NULL first, 
	CHANGE `player_id` `player_id` varchar(135)  COLLATE latin1_swedish_ci NOT NULL after `inventoryid`, 
	CHANGE `inventorytype` `inventorytype` double   NOT NULL after `player_id`, 
	CHANGE `level` `level` double   NOT NULL after `inventorytype`, 
	CHANGE `experience` `experience` float   NOT NULL after `level`, 
	CHANGE `bagid` `bagid` double   NULL after `experience`, 
	CHANGE `health` `health` double   NOT NULL after `bagid`, 
	CHANGE `hunger` `hunger` double   NOT NULL after `health`, 
	CHANGE `potioneffects` `potioneffects` varchar(480)  COLLATE latin1_swedish_ci NOT NULL after `hunger`, 
	DROP COLUMN `name`, 
	DROP KEY `name_type_UNIQUE`, 
	DROP KEY `PRIMARY`, add PRIMARY KEY(`player_id`,`inventorytype`), COMMENT='';

ALTER TABLE `player_password` 
	CHANGE `password` `password` blob   NOT NULL after `player_id`, 
	CHANGE `dateset` `dateset` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP after `password`, 
	DROP COLUMN `username`, ENGINE=MyISAM, COMMENT='';

ALTER TABLE `player_permission` 
	CHANGE `permissionid` `permissionid` int(11)   NOT NULL after `player_id`, 
	CHANGE `disabled` `disabled` tinyint(1)   NOT NULL DEFAULT '0' after `permissionid`, 
	DROP COLUMN `name`, COMMENT='';

ALTER TABLE `playermail` 
	CHANGE `from_player_id` `from_player_id` varchar(45)  COLLATE utf8_general_ci NULL after `to_player_id`, 
	CHANGE `title` `title` varchar(30)  COLLATE utf8_general_ci NULL after `from_player_id`, 
	CHANGE `content` `content` varchar(100)  COLLATE utf8_general_ci NULL after `title`, 
	DROP COLUMN `toplayer`, 
	DROP COLUMN `fromplayer`, COMMENT='';

ALTER TABLE `players` 
	CHANGE `player_id` `player_id` varchar(180)  COLLATE latin1_swedish_ci NOT NULL first, 
	CHANGE `name` `name` varchar(180)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `pvp_points` `pvp_points` double   NOT NULL DEFAULT '0' after `onlinetime`, 
	CHANGE `referral_points` `referral_points` double   NOT NULL DEFAULT '0' after `pvp_points`, 
	CHANGE `vote_points` `vote_points` double   NOT NULL DEFAULT '0' after `referral_points`, 
	CHANGE `valutas` `valutas` double   NOT NULL DEFAULT '0' after `vote_points`, 
	CHANGE `valutas_in_bank` `valutas_in_bank` double   NOT NULL DEFAULT '0' after `valutas`, 
	CHANGE `valutas_to_bank` `valutas_to_bank` double   NOT NULL DEFAULT '0' after `valutas_in_bank`, 
	CHANGE `valutas_to_player` `valutas_to_player` double   NOT NULL DEFAULT '0' after `valutas_to_bank`, 
	CHANGE `settings` `settings` double   NOT NULL DEFAULT '0' after `valutas_to_player`, 
	CHANGE `timezone` `timezone` varchar(90)  COLLATE latin1_swedish_ci NULL after `settings`, 
	CHANGE `playergroup` `playergroup` double   NOT NULL DEFAULT '-1' after `timezone`, 
	CHANGE `namecolour` `namecolour` varchar(30)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'x' after `playergroup`, 
	CHANGE `backpack` `backpack` double   NULL after `namecolour`, COMMENT='';

ALTER TABLE `players_ignored` 
	CHANGE `ignored_player_id` `ignored_player_id` varchar(60)  COLLATE utf8_general_ci NULL after `player_id`, 
	CHANGE `ignored_player` `ignored_player` varchar(60)  COLLATE utf8_general_ci NULL after `ignored_player_id`, 
	DROP COLUMN `player`, COMMENT='';

ALTER TABLE `poll_answers` 
	CHANGE `answer` `answer` varchar(100)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	DROP COLUMN `username`, 
	DROP KEY `PRIMARY`, ENGINE=MyISAM, COMMENT='';

ALTER TABLE `poll_list` ENGINE=MyISAM, COMMENT='';

ALTER TABLE `poll_options` ENGINE=MyISAM, COMMENT='';

ALTER TABLE `prefix` 
	CHANGE `subid` `subid` int(11)   NOT NULL after `player_id`, 
	CHANGE `text` `text` varchar(14)  COLLATE latin1_swedish_ci NULL after `subid`, 
	CHANGE `color1` `color1` varchar(10)  COLLATE latin1_swedish_ci NULL after `text`, 
	CHANGE `color2` `color2` varchar(10)  COLLATE latin1_swedish_ci NULL after `color1`, 
	DROP COLUMN `name`, 
	DROP KEY `prefix_UNIQUE`, add UNIQUE KEY `prefix_UNIQUE`(`subid`,`player_id`), COMMENT='';

ALTER TABLE `presents` 
	CHANGE `title` `title` varchar(100)  COLLATE latin1_swedish_ci NOT NULL after `creator_id`, 
	CHANGE `bagid` `bagid` bigint(20)   NOT NULL after `title`, 
	CHANGE `opened` `opened` bit(1)   NOT NULL DEFAULT b'0' after `bagid`, 
	DROP COLUMN `creator`, COMMENT='';

ALTER TABLE `staff_requests` 
	CHANGE `message` `message` varchar(200)  COLLATE latin1_swedish_ci NULL after `creator_id`, 
	DROP COLUMN `creator`, COMMENT='';

ALTER TABLE `switches` 
	CHANGE `locx` `locx` int(11)   NOT NULL after `owner_id`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `world` `world` varchar(50)  COLLATE utf8_general_ci NOT NULL after `locz`, 
	CHANGE `flags` `flags` bigint(20)   NOT NULL after `world`, 
	DROP COLUMN `owner`, ENGINE=MyISAM, COMMENT='', DEFAULT CHARSET='latin1';

ALTER TABLE `switches_links` ENGINE=MyISAM, COMMENT='', DEFAULT CHARSET='latin1';

ALTER TABLE `the_end_pot` 
	CHANGE `value` `value` int(11)   NULL DEFAULT '0' after `player_id`, 
	DROP COLUMN `player`, COMMENT='';

ALTER TABLE `trapdoors` 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner_id`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `flags` `flags` bigint(20)   NOT NULL after `locz`, 
	DROP COLUMN `owner`, COMMENT='';

ALTER TABLE `trapdoors_members` 
	CHANGE `isop` `isop` tinyint(1)   NOT NULL DEFAULT '0' after `player_id`, 
	DROP COLUMN `username`, COMMENT='';

ALTER TABLE `vote_log` 
	CHANGE `ip` `ip` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `service` `service` varchar(60)  COLLATE latin1_swedish_ci NULL after `ip`, 
	CHANGE `timestamp` `timestamp` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP after `service`, 
	DROP COLUMN `username`, 
	DROP KEY `username`, COMMENT='';

ALTER TABLE `waypoints` 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner_id`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `tworld` `tworld` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `locz`, 
	CHANGE `tlocx` `tlocx` int(11)   NOT NULL after `tworld`, 
	CHANGE `tlocy` `tlocy` int(11)   NOT NULL after `tlocx`, 
	CHANGE `tlocz` `tlocz` int(11)   NOT NULL after `tlocy`, 
	CHANGE `tyaw` `tyaw` float   NOT NULL after `tlocz`, 
	CHANGE `flags` `flags` bigint(11)   NOT NULL after `tyaw`, 
	CHANGE `forced` `forced` tinyint(4)   NULL after `flags`, 
	DROP COLUMN `owner`, COMMENT='';

ALTER TABLE `waypoints_members` 
	CHANGE `isop` `isop` tinyint(1)   NOT NULL DEFAULT '0' after `player_id`, 
	DROP COLUMN `username`, COMMENT='';

DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `getPlayerId`(input_name varchar(45)) RETURNS varchar(60) CHARSET latin1
    READS SQL DATA
    DETERMINISTIC
BEGIN
  declare return_id varchar(60);
  select player_id into return_id from players where lower(name) = lower(input_name) limit 1;  
  return return_id;
END$$
DELIMITER ;

INSERT INTO version (name,version) VALUES ('database', 161) ON DUPLICATE KEY UPDATE version = 161;