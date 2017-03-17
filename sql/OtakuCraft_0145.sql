/*
SQLyog Ultimate - MySQL GUI v8.2 
MySQL - 5.6.12 : Database - innectis_db
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `auto_responses` */

DROP TABLE IF EXISTS `auto_responses`;

CREATE TABLE `auto_responses` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `owner` varchar(64) NOT NULL,
  `order` int(11) NOT NULL,
  `response` text NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `ban_whitelist` */

DROP TABLE IF EXISTS `ban_whitelist`;

CREATE TABLE `ban_whitelist` (
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `banned_ip_players` */

DROP TABLE IF EXISTS `banned_ip_players`;

CREATE TABLE `banned_ip_players` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `iplist` text,
  `userlist` text,
  `banned_by` varchar(15) DEFAULT NULL,
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
  `username` varchar(45) NOT NULL,
  `banned_by` varchar(45) NOT NULL,
  `banned_time` timestamp NULL DEFAULT NULL,
  `duration_ticks` bigint(20) NOT NULL DEFAULT '0',
  `joinban` tinyint(1) DEFAULT '0',
  `expired` bigint(20) NOT NULL DEFAULT '0',
  UNIQUE KEY `ID` (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

/*Table structure for table `block_breaks` */

DROP TABLE IF EXISTS `block_breaks`;

CREATE TABLE `block_breaks` (
  `time` datetime NOT NULL,
  `username` varchar(60) NOT NULL,
  `blockid` int(11) NOT NULL,
  `world` varchar(60) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  UNIQUE KEY `coords` (`world`,`x`,`y`,`z`),
  KEY `time` (`time`),
  KEY `username` (`username`),
  KEY `blockid` (`blockid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `block_locks` */

DROP TABLE IF EXISTS `block_locks`;

CREATE TABLE `block_locks` (
  `lockid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(60) NOT NULL,
  `world` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  PRIMARY KEY (`lockid`),
  UNIQUE KEY `coords` (`world`,`locx`,`locy`,`locz`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Table structure for table `block_log` */

DROP TABLE IF EXISTS `block_log`;

CREATE TABLE `block_log` (
  `logid` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `world` varchar(45) NOT NULL,
  `Id` int(11) NOT NULL,
  `Data` int(11) NOT NULL,
  `DateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ActionType` int(11) NOT NULL,
  PRIMARY KEY (`logid`),
  KEY `INX_Username` (`name`),
  KEY `INX_Location` (`locx`,`locz`,`locy`,`world`)
) ENGINE=MyISAM AUTO_INCREMENT=54070 DEFAULT CHARSET=utf8;

/*Table structure for table `block_quota` */

DROP TABLE IF EXISTS `block_quota`;

CREATE TABLE `block_quota` (
  `username` varchar(60) NOT NULL,
  `blockid` int(11) NOT NULL,
  `maxblocks` int(11) NOT NULL,
  `timespan` int(11) NOT NULL,
  PRIMARY KEY (`username`,`blockid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `block_quota_log` */

DROP TABLE IF EXISTS `block_quota_log`;

CREATE TABLE `block_quota_log` (
  `time` datetime NOT NULL,
  `username` varchar(60) NOT NULL,
  `blockid` int(11) NOT NULL,
  KEY `idx` (`time`,`username`,`blockid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `bookcase` */

DROP TABLE IF EXISTS `bookcase`;

CREATE TABLE `bookcase` (
  `bookcaseid` bigint(20) NOT NULL AUTO_INCREMENT,
  `bagid` bigint(20) NOT NULL DEFAULT '0',
  `casetitle` varchar(35) NOT NULL DEFAULT 'Bookcase',
  `owner` varchar(60) NOT NULL,
  `world` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `flags` bigint(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`bookcaseid`),
  UNIQUE KEY `UNQ` (`world`,`locx`,`locy`,`locz`),
  KEY `INX_LOC` (`world`,`locx`,`locy`,`locz`),
  KEY `INX_OWNER` (`owner`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `bookcase_members` */

DROP TABLE IF EXISTS `bookcase_members`;

CREATE TABLE `bookcase_members` (
  `bookcaseid` bigint(20) NOT NULL,
  `username` varchar(60) NOT NULL,
  `isop` tinyint(1) NOT NULL DEFAULT '0',
  KEY `bookcaseid` (`bookcaseid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `channel_bans` */

DROP TABLE IF EXISTS `channel_bans`;

CREATE TABLE `channel_bans` (
  `channelid` int(11) NOT NULL,
  `username` varchar(45) DEFAULT NULL
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
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

/*Table structure for table `channel_members` */

DROP TABLE IF EXISTS `channel_members`;

CREATE TABLE `channel_members` (
  `channelid` int(11) NOT NULL,
  `username` varchar(45) DEFAULT NULL,
  `personalnum` int(11) NOT NULL,
  `membergroup` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `chestlog` */

DROP TABLE IF EXISTS `chestlog`;

CREATE TABLE `chestlog` (
  `logid` int(11) NOT NULL AUTO_INCREMENT,
  `chestid` int(11) NOT NULL,
  `username` varchar(45) NOT NULL,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`logid`),
  KEY `date` (`date`),
  KEY `name` (`username`)
) ENGINE=MyISAM AUTO_INCREMENT=625 DEFAULT CHARSET=latin1;

/*Table structure for table `chests` */

DROP TABLE IF EXISTS `chests`;

CREATE TABLE `chests` (
  `chestid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `typeid` int(11) NOT NULL DEFAULT '0',
  `owner` varchar(60) NOT NULL,
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
) ENGINE=MyISAM AUTO_INCREMENT=44 DEFAULT CHARSET=latin1;

/*Table structure for table `chests_members` */

DROP TABLE IF EXISTS `chests_members`;

CREATE TABLE `chests_members` (
  `chestid` int(11) unsigned NOT NULL,
  `username` varchar(60) NOT NULL,
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
) ENGINE=MyISAM AUTO_INCREMENT=46491 DEFAULT CHARSET=latin1;

/*Table structure for table `configvalues` */

DROP TABLE IF EXISTS `configvalues`;

CREATE TABLE `configvalues` (
  `ckey` varchar(50) NOT NULL,
  `cvalue` text NOT NULL,
  PRIMARY KEY (`ckey`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `contentbag` */

DROP TABLE IF EXISTS `contentbag`;

CREATE TABLE `contentbag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bagsize` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=71 DEFAULT CHARSET=latin1;

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

/*Table structure for table `converted_inventories` */

DROP TABLE IF EXISTS `converted_inventories`;

CREATE TABLE `converted_inventories` (
  `playername` varchar(255) NOT NULL,
  PRIMARY KEY (`playername`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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
  `owner` varchar(60) NOT NULL,
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
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

/*Table structure for table `doors_members` */

DROP TABLE IF EXISTS `doors_members`;

CREATE TABLE `doors_members` (
  `doorid` int(11) unsigned NOT NULL,
  `username` varchar(60) NOT NULL,
  `isop` tinyint(1) NOT NULL DEFAULT '0',
  KEY `doorid` (`doorid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `enderchests` */

DROP TABLE IF EXISTS `enderchests`;

CREATE TABLE `enderchests` (
  `chestid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `typeid` int(11) NOT NULL,
  `bagid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`chestid`),
  KEY `INDX_USERNAME_TYPEID` (`username`,`typeid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `fort_members` */

DROP TABLE IF EXISTS `fort_members`;

CREATE TABLE `fort_members` (
  `memberid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(55) NOT NULL,
  `fortid` int(11) NOT NULL,
  `rank` int(11) NOT NULL,
  PRIMARY KEY (`memberid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `game_profiles` */

DROP TABLE IF EXISTS `game_profiles`;

CREATE TABLE `game_profiles` (
  `username` varchar(60) NOT NULL,
  `flags` bigint(20) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `game_profiles_scores` */

DROP TABLE IF EXISTS `game_profiles_scores`;

CREATE TABLE `game_profiles_scores` (
  `username` varchar(60) NOT NULL DEFAULT '',
  `score_type` varchar(60) NOT NULL DEFAULT '',
  `score` int(11) DEFAULT NULL,
  PRIMARY KEY (`username`,`score_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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

/*Table structure for table `held_items` */

DROP TABLE IF EXISTS `held_items`;

CREATE TABLE `held_items` (
  `username` varchar(60) NOT NULL,
  `id` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `durability` int(11) NOT NULL,
  `data` int(11) NOT NULL,
  KEY `username` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `homes` */

DROP TABLE IF EXISTS `homes`;

CREATE TABLE `homes` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(60) NOT NULL,
  `homeid` int(11) NOT NULL,
  `homename` varchar(60) DEFAULT NULL,
  `world` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `yaw` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `coords` (`world`,`locx`,`locy`,`locz`)
) ENGINE=MyISAM AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;

/*Table structure for table `infractions` */

DROP TABLE IF EXISTS `infractions`;

CREATE TABLE `infractions` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `player` varchar(60) NOT NULL,
  `incidentTime` bigint(20) NOT NULL,
  `extraInfo` varchar(255) NOT NULL,
  `staffMember` varchar(60) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `ip_conlog` */

DROP TABLE IF EXISTS `ip_conlog`;

CREATE TABLE `ip_conlog` (
  `ip_id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `con_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ip_id`,`name`,`con_date`),
  KEY `FK_ip_id` (`ip_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `ip_gen` */

DROP TABLE IF EXISTS `ip_gen`;

CREATE TABLE `ip_gen` (
  `ip_id` int(11) NOT NULL AUTO_INCREMENT,
  `ip_addr` varchar(45) NOT NULL,
  `ip_banned` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ip_id`),
  UNIQUE KEY `ip_addr_UNIQUE` (`ip_addr`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `ip_log` */

DROP TABLE IF EXISTS `ip_log`;

CREATE TABLE `ip_log` (
  `logid` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `ip` varchar(45) NOT NULL,
  `logtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `logouttime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`logid`),
  KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=371 DEFAULT CHARSET=latin1;

/*Table structure for table `item` */

DROP TABLE IF EXISTS `item`;

CREATE TABLE `item` (
  `itemid` int(11) NOT NULL,
  `data` int(11) NOT NULL DEFAULT '0',
  `buyprice` int(11) NOT NULL DEFAULT '0',
  `sellprice` int(11) NOT NULL DEFAULT '0',
  `pointsonly` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`itemid`,`data`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `lot_banned` */

DROP TABLE IF EXISTS `lot_banned`;

CREATE TABLE `lot_banned` (
  `lotid` int(11) NOT NULL,
  `username` varchar(60) NOT NULL,
  `timeout` bigint(20) NOT NULL,
  UNIQUE KEY `primairy` (`lotid`,`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `lot_members` */

DROP TABLE IF EXISTS `lot_members`;

CREATE TABLE `lot_members` (
  `lotid` int(11) NOT NULL,
  `username` varchar(60) NOT NULL,
  `isop` tinyint(1) NOT NULL DEFAULT '0',
  UNIQUE KEY `primairy` (`lotid`,`username`)
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

/*Table structure for table `lots` */

DROP TABLE IF EXISTS `lots`;

CREATE TABLE `lots` (
  `lotid` int(11) NOT NULL AUTO_INCREMENT,
  `owner` varchar(60) NOT NULL,
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
  `creator` varchar(60) NOT NULL,
  `enter_msg` varchar(200) DEFAULT NULL,
  `exit_msg` varchar(200) DEFAULT NULL,
  `last_owner_edit` bigint(20) NOT NULL,
  `last_member_edit` varchar(45) DEFAULT NULL,
  `warp_count` int(11) NOT NULL DEFAULT '0',
  `hidden` tinyint(1) NOT NULL DEFAULT '0',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`lotid`),
  KEY `lotnr` (`lotnr`),
  KEY `owner` (`owner`),
  KEY `lotnumbers` (`owner`,`lotnr`),
  KEY `lotname` (`lotname`),
  KEY `coords` (`world`,`x1`,`x2`,`z1`,`z2`)
) ENGINE=MyISAM AUTO_INCREMENT=51 DEFAULT CHARSET=latin1;

/*Table structure for table `lots_backup` */

DROP TABLE IF EXISTS `lots_backup`;

CREATE TABLE `lots_backup` (
  `lotid` int(11) NOT NULL AUTO_INCREMENT,
  `owner` varchar(60) NOT NULL,
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
  `flags` int(11) NOT NULL DEFAULT '0',
  `creator` varchar(60) NOT NULL,
  `lastedit` bigint(20) NOT NULL,
  `hidden` tinyint(1) NOT NULL DEFAULT '0',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`lotid`),
  KEY `lotnr` (`lotnr`),
  KEY `owner` (`owner`),
  KEY `lotnumbers` (`owner`,`lotnr`),
  KEY `lotname` (`lotname`),
  KEY `coords` (`world`,`x1`,`x2`,`z1`,`z2`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `member_groups` */

DROP TABLE IF EXISTS `member_groups`;

CREATE TABLE `member_groups` (
  `groupid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `groupname` varchar(50) NOT NULL,
  PRIMARY KEY (`groupid`),
  UNIQUE KEY `usergroup` (`username`,`groupname`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `member_users` */

DROP TABLE IF EXISTS `member_users`;

CREATE TABLE `member_users` (
  `groupid` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`groupid`,`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `owned_entities` */

DROP TABLE IF EXISTS `owned_entities`;

CREATE TABLE `owned_entities` (
  `owner` varchar(45) DEFAULT NULL,
  `entityid` int(11) DEFAULT NULL,
  `mostsigbits` bigint(20) DEFAULT NULL,
  `leastsigbits` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `player_failedlogin` */

DROP TABLE IF EXISTS `player_failedlogin`;

CREATE TABLE `player_failedlogin` (
  `logid` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(60) NOT NULL,
  `ip` varchar(60) NOT NULL,
  `logdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`logid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `player_inventory` */

DROP TABLE IF EXISTS `player_inventory`;

CREATE TABLE `player_inventory` (
  `inventoryid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `inventorytype` int(11) NOT NULL,
  `level` int(11) NOT NULL DEFAULT '-1',
  `experience` float NOT NULL DEFAULT '-1',
  `bagid` bigint(20) DEFAULT NULL,
  `health` double NOT NULL DEFAULT '20',
  `hunger` int(11) NOT NULL DEFAULT '20',
  `potioneffects` varchar(160) DEFAULT NULL,
  PRIMARY KEY (`inventoryid`,`health`),
  UNIQUE KEY `name_type_UNIQUE` (`name`,`inventorytype`)
) ENGINE=MyISAM AUTO_INCREMENT=55 DEFAULT CHARSET=latin1;

/*Table structure for table `player_password` */

DROP TABLE IF EXISTS `player_password`;

CREATE TABLE `player_password` (
  `username` varchar(60) NOT NULL,
  `password` blob NOT NULL,
  `dateset` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `player_permission` */

DROP TABLE IF EXISTS `player_permission`;

CREATE TABLE `player_permission` (
  `name` varchar(50) NOT NULL,
  `permissionid` int(11) NOT NULL,
  `disabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`,`permissionid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `playermail` */

DROP TABLE IF EXISTS `playermail`;

CREATE TABLE `playermail` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `datecreated` date DEFAULT NULL,
  `readmail` tinyint(1) DEFAULT NULL,
  `toplayer` varchar(16) DEFAULT NULL,
  `fromplayer` varchar(16) DEFAULT NULL,
  `title` varchar(30) DEFAULT NULL,
  `content` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Table structure for table `players` */

DROP TABLE IF EXISTS `players`;

CREATE TABLE `players` (
  `name` varchar(60) NOT NULL,
  `lastlogin` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `onlinetime` float NOT NULL DEFAULT '0',
  `pvppoints` int(11) NOT NULL DEFAULT '0',
  `referralpoints` int(11) NOT NULL DEFAULT '0',
  `votepoints` int(11) NOT NULL DEFAULT '0',
  `balance` int(11) NOT NULL DEFAULT '0',
  `bank` int(11) NOT NULL DEFAULT '0',
  `settings` bigint(20) NOT NULL DEFAULT '0',
  `timezone` varchar(30) DEFAULT NULL,
  `playergroup` int(11) NOT NULL DEFAULT '-1',
  `namecolour` varchar(10) NOT NULL DEFAULT 'x' COMMENT 'Hexvalue',
  `backpack` bigint(20) DEFAULT NULL,
  `ignorelist` text,
  PRIMARY KEY (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `poll_answers` */

DROP TABLE IF EXISTS `poll_answers`;

CREATE TABLE `poll_answers` (
  `ID` int(11) NOT NULL DEFAULT '0',
  `username` varchar(60) NOT NULL DEFAULT '',
  `answer` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`,`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `poll_list` */

DROP TABLE IF EXISTS `poll_list`;

CREATE TABLE `poll_list` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `question` varchar(100) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '0',
  `multipleChoice` tinyint(1) DEFAULT '0',
  `minUserGroup` int(11) DEFAULT '8',
  `reward` bigint(20) DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `poll_options` */

DROP TABLE IF EXISTS `poll_options`;

CREATE TABLE `poll_options` (
  `ID` int(11) NOT NULL,
  `optionchoice` varchar(45) NOT NULL DEFAULT '',
  `answer` varchar(20) DEFAULT NULL,
  `correct` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`ID`,`optionchoice`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `prefix` */

DROP TABLE IF EXISTS `prefix`;

CREATE TABLE `prefix` (
  `prefixid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `subid` int(11) NOT NULL,
  `text` varchar(14) DEFAULT NULL,
  `color1` varchar(10) DEFAULT NULL,
  `color2` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`prefixid`),
  UNIQUE KEY `prefix_UNIQUE` (`name`,`subid`)
) ENGINE=MyISAM AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;

/*Table structure for table `presents` */

DROP TABLE IF EXISTS `presents`;

CREATE TABLE `presents` (
  `presentid` int(11) NOT NULL AUTO_INCREMENT,
  `creator` varchar(60) NOT NULL,
  `title` varchar(100) NOT NULL,
  `bagid` bigint(20) NOT NULL,
  `opened` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`presentid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `pvp_kills` */

DROP TABLE IF EXISTS `pvp_kills`;

CREATE TABLE `pvp_kills` (
  `username` varchar(60) NOT NULL,
  `victim` varchar(60) NOT NULL,
  `time` datetime NOT NULL,
  KEY `username` (`username`),
  KEY `victim` (`victim`),
  KEY `time` (`time`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `pvp_leavers` */

DROP TABLE IF EXISTS `pvp_leavers`;

CREATE TABLE `pvp_leavers` (
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `pvp_opt` */

DROP TABLE IF EXISTS `pvp_opt`;

CREATE TABLE `pvp_opt` (
  `username` varchar(60) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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

/*Table structure for table `saved_inventory` */

DROP TABLE IF EXISTS `saved_inventory`;

CREATE TABLE `saved_inventory` (
  `username` varchar(60) NOT NULL,
  `slot` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `durability` int(11) NOT NULL,
  `data` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `staff_requests` */

DROP TABLE IF EXISTS `staff_requests`;

CREATE TABLE `staff_requests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `datecreated` date DEFAULT NULL,
  `hasread` tinyint(1) DEFAULT '0',
  `creator` varchar(15) DEFAULT NULL,
  `message` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `stored_inventory` */

DROP TABLE IF EXISTS `stored_inventory`;

CREATE TABLE `stored_inventory` (
  `idstored_inventory` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `inventory` blob NOT NULL,
  `converted` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idstored_inventory`),
  UNIQUE KEY `Unique` (`username`,`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `switches` */

DROP TABLE IF EXISTS `switches`;

CREATE TABLE `switches` (
  `switchid` int(11) NOT NULL AUTO_INCREMENT,
  `owner` varchar(50) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `world` varchar(50) NOT NULL,
  `flags` bigint(20) NOT NULL,
  PRIMARY KEY (`switchid`),
  KEY `location` (`locx`,`locy`,`locz`,`world`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `switches_links` */

DROP TABLE IF EXISTS `switches_links`;

CREATE TABLE `switches_links` (
  `switchA` int(11) NOT NULL,
  `switchB` int(11) NOT NULL,
  PRIMARY KEY (`switchA`,`switchB`),
  KEY `switchA` (`switchA`),
  KEY `switchB` (`switchB`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tmp` */

DROP TABLE IF EXISTS `tmp`;

CREATE TABLE `tmp` (
  `lotid` int(11) NOT NULL,
  PRIMARY KEY (`lotid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `trapdoors` */

DROP TABLE IF EXISTS `trapdoors`;

CREATE TABLE `trapdoors` (
  `trapdoorid` int(11) NOT NULL AUTO_INCREMENT,
  `owner` varchar(60) NOT NULL,
  `world` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `flags` bigint(20) NOT NULL,
  PRIMARY KEY (`trapdoorid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `trapdoors_members` */

DROP TABLE IF EXISTS `trapdoors_members`;

CREATE TABLE `trapdoors_members` (
  `trapdoorid` int(11) NOT NULL,
  `username` varchar(60) NOT NULL,
  `isop` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
  `username` varchar(60) DEFAULT NULL,
  `ip` varchar(60) DEFAULT NULL,
  `service` varchar(60) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `username` (`username`)
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
  `hidden` int(2) NOT NULL DEFAULT '0',
  `yaw` int(11) NOT NULL DEFAULT '0',
  `comment` varchar(2000) NOT NULL,
  PRIMARY KEY (`idwarps`),
  UNIQUE KEY `unique` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;

/*Table structure for table `waypoints` */

DROP TABLE IF EXISTS `waypoints`;

CREATE TABLE `waypoints` (
  `waypointid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `owner` varchar(60) NOT NULL,
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
  PRIMARY KEY (`waypointid`),
  UNIQUE KEY `loc` (`world`,`locx`,`locy`,`locz`)
) ENGINE=MyISAM AUTO_INCREMENT=33 DEFAULT CHARSET=latin1;

/*Table structure for table `waypoints_members` */

DROP TABLE IF EXISTS `waypoints_members`;

CREATE TABLE `waypoints_members` (
  `waypointid` int(11) unsigned NOT NULL,
  `username` varchar(60) NOT NULL,
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

/*Table structure for table `non-goldy-prefixes` */

DROP TABLE IF EXISTS `non-goldy-prefixes`;

/*!50001 DROP VIEW IF EXISTS `non-goldy-prefixes` */;
/*!50001 DROP TABLE IF EXISTS `non-goldy-prefixes` */;

/*!50001 CREATE TABLE  `non-goldy-prefixes`(
 `prefixid` int(11) ,
 `name` varchar(45) ,
 `subid` int(11) ,
 `text` varchar(14) ,
 `color1` varchar(10) ,
 `color2` varchar(10) 
)*/;

/*View structure for view non-goldy-prefixes */

/*!50001 DROP TABLE IF EXISTS `non-goldy-prefixes` */;
/*!50001 DROP VIEW IF EXISTS `non-goldy-prefixes` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `non-goldy-prefixes` AS (select `prefix`.`prefixid` AS `prefixid`,`prefix`.`name` AS `name`,`prefix`.`subid` AS `subid`,`prefix`.`text` AS `text`,`prefix`.`color1` AS `color1`,`prefix`.`color2` AS `color2` from (`prefix` join `players` on((`prefix`.`name` = `players`.`name`))) where (`players`.`playergroup` > 6)) */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
