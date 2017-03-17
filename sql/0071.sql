INSERT INTO version (name,version) VALUES ('database', 71) ON DUPLICATE KEY UPDATE version=71;

DROP TABLE `ip_log`; 

CREATE TABLE `ip_log` (
  `logid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `ip` varchar(45) NOT NULL,
  `logtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`logid`),
  UNIQUE KEY `ip_name_UNIQUE` (`name`,`ip`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1
