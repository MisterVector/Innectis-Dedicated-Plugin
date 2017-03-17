INSERT INTO `version` (`name`,`version`) VALUES ('database', 36) ON DUPLICATE KEY UPDATE `version`=36;

CREATE  TABLE `stored_inventory` (
  `idstored_inventory` INT NOT NULL ,
  `username` VARCHAR(45) NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `inventory` BLOB NOT NULL ,
  PRIMARY KEY (`idstored_inventory`) ,
  UNIQUE INDEX `Unique` (`username` ASC, `name` ASC) )
ENGINE = MyISAM;

ALTER TABLE `otakucraft`.`stored_inventory` CHANGE COLUMN `idstored_inventory` `idstored_inventory` INT(11) NOT NULL AUTO_INCREMENT  ;

