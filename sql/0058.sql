INSERT INTO version (name,version) VALUES ('database', 58) ON DUPLICATE KEY UPDATE version=58;


DROP TABLE `shop_chests`; 
DROP TABLE `shop_items_official`; 


ALTER TABLE `shop_contents` 
	CHANGE `owner` `owner` tinytext  COLLATE latin1_swedish_ci NOT NULL first, 
	CHANGE `amount` `amount` int(11)   NOT NULL after `owner`, 
	CHANGE `damage` `damage` tinyint(2)   NOT NULL after `amount`, 
	CHANGE `ID` `ID` int(11)   NOT NULL after `damage`, 
	CHANGE `Data` `Data` int(11)   NOT NULL after `ID`, ENGINE=InnoDB, COMMENT='';


CREATE TABLE `shop_innectisicontents`(
	`ID` int(11) NOT NULL  , 
	`Data` int(11) NOT NULL  
) ENGINE=InnoDB DEFAULT CHARSET='latin1';


CREATE TABLE `shop_innectisitems`(
	`cost` int(11) NOT NULL  , 
	`ID` int(11) NOT NULL  , 
	`Data` int(11) NOT NULL  
) ENGINE=MyISAM DEFAULT CHARSET='latin1';


ALTER TABLE `shop_items` 
	CHANGE `owner` `owner` tinytext  COLLATE latin1_swedish_ci NULL first, 
	CHANGE `cost` `cost` int(11)   NOT NULL after `owner`, 
	CHANGE `ID` `ID` int(11)   NOT NULL after `cost`, 
	CHANGE `Data` `Data` int(11)   NOT NULL after `ID`, COMMENT='';
