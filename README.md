# PerformTrack – Employee Performance Management System

**Subject:** Employability Skills Development Laboratory (ESDL)  
**Project Type:** Backend REST API  
**Developer:** Second Year B.Tech Student  

PerformTrack is a backend RESTful service developed with Java and Spring Boot to streamline employee performance appraisals. The system allows organizations to manage employee records, configure review cycles, assign and track individual goals, submit progressive updates, conduct manager reviews with ratings and feedback, and generate performance summaries.

Security and access control are enforced using Spring Security with HTTP Basic Authentication, ensuring that users only access resources permitted for their assigned role (Employee, Manager, or HR).

---

## 1. Features

- **Employee Management:** HR can add, view, update, and deactivate employee records, including assigning reporting managers.
- **User Accounts & Roles:** Dedicated login accounts linked to employees with three distinct roles: `EMPLOYEE`, `MANAGER`, and `HR`.
- **Review Cycle Management:** HR can create and manage appraisal cycles with defined start and end dates.
- **Goal Creation & Tracking:** Employees and managers can define measurable goals within active review cycles with target metrics.
- **Progress Updates:** Employees can log incremental progress values and notes against assigned goals.
- **Performance Reviews & Assessments:** Managers can evaluate assigned employees, provide quantitative ratings (1 to 5), and write textual feedback on draft reviews.
- **Review Workflow & Release:** Standardized status transition (`DRAFT` → `SUBMITTED` → `COMPLETED`), with an exclusive release mechanism for HR to publish reviews to employees.
- **Performance Summaries:** Aggregated performance summaries available per employee and across an entire review cycle.
- **Role-Based Access Control (RBAC):** Strict endpoint and service-level ownership checks preventing unauthorized access.

---

## 2. Technologies Used

- **Java (JDK 21):** Core programming language for application logic and business rules.
- **Spring Boot (v4.1.1):** Framework for developing robust and standalone REST APIs.
- **Spring Data JPA / Hibernate:** Object-Relational Mapping (ORM) to map Java entities to MySQL relational tables.
- **Spring Security:** Security framework providing HTTP Basic Authentication and role-based authorization.
- **MySQL:** Relational database management system for persistent data storage.
- **Apache Maven:** Project management, dependency resolution, and build automation tool.
- **Postman:** Tool used for API development, manual execution, and automated test assertion.

---

## 3. Project Structure

The project follows a standard layered architecture organized by domain packages:

```text
performtrack/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── DATABASE_SCHEMA.md
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── performtrack/
    │   │           ├── PerformtrackApplication.java
    │   │           ├── EmployeeRequest.java
    │   │           ├── common/
    │   │           │   ├── ApiError.java
    │   │           │   ├── BusinessRuleException.java
    │   │           │   ├── GlobalExceptionHandler.java
    │   │           │   └── ResourceNotFoundException.java
    │   │           ├── employee/
    │   │           │   ├── Employee.java
    │   │           │   ├── EmployeeController.java
    │   │           │   ├── EmployeeRepository.java
    │   │           │   ├── EmployeeResponse.java
    │   │           │   └── EmployeeService.java
    │   │           ├── goal/
    │   │           │   ├── Goal.java
    │   │           │   ├── GoalController.java
    │   │           │   ├── GoalRepository.java
    │   │           │   ├── GoalRequest.java
    │   │           │   ├── GoalResponse.java
    │   │           │   ├── GoalService.java
    │   │           │   ├── GoalUpdate.java
    │   │           │   ├── GoalUpdateController.java
    │   │           │   ├── GoalUpdateRepository.java
    │   │           │   ├── GoalUpdateRequest.java
    │   │           │   ├── GoalUpdateResponse.java
    │   │           │   └── GoalUpdateService.java
    │   │           ├── review/
    │   │           │   ├── PerformanceReview.java
    │   │           │   ├── PerformanceReviewController.java
    │   │           │   ├── PerformanceReviewRepository.java
    │   │           │   ├── PerformanceReviewRequest.java
    │   │           │   ├── PerformanceReviewResponse.java
    │   │           │   ├── PerformanceReviewService.java
    │   │           │   ├── ReviewAssessmentRequest.java
    │   │           │   ├── ReviewReleaseRequest.java
    │   │           │   └── ReviewStatusRequest.java
    │   │           ├── reviewcycle/
    │   │           │   ├── ReviewCycle.java
    │   │           │   ├── ReviewCycleController.java
    │   │           │   ├── ReviewCycleRepository.java
    │   │           │   ├── ReviewCycleRequest.java
    │   │           │   ├── ReviewCycleResponse.java
    │   │           │   └── ReviewCycleService.java
    │   │           ├── security/
    │   │           │   ├── CreateUserRequest.java
    │   │           │   ├── CurrentUserService.java
    │   │           │   ├── CustomUserDetailsService.java
    │   │           │   ├── Role.java
    │   │           │   ├── SecurityConfig.java
    │   │           │   ├── SecurityExceptionHandler.java
    │   │           │   ├── UserAccount.java
    │   │           │   ├── UserAccountController.java
    │   │           │   ├── UserAccountRepository.java
    │   │           │   ├── UserAccountResponse.java
    │   │           │   └── UserAccountService.java
    │   │           └── summary/
    │   │               ├── CyclePerformanceSummary.java
    │   │               ├── EmployeePerformanceSummary.java
    │   │               ├── SummaryController.java
    │   │               └── SummaryService.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/
            └── com/
                └── performtrack/
                    └── PerformtrackApplicationTests.java
```

