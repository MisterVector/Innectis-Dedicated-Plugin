INSERT INTO version (name,version) VALUES ('database', 83) ON DUPLICATE KEY UPDATE version=83;

ALTER TABLE `banned_players` 
	ADD COLUMN `banned_time` timestamp   NULL after `end_time`, 
	ADD COLUMN `joinban` tinyint(1)   NULL DEFAULT '0' after `banned_time`, COMMENT='';