/*
SQLyog Ultimate - MySQL GUI v8.2 
MySQL - 5.5.45 : Database - innectis_db
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `ban_whitelist` */

DROP TABLE IF EXISTS `ban_whitelist`;

CREATE TABLE `ban_whitelist` (
  `player_id` varchar(45) NOT NULL,
  PRIMARY KEY (`player_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `banned_ip_players` */

DROP TABLE IF EXISTS `banned_ip_players`;

CREATE TABLE `banned_ip_players` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `iplist` text,
  `player_id_list` text,
  `banned_by_player_id` varchar(45) DEFAULT NULL,
  `banned_time` timestamp NULL DEFAULT NULL,
  `duration_ticks` bigint(20) DEFAULT NULL,
  `joinban` tinyint(1) DEFAULT NULL,
  `expired` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Table structure for table `banned_players` */

DROP TABLE IF EXISTS `banned_players`;

CREATE TABLE `banned_players` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` varchar(45) NOT NULL,
  `banned_by_player_id` varchar(45) NOT NULL,
  `banned_time` timestamp NULL DEFAULT NULL,
  `duration_ticks` bigint(20) NOT NULL DEFAULT '0',
  `joinban` tinyint(1) DEFAULT '0',
  `expired` bigint(20) NOT NULL DEFAULT '0',
  UNIQUE KEY `ID` (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

/*Table structure for table `block_breaks` */

DROP TABLE IF EXISTS `block_breaks`;

CREATE TABLE `block_breaks` (
  `time` datetime NOT NULL,
  `player_id` varchar(60) NOT NULL,
  `blockid` int(11) NOT NULL,
  `world` varchar(60) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  UNIQUE KEY `coords` (`world`,`x`,`y`,`z`),
  KEY `time` (`time`),
  KEY `blockid` (`blockid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `block_log` */

DROP TABLE IF EXISTS `block_log`;

CREATE TABLE `block_log` (
  `logid` bigint(20) NOT NULL AUTO_INCREMENT,
  `player_id` varchar(45) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `world` varchar(45) NOT NULL,
  `Id` int(11) NOT NULL,
  `Data` int(11) NOT NULL,
  `DateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ActionType` int(11) NOT NULL,
  PRIMARY KEY (`logid`),
  KEY `INX_Location` (`locx`,`locz`,`locy`,`world`)
) ENGINE=MyISAM AUTO_INCREMENT=641507 DEFAULT CHARSET=utf8;

/*Table structure for table `block_quota` */

DROP TABLE IF EXISTS `block_quota`;

CREATE TABLE `block_quota` (
  `player_id` varchar(60) NOT NULL,
  `blockid` int(11) NOT NULL,
  `maxblocks` int(11) NOT NULL,
  `timespan` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `block_quota_log` */

DROP TABLE IF EXISTS `block_quota_log`;

CREATE TABLE `block_quota_log` (
  `time` datetime NOT NULL,
  `player_id` varchar(60) NOT NULL,
  `blockid` int(11) NOT NULL,
  KEY `idx` (`time`,`blockid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `bookcase` */

DROP TABLE IF EXISTS `bookcase`;

CREATE TABLE `bookcase` (
  `bookcaseid` bigint(20) NOT NULL AUTO_INCREMENT,
  `bagid` bigint(20) NOT NULL DEFAULT '0',
  `casetitle` varchar(35) NOT NULL DEFAULT 'Bookcase',
  `owner_id` varchar(60) NOT NULL,
  `world` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `flags` bigint(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`bookcaseid`),
  UNIQUE KEY `UNQ` (`world`,`locx`,`locy`,`locz`),
  KEY `INX_LOC` (`world`,`locx`,`locy`,`locz`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

/*Table structure for table `bookcase_members` */

DROP TABLE IF EXISTS `bookcase_members`;

CREATE TABLE `bookcase_members` (
  `bookcaseid` bigint(20) NOT NULL,
  `player_id` varchar(60) NOT NULL,
  `isop` tinyint(1) NOT NULL DEFAULT '0',
  KEY `bookcaseid` (`bookcaseid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `channel_bans` */

DROP TABLE IF EXISTS `channel_bans`;

CREATE TABLE `channel_bans` (
  `channelid` int(11) NOT NULL,
  `player_id` varchar(45) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `channel_information` */

DROP TABLE IF EXISTS `channel_information`;

CREATE TABLE `channel_information` (
  `channelid` int(11) NOT NULL AUTO_INCREMENT,
  `channelname` varchar(45) DEFAULT NULL,
  `settings` bigint(20) NOT NULL,
  `password` varchar(45) DEFAULT NULL,
  `lastactivity` bigint(20) NOT NULL,
  PRIMARY KEY (`channelid`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

/*Table structure for table `channel_members` */

DROP TABLE IF EXISTS `channel_members`;

CREATE TABLE `channel_members` (
  `channelid` int(11) NOT NULL,
  `player_id` varchar(45) DEFAULT NULL,
  `personalnum` int(11) NOT NULL,
  `membergroup` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `chest_shop_list` */

DROP TABLE IF EXISTS `chest_shop_list`;

CREATE TABLE `chest_shop_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lotid` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

/*Table structure for table `chestlog` */

DROP TABLE IF EXISTS `chestlog`;

CREATE TABLE `chestlog` (
  `logid` int(11) NOT NULL AUTO_INCREMENT,
  `chestid` int(11) NOT NULL,
  `player_id` varchar(45) NOT NULL,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`logid`),
  KEY `date` (`date`)
) ENGINE=MyISAM AUTO_INCREMENT=964 DEFAULT CHARSET=latin1;

/*Table structure for table `chests` */

DROP TABLE IF EXISTS `chests`;

CREATE TABLE `chests` (
  `chestid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `typeid` int(11) NOT NULL DEFAULT '0',
  `owner_id` varchar(60) NOT NULL,
  `world` varchar(60) NOT NULL,
  `locx1` int(11) NOT NULL,
  `locy1` int(11) NOT NULL,
  `locz1` int(11) NOT NULL,
  `locx2` int(11) NOT NULL,
  `locy2` int(11) NOT NULL,
  `locz2` int(11) NOT NULL,
  `flags` bigint(11) NOT NULL,
  PRIMARY KEY (`chestid`),
  UNIQUE KEY `coord1` (`world`,`locx1`,`locy1`,`locz1`),
  KEY `coord2` (`world`,`locx2`,`locy2`,`locz2`)
) ENGINE=MyISAM AUTO_INCREMENT=78049 DEFAULT CHARSET=latin1;

/*Table structure for table `chests_members` */

DROP TABLE IF EXISTS `chests_members`;

CREATE TABLE `chests_members` (
  `chestid` int(11) unsigned NOT NULL,
  `player_id` varchar(60) NOT NULL,
  `isop` tinyint(1) NOT NULL DEFAULT '0',
  KEY `chestid` (`chestid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `chunk_data` */

DROP TABLE IF EXISTS `chunk_data`;

CREATE TABLE `chunk_data` (
  `chunkid` bigint(20) NOT NULL,
  `location` int(11) NOT NULL,
  `key` varchar(50) NOT NULL,
  `value` text NOT NULL,
  PRIMARY KEY (`key`,`location`,`chunkid`),
  KEY `fk_chunk_data_chunks1` (`chunkid`),
  KEY `INX_LOC` (`chunkid`,`location`),
  KEY `INX_LOCKEY` (`chunkid`,`location`,`key`),
  KEY `INX_KEY` (`key`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `chunks` */

DROP TABLE IF EXISTS `chunks`;

CREATE TABLE `chunks` (
  `chunkid` bigint(20) NOT NULL AUTO_INCREMENT,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `world` varchar(50) NOT NULL,
  PRIMARY KEY (`chunkid`),
  KEY `INX_LOC` (`world`,`locx`,`locz`,`locy`)
) ENGINE=MyISAM AUTO_INCREMENT=19829 DEFAULT CHARSET=latin1;

/*Table structure for table `configvalues` */

DROP TABLE IF EXISTS `configvalues`;

CREATE TABLE `configvalues` (
  `ckey` varchar(50) NOT NULL,
  `cvalue` text NOT NULL,
  PRIMARY KEY (`ckey`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `contentbag` */

DROP TABLE IF EXISTS `contentbag`;

CREATE TABLE `contentbag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bagsize` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=126 DEFAULT CHARSET=latin1;

/*Table structure for table `contentbag_items` */

DROP TABLE IF EXISTS `contentbag_items`;

CREATE TABLE `contentbag_items` (
  `bagid` bigint(20) NOT NULL,
  `locindex` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `data` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `itemdata` longblob,
  PRIMARY KEY (`bagid`,`locindex`),
  KEY `bagid` (`bagid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `custom_map_images` */

DROP TABLE IF EXISTS `custom_map_images`;

CREATE TABLE `custom_map_images` (
  `map_id` int(11) DEFAULT NULL,
  `image_data` blob
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `deaths` */

DROP TABLE IF EXISTS `deaths`;

CREATE TABLE `deaths` (
  `username` varchar(60) NOT NULL,
  `time` bigint(20) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `doors` */

DROP TABLE IF EXISTS `doors`;

CREATE TABLE `doors` (
  `doorid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `owner_id` varchar(60) NOT NULL,
  `world` varchar(60) NOT NULL,
  `locx1` int(11) NOT NULL,
  `locy1` int(11) NOT NULL,
  `locz1` int(11) NOT NULL,
  `locx2` int(11) NOT NULL,
  `locy2` int(11) NOT NULL,
  `locz2` int(11) NOT NULL,
  `flags` int(11) NOT NULL,
  PRIMARY KEY (`doorid`),
  KEY `coord1` (`world`,`locx1`,`locy1`,`locz1`),
  KEY `coord2` (`world`,`locx2`,`locy2`,`locz2`)
) ENGINE=MyISAM AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;

/*Table structure for table `doors_members` */

DROP TABLE IF EXISTS `doors_members`;

CREATE TABLE `doors_members` (
  `doorid` int(11) unsigned NOT NULL,
  `player_id` varchar(60) NOT NULL,
  `isop` tinyint(1) NOT NULL DEFAULT '0',
  KEY `doorid` (`doorid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `end_city_visits` */

DROP TABLE IF EXISTS `end_city_visits`;

CREATE TABLE `end_city_visits` (
  `player_id` varchar(180) DEFAULT NULL,
  `start_time` double NOT NULL,
  `leave_time` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `enderchests` */

DROP TABLE IF EXISTS `enderchests`;

CREATE TABLE `enderchests` (
  `chestid` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` varchar(50) NOT NULL,
  `typeid` int(11) NOT NULL,
  `bagid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`chestid`),
  KEY `INDX_USERNAME_TYPEID` (`typeid`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Table structure for table `geolite_blocks` */

DROP TABLE IF EXISTS `geolite_blocks`;

CREATE TABLE `geolite_blocks` (
  `startIpNum` int(11) NOT NULL,
  `endIpNum` int(11) NOT NULL,
  `locId` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `geolite_country` */

DROP TABLE IF EXISTS `geolite_country`;

CREATE TABLE `geolite_country` (
  `CountryCode` varchar(2) NOT NULL,
  `Name` varchar(100) NOT NULL,
  PRIMARY KEY (`CountryCode`),
  UNIQUE KEY `CountryCode_UNIQUE` (`CountryCode`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `geolite_location` */

DROP TABLE IF EXISTS `geolite_location`;

CREATE TABLE `geolite_location` (
  `locId` int(11) NOT NULL,
  `country` char(2) NOT NULL,
  `region` char(2) NOT NULL,
  `city` varchar(45) NOT NULL,
  `postalCode` varchar(10) NOT NULL,
  `latitude` mediumtext NOT NULL,
  `longitude` mediumtext NOT NULL,
  PRIMARY KEY (`locId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `homes` */

DROP TABLE IF EXISTS `homes`;

CREATE TABLE `homes` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` varchar(60) NOT NULL,
  `homeid` int(11) NOT NULL,
  `homename` varchar(60) DEFAULT NULL,
  `world` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `yaw` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `coords` (`world`,`locx`,`locy`,`locz`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;

/*Table structure for table `ip_log` */

DROP TABLE IF EXISTS `ip_log`;

CREATE TABLE `ip_log` (
  `logid` bigint(20) NOT NULL AUTO_INCREMENT,
  `player_id` varchar(45) NOT NULL,
  `ip` varchar(45) NOT NULL,
  `logtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `logouttime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`logid`),
  KEY `ip` (`ip`),
  KEY `ipplayer` (`player_id`,`ip`),
  KEY `player_id` (`player_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2473 DEFAULT CHARSET=latin1;

/*Table structure for table `lot_banned` */

DROP TABLE IF EXISTS `lot_banned`;

CREATE TABLE `lot_banned` (
  `lotid` int(11) NOT NULL,
  `player_id` varchar(60) NOT NULL,
  `timeout` bigint(20) NOT NULL,
  UNIQUE KEY `primairy` (`lotid`,`player_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `lot_members` */

DROP TABLE IF EXISTS `lot_members`;

CREATE TABLE `lot_members` (
  `lotid` int(11) NOT NULL,
  `player_id` varchar(60) NOT NULL,
  `isop` tinyint(1) NOT NULL DEFAULT '0',
  UNIQUE KEY `primairy` (`lotid`,`player_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `lot_names` */

DROP TABLE IF EXISTS `lot_names`;

CREATE TABLE `lot_names` (
  `lotname` varchar(60) NOT NULL,
  `lotid` int(11) NOT NULL,
  `time` bigint(20) NOT NULL,
  UNIQUE KEY `lotid` (`lotid`),
  KEY `lotname` (`lotname`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `lot_respawns` */

DROP TABLE IF EXISTS `lot_respawns`;

CREATE TABLE `lot_respawns` (
  `player_id` varchar(60) DEFAULT NULL,
  `personalid` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `lot_safelist` */

DROP TABLE IF EXISTS `lot_safelist`;

CREATE TABLE `lot_safelist` (
  `lotid` int(11) DEFAULT NULL,
  `player_id` varchar(60) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `lot_tags` */

DROP TABLE IF EXISTS `lot_tags`;

CREATE TABLE `lot_tags` (
  `lot_id` int(11) NOT NULL,
  `tag` text,
  `public_tag` tinyint(4) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `lots` */

DROP TABLE IF EXISTS `lots`;

CREATE TABLE `lots` (
  `lotid` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` varchar(60) NOT NULL,
  `lotnr` int(11) NOT NULL,
  `world` varchar(60) NOT NULL,
  `lotname` varchar(60) NOT NULL,
  `x1` int(11) NOT NULL,
  `y1` int(11) NOT NULL DEFAULT '-1',
  `z1` int(11) NOT NULL,
  `x2` int(11) NOT NULL,
  `y2` int(11) NOT NULL DEFAULT '-1',
  `z2` int(11) NOT NULL,
  `sx` int(11) NOT NULL,
  `sy` int(11) NOT NULL,
  `sz` int(11) NOT NULL,
  `yaw` int(11) NOT NULL DEFAULT '0',
  `flags` bigint(11) NOT NULL DEFAULT '0',
  `creator_id` varchar(60) NOT NULL,
  `creator` varchar(60) DEFAULT NULL,
  `enter_msg` varchar(200) DEFAULT NULL,
  `exit_msg` varchar(200) DEFAULT NULL,
  `last_owner_edit` bigint(20) NOT NULL,
  `last_member_edit` varchar(45) DEFAULT NULL,
  `warp_count` int(11) NOT NULL DEFAULT '0',
  `hidden` tinyint(1) NOT NULL DEFAULT '0',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`lotid`),
  KEY `lotnr` (`lotnr`),
  KEY `lotname` (`lotname`),
  KEY `coords` (`world`,`x1`,`x2`,`z1`,`z2`),
  KEY `lotnumbers` (`lotnr`)
) ENGINE=MyISAM AUTO_INCREMENT=131 DEFAULT CHARSET=latin1;

/*Table structure for table `owned_entities` */

DROP TABLE IF EXISTS `owned_entities`;

CREATE TABLE `owned_entities` (
  `owner_id` varchar(45) DEFAULT NULL,
  `entityid` int(11) DEFAULT NULL,
  `mostsigbits` bigint(20) DEFAULT NULL,
  `leastsigbits` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `player_failedlogin` */

DROP TABLE IF EXISTS `player_failedlogin`;

CREATE TABLE `player_failedlogin` (
  `logid` bigint(20) NOT NULL AUTO_INCREMENT,
  `player_id` varchar(60) NOT NULL,
  `ip` varchar(60) NOT NULL,
  `logdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`logid`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Table structure for table `player_infracts` */

DROP TABLE IF EXISTS `player_infracts`;

CREATE TABLE `player_infracts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` varchar(60) CHARACTER SET utf8 NOT NULL,
  `intensity` int(11) NOT NULL,
  `dateGMT` bigint(20) NOT NULL,
  `summary` varchar(100) CHARACTER SET utf8 NOT NULL,
  `details` text CHARACTER SET utf8,
  `staff_id` varchar(60) CHARACTER SET utf8 NOT NULL,
  `revoker_id` varchar(60) CHARACTER SET utf8 DEFAULT NULL,
  `revokeDateGMT` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `player_inventory` */

DROP TABLE IF EXISTS `player_inventory`;

CREATE TABLE `player_inventory` (
  `inventoryid` double DEFAULT NULL,
  `player_id` varchar(135) NOT NULL,
  `inventorytype` double NOT NULL,
  `level` double NOT NULL,
  `experience` float NOT NULL,
  `bagid` double DEFAULT NULL,
  `health` double NOT NULL,
  `hunger` double NOT NULL,
  `potioneffects` varchar(480) NOT NULL,
  PRIMARY KEY (`player_id`,`inventorytype`)
) ENGINE=MyISAM AUTO_INCREMENT=55 DEFAULT CHARSET=latin1;

/*Table structure for table `player_mining_stick` */

DROP TABLE IF EXISTS `player_mining_stick`;

CREATE TABLE `player_mining_stick` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` varchar(60) NOT NULL,
  `settings` double NOT NULL,
  `size` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

/*Table structure for table `player_nicknames` */

DROP TABLE IF EXISTS `player_nicknames`;

CREATE TABLE `player_nicknames` (
  `player_id` varchar(45) NOT NULL,
  `target_id` varchar(45) NOT NULL,
  `target_nickname` varchar(45) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `player_password` */

DROP TABLE IF EXISTS `player_password`;

CREATE TABLE `player_password` (
  `player_id` varchar(60) NOT NULL,
  `password` blob NOT NULL,
  `dateset` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`player_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `player_permission` */

DROP TABLE IF EXISTS `player_permission`;

CREATE TABLE `player_permission` (
  `player_id` varchar(50) NOT NULL,
  `permissionid` int(11) NOT NULL,
  `disabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`player_id`,`permissionid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `player_renames` */

DROP TABLE IF EXISTS `player_renames`;

CREATE TABLE `player_renames` (
  `player_id` varchar(45) NOT NULL,
  `rename_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `old_name` varchar(45) NOT NULL,
  `new_name` varchar(45) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `playermail` */

DROP TABLE IF EXISTS `playermail`;

CREATE TABLE `playermail` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `datecreated` date DEFAULT NULL,
  `readmail` tinyint(1) DEFAULT NULL,
  `to_player_id` varchar(45) DEFAULT NULL,
  `from_player_id` varchar(45) DEFAULT NULL,
  `title` varchar(30) DEFAULT NULL,
  `content` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=966 DEFAULT CHARSET=utf8;

/*Table structure for table `players` */

DROP TABLE IF EXISTS `players`;

CREATE TABLE `players` (
  `player_id` varchar(180) NOT NULL,
  `name` varchar(180) DEFAULT NULL,
  `lastlogin` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `onlinetime` float NOT NULL DEFAULT '0',
  `pvp_points` double NOT NULL DEFAULT '0',
  `vote_points` double NOT NULL DEFAULT '0',
  `valutas` double NOT NULL DEFAULT '0',
  `valutas_in_bank` double NOT NULL DEFAULT '0',
  `valutas_to_bank` double NOT NULL DEFAULT '0',
  `valutas_to_player` double NOT NULL DEFAULT '0',
  `settings` double NOT NULL DEFAULT '0',
  `chat_sound_settings` double NOT NULL DEFAULT '0',
  `playergroup` double NOT NULL DEFAULT '-1',
  `namecolour` varchar(30) NOT NULL DEFAULT 'x',
  `backpack` double DEFAULT NULL,
  `last_version` varchar(45) NOT NULL DEFAULT 'unknown',
  `refer_bonus` double NOT NULL DEFAULT '0',
  `refer_type` double NOT NULL DEFAULT '0',
  `refer_id` varchar(180) DEFAULT NULL,
  PRIMARY KEY (`player_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `players_ignored` */

DROP TABLE IF EXISTS `players_ignored`;

CREATE TABLE `players_ignored` (
  `player_id` varchar(60) DEFAULT NULL,
  `ignored_player_id` varchar(60) DEFAULT NULL,
  `ignored_player` varchar(60) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `prefix` */

DROP TABLE IF EXISTS `prefix`;

CREATE TABLE `prefix` (
  `prefixid` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` varchar(45) NOT NULL,
  `subid` int(11) NOT NULL,
  `text` varchar(14) DEFAULT NULL,
  `color1` varchar(10) DEFAULT NULL,
  `color2` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`prefixid`),
  UNIQUE KEY `prefix_UNIQUE` (`subid`,`player_id`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;

/*Table structure for table `presents` */

DROP TABLE IF EXISTS `presents`;

CREATE TABLE `presents` (
  `presentid` int(11) NOT NULL AUTO_INCREMENT,
  `creator_id` varchar(60) NOT NULL,
  `title` varchar(100) NOT NULL,
  `bagid` bigint(20) NOT NULL,
  `opened` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`presentid`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

/*Table structure for table `referral_forum_cache` */

DROP TABLE IF EXISTS `referral_forum_cache`;

CREATE TABLE `referral_forum_cache` (
  `userid` int(11) NOT NULL,
  `username` varchar(60) NOT NULL,
  `mcname` varchar(60) NOT NULL,
  `referrer` int(11) NOT NULL,
  `referrer_username` varchar(60) NOT NULL,
  `referrer_mcname` varchar(60) NOT NULL,
  PRIMARY KEY (`userid`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `referral_list` */

DROP TABLE IF EXISTS `referral_list`;

CREATE TABLE `referral_list` (
  `username` varchar(60) NOT NULL,
  `referred` varchar(60) NOT NULL,
  PRIMARY KEY (`username`,`referred`),
  UNIQUE KEY `referred` (`referred`),
  KEY `username` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `staff_requests` */

DROP TABLE IF EXISTS `staff_requests`;

CREATE TABLE `staff_requests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `datecreated` date DEFAULT NULL,
  `hasread` tinyint(1) DEFAULT '0',
  `creator_id` varchar(45) DEFAULT NULL,
  `message` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `switches` */

DROP TABLE IF EXISTS `switches`;

CREATE TABLE `switches` (
  `switchid` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` varchar(50) CHARACTER SET utf8 NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `world` varchar(50) CHARACTER SET utf8 NOT NULL,
  `flags` bigint(20) NOT NULL,
  PRIMARY KEY (`switchid`),
  KEY `location` (`locx`,`locy`,`locz`,`world`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

/*Table structure for table `switches_links` */

DROP TABLE IF EXISTS `switches_links`;

CREATE TABLE `switches_links` (
  `switchA` int(11) NOT NULL,
  `switchB` int(11) NOT NULL,
  PRIMARY KEY (`switchA`,`switchB`),
  KEY `switchA` (`switchA`),
  KEY `switchB` (`switchB`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `trapdoors` */

DROP TABLE IF EXISTS `trapdoors`;

CREATE TABLE `trapdoors` (
  `trapdoorid` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` varchar(60) NOT NULL,
  `world` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `flags` bigint(20) NOT NULL,
  PRIMARY KEY (`trapdoorid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Table structure for table `trapdoors_members` */

DROP TABLE IF EXISTS `trapdoors_members`;

CREATE TABLE `trapdoors_members` (
  `trapdoorid` int(11) NOT NULL,
  `player_id` varchar(60) NOT NULL,
  `isop` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `trash_items` */

DROP TABLE IF EXISTS `trash_items`;

CREATE TABLE `trash_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `typeid` int(11) NOT NULL,
  `data` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `itemdata` longblob,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4892 DEFAULT CHARSET=utf8;

/*Table structure for table `version` */

DROP TABLE IF EXISTS `version`;

CREATE TABLE `version` (
  `name` varchar(60) NOT NULL,
  `version` int(10) unsigned NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `vote_log` */

DROP TABLE IF EXISTS `vote_log`;

CREATE TABLE `vote_log` (
  `player_id` varchar(60) DEFAULT NULL,
  `ip` varchar(60) DEFAULT NULL,
  `service` varchar(60) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `warps` */

DROP TABLE IF EXISTS `warps`;

CREATE TABLE `warps` (
  `idwarps` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  `world` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `settings` bigint(20) NOT NULL,
  `yaw` int(11) NOT NULL DEFAULT '0',
  `comment` varchar(2000) NOT NULL,
  PRIMARY KEY (`idwarps`),
  UNIQUE KEY `unique` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=45 DEFAULT CHARSET=latin1;

/*Table structure for table `waypoints` */

DROP TABLE IF EXISTS `waypoints`;

CREATE TABLE `waypoints` (
  `waypointid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `owner_id` varchar(60) NOT NULL,
  `world` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `tworld` varchar(60) NOT NULL,
  `tlocx` int(11) NOT NULL,
  `tlocy` int(11) NOT NULL,
  `tlocz` int(11) NOT NULL,
  `tyaw` float NOT NULL,
  `flags` bigint(11) NOT NULL,
  `cost_type` int(11) NOT NULL,
  PRIMARY KEY (`waypointid`),
  UNIQUE KEY `loc` (`world`,`locx`,`locy`,`locz`)
) ENGINE=MyISAM AUTO_INCREMENT=54 DEFAULT CHARSET=latin1;

/*Table structure for table `waypoints_members` */

DROP TABLE IF EXISTS `waypoints_members`;

CREATE TABLE `waypoints_members` (
  `waypointid` int(11) unsigned NOT NULL,
  `player_id` varchar(60) NOT NULL,
  `isop` tinyint(1) NOT NULL DEFAULT '0',
  KEY `chestid` (`waypointid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/* Function  structure for function  `getChunkId` */

/*!50003 DROP FUNCTION IF EXISTS `getChunkId` */;
DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` FUNCTION `getChunkId`(locx int(11), locy int(11), locz int(11), world varchar(50)) RETURNS bigint(11)
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
END */$$
DELIMITER ;

/* Function  structure for function  `getplayerid` */

/*!50003 DROP FUNCTION IF EXISTS `getplayerid` */;
DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` FUNCTION `getplayerid`(input_name VARCHAR(45)) RETURNS varchar(60) CHARSET latin1
    READS SQL DATA
    DETERMINISTIC
BEGIN
  DECLARE return_id VARCHAR(60);
  SELECT player_id INTO return_id FROM players WHERE LOWER(NAME) = LOWER(input_name) LIMIT 1;  
  RETURN return_id;
END */$$
DELIMITER ;

/* Procedure structure for procedure `cleanup_assignable_lots` */

/*!50003 DROP PROCEDURE IF EXISTS  `cleanup_assignable_lots` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `cleanup_assignable_lots`()
BEGIN
        UPDATE lots SET flags=0, lastedit=0 WHERE OWNER='#';
        DELETE FROM lot_members USING lot_members INNER JOIN lots ON lot_members.lotid=lots.lotid WHERE lots.owner='#';
        DELETE FROM lot_messages USING lot_messages INNER JOIN lots ON lot_messages.lotid=lots.lotid WHERE lots.owner='#';
    END */$$
DELIMITER ;

/* Procedure structure for procedure `cleanup_old_logs` */

/*!50003 DROP PROCEDURE IF EXISTS  `cleanup_old_logs` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `cleanup_old_logs`()
BEGIN
DELETE FROM block_breaks WHERE `time` < DATE_SUB(NOW(), INTERVAL 4 MONTH);
DELETE FROM chestlog WHERE `date` < DATE_SUB(NOW(), INTERVAL 4 MONTH);
    END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
