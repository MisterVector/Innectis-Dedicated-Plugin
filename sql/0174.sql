ALTER TABLE `players` 
	ADD COLUMN `chat_sound_settings` double   NOT NULL DEFAULT '0' after `settings`, 
	CHANGE `timezone` `timezone` varchar(90)  COLLATE latin1_swedish_ci NULL after `chat_sound_settings`, 
	CHANGE `playergroup` `playergroup` double   NOT NULL DEFAULT '-1' after `timezone`, 
	CHANGE `namecolour` `namecolour` varchar(30)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'x' after `playergroup`, 
	CHANGE `backpack` `backpack` double   NULL after `namecolour`, 
	CHANGE `last_version` `last_version` varchar(45)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'unknown' after `backpack`, 
	CHANGE `refer_bonus` `refer_bonus` double   NOT NULL DEFAULT '0' after `last_version`, 
	CHANGE `refer_type` `refer_type` double   NOT NULL DEFAULT '0' after `refer_bonus`, 
	CHANGE `refer_id` `refer_id` varchar(180)  COLLATE latin1_swedish_ci NULL after `refer_type`, 
	CHANGE `stick_size` `stick_size` double   NOT NULL DEFAULT '1' after `refer_id`, COMMENT='';
INSERT INTO version (name,version) VALUES ('database', 174) ON DUPLICATE KEY UPDATE version = 174;