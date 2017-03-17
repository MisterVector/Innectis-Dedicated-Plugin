INSERT INTO version (name,version) VALUES ('database', 55) ON DUPLICATE KEY UPDATE version=55;

drop table converted_inventories;

CREATE  TABLE converted_inventories (
  playername VARCHAR(255) NOT NULL ,
  PRIMARY KEY (playername) );

