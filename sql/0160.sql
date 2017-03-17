ALTER TABLE `ban_whitelist` 
	ADD COLUMN `player_id` varchar(45)  COLLATE latin1_swedish_ci NOT NULL first, 
	CHANGE `name` `name` varchar(45)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	DROP KEY `PRIMARY`, add PRIMARY KEY(`player_id`), COMMENT='';

ALTER TABLE `banned_ip_players` 
	ADD COLUMN `player_id_list` text  COLLATE latin1_swedish_ci NULL after `iplist`, 
	CHANGE `userlist` `userlist` text  COLLATE latin1_swedish_ci NULL after `player_id_list`, 
	ADD COLUMN `banned_by_player_id` varchar(45)  COLLATE latin1_swedish_ci NULL after `userlist`, 
	CHANGE `banned_by` `banned_by` varchar(15)  COLLATE latin1_swedish_ci NULL after `banned_by_player_id`, 
	CHANGE `banned_time` `banned_time` timestamp   NULL after `banned_by`, 
	CHANGE `duration_ticks` `duration_ticks` bigint(20)   NULL after `banned_time`, 
	CHANGE `joinban` `joinban` tinyint(1)   NULL after `duration_ticks`, 
	CHANGE `expired` `expired` bigint(20)   NOT NULL DEFAULT '0' after `joinban`, COMMENT='';

ALTER TABLE `banned_players` 
	ADD COLUMN `player_id` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `ID`, 
	CHANGE `username` `username` varchar(45)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `banned_by` `banned_by` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `username`, 
	CHANGE `banned_time` `banned_time` timestamp   NULL after `banned_by`, 
	CHANGE `duration_ticks` `duration_ticks` bigint(20)   NOT NULL DEFAULT '0' after `banned_time`, 
	CHANGE `joinban` `joinban` tinyint(1)   NULL DEFAULT '0' after `duration_ticks`, 
	CHANGE `expired` `expired` bigint(20)   NOT NULL DEFAULT '0' after `joinban`, COMMENT='';

ALTER TABLE `block_breaks` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `time`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `blockid` `blockid` int(11)   NOT NULL after `username`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `blockid`, 
	CHANGE `x` `x` int(11)   NOT NULL after `world`, 
	CHANGE `y` `y` int(11)   NOT NULL after `x`, 
	CHANGE `z` `z` int(11)   NOT NULL after `y`, COMMENT='';

ALTER TABLE `block_locks` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `lockid`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `username`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, COMMENT='';

ALTER TABLE `block_log` 
	ADD COLUMN `player_id` varchar(45)  COLLATE utf8_general_ci NOT NULL after `logid`, 
	CHANGE `name` `name` varchar(45)  COLLATE utf8_general_ci NULL after `player_id`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `name`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `world` `world` varchar(45)  COLLATE utf8_general_ci NOT NULL after `locz`, 
	CHANGE `Id` `Id` int(11)   NOT NULL after `world`, 
	CHANGE `Data` `Data` int(11)   NOT NULL after `Id`, 
	CHANGE `DateTime` `DateTime` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP after `Data`, 
	CHANGE `ActionType` `ActionType` int(11)   NOT NULL after `DateTime`, COMMENT='';

ALTER TABLE `block_quota` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL first, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `blockid` `blockid` int(11)   NOT NULL after `username`, 
	CHANGE `maxblocks` `maxblocks` int(11)   NOT NULL after `blockid`, 
	CHANGE `timespan` `timespan` int(11)   NOT NULL after `maxblocks`, 
	DROP KEY `PRIMARY`, COMMENT='';

ALTER TABLE `block_quota_log` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `time`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `blockid` `blockid` int(11)   NOT NULL after `username`, COMMENT='';

