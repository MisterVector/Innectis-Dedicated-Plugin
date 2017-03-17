INSERT INTO version (name,version) VALUES ('database', 97) ON DUPLICATE KEY UPDATE version=97;


CREATE TABLE `contentbag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bagsize` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM;

CREATE TABLE `contentbag_items` (
  `bagid` bigint(20) NOT NULL,
  `locindex` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `data` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `itemdata` longblob,
  PRIMARY KEY (`bagid`,`locindex`),
  KEY `bagid` (`bagid`)
) ENGINE=MyISAM;


