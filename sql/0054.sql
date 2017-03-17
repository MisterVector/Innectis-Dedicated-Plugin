INSERT INTO version (name,version) VALUES ('database', 54) ON DUPLICATE KEY UPDATE version=54;


CREATE  TABLE converted_inventories (
  playername INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (playername))
ENGINE = MyISAM;