# VeloxAuto

Backend for a car dealership platform.  
Built with Spring Boot and PostgreSQL, aiming to resemble a real-world backend rather than a demo.

A Next.js frontend is planned.

---

![Java](https://img.shields.io/badge/Java-25-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-blue)
![Status](https://img.shields.io/badge/status-in%20progress-yellow)

---

## Stack

**Backend**
- Spring Boot  
- Spring Data JPA (Hibernate)  
- PostgreSQL (Neon)  
- MapStruct  
- Lombok  

**Frontend (planned)**
- Next.js (TypeScript)  

---

## Structure

Simple layered structure, nothing unusual.

---


## Run locally


```bash
git clone https://github.com/lazar403/veloxauto.git
cd veloxauto/backendbash
```

Create .env:
```
DB_URL=
DB_USERNAME=
DB_PASSWORD=
PORT=8080
```

Run:
```
./mvnw spring-boot:run
```
---

## Notes & Contributing

Work in progress.\
Focus is on backend structure and realistic features. \
PRs are welcome. Keep changes simple and readable.
