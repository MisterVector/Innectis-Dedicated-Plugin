INSERT INTO version (name,version) VALUES ('database', 72) ON DUPLICATE KEY UPDATE version=72;

ALTER TABLE `converted_inventories` ENGINE=MyISAM, COMMENT='';
ALTER TABLE `lot_names` ENGINE=MyISAM, COMMENT='';
ALTER TABLE `shop_contents` ENGINE=MyISAM, COMMENT='';
ALTER TABLE `shop_innectisicontents` ENGINE=MyISAM, COMMENT='';

CREATE TABLE `waypoints_members`(
	`waypointid` int(11) unsigned NOT NULL  , 
	`username` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	KEY `chestid`(`waypointid`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';

INSERT INTO waypoints_members (waypointid, username)
	SELECT waypointid, '%' FROM waypoints;
