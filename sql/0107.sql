INSERT INTO version (name,version) VALUES ('database', 107) ON DUPLICATE KEY UPDATE version=107;

ALTER TABLE `playermail` 
	ADD COLUMN `datecreated` date   NULL after `id`, 
	CHANGE `readmail` `readmail` tinyint(1)   NULL after `datecreated`, 
	CHANGE `toplayer` `toplayer` varchar(16)  COLLATE utf8_general_ci NULL after `readmail`, 
	CHANGE `fromplayer` `fromplayer` varchar(16)  COLLATE utf8_general_ci NULL after `toplayer`, 
	CHANGE `title` `title` varchar(30)  COLLATE utf8_general_ci NULL after `fromplayer`, 
	CHANGE `content` `content` varchar(100)  COLLATE utf8_general_ci NULL after `title`, 
	DROP KEY `PRIMARY`, add PRIMARY KEY(`id`), COMMENT='';