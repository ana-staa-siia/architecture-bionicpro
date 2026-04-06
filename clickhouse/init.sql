CREATE DATABASE IF NOT EXISTS reports_db;

CREATE TABLE IF NOT EXISTS reports_db.crm_temp
(
    user_id          String,
    user_name        String,
    prosthesis_id    String,
    prosthesis_model String
) ENGINE = Memory;

CREATE TABLE IF NOT EXISTS reports_db.telemetry_temp
(
    user_id             String,
    prosthesis_id       String,
    event_date          Date,
    total_sessions      UInt32,
    avg_signal_strength Float32,
    total_movements     UInt32,
    error_count         UInt32
) ENGINE = Memory;

CREATE TABLE IF NOT EXISTS reports_db.prosthesis_daily_fact
(
    report_date         Date,
    user_id             String,
    prosthesis_id       String,
    user_name           String,
    prosthesis_model    String,
    total_sessions      UInt32,
    avg_signal_strength Float32,
    total_movements     UInt32,
    error_count         UInt32,
    updated_at          DateTime DEFAULT now()
) ENGINE = MergeTree()
ORDER BY (report_date, user_id, prosthesis_id);