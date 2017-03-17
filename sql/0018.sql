INSERT INTO `version` (`name`,`version`) VALUES ('database', 18) ON DUPLICATE KEY UPDATE `version`=18;

ALTER TABLE `block_breaks` 
	ADD COLUMN `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `blockid`, 
	CHANGE `x` `x` int(11)   NOT NULL after `world`, 
	CHANGE `y` `y` int(11)   NOT NULL after `x`, 
	CHANGE `z` `z` int(11)   NOT NULL after `y`, 
	DROP KEY `coords`, add UNIQUE KEY `coords`(`world`,`x`,`y`,`z`), COMMENT='';

UPDATE block_breaks SET world='world' WHERE world='';

ALTER TABLE `chests` 
	ADD COLUMN `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `locked`, 
	CHANGE `locx1` `locx1` int(11)   NOT NULL after `world`, 
	CHANGE `locy1` `locy1` int(11)   NOT NULL after `locx1`, 
	CHANGE `locz1` `locz1` int(11)   NOT NULL after `locy1`, 
	CHANGE `locx2` `locx2` int(11)   NOT NULL after `locz1`, 
	CHANGE `locy2` `locy2` int(11)   NOT NULL after `locx2`, 
	CHANGE `locz2` `locz2` int(11)   NOT NULL after `locy2`, 
	ADD KEY `world`(`world`), COMMENT='';

UPDATE chests SET world='world' WHERE world='';

ALTER TABLE `doors` 
	ADD COLUMN `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `locked`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	DROP KEY `coords`, add UNIQUE KEY `coords`(`world`,`locx`,`locy`,`locz`), COMMENT='';

UPDATE doors SET world='world' WHERE world='';

ALTER TABLE `homes` 
	CHANGE `name` `name` varchar(60)  COLLATE latin1_swedish_ci NOT NULL first, 
	ADD COLUMN `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `name`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `yaw` `yaw` int(11)   NOT NULL after `locz`, 
	DROP KEY `coords`, add UNIQUE KEY `coords`(`world`,`locx`,`locy`,`locz`), COMMENT='';

UPDATE homes SET world='world' WHERE world='';

ALTER TABLE `lots` 
	ADD COLUMN `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `lotnr`, 
	CHANGE `x1` `x1` int(11)   NOT NULL after `world`, 
	CHANGE `x2` `x2` int(11)   NOT NULL after `x1`, 
	CHANGE `z1` `z1` int(11)   NOT NULL after `x2`, 
	CHANGE `z2` `z2` int(11)   NOT NULL after `z1`, 
	CHANGE `sx` `sx` int(11)   NOT NULL after `z2`, 
	CHANGE `sy` `sy` int(11)   NOT NULL after `sx`, 
	CHANGE `sz` `sz` int(11)   NOT NULL after `sy`, 
	CHANGE `yaw` `yaw` int(11)   NOT NULL DEFAULT '0' after `sz`, 
	CHANGE `flags` `flags` int(11)   NOT NULL DEFAULT '0' after `yaw`, 
	DROP KEY `coords`, add UNIQUE KEY `coords`(`world`,`x1`,`x2`,`z1`,`z2`), COMMENT='';

UPDATE lots SET world='world' WHERE world='';

ALTER TABLE `npcs` 
	CHANGE `name` `name` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `idnpcs`, 
	ADD COLUMN `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `name`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `locyaw` `locyaw` int(11)   NOT NULL after `locz`, 
	DROP KEY `coords`, add UNIQUE KEY `coords`(`world`,`locx`,`locy`,`locz`), COMMENT='';

UPDATE npcs SET world='world' WHERE world='';

ALTER TABLE `warps` 
	CHANGE `name` `name` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `idwarps`, 
	ADD COLUMN `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `name`, 
	CHANGE `locx` `locx` int(11)   NOT NULL after `world`, 
	CHANGE `locy` `locy` int(11)   NOT NULL after `locx`, 
	CHANGE `locz` `locz` int(11)   NOT NULL after `locy`, 
	CHANGE `hidden` `hidden` int(2)   NOT NULL DEFAULT '0' after `locz`, 
	CHANGE `yaw` `yaw` int(11)   NULL DEFAULT '0' after `hidden`, COMMENT='';

UPDATE warps SET world='world' WHERE world='';