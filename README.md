# Banking Service — educational pet project

[![Java](https://img.shields.io/badge/Java-25-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-✓-2496ED.svg)](https://www.docker.com/)

A web application for managing bank accounts.  
The project was created for **Java (Spring Boot)** development practice and has no real financial value.  
Three types of accounts are supported: **checking**, **credit**, and **savings**.

> 🖥 **A demo version is available at:**  
> **[water-flod.ru](https://water-flod.ru/home)**  

---

## 📋 Features

### Users
- Registration / login (email or phone number).
- Authentication via Spring Security (BCrypt).
- Roles: `USER` (default), `ADMIN` (reserved).

### Account Management
| Account Type | Details |
|--------------|---------|
| **Checking** | Basic account for deposits, withdrawals, and transfers. |
| **Savings**  | Daily interest accrual (5% per annum) – executed at 3:00 AM via cron. |
| **Credit**   | Credit limit (fixed 500,000), daily interest accrual on the outstanding balance (20% per annum). |

### Operations
- ✅ Deposit to an account (`/account/{number}/deposit`)
- ✅ Withdrawal from an account (`/account/{number}/withdraw` – present in the service; a form can be added)
- ✅ Transfer between accounts (sufficient funds/limit check)
- ✅ View the last 10 transactions across all user accounts
- ✅ Transaction history for a specific account

### Scheduled Tasks
- **Interest accrual** for savings and credit accounts (daily at 3:00 AM).
- Uses `@Scheduled` + JPA `@Version` for optimistic locking.

---

## 🛠 Technology Stack

| Component       | Technology |
|-----------------|------------|
| Language        | Java 25 |
| Framework       | Spring Boot 3.4.x |
| Security        | Spring Security |
| Database        | PostgreSQL 17 (primary), H2 (tests) |
| ORM             | Spring Data JPA (Hibernate) |
| Template engine | Thymeleaf + Spring Security Extras |
| Build tool      | Maven |
| Containerization| Docker + Docker Compose |
| Logging         | SLF4J + Logback |
| Utilities       | Lombok, Jakarta Validation |

---

## 🚀 Running the Project

### Prerequisites
- Docker & Docker Compose
- (Optional) Java 25 + Maven for local run without Docker

### Option 1: Run with Docker Compose (recommended)
```bash
# Clone the repository
git clone https://github.com/your-username/bank-account-service.git
cd bank-account-service

# Create an .env file (example)
echo "POSTGRES_DB=bankdb
POSTGRES_USER=bankuser
POSTGRES_PASSWORD=password123" > .env

# Start the containers
docker-compose up -d
```

The application will be available at: `http://localhost:8080`

### Option 2: Local run (without Docker)
```bash
# Make sure PostgreSQL is running and the database exists
# Configure the connection in src/main/resources/application-prod.yml

mvn clean package
java -jar target/app.jar
```

### Access after launch
- **Home page:** `http://localhost:8080/home`
- **Login page:** `http://localhost:8080/auth/login`
- **Dashboard:** after login → `/account`

> 💡 For testing, you can register a new user (password must be at least 8 characters).

---

## 🧪 Project Structure (overview)
```
src/
├── main/
│   ├── java/com/bank/
│   │   ├── account/          # accounts, transactions, services, controllers
│   │   ├── user/             # users, registration, security
│   │   └── configuration/    # settings (Security, Scheduling)
│   └── resources/
│       ├── templates/        # Thymeleaf templates
│       └── application.yml   # configuration (active profiles, etc.)
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## 🐛 Known Limitations (ideas for improvement)
- No full-fledged REST API (MVC only).
- No database migrations (Flyway/Liquibase).
- Credit limit is hardcoded (500,000).
- No pagination for transaction history.
- Tests are not written (only basic structure).

Planned for future versions.

---

## 🤝 How to Contribute (if you want)
You can:
- Create an **Issue** with a suggestion for improvement or a bug report.
- Submit a **Pull Request** with a fix or a new feature.

---

## 📄 License
This project is educational and is distributed without licensing restrictions (MIT or similar).

---

## 📬 Contacts
Author: *Matthew*  
Questions or ideas? Reach out via [Telegram](https://t.me/waterflod) or open an issue.

---

_Last updated: June 2026_
