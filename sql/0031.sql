INSERT INTO `version` (`name`,`version`) VALUES ('database', 31) ON DUPLICATE KEY UPDATE `version`=31;

ALTER TABLE `held_items` ENGINE=MyISAM, COMMENT='';

ALTER TABLE `saved_inventory` ENGINE=MyISAM, COMMENT='';