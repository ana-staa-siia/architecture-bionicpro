from datetime import datetime, timedelta
from airflow import DAG
from airflow.operators.python import PythonOperator
from clickhouse_driver import Client
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
    schedule_interval='0 2 * * *',
    catchup=False,
)

def get_client():
    return Client(host='clickhouse', port=9000, user='default', password='clickhouse123')

def cleanup():
    client = get_client()
    client.execute('TRUNCATE TABLE reports_db.crm_temp')
    client.execute('TRUNCATE TABLE reports_db.telemetry_temp')

def load_crm():
    client = get_client()
    with open('/opt/airflow/data/crm_data.csv', 'r') as f:
        reader = csv.reader(f)
        next(reader)
        for row in reader:
            client.execute(
                'INSERT INTO reports_db.crm_temp VALUES',
                [(row[0], row[1], row[2], row[3])]
            )

def load_telemetry():
    client = get_client()
    with open('/opt/airflow/data/telemetry_data.csv', 'r') as f:
        reader = csv.reader(f)
        next(reader)
        for row in reader:
            event_date = datetime.strptime(row[2], '%Y-%m-%d').date()
            client.execute(
                'INSERT INTO reports_db.telemetry_temp VALUES',
                [(row[0], row[1], event_date, int(row[3]), float(row[4]), int(row[5]), int(row[6]))]
            )

def merge_to_fact():
    client = get_client()
    client.execute('TRUNCATE TABLE reports_db.prosthesis_daily_fact')

    client.execute('''
        INSERT INTO reports_db.prosthesis_daily_fact (
            report_date, user_id, prosthesis_id, user_name, prosthesis_model,
            total_sessions, avg_signal_strength, total_movements, error_count, updated_at
        )
        SELECT
            t.event_date,
            t.user_id,
            t.prosthesis_id,
            COALESCE(c.user_name, t.user_id),
            COALESCE(c.prosthesis_model, 'Unknown'),
            t.total_sessions,
            t.avg_signal_strength,
            t.total_movements,
            t.error_count,
            now()
        FROM reports_db.telemetry_temp t
        LEFT JOIN reports_db.crm_temp c ON t.user_id = c.user_id AND t.prosthesis_id = c.prosthesis_id
    ''')

cleanup_task = PythonOperator(task_id='cleanup', python_callable=cleanup, dag=dag)
load_crm_task = PythonOperator(task_id='load_crm', python_callable=load_crm, dag=dag)
load_telemetry_task = PythonOperator(task_id='load_telemetry', python_callable=load_telemetry, dag=dag)
merge_task = PythonOperator(task_id='merge', python_callable=merge_to_fact, dag=dag)

cleanup_task >> [load_crm_task, load_telemetry_task] >> merge_task