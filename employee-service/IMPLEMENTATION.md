# Implementation: Smart Employee Management & Performance Analytics System

## Phase 1: Database & Core API Implementation

### 1. Database Schema
The database schema is defined in `src/main/resources/schema.sql`. It includes:
- `department`: Stores department names.
- `employee`: Stores employee details with a foreign key to `department`.
- `project`: Stores project info.
- `employee_project`: Junction table for Many-to-Many relationship.

### 2. Core Entities (Models)
Located in `com.company.ems.model`:
- **Employee**: Implements `Comparable` for natural sorting by salary. Includes relationships to Department and Projects. Has its own primary key `id`.
- **Department**: One-to-Many relationship with Employees. Has its own primary key `id`.
- **Project**: Many-to-Many relationship with Employees. Has its own primary key `id`.

### 3. Service Layer Refactoring
- **IEmployeeService**: Interface defining the business logic contract.
- **EmployeeServiceImpl**: Implementation class for the service interface, annotated with `@Service`.
- **Controller Injection**: The `EmployeeController` now injects `IEmployeeService` instead of the concrete implementation, adhering to the Dependency Inversion Principle.

### 4. REST API Endpoints
Implemented in `EmployeeController`:

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/employees` | Create a new employee |
| GET | `/employees/{id}` | Retrieve an employee by ID |
| GET | `/employees?department={name}` | Filter employees by department name |
| PUT | `/employees/{id}` | Update employee details |
| DELETE | `/employees/{id}` | Remove an employee record |

### 4. Exception Handling
- **EmployeeNotFoundException**: Custom exception for missing records.
- **GlobalExceptionHandler**: Uses `@ControllerAdvice` to return standardized JSON error responses.

## Phase 2: Microservices Architecture (Module 4)

The application has been split into microservices for better scalability:

### 1. Eureka Server (`eureka-server`)
- **Port**: 8761
- **Role**: Service registry where all services register themselves.
- **Access**: [http://localhost:8761](http://localhost:8761)

### 2. Employee Service (`employee-service` - formerly monolith)
- **Port**: 8080
- **Role**: Manages core entities (Employee, Department, Project) and REST APIs.
- **Discovery**: Registers as `employee-service` with Eureka.

### 3. Reporting Service (`reporting-service`)
- **Port**: 8082
- **Role**: Handles background report generation tasks.
- **Inter-service Communication**: Uses **Spring Cloud OpenFeign** to fetch data from `employee-service`.
- **Background Task**: Scheduled task runs every 1 minute to generate reports based on data from the Employee Service.

## Running the Microservices
1. Start **Eureka Server** first.
2. Start **Employee Service**.
3. Start **Reporting Service**.
