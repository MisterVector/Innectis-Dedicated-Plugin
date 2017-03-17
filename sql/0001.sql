ALTER TABLE `chests` 
	CHANGE `chestid` `chestid` int(11) unsigned   NOT NULL auto_increment first, 
	CHANGE `owner` `owner` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `chestid`, 
	ADD COLUMN `locked` tinyint(1)   NOT NULL DEFAULT 1 after `owner`, 
	CHANGE `locx` `locx1` int(11)   NOT NULL after `locked`, 
	CHANGE `locy` `locy1` int(11)   NOT NULL after `locx1`, 
	CHANGE `locz` `locz1` int(11)   NOT NULL after `locy1`, 
	ADD COLUMN `locx2` int(11)   NOT NULL after `locz1`, 
	ADD COLUMN `locy2` int(11)   NOT NULL after `locx2`, 
	ADD COLUMN `locz2` int(11)   NOT NULL after `locy2`, 
	DROP COLUMN `upgraded`, 
	ADD KEY `locX1`(`locx1`), 
	ADD KEY `locX2`(`locx2`), 
	ADD KEY `locY1`(`locy1`), 
	ADD KEY `locY2`(`locy2`), 
	ADD KEY `locZ1`(`locz1`), 
	ADD KEY `locZ2`(`locz2`), COMMENT='';

CREATE TABLE `doors`(
	`doorid` int(11) unsigned NOT NULL  auto_increment , 
	`owner` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`locx` int(11) NOT NULL  , 
	`locy` int(11) NOT NULL  , 
	`locz` int(11) NOT NULL  , 
	PRIMARY KEY (`doorid`) , 
	KEY `locX`(`locx`) , 
	KEY `locY`(`locy`) , 
	KEY `locZ`(`locz`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';
