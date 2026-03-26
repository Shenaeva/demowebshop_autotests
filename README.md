# demowebshop_autotests

UI автотесты для сайта [Demo Web Shop](https://demowebshop.tricentis.com/) на **Java + Playwright + JUnit 5 + Maven**.  
Проект сделан как pet-project для демонстрации навыков UI automation, визуальных проверок и CI через **GitHub Actions**.

## Что покрыто

Сейчас в проекте есть smoke-проверки для:

- авторизации
- поиска
- работы с корзиной
- переходов по категориям и на карточку товара
- визуальных проверок через baseline screenshots

## Стек

- Java 21
- Maven
- Playwright
- JUnit 5
- Allure
- GitHub Actions
- dotenv-java
- image-comparison

## Структура проекта

```text
src
├── test
│   ├── java
│   │   ├── config
│   │   ├── core
│   │   ├── pages
│   │   ├── tests
│   │   └── utils
│   └── resources
│       └── visual-baseline
.github
└── workflows
    └── ui-tests.yml
```

## Что умеет CI

Workflow в GitHub Actions умеет:

* запускаться вручную
* запускаться по расписанию
* запускать весь smoke-набор
* запускать один конкретный тест или метод
* переснимать visual baseline по флагу
* ретраить упавшие тесты 2 раза
* собирать Allure report
* сохранять артефакты прогона

## Локальный запуск
### 1. Клонировать репозиторий
```bash
git clone https://github.com/Shenaeva/demowebshop_autotests.git
cd demowebshop_autotests
```
### 2. Создать `.env`
Создай файл `.env` в корне проекта:
```env
DEMO_USER_EMAIL=your_email
DEMO_USER_PASSWORD=your_password
```
Для примера можно ориентироваться на .env.example.

### 3. Установить зависимости
```bash
mvn clean test -DskipTests
```
### 4. Установить браузеры Playwright
```bash
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```
Если нужно установить системные зависимости на Linux:
```bash
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"
```
### Запуск тестов локально
Все тесты
```bash
mvn clean test
```
Только smoke
```bash
mvn clean test -Dgroups=smoke
```
Один тестовый класс
```bash
mvn clean test -Dtest=SmokeSearchTest
```
Один тестовый метод
```bash
mvn clean test -Dtest=SmokeSearchTest#search_returnsResults
```
Запуск в headed режиме
```bash
mvn clean test -Dheadless=false
```
Запуск в headless режиме
```bash
mvn clean test -Dheadless=true
```
Выбор браузера
```bash
mvn clean test -Dbrowser=chromium
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=webkit
```

## Visual testing

В проекте используются baseline screenshots.

Baseline хранятся тут:
```
src/test/resources/visual-baseline/
```
Как работает
* тест делает скриншот
* скрин сравнивается с baseline
* если есть различия, создаются: actual, diff

Артефакты сохраняются в:
```
artifacts/screenshots/actual/
artifacts/screenshots/diff/
```

### Обновление baseline локально
```bash
mvn clean test -Dvisual.updateBaseline=true
```
### Обновление baseline в GitHub Actions

В workflow есть флаг update_baseline=true.
После прогона можно скачать артефакт visual-baseline-... и закоммитить новые baseline в репозиторий.

## Allure report
Локальная генерация отчета
```bash
mvn clean test
mvn allure:report
```

Готовый HTML-отчет будет лежать в:
```
target/site/allure-maven/
```
### В GitHub Actions

После каждого прогона сохраняются артефакты:

`surefire reports`  
`allure results`  
`allure html report`  
`test artifacts`  
### GitHub Actions

Workflow лежит в:
```
.github/workflows/ui-tests.yml
```
Поддерживает параметры:

`tags` — запуск по тегам  
`excluded_tags` — исключение тегов  
`browser` — выбор браузера  
`update_baseline` — пересъемка baseline  
`test_name` — запуск одного теста или одного метода

### Примеры

Запуск одного класса:
```
SmokeSearchTest
```
Запуск одного метода:
```
SmokeSearchTest#search_returnsResults
```
## Теги

В проекте используются кастомные теги через аннотации, например:

`@RunTags.UI`  
`@RunTags.Smoke`  
`@FeatureTags.Cart`  
`@FeatureTags.Search`  
`@FeatureTags.Catalog`

Это позволяет гибко запускать нужные наборы тестов.

## Полезные команды
Проверить статус git
```bash
git status
```
Сгенерировать отчет Allure
```bash
mvn allure:report
```
Запустить один тест с ретраем
```bash
mvn clean test -Dtest=SmokeSearchTest -Dsurefire.rerunFailingTestsCount=1
```