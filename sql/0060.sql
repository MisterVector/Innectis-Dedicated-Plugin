INSERT INTO version (name,version) VALUES ('database', 60) ON DUPLICATE KEY UPDATE version=60;

ALTER TABLE `chests` 
	DROP KEY `coord1`, add UNIQUE KEY `coord1`(`world`,`locx1`,`locy1`,`locz1`), 
	DROP KEY `coord2`, add KEY `coord2`(`world`,`locx2`,`locy2`,`locz2`), 
	DROP KEY `world`;

ALTER TABLE `doors` 
	CHANGE `world` `world` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `owner`, 
	CHANGE `locx` `locx1` int(11) NOT NULL after `world`, 
	CHANGE `locy` `locy1` int(11) NOT NULL after `locx1`, 
	CHANGE `locz` `locz1` int(11) NOT NULL after `locy1`, 
	ADD COLUMN `locx2` int(11)   NOT NULL after `locz1`, 
	ADD COLUMN `locy2` int(11)   NOT NULL after `locx2`, 
	ADD COLUMN `locz2` int(11)   NOT NULL after `locy2`, 
	CHANGE `flags` `flags` int(11)   NOT NULL after `locz2`, 
	DROP COLUMN `locked`, 
	ADD KEY `coord1`(`world`,`locx1`,`locy1`,`locz1`), 
	ADD KEY `coord2`(`world`,`locx2`,`locy2`,`locz2`), 
	DROP KEY `coords`;

UPDATE chests LEFT JOIN chests_members ON chests.chestid = chests_members.chestid SET chests.flags=chests.flags|1 WHERE chests_members.username='#';