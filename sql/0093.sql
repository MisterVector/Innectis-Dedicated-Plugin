INSERT INTO version (name,version) VALUES ('database', 93) ON DUPLICATE KEY UPDATE version=93;

ALTER TABLE `players` 
	ADD COLUMN `backpack` varchar(255)  COLLATE latin1_swedish_ci NOT NULL after `showdeathmsg`, COMMENT='';