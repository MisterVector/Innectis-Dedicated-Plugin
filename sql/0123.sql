CREATE TABLE `item_shop`(
	`shopid` int(11) NOT NULL  auto_increment , 
	`owner` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`bagid` bigint(20) NOT NULL  ,
	`buy_amount` int(11) NOT NULL  , 
	`buy_price` int(11) NOT NULL  , 
	`status` int(1) NOT NULL  , 
	`world` varchar(60) COLLATE latin1_swedish_ci NOT NULL  , 
	`locx` int(11) NOT NULL  , 
	`locy` int(11) NOT NULL  , 
	`locz` int(11) NOT NULL  , 
	`flags` bigint(20) NOT NULL  , 
	PRIMARY KEY (`shopid`) 
) ENGINE=InnoDB DEFAULT CHARSET='latin1';


CREATE TABLE `shop_members`(
	`shopid` int(11) NULL  , 
	`username` varchar(60) COLLATE latin1_swedish_ci NULL  
) ENGINE=InnoDB DEFAULT CHARSET='latin1';

INSERT INTO version (name,version) VALUES ('database', 123) ON DUPLICATE KEY UPDATE version = 123;