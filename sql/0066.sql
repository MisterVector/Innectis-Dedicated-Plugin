CREATE TABLE `shop_signpool`(
	`balance` int(10) NOT NULL  DEFAULT '0' , 
	PRIMARY KEY (`balance`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';

/* this needs a default row, for the money pool */
INSERT INTO shop_signpool VALUES (0);

INSERT INTO version (name,version) VALUES ('database', 66) ON DUPLICATE KEY UPDATE version=66;