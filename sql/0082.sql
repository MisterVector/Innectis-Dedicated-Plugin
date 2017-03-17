
INSERT INTO version (name,version) VALUES ('database', 82) ON DUPLICATE KEY UPDATE version=82;

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `otakucraft` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `otakucraft` ;

-- -----------------------------------------------------
-- Table `otakucraft`.`chunks`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `otakucraft`.`chunks` (
  `chunkid` BIGINT NOT NULL AUTO_INCREMENT ,
  `locx` INT NOT NULL ,
  `locy` INT NOT NULL ,
  `locz` INT NOT NULL ,
  `world` VARCHAR(50) NOT NULL ,
  PRIMARY KEY (`chunkid`) ,
  INDEX `INX_LOC` (`world` ASC, `locx` ASC, `locz` ASC, `locy` ASC) )
ENGINE = MyISAM;


-- -----------------------------------------------------
-- Table `otakucraft`.`chunk_data`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `otakucraft`.`chunk_data` (
  `chunkid` BIGINT NOT NULL ,
  `location` INT NOT NULL ,
  `key` VARCHAR(50) NOT NULL ,
  `value` TEXT NOT NULL ,
  INDEX `fk_chunk_data_chunks1` (`chunkid` ASC) ,
  INDEX `INX_LOC` (`chunkid` ASC, `location` ASC) ,
  INDEX `INX_LOCKEY` (`chunkid` ASC, `location` ASC, `key` ASC) ,
  INDEX `INX_KEY` (`key` ASC) ,
  PRIMARY KEY (`key`, `location`, `chunkid`) )
ENGINE = MyISAM;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;



-- --------------------------------------------------------------------------------
-- Routine DDL
-- Note: comments before and after the routine body will not be stored by the server
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE FUNCTION `getChunkId`(locx int(11), locy int(11), locz int(11), world varchar(50)) RETURNS bigint(11)
    READS SQL DATA
    DETERMINISTIC
BEGIN
    declare id bigint(11);
    set id = -999;
    
    SELECT c.chunkid into id FROM chunks as c 
    WHERE c.world = world AND c.locx = locx AND c.locz = locz AND c.locy = locy LIMIT 1;
    
    IF id = -999 THEN
        INSERT INTO chunks (`locx`,`locy`,`locz`,`world`)
        VALUES (locx,locy,locz,world);
        
        SELECT LAST_INSERT_ID() INTO id;
    END IF;
    
    return id;
END
