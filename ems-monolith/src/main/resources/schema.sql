-- Department Table
CREATE TABLE IF NOT EXISTS department (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Employee Table
CREATE TABLE IF NOT EXISTS employee (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    salary DECIMAL(10, 2) NOT NULL,
    joining_date DATE NOT NULL,
    department_id INT REFERENCES department(id)
);

-- Project Table
CREATE TABLE IF NOT EXISTS project (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    duration INT NOT NULL -- duration in months
);

-- Employee_Project Junction Table (Many-to-Many)
CREATE TABLE IF NOT EXISTS employee_project (
    employee_id INT REFERENCES employee(id),
    project_id INT REFERENCES project(id),
    PRIMARY KEY (employee_id, project_id)
);
