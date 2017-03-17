
drop table `otakucraft`.`bankaccounts`;
drop table `otakucraft`.`banktransferlogs`;
drop table `otakucraft`.`blacklist_events`;
drop table `otakucraft`.`sales`;
drop table `otakucraft`.`npcs`;
drop table `otakucraft`.`log`;
drop table `otakucraft`.`sdbox`;
drop table `otakucraft`.`sdbpayment`;
drop table `otakucraft`.`item_inbox`;
drop table `otakucraft`.`kickedplayers`;
drop table `otakucraft`.`blocklog`;
drop table `otakucraft`.`chatlog`;

CREATE  TABLE `otakucraft`.`item` (
  `itemid` INT NOT NULL ,
  `data` INT NOT NULL DEFAULT 0 ,
  `name` VARCHAR(45) NOT NULL ,
  `forsale` INT NOT NULL DEFAULT 0 ,
  `buystack` INT NOT NULL DEFAULT 0 ,
  `buyprize` INT NOT NULL DEFAULT 0 ,
  `buypointprize` INT NOT NULL DEFAULT 0 ,
  `sellstack` INT NOT NULL DEFAULT 0 ,
  `sellprize` INT NOT NULL DEFAULT 0 ,
  `stock` INT NOT NULL DEFAULT 0 ,
  `respawnstock` INT NOT NULL DEFAULT 0 ,
  `maxstock` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`itemid`, `data`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) );
  
ALTER TABLE `otakucraft`.`item` ENGINE = MyISAM ;


ALTER TABLE `otakucraft`.`players` ADD COLUMN `pvppoints` INT NOT NULL DEFAULT 0  AFTER `onlinetime` , ADD COLUMN `balance` INT NOT NULL DEFAULT 0  AFTER `pvppoints` , CHANGE COLUMN `onlinetime` `onlinetime` FLOAT NOT NULL DEFAULT '0'  ;

insert into pvp_points (username, points) select `name` , 0 as points from players where `name` not in (select username from pvp_points);

update players as a set pvppoints = (select b.points from pvp_points as b where `name` = username);

drop table `otakucraft`.`pvp_points`


ALTER TABLE `otakucraft`.`players` CHANGE COLUMN `name` `username` VARCHAR(60) NOT NULL  
, DROP PRIMARY KEY 
, ADD PRIMARY KEY (`username`) ;

ALTER TABLE `otakucraft`.`chestlog` CHANGE COLUMN `name` `username` VARCHAR(45) NOT NULL  
, DROP INDEX `name` 
, ADD INDEX `name` (`username` ASC) ;

ALTER TABLE `otakucraft`.`homes` CHANGE COLUMN `name` `username` VARCHAR(60) NOT NULL  
, DROP PRIMARY KEY 
, ADD PRIMARY KEY (`username`) ;


INSERT INTO `version` (`name`,`version`) VALUES ('database', 28) ON DUPLICATE KEY UPDATE `version`=28;
