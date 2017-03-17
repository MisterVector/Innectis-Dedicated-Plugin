INSERT INTO `version` (`name`,`version`) VALUES ('database', 23) ON DUPLICATE KEY UPDATE `version`=23;

CREATE TABLE `block_quota`(
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`blockid` int(11) NOT NULL  , 
	`maxblocks` int(11) NOT NULL  , 
	`timespan` int(11) NOT NULL  , 
	PRIMARY KEY (`username`,`blockid`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';


CREATE TABLE `block_quota_log`(
	`time` datetime NOT NULL  , 
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`blockid` int(11) NOT NULL  , 
	KEY `idx`(`time`,`username`,`blockid`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';

INSERT INTO block_quota (`username`, `blockid`, `maxblocks`, `timespan`) VALUES ('%', 56, 16, 3600);
INSERT INTO block_quota (`username`, `blockid`, `maxblocks`, `timespan`) VALUES ('%', 21, 16, 3600);
