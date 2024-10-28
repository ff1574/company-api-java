
# Company Management API

## Project Overview

This project is a RESTful API for managing a company’s organizational data, including departments, employees, and timecards. The API provides endpoints for CRUD (Create, Read, Update, Delete) operations on departments, employees, and timecards, as well as a delete-all operation for removing all data associated with a particular company.

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
- **GlassFish Server**: The application is hosted on a local GlassFish server, which should be configured in your environment.
- **VSCode Extensions**:
  - **Java Extension Pack**: Includes essential extensions like `Debugger for Java`, `Maven for Java`, `Java Test Runner`, etc.

### Setup and Run

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/company-management-api.git
   cd company-management-api
   ```

2. **Open Project in VSCode**
   - Launch Visual Studio Code and open the project folder (`company-management-api`).

3. **Configure GlassFish Server in VSCode**
   - In the VSCode Command Palette (`Ctrl+Shift+P`), search for `Java: Add Server`.
   - Add your GlassFish server path and configure it to deploy the application.

4. **Build the Project with Maven**
   - Open the VSCode terminal (`Ctrl+``) and run:
     ```bash
     mvn clean install
     ```
   - This command will compile the code, run tests, and package the application as a WAR file.

5. **Deploy the Project on GlassFish**
   - Right-click on the project in the VSCode Explorer and select `Run on Server` or deploy the WAR file manually on your configured GlassFish server.

6. **Access the API**
   - Once deployed, the API will be available at `http://localhost:8080/your-app-context/`.

---

## Project Structure

The primary packages and files include:

- **`business`**: Contains business logic classes such as `DepartmentBusiness`, `EmployeeBusiness`, and `TimecardBusiness`.
- **`service`**: Contains REST endpoint classes for handling HTTP requests for each entity (`DepartmentResource`, `EmployeeResource`, `TimecardResource`, and `CompanyResource`).
- **`response`**: Contains classes for standardized success and error responses.
- **`companydata`**: Contains data model classes (`Department`, `Employee`, `Timecard`) representing the company’s entities.

---

## API Endpoints

### Department Endpoints

- **GET /departments**: Retrieve all departments for a specified company.
  ```bash
  curl -X GET "http://localhost:8080/your-app-context/departments?company=your-company-name"
  ```

- **GET /department**: Retrieve a single department by company and department ID.
  ```bash
  curl -X GET "http://localhost:8080/your-app-context/department?company=your-company-name&dept_id=1"
  ```

- **POST /department**: Insert a new department.
  ```bash
  curl -X POST "http://localhost:8080/your-app-context/department"        -d "company=your-company-name"        -d "dept_name=Engineering"        -d "dept_no=D001"        -d "location=Headquarters"
  ```

- **PUT /department**: Update an existing department.
  ```bash
  curl -X PUT "http://localhost:8080/your-app-context/department"        -H "Content-Type: application/json"        -d '{
             "company": "your-company-name",
             "dept_id": 1,
             "dept_name": "Updated Engineering",
             "dept_no": "D001",
             "location": "Main Office"
           }'
  ```

- **DELETE /department**: Delete a specific department by ID.
  ```bash
  curl -X DELETE "http://localhost:8080/your-app-context/department?company=your-company-name&dept_id=1"
  ```

### Employee Endpoints

- **GET /employees**: Retrieve all employees for a specified company.
  ```bash
  curl -X GET "http://localhost:8080/your-app-context/employees?company=your-company-name"
  ```

- **GET /employee**: Retrieve a single employee by ID.
  ```bash
  curl -X GET "http://localhost:8080/your-app-context/employee?emp_id=1"
  ```

- **POST /employee**: Insert a new employee.
  ```bash
  curl -X POST "http://localhost:8080/your-app-context/employee"        -d "company=your-company-name"        -d "emp_name=John Doe"        -d "emp_no=E123"        -d "hire_date=2023-10-15"        -d "job=Developer"        -d "salary=60000"        -d "dept_id=1"        -d "mng_id=2"
  ```

- **PUT /employee**: Update an existing employee.
  ```bash
  curl -X PUT "http://localhost:8080/your-app-context/employee"        -H "Content-Type: application/json"        -d '{
             "company": "your-company-name",
             "emp_id": 1,
             "emp_name": "Jane Doe",
             "emp_no": "E123",
             "hire_date": "2023-10-15",
             "job": "Senior Developer",
             "salary": 70000,
             "dept_id": 1,
             "mng_id": 2
           }'
  ```

- **DELETE /employee**: Delete an employee by ID.
  ```bash
  curl -X DELETE "http://localhost:8080/your-app-context/employee?emp_id=1"
  ```

### Timecard Endpoints

- **GET /timecards**: Retrieve all timecards for a specified employee.
  ```bash
  curl -X GET "http://localhost:8080/your-app-context/timecards?emp_id=1"
  ```

- **GET /timecard**: Retrieve a specific timecard by ID.
  ```bash
  curl -X GET "http://localhost:8080/your-app-context/timecard?timecard_id=1"
  ```

- **POST /timecard**: Insert a new timecard.
  ```bash
  curl -X POST "http://localhost:8080/your-app-context/timecard"        -d "start_time=2024-10-25 08:00:00"        -d "end_time=2024-10-25 17:00:00"        -d "emp_id=1"
  ```

- **PUT /timecard**: Update an existing timecard.
  ```bash
  curl -X PUT "http://localhost:8080/your-app-context/timecard"        -H "Content-Type: application/json"        -d '{
             "timecard_id": 1,
             "emp_id": 1,
             "start_time": "2024-10-25 08:00:00",
             "end_time": "2024-10-25 17:00:00"
           }'
  ```

- **DELETE /timecard**: Delete a specific timecard by ID.
  ```bash
  curl -X DELETE "http://localhost:8080/your-app-context/timecard?timecard_id=1"
  ```

### Company-Wide Data Deletion

- **DELETE /company/deleteAll**: Deletes all data for a specified company, including all departments, employees, and timecards.
  ```bash
  curl -X DELETE "http://localhost:8080/your-app-context/company/deleteAll?company=your-company-name"
  ```

---

## Contributing

If you’d like to contribute to this project, feel free to open a pull request or submit an issue.

---

## License

This project is open-source and available under the MIT License.

---

## Notes

- Ensure GlassFish is running on the configured port (default `8080`) before attempting to access the endpoints.
- This project uses `Gson` for JSON handling and `Maven` for dependency management.
