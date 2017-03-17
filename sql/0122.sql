CREATE TABLE `trapdoors`(
	`trapdoorid` int(11) NOT NULL  auto_increment , 
	`owner` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`world` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`locx` int(11) NOT NULL  , 
	`locy` int(11) NOT NULL  , 
	`locz` int(11) NOT NULL  , 
	`flags` bigint(20) NOT NULL  , 
	PRIMARY KEY (`trapdoorid`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';


CREATE TABLE `trapdoors_members`(
	`trapdoorid` int(11) NULL  , 
	`username` varchar(60) COLLATE latin1_swedish_ci NULL  
) ENGINE=InnoDB DEFAULT CHARSET='latin1';

INSERT INTO version (name,version) VALUES ('database', 122) ON DUPLICATE KEY UPDATE version = 122;