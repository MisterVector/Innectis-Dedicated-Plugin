/*
SQLyog Enterprise - MySQL GUI v7.02 
MySQL - 5.5.11 : Database - otakucraft
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

/*Table structure for table `bankaccounts` */

DROP TABLE IF EXISTS `bankaccounts`;

CREATE TABLE `bankaccounts` (
  `accountid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `balance` float NOT NULL DEFAULT '1000',
  `bankpin` varchar(14) DEFAULT NULL,
  PRIMARY KEY (`accountid`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=latin1;

/*Table structure for table `banktransferlogs` */

DROP TABLE IF EXISTS `banktransferlogs`;

CREATE TABLE `banktransferlogs` (
  `transferid` int(11) NOT NULL AUTO_INCREMENT,
  `fromid` int(11) NOT NULL,
  `toid` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`transferid`)
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=latin1;

/*Table structure for table `blacklist_events` */

DROP TABLE IF EXISTS `blacklist_events`;

CREATE TABLE `blacklist_events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `event` varchar(25) NOT NULL,
  `player` varchar(16) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  `item` int(11) NOT NULL,
  `time` int(11) NOT NULL,
  `comment` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `blocklog` */

DROP TABLE IF EXISTS `blocklog`;

CREATE TABLE `blocklog` (
  `blocklogid` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(60) NOT NULL,
  `blockx` int(11) NOT NULL,
  `blocky` int(11) NOT NULL,
  `blockz` int(11) NOT NULL,
  `action` int(11) NOT NULL,
  `blocktype` int(11) NOT NULL,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`blocklogid`)
) ENGINE=InnoDB AUTO_INCREMENT=506 DEFAULT CHARSET=latin1;

/*Table structure for table `chatlog` */

DROP TABLE IF EXISTS `chatlog`;

