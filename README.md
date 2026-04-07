### Шаги запуска
1. Запустить все сервисы
```bash
docker-compose up -d --build 
```
2. проверить ClickHouse
```bash
docker exec -it clickhouse-server clickhouse-client --password clickhouse123 --query "SELECT * FROM reports_db.prosthesis_daily_fact"
```
3. Запустить DAG prosthesis_report_daily в Airflow http://localhost:8081

Логин: admin / admin
4. проверить ClickHouse
```bash
docker exec -it clickhouse-server clickhouse-client --password clickhouse123 --query "SELECT COUNT(*) FROM reports_db.prosthesis_daily_fact"
```
5. Проверить авторизацию и скачивание отчёта  http://localhost:3000
- prothetic1@example.com / prothetic123
- prothetic2@example.com / prothetic123 
- prothetic3@example.com / prothetic123 
1. Выключить все сервисы
```bash
docker-compose down
```