ALTER TABLE `bookcase` 
	ADD COLUMN `owner_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `casetitle`, 
	CHANGE `owner` `owner` varchar(60)  COLLATE latin1_swedish_ci NULL after `owner_id`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `flags` `flags` bigint(11)   NOT NULL DEFAULT '0' after `locz`, COMMENT='';

ALTER TABLE `bookcase_members` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `bookcaseid`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `isop` `isop` tinyint(1)   NOT NULL DEFAULT '0' after `username`, COMMENT='';

ALTER TABLE `channel_bans` 
	ADD COLUMN `player_id` varchar(45)  COLLATE latin1_swedish_ci NULL after `channelid`, 
	CHANGE `username` `username` varchar(45)  COLLATE latin1_swedish_ci NULL after `player_id`, COMMENT='';

ALTER TABLE `channel_members` 
	ADD COLUMN `player_id` varchar(45)  COLLATE latin1_swedish_ci NULL after `channelid`, 
	CHANGE `username` `username` varchar(45)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `personalnum` `personalnum` int(11)   NOT NULL after `username`, 
	CHANGE `membergroup` `membergroup` int(11)   NOT NULL after `personalnum`, COMMENT='';

ALTER TABLE `chestlog` 
	ADD COLUMN `player_id` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `chestid`, 
	CHANGE `username` `username` varchar(45)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `date` `date` timestamp   NULL DEFAULT CURRENT_TIMESTAMP after `username`, COMMENT='';

ALTER TABLE `chests` 
	ADD COLUMN `owner_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `typeid`, 
	CHANGE `owner` `owner` varchar(60)  COLLATE latin1_swedish_ci NULL after `owner_id`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner`, 
	CHANGE `locx1` `locx1` int(11)   NOT NULL after `world`, 
	CHANGE `locy1` `locy1` int(11)   NOT NULL after `locx1`, 
	CHANGE `locz1` `locz1` int(11)   NOT NULL after `locy1`, 
	CHANGE `locx2` `locx2` int(11)   NOT NULL after `locz1`, 
	CHANGE `locy2` `locy2` int(11)   NOT NULL after `locx2`, 
	CHANGE `locz2` `locz2` int(11)   NOT NULL after `locy2`, 
	CHANGE `flags` `flags` bigint(11)   NOT NULL after `locz2`, COMMENT='';

ALTER TABLE `chests_members` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `chestid`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `isop` `isop` tinyint(1)   NOT NULL DEFAULT '0' after `username`, COMMENT='';

ALTER TABLE `doors` 
	ADD COLUMN `owner_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `doorid`, 
	CHANGE `owner` `owner` varchar(60)  COLLATE latin1_swedish_ci NULL after `owner_id`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner`, 
	CHANGE `locx1` `locx1` int(11)   NOT NULL after `world`, 
	CHANGE `locy1` `locy1` int(11)   NOT NULL after `locx1`, 
	CHANGE `locz1` `locz1` int(11)   NOT NULL after `locy1`, 
	CHANGE `locx2` `locx2` int(11)   NOT NULL after `locz1`, 
	CHANGE `locy2` `locy2` int(11)   NOT NULL after `locx2`, 
	CHANGE `locz2` `locz2` int(11)   NOT NULL after `locy2`, 
	CHANGE `flags` `flags` int(11)   NOT NULL after `locz2`, COMMENT='';

ALTER TABLE `doors_members` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `doorid`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `isop` `isop` tinyint(1)   NOT NULL DEFAULT '0' after `username`, COMMENT='';

ALTER TABLE `enderchests` 
	ADD COLUMN `player_id` varchar(50)  COLLATE latin1_swedish_ci NOT NULL after `chestid`, 
	CHANGE `username` `username` varchar(50)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `typeid` `typeid` int(11)   NOT NULL after `username`, 
	CHANGE `bagid` `bagid` bigint(20)   NULL after `typeid`, COMMENT='';

ALTER TABLE `homes` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `ID`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `homeid` `homeid` int(11)   NOT NULL after `username`, 
	CHANGE `homename` `homename` varchar(60)  COLLATE latin1_swedish_ci NULL after `homeid`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `homename`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `yaw` `yaw` int(11)   NOT NULL after `locz`, COMMENT='';

ALTER TABLE `ip_log` 
	ADD COLUMN `player_id` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `logid`, 
	CHANGE `name` `name` varchar(45)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `ip` `ip` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `name`, 
	CHANGE `logtime` `logtime` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP after `ip`, 
	CHANGE `logouttime` `logouttime` timestamp   NULL after `logtime`, COMMENT='';

ALTER TABLE `lot_banned` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `lotid`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `timeout` `timeout` bigint(20)   NOT NULL after `username`, COMMENT='';

ALTER TABLE `lot_members` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `lotid`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `isop` `isop` tinyint(1)   NOT NULL DEFAULT '0' after `username`, COMMENT='';

ALTER TABLE `lot_respawns` 
	ADD COLUMN `player_id` varchar(60)  COLLATE utf8_general_ci NULL first, 
	CHANGE `name` `name` varchar(60)  COLLATE utf8_general_ci NULL after `player_id`, 
	CHANGE `personalid` `personalid` int(11)   NULL after `name`, COMMENT='';

ALTER TABLE `lot_safelist` 
	ADD COLUMN `player_id` varchar(60)  COLLATE utf8_general_ci NULL after `lotid`, 
	CHANGE `username` `username` varchar(60)  COLLATE utf8_general_ci NULL after `player_id`, COMMENT='';

ALTER TABLE `lots` 
	ADD COLUMN `owner_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `lotid`, 
	CHANGE `owner` `owner` varchar(60)  COLLATE latin1_swedish_ci NULL after `owner_id`, 
	CHANGE `lotnr` `lotnr` int(11)   NOT NULL after `owner`, 
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
	ADD COLUMN `creator_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `flags`, 
	CHANGE `creator` `creator` varchar(60)  COLLATE latin1_swedish_ci NULL after `creator_id`, 
	CHANGE `enter_msg` `enter_msg` varchar(200)  COLLATE latin1_swedish_ci NULL after `creator`, 
	CHANGE `exit_msg` `exit_msg` varchar(200)  COLLATE latin1_swedish_ci NULL after `enter_msg`, 
	CHANGE `last_owner_edit` `last_owner_edit` bigint(20)   NOT NULL after `exit_msg`, 
	CHANGE `last_member_edit` `last_member_edit` varchar(45)  COLLATE latin1_swedish_ci NULL after `last_owner_edit`, 
	CHANGE `warp_count` `warp_count` int(11)   NOT NULL DEFAULT '0' after `last_member_edit`, 
	CHANGE `hidden` `hidden` tinyint(1)   NOT NULL DEFAULT '0' after `warp_count`, 
	CHANGE `deleted` `deleted` tinyint(1)   NOT NULL DEFAULT '0' after `hidden`, COMMENT='';

ALTER TABLE `member_groups` 
	ADD COLUMN `player_id` varchar(50)  COLLATE latin1_swedish_ci NOT NULL after `groupid`, 
	CHANGE `username` `username` varchar(50)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `groupname` `groupname` varchar(50)  COLLATE latin1_swedish_ci NOT NULL after `username`, COMMENT='';

ALTER TABLE `member_users` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `groupid`, 
	CHANGE `username` `username` varchar(50)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	DROP KEY `PRIMARY`, add PRIMARY KEY(`groupid`,`player_id`), COMMENT='';

ALTER TABLE `owned_entities` 
	ADD COLUMN `owenr_id` varchar(45)  COLLATE latin1_swedish_ci NULL first, 
	CHANGE `owner` `owner` varchar(45)  COLLATE latin1_swedish_ci NULL after `owenr_id`, 
	CHANGE `entityid` `entityid` int(11)   NULL after `owner`, 
	CHANGE `mostsigbits` `mostsigbits` bigint(20)   NULL after `entityid`, 
	CHANGE `leastsigbits` `leastsigbits` bigint(20)   NULL after `mostsigbits`, COMMENT='';

ALTER TABLE `player_failedlogin` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `logid`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `ip` `ip` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `username`, 
	CHANGE `logdate` `logdate` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP after `ip`, COMMENT='';

ALTER TABLE `player_infracts` 
	ADD COLUMN `player_id` varchar(60)  COLLATE utf8_general_ci NOT NULL after `id`, 
	CHANGE `name` `name` varchar(60)  COLLATE utf8_general_ci NULL after `player_id`, 
	CHANGE `intensity` `intensity` int(11)   NOT NULL after `name`, 
	CHANGE `dateGMT` `dateGMT` bigint(20)   NOT NULL after `intensity`, 
	CHANGE `summary` `summary` varchar(100)  COLLATE utf8_general_ci NOT NULL after `dateGMT`, 
	CHANGE `details` `details` text  COLLATE utf8_general_ci NULL after `summary`, 
	ADD COLUMN `staff_id` varchar(60)  COLLATE utf8_general_ci NOT NULL after `details`, 
	CHANGE `staff` `staff` varchar(60)  COLLATE utf8_general_ci NULL after `staff_id`, 
	ADD COLUMN `revoker_id` varchar(60)  COLLATE utf8_general_ci NULL after `staff`, 
	CHANGE `revoker` `revoker` varchar(60)  COLLATE utf8_general_ci NULL after `revoker_id`, 
	CHANGE `revokeDateGMT` `revokeDateGMT` bigint(20)   NULL after `revoker`, COMMENT='';

ALTER TABLE `player_inventory` 
	ADD COLUMN `player_id` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `inventoryid`, 
	CHANGE `name` `name` varchar(45)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `inventorytype` `inventorytype` int(11)   NOT NULL after `name`, 
	CHANGE `level` `level` int(11)   NOT NULL DEFAULT '-1' after `inventorytype`, 
	CHANGE `experience` `experience` float   NOT NULL DEFAULT '-1' after `level`, 
	CHANGE `bagid` `bagid` bigint(20)   NULL after `experience`, 
	CHANGE `health` `health` double   NOT NULL DEFAULT '20' after `bagid`, 
	CHANGE `hunger` `hunger` int(11)   NOT NULL DEFAULT '20' after `health`, 
	CHANGE `potioneffects` `potioneffects` varchar(160)  COLLATE latin1_swedish_ci NULL after `hunger`, COMMENT='';

ALTER TABLE `player_password` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL first, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `password` `password` blob   NOT NULL after `username`, 
	CHANGE `dateset` `dateset` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP after `password`, 
	DROP KEY `PRIMARY`, add PRIMARY KEY(`player_id`), COMMENT='';

ALTER TABLE `player_permission` 
	ADD COLUMN `player_id` varchar(50)  COLLATE latin1_swedish_ci NOT NULL first, 
	CHANGE `name` `name` varchar(50)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `permissionid` `permissionid` int(11)   NOT NULL after `name`, 
	CHANGE `disabled` `disabled` tinyint(1)   NOT NULL DEFAULT '0' after `permissionid`, 
	DROP KEY `PRIMARY`, add PRIMARY KEY(`player_id`,`permissionid`), COMMENT='';

ALTER TABLE `playermail` 
	ADD COLUMN `to_player_id` varchar(45)  COLLATE utf8_general_ci NULL after `readmail`, 
	CHANGE `toplayer` `toplayer` varchar(16)  COLLATE utf8_general_ci NULL after `to_player_id`, 
	ADD COLUMN `from_player_id` varchar(45)  COLLATE utf8_general_ci NULL after `toplayer`, 
	CHANGE `fromplayer` `fromplayer` varchar(16)  COLLATE utf8_general_ci NULL after `from_player_id`, 
	CHANGE `title` `title` varchar(30)  COLLATE utf8_general_ci NULL after `fromplayer`, 
	CHANGE `content` `content` varchar(100)  COLLATE utf8_general_ci NULL after `title`, COMMENT='';

ALTER TABLE `players` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL first, 
	CHANGE `name` `name` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `lastlogin` `lastlogin` timestamp   NULL DEFAULT CURRENT_TIMESTAMP after `name`, 
	CHANGE `onlinetime` `onlinetime` float   NOT NULL DEFAULT '0' after `lastlogin`, 
	CHANGE `pvp_points` `pvp_points` int(11)   NOT NULL DEFAULT '0' after `onlinetime`, 
	CHANGE `referral_points` `referral_points` int(11)   NOT NULL DEFAULT '0' after `pvp_points`, 
	CHANGE `vote_points` `vote_points` int(11)   NOT NULL DEFAULT '0' after `referral_points`, 
	CHANGE `valutas` `valutas` int(11)   NOT NULL DEFAULT '0' after `vote_points`, 
	CHANGE `valutas_in_bank` `valutas_in_bank` int(11)   NOT NULL DEFAULT '0' after `valutas`, 
	CHANGE `valutas_to_bank` `valutas_to_bank` int(11)   NULL DEFAULT '0' after `valutas_in_bank`, 
	CHANGE `valutas_to_player` `valutas_to_player` int(11)   NULL DEFAULT '0' after `valutas_to_bank`, 
	CHANGE `settings` `settings` bigint(20)   NOT NULL DEFAULT '0' after `valutas_to_player`, 
	CHANGE `timezone` `timezone` varchar(30)  COLLATE latin1_swedish_ci NULL after `settings`, 
	CHANGE `playergroup` `playergroup` int(11)   NOT NULL DEFAULT '-1' after `timezone`, 
	CHANGE `namecolour` `namecolour` varchar(10)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'x' COMMENT 'Hexvalue' after `playergroup`, 
	CHANGE `backpack` `backpack` bigint(20)   NULL after `namecolour`, 
	DROP KEY `PRIMARY`, add PRIMARY KEY(`player_id`), COMMENT='';

ALTER TABLE `players_ignored` 
	ADD COLUMN `player_id` varchar(60)  COLLATE utf8_general_ci NULL first, 
	CHANGE `player` `player` varchar(60)  COLLATE utf8_general_ci NULL after `player_id`, 
	ADD COLUMN `ignored_player_id` varchar(60)  COLLATE utf8_general_ci NULL after `player`, 
	CHANGE `ignored_player` `ignored_player` varchar(60)  COLLATE utf8_general_ci NULL after `ignored_player_id`, COMMENT='';

ALTER TABLE `poll_answers` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `ID`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL DEFAULT '' after `player_id`, 
	CHANGE `answer` `answer` varchar(100)  COLLATE latin1_swedish_ci NULL after `username`, 
	DROP KEY `PRIMARY`, add PRIMARY KEY(`ID`,`player_id`), COMMENT='';

ALTER TABLE `prefix` 
	ADD COLUMN `player_id` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `prefixid`, 
	CHANGE `name` `name` varchar(45)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `subid` `subid` int(11)   NOT NULL after `name`, 
	CHANGE `text` `text` varchar(14)  COLLATE latin1_swedish_ci NULL after `subid`, 
	CHANGE `color1` `color1` varchar(10)  COLLATE latin1_swedish_ci NULL after `text`, 
	CHANGE `color2` `color2` varchar(10)  COLLATE latin1_swedish_ci NULL after `color1`, COMMENT='';

ALTER TABLE `presents` 
	ADD COLUMN `creator_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `presentid`, 
	CHANGE `creator` `creator` varchar(60)  COLLATE latin1_swedish_ci NULL after `creator_id`, 
	CHANGE `title` `title` varchar(100)  COLLATE latin1_swedish_ci NOT NULL after `creator`, 
	CHANGE `bagid` `bagid` bigint(20)   NOT NULL after `title`, 
	CHANGE `opened` `opened` bit(1)   NOT NULL DEFAULT b'0' after `bagid`, COMMENT='';

