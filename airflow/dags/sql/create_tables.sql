DROP TABLE IF EXISTS crm_temp;
CREATE TABLE crm_temp (
                          user_id VARCHAR(100),
                          user_name VARCHAR(200),
                          prosthesis_id VARCHAR(100),
                          prosthesis_model VARCHAR(100)
);

DROP TABLE IF EXISTS telemetry_temp;
CREATE TABLE telemetry_temp (
                                user_id VARCHAR(100),
                                prosthesis_id VARCHAR(100),
                                event_date DATE,
                                total_sessions INTEGER,
                                avg_signal_strength FLOAT,
                                total_movements INTEGER,
                                error_count INTEGER
);


DROP TABLE IF EXISTS prosthesis_daily_fact;
CREATE TABLE prosthesis_daily_fact (
                                       report_date DATE,
                                       user_id VARCHAR(100),
                                       prosthesis_id VARCHAR(100),
                                       user_name VARCHAR(200),
                                       prosthesis_model VARCHAR(100),
                                       total_sessions INTEGER,
                                       avg_signal_strength FLOAT,
                                       total_movements INTEGER,
                                       error_count INTEGER,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);