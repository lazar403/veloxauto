# VeloxAuto 🚗

VeloxAuto is a backend system for a car dealership platform built with **Spring Boot** and **PostgreSQL**.
The project focuses on clean architecture, real-world business logic, and production-ready backend practices.

A **Next.js frontend** is planned and will consume this API as part of a full-stack dealership platform.

---

## Tech Stack

**Backend**

* Java 25
* Spring Boot 4
* Spring Data JPA (Hibernate)
* PostgreSQL (Neon)
* MapStruct
* Lombok
* Maven

**Frontend (planned)**

* Next.js (TypeScript)
* REST API integration

---

## Architecture

Layered structure:

```
Controller → DTO → Service → Mapper → Entity
```
---

## Current Features

### Customer Module

* Customer CRUD
* Email uniqueness validation
* Role system (`CUSTOMER`, `ADMIN`, `SALE`)
* Active status management
* Audit fields (`createdAt`, `updatedAt`)
* DTO layer + MapStruct mapping

---

## Planned Features (Roadmap)

* Global exception handling
* Authentication (JWT)
* Vehicle management
* Search & pagination
* Reservations & sales workflow
* Favorites
* Activity logging
* Admin operations
* Next.js frontend integration

---

## Getting Started

### 1. Clone

```
git clone https://github.com/lazar403/veloxauto.git
cd veloxauto/backend
```

### 2. Configure `.env`

```
DB_URL=your_database_url
DB_USERNAME=your_user
DB_PASSWORD=your_password
PORT=8080
```

### 3. Run

```
./mvnw spring-boot:run
```

---

## Example Endpoint

**POST /api/customers**

```json
{
  "firstName": "TestFirst",
  "lastName": "TestLast",
  "email": "test@example.com",
  "password": "password123",
  "phoneNumber": "+381601234567"
}
```

---

## Contributing

1. Fork the repository
2. Create a feature branch

   ```
   git checkout -b feature/your-feature
   ```
3. Use conventional commits:

4. Open a Pull Request

