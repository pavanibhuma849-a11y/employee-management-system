import pandas as pd
import json
import os
import numpy as np
from sklearn.linear_model import LinearRegression
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
from sqlalchemy import create_engine

# Database configuration (adjust as per environment)
DB_URL = "postgresql://postgres:root@localhost:5432/MYDB"
CSV_FILE = "employees.csv"

def fetch_from_db():
    """Fetch employee data dynamically from the database (TC-049)."""
    try:
        engine = create_engine(DB_URL)
        query = """
            SELECT e.id, e.name, e.role, e.salary, e.joining_date as "joiningDate", d.name as department
            FROM employee e
            LEFT JOIN department d ON e.department_id = d.id
        """
        df = pd.read_sql(query, engine)
        
        if not df.empty:
            print("Data fetched dynamically from database.")
            return process_data(df)
        return None
    except Exception as e:
        print(f"Database connection failed: {e}.")
        return None

def fetch_from_csv():
    """Fallback: Fetch employee data from CSV file."""
    try:
        if os.path.exists(CSV_FILE):
            df = pd.read_csv(CSV_FILE)
            print("Data fetched from CSV file.")
            return process_data(df)
        return None
    except Exception as e:
        print(f"Error reading CSV: {e}.")
        return None

def generate_sample_data():
    data = {
        'id': range(1, 11),
        'name': [f'Employee {i}' for i in range(1, 11)],
        'role': ['Developer', 'Manager', 'Developer', 'Designer', 'Developer', 'HR', 'IT Support', 'Manager', 'Developer', 'Designer'],
        'salary': [60000, 85000, 62000, 55000, 61000, 50000, 48000, 90000, 63000, 57000],
        'years_experience': [2, 8, 3, 4, 3, 5, 2, 10, 4, 5],
        'department': ['IT', 'HR', 'IT', 'Design', 'IT', 'HR', 'IT', 'Management', 'IT', 'Design'],
        'joiningDate': ['2022-01-15', '2018-03-10', '2021-05-20', '2020-11-05', '2021-06-15', '2019-02-10', '2022-08-15', '2015-01-01', '2021-09-20', '2020-03-15'],
        'performance_score': [85, 92, 78, 88, 95, 82, 75, 96, 80, 89]
    }
    df = pd.DataFrame(data)
    df.to_csv('employees.csv', index=False)
    print("Sample data generated: employees.csv")
    return process_data(df)

def process_data(df):
    """Common processing for both DB and CSV data (TC-044)."""
    if df is None or df.empty:
        print("Warning: Received empty dataset for processing.")
        return pd.DataFrame()
        
    # Standardize department names to avoid duplicates like "HR" vs "Human Resources"
    dept_mapping = {
        'Human Resources': 'HR',
        'Information Technology': 'IT',
        'Fin': 'Finance',
        'Admin': 'Management'
    }
    if 'department' in df.columns:
        df['department'] = df['department'].fillna('Unassigned').replace(dept_mapping)
        
    # Handle missing salary
    if 'salary' in df.columns:
        df['salary'] = pd.to_numeric(df['salary'], errors='coerce')
        df['salary'] = df['salary'].fillna(df['salary'].mean())
        
    # Calculate years_experience from joiningDate for more realistic ML insights
    if 'joiningDate' in df.columns:
        df['joiningDate'] = pd.to_datetime(df['joiningDate'], errors='coerce')
        # Fill NaTs with a default date if necessary
        df['joiningDate'] = df['joiningDate'].fillna(pd.Timestamp('2020-01-01'))
        current_year = pd.Timestamp.now().year
        df['years_experience'] = current_year - df['joiningDate'].dt.year
    elif 'years_experience' not in df.columns:
        df['years_experience'] = np.random.randint(1, 15, size=len(df))
        
    # Add performance_score if missing
    if 'performance_score' not in df.columns or df['performance_score'].isnull().any():
        mask = df['performance_score'].isnull() if 'performance_score' in df.columns else [True] * len(df)
        if 'performance_score' not in df.columns:
            df['performance_score'] = np.random.randint(70, 100, size=len(df))
        else:
            df.loc[mask, 'performance_score'] = np.random.randint(70, 100, size=mask.sum())
            
    return df

def calculate_avg_salary(df):
    """Calculate average salary per department (TC-039)."""
    if 'department' in df.columns and 'salary' in df.columns:
        avg_salary = df.groupby('department')['salary'].mean().to_dict()
        print(f"Average Salary per Department: {avg_salary}")
        return avg_salary
    return {}

def get_top_performers(df, threshold=90):
    """Identify top performers based on threshold (TC-040)."""
    if 'performance_score' in df.columns:
        top_performers = df[df['performance_score'] >= threshold].sort_values(by='performance_score', ascending=False)
        return top_performers[['name', 'performance_score']].to_dict(orient='records')
    return []

