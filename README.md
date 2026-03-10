# Bank Cards Control System

Backend-приложение для управления банковскими картами. Аутентификация JWT + Spring Security (роли), REST API с Swagger, PostgreSQL + Liquibase миграции, Spring Data JPA.

## Технологии
- **Java 21**, Spring Boot 3.5.9
- Spring Security, Data JPA, Web, Validation, Actuator
- PostgreSQL, Liquibase (db/changelog/db.changelog-master.yaml)
- JWT (jjwt 0.12.6), Swagger (springdoc-openapi 2.8.15)
- Lombok, ModelMapper, PostgreSQL driver

## Предварительные требования
- Docker & Docker Compose (20+)
- Maven 3.8+ (Java 21)
- Git

## Установка
1. Клонируйте:
   :<br>
   `git clone` https://github.com/Kirill5112/bank_rest-main.git <br>
   `cd bank_rest-main`

2. Соберите:<br>
`mvn clean package -DskipTests` <br>
*Генерирует JAR в `target/`

## Запуск
### С docker-compose (рекомендуется, с PostgreSQL)

`docker-compose up -d --build`

- **App**: http://localhost:8080
- **Swagger API**: http://localhost:8080/swagger-ui.html
- **Health check**: http://localhost:8080/actuator/health
- **PostgreSQL**: localhost:5435 (DB: `banks_card_db`, user: `user`, pass: `passbcd`)

Liquibase автоматически применит миграции при первом запуске.

### Только app (внешняя БД)
```
docker build -t bank-app:latest.
docker run -p 8080:8080
-e SPRING_DATASOURCE_URL="jdbc:postgresql://host:5435/banks_card_db"
bank-app:latest 
```

## Конфигурация (application.yml)
```yaml
spring:
   datasource:
      url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5435/banks_card_db}
      username: ${SPRING_DATASOURCE_USERNAME:user}
      password: ${SPRING_DATASOURCE_PASSWORD:passbcd}

   jpa:
      hibernate:
         ddl-auto: validate  # Liquibase управляет схемой

   liquibase:
      enabled: true
      change-log: classpath:db/changelog/db.changelog-master.yaml

server:
   port: 8080

springdoc:
   swagger-ui:
      path: /swagger-ui.html

secret:
   key: 6++K7214ozh8g2Xxw71+4ULUVJ/Cbu/91RVqCkALErf/zzX22LE2aLAnhFuhFh+VpgpH4kvKxXr0ploPHZ79gw==  # Измените в проде!
   access-token-expiration: 86400000  # 1 день
```


## Переменные окружения (.env рекоменд.)
| Переменная                   | По умолчанию / Описание                          |
|------------------------------|--------------------------------------------------|
| `SPRING_DATASOURCE_URL`      | `jdbc:postgresql://localhost:5435/banks_card_db` |
| `SPRING_DATASOURCE_USERNAME` | `user`                                           |
| `SPRING_DATASOURCE_PASSWORD` | `passbcd`                                        |
| `SPRING_PROFILES_ACTIVE`     | `dev`                                            |
| `SECRET_KEY`                 | Из application.yml (JWT)                         |

Загрузка `.env`:" <br>

`docker-compose --env-file .env up -d`

## Дефолтный администратор (Liquibase)

При первом запуске Liquibase применяет changeset `006-insert-admin-credentials.yaml`, который создаёт пользователя-администратора и привязывает ему роль admin:

- Пользователь создаётся в таблице `users`
- Роль назначается через таблицу `user_roles`

Учётные данные администратора по умолчанию:
- **Логин (username)**: `8 888 888 88 88` (в БД хранится как `78888888888`)
- **Пароль**: `admin` (в БД хранится BCrypt-хэшом)

Рекомендуется изменить эти данные в продакшене и/или отключить данный changeset после начальной инициализации.

## Тестирование API
- **Swagger**: Полная документация + тесты.
- **Login**: POST `/api/auth/login` (JSON: `{"username": "...", "password": "..."}`) → JWT.
- **Защищенные**: `/api/cards` (header: `Authorization: Bearer <token>`).
- **Health**: `/actuator/health`.

## Логи

`docker-compose logs -f bank-app` <br>
## Сброс данных
`docker-compose down -v` 

## Остановка и очистка
`docker-compose down -v` # Удалить volumes (потеря данных!)<br>
`docker system prune -f` # Очистка образов