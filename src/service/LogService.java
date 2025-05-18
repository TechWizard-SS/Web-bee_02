package service;

import model.LogEntry;
import parser.LogParser;
import util.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Основной сервис для загрузки логов, обработки и сохранения пользовательских логов.
public class LogService {

    // Вспомогательный класс для сохранения логов пользователей
    private final LogFileSaver logFileSaver = new LogFileSaver();

    /**
     * Загружает и парсит лог-файлы из указанной директории.
     * Возвращает мапу с логами, сгруппированными по пользователю.
     */
    public Map<String, List<LogEntry>> loadLogs(Path rootDir) throws IOException {
        Map<String, List<LogEntry>> userLogs = new HashMap<>();

        // Ищем все .log файлы в указанной директории
        for (Path file : FileUtils.findLogFiles(rootDir)) {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;        // Пропуск пустых строк
                LogEntry entry = LogParser.parse(line);     // Парсинг строки в объект LogEntry
                addEntry(userLogs, entry);

                // Обработка перевода — создаем зеркальную запись для получателя
                if (entry.getOperation() == LogEntry.Operation.TRANSFERRED && entry.getTargetUser() != null) {
                    createReceivedEntry(userLogs, entry);
                }
            }
        }
        return userLogs;
    }

    // Добавляет лог-запись в список записей конкретного пользователя
    private void addEntry(Map<String, List<LogEntry>> userLogs, LogEntry entry) {
        userLogs.computeIfAbsent(entry.getUser(), k -> new ArrayList<>()).add(entry);
    }

    /**
     * Создает "зеркальную" запись о переводе для получателя
     * (например, если user001 отправил деньги user002, то создается запись:
     * user002 received X from user001)
     */
    private void createReceivedEntry(Map<String, List<LogEntry>> userLogs, LogEntry entry) {
        String receivedLine = String.format(Locale.ENGLISH, "[%s] %s received %.2f from %s",
                entry.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                entry.getTargetUser(),
                entry.getAmount(),
                entry.getUser()
        );

        try {
            LogEntry receivedEntry = LogParser.parse(receivedLine);
            userLogs.computeIfAbsent(receivedEntry.getUser(), k -> new ArrayList<>()).add(receivedEntry);
        } catch (Exception e) {
            System.err.println("Error creating received entry: " + e.getMessage());
        }
    }

    /**
     * Сохраняет лог-файлы пользователей в указанную директорию
     */
    public void saveUserLogs(Map<String, List<LogEntry>> userLogs, Path rootDir) throws IOException {
        logFileSaver.saveUserLogs(userLogs, rootDir);
    }
}