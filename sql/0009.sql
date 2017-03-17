INSERT INTO `version` (`name`,`version`) VALUES ('database', 9) ON DUPLICATE KEY UPDATE `version`=9;

ALTER TABLE `lots` 
	CHANGE `lotid` `lotid` int(11)   NOT NULL auto_increment first, 
	CHANGE `owner` `owner` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `lotid`, 
	CHANGE `lotnr` `lotnr` int(11)   NOT NULL after `owner`, 
	CHANGE `x1` `x1` int(11)   NOT NULL after `lotnr`, 
	CHANGE `x2` `x2` int(11)   NOT NULL after `x1`, 
	CHANGE `z1` `z1` int(11)   NOT NULL after `x2`, 
	CHANGE `z2` `z2` int(11)   NOT NULL after `z1`, 
	CHANGE `sx` `sx` int(11)   NOT NULL after `z2`, 
	CHANGE `sy` `sy` int(11)   NOT NULL after `sx`, 
	CHANGE `sz` `sz` int(11)   NOT NULL after `sy`, 
	CHANGE `yaw` `yaw` int(11)   NOT NULL DEFAULT '0' after `sz`, 
	DROP COLUMN `flag`,
	ADD COLUMN `flags` int(11)  NOT NULL DEFAULT '0' after `yaw`, 
	COMMENT='';