import pandas as pd
import json
import os
import numpy as np
from sklearn.linear_model import LinearRegression
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler

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
        'attrition_risk': attrition_risk,
        'salary_regression': {
            'coefficient': model.coef_[0],
            'intercept': model.intercept_
        },
        'segments': df[['name', 'segment']].to_dict(orient='records')
    }
    
    with open('reports/analytics_report.json', 'w') as f:
        json.dump(report_data, f, indent=4)
        
    df.groupby('department')['salary'].mean().to_csv('reports/department_salary_report.csv')
    df.to_csv('reports/full_analytics_data.csv', index=False)
    
    print("\nReports generated in 'reports/' directory.")

def main():
    print("Smart Employee Management Analytics Engine")
    
    if os.path.exists('employees.csv'):
        df = pd.read_csv('employees.csv')
    else:
        df = generate_sample_data()
        
    run_analytics(df)

if __name__ == "__main__":
    main()
