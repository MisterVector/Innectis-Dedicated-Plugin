INSERT INTO `version` (`name`,`version`) VALUES ('database', 34) ON DUPLICATE KEY UPDATE `version`=34;

ALTER TABLE `chests` 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner`, 
	CHANGE `locx1` `locx1` int(11)   NOT NULL after `world`, 
	CHANGE `locy1` `locy1` int(11)   NOT NULL after `locx1`, 
	CHANGE `locz1` `locz1` int(11)   NOT NULL after `locy1`, 
	CHANGE `locx2` `locx2` int(11)   NOT NULL after `locz1`, 
	CHANGE `locy2` `locy2` int(11)   NOT NULL after `locx2`, 
	CHANGE `locz2` `locz2` int(11)   NOT NULL after `locy2`, 
	DROP COLUMN `locked`, COMMENT='';

CREATE TABLE `item`(
	`itemid` int(11) NOT NULL  , 
	`data` int(11) NOT NULL  DEFAULT '0' , 
	`buyprice` int(11) NOT NULL  DEFAULT '0' , 
	`sellprice` int(11) NOT NULL  DEFAULT '0' , 
	`pointsonly` int(11) NOT NULL  DEFAULT '0' , 
	PRIMARY KEY (`itemid`,`data`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';
