INSERT INTO version (name,version) VALUES ('database', 44) ON DUPLICATE KEY UPDATE version=44;

ALTER TABLE players ADD COLUMN playergroup INT(11) NOT NULL DEFAULT '-1'  AFTER balance;
ALTER TABLE otakucraft.players ADD COLUMN namecolour VARCHAR(10) NOT NULL DEFAULT 'x' COMMENT 'Hexvalue'  AFTER playergroup ;


CREATE  TABLE prefix (
  prefixid INT(11) NOT NULL AUTO_INCREMENT ,
  name INT NOT NULL ,
  subid INT NOT NULL ,
  string VARCHAR(50) NULL ,
  colour VARCHAR(10) NULL ,
  UNIQUE INDEX prefixid_UNIQUE (name ASC, subid ASC) ,
  PRIMARY KEY (prefixid) );

ALTER TABLE prefix ADD COLUMN color2 VARCHAR(10) NULL  AFTER color1 , CHANGE COLUMN string text VARCHAR(45) NULL DEFAULT NULL  , CHANGE COLUMN colour color1 VARCHAR(10) NULL DEFAULT NULL;

ALTER TABLE prefix CHANGE COLUMN name name VARCHAR(45) NOT NULL  , CHANGE COLUMN text text VARCHAR(14) NULL DEFAULT NULL  ;