ALTER TABLE `staff_requests` 
	ADD COLUMN `creator_id` varchar(45)  COLLATE latin1_swedish_ci NULL after `hasread`, 
	CHANGE `creator` `creator` varchar(15)  COLLATE latin1_swedish_ci NULL after `creator_id`, 
	CHANGE `message` `message` varchar(200)  COLLATE latin1_swedish_ci NULL after `creator`, COMMENT='';

ALTER TABLE `switches` 
	ADD COLUMN `owner_id` varchar(50)  COLLATE utf8_general_ci NOT NULL after `switchid`, 
	CHANGE `owner` `owner` varchar(50)  COLLATE utf8_general_ci NULL after `owner_id`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `owner`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `world` `world` varchar(50)  COLLATE utf8_general_ci NOT NULL after `locz`, 
	CHANGE `flags` `flags` bigint(20)   NOT NULL after `world`, COMMENT='';

ALTER TABLE `the_end_pot` 
	ADD COLUMN `player_id` varchar(60)  COLLATE utf8_general_ci NULL first, 
	CHANGE `player` `player` varchar(60)  COLLATE utf8_general_ci NULL after `player_id`, 
	CHANGE `value` `value` int(11)   NULL DEFAULT '0' after `player`, COMMENT='';

ALTER TABLE `trapdoors` 
	ADD COLUMN `owner_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `trapdoorid`, 
	CHANGE `owner` `owner` varchar(60)  COLLATE latin1_swedish_ci NULL after `owner_id`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `flags` `flags` bigint(20)   NOT NULL after `locz`, COMMENT='';

ALTER TABLE `trapdoors_members` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `trapdoorid`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `isop` `isop` tinyint(1)   NOT NULL DEFAULT '0' after `username`, COMMENT='';

ALTER TABLE `vote_log` 
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NULL first, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `ip` `ip` varchar(60)  COLLATE latin1_swedish_ci NULL after `username`, 
	CHANGE `service` `service` varchar(60)  COLLATE latin1_swedish_ci NULL after `ip`, 
	CHANGE `timestamp` `timestamp` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP after `service`, COMMENT='';

ALTER TABLE `waypoints` 
	ADD COLUMN `owner_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `waypointid`, 
	CHANGE `owner` `owner` varchar(60)  COLLATE latin1_swedish_ci NULL after `owner_id`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `tworld` `tworld` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `locz`, 
	CHANGE `tlocx` `tlocx` int(11)   NOT NULL after `tworld`, 
	CHANGE `tlocy` `tlocy` int(11)   NOT NULL after `tlocx`, 
	CHANGE `tlocz` `tlocz` int(11)   NOT NULL after `tlocy`, 
	CHANGE `tyaw` `tyaw` float   NOT NULL after `tlocz`, 
	CHANGE `flags` `flags` bigint(11)   NOT NULL after `tyaw`, 
	CHANGE `forced` `forced` tinyint(4)   NULL after `flags`, COMMENT='';

