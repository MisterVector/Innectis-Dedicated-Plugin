INSERT INTO version (name,version) VALUES ('database', 129) ON DUPLICATE KEY UPDATE version = 129;

CREATE TABLE `channel_bans`(
	`channelid` int(11) NOT NULL  , 
	`username` varchar(45) COLLATE latin1_swedish_ci NULL  
) ENGINE=MyISAM DEFAULT CHARSET='latin1';


CREATE TABLE `channel_information`(
	`channelid` int(11) NOT NULL  auto_increment , 
	`channelname` varchar(45) COLLATE latin1_swedish_ci NULL  , 
	`settings` bigint(20) NOT NULL  , 
	`password` varchar(45) COLLATE latin1_swedish_ci NULL  , 
	`lastactivity` bigint(20) NOT NULL  , 
	PRIMARY KEY (`channelid`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';


CREATE TABLE `channel_members`(
	`channelid` int(11) NOT NULL  , 
	`username` varchar(45) COLLATE latin1_swedish_ci NULL  , 
	`personalnum` int(11) NOT NULL  , 
	`membergroup` int(11) NOT NULL  
) ENGINE=MyISAM DEFAULT CHARSET='latin1';

DROP TABLE `player_channels`;