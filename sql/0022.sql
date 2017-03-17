INSERT INTO `version` (`name`,`version`) VALUES ('database', 22) ON DUPLICATE KEY UPDATE `version`=22;

ALTER TABLE `players` 
	ADD COLUMN `onlinetime` int(11)   NOT NULL after `lastlogin`, COMMENT='';