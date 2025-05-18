package service;

import model.LogEntry;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Locale;

/**
 * Класс для сохранения логов транзакций пользователей в файлы.
 * Формирует отдельные файлы для каждого пользователя в директории transactions_by_users.
 * Файлы содержат отсортированные по времени транзакции и итоговый баланс.
 */

public class LogFileSaver {

    // Форматтер для временных меток в финальной строке баланса
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * Сохраняет логи транзакций пользователей в файловую систему.
     *
     * @param userLogs Карта логов по пользователям
     * @param rootDir Корневая директория для сохранения
     * @throws IOException При ошибках записи файлов
     */
    public void saveUserLogs(Map<String, List<LogEntry>> userLogs, Path rootDir) throws IOException {
        // Создаем целевую директорию если не существует
        Path outputDir = rootDir.resolve("transactions_by_users");
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        // Обрабатываем логи для каждого пользователя
        for (Map.Entry<String, List<LogEntry>> entry : userLogs.entrySet()) {
            String user = entry.getKey();
            List<LogEntry> logs = entry.getValue();

            // Сортируем транзакции по времени
            logs.sort(Comparator.comparing(LogEntry::getTimestamp));

            // Создаем файл для пользователя
            Path userFile = outputDir.resolve(user + ".log");
            try (BufferedWriter writer = Files.newBufferedWriter(userFile)) {
                writeLogEntries(writer, logs);              // Записываем все транзакции
                writeFinalBalance(writer, user, logs);      // Добавляем итоговый баланс
            }
        }
    }

    /**
     * Записывает список транзакций в файл.
     *
     * @param writer Поток для записи
     * @param logs Список транзакций
     */
    private void writeLogEntries(BufferedWriter writer, List<LogEntry> logs) throws IOException {
        for (LogEntry log : logs) {
            writer.write(log.getRawLine());
            writer.newLine();
        }
    }

    /**
     * Добавляет строку с итоговым балансом в конец файла.
     *
     * @param writer Поток для записи
     * @param user Имя пользователя
     * @param logs Список транзакций для расчета баланса
     */
    private void writeFinalBalance(BufferedWriter writer, String user, List<LogEntry> logs) throws IOException {
        double balance = calculateBalance(logs);
        String finalLine = String.format(Locale.ENGLISH, "[%s] %s final balance %.2f",
                LocalDateTime.now().format(formatter), user, balance);
        writer.write(finalLine);
    }

    /**
     * Вычисляет итоговый баланс пользователя на основе транзакций.
     *
     * @param logs Список транзакций пользователя
     * @return Рассчитанный баланс
     */
    public double calculateBalance(List<LogEntry> logs) {
        double balance = 0.0;
        boolean initialBalanceSet = false;
        for (LogEntry log : logs) {
            switch (log.getOperation()) {
                case BALANCE_INQUIRY:
                    // Учитываем только первый балансовый запрос как начальный баланс
                    if (!initialBalanceSet) {
                        balance = log.getAmount();
                        initialBalanceSet = true;
                    }
                    break;
                case TRANSFERRED:
                case WITHDREW:
                    // Списания уменьшают баланс
                    balance -= log.getAmount();
                    break;
                case RECEIVED:
                    // Поступления увеличивают баланс
                    balance += log.getAmount();
                    break;
            }
        }
        return balance;
    }
}

