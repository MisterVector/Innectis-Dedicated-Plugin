INSERT INTO `version` (`name`,`version`) VALUES ('database', 20) ON DUPLICATE KEY UPDATE `version`=20;

CREATE TABLE `lot_banned`(
	`lotid` int(11) NOT NULL  , 
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	UNIQUE KEY `primairy`(`lotid`,`username`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';

ALTER TABLE `lots` 
	ADD COLUMN `parent` int(11)   NOT NULL after `flags`, 
	ADD COLUMN `creator` varchar(60)  COLLATE latin1_swedish_ci NOT NULL after `parent`, 
	ADD COLUMN `lastedit` int(11)   NOT NULL after `creator`, 
	ADD KEY `parent`(`parent`), COMMENT='';

UPDATE lots SET creator='Unknown' WHERE creator='';