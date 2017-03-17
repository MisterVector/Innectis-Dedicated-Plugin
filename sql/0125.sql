

# Add a columkn to store the logout date
ALTER TABLE ip_log ADD COLUMN logouttime TIMESTAMP NULL AFTER logtime ;

# Add an index here for quick lookups
ALTER TABLE ip_log ADD INDEX name (name ASC) ;



INSERT INTO version (name,version) VALUES ('database', 125) ON DUPLICATE KEY UPDATE version = 125;