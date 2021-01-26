CREATE PROCEDURE `reset_data` ()
BEGIN
	UPDATE teams SET score = 0;
    
    UPDATE players SET score = 0, team_number = 0, kills = 0;
    
    UPDATE tc_data SET data_value = null WHERE data_key = "active_server";
END