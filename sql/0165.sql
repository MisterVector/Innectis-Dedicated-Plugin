CREATE TABLE `owned_object_tags`(
	`type_id` int(11) NOT NULL  , 
	`object_id` int(11) NOT NULL  , 
	`tag` text COLLATE latin1_swedish_ci NULL  
) ENGINE=MyISAM DEFAULT CHARSET='latin1';

INSERT INTO version (name,version) VALUES ('database', 165) ON DUPLICATE KEY UPDATE version = 165;