def attrition_risk(df):
    """Flag employees at risk of attrition based on low performance and experience (TC-041)."""
    if df.empty:
        return []
    if 'performance_score' in df.columns and 'years_experience' in df.columns:
        # Rule: High risk if performance < 75 or (performance < 85 and experience > 10)
        risk_mask = (df['performance_score'] < 75) | ((df['performance_score'] < 85) & (df['years_experience'] > 10))
        at_risk = df[risk_mask]
        return at_risk[['name', 'role', 'performance_score', 'years_experience']].to_dict(orient='records')
    return []

def verify_aggregation_accuracy(df):
    """Verify that groupby aggregations are accurate (TC-045)."""
    if df.empty or 'department' not in df.columns or 'salary' not in df.columns:
        return False
        
    # Example manual check for one department
    depts = df['department'].unique()
    if len(depts) > 0:
        test_dept = depts[0]
        dept_salaries = df[df['department'] == test_dept]['salary']
        expected_mean = dept_salaries.mean()
        actual_mean = df.groupby('department')['salary'].mean().loc[test_dept]
        
        is_accurate = np.isclose(expected_mean, actual_mean)
        print(f"Aggregation Accuracy Check ({test_dept}): {'PASSED' if is_accurate else 'FAILED'}")
        return is_accurate
    return True

def export_report(data, format='json'):
    """Export the analytics report to CSV or JSON (TC-042, TC-043)."""
    if not os.path.exists('reports'):
        os.makedirs('reports')
        
    if format == 'json':
        file_path = 'reports/analytics_report_export.json'
        with open(file_path, 'w') as f:
            json.dump(data, f, indent=4)
        print(f"Report exported to {file_path}")
    elif format == 'csv':
        file_path = 'reports/analytics_report_export.csv'
        # Convert the dictionary data to a flat DataFrame for CSV export
        export_df = pd.DataFrame(data.get('top_performers', []))
        export_df.to_csv(file_path, index=False)
        print(f"Report exported to {file_path}")
    return True

def run_analytics(df):
    """Run the complete analytics suite (TC-039 to TC-045)."""
    if df is None or df.empty:
        print("No data available to run analytics (TC-044).")
        return
        
    print("\n--- Employee Analytics Summary ---")
    
    # 0. Verify aggregation accuracy (TC-045)
    verify_aggregation_accuracy(df)
    
    # 1. Average salary per department
    avg_salary = calculate_avg_salary(df)
    
    # 2. Identify top performers (TC-040)
    top_performers = get_top_performers(df, threshold=90)
    print(f"Top Performers: {top_performers}")
    
    # 3. Attrition Risk (TC-041)
    attrition_data = attrition_risk(df)
    print(f"Attrition Risk (High Risk): {attrition_data}")
    
    # --- Optional ML Components (Bonus) ---
    print("\n--- ML Analytics (Bonus) ---")
    
    # 4. Linear Regression: Salary prediction based on experience
    X = df[['years_experience']]
    y = df['salary']
    model = LinearRegression()
    model.fit(X, y)
    print(f"Salary Trend: Each year of experience adds approx. ${model.coef_[0]:.2f}")
    
    # 5. K-Means Clustering: Employee segmentation
    # Segmenting based on Salary and Performance Score
    features = df[['salary', 'performance_score']]
    scaler = StandardScaler()
    scaled_features = scaler.fit_transform(features)
    
    kmeans = KMeans(n_clusters=3, random_state=42, n_init=10)
    df['segment'] = kmeans.fit_predict(scaled_features)
    print("Employee Segmentation completed. See reports for details.")

    # Generate Reports
    if not os.path.exists('reports'):
        os.makedirs('reports')
        
    report_data = {
        'average_salary': avg_salary,
        'top_performers': top_performers,
        'attrition_risk': attrition_data,
        'salary_regression': {
            'coefficient': model.coef_[0],
            'intercept': model.intercept_
        },
        'segments': df[['name', 'segment']].to_dict(orient='records')
    }
    
    with open('reports/analytics_report.json', 'w') as f:
        json.dump(report_data, f, indent=4)
        
    # Export reports in CSV and JSON formats (TC-042, TC-043)
    export_report(report_data, format='csv')
    export_report(report_data, format='json')
    
    df.groupby('department')['salary'].mean().to_csv('reports/department_salary_report.csv')
    df.to_csv('reports/full_analytics_data.csv', index=False)
    
    print("\nReports generated in 'reports/' directory.")

def main():
    print("Smart Employee Management Analytics Engine")
    
    # Try to load from CSV (TC-038) if it exists, otherwise fallback to DB
    if os.path.exists(CSV_FILE):
        df = fetch_from_csv()
    else:
        df = fetch_from_db()
        
    if df is None or df.empty:
        df = generate_sample_data()
        
    run_analytics(df)

if __name__ == "__main__":
    main()
