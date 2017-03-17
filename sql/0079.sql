INSERT INTO version (name,version) VALUES ('database', 79) ON DUPLICATE KEY UPDATE version=79;

CREATE  TABLE `otakucraft`.`block_log` (
  `logid` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `locx` INT NOT NULL ,
  `locy` INT NOT NULL ,
  `locz` INT NOT NULL ,
  `world` VARCHAR(45) NOT NULL ,
  `Id` INT NOT NULL ,
  `Data` INT NOT NULL ,
  `DateTime` TIMESTAMP NOT NULL ,
  `ActionType` INT NOT NULL ,
  PRIMARY KEY (`logid`) ,
  INDEX `INX_Username` (`name` ASC) ,
  INDEX `INX_Location` (`locx` ASC, `locz` ASC, `locy` ASC, `world` ASC) )
ENGINE = MyISAM;

