-- Department Table
CREATE TABLE IF NOT EXISTS department (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Employee Table
CREATE TABLE IF NOT EXISTS employee (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    salary DECIMAL(10, 2) NOT NULL,
    joining_date DATE NOT NULL,
    department_id INT REFERENCES department(id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Project Table
CREATE TABLE IF NOT EXISTS project (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    duration INT NOT NULL, -- duration in months
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Employee_Project Junction Table (Many-to-Many)
CREATE TABLE IF NOT EXISTS employee_project (
    employee_id INT REFERENCES employee(id) ON DELETE CASCADE,
    project_id INT REFERENCES project(id) ON DELETE CASCADE,
    PRIMARY KEY (employee_id, project_id)
);
