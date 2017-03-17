ALTER TABLE `players` 
	ADD COLUMN `stick_size` double   NOT NULL DEFAULT '1' after `refer_id`, COMMENT='';

INSERT INTO version (name,version) VALUES ('database', 171) ON DUPLICATE KEY UPDATE version = 171;