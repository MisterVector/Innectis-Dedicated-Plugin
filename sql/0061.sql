INSERT INTO version (name,version) VALUES ('database', 61) ON DUPLICATE KEY UPDATE version=61;

ALTER TABLE `lots` 
	CHANGE `hidden` `hidden` tinyint(1)   NOT NULL DEFAULT '0' after `lastedit`, 
	ADD COLUMN `deleted` tinyint(1)   NOT NULL DEFAULT '0' after `hidden`, COMMENT='';