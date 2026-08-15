# Philadelphia Archaeological Inventory System

A full-stack web application developed for the digital management, documentation, and archival of archaeological finds from the Philadelphia (Alaşehir) excavation.

The system provides a centralized environment where authorized excavation team members can create, manage, search, document, and review archaeological artifact records.

---

## Project Overview

The Philadelphia Archaeological Inventory System was designed to support the documentation workflow of archaeological excavations by replacing fragmented or manual record management with a structured digital system.

Each archaeological find can be registered with its excavation information, typological and technical characteristics, measurements, photographs, visibility settings, and related documentation.

The system also maintains a change history to improve traceability and accountability of artifact records.

---

## Main Features

### Artifact Management

- Create archaeological artifact records
- Search artifacts by artifact code
- View artifact details
- Edit existing artifact records
- Soft-delete artifacts
- View deleted artifacts
- Restore/manage records according to user permissions

### Archaeological Documentation

Artifact records include fields for:

- Artifact code
- Type
- Form number
- Inventory number
- Study number
- Bag and box information
- Find location
- Locality
- Sector
- Find date and year
- Area
- Form
- Decoration type
- Paste structure
- Firing
- Technique
- Temper and temper amount
- Slip structure
- Angle
- Period
- Kind
- Munsell information
- Measurements
- Preserved part
- Material
- Production place
- Description
- Bibliography

### Artifact Photography

Authorized users can:

- Upload artifact photographs
- Assign photograph numbers
- View photographs
- Manage photographs according to their permissions

### Change History

Changes made to artifact records are recorded so that users can review:

- Who made the change
- When the change was made
- Which fields were changed
- Previous and updated values

### Export

Artifact information can be exported for documentation and research purposes.

Supported formats include:

- PDF
- Excel

### Authentication and User Management

The application includes:

- Secure login
- Password change
- Forgot password / password reset workflow
- User profile management
- Role-based authorization

---

## User Roles

The system uses three primary roles.

### ADMIN

Administrators can manage the system and its users, including creating user accounts and accessing administrative functionality.

### CREW_MEMBER

Excavation team members can create and edit artifact records and manage archaeological documentation according to their permissions.

### LOOKUP_USER

Lookup users have read-only access to the records available to them.

---

## Artifact Visibility

Artifact records support visibility controls.

### PRIVATE_FOR_CREW

The artifact is accessible only to authorized excavation team members.

### PUBLIC

The artifact can be made available according to the application's public-access rules.

---

## Technology Stack

### Frontend

- Angular
- TypeScript
- HTML
- SCSS

### Backend

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Maven

### Database

- PostgreSQL
- Flyway database migrations

### Additional Features

- REST API architecture
- Role-based access control
- File storage for artifact photographs
- PDF export
- Excel export
- E-mail based password reset

---

## Project Structure

```text
philadelphia-inventory/
│
├── backend/
│   ├── src/main/java/com/philadelphia/inventory/
│   │   ├── auth/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   │
│   └── src/main/resources/
│       ├── db/migration/
│       ├── application.properties
│       └── application-prod.properties
│
└── frontend/
    ├── public/
    └── src/
        ├── app/
        │   ├── core/
        │   ├── features/
        │   └── shared/
        └── environments/
```

---

## Local Development

### Requirements

The development environment requires:

- Java 21+
- Maven
- Node.js
- npm
- Angular CLI
- PostgreSQL

### Database

Create a PostgreSQL database for the application.

The local configuration expects the database connection to be supplied through the backend configuration and environment variables.

Sensitive credentials must not be committed to the repository.

### Backend

Navigate to:

```bash
cd backend
```

Run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

By default, the backend runs locally on:

```text
http://localhost:8080
```

### Frontend

Navigate to:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the Angular development server:

```bash
npm start
```

The frontend is then available locally at:

```text
http://localhost:4200
```

---

## Build

### Backend

```bash
cd backend
./mvnw clean package
```

Windows:

```powershell
.\mvnw.cmd clean package
```

### Frontend

```bash
cd frontend
npm run build
```

The production Angular build is generated inside the `dist/` directory.

---

## Environment Variables

Sensitive configuration values are supplied through environment variables rather than being stored directly in the source code.

Examples include:

```text
DB_PASSWORD
MAIL_USERNAME
MAIL_PASSWORD
```

Production credentials must never be committed to Git.

---

## Security

The application uses Spring Security and role-based authorization to restrict access to protected functionality.

Passwords and sensitive credentials must not be stored directly in the repository.

Production secrets should be configured through the deployment platform's environment-variable or secret-management system.

---

## Production

The application is designed to be deployed as separate frontend, backend, database, and persistent file-storage components.

Production deployment configuration and infrastructure are maintained separately from local development credentials.

---

## Project Status

The core application has been implemented and successfully builds locally.

Current phase:

**Production deployment and final delivery preparation.**

---

## Purpose

This project was developed specifically to support archaeological documentation and digital inventory management for the Philadelphia (Alaşehir) excavation.