---

## 4. Database Setup

- **Database Name:** `performtrack`
- **Tables:**
  - `employees`: Stores employee profile, designation, department, and reporting manager relationship.
  - `user_accounts`: Stores user credentials, BCrypt password hashes, and user roles.
  - `review_cycles`: Stores appraisal cycle date boundaries and active statuses.
  - `goals`: Stores performance goals linked to an employee and a review cycle.
  - `goal_updates`: Stores progressive values and logs for goals.
  - `performance_reviews`: Stores ratings, feedback text, status, and release flags.

### Creating the Database in MySQL
Log into MySQL using the command line or MySQL Workbench and run:

```sql
CREATE DATABASE performtrack;
```

### Configuring Connection in `application.properties`
The database connection is configured in `src/main/resources/application.properties`:

```properties
spring.application.name=performtrack

# MySQL connection settings
spring.datasource.url=jdbc:mysql://localhost:3306/performtrack
spring.datasource.username=root
spring.datasource.password=your_mysql_password_here

# JPA and Hibernate configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

server.port=8080
```
*(Replace `your_mysql_password_here` with your local MySQL password. Never commit production secrets.)*

---

## 5. How to Run the Project

### Step 1: Prerequisites
Ensure you have the following installed on your machine:
- **Java Development Kit (JDK 21 or higher)**
- **MySQL Server (8.0 or higher)**
- **Git** (optional)

### Step 2: Create MySQL Database
Ensure MySQL service is running and execute:
```sql
CREATE DATABASE performtrack;
```

### Step 3: Configure Database Credentials
Open `src/main/resources/application.properties` and verify your MySQL `username` and `password`.

### Step 4: Open Terminal
Open a terminal (PowerShell, Command Prompt, or bash) in the project root directory (`performtrack`).

### Step 5: Start the Application Using Maven Wrapper
- **Windows (Command Prompt / PowerShell):**
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```
- **Linux / macOS:**
  ```bash
  ./mvnw spring-boot:run
  ```

### Step 6: Verify Application Startup
The application runs on port **`8080`**. Look for the following log output in the terminal:
```text
INFO ... o.s.b.w.e.t.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
INFO ... c.p.PerformtrackApplication   : Started PerformtrackApplication in ... seconds
```
Once this message appears, the REST API is ready to accept requests at `http://localhost:8080`.

---

## 6. API Endpoints

All protected endpoints require HTTP Basic Authentication with appropriate user credentials.

### 1. Authentication Endpoints
| HTTP Method | Endpoint URL | Description | Required Role |
|---|---|---|---|
| `POST` | `/api/auth/accounts` | Create a new user account linked to an employee | `HR` |

