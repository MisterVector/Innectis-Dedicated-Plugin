INSERT INTO version (name,version) VALUES ('database', 73) ON DUPLICATE KEY UPDATE version=73;

CREATE TABLE `member_groups`(
	`groupid` int(11) NOT NULL  auto_increment , 
	`username` varchar(50) COLLATE latin1_swedish_ci NOT NULL  , 
	`groupname` varchar(50) COLLATE latin1_swedish_ci NOT NULL  , 
	PRIMARY KEY (`groupid`) , 
	UNIQUE KEY `usergroup`(`username`,`groupname`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';


CREATE TABLE `member_users`(
	`groupid` int(11) NOT NULL  , 
	`username` varchar(50) COLLATE latin1_swedish_ci NOT NULL  , 
	PRIMARY KEY (`groupid`,`username`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';
