INSERT INTO `version` (`name`,`version`) VALUES ('database', 27) ON DUPLICATE KEY UPDATE `version`=27;

ALTER TABLE `players` 
	CHANGE `name` `name` varchar(60)  COLLATE latin1_swedish_ci NOT NULL first, 
	CHANGE `lastlogin` `lastlogin` timestamp   NULL DEFAULT CURRENT_TIMESTAMP after `name`, 
	CHANGE `onlinetime` `onlinetime` int(11)   NOT NULL DEFAULT '0' after `lastlogin`, COMMENT='';

ALTER TABLE `warps` 
	CHANGE `yaw` `yaw` int(11)   NOT NULL DEFAULT '0' after `hidden`, 
	ADD COLUMN `comment` varchar(2000)  COLLATE latin1_swedish_ci NOT NULL after `yaw`, COMMENT='';
