
INSERT INTO version (name,version) VALUES ('database', 94) ON DUPLICATE KEY UPDATE version=94;

CREATE  TABLE `otakucraft`.`fort` (
  `fortId` INT(11) NOT NULL AUTO_INCREMENT ,
  `fortHealth` INT(11) NOT NULL ,
  PRIMARY KEY (`fortId`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;