ALTER TABLE `waypoints_members`
	ADD COLUMN `player_id` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `waypointid`, 
	CHANGE `username` `username` varchar(60)  COLLATE latin1_swedish_ci NULL after `player_id`, 
	CHANGE `isop` `isop` tinyint(1)   NOT NULL DEFAULT '0' after `username`, COMMENT='';

ALTER TABLE `banned_players`
	CHANGE `banned_by` `banned_by` varchar(45)  COLLATE latin1_swedish_ci NULL after `username`, 
	ADD COLUMN `banned_by_player_id` varchar(45)  COLLATE latin1_swedish_ci NOT NULL after `banned_by`, 
	CHANGE `banned_time` `banned_time` timestamp   NULL after `banned_by_player_id`, 
	CHANGE `duration_ticks` `duration_ticks` bigint(20)   NOT NULL DEFAULT '0' after `banned_time`, 
	CHANGE `joinban` `joinban` tinyint(1)   NULL DEFAULT '0' after `duration_ticks`, 
	CHANGE `expired` `expired` bigint(20)   NOT NULL DEFAULT '0' after `joinban`, COMMENT='';

ALTER TABLE `lot_respawns`
	CHANGE `player_id` `player_id` varchar(60)  COLLATE utf8_general_ci NOT NULL first, 
	ADD PRIMARY KEY(`player_id`), COMMENT='';

ALTER TABLE `owned_entities`
	ADD COLUMN `owner_id` varchar(45)  COLLATE latin1_swedish_ci NULL first,
	CHANGE `owner` `owner` varchar(45)  COLLATE latin1_swedish_ci NULL after `owner_id`,
	DROP COLUMN `owenr_id`, COMMENT='';

INSERT INTO version (name,version) VALUES ('database', 160) ON DUPLICATE KEY UPDATE version = 160;