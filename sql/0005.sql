INSERT INTO `version` (`name`,`version`) VALUES ('database', 5) ON DUPLICATE KEY UPDATE `version`=5;

ALTER TABLE `doors` 
	ADD COLUMN `locked` tinyint(1)   NOT NULL DEFAULT '0' after `owner`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `locked`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, COMMENT='';


CREATE TABLE `doors_members`(
	`doorid` int(11) unsigned NOT NULL  , 
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	PRIMARY KEY (`doorid`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';
