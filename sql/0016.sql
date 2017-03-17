INSERT INTO `version` (`name`,`version`) VALUES ('database', 16) ON DUPLICATE KEY UPDATE `version`=16;

CREATE  TABLE `fort` (
  `pillarid` INT NOT NULL AUTO_INCREMENT ,
  `fortid` INT NOT NULL ,
  `locx` INT NOT NULL ,
  `locy` INT NOT NULL ,
  `locz` INT NOT NULL ,
  `radius` INT NOT NULL ,
  PRIMARY KEY (`pillarid`) );

CREATE  TABLE `fort_members` (
  `memberid` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(55) NOT NULL ,
  `fortid` INT NOT NULL ,
  `rank` INT NOT NULL ,
  PRIMARY KEY (`memberid`) );
 