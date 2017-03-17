INSERT INTO version (name,version) VALUES ('database', 69) ON DUPLICATE KEY UPDATE version=69;

ALTER TABLE `players` 
	ADD COLUMN `starvation` tinyint(1)   NOT NULL DEFAULT '1' after `namecolour`, COMMENT='';
UPDATE `players` SET `starvation` = '0' WHERE `playergroup` < '6';