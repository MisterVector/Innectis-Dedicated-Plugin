INSERT INTO version (name,version) VALUES ('database', 51) ON DUPLICATE KEY UPDATE version=51;


ALTER TABLE shop_chests 
	ADD COLUMN world text  COLLATE latin1_swedish_ci NOT NULL after ChestType, 
	CHANGE X X int(11)   NOT NULL DEFAULT '0' after world, 
	CHANGE Y Y int(11)   NOT NULL DEFAULT '0' after X, 
	CHANGE Z Z int(11)   NOT NULL DEFAULT '0' after Y, COMMENT='';


ALTER TABLE shop_contents 
	ADD COLUMN damage tinyint(4)   NOT NULL DEFAULT '0' after amount, 
	CHANGE ID ID smallint(6)   NOT NULL DEFAULT '0' after damage, 
	CHANGE Data Data tinyint(4)   NOT NULL DEFAULT '0' after ID, COMMENT='';