INSERT INTO `version` (`name`,`version`) VALUES ('database', 6) ON DUPLICATE KEY UPDATE `version`=6;

ALTER TABLE `lots` 
	ADD KEY `lotnr`(`lotnr`), 
	ADD KEY `owner`(`owner`), COMMENT='';