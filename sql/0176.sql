ALTER TABLE `players` 
	CHANGE `playergroup` `playergroup` double   NOT NULL DEFAULT '-1' after `chat_sound_settings`, 
	CHANGE `namecolour` `namecolour` varchar(30)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'x' after `playergroup`, 
	CHANGE `backpack` `backpack` double   NULL after `namecolour`, 
	CHANGE `last_version` `last_version` varchar(45)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'unknown' after `backpack`, 
	CHANGE `refer_bonus` `refer_bonus` double   NOT NULL DEFAULT '0' after `last_version`, 
	CHANGE `refer_type` `refer_type` double   NOT NULL DEFAULT '0' after `refer_bonus`, 
	CHANGE `refer_id` `refer_id` varchar(180)  COLLATE latin1_swedish_ci NULL after `refer_type`, 
	CHANGE `stick_size` `stick_size` double   NOT NULL DEFAULT '1' after `refer_id`, 
	DROP COLUMN `timezone`, COMMENT='';
INSERT INTO version (name,version) VALUES ('database', 176) ON DUPLICATE KEY UPDATE version = 176;