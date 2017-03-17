INSERT INTO `version` (`name`,`version`) VALUES ('database', 37) ON DUPLICATE KEY UPDATE `version`=37;

CREATE  TABLE `stored_inventory` (
  `idstored_inventory` INT NOT NULL ,
  `username` VARCHAR(45) NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `inventory` BLOB NOT NULL ,
  PRIMARY KEY (`idstored_inventory`) ,
  UNIQUE INDEX `Unique` (`username` ASC, `name` ASC) )
ENGINE = MyISAM;

ALTER TABLE `otakucraft`.`stored_inventory` CHANGE COLUMN `idstored_inventory` `idstored_inventory` INT(11) NOT NULL AUTO_INCREMENT  ;



ALTER TABLE `players` 
	CHANGE `referralpoints` `referralpoints` int(11)   NOT NULL DEFAULT '0' after `pvppoints`, 
	CHANGE `balance` `balance` int(11)   NOT NULL DEFAULT '0' after `referralpoints`, COMMENT='';