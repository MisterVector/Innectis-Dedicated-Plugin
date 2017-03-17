INSERT INTO version (name,version) VALUES ('database', 45) ON DUPLICATE KEY UPDATE version=45;

CREATE  TABLE player_inventory (
  inventoryid INT NOT NULL AUTO_INCREMENT ,
  name VARCHAR(45) NOT NULL ,
  inventorytype INT NOT NULL ,
  content LONGTEXT NOT NULL ,
  PRIMARY KEY (inventoryid) ,
  UNIQUE INDEX name_type_UNIQUE (name ASC, inventorytype ASC) )
ENGINE = MyISAM;

