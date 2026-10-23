# PerformTrack - Database Schema

**Subject:** Employability Skills Development Laboratory (ESDL)  
**Project Title:** PerformTrack – Employee Performance Management System  
**Database:** MySQL  

---

## 1. Introduction

This document contains the database schema for the **PerformTrack** system. The database is designed in MySQL using Spring Boot JPA / Hibernate to store and manage employee details, user login accounts, review cycles, goals, progress updates, and performance reviews.

All tables use a primary key `id` with auto-increment. Foreign keys are used to link related entities such as employees to their managers, goals to employees, and reviews to cycles. 

*(Note: Feedback does not have a separate table; it is stored directly as a column inside the `performance_reviews` table.)*

---

## 2. Table Details

### 1. `employees` Table
Stores basic information about each employee and links them to their reporting manager.

| Column Name | Data Type | Key |
|---|---|---|
| `id` | BIGINT | PK |
| `full_name` | VARCHAR(100) | - |
| `email` | VARCHAR(150) | - |
| `department` | VARCHAR(255) | - |
| `designation` | VARCHAR(255) | - |
| `joining_date` | DATE | - |
| `active` | BOOLEAN / BIT | - |
| `manager_id` | BIGINT | FK (references `employees.id`) |

---

### 2. `user_accounts` Table
Stores authentication details, hashed passwords, and system roles for logging in.

| Column Name | Data Type | Key |
|---|---|---|
| `id` | BIGINT | PK |
| `username` | VARCHAR(255) | - |
| `password_hash` | VARCHAR(255) | - |
| `role` | VARCHAR(20) / ENUM | - |
| `employee_id` | BIGINT | FK (references `employees.id`) |

---

### 3. `review_cycles` Table
Stores performance appraisal cycles with start and end dates.

| Column Name | Data Type | Key |
|---|---|---|
| `id` | BIGINT | PK |
| `name` | VARCHAR(100) | - |
| `start_date` | DATE | - |
| `end_date` | DATE | - |
| `active` | BOOLEAN / BIT | - |

---

### 4. `goals` Table
Stores individual performance goals assigned to employees for a specific review cycle.

| Column Name | Data Type | Key |
|---|---|---|
| `id` | BIGINT | PK |
| `title` | VARCHAR(150) | - |
| `description` | VARCHAR(1000) | - |
| `target_value` | DECIMAL(10,2) | - |
| `current_value` | DECIMAL(10,2) | - |
| `unit` | VARCHAR(50) | - |
| `due_date` | DATE | - |
| `status` | VARCHAR(30) | - |
| `employee_id` | BIGINT | FK (references `employees.id`) |
| `review_cycle_id` | BIGINT | FK (references `review_cycles.id`) |

---

### 5. `goal_updates` Table
Stores incremental progress updates and logs submitted for a goal.

| Column Name | Data Type | Key |
|---|---|---|
| `id` | BIGINT | PK |
| `progress_value` | DECIMAL(10,2) | - |
| `note` | VARCHAR(1000) | - |
| `updated_at` | DATETIME | - |
| `goal_id` | BIGINT | FK (references `goals.id`) |

---

### 6. `performance_reviews` Table
Stores final evaluations, ratings, manager feedback, and workflow status for an employee in a cycle.

| Column Name | Data Type | Key |
|---|---|---|
| `id` | BIGINT | PK |
| `rating` | INT | - |
| `feedback` | VARCHAR(2000) | - |
| `status` | VARCHAR(20) | - |
| `released` | BOOLEAN / BIT | - |
| `employee_id` | BIGINT | FK (references `employees.id`) |
| `manager_id` | BIGINT | FK (references `employees.id`) |
| `review_cycle_id` | BIGINT | FK (references `review_cycles.id`) |

---

## 3. Conclusion

The above schema covers all the required entities for the PerformTrack Employee Performance Management System. The tables maintain proper relational integrity using foreign keys to support core operations like goal tracking, regular updates, and performance evaluations.
