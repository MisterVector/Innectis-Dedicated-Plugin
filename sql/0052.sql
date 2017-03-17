INSERT INTO version (name,version) VALUES ('database', 52) ON DUPLICATE KEY UPDATE version=52;


ALTER TABLE shop_chests 
	CHANGE ID ID int(11)   NOT NULL DEFAULT '0' first, 
	CHANGE ChestType ChestType int(11)   NOT NULL DEFAULT '0' after ID, COMMENT='';


ALTER TABLE shop_contents 
	CHANGE amount amount int(11)   NOT NULL DEFAULT '0' after owner, 
	CHANGE damage damage int(11)   NOT NULL DEFAULT '0' after amount, 
	CHANGE ID ID int(11)   NOT NULL DEFAULT '0' after damage, 
	CHANGE Data Data int(11)   NOT NULL DEFAULT '0' after ID, COMMENT='';


ALTER TABLE shop_items 
	CHANGE ID ID int(11)   NOT NULL DEFAULT '0' after cost, 
	CHANGE Data Data int(11)   NOT NULL DEFAULT '0' after ID, COMMENT='';


CREATE TABLE shop_items_official(
	ID int(11) NOT NULL  DEFAULT '0' , 
	Data int(11) NOT NULL  DEFAULT '0' 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';
