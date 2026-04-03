# Finance Dashboard Backend

Backend assignment submission for a finance dashboard system with role-based access control, financial record management, dashboard summaries, validation, and error handling.

## Overview

This project implements a backend for a finance dashboard where different users interact with financial data based on their role.

The API supports:

- user creation and management
- role-based access control
- financial record CRUD operations
- dashboard analytics and aggregated summaries
- validation and structured error responses
- seeded demo users and sample records for quick evaluation

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Security
- Spring Data JPA
- H2 in-memory database
- Maven

## Why This Approach

I chose Spring Boot with JPA and H2 to keep the assignment focused on backend design, API correctness, access control, and business logic rather than infrastructure complexity.

Key design choices:

- H2 is used for fast local setup and easy evaluation
- HTTP Basic Auth is used to keep authentication simple and transparent
- method-level authorization is used to clearly enforce permissions
- the code is separated into controller, service, repository, DTO, config, and exception layers

## Features Implemented

### 1. User and Role Management

The system supports:

- creating users
- updating users
- fetching all users
- fetching a user by id
- assigning a role
- marking users as `ACTIVE` or `INACTIVE`

Supported roles:

- `VIEWER`
- `ANALYST`
- `ADMIN`

### 2. Financial Records Management

Each financial record contains:

- amount
- type (`INCOME` or `EXPENSE`)
- category
- date
- notes
- creator information

Supported operations:

- create a record
- get all records
- get a record by id
- update a record
- delete a record
- filter by type, category, and date range

### 3. Dashboard Summary APIs

The backend exposes aggregated dashboard data, including:

- total income
- total expenses
- net balance
- category-wise totals
- monthly trends
- recent activity

### 4. Access Control

Permissions are enforced in the backend.

- `VIEWER`
  - can access dashboard summary APIs
  - cannot access record management
  - cannot manage users
- `ANALYST`
  - can view records
  - can access dashboard summary APIs
  - cannot create, update, or delete records
  - cannot manage users
- `ADMIN`
  - full access to users
  - full access to financial records
  - full access to dashboard APIs

Inactive users are blocked from authenticating.

### 5. Validation and Error Handling

The project includes:

- bean validation on request payloads
- meaningful HTTP status codes
- structured JSON error responses
- duplicate email checks
- invalid date range checks
- resource not found handling

## Project Structure

```text
src/main/java/com/finance/dashboard
|- config
|- controller
|- dto
|- exception
|- model
|- repository
|- security
|- service
```

High-level responsibilities:

- `controller`: request handling and API exposure
- `service`: business logic and aggregation logic
- `repository`: persistence access
- `model`: enums and JPA entities
- `dto`: request and response payloads
- `security`: authentication user lookup
- `config`: security and seed data setup
- `exception`: global exception handling

## Authentication

The API uses HTTP Basic Authentication.

Demo credentials are seeded automatically on startup:

- `admin@finance.local` / `Admin@123`
- `analyst@finance.local` / `Analyst@123`
- `viewer@finance.local` / `Viewer@123`
- `inactive@finance.local` / `Inactive@123`

## API Endpoints

### Public

- `GET /api/health`

### Users

- `GET /api/users`
- `GET /api/users/{id}`
- `POST /api/users`
- `PUT /api/users/{id}`

Access:

- admin only

### Financial Records

- `GET /api/records`
- `GET /api/records/{id}`
- `POST /api/records`
- `PUT /api/records/{id}`
- `DELETE /api/records/{id}`

Access:

- `GET`: admin, analyst
- `POST`, `PUT`, `DELETE`: admin only

Filters on `GET /api/records`:

- `type=INCOME|EXPENSE`
- `category=Groceries`
- `from=2026-03-01`
- `to=2026-03-31`

### Dashboard

- `GET /api/dashboard/summary`
- `GET /api/dashboard/summary?from=2026-03-01&to=2026-03-31`

Access:

- admin, analyst, viewer

## Sample Requests

### Health Check

```bash
curl http://localhost:8080/api/health
```

### Get Dashboard Summary as Viewer

```bash
curl -u viewer@finance.local:Viewer@123 \
  http://localhost:8080/api/dashboard/summary
```

### Get Records as Analyst

```bash
curl -u analyst@finance.local:Analyst@123 \
  http://localhost:8080/api/records
```

### Create User as Admin

