INSERT INTO version (name,version) VALUES ('database', 121) ON DUPLICATE KEY UPDATE version = 121;


DELETE FROM contentbag WHERE id in (select bagid from player_inventory where inventorytype = 3);
DELETE FROM contentbag_items WHERE bagid in (select bagid from player_inventory where inventorytype = 3);
DELETE FROM player_inventory WHERE inventorytype = 3;


DROP TABLE fort;


# DELETE FROM `innectis_db`.`player_inventory` WHERE `inventorytype`='3';