CREATE TABLE `chatlog` (
  `chatid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `message` text NOT NULL,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`chatid`),
  KEY `username` (`username`),
  KEY `date` (`date`)
) ENGINE=InnoDB AUTO_INCREMENT=2986 DEFAULT CHARSET=latin1;

/*Table structure for table `chestlog` */

DROP TABLE IF EXISTS `chestlog`;

CREATE TABLE `chestlog` (
  `logid` int(11) NOT NULL AUTO_INCREMENT,
  `chestx` int(11) NOT NULL,
  `chesty` int(11) NOT NULL,
  `chestz` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`logid`),
  KEY `name` (`name`),
  KEY `date` (`date`),
  KEY `coords` (`chestx`,`chesty`,`chestz`)
) ENGINE=InnoDB AUTO_INCREMENT=76855 DEFAULT CHARSET=latin1;

/*Table structure for table `chests` */

DROP TABLE IF EXISTS `chests`;

CREATE TABLE `chests` (
  `chestid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `owner` varchar(60) NOT NULL,
  `locked` tinyint(1) NOT NULL DEFAULT '1',
  `locx1` int(11) NOT NULL,
  `locy1` int(11) NOT NULL,
  `locz1` int(11) NOT NULL,
  `locx2` int(11) NOT NULL,
  `locy2` int(11) NOT NULL,
  `locz2` int(11) NOT NULL,
  PRIMARY KEY (`chestid`),
  UNIQUE KEY `coord1` (`locx1`,`locy1`,`locz1`),
  KEY `coord2` (`locx2`,`locy2`,`locz2`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

/*Table structure for table `chests_members` */

DROP TABLE IF EXISTS `chests_members`;

CREATE TABLE `chests_members` (
  `chestid` int(11) unsigned NOT NULL,
  `username` varchar(60) NOT NULL,
  PRIMARY KEY (`chestid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `doors` */

DROP TABLE IF EXISTS `doors`;

CREATE TABLE `doors` (
  `doorid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `owner` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  PRIMARY KEY (`doorid`),
  UNIQUE KEY `coords` (`locx`,`locy`,`locz`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=latin1;

/*Table structure for table `homes` */

DROP TABLE IF EXISTS `homes`;

CREATE TABLE `homes` (
  `name` varchar(45) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `yaw` int(11) NOT NULL,
  PRIMARY KEY (`name`),
  UNIQUE KEY `coords` (`locx`,`locy`,`locz`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `item_inbox` */

DROP TABLE IF EXISTS `item_inbox`;

CREATE TABLE `item_inbox` (
  `inboxid` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL,
  `itemid` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `damage` int(11) NOT NULL,
  `byte` int(11) NOT NULL,
  PRIMARY KEY (`inboxid`)
) ENGINE=MyISAM AUTO_INCREMENT=616 DEFAULT CHARSET=latin1;

/*Table structure for table `kickedplayers` */

DROP TABLE IF EXISTS `kickedplayers`;

CREATE TABLE `kickedplayers` (
  `idkickedplayers` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `till` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`idkickedplayers`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `log` */

DROP TABLE IF EXISTS `log`;

CREATE TABLE `log` (
  `logid` int(11) NOT NULL AUTO_INCREMENT,
  `message` longtext NOT NULL,
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`logid`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=latin1;

/*Table structure for table `lot_members` */

DROP TABLE IF EXISTS `lot_members`;

CREATE TABLE `lot_members` (
  `lotid` int(11) NOT NULL,
  `username` varchar(60) NOT NULL,
  UNIQUE KEY `primairy` (`lotid`,`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `lot_requests` */

DROP TABLE IF EXISTS `lot_requests`;

CREATE TABLE `lot_requests` (
  `requestid` int(11) NOT NULL AUTO_INCREMENT,
  `done` int(11) NOT NULL,
  `name` varchar(60) NOT NULL,
  PRIMARY KEY (`requestid`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=latin1;

/*Table structure for table `lots` */

DROP TABLE IF EXISTS `lots`;

CREATE TABLE `lots` (
  `lotid` int(11) NOT NULL AUTO_INCREMENT,
  `owner` varchar(60) NOT NULL,
  `lotnr` int(11) NOT NULL,
  `x1` int(11) NOT NULL,
  `x2` int(11) NOT NULL,
  `z1` int(11) NOT NULL,
  `z2` int(11) NOT NULL,
  `sx` int(11) NOT NULL,
  `sy` int(11) NOT NULL,
  `sz` int(11) NOT NULL,
  `yaw` int(11) NOT NULL DEFAULT '0',
  `flag` varchar(45) NOT NULL DEFAULT '0000',
  PRIMARY KEY (`lotid`),
  UNIQUE KEY `lotnumbers` (`owner`,`lotnr`),
  UNIQUE KEY `coords` (`x1`,`x2`,`z1`,`z2`)
) ENGINE=InnoDB AUTO_INCREMENT=381 DEFAULT CHARSET=latin1;

/*Table structure for table `npcs` */

DROP TABLE IF EXISTS `npcs`;

CREATE TABLE `npcs` (
  `idnpcs` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `locyaw` int(11) NOT NULL,
  PRIMARY KEY (`idnpcs`),
  UNIQUE KEY `coords` (`locx`,`locy`,`locz`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

/*Table structure for table `players` */

DROP TABLE IF EXISTS `players`;

CREATE TABLE `players` (
  `name` varchar(45) NOT NULL,
  `lastlogin` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `pvp_leavers` */

DROP TABLE IF EXISTS `pvp_leavers`;

CREATE TABLE `pvp_leavers` (
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `sales` */

DROP TABLE IF EXISTS `sales`;

CREATE TABLE `sales` (
  `sid` int(11) NOT NULL AUTO_INCREMENT,
  `sellerid` int(11) NOT NULL,
  `item` int(11) NOT NULL,
  `amount` float NOT NULL,
  `price` int(11) NOT NULL,
  `buyer` int(11) NOT NULL DEFAULT '-1',
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `damage` int(4) NOT NULL DEFAULT '0',
  `byte` varchar(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`sid`)
) ENGINE=InnoDB AUTO_INCREMENT=1670 DEFAULT CHARSET=latin1;

/*Table structure for table `sdbox` */

DROP TABLE IF EXISTS `sdbox`;

CREATE TABLE `sdbox` (
  `idsdbox` int(11) NOT NULL AUTO_INCREMENT,
  `chestloc` int(3) NOT NULL,
  `owner` varchar(45) NOT NULL,
  `itemid` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `damage` int(11) NOT NULL,
  PRIMARY KEY (`idsdbox`,`chestloc`)
) ENGINE=MyISAM AUTO_INCREMENT=912 DEFAULT CHARSET=latin1;

/*Table structure for table `sdbpayment` */

DROP TABLE IF EXISTS `sdbpayment`;

CREATE TABLE `sdbpayment` (
  `idsdbpayment` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`idsdbpayment`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

/*Table structure for table `version` */

DROP TABLE IF EXISTS `version`;

CREATE TABLE `version` (
  `name` varchar(60) NOT NULL,
  `version` int(10) unsigned NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `warps` */

DROP TABLE IF EXISTS `warps`;

CREATE TABLE `warps` (
  `idwarps` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `hidden` int(2) NOT NULL DEFAULT '0',
  `yaw` int(11) DEFAULT '0',
  PRIMARY KEY (`idwarps`),
  UNIQUE KEY `unique` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=latin1;

INSERT INTO `version` (`name`,`version`) VALUES ('database', 4) ON DUPLICATE KEY UPDATE `version`=4;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
