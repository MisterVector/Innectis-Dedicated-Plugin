ALTER TABLE bookcase_members ADD COLUMN `isop` TINYINT(1) NOT NULL DEFAULT 0  AFTER `username` ;
ALTER TABLE chests_members ADD COLUMN `isop` TINYINT(1) NOT NULL DEFAULT 0  AFTER `username` ;
ALTER TABLE doors_members ADD COLUMN `isop` TINYINT(1) NOT NULL DEFAULT 0  AFTER `username` ;
ALTER TABLE lot_members ADD COLUMN `isop` TINYINT(1) NOT NULL DEFAULT 0  AFTER `username` ;
ALTER TABLE trapdoors_members ADD COLUMN `isop` TINYINT(1) NOT NULL DEFAULT 0  AFTER `username` ;
ALTER TABLE waypoints_members ADD COLUMN `isop` TINYINT(1) NOT NULL DEFAULT 0  AFTER `username` ;




# These two should not be NULLABLE!
ALTER TABLE trapdoors_members 
    CHANGE COLUMN `trapdoorid` `trapdoorid` int(11) NOT NULL,
    CHANGE COLUMN `username` `username` VARCHAR(60) NOT NULL ;


INSERT INTO version (name,version) VALUES ('database', 140) ON DUPLICATE KEY UPDATE version = 140;