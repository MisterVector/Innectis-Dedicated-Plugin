INSERT INTO version (name,version) VALUES ('database', 67) ON DUPLICATE KEY UPDATE version=67;

ALTER TABLE `player_inventory` 
	ADD COLUMN `level` int(11)   NOT NULL DEFAULT '-1' after `inventorytype`, 
	ADD COLUMN `experience` float   NOT NULL DEFAULT '-1' after `level`, 
	CHANGE `content` `content` longtext  COLLATE latin1_swedish_ci NOT NULL after `experience`, COMMENT='';