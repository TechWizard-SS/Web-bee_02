
# Transaction Log Processor

Обработчик логов транзакций с возможностью группировки по пользователям и расчетом баланса.

## 🚀 Особенности

- Парсинг логов транзакций из файлов
- Автоматическое создание зеркальных записей для получателей
- Расчет итогового баланса для каждого пользователя
- Сохранение результатов в отдельные файлы
- Поддержка Docker-контейнеризации
- Набор автоматических тестов (JUnit 5)

## 📦 Требования

- JDK 23+
- Docker (опционально, для контейнеризации)
- IntelliJ IDEA (или другая IDE для запуска/тестов)

## ⚙️ Установка и запуск

### 🔧 Сборка и запуск через командную строку

```bash

# Компиляция проекта
javac -d out src/main/Main.java src/model/LogEntry.java src/service/LogService.java src/service/LogFileSaver.java src/parser/LogParser.java src/util/FileUtils.java

# Запуск
java -cp out main.Main "src/logs"
```


### 🐳 Docker-сборка и запуск

```bash
# Сборка Docker-образа
docker build -t transaction-processor .

# Запуск контейнера (Linux/macOS)
docker run --rm -v "$(pwd)/src/logs:/app/logs" transaction-processor

# Запуск контейнера (Windows, PowerShell)
docker run --rm -v "C:\путь\к\проекту\src\logs:/app/logs" transaction-processor
```

## 🧪 Тестирование

Тесты написаны с использованием **JUnit 5** и запускаются из вашей IDE (например, IntelliJ IDEA).

### Покрытие тестами:

* Парсинг различных типов операций
* Расчет баланса
* Обработка ошибочных данных
* Проверка сохранения файлов

## 📂 Структура проекта

```
├── src/
│   ├── logs/              # Примеры логов
│   ├── main/              # Главный класс Main.java
│   ├── model/             # Модели данных (LogEntry)
│   ├── parser/            # Парсеры логов
│   ├── service/           # Бизнес-логика
│   └── util/              # Вспомогательные классы
├── test/                  # Unit-тесты (JUnit 5)
│   ├── parser/
│   └── service/
├── Dockerfile             # Docker-конфигурация
└── run.sh                 # (опциональный) Bash-скрипт запуска
```

## 🛠 Пример использования

Исходные логи (`src/logs/log1.log`):

```
[2025-05-10 09:00:22] user001 balance inquiry 1000.00
[2025-05-10 09:05:44] user001 transferred 100.00 to user002
```

Результат обработки (`src/logs/transactions_by_users/user001.log`):

```
[2025-05-10 09:00:22] user001 BALANCE_INQUIRY 1000.00
[2025-05-10 09:05:44] user001 TRANSFERRED 100.00 to user002
[2025-05-10 09:05:44] user001 final balance 900.00
```
```
