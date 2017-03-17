INSERT INTO version (name,version) VALUES ('database', 98) ON DUPLICATE KEY UPDATE version=98;


CREATE  TABLE bookcase (
  `bookcaseid` BIGINT NOT NULL AUTO_INCREMENT ,
  `bagid` BIGINT NOT NULL DEFAULT '0',
  `casetitle`  VARCHAR(35) NOT NULL DEFAULT 'Bookcase' ,
  `owner` VARCHAR(60) NOT NULL ,
  `world` VARCHAR(60) NOT NULL ,
  `locx` INT NOT NULL ,
  `locy` INT NOT NULL ,
  `locz` INT NOT NULL ,
  `flags` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`bookcaseid`) ,
  UNIQUE INDEX `UNQ` (`world` ASC, `locx` ASC, `locy` ASC, `locz` ASC) ,
  INDEX `INX_LOC` (`world` ASC, `locx` ASC, `locy` ASC, `locz` ASC) ,
  INDEX `INX_OWNER` (`owner` ASC) );


CREATE TABLE `bookcase_members` (
  `bookcaseid` BIGINT NOT NULL,
  `username` varchar(60) NOT NULL,
  KEY `bookcaseid` (`bookcaseid`)
);