INSERT INTO version (name,version) VALUES ('database', 50) ON DUPLICATE KEY UPDATE version=50;



CREATE TABLE shop_chests(
	ID smallint(6) NOT NULL  DEFAULT '0' , 
	`index` tinyint(4) NOT NULL  DEFAULT '0' , 
	ChestType tinyint(4) NOT NULL  DEFAULT '0' , 
	X int(11) NOT NULL  DEFAULT '0' , 
	Y int(11) NOT NULL  DEFAULT '0' , 
	Z int(11) NOT NULL  DEFAULT '0' 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';


CREATE TABLE shop_contents(
	owner varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	amount tinyint(4) NOT NULL  DEFAULT '0' , 
	ID smallint(6) NOT NULL  DEFAULT '0' , 
	Data tinyint(4) NOT NULL  DEFAULT '0' 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';



CREATE TABLE shop_items(
	owner varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	cost int(11) NOT NULL  DEFAULT '0' , 
	ID smallint(6) NOT NULL  DEFAULT '0' , 
	Data tinyint(4) NOT NULL  DEFAULT '0' 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';
