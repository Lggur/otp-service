# OTP Service

Сервис для генерации и проверки одноразовых паролей (One-Time Password) с поддержкой уведомлений через Telegram, Email и логирование в файл.

---

## Технологии

| Слой              | Технология                                  |
|-------------------|---------------------------------------------|
| Язык              | Java 21                                     |
| Фреймворк         | Spring Boot 4.0                             |
| База данных       | PostgreSQL (Spring JDBC)                    |
| Безопасность      | JWT (JJWT 0.12.5), BCrypt                   |
| Email             | Spring Boot Starter Mail (SMTP)             |
| Telegram          | Telegram Bot API (HTTP)                     |
| Логирование       | SLF4J + Logback                             |
| Сборка            | Maven                                       |

---

## Быстрый старт

### 1. Подготовка базы данных

Проект использует PostgreSQL.

1. Подключитесь к PostgreSQL (например, через терминал):
   ```bash
   psql -U postgres
   ```
2. Создайте базу данных:
   ```sql
   CREATE DATABASE otp_service;
   ```
3. Выполните SQL-скрипт для создания схемы таблиц:
   ```bash
   psql -U postgres -d otp_service -f src/main/resources/db/schema.sql
   ```

 Скрипт создаёт:
- Таблицу `users` - пользователи с ролями `USER` / `ADMIN`
- Таблицу `otp_config` - глобальная конфигурация OTP (длина кода, TTL)
- Таблицу `otp_codes` - выданные коды со статусами `ACTIVE` / `USED` / `EXPIRED`
- Триггеры автообновления поля `updated_at`
- Индексы для ускорения запросов по статусу и пользователю

