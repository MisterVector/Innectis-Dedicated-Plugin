INSERT INTO version (name,version) VALUES ('database', 91) ON DUPLICATE KEY UPDATE version=91;


CREATE  TABLE `otakucraft`.`enderchests` (
  `chestid` INT NOT NULL AUTO_INCREMENT ,
  `username` VARCHAR(50) NOT NULL ,
  `typeid` INT NOT NULL ,
  `contents` TEXT NOT NULL ,
  PRIMARY KEY (`chestid`) ,
  INDEX `INDX_USERNAME_TYPEID` (`username` ASC, `typeid` ASC) );

