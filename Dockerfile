# Используем официальный образ OpenJDK 23
FROM openjdk:23

LABEL authors="Samir"

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем все файлы проекта внутрь контейнера
COPY . /app

# Делаем скрипт исполняемым
RUN chmod +x run.sh

# Устанавливаем команду запуска через bash с аргументом
CMD ["bash", "run.sh", "/app/logs"]
