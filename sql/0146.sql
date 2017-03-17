CREATE  TABLE `player_infracts` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(60) NOT NULL ,
  `intensity` INT NOT NULL ,
  `dateGMT` BIGINT NOT NULL ,
  `summary` VARCHAR(100) NOT NULL ,
  `details` TEXT NULL ,
  `staff` VARCHAR(60) NOT NULL ,
  `revoker` VARCHAR(60) NULL ,
  `revokeDateGMT` BIGINT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `INDEX_USR` (`name` ASC) ,
  INDEX `INDEX_SFF` (`staff` ASC) )
ENGINE = MyISAM;

INSERT INTO version (name,version) VALUES ('database', 146) ON DUPLICATE KEY UPDATE version = 146;