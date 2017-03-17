DROP TABLE `game_profiles`; 
DROP TABLE `game_profiles_scores`; 

INSERT INTO version (name,version) VALUES ('database', 157) ON DUPLICATE KEY UPDATE version = 157;