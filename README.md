# The bank's website

Сайт банка с использованием современного Java-стека.<br>
Проект разрабатываю для изучения банковского домена изнутри.

## Технологический стек

| Компонент           | Технология                        | Назначение                         |
|---------------------|-----------------------------------|------------------------------------|
| **Backend**         | Java 25, Spring Boot 4.x          | Основной фреймворк                 |
| **Frontend**        | HTML5, CSS, JavaScript, Thymeleaf | Отображение динамичных веб-страниц |
| **База данных**     | PostgreSQL 15, Spring Data JPA    | Хранение данных                    |
| **Безопасность**    | Spring Security 6                 | Аутентификация пользователя        |
| **Утилиты**         | Lombok                            | Уменьшение boilerplate кода        |
| **Контейнеризация** | Docker, Docker Compose            | Развертывание инфраструктуры       |

##  Требования

- **Java 25** или выше
- **Maven 3.9+**
- **Docker** и **Docker Compose**
- **Git**

## База данных

### Схема данных

```sql
-- Основные таблицы
user          -- Информация о пользователях
├── id (PK)
├── email (UNIQUE)
├── phone_number (UNIQUE)
├── password 
├── firstName
├── lastName
├── role (ENUM)
└── created_at

accounts          -- Информация о счетах
├── id (PK)
├── user_id (FK) 
├── account_number (UNIQUE)
├── balance
├── type (ENUM)
└── created_at

transactions      -- История транзакций
├── id (PK)
├── account_id (FK)
├── amount
├── type (ENUM)
├── description
├── balance_after
└── timestamp
```

## Карта web-сайта

### Публичные страницы

| Метод  | Endpoint | Описание                         |
|--------|----------|----------------------------------|
| `GET`  | `/`      | Редирект на '/home'              |
| `GET`  | `/home`  | Главная страница                 |
| `GET`  | `/debit` | Страница расчетного счета        |
| `GET`  | `/login` | Страница авторизации             |

### Страницы для авторизированных пользователей

| Метод  | Endpoint                               | Описание                         |
|--------|----------------------------------------|----------------------------------|
| `GET`  | `/set-password`                        | Форма установки пароля           |
| `GET`  | `/account`                             | Возвращает страницу счетов       |
| `GET`  | `/account/open-account`                | Форма открытия нового счета      |
| `GET`  | `/account/{accountNumber}/deposit`     | Форма депозита                   |
| `GET`  | `/account/{accountNumber}/transfer`    | Форма перевода                   |
| `GET`  | `/account/{accountNumber}/transaction` | История транзакций               |

##  Структура проекта

```
src/main/java/com/bank/account/
├── BankAccountServiceApplication.java  # Главный файл
├── controller/                         # Контроллеры
├── service/                            # Бизнес-логика
├── repository/                         # Доступ к данным
├── model/                              # Сущности JPA
├── dto/                                # Data Transfer Objects
└── security/                           # Безопасность

src/main/resources/
├── static/ 
│   ├── css/                            # Стили шаблонов
│   ├── js/                             # Анимация canvas
├── templates/                          # Шаблоны thymeleaf
├── application.yml                     # Основная конфигурация
```

##  Контакты

Матвей Андреевич - [@waterflod](https://t.me/waterflod) - mse25019@gmail.com

Ссылка на проект: [https://github.com/WaterFlod/BankAccountManagmentService](https://github.com/WaterFlod/BankAccountManagmentService)

---

<div align="center">

### ⭐ Если проект вам понравился, поставьте звезду на GitHub!

</div>