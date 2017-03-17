ALTER TABLE `player_inventory` 
	CHANGE `potioneffects` `potioneffects` varchar(160)  COLLATE latin1_swedish_ci NULL after `hunger`, COMMENT='';

INSERT INTO version (name,version) VALUES ('database', 143) ON DUPLICATE KEY UPDATE version = 143;