### 2. Настройка конфигурации

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Откройте `application.properties` и заполните параметры (подробнее - в разделе [Конфигурация](#конфигурация)).

### 3. Запуск

```bash
./mvnw spring-boot:run
```

Приложение поднимается на `http://localhost:8080`.

---

## Конфигурация

Полный список параметров `application.properties`:

### База данных

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/otp_service
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Безопасность (JWT)

Секретный ключ для подписи токенов. Минимум 32 символа (HS256)

```properties
jwt.secret=super-secret-key-super-secret-key-123456
```

### Email (SMTP)

Для тестирования удобно использовать [Mailtrap](https://mailtrap.io)

```properties
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=587
spring.mail.username=your_mailtrap_username
spring.mail.password=your_mailtrap_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Telegram

```properties
telegram.bot.token=ваш_токен_бота
```

**Как создать бота и получить токен:**
1. Найдите `@BotFather` в Telegram.
2. Отправьте команду `/newbot` и следуйте инструкциям.
3. Скопируйте выданный токен в `application.properties`.

**Как узнать свой `chat_id`:**
- Отправьте любое сообщение своему боту.
- Откройте в браузере:
  ```
  https://api.telegram.org/bot<ВАШ_ТОКЕН>/getUpdates
  ```
- Найдите поле `"id"` внутри объекта `"chat"`.

---

## API Reference

Базовый URL: `http://localhost:8080`

### Аутентификация

Все эндпоинты, кроме `/auth/**`, требуют заголовок:
```
Authorization: Bearer <JWT_TOKEN>
```

---

#### `POST /auth/register` - Регистрация

| Параметр   | Тип    | Обязательный | Описание                          |
|------------|--------|:------------:|-----------------------------------|
| `username` | string |      Да      | Уникальное имя пользователя       |
| `password` | string |      Да      | Пароль (хранится в виде BCrypt)   |
| `role`     | string |     Нет      | `USER` (по умолчанию) или `ADMIN` |

**Пример:**
```bash
curl -X POST "http://localhost:8080/auth/register?username=alice&password=secret"
```

**Ответ:** объект пользователя (JSON) с `id`, `username`, `role`.

---

#### `POST /auth/login` - Вход

| Параметр   | Тип    | Обязательный | Описание         |
|------------|--------|:------------:|------------------|
| `username` | string |      Да      | Имя пользователя |
| `password` | string |      Да      | Пароль           |

**Пример:**
```bash
curl -X POST "http://localhost:8080/auth/login?username=alice&password=secret"
```

**Ответ:** строка - JWT-токен.

---

#### `POST /otp/generate` - Генерация OTP

Требует JWT-токен

| Параметр      | Тип    | Обязательный | Описание                                            |
|---------------|--------|:------------:|-----------------------------------------------------|
| `channel`     | string |      Да      | Канал доставки: `TELEGRAM`, `EMAIL`, `FILE`         |
| `destination` | string |      Да      | Адресат: chat_id, email или имя файла               |
| `operationId` | string |     Нет      | Идентификатор операции для привязки кода            |

**Примеры:**

```bash
# Telegram
curl -X POST "http://localhost:8080/otp/generate?channel=TELEGRAM&destination=chat_id" \
     -H "Authorization: Bearer <TOKEN>"

# Email
curl -X POST "http://localhost:8080/otp/generate?channel=EMAIL&destination=user@example.com" \
     -H "Authorization: Bearer <TOKEN>"

# Файл
curl -X POST "http://localhost:8080/otp/generate?channel=FILE&destination=test_user" \
     -H "Authorization: Bearer <TOKEN>"

# С привязкой к операции
curl -X POST "http://localhost:8080/otp/generate?channel=EMAIL&destination=user@example.com&operationId=payment-123" \
     -H "Authorization: Bearer <TOKEN>"
```

**Ответ:** `OTP sent`

---

#### `POST /otp/validate` - Проверка OTP

Требует JWT-токен

| Параметр      | Тип    | Обязательный | Описание                                              |
|---------------|--------|:------------:|-------------------------------------------------------|
| `code`        | string |      Да      | OTP-код для проверки                                  |
| `operationId` | string |     Нет      | Идентификатор операции (если задавался при генерации) |

**Пример:**
```bash
curl -X POST "http://localhost:8080/otp/validate?code=483920" \
     -H "Authorization: Bearer <TOKEN>"
```

**Ответ:** `VALID` или `INVALID`

После успешной проверки код помечается как `USED` и не может быть использован повторно.

---

## Безопасность

- Пароли хранятся в БД в виде BCrypt-хешей.
- JWT подписывается алгоритмом HS256 с ключом из `application.properties`. Токен содержит `userId`.
- JwtFilter перехватывает все запросы, кроме `/auth/**`, и проверяет токен перед передачей в контроллер.
- OTP генерируется криптографически стойким `SecureRandom`.

---

## Фоновая очистка кодов

Сервис автоматически переводит просроченные OTP-коды из статуса `ACTIVE` в `EXPIRED`.
За это отвечает `OtpCleanupJob` - компонент с аннотацией `@Scheduled`, который запускается каждые 60 секунд.
TTL кода задаётся в таблице `otp_config` (поле `ttl_seconds`, по умолчанию 300 секунд).
Изменить его можно напрямую в БД.

---

## Структура проекта

```
otp-service/
├── src/main/
│   ├── java/lggur/otp_service/
│   │   ├── api/
│   │   │   ├── AuthController.java       # POST /auth/register, /auth/login
│   │   │   └── OtpController.java        # POST /otp/generate, /otp/validate
│   │   ├── config/
│   │   │   ├── FilterConfig.java         # Регистрация JwtFilter
│   │   │   └── JwtFilter.java            # Фильтр проверки JWT
│   │   ├── dao/
│   │   │   ├── OtpCodeDao.java           # CRUD и валидация кодов
│   │   │   ├── OtpConfigDao.java         # Чтение настроек OTP
│   │   │   └── UserDao.java              # CRUD пользователей
│   │   ├── model/
│   │   │   ├── OtpCode.java              # Сущность OTP-кода
│   │   │   ├── OtpConfig.java            # Сущность конфигурации
│   │   │   └── User.java                 # Сущность пользователя
│   │   └── service/
│   │       ├── AuthService.java          # Регистрация, вход
│   │       ├── JwtService.java           # Генерация и верификация JWT
│   │       ├── OtpService.java           # Генерация и проверка OTP
│   │       ├── OtpCleanupJob.java        # Планировщик очистки (каждые 60с)
│   │       └── notification/
│   │           ├── NotificationService.java        # Интерфейс
│   │           ├── NotificationFactory.java        # Фабрика по каналу
│   │           ├── TelegramNotificationService.java
│   │           ├── EmailNotificationService.java
│   │           ├── FileNotificationService.java
│   │           └── SmsNotificationService.java
│   └── resources/
│       ├── application.properties.example  # Шаблон конфигурации
│       ├── logback-spring.xml              # Настройки логирования
│       └── db/
│           └── schema.sql                  # DDL схемы БД
└── pom.xml
```
