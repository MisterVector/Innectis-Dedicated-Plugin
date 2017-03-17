ALTER TABLE `waypoints` 
	ADD COLUMN `forced` tinyint(4)   NULL after `flags`, COMMENT='';

INSERT INTO version (name,version) VALUES ('database', 151) ON DUPLICATE KEY UPDATE version = 151;