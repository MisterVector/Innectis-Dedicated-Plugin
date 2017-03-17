CREATE TABLE `owned_entities`(
	`owner` varchar(45) COLLATE latin1_swedish_ci NULL  ,
	`entityid` int(11) NULL  ,
	`mostsigbits` bigint(20) NULL  ,
	`leastsigbits` bigint(20) NULL
) ENGINE=InnoDB DEFAULT CHARSET='latin1';

INSERT INTO version (name,version) VALUES ('database', 128) ON DUPLICATE KEY UPDATE version = 128;