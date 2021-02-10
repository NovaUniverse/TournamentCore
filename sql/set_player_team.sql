CREATE PROCEDURE `set_player_team` (
	player_uuid VARCHAR(36),
    player_username VARCHAR(16),
    player_team_number INT
)
BEGIN
	IF NOT EXISTS (SELECT id FROM players WHERE uuid = player_uuid) THEN
		INSERT INTO players (uuid, username) VALUES (player_uuid, player_username);
    END IF;
    
    UPDATE players SET username = player_username, team_number = player_team_number WHERE uuid = player_uuid;
END