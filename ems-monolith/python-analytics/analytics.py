import pandas as pd
import json
import os
import numpy as np
import matplotlib.pyplot as plt
from sqlalchemy import create_engine
from sklearn.linear_model import LinearRegression
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error, r2_score

# Database configuration (adjust as per environment)
DB_URL = "postgresql://postgres:root@localhost:5432/MYDB"
CSV_FILE = "employees.csv"

def fetch_from_db():
    """Fetch employee data dynamically from the database."""
    try:
        engine = create_engine(DB_URL)
        # Join with department table to get department names
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

def process_data(df):
    """Common processing for both DB and CSV data."""
    if df.empty:
        return df
        
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

def run_analytics(df):
    print("\n--- Employee Analytics Summary ---")
    
    # 1. Average salary per department
    avg_salary = df.groupby('department')['salary'].mean().to_dict()
    print(f"Average Salary per Department: {avg_salary}")
    
    # 2. Identify top performers (score > 90)
    top_performers = df[df['performance_score'] > 90][['name', 'performance_score']].to_dict(orient='records')
    print(f"Top Performers: {top_performers}")
    
    # 3. Attrition Risk (Rule-based: low performance)
    attrition_risk = df[df['performance_score'] < 80][['name', 'role']].to_dict(orient='records')
    print(f"Attrition Risk (Low Performance): {attrition_risk}")
    
    # --- Module 6: AI/ML Concept Application ---
    print("\n--- Module 6: AI/ML Concept Application ---")
    
    # Pre-ML Cleaning: Drop any rows that still have NaNs in key features
    ml_df = df.dropna(subset=['years_experience', 'salary', 'performance_score']).copy()
    
    if len(ml_df) < 5:
        print("Warning: Insufficient data for reliable ML modeling.")
        mse, r2, coef, intercept = 0, 0, 0, 0
    else:
        # A. Supervised Learning: Linear Regression
        print("\n1. Supervised Learning: Salary Prediction (Regression)")
        print("   - Business Problem: Predicting fair salary based on professional experience.")
        print("   - Algorithm Choice: Linear Regression (suitable for continuous value prediction).")
        print("   - Feature Selection: 'years_experience' (Independent), 'salary' (Target/Label).")
        
        X = ml_df[['years_experience']]
        y = ml_df['salary']
        
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
        
        model = LinearRegression()
        model.fit(X_train, y_train)
        
        y_pred = model.predict(X_test)
        mse = mean_squared_error(y_test, y_pred)
        r2 = r2_score(y_test, y_pred)
        coef = model.coef_[0]
        intercept = model.intercept_
        
        print(f"   - Insight: Each year of experience adds approx. ${coef:.2f} (Model R2: {r2:.2f})")
        
        # B. Unsupervised Learning: K-Means Clustering
        print("\n2. Unsupervised Learning: Employee Segmentation (Clustering)")
        print("   - Business Problem: Grouping employees by behavior patterns without predefined labels.")
        print("   - Algorithm Choice: K-Means Clustering (identifies natural groupings in data).")
        print("   - Feature Selection: 'salary' and 'performance_score' to find high-value/low-cost clusters.")
        
        features = ml_df[['salary', 'performance_score']]
        scaler = StandardScaler()
        scaled_features = scaler.fit_transform(features)
        
        kmeans = KMeans(n_clusters=3, random_state=42, n_init=10)
        ml_df['segment'] = kmeans.fit_predict(scaled_features)
        print("   - Insight: Employee Segmentation completed based on productivity vs. cost.")

    # Visualization (using matplotlib from requirements.txt)
    plt.figure(figsize=(10, 6))
    plt.scatter(df['years_experience'], df['salary'], alpha=0.5)
    plt.title('Salary vs Experience')
    plt.xlabel('Years of Experience')
    plt.ylabel('Salary')
    plt.grid(True)
    plt.savefig('reports/salary_experience_plot.png')
    print("Visualization saved to 'reports/salary_experience_plot.png'")

    # Generate Reports
    if not os.path.exists('reports'):
        os.makedirs('reports')
        
    report_data = {
        'average_salary': avg_salary,
        'top_performers': top_performers,
        'attrition_risk': attrition_risk,
        'salary_regression': {
            'coefficient': coef,
            'intercept': intercept,
            'mse': mse,
            'r2_score': r2
        }
    }
    
    if 'segment' in ml_df.columns:
        report_data['segments'] = ml_df[['name', 'segment']].to_dict(orient='records')
    
    with open('reports/analytics_report.json', 'w') as f:
        json.dump(report_data, f, indent=4)
        
    df.groupby('department')['salary'].mean().to_csv('reports/department_salary_report.csv')
    df.to_csv('reports/full_analytics_data.csv', index=False)
    
    print("\nReports generated in 'reports/' directory.")

def main():
    print("Smart Employee Management Analytics Engine")
    
    # Force database connection (removed fallback to CSV as per request)
    df = fetch_from_db()
    
    if df is not None and not df.empty:
        run_analytics(df)
    else:
        print("Error: No data available from the database. Please ensure the database is running and populated.")

if __name__ == "__main__":
    main()
