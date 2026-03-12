-- Insert Departments
INSERT INTO department (name) VALUES ('Human Resources') ON CONFLICT (name) DO NOTHING;
INSERT INTO department (name) VALUES ('Engineering') ON CONFLICT (name) DO NOTHING;
INSERT INTO department (name) VALUES ('Marketing') ON CONFLICT (name) DO NOTHING;
INSERT INTO department (name) VALUES ('Finance') ON CONFLICT (name) DO NOTHING;

-- Insert Employees
INSERT INTO employee (name, email, role, salary, joining_date, department_id) VALUES ('Alice Johnson', 'alice.johnson@company.com', 'HR Manager', 75000.00, '2022-01-15', 1) ON CONFLICT (email) DO NOTHING;
INSERT INTO employee (name, email, role, salary, joining_date, department_id) VALUES ('Bob Smith', 'bob.smith@company.com', 'Senior Developer', 95000.00, '2021-06-20', 2) ON CONFLICT (email) DO NOTHING;
INSERT INTO employee (name, email, role, salary, joining_date, department_id) VALUES ('Charlie Brown', 'charlie.brown@company.com', 'Junior Developer', 60000.00, '2023-03-10', 2) ON CONFLICT (email) DO NOTHING;
INSERT INTO employee (name, email, role, salary, joining_date, department_id) VALUES ('Diana Prince', 'diana.prince@company.com', 'Marketing Lead', 82000.00, '2022-11-05', 3) ON CONFLICT (email) DO NOTHING;
INSERT INTO employee (name, email, role, salary, joining_date, department_id) VALUES ('Ethan Hunt', 'ethan.hunt@company.com', 'Financial Analyst', 78000.00, '2020-08-12', 4) ON CONFLICT (email) DO NOTHING;

-- Insert Projects
INSERT INTO project (name, duration, start_date, end_date) VALUES ('Cloud Migration', 12, '2024-01-01', '2024-12-31') ON CONFLICT (name) DO NOTHING;
INSERT INTO project (name, duration, start_date, end_date) VALUES ('Q3 Marketing Campaign', 3, '2024-07-01', '2024-09-30') ON CONFLICT (name) DO NOTHING;
INSERT INTO project (name, duration, start_date, end_date) VALUES ('Employee Portal Redesign', 6, '2024-03-01', '2024-08-31') ON CONFLICT (name) DO NOTHING;

-- Associate Employees with Projects (Many-to-Many)
-- Primary key in schema.sql is (employee_id, project_id)
INSERT INTO employee_project (employee_id, project_id) VALUES (2, 1) ON CONFLICT (employee_id, project_id) DO NOTHING;
INSERT INTO employee_project (employee_id, project_id) VALUES (3, 1) ON CONFLICT (employee_id, project_id) DO NOTHING;
INSERT INTO employee_project (employee_id, project_id) VALUES (4, 2) ON CONFLICT (employee_id, project_id) DO NOTHING;
INSERT INTO employee_project (employee_id, project_id) VALUES (2, 3) ON CONFLICT (employee_id, project_id) DO NOTHING;
INSERT INTO employee_project (employee_id, project_id) VALUES (3, 3) ON CONFLICT (employee_id, project_id) DO NOTHING;
INSERT INTO employee_project (employee_id, project_id) VALUES (1, 3) ON CONFLICT (employee_id, project_id) DO NOTHING;