### 2. Employee Endpoints
| HTTP Method | Endpoint URL | Description | Required Role |
|---|---|---|---|
| `POST` | `/api/employees` | Create a new employee record | `HR` |
| `GET` | `/api/employees` | Retrieve a list of all employees | `HR` |
| `GET` | `/api/employees/{id}` | Retrieve details of a specific employee by ID | `HR` |
| `PUT` | `/api/employees/{id}` | Update employee details | `HR` |
| `DELETE` | `/api/employees/{id}` | Deactivate an employee (soft delete, returns 204) | `HR` |

### 3. Review Cycle Endpoints
| HTTP Method | Endpoint URL | Description | Required Role |
|---|---|---|---|
| `POST` | `/api/review-cycles` | Create a new appraisal cycle | `HR` |
| `GET` | `/api/review-cycles` | Retrieve all appraisal cycles | `HR` |
| `GET` | `/api/review-cycles/{id}` | Retrieve a specific review cycle by ID | `HR` |
| `PUT` | `/api/review-cycles/{id}` | Update review cycle dates and details | `HR` |
| `DELETE` | `/api/review-cycles/{id}` | Deactivate a review cycle (returns 204) | `HR` |

### 4. Goal Endpoints
| HTTP Method | Endpoint URL | Description | Required Role |
|---|---|---|---|
| `POST` | `/api/goals` | Create a performance goal | `EMPLOYEE`, `MANAGER`, `HR` |
| `GET` | `/api/goals` | List all goals accessible to the user | `EMPLOYEE`, `MANAGER`, `HR` |
| `GET` | `/api/goals/{id}` | Get goal by ID (with ownership validation) | `EMPLOYEE`, `MANAGER`, `HR` |
| `GET` | `/api/goals/employee/{employeeId}` | Get all goals for a specific employee | `EMPLOYEE`, `MANAGER`, `HR` |
| `GET` | `/api/goals/cycle/{cycleId}` | Get goals belonging to a review cycle | `EMPLOYEE`, `MANAGER`, `HR` |
| `PUT` | `/api/goals/{id}` | Update goal details and target value | `EMPLOYEE`, `MANAGER`, `HR` |
| `DELETE` | `/api/goals/{id}` | Delete a goal (returns 204) | `EMPLOYEE`, `MANAGER`, `HR` |

### 5. Goal Update Endpoints
| HTTP Method | Endpoint URL | Description | Required Role |
|---|---|---|---|
| `POST` | `/api/goals/{goalId}/updates` | Add progressive value update to a goal | `EMPLOYEE`, `MANAGER`, `HR` |
| `GET` | `/api/goals/{goalId}/updates` | View history of progress updates for a goal | `EMPLOYEE`, `MANAGER`, `HR` |

### 6. Performance Review Endpoints
| HTTP Method | Endpoint URL | Description | Required Role |
|---|---|---|---|
| `POST` | `/api/reviews` | Create a review in `DRAFT` status | `MANAGER`, `HR` |
| `GET` | `/api/reviews` | Retrieve all accessible performance reviews | `EMPLOYEE`, `MANAGER`, `HR` |
| `GET` | `/api/reviews/{id}` | Retrieve review details by ID | `EMPLOYEE`, `MANAGER`, `HR` |
| `GET` | `/api/reviews/employee/{employeeId}` | Retrieve reviews for a specific employee | `EMPLOYEE`, `MANAGER`, `HR` |
| `GET` | `/api/reviews/employee/{employeeId}/released` | Retrieve only released reviews for an employee | `EMPLOYEE`, `MANAGER`, `HR` |
| `PUT` | `/api/reviews/{id}/assessment` | Add rating (1 to 5) and feedback to a draft review | `MANAGER`, `HR` |
| `PATCH` | `/api/reviews/{id}/status` | Advance status (`DRAFT` → `SUBMITTED` → `COMPLETED`) | `MANAGER`, `HR` |
| `PATCH` | `/api/reviews/{id}/release` | Release a completed review to the employee | `HR` |

