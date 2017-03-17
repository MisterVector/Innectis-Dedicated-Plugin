INSERT INTO version (name,version) VALUES ('database', 99) ON DUPLICATE KEY UPDATE version=99;

ALTER TABLE `players` 
	ADD COLUMN `ignorelist` text  COLLATE latin1_swedish_ci NULL after `backpack`, COMMENT='';
