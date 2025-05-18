#!/bin/bash

echo "$(date +%F\ %T) Начало компиляции..."

# Компиляция всех файлов .java
javac -d out src/**/*.java

echo "$(date +%F\ %T) Компиляция завершена"

# Запуск основного класса с передачей аргумента
echo "$(date +%F\ %T) Запуск программы..."
java -cp out main/Main "$1"
