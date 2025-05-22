
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

java -cp out main.Main "logs"
```


### 🐳 Docker-сборка и запуск

```bash
docker build -t web-bee_project .

docker run --rm -v "C:\Users\Samir\Desktop\YLab\Web-bee_2:/app" web-bee_project
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
│   ├── main/              # Главный класс Main.java
│   ├── model/             # Модели данных (LogEntry)
│   ├── parser/            # Парсеры логов
│   ├── service/           # Бизнес-логика
│   └── util/              # Вспомогательные классы
├── logs/                  # Примеры логов
├── transactions_by_users/ # Папка куда будут сохраняться наши результаты
├── test/                  # Unit-тесты (JUnit 5)
│   ├── parser/
│   └── service/
├── Dockerfile             # Docker-конфигурация
└── run.sh                 # (опциональный) Bash-скрипт запуска
```

## 🛠 Пример использования

Исходные логи 
(`logs/log1.log`):
```
[2025-05-10 09:00:22] user001 balance inquiry 1000.00
[2025-05-10 09:05:44] user001 transferred 100.00 to user002
[2025-05-10 09:06:00] user001 transferred 120.00 to user002
[2025-05-10 10:30:55] user005 transferred 10.00 to user003
[2025-05-10 11:09:01] user001 transferred 235.54 to user004
[2025-05-10 12:38:31] user003 transferred 150.00 to user002
[2025-05-11 10:00:31] user002 balance inquiry 210.00
```

(`src/logs/log2.log`):
```
[2025-05-10 10:03:23] user002 transferred 990.00 to user001
[2025-05-10 10:15:56] user002 balance inquiry 110.00
[2025-05-10 10:25:43] user003 transferred 120.00 to user002
[2025-05-10 11:00:03] user001 balance inquiry 1770
[2025-05-10 11:01:12] user001 transferred 102.00 to user003
[2025-05-10 17:04:09] user001 transferred 235.54 to user004
[2025-05-10 23:45:32] user003 transferred 150.00 to user002
[2025-05-10 23:55:32] user002 withdrew 50
```

Результат обработки 

(`transactions_by_users/user001.log`):
```
[2025-05-10 09:00:22] user001 balance inquiry 1000.00
[2025-05-10 09:05:44] user001 transferred 100.00 to user002
[2025-05-10 09:06:00] user001 transferred 120.00 to user002
[2025-05-10 10:03:23] user001 received 990.00 from user002
[2025-05-10 11:00:03] user001 balance inquiry 1770
[2025-05-10 11:01:12] user001 transferred 102.00 to user003
[2025-05-10 11:09:01] user001 transferred 235.54 to user004
[2025-05-10 17:04:09] user001 transferred 235.54 to user004
[2025-05-18 08:00:40] user001 final balance 1196.92
```

(`transactions_by_users/user002.log`):
```
[2025-05-10 09:05:44] user002 received 100.00 from user001
[2025-05-10 09:06:00] user002 received 120.00 from user001
[2025-05-10 10:03:23] user002 transferred 990.00 to user001
[2025-05-10 10:15:56] user002 balance inquiry 110.00
[2025-05-10 10:25:43] user002 received 120.00 from user003
[2025-05-10 12:38:31] user002 received 150.00 from user003
[2025-05-10 23:45:32] user002 received 150.00 from user003
[2025-05-10 23:55:32] user002 withdrew 50
[2025-05-11 10:00:31] user002 balance inquiry 210.00
[2025-05-18 08:00:40] user002 final balance 480.00
```
...

# Важно!!!
## Примечание: 
Директория transactions_by_users должна быть пуста, иначе произойдёт ошибка. Если у вас нет этой директории, то она автоматически создастся.
