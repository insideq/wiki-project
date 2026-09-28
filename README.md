# WikiSystem

![CI](https://github.com/insideq/wiki-project/actions/workflows/ci.yml/badge.svg)
![Commit Lint](https://github.com/insideq/wiki-project/actions/workflows/commit-lint.yml/badge.svg)

Wiki-система на Spring Boot 3 + React.

## Технологии

**Backend:**

- Java 21
- Spring Boot 3.5.8
- Spring Security + JWT
- Spring Data JPA
- Liquibase
- H2 (dev) / PostgreSQL (prod)
- OpenAPI/Swagger

**Frontend**:

- React 18
- Vite
- Bootstrap 5

**Инфраструктура:**

- Gradle
- GitHub Actions (CI/CD)
- Checkstyle, JaCoCo

## Документация API

После запуска: http://localhost:8080/swagger-ui/index.html

## Запуск

```bash
./gradlew bootRun

Swagger UI URL:
http://localhost:8080/swagger-ui/index.html

MVN Repository:
https://mvnrepository.com/

H2 Console:
http://localhost:8080/h2-console

JDBC Settings:

-   URL: jdbc:h2:file:./data
-   User Name: sa
-   Password: sa

Bcrypt Generator (Cost factor = 10):
https://bcrypt-generator.com

Про Spring Security:
https://habr.com/ru/articles/346628/
```
