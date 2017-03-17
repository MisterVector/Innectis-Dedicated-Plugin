-- MySQL dump 10.11
--
-- Host: localhost    Database: otakucraft
-- ------------------------------------------------------
-- Server version	5.0.77

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bankaccounts`
--

DROP TABLE IF EXISTS `bankaccounts`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `bankaccounts` (
  `accountid` int(11) NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `balance` float NOT NULL default '1000',
  `bankpin` varchar(14) default NULL,
  PRIMARY KEY  (`accountid`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `banktransferlogs`
--

DROP TABLE IF EXISTS `banktransferlogs`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `banktransferlogs` (
  `transferid` int(11) NOT NULL auto_increment,
  `fromid` int(11) NOT NULL,
  `toid` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`transferid`)
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `blacklist_events`
--

DROP TABLE IF EXISTS `blacklist_events`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `blacklist_events` (
  `id` int(11) NOT NULL auto_increment,
  `event` varchar(25) NOT NULL,
  `player` varchar(16) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  `item` int(11) NOT NULL,
  `time` int(11) NOT NULL,
  `comment` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `blocklog`
--

DROP TABLE IF EXISTS `blocklog`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `blocklog` (
  `blocklogid` int(11) NOT NULL auto_increment,
  `user` varchar(60) NOT NULL,
  `blockx` int(11) NOT NULL,
  `blocky` int(11) NOT NULL,
  `blockz` int(11) NOT NULL,
  `action` int(11) NOT NULL,
  `blocktype` int(11) NOT NULL,
  `date` timestamp NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`blocklogid`)
) ENGINE=InnoDB AUTO_INCREMENT=506 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

-- Table structure for table `chatlog`
--

DROP TABLE IF EXISTS `chatlog`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `chatlog` (
  `chatid` int(11) NOT NULL auto_increment,
  `username` varchar(45) NOT NULL,
  `message` text NOT NULL,
  `date` timestamp NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`chatid`)
) ENGINE=InnoDB AUTO_INCREMENT=2986 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

-- Table structure for table `chestlog`
--

DROP TABLE IF EXISTS `chestlog`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `chestlog` (
  `logid` int(11) NOT NULL auto_increment,
  `chestx` int(11) NOT NULL,
  `chesty` int(11) NOT NULL,
  `chestz` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `date` timestamp NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`logid`)
) ENGINE=InnoDB AUTO_INCREMENT=76764 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `chests`
--

DROP TABLE IF EXISTS `chests`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `chests` (
  `chestid` int(11) NOT NULL auto_increment,
  `owner` varchar(60) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `upgraded` int(2) NOT NULL default '0',
  PRIMARY KEY  (`chestid`)
) ENGINE=InnoDB AUTO_INCREMENT=15054 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `homes`
--

DROP TABLE IF EXISTS `homes`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `homes` (
  `name` varchar(45) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `yaw` int(11) NOT NULL,
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `item_inbox`
--

DROP TABLE IF EXISTS `item_inbox`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `item_inbox` (
  `inboxid` int(11) NOT NULL auto_increment,
  `accountid` int(11) NOT NULL,
  `itemid` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `damage` int(11) NOT NULL,
  `byte` int(11) NOT NULL,
  PRIMARY KEY  (`inboxid`)
) ENGINE=MyISAM AUTO_INCREMENT=616 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `kickedplayers`
--

DROP TABLE IF EXISTS `kickedplayers`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `kickedplayers` (
  `idkickedplayers` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `till` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`idkickedplayers`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `log`
--

DROP TABLE IF EXISTS `log`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `log` (
  `logid` int(11) NOT NULL auto_increment,
  `message` longtext NOT NULL,
  `date` timestamp NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`logid`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `lot_members`
--

DROP TABLE IF EXISTS `lot_members`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `lot_members` (
  `lotid` int(11) NOT NULL,
  `username` varchar(60) NOT NULL,
  UNIQUE KEY `primairy` (`lotid`,`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `lot_requests`
--

DROP TABLE IF EXISTS `lot_requests`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `lot_requests` (
  `requestid` int(11) NOT NULL auto_increment,
  `done` int(11) NOT NULL,
  `name` varchar(60) NOT NULL,
  PRIMARY KEY  (`requestid`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `lots`
--

DROP TABLE IF EXISTS `lots`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `lots` (
  `lotid` int(11) NOT NULL auto_increment,
  `owner` varchar(60) NOT NULL,
  `lotnr` int(11) NOT NULL,
  `x1` int(11) NOT NULL,
  `x2` int(11) NOT NULL,
  `z1` int(11) NOT NULL,
  `z2` int(11) NOT NULL,
  `sx` int(11) NOT NULL,
  `sy` int(11) NOT NULL,
  `sz` int(11) NOT NULL,
  `yaw` int(11) NOT NULL default '0',
  `flag` varchar(45) NOT NULL default '0000',
  PRIMARY KEY  (`lotid`),
  UNIQUE KEY `lotnumbers` (`owner`,`lotnr`)
) ENGINE=InnoDB AUTO_INCREMENT=380 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `npcs`
--

DROP TABLE IF EXISTS `npcs`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `npcs` (
  `idnpcs` int(11) NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `locyaw` int(11) NOT NULL,
  PRIMARY KEY  (`idnpcs`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `players`
--

DROP TABLE IF EXISTS `players`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `players` (
  `name` varchar(45) NOT NULL,
  `lastlogin` timestamp NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


-- Table structure for table `pvp_leavers`
--

DROP TABLE IF EXISTS `pvp_leavers`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pvp_leavers` (
  `username` varchar(50) NOT NULL,
  PRIMARY KEY  (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `sales`
--

DROP TABLE IF EXISTS `sales`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `sales` (
  `sid` int(11) NOT NULL auto_increment,
  `sellerid` int(11) NOT NULL,
  `item` int(11) NOT NULL,
  `amount` float NOT NULL,
  `price` int(11) NOT NULL,
  `buyer` int(11) NOT NULL default '-1',
  `date` timestamp NULL default CURRENT_TIMESTAMP,
  `damage` int(4) NOT NULL default '0',
  `byte` varchar(4) NOT NULL default '0',
  PRIMARY KEY  (`sid`)
) ENGINE=InnoDB AUTO_INCREMENT=1670 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `sdbox`
--

DROP TABLE IF EXISTS `sdbox`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `sdbox` (
  `idsdbox` int(11) NOT NULL auto_increment,
  `chestloc` int(3) NOT NULL,
  `owner` varchar(45) NOT NULL,
  `itemid` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `damage` int(11) NOT NULL,
  PRIMARY KEY  (`idsdbox`,`chestloc`)
) ENGINE=MyISAM AUTO_INCREMENT=912 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `sdbpayment`
--

DROP TABLE IF EXISTS `sdbpayment`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `sdbpayment` (
  `idsdbpayment` int(11) NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY  (`idsdbpayment`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `warps`
--

DROP TABLE IF EXISTS `warps`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `warps` (
  `idwarps` int(11) NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `locx` int(11) NOT NULL,
  `locy` int(11) NOT NULL,
  `locz` int(11) NOT NULL,
  `hidden` int(2) NOT NULL default '0',
  `yaw` int(11) default '0',
  PRIMARY KEY  (`idwarps`),
  UNIQUE KEY `unique` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-04-22 23:57:41
