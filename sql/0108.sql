INSERT INTO version (name,version) VALUES ('database', 108) ON DUPLICATE KEY UPDATE version=108;

CREATE TABLE `staff_requests`(
	`id` int(11) NOT NULL  auto_increment , 
	`datecreated` date NULL  , 
	`hasread` tinyint(1) NULL  DEFAULT '0' , 
	`creator` varchar(15) COLLATE latin1_swedish_ci NULL  , 
	`message` varchar(200) COLLATE latin1_swedish_ci NULL  , 
	PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';