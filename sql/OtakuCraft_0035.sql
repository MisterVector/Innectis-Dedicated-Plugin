/*
SQLyog Enterprise - MySQL GUI v7.02 
MySQL - 5.5.11 : Database - otakucraft
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

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
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

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
) ENGINE=MyISAM AUTO_INCREMENT=76965 DEFAULT CHARSET=latin1;

/*Table structure for table `chests` */

DROP TABLE IF EXISTS `chests`;

CREATE TABLE `chests` (
  `chestid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `owner` varchar(60) NOT NULL,
  `world` varchar(60) NOT NULL,
  `locx1` int(11) NOT NULL,
  `locy1` int(11) NOT NULL,
  `locz1` int(11) NOT NULL,
  `locx2` int(11) NOT NULL,
  `locy2` int(11) NOT NULL,
  `locz2` int(11) NOT NULL,
  PRIMARY KEY (`chestid`),
  UNIQUE KEY `coord1` (`locx1`,`locy1`,`locz1`),
  KEY `coord2` (`locx2`,`locy2`,`locz2`),
  KEY `world` (`world`)
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;

/*Table structure for table `chests_members` */

DROP TABLE IF EXISTS `chests_members`;

CREATE TABLE `chests_members` (
  `chestid` int(11) unsigned NOT NULL,
  `username` varchar(60) NOT NULL,
  KEY `chestid` (`chestid`)
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
  `locked` tinyint(1) NOT NULL DEFAULT '0',
  `world` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  PRIMARY KEY (`doorid`),
  UNIQUE KEY `coords` (`world`,`locx`,`locy`,`locz`)
) ENGINE=MyISAM AUTO_INCREMENT=64 DEFAULT CHARSET=latin1;

/*Table structure for table `doors_members` */

DROP TABLE IF EXISTS `doors_members`;

CREATE TABLE `doors_members` (
  `doorid` int(11) unsigned NOT NULL,
  `username` varchar(60) NOT NULL,
  KEY `doorid` (`doorid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `fort` */

DROP TABLE IF EXISTS `fort`;

CREATE TABLE `fort` (
  `pillarid` int(11) NOT NULL AUTO_INCREMENT,
  `fortid` int(11) NOT NULL,
  `world` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `radius` int(11) NOT NULL,
  PRIMARY KEY (`pillarid`),
  KEY `world` (`world`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `fort_members` */

DROP TABLE IF EXISTS `fort_members`;

CREATE TABLE `fort_members` (
  `memberid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(55) NOT NULL,
  `fortid` int(11) NOT NULL,
  `rank` int(11) NOT NULL,
  PRIMARY KEY (`memberid`)
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
  `username` varchar(60) NOT NULL,
  `world` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `yaw` int(11) NOT NULL,
  PRIMARY KEY (`username`),
  UNIQUE KEY `coords` (`world`,`locx`,`locy`,`locz`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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
  UNIQUE KEY `primairy` (`lotid`,`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `lot_members` */

DROP TABLE IF EXISTS `lot_members`;

CREATE TABLE `lot_members` (
  `lotid` int(11) NOT NULL,
  `username` varchar(60) NOT NULL,
  UNIQUE KEY `primairy` (`lotid`,`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `lot_messages` */

DROP TABLE IF EXISTS `lot_messages`;

CREATE TABLE `lot_messages` (
  `lotid` int(11) NOT NULL,
  `type` tinyint(4) NOT NULL,
  `message` varchar(255) NOT NULL,
  PRIMARY KEY (`lotid`,`type`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `lots` */

DROP TABLE IF EXISTS `lots`;

CREATE TABLE `lots` (
  `lotid` int(11) NOT NULL AUTO_INCREMENT,
  `owner` varchar(60) NOT NULL,
  `lotnr` int(11) NOT NULL,
  `world` varchar(60) NOT NULL,
  `x1` int(11) NOT NULL,
  `x2` int(11) NOT NULL,
  `z1` int(11) NOT NULL,
  `z2` int(11) NOT NULL,
  `sx` int(11) NOT NULL,
  `sy` int(11) NOT NULL,
  `sz` int(11) NOT NULL,
  `yaw` int(11) NOT NULL DEFAULT '0',
  `flags` int(11) NOT NULL DEFAULT '0',
  `creator` varchar(60) NOT NULL,
  `lastedit` bigint(20) NOT NULL,
  `hidden` tinyint(1) NOT NULL,
  PRIMARY KEY (`lotid`),
  UNIQUE KEY `lotnumbers` (`owner`,`lotnr`),
  UNIQUE KEY `coords` (`world`,`x1`,`x2`,`z1`,`z2`),
  KEY `lotnr` (`lotnr`),
  KEY `owner` (`owner`)
) ENGINE=MyISAM AUTO_INCREMENT=501 DEFAULT CHARSET=latin1;

/*Table structure for table `players` */

DROP TABLE IF EXISTS `players`;

CREATE TABLE `players` (
  `name` varchar(60) NOT NULL,
  `lastlogin` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `onlinetime` float NOT NULL DEFAULT '0',
  `pvppoints` int(11) NOT NULL DEFAULT '0',
  `referralpoints` int(11) NOT NULL,
  `balance` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`)
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

/*Table structure for table `version` */

DROP TABLE IF EXISTS `version`;

CREATE TABLE `version` (
  `name` varchar(60) NOT NULL,
  `version` int(10) unsigned NOT NULL,
  PRIMARY KEY (`name`)
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
  `tyaw` int(11) NOT NULL,
  PRIMARY KEY (`waypointid`),
  UNIQUE KEY `loc` (`world`,`locx`,`locy`,`locz`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

/* Procedure structure for procedure `convert_locked` */

/*!50003 DROP PROCEDURE IF EXISTS  `convert_locked` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `convert_locked`()
BEGIN
	DECLARE cid INT DEFAULT 0;
	DECLARE no_more_rows BOOLEAN DEFAULT FALSE;
	DECLARE cur1 CURSOR FOR
		SELECT chestid FROM chests WHERE locked = 1;
	DECLARE CONTINUE HANDLER FOR NOT FOUND
		SET no_more_rows = TRUE;
	
	OPEN cur1;
	the_loop: LOOP
		SET no_more_rows = FALSE;
		FETCH cur1 INTO cid;
		IF no_more_rows THEN
			LEAVE the_loop;
		END IF;
		INSERT INTO chests_members (chestid, username) VALUES (cid, "%");
	END LOOP the_loop;
	CLOSE cur1;
	
    END */$$
DELIMITER ;

INSERT INTO `version` (`name`,`version`) VALUES ('database', 35) ON DUPLICATE KEY UPDATE `version`=35;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
