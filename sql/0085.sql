INSERT INTO version (name,version) VALUES ('database', 85) ON DUPLICATE KEY UPDATE version=85;

ALTER TABLE `playermail` 
	CHANGE `readmail` `readmail` tinyint(1)   NOT NULL after `content`, COMMENT='';