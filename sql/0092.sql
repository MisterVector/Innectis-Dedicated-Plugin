INSERT INTO version (name,version) VALUES ('database', 92) ON DUPLICATE KEY UPDATE version=92;


ALTER TABLE `players` 
	ADD COLUMN `showdeathmsg` tinyint(4)   NULL DEFAULT '1' after `shopnotification`, COMMENT='';