CREATE TABLE `custom_map_images`(
	`map_id` int(11) NULL  , 
	`image_data` blob NULL  
) ENGINE=MyISAM DEFAULT CHARSET='utf8';

INSERT INTO version (name,version) VALUES ('database', 163) ON DUPLICATE KEY UPDATE version = 163;