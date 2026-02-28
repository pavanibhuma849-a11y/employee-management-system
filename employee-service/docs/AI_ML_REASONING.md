# Module 6: AI/ML Concept Application

## 1. Business Problem Formulation
The primary goal is to optimize organizational performance and employee retention. We identified two key problems:
- **Salary Optimization**: Predicting fair salary levels based on experience to maintain market competitiveness.
- **Employee Segmentation**: Identifying distinct groups of employees based on their performance and compensation to tailor management strategies.

## 2. Feature Selection & Engineering
Relevant features extracted from the dataset include:
- `years_experience`: The primary independent variable for salary prediction.
- `salary`: Used for both trend prediction (dependent variable) and segmentation.
- `performance_score`: A key metric for identifying top performers and high-risk employees.
- `department`: Used to categorize and aggregate data for more granular insights.

## 3. Model Selection Reasoning
### Linear Regression (Supervised Learning)
- **Use Case**: Salary Trend Prediction.
- **Justification**: Salary typically follows a linear relationship with years of experience. Linear Regression provides a simple, interpretable coefficient that shows exactly how much "value" each additional year of experience adds to an employee's compensation.
- **Practical Enhancement**: Implemented a **Train/Test Split (80/20)** to evaluate the model's accuracy on unseen data. Used **Mean Squared Error (MSE)** and **R2 Score** to measure the model's predictive power.

### K-Means Clustering (Unsupervised Learning)
- **Use Case**: Employee Segmentation.
- **Justification**: We want to find hidden patterns in the data without predefined labels. By clustering employees based on `salary` and `performance_score`, we can identify groups such as "High Performers/Underpaid" (retention risk) or "Low Performers/Overpaid" (performance improvement candidates).

## 4. Supervised vs. Unsupervised Learning
- **Supervised Learning (Regression)**: We have "labels" (actual salaries) and we train the model to map inputs (experience) to these known outputs. Used for prediction.
- **Unsupervised Learning (Clustering)**: We don't have labels for the segments. The model groups data points based on inherent similarities in the feature space. Used for discovery.
