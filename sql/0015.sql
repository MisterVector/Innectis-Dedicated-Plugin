INSERT INTO `version` (`name`,`version`) VALUES ('database', 15) ON DUPLICATE KEY UPDATE `version`=15;

DELIMITER $$

DROP PROCEDURE IF EXISTS `convert_chestlog`$$

CREATE PROCEDURE `convert_chestlog`()
BEGIN
	DECLARE done INT DEFAULT 0;
	
	DECLARE cx, cy, cz INT;
	DECLARE id INT;
	
	DECLARE cur1 CURSOR FOR SELECT chestx, chesty, chestz FROM chestlog;
	DECLARE cur2 CURSOR FOR SELECT chestid FROM chests WHERE (locx1=cx AND locy1=cy AND locz1=cz) OR (locx2=cx AND locy2=cy AND locz2=cz);
	
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
	
	ALTER TABLE chestlog ADD COLUMN `chestid` int(11) NOT NULL after `logid`, COMMENT='';
	
	OPEN cur1;
	
	read_loop: LOOP
		SET done = 0;
		FETCH cur1 INTO cx, cy, cz;
		IF done THEN
			LEAVE read_loop;
		END IF;
		
		OPEN cur2;
		FETCH cur2 INTO id;
		CLOSE cur2;
		
		UPDATE chestlog SET chestid=id WHERE chestx=cx AND chesty=cy AND chestz=cz;
	END LOOP;
	CLOSE cur1;
	
	ALTER TABLE chestlog DROP COLUMN `chestx`, DROP COLUMN `chesty`, DROP COLUMN `chestz`;
	DELETE FROM chestlog WHERE chestid = 0;
    END$$

DELIMITER ;

CALL convert_chestlog();

DROP PROCEDURE IF EXISTS `convert_chestlog`;