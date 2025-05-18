package main;

import model.LogEntry;
import service.LogService;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Главный класс приложения для обработки финансовых логов.
 * Читает логи транзакций из указанной директории, группирует по пользователям
 * и сохраняет результат в отдельные файлы.
 */
public class Main {
    /**
     * Точка входа в приложение.
     *
     * @param args Путь к директории с лог-файлами в формате:
     *             args[0] = "путь/к/директории/с/logs"
     * @throws IOException При ошибках чтения/записи файлов
     * @throws IllegalArgumentException Если аргументы некорректны
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Main <path_to_logs_directory>");
            return; // Завершаем работу при неправильных аргументах
        }

        Path logsDir = Paths.get(args[0]);
        LogService logService = new LogService();

        // 1. Загрузка и парсинг логов
        Map<String, List<LogEntry>> userLogs = logService.loadLogs(logsDir);

        // 2. Сохранение результатов
        logService.saveUserLogs(userLogs, logsDir);

        System.out.println("Processing completed successfully!");
    }
}