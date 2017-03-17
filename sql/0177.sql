CREATE TABLE `lot_tags`(
	`lot_id` int(11) NOT NULL  , 
	`tag` text COLLATE latin1_swedish_ci NULL  , 
	`public_tag` tinyint(4) NOT NULL  
) ENGINE=MyISAM DEFAULT CHARSET='latin1';
DROP TABLE `owned_object_tags`; 
INSERT INTO version (name,version) VALUES ('database', 177) ON DUPLICATE KEY UPDATE version = 177;