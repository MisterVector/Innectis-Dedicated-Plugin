INSERT INTO version (name,version) VALUES ('database', 63) ON DUPLICATE KEY UPDATE version=63;

ALTER TABLE `lots` 
	DROP KEY `coords`, add KEY `coords`(`world`,`x1`,`x2`,`z1`,`z2`);