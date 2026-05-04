# OTP Service

Сервис для генерации и проверки одноразовых паролей (One-Time Password) с поддержкой уведомлений через Telegram, Email и логирование в файл.

## Быстрый старт

### 1. Подготовка базы данных

Проект использует **PostgreSQL**. 

1. Подключитесь к PostgreSQL (например, через терминал):
   ```bash
   psql -U postgres
   ```
2. Создайте базу данных:
   ```sql
   CREATE DATABASE otp_service;
   ```
3. Выполните SQL-скрипт для создания схемы таблиц. Вы можете сделать это через `psql`:
   ```bash
   psql -U postgres -d otp_service -f src/main/resources/db/schema.sql
   ```

### 2. Настройка конфигурации

1. Скопируйте файл-шаблон:
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```
2. Откройте `src/main/resources/application.properties` и заполните следующие параметры:

#### База данных
Укажите ваши учетные данные PostgreSQL:
```properties
spring.datasource.username=postgres
spring.datasource.password=your_password
```

#### Безопасность (JWT)
Задайте секретный ключ для подписи JWT токенов (минимум 32 символа):
```properties
jwt.secret=super-secret-key-super-secret-key-123456
```

#### Уведомления и внешние сервисы

*   **Email**: Требуется настройка SMTP в `application.properties` (хост, порт, логин/пароль).
*   **Telegram**: Необходимо создать бота в `@BotFather` и указать его токен:
    ```properties
    telegram.bot.token=ваш_токен_бота
    ```
    Для отправки сообщений пользователь должен сначала начать диалог с ботом, чтобы получить `chat_id`. 
    
    **Как узнать свой chat_id:**
    *   Отправьте любое сообщение своему боту и перейдите по ссылке:
        `https://api.telegram.org/bot<ВАШ_ТОКЕН>/getUpdates`
        Найдите поле `id` в объекте `chat`.

*   **Каналы доставки**: При вызове API можно использовать значения `EMAIL`, `TELEGRAM` или `FILE`.

### 3. Запуск

Для запуска приложения используйте Maven Wrapper:

```bash
./mvnw spring-boot:run
```

## API Использование

### 1. Генерация OTP
Отправьте POST-запрос с указанием канала и адресата.

**Telegram:**
```bash
curl -X POST "http://localhost:8080/otp/generate?channel=TELEGRAM&destination=775090928"
```

**Email:**
```bash
curl -X POST "http://localhost:8080/otp/generate?channel=EMAIL&destination=user@example.com"
```

**Локальный файл:**
```bash
curl -X POST "http://localhost:8080/otp/generate?channel=FILE&destination=test_user"
```

## Функционал
- Генерация OTP кодов с настраиваемой длиной и временем жизни.
- Выбор канала доставки: Telegram, Email или локальный файл.
- Проверка кодов с защитой от повторного использования.
- JWT аутентификация для защищенных эндпоинтов.