### 7. Performance Summary Endpoints
| HTTP Method | Endpoint URL | Description | Required Role |
|---|---|---|---|
| `GET` | `/api/summaries/employees/{employeeId}` | Get aggregated performance metrics for an employee | `EMPLOYEE`, `MANAGER`, `HR` |
| `GET` | `/api/summaries/cycles/{cycleId}` | Get cycle-wide aggregated appraisal metrics | `HR` |

---

## 7. Authentication and Authorization Rules

The application uses Spring Security with **HTTP Basic Authentication**. Passwords are encrypted using BCrypt (`BCryptPasswordEncoder`).

### Role Permissions:
- **`HR` (Human Resources):**
  - Manages employee profiles and user accounts.
  - Creates, updates, and closes review cycles.
  - Can view all goals, reviews, and cycle-wide summaries.
  - **Exclusive privilege:** Only HR can release completed performance reviews.
- **`MANAGER` (Reporting Manager):**
  - Manages and monitors goals of assigned direct-report employees.
  - Conducts performance assessments by assigning ratings (1–5) and written feedback.
  - Submits reviews through the evaluation lifecycle.
  - Can view performance summaries for assigned employees only.
- **`EMPLOYEE` (Individual Contributor):**
  - Creates and manages their own performance goals.
  - Posts progress updates and notes on their goals.
  - Views their own reviews and summaries.
  - Can view released reviews once published by HR.
  - Cannot access administrative endpoints or other employees' data.

---

## 8. Testing with Postman

An automated test collection titled **`PerformTrack`** is provided to test all endpoints.

### Steps to Test:
1. Open the **Postman** application.
2. Select any request from the 8 folders in the **PerformTrack** collection.
3. In the request configuration, navigate to the **Authorization** tab:
   - **Type:** `Basic Auth`
   - **Username:** Enter the demo username (e.g., `hradmin` or `priya`).
   - **Password:** Enter the demo password (e.g., `HrDemo123!` or `PriyaDemo123!`).
4. Click **Send**.
5. Check the response body and the **Test Results** tab in Postman to confirm status code assertions (e.g., `200 OK`, `201 Created`, `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`).

*Note: Requests in the collection use direct URLs (`http://localhost:8080`) and direct credentials without requiring separate Postman environments.*

---

## 9. Example Demo Accounts

The following demo accounts are pre-configured for local testing:

| Role | Username | Local Demo Password | Linked Employee | Description |
|---|---|---|---|---|
| **HR** | `hradmin` | `HrDemo123!` | None | HR Administrator with full administrative access |
| **Manager** | `rahul` | `RahulDemo123!` | Employee ID 1 (Rahul Sharma) | Reporting manager for IT department |
| **Employee** | `priya` | `PriyaDemo123!` | Employee ID 2 (Priya Patil) | Software developer reporting to Rahul |
| **Employee** | `amit` | `AmitDemo123!` | Employee ID 3 (Amit Deshmukh) | Software developer reporting to Rahul |

> **Warning:** These demo accounts and passwords are strictly intended for local educational and assignment demonstration purposes. Never use hardcoded or simple passwords in a production deployment.

---

## 10. Future Scope

1. **JWT (JSON Web Token) Authentication:** Upgrade from Basic Authentication to stateless JWT tokens with refresh token rotation for enhanced session management.
2. **Email Notifications:** Integrate Spring Boot Mail starter to notify employees when goals are assigned, updates are requested, or reviews are released.
3. **File Attachments for Goals:** Allow employees to upload supporting documents or project links as tangible evidence of goal completion.
4. **Analytics & Graphical Dashboard:** Provide statistical visual charts (e.g., bell curves, goal completion percentages) to assist HR in organizational performance reviews.

---

## 11. Conclusion

PerformTrack demonstrates a clean, layered Spring Boot backend architecture implementing core enterprise performance management workflows. By combining Spring Data JPA with Spring Security, the project enforces data integrity and role-based authorization, providing a reliable and structured REST API for student evaluation in the ESDL laboratory.