```bash
curl -u admin@finance.local:Admin@123 \
  -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Priya Admin",
    "email": "priya2@finance.local",
    "password": "Secure@123",
    "role": "ADMIN",
    "status": "ACTIVE"
  }'
```

### Create Record as Admin

```bash
curl -u admin@finance.local:Admin@123 \
  -X POST http://localhost:8080/api/records \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 120.50,
    "type": "EXPENSE",
    "category": "Office",
    "date": "2026-03-31",
    "notes": "Printer supplies"
  }'
```

## Example Error Response

```json
{
  "timestamp": "2026-04-02T09:29:36.084922100Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/users",
  "details": [
    "password: Password is required"
  ]
}
```

## Database

This project uses H2 in-memory database.

H2 Console:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:finance_dashboard`
- Username: `sa`
- Password: blank

Example SQL:

```sql
SELECT * FROM APP_USERS;
```

```sql
SELECT * FROM FINANCIAL_RECORDS;
```

## How to Run Locally

### PowerShell

```powershell
$env:MAVEN_OPTS='-Dmaven.repo.local=C:\Users\rishi\Downloads\dashboard\dashboard\.m2repo'
.\mvnw.cmd spring-boot:run
```

### Bash

```bash
./mvnw spring-boot:run
```

The API starts on:

- `http://localhost:8080`

Useful links:

- `http://localhost:8080/api/health`
- `http://localhost:8080/api/dashboard/summary`
- `http://localhost:8080/h2-console`

## Deploy on Render

This project can be deployed as a Render Web Service.

Recommended settings:

- Environment: `Java`
- Build Command: `./mvnw clean package -DskipTests`
- Start Command: `java -jar target/dashboard-0.0.1-SNAPSHOT.jar`

Recommended environment variables:

- `PORT` = `10000` is injected by Render automatically
- `H2_CONSOLE_ENABLED` = `false`

After deployment, test:

- `/api/health`
- `/api/dashboard/summary`

Note:

- the project uses an in-memory H2 database, so data resets whenever the service restarts

## How to Test

### Run the automated test suite

PowerShell:

```powershell
$env:MAVEN_OPTS='-Dmaven.repo.local=C:\Users\rishi\Downloads\dashboard\dashboard\.m2repo'
.\mvnw.cmd test
```

### Manual testing

The project was manually verified using:

- `POST /api/users`
- `POST /api/records`
- `PUT /api/records/{id}`
- `DELETE /api/records/{id}`
- `GET /api/dashboard/summary`
- `GET /api/records`

Manual testing can be done using:

- Postman
- browser for simple GET requests
- `curl`

## Assumptions and Tradeoffs

- H2 is used instead of PostgreSQL or MySQL to keep setup simple
- the database is in-memory, so data resets when the app restarts
- Basic Auth is used instead of JWT because the assignment allows simplified authentication
- pagination and full text search were not added to keep the implementation compact and focused
- records are linked to the authenticated user who created them

## Evaluation Mapping

This implementation addresses the assignment criteria as follows:

- `Backend Design`
  - layered structure with separation of concerns
- `Logical Thinking`
  - clear RBAC model and service-level business logic
- `Functionality`
  - CRUD, filters, summaries, and access control all implemented
- `Code Quality`
  - DTO-based API design, central exception handling, readable naming
- `Database and Data Modeling`
  - separate user and financial record entities with clear relationships
- `Validation and Reliability`
  - request validation and proper error responses
- `Documentation`
  - setup, credentials, API surface, assumptions, and testing documented
- `Additional Thoughtfulness`
  - seeded demo data, integration tests, H2 console

## Submission Links

Suggested submission format:

- Repository link:[Finance Dashboard Backend](https://github.com/Rishitha-Goud/Finance-Dashboard-Backend)
- Documentation link:  [README Documentation](https://github.com/Rishitha-Goud/Finance-Dashboard-Backend/blob/main/dashboard/README.md)
- Postman API Documentation: [Postman Docs](https://.postman.co/workspace/Finance-Dashboard~8a8a824f-2ac6-4a35-8e39-419388f8e9de/collection/42623948-6aaf0929-6d45-4d5a-b391-d46a9f1a840b?action=share&creator=42623948)

## Author Note

This project is designed as an assessment-friendly backend solution: small enough to review quickly, but structured enough to demonstrate backend architecture, API design, access control, validation, persistence, and testing.
