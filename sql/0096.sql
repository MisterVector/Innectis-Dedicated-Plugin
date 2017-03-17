INSERT INTO version (name,version) VALUES ('database', 96) ON DUPLICATE KEY UPDATE version=96;

CREATE  TABLE `otakucraft`.`infractions` (

  `ID` INT NOT NULL AUTO_INCREMENT ,

  `player` VARCHAR(60) NOT NULL ,

  `incidentTime` BIGINT(20) NOT NULL ,

  `extraInfo` VARCHAR(255) NOT NULL ,

  `staffMember` VARCHAR(60) NOT NULL ,

  PRIMARY KEY (`ID`) );