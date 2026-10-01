# WikiSystem

![CI](https://github.com/insideq/wiki-project/actions/workflows/ci.yml/badge.svg)
![Commit Lint](https://github.com/insideq/wiki-project/actions/workflows/commit-lint.yml/badge.svg)
![Release](https://github.com/insideq/wiki-project/actions/workflows/release.yml/badge.svg)

Wiki-система на Spring Boot 3 + React.

## Содержание

- [Стек технологий](#стек-технологий)
- [Запуск](#запуск)
- [Стратегия ветвления](#стратегия-ветвления)
- [Процесс разработки](#процесс-разработки)
- [Правила оформления коммитов](#правила-оформления-коммитов)
- [CI/CD](#cicd)
- [Метрики качества](#метрики-качества)
- [Релизы](#релизы)

## Стек технологий

### Backend

- Java 21
- Spring Boot 3.5.8
- Spring Security + JWT
- Spring Data JPA
- Liquibase
- H2 (dev) / PostgreSQL (prod)
- OpenAPI / Swagger

### Frontend

- React 18
- Vite
- Bootstrap 5
- react-markdown + @uiw/react-md-editor

### Инфраструктура

- Gradle
- GitHub Actions
- Checkstyle, JaCoCo, ESLint

## Запуск

### Backend

```bash
./gradlew bootRun
```

Приложение: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui/index.html

### Frontend

```bash
cd front
npm install
npm start
```

Фронт: http://localhost:5173

## Стратегия ветвления

Проект использует **GitHub Flow** с двумя постоянными ветками:

| Ветка     | Назначение                               | Защита                                                             |
| --------- | ---------------------------------------- | ------------------------------------------------------------------ |
| `main`    | Стабильная версия. Только релизы.        | PR + review, CI checks, linear history, нет force push, нет delete |
| `develop` | Интеграционная. Все фичи вливаются сюда. | PR, CI checks                                                      |

### Временные ветки

Создаются от `develop` и удаляются после merge:

- `feature/WIKI-XX-название` — новая функциональность.
- `fix/название` — исправление бага.
- `refactor/название` — рефакторинг.
- `test/название` — тесты.
- `docs/название` — документация.
- `ci/название` — изменения CI/CD.
- `chore/название` — рутина (обновление зависимостей).

### Пример

```bash
git checkout develop
git pull origin develop
git checkout -b feature/WIKI-14-create-tag
# ... работа ...
git push -u origin feature/WIKI-14-create-tag
# PR на GitHub → CI → merge → удаление ветки
```

## Процесс разработки

1. **Создать ветку** от актуального `develop`.
2. **Написать код** с соблюдением стиля (Checkstyle, ESLint).
3. **Запустить локально** тесты: `./gradlew test`.
4. **Закоммитить** с сообщением по Conventional Commits.
5. **Push** ветки на GitHub.
6. **Открыть PR** в `develop`.
7. **Дождаться зелёных checks**:
    - `Lint (Checkstyle)`
    - `Test + Coverage (JaCoCo)`
    - `Build JAR`
    - `Check commit messages`
8. **Merge PR** (squash или merge — по ситуации).
9. **Удалить ветку** после merge.

### Прямой push запрещён

Настроена защита `main` и `develop`:

- недоступен прямой push;
- только через PR;
- CI должен пройти;
- требуется 1 approval для `main`.

### Merge в `main`

Только через PR из `develop`. После merge — ставится тег (`vX.Y.Z`), GitHub Actions автоматически создаёт релиз.

## Правила оформления коммитов

Используется **Conventional Commits**:

```
<type>(<scope>): <description>
```

### Типы

| Тип        | Назначение                            |
| ---------- | ------------------------------------- |
| `feat`     | Новая функциональность                |
| `fix`      | Исправление бага                      |
| `docs`     | Документация                          |
| `style`    | Форматирование (без изменения логики) |
| `refactor` | Рефакторинг                           |
| `test`     | Тесты                                 |
| `chore`    | Рутина, обновления                    |
| `build`    | Сборка, зависимости                   |
| `ci`       | CI/CD                                 |
| `perf`     | Оптимизация                           |
| `revert`   | Откат                                 |

### Примеры

```
feat: add WikiPage API and service
fix: correct password validation in UserService
test: add unit tests for TagService
ci: add GitHub Actions workflows
docs: update README with branching strategy
```

### Автоматическая проверка

- **Локально** — Husky + commitlint (`.husky/commit-msg`).
- **На CI** — workflow `.github/workflows/commit-lint.yml`.
- **Проверяется**: заголовок PR и все коммиты в PR.

## CI/CD

Пайплайны в `.github/workflows/`:

| Workflow          | Триггер                         | Что делает                      |
| ----------------- | ------------------------------- | ------------------------------- |
| `commit-lint.yml` | PR в main/develop               | Проверка сообщений коммитов     |
| `ci.yml`          | push в main/develop, PR         | Lint, тесты, JaCoCo, сборка JAR |
| `release.yml`     | push тега `v*.*.*`              | Создание GitHub Release с JAR   |
| `metrics.yml`     | расписание (пн 06:00) + вручную | Сбор метрик                     |

### Артефакты CI

После успешного прогона в Actions доступны:

- `checkstyle-report` — HTML-отчёт Checkstyle.
- `jacoco-report` — HTML-отчёт покрытия JaCoCo.
- `wiki-jar` — собранный JAR-файл.

## Метрики качества

Проект отслеживает **6 метрик** (требование: минимум 5).

| №   | Метрика                                 | Инструмент     | Где смотреть                            |
| --- | --------------------------------------- | -------------- | --------------------------------------- |
| 1   | Покрытие Instructions                   | JaCoCo         | Actions → test → артефакт jacoco-report |
| 2   | Покрытие Branches                       | JaCoCo         | там же                                  |
| 3   | Кол-во тестов (passed/failed)           | JUnit          | Actions → test                          |
| 4   | Время выполнения пайплайна              | GitHub Actions | Actions → run                           |
| 5   | Кол-во ошибок/предупреждений Checkstyle | Checkstyle     | Actions → lint                          |
| 6   | Частота коммитов                        | git log        | Insights → Commits                      |

### Автоматический сбор

`metrics.yml` публикует отчёт в **Summary** workflow:

- коммиты за неделю;
- последние 10 коммитов;
- покрытие JaCoCo;
- отчёт Checkstyle.

## Релизы

### Создание релиза

1. Смержить `develop` → `main` через PR.
2. Поставить тег:

    ```bash
    git checkout main
    git pull origin main
    git tag -a v1.0.0 -m "Release 1.0.0: description"
    git push origin v1.0.0
    ```

3. GitHub Actions автоматически:
    - соберёт `bootJar`;
    - создаст Release;
    - прикрепит JAR к релизу;
    - сгенерирует release notes из коммитов.

### История релизов

| Тег      | Дата | Что вошло               |
| -------- | ---- | ----------------------- |
| `v0.1.0` | —    | Initial CI/CD setup     |
| `v0.2.0` | —    | Wiki backend + frontend |

## Лицензия

ISC
