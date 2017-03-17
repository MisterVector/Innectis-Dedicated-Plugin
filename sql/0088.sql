INSERT INTO version (name,version) VALUES ('database', 88) ON DUPLICATE KEY UPDATE version=88;

CREATE  TABLE `otakucraft`.`auto_responses` (

  `ID` INT NOT NULL AUTO_INCREMENT ,

  `owner` VARCHAR(64) NOT NULL ,

  `order` INT NOT NULL ,

  `response` TEXT NOT NULL ,

  PRIMARY KEY (`ID`) )

ENGINE = MyISAM;
