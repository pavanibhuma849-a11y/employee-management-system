-- Insert Departments
INSERT INTO department (name) VALUES ('Human Resources');
INSERT INTO department (name) VALUES ('Engineering');
INSERT INTO department (name) VALUES ('Marketing');
INSERT INTO department (name) VALUES ('Finance');

-- Insert Employees
INSERT INTO employee (name, role, salary, joining_date, department_id) VALUES ('Alice Johnson', 'HR Manager', 75000.00, '2022-01-15', 1);
INSERT INTO employee (name, role, salary, joining_date, department_id) VALUES ('Bob Smith', 'Senior Developer', 95000.00, '2021-06-20', 2);
INSERT INTO employee (name, role, salary, joining_date, department_id) VALUES ('Charlie Brown', 'Junior Developer', 60000.00, '2023-03-10', 2);
INSERT INTO employee (name, role, salary, joining_date, department_id) VALUES ('Diana Prince', 'Marketing Lead', 82000.00, '2022-11-05', 3);
INSERT INTO employee (name, role, salary, joining_date, department_id) VALUES ('Ethan Hunt', 'Financial Analyst', 78000.00, '2020-08-12', 4);

-- Insert Projects
INSERT INTO project (name, duration) VALUES ('Cloud Migration', 12);
INSERT INTO project (name, duration) VALUES ('Q3 Marketing Campaign', 3);
INSERT INTO project (name, duration) VALUES ('Employee Portal Redesign', 6);

-- Associate Employees with Projects (Many-to-Many)
INSERT INTO employee_project (employee_id, project_id) VALUES (2, 1); -- Bob on Cloud Migration
INSERT INTO employee_project (employee_id, project_id) VALUES (3, 1); -- Charlie on Cloud Migration
INSERT INTO employee_project (employee_id, project_id) VALUES (4, 2); -- Diana on Marketing Campaign
INSERT INTO employee_project (employee_id, project_id) VALUES (2, 3); -- Bob on Portal Redesign
INSERT INTO employee_project (employee_id, project_id) VALUES (3, 3); -- Charlie on Portal Redesign
INSERT INTO employee_project (employee_id, project_id) VALUES (1, 3); -- Alice on Portal Redesign
