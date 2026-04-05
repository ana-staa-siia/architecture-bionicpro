INSERT INTO prosthesis_daily_fact (report_date, user_id, prosthesis_id, user_name, prosthesis_model,
                                   total_sessions, avg_signal_strength, total_movements,
                                   error_count)
SELECT t.event_date                       as report_date,
       c.user_id,
       c.prosthesis_id,
       c.user_name,
       c.prosthesis_model,
       COALESCE(t.total_sessions, 0)      as total_sessions,
       COALESCE(t.avg_signal_strength, 0) as avg_signal_strength,
       COALESCE(t.total_movements, 0)     as total_movements,
       COALESCE(t.error_count, 0)         as error_count
FROM crm_temp c
         LEFT JOIN telemetry_temp t ON c.user_id = t.user_id AND c.prosthesis_id = t.prosthesis_id;