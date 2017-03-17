
INSERT INTO version (name,version) VALUES ('database', 90) ON DUPLICATE KEY UPDATE version=90;

UPDATE `lots` SET world='oldworld' WHERE world='innectis';
