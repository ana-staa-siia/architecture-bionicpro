from datetime import datetime, timedelta
from airflow import DAG
from airflow.operators.python import PythonOperator
from airflow.providers.postgres.operators.postgres import PostgresOperator
import csv

default_args = {
    'owner': 'bionicpro',
    'start_date': datetime(2026, 1, 1),
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

dag = DAG(
    'prosthesis_report_daily',
    default_args=default_args,
    description='Ежедневная витрина отчётов по протезам',
    schedule_interval= '*/2 * * * *',#'0 2 * * *',
    catchup=False,
)

# 1. Создание таблиц
create_tables = PostgresOperator(
    task_id='create_tables',
    postgres_conn_id='olap_db',
    sql='sql/create_tables.sql',
    dag=dag,
)

# 2. Функции для чтения CSV и генерации SQL
def generate_crm_queries():
    """Читает CRM данные из CSV и генерирует INSERT запросы"""
    csv_file_path = '/opt/airflow/data/crm_data.csv'
    insert_queries = []

    with open(csv_file_path, 'r') as csvfile:
        csvreader = csv.reader(csvfile)
        is_header = True
        for row in csvreader:
            if is_header:
                is_header = False
                continue
            query = f"INSERT INTO crm_temp (user_id, user_name, prosthesis_id, prosthesis_model) VALUES ('{row[0]}', '{row[1]}', '{row[2]}', '{row[3]}');"
            insert_queries.append(query)

    with open('./dags/sql/crm_queries.sql', 'w') as f:
        for query in insert_queries:
            f.write(f"{query}\n")

def generate_telemetry_queries():
    """Читает телеметрию из CSV и генерирует INSERT запросы"""
    csv_file_path = '/opt/airflow/data/telemetry_data.csv'
    insert_queries = []

    with open(csv_file_path, 'r') as csvfile:
        csvreader = csv.reader(csvfile)
        is_header = True
        for row in csvreader:
            if is_header:
                is_header = False
                continue
            query = f"INSERT INTO telemetry_temp (user_id, prosthesis_id, event_date, total_sessions, avg_signal_strength, total_movements, error_count) VALUES ('{row[0]}', '{row[1]}', '{row[2]}', {row[3]}, {row[4]}, {row[5]}, {row[6]});"
            insert_queries.append(query)

    with open('./dags/sql/telemetry_queries.sql', 'w') as f:
        for query in insert_queries:
            f.write(f"{query}\n")

# 3. Операторы для генерации SQL
generate_crm = PythonOperator(
    task_id='generate_crm_queries',
    python_callable=generate_crm_queries,
    dag=dag,
)

generate_telemetry = PythonOperator(
    task_id='generate_telemetry_queries',
    python_callable=generate_telemetry_queries,
    dag=dag,
)

# 4. Выполнение SQL запросов
run_crm_queries = PostgresOperator(
    task_id='run_crm_queries',
    postgres_conn_id='olap_db',
    sql='sql/crm_queries.sql',
    dag=dag,
)

run_telemetry_queries = PostgresOperator(
    task_id='run_telemetry_queries',
    postgres_conn_id='olap_db',
    sql='sql/telemetry_queries.sql',
    dag=dag,
)

run_merge_queries = PostgresOperator(
    task_id='run_merge_queries',
    postgres_conn_id='olap_db',
    sql='sql/merge_queries.sql',
    dag=dag,
)

# Порядок выполнения задач
create_tables >> [generate_crm, generate_telemetry]
generate_crm >> run_crm_queries
generate_telemetry >> run_telemetry_queries
[run_crm_queries, run_telemetry_queries] >> run_merge_queries