# Company Management API

## Project Overview

This project is a RESTful API that showcases my abilities in backend development and DevOps to build and deploy a simple API for a company’s data, including departments, employees, and timecards. The API provides endpoints for CRUD operations on departments, employees, and timecards.

The API is built with Java, Maven, and runs on a GlassFish server. The project is configured to run in Visual Studio Code (VSCode) with the necessary dependencies managed through Maven.

### Key Features

- **Department Management**: Create, retrieve, update, and delete departments within a company.
- **Employee Management**: Manage employees, including their details and department affiliations.
- **Timecard Management**: Manage timecard records associated with each employee.
- **Company-wide Data Deletion**: Delete all records for a specific company, removing associated departments, employees, and timecards.

---

## Getting Started with VSCode

### Prerequisites

- **Java Development Kit (JDK)**: Ensure that you have Java 8 or above installed.
- **Maven**: Apache Maven is required to manage dependencies and build the project.
- **GlassFish Server**: The application is hosted on a local GlassFish server, which should be configured in your environment. I suggest to use the `Community Server Connector` extension. Run the Maven lifecycle and deploy the generated `.war` file on the server.

### Setup and Run

1. **Clone the Repository**

   ```bash
   git clone https://github.com/ff1574/company-api-java.git
   cd company-management-api
   ```

2. **Build the Project with Maven**

   - Open the VSCode terminal (`Ctrl+``) and run:
     ```bash
     mvn clean compile package
     ```
   - This command will compile the code, run tests, and package the application as a WAR file.

3. **Deploy the Project on GlassFish**

   - Find the generated `.war` file in the `target` folder and deploy it on the GlassFish server.

4. **Access the API**
   - Once deployed, the API will be available at `http://localhost:8080/project2-1.0-SNAPSHOT` by default, this might change depending on your GlassFish configuration.

---

## API Endpoints

## Department Endpoints

- **GET All Departments**: `GET http://localhost:8080/your-app-context/api/departments`

  - **Query Parameters**:
    - `company`: _string_ - The name of the company.

- **GET Single Department**: `GET http://localhost:8080/your-app-context/api/department`

  - **Query Parameters**:
    - `company`: _string_ - The name of the company.
    - `dept_id`: _integer_ - The department ID.

- **POST New Department**: `POST http://localhost:8080/your-app-context/api/department`

  - **Form Data**:
    - `company`: _string_ - The name of the company.
    - `dept_name`: _string_ - The name of the department.
    - `dept_no`: _string_ - The department number.
    - `location`: _string_ - The department location.

- **PUT Update Department**: `PUT http://localhost:8080/your-app-context/api/department`

  - **Body (JSON)**:
    ```json
    {
      "company": "your-company-name",
      "dept_id": 1,
      "dept_name": "Updated Engineering",
      "dept_no": "D001",
      "location": "Main Office"
    }
    ```

- **DELETE Department**: `DELETE http://localhost:8080/your-app-context/api/department`
  - **Query Parameters**:
    - `company`: _string_ - The name of the company.
    - `dept_id`: _integer_ - The department ID.

---

## Employee Endpoints

- **GET All Employees**: `GET http://localhost:8080/your-app-context/api/employees`

  - **Query Parameters**:
    - `company`: _string_ - The name of the company.

- **GET Single Employee**: `GET http://localhost:8080/your-app-context/api/employee`

  - **Query Parameters**:
    - `emp_id`: _integer_ - The employee ID.

- **POST New Employee**: `POST http://localhost:8080/your-app-context/api/employee`

  - **Form Data**:
    - `company`: _string_ - The name of the company.
    - `emp_name`: _string_ - The name of the employee.
    - `emp_no`: _string_ - The employee number.
    - `hire_date`: _string_ - The hire date (e.g., "2023-10-15").
    - `job`: _string_ - The job title.
    - `salary`: _double_ - The salary amount.
    - `dept_id`: _integer_ - The department ID.
    - `mng_id`: _integer_ (optional) - The manager ID.

- **PUT Update Employee**: `PUT http://localhost:8080/your-app-context/api/employee`

  - **Body (JSON)**:
    ```json
    {
      "company": "your-company-name",
      "emp_id": 1,
      "emp_name": "Jane Doe",
      "emp_no": "E123",
      "hire_date": "2023-10-15",
      "job": "Senior Developer",
      "salary": 70000,
      "dept_id": 1,
      "mng_id": 2
    }
    ```

- **DELETE Employee**: `DELETE http://localhost:8080/your-app-context/api/employee`
  - **Query Parameters**:
    - `emp_id`: _integer_ - The employee ID.

---

## Timecard Endpoints

- **GET All Timecards**: `GET http://localhost:8080/your-app-context/api/timecards`

  - **Query Parameters**:
    - `emp_id`: _integer_ - The employee ID.

- **GET Single Timecard**: `GET http://localhost:8080/your-app-context/api/timecard`

  - **Query Parameters**:
    - `timecard_id`: _integer_ - The timecard ID.

- **POST New Timecard**: `POST http://localhost:8080/your-app-context/api/timecard`

  - **Form Data**:
    - `start_time`: _string_ - Start time (e.g., "2024-10-25 08:00:00").
    - `end_time`: _string_ - End time (e.g., "2024-10-25 17:00:00").
    - `emp_id`: _integer_ - The employee ID.

- **PUT Update Timecard**: `PUT http://localhost:8080/your-app-context/api/timecard`

  - **Body (JSON)**:
    ```json
    {
      "timecard_id": 1,
      "emp_id": 1,
      "start_time": "2024-10-25 08:00:00",
      "end_time": "2024-10-25 17:00:00"
    }
    ```

- **DELETE Timecard**: `DELETE http://localhost:8080/your-app-context/api/timecard`
  - **Query Parameters**:
    - `timecard_id`: _integer_ - The timecard ID.

---

## Company-Wide Data Deletion

- **DELETE All Company Data**: `DELETE http://localhost:8080/your-app-context/api/company/deleteAll`
  - **Query Parameters**:
    - `company`: _string_ - The name of the company.

---

## Notes

- Ensure GlassFish is running on the configured port (default `8080`) before attempting to access the endpoints.
- This project uses `Gson` for JSON handling and `Maven` for dependency management.
