INSERT INTO version (name,version) VALUES ('database', 59) ON DUPLICATE KEY UPDATE version=59;

ALTER TABLE `chests` 
	CHANGE `owner` `owner` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `chestid`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner`, 
	CHANGE `locx1` `locx1` int(11)   NOT NULL after `world`, 
	CHANGE `locy1` `locy1` int(11)   NOT NULL after `locx1`, 
	CHANGE `locz1` `locz1` int(11)   NOT NULL after `locy1`, 
	CHANGE `locx2` `locx2` int(11)   NOT NULL after `locz1`, 
	CHANGE `locy2` `locy2` int(11)   NOT NULL after `locx2`, 
	CHANGE `locz2` `locz2` int(11)   NOT NULL after `locy2`, 
	ADD COLUMN `flags` int(11)   NOT NULL after `locz2`, COMMENT='';

/* Alter table in target */
ALTER TABLE `doors` 
	CHANGE `owner` `owner` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `doorid`, 
	CHANGE `locked` `locked` tinyint(1)   NOT NULL DEFAULT '0' after `owner`, 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `locked`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	ADD COLUMN `flags` int(11)   NOT NULL after `locz`, COMMENT='';